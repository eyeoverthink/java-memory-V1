#!/usr/bin/env python3
"""
Heuristic deepfake/image-authenticity scanner for local files and web URLs.
Outputs one JSON payload to stdout.

This is not a forensic certainty engine. It returns a risk-oriented heuristic score.
"""

from __future__ import annotations

import argparse
import json
import re
import urllib.error
import urllib.parse
import urllib.request
from dataclasses import dataclass
from html.parser import HTMLParser
from typing import List, Optional, Tuple

PHI = (1.0 + 5.0 ** 0.5) / 2.0
QHRC_PRIMARY_FREQ = 4790.45
QHRC_SECONDARY_FREQ = 7750.95
QHRC_RATIO_TARGET = 1.3777


def clamp01(v: float) -> float:
    if v < 0.0:
        return 0.0
    if v > 1.0:
        return 1.0
    return float(v)


def fail(message: str, source: str = "", source_type: str = "unknown") -> int:
    print(
        json.dumps(
            {
                "ok": False,
                "message": message,
                "source_type": source_type,
                "source": source,
            }
        )
    )
    return 1


def ok(payload: dict) -> int:
    payload["ok"] = True
    print(json.dumps(payload))
    return 0


class ImgSrcParser(HTMLParser):
    def __init__(self) -> None:
        super().__init__()
        self.sources: List[str] = []

    def handle_starttag(self, tag: str, attrs) -> None:
        if tag.lower() != "img":
            return
        src = None
        for k, v in attrs:
            if k and v and k.lower() in ("src", "data-src", "data-original"):
                src = v.strip()
                break
        if src:
            self.sources.append(src)


@dataclass
class FaceMetrics:
    depth_consistency: float
    reflection_consistency: float
    noise_naturalness: float
    boundary_consistency: float
    symmetry_score: float
    qhrc_primary_alignment: float
    qhrc_secondary_alignment: float
    qhrc_dual_coherence: float
    qhrc_ratio_alignment: float
    risk: float


def fetch_url(url: str, timeout_sec: float) -> Tuple[bytes, str, str]:
    req = urllib.request.Request(
        url,
        headers={
            "User-Agent": "FRAYMUS-Deepfake-Scanner/1.0",
            "Accept": "image/*,text/html;q=0.9,*/*;q=0.8",
        },
    )
    with urllib.request.urlopen(req, timeout=timeout_sec) as resp:
        data = resp.read(8 * 1024 * 1024)
        content_type = (resp.headers.get("Content-Type") or "").lower()
        final_url = resp.geturl() or url
        return data, final_url, content_type


def inspect_provenance(raw: Optional[bytes], source: str) -> dict:
    if raw is None or len(raw) < 16:
        return {
            "status": "not_checked",
            "confidence": 0.0,
            "signals": ["raw_bytes_unavailable"],
        }
    b = raw
    low = b.lower()
    signals: List[str] = []
    score = 0.0

    # Heuristic C2PA/JUMBF indicators (not full cryptographic verification).
    if b"\xff\xeb" in b:
        signals.append("jpeg_app11_marker_present")
        score += 0.10
    if b"c2pa" in low:
        signals.append("c2pa_label_present")
        score += 0.35
    if b"jumbf" in low or b"jumb" in low:
        signals.append("jumbf_marker_present")
        score += 0.25
    if b"c2ma" in low:
        signals.append("c2pa_manifest_store_hint")
        score += 0.20
    if b"contentcredentials" in low or b"content credentials" in low:
        signals.append("content_credentials_text_present")
        score += 0.15

    if score >= 0.45:
        status = "credentials_detected"
    elif score > 0.0:
        status = "credentials_hint"
    else:
        status = "not_detected"
        signals.append("no_credentials_markers_found")

    return {
        "status": status,
        "confidence": clamp01(score),
        "signals": signals,
    }


def is_image_url(url: str, content_type: str) -> bool:
    if content_type.startswith("image/"):
        return True
    path = urllib.parse.urlparse(url).path.lower()
    return path.endswith(
        (".jpg", ".jpeg", ".png", ".bmp", ".webp", ".tif", ".tiff", ".gif")
    )


def extract_img_urls(html_text: str, base_url: str, max_images: int) -> List[str]:
    parser = ImgSrcParser()
    parser.feed(html_text)
    unique: List[str] = []
    seen = set()
    for src in parser.sources:
        absolute = urllib.parse.urljoin(base_url, src)
        if absolute in seen:
            continue
        if absolute.startswith("http://") or absolute.startswith("https://"):
            seen.add(absolute)
            unique.append(absolute)
            if len(unique) >= max_images:
                break
    ranked = sorted(unique, key=image_url_rank, reverse=True)
    return ranked


def image_url_rank(url: str) -> int:
    u = url.lower()
    score = 0
    if re.search(r"\.(jpe?g|png|webp|bmp|tiff?)($|[?#])", u):
        score += 3
    if "upload" in u or "/thumb/" in u or "photo" in u:
        score += 2
    if re.search(r"(logo|icon|sprite|wordmark|favicon|banner)", u):
        score -= 3
    if u.endswith(".svg") or ".svg?" in u:
        score -= 4
    return score


def decode_image(cv2, np, raw: bytes):
    arr = np.frombuffer(raw, dtype=np.uint8)
    if arr.size == 0:
        return None
    return cv2.imdecode(arr, cv2.IMREAD_COLOR)


def spectral_profile(np, roi_gray, bins: int = 128):
    img = roi_gray.astype("float32")
    if img.size == 0:
        return np.zeros((bins,), dtype="float64")
    f = np.fft.fftshift(np.fft.fft2(img))
    mag = np.abs(f).astype("float64")
    h, w = mag.shape
    cy, cx = h // 2, w // 2
    yy, xx = np.indices((h, w))
    rr = np.sqrt((xx - cx) ** 2 + (yy - cy) ** 2)
    rmax = float(rr.max())
    if rmax <= 0.0:
        return np.zeros((bins,), dtype="float64")
    rr_norm = np.clip(rr / rmax, 0.0, 1.0)
    idx = np.minimum((rr_norm * (bins - 1)).astype("int32"), bins - 1)
    sums = np.bincount(idx.ravel(), weights=mag.ravel(), minlength=bins).astype("float64")
    cnts = np.bincount(idx.ravel(), minlength=bins).astype("float64")
    prof = sums / np.maximum(cnts, 1e-9)
    prof /= float(np.max(prof) + 1e-9)
    return prof


def band_mean(np, profile, center: float, width: float) -> float:
    n = int(profile.shape[0])
    xs = np.linspace(0.0, 1.0, n)
    mask = np.abs(xs - center) <= width
    if not np.any(mask):
        return 0.0
    return float(np.mean(profile[mask]))


def compute_qhrc_resonance(np, roi_gray):
    profile = spectral_profile(np, roi_gray)
    if profile.size == 0:
        return {
            "primary_alignment": 0.0,
            "secondary_alignment": 0.0,
            "dual_coherence": 0.0,
            "ratio_alignment": 0.0,
        }

    p_center = QHRC_PRIMARY_FREQ / (QHRC_PRIMARY_FREQ + QHRC_SECONDARY_FREQ)
    s_center = QHRC_SECONDARY_FREQ / (QHRC_PRIMARY_FREQ + QHRC_SECONDARY_FREQ)
    width = 0.05

    mean_energy = float(np.mean(profile)) + 1e-9
    primary_raw = band_mean(np, profile, p_center, width)
    secondary_raw = band_mean(np, profile, s_center, width)
    # Normalize against global radial mean so values are interpretable.
    primary = clamp01(primary_raw / (mean_energy * 2.1))
    secondary = clamp01(secondary_raw / (mean_energy * 2.1))
    dual = clamp01((primary * secondary) ** 0.5)
    ratio = secondary / max(primary, 1e-6)
    ratio_align = clamp01(1.0 - min(abs(ratio - QHRC_RATIO_TARGET) / QHRC_RATIO_TARGET, 1.0))

    # φ-harmonic stabilization: reward balance near reciprocal bands.
    phi_balance = clamp01(1.0 - abs((p_center + s_center) - 1.0) * PHI)
    dual_coherence = clamp01(0.65 * dual + 0.20 * ratio_align + 0.15 * phi_balance)

    return {
        "primary_alignment": primary,
        "secondary_alignment": secondary,
        "dual_coherence": dual_coherence,
        "ratio_alignment": ratio_align,
    }


def measure_face(cv2, np, gray, color, face_box) -> FaceMetrics:
    x, y, w, h = face_box
    roi_gray = gray[y : y + h, x : x + w]
    roi_color = color[y : y + h, x : x + w]
    if roi_gray.size == 0:
        return FaceMetrics(0.0, 0.0, 0.0, 0.0, 0.0, 1.0)

    # 1) Depth consistency proxy: edge and texture complexity.
    lap_var = float(cv2.Laplacian(roi_gray, cv2.CV_64F).var())
    edges = cv2.Canny(roi_gray, 80, 160)
    edge_density = float((edges > 0).mean())
    depth_consistency = clamp01(0.55 * clamp01(lap_var / 700.0) + 0.45 * clamp01(edge_density / 0.12))

    # 2) Reflection consistency proxy: heavy clipped highlights.
    b = roi_color[:, :, 0]
    g = roi_color[:, :, 1]
    r = roi_color[:, :, 2]
    bright_all = ((r > 245) & (g > 245) & (b > 245)).mean()
    saturated_any = ((r > 250) | (g > 250) | (b > 250)).mean()
    reflection_risk = clamp01((bright_all * 18.0) + max(0.0, saturated_any - 0.03) * 7.0)
    reflection_consistency = clamp01(1.0 - reflection_risk)

    # 3) Noise naturalness proxy: residual microtexture.
    blur = cv2.GaussianBlur(roi_gray, (3, 3), 0)
    residual = roi_gray.astype("float32") - blur.astype("float32")
    residual_std = float(np.std(residual))
    # Bell around practical camera residual range.
    noise_naturalness = clamp01(1.0 - min(abs(residual_std - 9.0) / 9.0, 1.0))

    # 4) Boundary consistency proxy: detect blending seam around perimeter.
    min_side = min(w, h)
    border = max(2, min_side // 12)
    inner = edges[border : h - border, border : w - border] if (h - 2 * border > 2 and w - 2 * border > 2) else edges
    border_mask = np.ones_like(edges, dtype=np.uint8)
    border_mask[border : h - border, border : w - border] = 0
    border_edges = float(edges[border_mask == 1].mean() / 255.0) if (border_mask == 1).any() else 0.0
    inner_edges = float(inner.mean() / 255.0) if inner.size > 0 else 0.0
    seam = max(0.0, border_edges - inner_edges)
    boundary_consistency = clamp01(1.0 - clamp01(seam * 4.0))

    # 5) Symmetry score (over-perfect symmetry can be suspicious).
    half = w // 2
    if half > 8:
        left = roi_gray[:, :half].astype("float32")
        right = roi_gray[:, w - half :].astype("float32")
        right_flip = cv2.flip(right, 1)
        min_w = min(left.shape[1], right_flip.shape[1])
        diff = float(np.mean(np.abs(left[:, :min_w] - right_flip[:, :min_w])))
        symmetry_score = clamp01(1.0 - (diff / 84.0))
    else:
        symmetry_score = 0.5
    over_perfect_penalty = clamp01(max(0.0, symmetry_score - 0.92) * 4.0)

    # 6) QHRC-inspired resonance lane (spectral coherence heuristic).
    qhrc = compute_qhrc_resonance(np, roi_gray)
    resonance_coherence = qhrc["dual_coherence"]

    risk = (
        0.22 * (1.0 - depth_consistency)
        + 0.20 * (1.0 - reflection_consistency)
        + 0.20 * (1.0 - noise_naturalness)
        + 0.20 * (1.0 - boundary_consistency)
        + 0.08 * over_perfect_penalty
        + 0.10 * (1.0 - resonance_coherence)
    )
    risk = clamp01(risk)

    return FaceMetrics(
        depth_consistency=depth_consistency,
        reflection_consistency=reflection_consistency,
        noise_naturalness=noise_naturalness,
        boundary_consistency=boundary_consistency,
        symmetry_score=symmetry_score,
        qhrc_primary_alignment=qhrc["primary_alignment"],
        qhrc_secondary_alignment=qhrc["secondary_alignment"],
        qhrc_dual_coherence=qhrc["dual_coherence"],
        qhrc_ratio_alignment=qhrc["ratio_alignment"],
        risk=risk,
    )


def classify_risk(risk: float) -> Tuple[str, float]:
    if risk >= 0.67:
        confidence = clamp01(0.60 + (risk - 0.67) * 1.4)
        return "likely_synthetic", confidence
    if risk <= 0.40:
        confidence = clamp01(0.60 + (0.40 - risk) * 1.4)
        return "likely_authentic", confidence
    confidence = clamp01(0.42 + abs(risk - 0.535) * 0.55)
    return "inconclusive", confidence


def scan_image_array(cv2, np, image_bgr, source: str, raw_bytes: Optional[bytes] = None) -> dict:
    provenance = inspect_provenance(raw_bytes, source)
    if image_bgr is None:
        return {
            "source": source,
            "decision": "inconclusive",
            "risk": 0.50,
            "confidence": 0.10,
            "faces": 0,
            "depth_consistency": 0.0,
            "reflection_consistency": 0.0,
            "noise_naturalness": 0.0,
            "boundary_consistency": 0.0,
            "symmetry_score": 0.0,
            "qhrc_primary_alignment": 0.0,
            "qhrc_secondary_alignment": 0.0,
            "qhrc_dual_coherence": 0.0,
            "qhrc_ratio_alignment": 0.0,
            "provenance_status": provenance["status"],
            "provenance_confidence": provenance["confidence"],
            "provenance_signals": provenance["signals"],
            "notes": ["image decode failed"],
        }

    gray = cv2.cvtColor(image_bgr, cv2.COLOR_BGR2GRAY)
    face_cascade = cv2.CascadeClassifier(
        cv2.data.haarcascades + "haarcascade_frontalface_default.xml"
    )
    faces = face_cascade.detectMultiScale(
        gray,
        scaleFactor=1.1,
        minNeighbors=5,
        minSize=(80, 80),
    )

    if len(faces) == 0:
        return {
            "source": source,
            "decision": "inconclusive",
            "risk": 0.50,
            "confidence": 0.35,
            "faces": 0,
            "depth_consistency": 0.0,
            "reflection_consistency": 0.0,
            "noise_naturalness": 0.0,
            "boundary_consistency": 0.0,
            "symmetry_score": 0.0,
            "qhrc_primary_alignment": 0.0,
            "qhrc_secondary_alignment": 0.0,
            "qhrc_dual_coherence": 0.0,
            "qhrc_ratio_alignment": 0.0,
            "provenance_status": provenance["status"],
            "provenance_confidence": provenance["confidence"],
            "provenance_signals": provenance["signals"],
            "notes": ["no face detected"],
        }

    per_face = [measure_face(cv2, np, gray, image_bgr, f) for f in faces]
    n = float(len(per_face))
    depth = sum(m.depth_consistency for m in per_face) / n
    refl = sum(m.reflection_consistency for m in per_face) / n
    noise = sum(m.noise_naturalness for m in per_face) / n
    boundary = sum(m.boundary_consistency for m in per_face) / n
    sym = sum(m.symmetry_score for m in per_face) / n
    qhrc_primary = sum(m.qhrc_primary_alignment for m in per_face) / n
    qhrc_secondary = sum(m.qhrc_secondary_alignment for m in per_face) / n
    qhrc_dual = sum(m.qhrc_dual_coherence for m in per_face) / n
    qhrc_ratio = sum(m.qhrc_ratio_alignment for m in per_face) / n
    risk = sum(m.risk for m in per_face) / n

    # If robust provenance credentials are detected, reduce risk modestly.
    if provenance["status"] == "credentials_detected":
        risk = clamp01(risk * 0.92)

    decision, confidence = classify_risk(risk)

    notes = []
    if refl < 0.40:
        notes.append("reflection clipping anomaly")
    if noise < 0.35:
        notes.append("sensor noise pattern atypical")
    if boundary < 0.35:
        notes.append("boundary blending anomaly")
    if depth < 0.35:
        notes.append("depth/texture inconsistency")
    if qhrc_dual < 0.20:
        notes.append("qhrc resonance incoherence")
    if provenance["status"] == "credentials_detected":
        notes.append("provenance credentials detected")
    if not notes:
        notes.append("no dominant anomaly detected")

    return {
        "source": source,
        "decision": decision,
        "risk": float(risk),
        "confidence": float(confidence),
        "faces": int(len(faces)),
        "depth_consistency": float(depth),
        "reflection_consistency": float(refl),
        "noise_naturalness": float(noise),
        "boundary_consistency": float(boundary),
        "symmetry_score": float(sym),
        "qhrc_primary_alignment": float(qhrc_primary),
        "qhrc_secondary_alignment": float(qhrc_secondary),
        "qhrc_dual_coherence": float(qhrc_dual),
        "qhrc_ratio_alignment": float(qhrc_ratio),
        "provenance_status": provenance["status"],
        "provenance_confidence": float(provenance["confidence"]),
        "provenance_signals": provenance["signals"],
        "notes": notes,
    }


def aggregate_results(results: List[dict]) -> Tuple[str, float, float]:
    if not results:
        return "inconclusive", 0.5, 0.2
    risk = sum(float(r.get("risk", 0.5)) for r in results) / float(len(results))
    decision, confidence = classify_risk(risk)
    return decision, float(risk), float(confidence)


def scan_file(cv2, np, path: str) -> int:
    try:
        raw = open(path, "rb").read()
    except Exception as e:
        return fail(f"file read failed: {e}", source=path, source_type="file")
    image = decode_image(cv2, np, raw)
    result = scan_image_array(cv2, np, image, path, raw)
    decision, risk, confidence = aggregate_results([result])
    return ok(
        {
            "message": "scan complete",
            "source_type": "file",
            "source": path,
            "overall_decision": decision,
            "overall_risk": risk,
            "confidence": confidence,
            "images": [result],
        }
    )


def scan_url(cv2, np, url: str, max_images: int, timeout_sec: float) -> int:
    try:
        data, final_url, content_type = fetch_url(url, timeout_sec)
    except urllib.error.URLError as e:
        return fail(f"url fetch failed: {e}", source=url, source_type="url")
    except Exception as e:
        return fail(f"url fetch failed: {e}", source=url, source_type="url")

    if is_image_url(final_url, content_type):
        image = decode_image(cv2, np, data)
        r = scan_image_array(cv2, np, image, final_url, data)
        decision, risk, confidence = aggregate_results([r])
        return ok(
            {
                "message": "scan complete",
                "source_type": "url-image",
                "source": final_url,
                "overall_decision": decision,
                "overall_risk": risk,
                "confidence": confidence,
                "images": [r],
            }
        )

    html = data.decode("utf-8", errors="ignore")
    candidate_limit = max(max_images * 8, 24)
    image_urls = extract_img_urls(html, final_url, candidate_limit)
    if not image_urls:
        return fail(
            "no image URLs discovered on page",
            source=final_url,
            source_type="url-page",
        )

    scanned: List[dict] = []
    skipped = 0
    attempts = 0
    for img_url in image_urls:
        if len(scanned) >= max_images:
            break
        attempts += 1
        lower = img_url.lower()
        if lower.startswith("data:") or lower.endswith(".svg") or ".svg?" in lower:
            skipped += 1
            continue
        try:
            raw, resolved, ctype = fetch_url(img_url, timeout_sec)
            if ctype.startswith("image/svg"):
                skipped += 1
                continue
            if not is_image_url(resolved, ctype):
                skipped += 1
                continue
            image = decode_image(cv2, np, raw)
            if image is None:
                skipped += 1
                continue
            h, w = image.shape[:2]
            if w < 96 or h < 96:
                skipped += 1
                continue
            scanned.append(scan_image_array(cv2, np, image, resolved, raw))
        except Exception:
            skipped += 1
            continue

    if not scanned:
        return fail(
            "no downloadable image content discovered on page",
            source=final_url,
            source_type="url-page",
        )

    decision, risk, confidence = aggregate_results(scanned)
    message = f"scan complete (images={len(scanned)}, skipped={skipped}, attempts={attempts})"
    return ok(
        {
            "message": message,
            "source_type": "url-page",
            "source": final_url,
            "overall_decision": decision,
            "overall_risk": risk,
            "confidence": confidence,
            "images": scanned,
        }
    )


def main() -> int:
    parser = argparse.ArgumentParser(description="FRAYMUS heuristic deepfake scanner")
    parser.add_argument("--file", type=str, default="")
    parser.add_argument("--url", type=str, default="")
    parser.add_argument("--max-images", type=int, default=6)
    parser.add_argument("--timeout-sec", type=float, default=8.0)
    args = parser.parse_args()

    if not args.file and not args.url:
        return fail("provide --file or --url")

    try:
        import cv2  # type: ignore
        import numpy as np  # type: ignore
    except Exception as e:
        return fail(f"OpenCV/Numpy unavailable: {e}")

    if args.file:
        return scan_file(cv2, np, args.file)
    return scan_url(cv2, np, args.url, max(1, args.max_images), max(2.0, float(args.timeout_sec)))


if __name__ == "__main__":
    raise SystemExit(main())
