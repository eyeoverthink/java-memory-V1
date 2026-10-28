#!/usr/bin/env python3
"""
OpenCV camera capture bridge for Fraymus avatar sovereignty lane.
Outputs one JSON object to stdout.
"""

from __future__ import annotations

import argparse
import json
import math
import os
import time


def fail(message: str) -> None:
    print(json.dumps({"ok": False, "message": message}))


def clamp01(v: float) -> float:
    if v < 0.0:
        return 0.0
    if v > 1.0:
        return 1.0
    return float(v)


def backend_sequence(cv2, backend_name: str):
    name = (backend_name or "auto").strip().lower()
    if name == "dshow":
        return [("dshow", cv2.CAP_DSHOW)]
    if name == "msmf":
        return [("msmf", cv2.CAP_MSMF)]
    if name == "any":
        return [("any", cv2.CAP_ANY)]
    if os.name == "nt":
        return [
            ("dshow", cv2.CAP_DSHOW),
            ("msmf", cv2.CAP_MSMF),
            ("any", cv2.CAP_ANY),
        ]
    return [("any", cv2.CAP_ANY)]


def open_capture(cv2, camera_index: int, backend_name: str):
    errors = []
    for label, backend in backend_sequence(cv2, backend_name):
        try:
            cap = cv2.VideoCapture(camera_index, backend)
        except Exception as e:
            errors.append(f"{label} open error: {e}")
            continue
        if cap is None:
            errors.append(f"{label} open returned null capture")
            continue
        if cap.isOpened():
            return cap, label, ""
        cap.release()
        errors.append(f"{label} could not open camera")
    return None, "none", "; ".join(errors) if errors else "no backend succeeded"


def main() -> int:
    parser = argparse.ArgumentParser(description="Capture OpenCV face observation")
    parser.add_argument("--camera", type=int, default=0)
    parser.add_argument("--frames", type=int, default=48)
    parser.add_argument("--warmup", type=int, default=8)
    parser.add_argument("--width", type=int, default=640)
    parser.add_argument("--height", type=int, default=480)
    parser.add_argument("--timeout-sec", type=float, default=8.0)
    parser.add_argument("--backend", type=str, default="auto")
    args = parser.parse_args()

    try:
        import cv2  # type: ignore
        import numpy as np  # type: ignore
    except Exception as e:
        fail(f"OpenCV/Numpy unavailable: {e}")
        return 1

    cap, backend_used, open_error = open_capture(cv2, args.camera, args.backend)
    if cap is None:
        fail(f"Camera {args.camera} unavailable ({open_error})")
        return 1

    cap.set(cv2.CAP_PROP_FRAME_WIDTH, args.width)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, args.height)

    face_cascade = cv2.CascadeClassifier(
        cv2.data.haarcascades + "haarcascade_frontalface_default.xml"
    )
    eye_cascade = cv2.CascadeClassifier(
        cv2.data.haarcascades + "haarcascade_eye.xml"
    )

    for _ in range(max(0, args.warmup)):
        cap.read()

    histogram_acc = None
    depth_acc = None
    thermal_acc = None
    landmark_acc = None

    sample_count = 0
    frame_count = 0
    face_detected = 0

    interocular_samples = []
    symmetry_samples = []
    face_area_samples = []
    motion_samples = []
    texture_samples = []
    thermal_delta_samples = []

    prev_gray = None
    currently_closed = False
    blink_events = 0

    start = time.time()
    deadline = start + max(2.0, float(args.timeout_sec))

    for _ in range(max(16, args.frames)):
        if time.time() >= deadline:
            break
        ok, frame = cap.read()
        if not ok or frame is None:
            continue
        frame_count += 1

        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        h_img, w_img = gray.shape[:2]

        faces = face_cascade.detectMultiScale(
            gray,
            scaleFactor=1.1,
            minNeighbors=4,
            minSize=(80, 80),
        )
        if len(faces) == 0:
            prev_gray = gray
            continue

        face_detected += 1
        x, y, w, h = max(faces, key=lambda f: f[2] * f[3])
        roi_gray = gray[y : y + h, x : x + w]
        roi_color = frame[y : y + h, x : x + w]

        if roi_gray.size == 0:
            prev_gray = gray
            continue

        # Eyes for interocular and blink proxy.
        eyes = eye_cascade.detectMultiScale(
            roi_gray,
            scaleFactor=1.1,
            minNeighbors=6,
            minSize=(12, 12),
        )
        eyes_sorted = sorted(eyes, key=lambda e: e[2] * e[3], reverse=True)
        eyes_visible = len(eyes_sorted) >= 2

        if not eyes_visible:
            currently_closed = True
        else:
            if currently_closed:
                blink_events += 1
                currently_closed = False
            e1 = eyes_sorted[0]
            e2 = eyes_sorted[1]
            c1 = (x + e1[0] + e1[2] * 0.5, y + e1[1] + e1[3] * 0.5)
            c2 = (x + e2[0] + e2[2] * 0.5, y + e2[1] + e2[3] * 0.5)
            interocular = math.dist(c1, c2) / max(1.0, float(w_img))
            interocular_samples.append(interocular)
        # Symmetry proxy.
        half = w // 2
        if half > 8:
            left = roi_gray[:, :half].astype("float32")
            right = roi_gray[:, w - half :].astype("float32")
            right_flip = cv2.flip(right, 1)
            min_w = min(left.shape[1], right_flip.shape[1])
            left = left[:, :min_w]
            right_flip = right_flip[:, :min_w]
            diff = float(np.mean(np.abs(left - right_flip)))
            symmetry_samples.append(clamp01(1.0 - (diff / 96.0)))

        # Histogram (16 bins, normalized).
        hist = cv2.calcHist([roi_gray], [0], None, [16], [0, 256]).flatten().astype("float64")
        hsum = float(hist.sum()) if hist.sum() > 0 else 1.0
        hist /= hsum

        # Depth profile proxies.
        lap_var = float(cv2.Laplacian(roi_gray, cv2.CV_64F).var())
        canny = cv2.Canny(roi_gray, 80, 160)
        edge_density = float((canny > 0).mean())
        brightness_var = float(roi_gray.var())
        face_ratio = float((w * h) / max(1.0, float(w_img * h_img)))

        depth_profile = [
            clamp01(lap_var / 1200.0),
            clamp01(edge_density),
            clamp01(brightness_var / 6000.0),
            clamp01(face_ratio),
        ]

        # Thermal proxies (RGB balance only; no IR sensor).
        b = roi_color[:, :, 0].astype("float32")
        g = roi_color[:, :, 1].astype("float32")
        r = roi_color[:, :, 2].astype("float32")
        rb_delta = float(np.mean(np.abs(r - b)) / 255.0)
        rg_delta = float(np.mean(np.abs(r - g)) / 255.0)
        gb_delta = float(np.mean(np.abs(g - b)) / 255.0)
        channel_std = float(np.std(roi_color.astype("float32")) / 255.0)
        thermal_profile = [clamp01(rb_delta), clamp01(rg_delta), clamp01(gb_delta), clamp01(channel_std)]
        thermal_delta_samples.append(clamp01(rb_delta))

        # 16-value landmark proxy vector from bbox/eyes/proxies.
        face_cx = x + w * 0.5
        face_cy = y + h * 0.5
        if eyes_visible:
            e1 = eyes_sorted[0]
            e2 = eyes_sorted[1]
            e1x = x + e1[0] + e1[2] * 0.5
            e1y = y + e1[1] + e1[3] * 0.5
            e2x = x + e2[0] + e2[2] * 0.5
            e2y = y + e2[1] + e2[3] * 0.5
        else:
            e1x = face_cx - w * 0.15
            e1y = face_cy - h * 0.1
            e2x = face_cx + w * 0.15
            e2y = face_cy - h * 0.1

        eye_dx = e2x - e1x
        eye_dy = e2y - e1y
        eye_dist = math.dist((e1x, e1y), (e2x, e2y))

        landmark = [
            x / max(1.0, float(w_img)),
            y / max(1.0, float(h_img)),
            w / max(1.0, float(w_img)),
            h / max(1.0, float(h_img)),
            face_cx / max(1.0, float(w_img)),
            face_cy / max(1.0, float(h_img)),
            e1x / max(1.0, float(w_img)),
            e1y / max(1.0, float(h_img)),
            e2x / max(1.0, float(w_img)),
            e2y / max(1.0, float(h_img)),
            eye_dx / max(1.0, float(w_img)),
            eye_dy / max(1.0, float(h_img)),
            eye_dist / max(1.0, float(max(w_img, h_img))),
            clamp01(1.0 - (abs(face_cx - (w_img * 0.5)) / max(1.0, float(w_img * 0.5)))),
            clamp01(lap_var / 1200.0),
            clamp01(edge_density),
        ]

        if prev_gray is not None:
            prev_roi = prev_gray[y : y + h, x : x + w]
            if prev_roi.shape == roi_gray.shape and prev_roi.size > 0:
                diff = cv2.absdiff(roi_gray, prev_roi)
                motion_samples.append(float(np.mean(diff) / 255.0))
        prev_gray = gray

        face_area_samples.append(face_ratio)
        texture_samples.append(clamp01(lap_var / 1200.0))

        if histogram_acc is None:
            histogram_acc = hist
            depth_acc = np.array(depth_profile, dtype="float64")
            thermal_acc = np.array(thermal_profile, dtype="float64")
            landmark_acc = np.array(landmark, dtype="float64")
        else:
            histogram_acc += hist
            depth_acc += np.array(depth_profile, dtype="float64")
            thermal_acc += np.array(thermal_profile, dtype="float64")
            landmark_acc += np.array(landmark, dtype="float64")
        sample_count += 1

    cap.release()

    duration = max(0.001, time.time() - start)
    if frame_count < 8:
        fail("Insufficient camera frames")
        return 1
    if sample_count == 0 or face_detected == 0:
        fail("No usable face observations captured")
        return 1

    histogram = (histogram_acc / sample_count).tolist()
    depth_profile = (depth_acc / sample_count).tolist()
    thermal_profile = (thermal_acc / sample_count).tolist()
    landmark_vector = (landmark_acc / sample_count).tolist()

    interocular_distance = float(sum(interocular_samples) / len(interocular_samples)) if interocular_samples else 0.0
    symmetry_score = float(sum(symmetry_samples) / len(symmetry_samples)) if symmetry_samples else 0.0

    blink_rate_hz = float(blink_events / duration)
    depth_variance = float(np.std(face_area_samples)) if face_area_samples else 0.0
    parallax_score = float(sum(motion_samples) / len(motion_samples)) if motion_samples else 0.0
    texture_variance = float(np.std(texture_samples)) if texture_samples else 0.0
    thermal_delta = float(sum(thermal_delta_samples) / len(thermal_delta_samples)) if thermal_delta_samples else 0.0

    payload = {
        "ok": True,
        "message": "capture complete",
        "backend": "python-opencv",
        "camera_index": int(args.camera),
        "backend_used": backend_used,
        "frames_captured": int(frame_count),
        "frames_used": int(sample_count),
        "interocular_distance": float(interocular_distance),
        "symmetry_score": float(symmetry_score),
        "grayscale_histogram": [float(v) for v in histogram],
        "depth_profile": [float(v) for v in depth_profile],
        "thermal_profile": [float(v) for v in thermal_profile],
        "landmark_vector": [float(v) for v in landmark_vector],
        "blink_rate_hz": float(blink_rate_hz),
        "depth_variance": float(depth_variance),
        "parallax_score": float(parallax_score),
        "texture_variance": float(texture_variance),
        "thermal_delta": float(thermal_delta),
        "simulated": False,
    }
    print(json.dumps(payload))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
