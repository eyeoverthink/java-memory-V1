const NAV_ITEMS = [
    { page: "headroom", href: "/", label: "Headroom", note: "visual shell" },
    { page: "overview", href: "/overview", label: "Overview", note: "system shape" },
    { page: "companion", href: "/companion", label: "Companion", note: "chat and media" },
    { page: "session", href: "/session", label: "Session", note: "live process" },
    { page: "systems", href: "/systems", label: "Systems", note: "subsystems" },
    { page: "surfaces", href: "/surfaces", label: "Surfaces", note: "commands" },
    { page: "knowledge", href: "/knowledge", label: "Knowledge", note: "docs and graph" },
    { page: "runtime", href: "/runtime", label: "Runtime", note: "native kernel" },
    { page: "launchpad", href: "/launchpad", label: "Launchpad", note: "execute and route" }
];
let companionSelection = { visualPath: null, audioPath: null };
let companionDreamMemory = null;
let companionHeadroomMemory = null;
let companionLastAssistantTurn = null;
const HEADROOM_MODE_KEY = "fraynix.headroomMode";
const SYSTEM_FOCUS_MEMORY_KEY = "fraynix.systemFocusMemory";
const RUNTIME_FOCUS_MEMORY_KEY = "fraynix.runtimeFocusMemory";
const MATH_FOCUS_MEMORY_KEY = "fraynix.mathFocus";
const PROOF_FOCUS_MEMORY_KEY = "fraynix.proofFocus";
const LANE_TOUCH_HALF_LIFE_MS = 12 * 60 * 60 * 1000;
const LANE_TOUCH_WARM_THRESHOLD = 0.18;
const SYSTEM_STATE_REFRESH_MS = 5000;
const SYSTEM_STATE_REFRESH_TIMERS = new Map();
const MATH_FAMILY_RULES = Object.freeze({
    "phi-harmonic-structure": {
        id: "phi-harmonic-structure",
        label: "Phi-harmonic structure",
        titles: ["Phi-harmonic structure"],
        terms: ["phi", "harmonic", "resonance", "quantum", "phi_field"],
        strata: ["governing-core", "cognition-core", "organism-core", "adjacent-historical-core"],
        actions: ["learn PHI_FIELD", "braid", "aether"]
    },
    "zero-time-hdc": {
        id: "zero-time-hdc",
        label: "Zero-time HDC",
        titles: ["Zero-time hyperdimensional recognition"],
        terms: ["zero-time", "hdc", "hyperdimensional", "hypervector", "hamming", "binding", "bundling"],
        strata: ["cognition-core", "absorption-core", "kernel-discipline"],
        actions: ["aether", "braid"]
    },
    "causal-intervention": {
        id: "causal-intervention",
        label: "Causal intervention",
        titles: ["Causal intervention math"],
        terms: ["causal", "do-calculus", "dag", "intervention", "counterfactual", "verifier"],
        strata: ["proof-lane", "kernel-discipline", "cognition-core"],
        actions: ["braid", "aether"]
    },
    "persistence-continuity": {
        id: "persistence-continuity",
        label: "Persistence and continuity",
        titles: ["Persistence and continuity math"],
        terms: ["persistence", "continuity", "memory", "immortal", "session", "akashic"],
        strata: ["organism-core", "absorption-core", "proof-lane"],
        actions: ["nexus awaken", "braid"]
    },
    "attention-transformer": {
        id: "attention-transformer",
        label: "Attention and transformer",
        titles: ["Attention and transformer math"],
        terms: ["attention", "transformer", "token", "embedding", "rotary", "tokenizer", "optimizer"],
        strata: ["model-substrate-lane", "construction-lane", "cognition-core"],
        actions: ["learn PHI_FIELD", "miving gen 24"]
    },
    "state-space-recurrence": {
        id: "state-space-recurrence",
        label: "State-space and recurrence",
        titles: ["State-space and recurrence math"],
        terms: ["state-space", "state", "recurrence", "transition", "temporal", "memory"],
        strata: ["cognition-core", "model-substrate-lane", "organism-core"],
        actions: ["braid", "aether"]
    },
    "mixture-of-experts-routing": {
        id: "mixture-of-experts-routing",
        label: "Mixture-of-experts and routing",
        titles: ["Mixture-of-experts and routing math"],
        terms: ["mixture-of-experts", "experts", "routing", "gating", "sparse", "router"],
        strata: ["cognition-core", "construction-lane", "operator-shell"],
        actions: ["miving gen 24", "nexus awaken"]
    },
    "search-optimization-verifier": {
        id: "search-optimization-verifier",
        label: "Search, optimization, and verifier",
        titles: ["Search, optimization, and verifier math"],
        terms: ["search", "optimization", "verifier", "validation", "ranking", "bandit", "repair"],
        strata: ["proof-lane", "kernel-discipline", "construction-lane"],
        actions: ["miving gen 24", "braid"]
    },
    "symbolic-discrete-substrate": {
        id: "symbolic-discrete-substrate",
        label: "Symbolic and discrete substrate",
        titles: ["Symbolic and discrete substrate math"],
        terms: ["symbolic", "discrete", "letters", "phonetics", "digits", "arithmetic", "times tables", "machine"],
        strata: ["bare-metal-substrate-lane", "model-substrate-lane", "governing-core"],
        actions: ["aether", "learn PHI_FIELD"]
    }
});

let overviewCache;

document.addEventListener("DOMContentLoaded", async () => {
    renderNav();
    await renderSidebarMeta();

    const page = document.body.dataset.page || "overview";
    switch (page) {
        case "overview":
            await loadOverview();
            break;
        case "headroom":
            await loadHeadroom();
            break;
        case "companion":
            await loadCompanion();
            break;
        case "session":
            await loadSession();
            break;
        case "systems":
            await loadSystems();
            break;
        case "surfaces":
            await loadSurfaces();
            break;
        case "knowledge":
            await loadKnowledge();
            break;
        case "runtime":
            await loadRuntime();
            break;
        case "launchpad":
            await loadLaunchpad();
            break;
        default:
            break;
    }
});

async function loadHeadroom() {
    const [companion, session, runtime, overview] = await Promise.all([
        loadCompanion(),
        fetchJson("/api/session"),
        fetchJson("/api/runtime"),
        getOverview()
    ]);

    const mode = readHeadroomMode();
    const emotionalModel = deriveHeadroomEmotion(companion, session, runtime);
    const presence = document.getElementById("headroom-presence");
    const modePanel = document.getElementById("headroom-mode");
    const emotionPanel = document.getElementById("headroom-emotion");
    const atmospherePanel = document.getElementById("headroom-atmosphere");
    const kernelSpine = document.getElementById("headroom-kernel-spine");

    if (presence) {
        presence.innerHTML = headroomPresenceHtml(mode, emotionalModel, companion, session, runtime, overview);
    }
    if (modePanel) {
        modePanel.innerHTML = headroomModeHtml(mode, emotionalModel, companion);
        modePanel.querySelectorAll("[data-headroom-mode]").forEach(button => {
            button.addEventListener("click", async () => {
                writeHeadroomMode(button.dataset.headroomMode);
                await loadHeadroom();
            });
        });
    }
    if (emotionPanel) {
        emotionPanel.innerHTML = headroomEmotionHtml(emotionalModel);
    }
    if (atmospherePanel) {
        atmospherePanel.innerHTML = headroomAtmosphereHtml(mode, emotionalModel, companion, session, runtime, overview);
    }
    if (kernelSpine) {
        kernelSpine.innerHTML = headroomKernelSpineHtml(overview?.legacySpine, companion, session, runtime);
    }
}

async function fetchJson(url, options = {}) {
    const response = await fetch(url, options);
    const data = await response.json();
    if (!response.ok) {
        throw new Error(data.error || `Request failed for ${url}`);
    }
    return data;
}

async function fetchText(url, options = {}) {
    const response = await fetch(url, options);
    const text = await response.text();
    if (!response.ok) {
        throw new Error(text || `Request failed for ${url}`);
    }
    return text;
}

async function fetchSystemState(consumer = "browser") {
    return fetchJson(`/api/system-state?consumer=${encodeURIComponent(consumer)}`);
}

function ensureSystemStateLiveRefresh(timerKey, elementId, consumer, laneLabel) {
    const render = async () => {
        const container = document.getElementById(elementId);
        if (!container) {
            return;
        }
        try {
            const payload = await fetchSystemState(consumer);
            container.innerHTML = systemStateEnvelopeHtml(payload, laneLabel);
        } catch (error) {
            console.error(error);
            container.innerHTML = `<div class="empty-state">Unable to load grounded system state.</div>`;
        }
    };

    const existing = SYSTEM_STATE_REFRESH_TIMERS.get(timerKey);
    if (existing) {
        window.clearInterval(existing);
    }
    render().catch(console.error);
    const timer = window.setInterval(() => {
        render().catch(() => {});
    }, SYSTEM_STATE_REFRESH_MS);
    SYSTEM_STATE_REFRESH_TIMERS.set(timerKey, timer);
}

function readHeadroomMode() {
    try {
        const stored = window.localStorage?.getItem(HEADROOM_MODE_KEY);
        return stored === "garden" ? "garden" : "headroom";
    } catch (error) {
        console.error(error);
        return "headroom";
    }
}

function writeHeadroomMode(mode) {
    try {
        window.localStorage?.setItem(HEADROOM_MODE_KEY, mode === "garden" ? "garden" : "headroom");
    } catch (error) {
        console.error(error);
    }
}

function readMathFocus(params = null) {
    if (params?.get("clearMath") === "1") {
        return null;
    }
    const requested = params?.get("math");
    if (requested && MATH_FAMILY_RULES[requested]) {
        return MATH_FAMILY_RULES[requested];
    }
    try {
        const stored = window.localStorage?.getItem(MATH_FOCUS_MEMORY_KEY);
        return stored && MATH_FAMILY_RULES[stored] ? MATH_FAMILY_RULES[stored] : null;
    } catch (error) {
        console.error(error);
        return null;
    }
}

function syncMathFocus(params = null) {
    if (params?.get("clearMath") === "1") {
        clearMathFocus();
        return null;
    }
    const requested = params?.get("math");
    if (requested !== null) {
        if (MATH_FAMILY_RULES[requested]) {
            rememberMathFocus(requested);
        } else {
            clearMathFocus();
        }
    }
    return readMathFocus(params);
}

function rememberMathFocus(id) {
    try {
        if (!id || !MATH_FAMILY_RULES[id]) {
            window.localStorage?.removeItem(MATH_FOCUS_MEMORY_KEY);
            return;
        }
        window.localStorage?.setItem(MATH_FOCUS_MEMORY_KEY, id);
    } catch (error) {
        console.error(error);
    }
}

function clearMathFocus() {
    try {
        window.localStorage?.removeItem(MATH_FOCUS_MEMORY_KEY);
    } catch (error) {
        console.error(error);
    }
}

function proofLaneById(lanes, id) {
    if (!id || !Array.isArray(lanes)) {
        return null;
    }
    const needle = String(id).trim().toLowerCase();
    return lanes.find(lane => String(lane.id || "").trim().toLowerCase() === needle) || null;
}

function readProofFocus(lanes, params = null) {
    if (params?.get("clearProof") === "1") {
        return null;
    }
    const requested = params?.get("proof");
    if (requested) {
        return proofLaneById(lanes, requested);
    }
    try {
        const stored = window.localStorage?.getItem(PROOF_FOCUS_MEMORY_KEY);
        return stored ? proofLaneById(lanes, stored) : null;
    } catch (error) {
        console.error(error);
        return null;
    }
}

function syncProofFocus(params, lanes) {
    if (params?.get("clearProof") === "1") {
        clearProofFocus();
        return null;
    }
    const requested = params?.get("proof");
    if (requested !== null) {
        const lane = proofLaneById(lanes, requested);
        if (lane) {
            rememberProofFocus(lane.id);
            return lane;
        }
        clearProofFocus();
        return null;
    }
    return readProofFocus(lanes, params);
}

function rememberProofFocus(id) {
    try {
        if (!id) {
            window.localStorage?.removeItem(PROOF_FOCUS_MEMORY_KEY);
            return;
        }
        window.localStorage?.setItem(PROOF_FOCUS_MEMORY_KEY, id);
    } catch (error) {
        console.error(error);
    }
}

function clearProofFocus() {
    try {
        window.localStorage?.removeItem(PROOF_FOCUS_MEMORY_KEY);
    } catch (error) {
        console.error(error);
    }
}

function replaceMathFocusQuery(id) {
    const url = new URL(window.location.href);
    if (id && MATH_FAMILY_RULES[id]) {
        url.searchParams.set("math", id);
        url.searchParams.delete("clearMath");
    } else {
        url.searchParams.delete("math");
        url.searchParams.set("clearMath", "1");
    }
    window.history.replaceState({}, "", url);
}

function replaceProofFocusQuery(id) {
    const url = new URL(window.location.href);
    if (id) {
        url.searchParams.set("proof", id);
        url.searchParams.delete("clearProof");
    } else {
        url.searchParams.delete("proof");
        url.searchParams.set("clearProof", "1");
    }
    window.history.replaceState({}, "", url);
}

function mathFamilyForTitle(title) {
    const lowered = String(title || "").trim().toLowerCase();
    return Object.values(MATH_FAMILY_RULES).find(rule => rule.titles.some(item => item.toLowerCase() === lowered)) || null;
}

function mathFamilyMatchScore(system, family) {
    if (!system || !family) {
        return 0;
    }
    let score = 0;
    const haystack = [
        ...(system.mathKeywords || []),
        ...(system.capabilities || []),
        system.summary || "",
        system.designRole || "",
        ...(system.agiStrata || []),
        system.family || "",
        system.name || ""
    ].join(" ").toLowerCase();
    for (const term of family.terms) {
        if (haystack.includes(term.toLowerCase())) {
            score += 2;
        }
    }
    for (const stratum of family.strata) {
        if ((system.agiStrata || []).includes(stratum)) {
            score += 3;
        }
    }
    return score;
}

function proofLaneTerms(lane) {
    if (!lane) {
        return [];
    }
    const tokens = new Set();
    [lane.title, lane.summary, lane.operatorRole, ...(lane.notes || [])].forEach(value => {
        String(value || "")
            .toLowerCase()
            .split(/[^a-z0-9]+/)
            .filter(token => token.length > 2)
            .forEach(token => tokens.add(token));
    });
    (lane.focusCommands || []).forEach(command => {
        String(command || "")
            .toLowerCase()
            .split(/[^a-z0-9]+/)
            .filter(token => token.length > 1)
            .forEach(token => tokens.add(token));
    });
    (lane.evidencePaths || []).forEach(path => {
        const basename = String(path || "").split("/").pop() || "";
        basename.toLowerCase()
            .split(/[^a-z0-9]+/)
            .filter(token => token.length > 2)
            .forEach(token => tokens.add(token));
    });
    (lane.sections || []).forEach(section => {
        [
            section.surfaceId,
            section.surfaceTitle,
            section.sectionId,
            section.sectionTitle,
            section.theme,
            section.jumpUrl,
            section.routeUrl
        ].forEach(value => {
            String(value || "")
                .toLowerCase()
                .split(/[^a-z0-9]+/)
                .filter(token => token.length > 2)
                .forEach(token => tokens.add(token));
        });
    });
    return Array.from(tokens);
}

function proofLaneMatchScore(system, lane) {
    if (!system || !lane) {
        return 0;
    }
    let score = 0;
    for (const stratum of lane.agiStrata || []) {
        if ((system.agiStrata || []).includes(stratum)) {
            score += 3;
        }
    }
    const haystack = [
        ...(system.capabilities || []),
        ...(system.mathKeywords || []),
        system.summary || "",
        system.designRole || "",
        system.path || "",
        system.workingDirectory || "",
        system.family || "",
        system.name || "",
        system.launchCommand || ""
    ].join(" ").toLowerCase();
    for (const term of proofLaneTerms(lane)) {
        if (haystack.includes(term)) {
            score += 1;
        }
    }
    return score;
}

function proofLaneMatchesNode(node, lane) {
    if (!node || !lane) {
        return false;
    }
    const haystack = [
        node.id || "",
        node.label || "",
        node.summary || "",
        node.description || "",
        node.theme || "",
        ...(node.agiStrata || []),
        node.primaryAgiStratum || "",
        node.path || "",
        node.jumpUrl || "",
        node.routeCommand || ""
    ].join(" ").toLowerCase();
    return (lane.agiStrata || []).some(stratum => (node.agiStrata || []).includes(stratum) || node.primaryAgiStratum === stratum)
        || proofLaneTerms(lane).some(term => haystack.includes(term));
}

function proofLaneMatchSurface(surface, lane) {
    if (!surface || !lane) {
        return 0;
    }
    let score = 0;
    const sectionRefs = lane.sections || [];
    for (const section of sectionRefs) {
        if (section.surfaceId === surface.id) {
            score += 2;
        }
    }
    for (const stratum of lane.agiStrata || []) {
        if ((surface.agiStrata || []).includes(stratum)) {
            score += 2;
        }
    }
    for (const section of surface.sections || []) {
        score += proofLaneMatchSection(surface.id, section, lane);
    }
    return score;
}

function proofLaneMatchSection(surfaceId, section, lane) {
    if (!section || !lane) {
        return 0;
    }
    let score = 0;
    const exact = (lane.sections || []).some(item => item.surfaceId === surfaceId && item.sectionId === section.id);
    if (exact) {
        score += 8;
    }
    for (const stratum of lane.agiStrata || []) {
        if ((section.agiStrata || []).includes(stratum)) {
            score += 1;
        }
    }
    const verbs = new Set((section.primaryVerbs || []).map(item => String(item || "").toLowerCase()));
    for (const command of lane.focusCommands || []) {
        const head = String(command || "").trim().toLowerCase().split(/\s+/, 1)[0];
        if (verbs.has(head)) {
            score += 2;
        }
    }
    return score;
}

function proofLaneMatchesRuntimeAction(action, lane) {
    if (!action || !lane) {
        return false;
    }
    const normalized = String(action).trim().toLowerCase();
    const head = normalized.split(/\s+/, 1)[0];
    return (lane.focusCommands || []).some(command => {
        const candidate = String(command || "").trim().toLowerCase();
        return normalized === candidate || normalized.startsWith(candidate) || head === candidate.split(/\s+/, 1)[0];
    });
}

function preferredProofAction(system, lane) {
    if (!system || !lane) {
        return "";
    }
    const preferred = (lane.focusCommands || []).find(command => {
        const normalized = String(command || "").trim().toLowerCase();
        if (!normalized) {
            return false;
        }
        if (normalized.startsWith("nexus") && system.organismCore) {
            return true;
        }
        if (normalized.startsWith("native") && (system.proofLane || system.kernelDiscipline)) {
            return true;
        }
        if (normalized.startsWith("learn") && system.cognitionCore) {
            return true;
        }
        if (normalized.startsWith("emf") && system.bareMetalSubstrateLane) {
            return true;
        }
        return proofLaneMatchScore(system, lane) > 0;
    });
    return preferred || lane.focusCommands?.[0] || "";
}

function mathFamilyMatchesNode(node, family) {
    if (!node || !family) {
        return false;
    }
    const haystack = [
        node.label || "",
        node.summary || "",
        node.description || "",
        node.theme || "",
        ...(node.agiStrata || []),
        node.primaryAgiStratum || "",
        node.path || ""
    ].join(" ").toLowerCase();
    return family.strata.some(stratum => (node.agiStrata || []).includes(stratum) || node.primaryAgiStratum === stratum)
        || family.terms.some(term => haystack.includes(term.toLowerCase()));
}

async function getOverview() {
    if (!overviewCache) {
        overviewCache = fetchJson("/api/overview");
    }
    return overviewCache;
}

function renderNav() {
    const nav = document.getElementById("sidebar-nav");
    if (!nav) {
        return;
    }

    const activePage = document.body.dataset.page || "overview";
    nav.innerHTML = NAV_ITEMS.map(item => `
        <a class="nav-link ${item.page === activePage ? "active" : ""}" href="${item.href}">
            <span>${item.label}</span>
            <small>${item.note}</small>
        </a>
    `).join("");
}

async function renderSidebarMeta() {
    const meta = document.getElementById("sidebar-meta");
    if (!meta) {
        return;
    }

    const overview = await getOverview();
    meta.innerHTML = `
        <h2>Unified State</h2>
        <p>${overview.counts.subsystems} subsystems, ${overview.counts.commandSurfaces} command surfaces, ${overview.counts.subsystemAgiStrata} subsystem role families, and ${overview.counts.documents} indexed files are active in one browser shell.</p>
        <div class="footer-note">Artifacts live at ${escapeHtml(overview.paths.artifacts)}</div>
    `;
}

async function loadOverview() {
    const overview = await getOverview();
    setMetricGrid("overview-metrics", [
        metric("Subsystems", overview.counts.subsystems, `${overview.counts.families} role families`),
        metric("Command surfaces", overview.counts.commandSurfaces, `${overview.counts.cataloguedCommands} commands`),
        metric("Boot branches", overview.counts.bootTreeBranches, `${overview.counts.uncataloguedBootBranches} uncatalogued`),
        metric("Kernel overlap", overview.counts.kernelOverlapSections, `${overview.counts.kernelPreferredSurfaces} preferred surfaces`),
        metric("Knowledge", overview.counts.documents, `${overview.counts.modules} modules indexed`),
        metric("Readmes", overview.counts.readmes, "source anchors")
    ]);

    renderLegacySpine("overview-legacy-spine", overview.legacySpine);
    renderCommandSpine("overview-command-spine", overview.legacyCommandSpine);
    renderBarList("overview-families", overview.families, "Subsystem families");
    renderBarList("overview-strata", overview.subsystemAgiStrata, "Kernel-role strata");
    renderBarList("overview-modules", overview.topModules, "Top modules");
    renderBarList("overview-signals", overview.topSignals, "Signal terms");
    renderSubsystemCards("overview-subsystems", overview.subsystems);
    renderReadmes("overview-readmes", overview.anchorDocs);

    setCode("overview-abstract", overview.abstract);
    renderArtifacts("overview-artifacts", overview.artifacts);
    renderPrinciples("overview-principles", overview.principles || []);
}

async function loadCompanion() {
    const [data, overview] = await Promise.all([
        fetchJson("/api/companion"),
        fetchJson("/api/overview")
    ]);
    const transcript = data.transcript || [];
    const visualLibrary = data.visualLibrary || [];
    const audioLibrary = data.audioLibrary || [];
    const headroom = data.headroom || {};
    companionLastAssistantTurn = [...transcript].reverse().find(turn => turn.role === "assistant") || null;
    const selectedVisual = visualLibrary.find(item => item.path === companionSelection.visualPath) || null;
    const selectedAudio = audioLibrary.find(item => item.path === companionSelection.audioPath) || null;
    if (!selectedVisual) {
        companionSelection.visualPath = null;
    }
    if (!selectedAudio) {
        companionSelection.audioPath = null;
    }

    setMetricGrid("companion-status", [
        metric("Chat mouth", data.status.chatAvailable ? "online" : "offline", data.status.model || "no local model"),
        metric("Memory", data.status.memoryMessages, "conversation carry"),
        metric("Visuals", data.status.visualLibraryCount, data.status.visualDreamAvailable ? `${data.status.dreamOutputCount || 0} dream outputs visible` : "library only"),
        metric("Audio", data.status.audioLibraryCount, data.status.audioAnalysisAvailable ? "analysis ready" : "basic playback only"),
        metric("Headroom", data.status.headroomLive ? "live" : (data.status.headroomAvailable ? "ready" : "offline"), `${data.status.headroomBroadcasts || 0} broadcasts`)
    ]);
    renderLegacySpine("companion-legacy-spine", overview.legacySpine);
    ensureSystemStateLiveRefresh("companion-system-state", "companion-system-state", "companion", "Companion");

    const thread = document.getElementById("companion-thread");
    if (thread) {
        thread.innerHTML = companionThreadHtml(transcript);
    }

    const headroomContainer = document.getElementById("companion-headroom");
    if (headroomContainer) {
        headroomContainer.innerHTML = companionHeadroomStatusHtml(
            companionHeadroomMemory,
            headroom,
            companionLastAssistantTurn,
            selectedVisual || data.latestDream || null,
            data.commandDeck || overview.commandDecks?.broadcast || null
        );
        headroomContainer.querySelectorAll("[data-companion-headroom]").forEach(button => {
            button.addEventListener("click", async () => {
                const action = button.dataset.companionHeadroom;
                const input = document.getElementById("companion-prompt");
                const typedPrompt = (input?.value || "").trim();
                const fallbackText = companionLastAssistantTurn?.content || typedPrompt || "";
                const visualPath = companionSelection.visualPath || data.latestDream?.path || "";
                const payload = {
                    action,
                    text: fallbackText,
                    visualPath,
                    intensity: Number(button.dataset.intensity || 0.72)
                };
                if ((action === "speak" || action === "broadcast") && !payload.text) {
                    return;
                }
            headroomContainer.innerHTML = `<div class="footer-note">Routing ${escapeHtml(action)} through HeadroomInterface / MaxHeadroomLive...</div>`;
                try {
                    companionHeadroomMemory = await fetchJson("/api/companion/headroom", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(payload)
                    });
                    await loadCompanion();
                } catch (error) {
                    console.error(error);
                    headroomContainer.innerHTML = `<div class="empty-state">Headroom action failed.</div>`;
                }
            });
        });
    }

    const selection = document.getElementById("companion-selection");
    if (selection) {
        selection.innerHTML = companionSelectionHtml(selectedVisual, selectedAudio);
        selection.querySelectorAll("[data-clear-companion]").forEach(button => {
            button.addEventListener("click", () => {
                if (button.dataset.clearCompanion === "visual") {
                    companionSelection.visualPath = null;
                }
                if (button.dataset.clearCompanion === "audio") {
                    companionSelection.audioPath = null;
                }
                loadCompanion().catch(console.error);
            });
        });
    }

    const dreamStatus = document.getElementById("companion-dream-status");
    if (dreamStatus) {
        dreamStatus.innerHTML = companionDreamStatusHtml(companionDreamMemory, data.latestDream, data.status);
        dreamStatus.querySelectorAll("[data-use-dream-visual]").forEach(button => {
            button.addEventListener("click", () => {
                companionSelection.visualPath = button.dataset.useDreamVisual || null;
                loadCompanion().catch(console.error);
            });
        });
    }

    const quickPrompts = document.getElementById("companion-quick-prompts");
    if (quickPrompts) {
        quickPrompts.innerHTML = (data.quickPrompts || []).map(prompt => `
            <button class="btn ghost" type="button" data-companion-prompt="${escapeHtml(prompt)}">${escapeHtml(prompt)}</button>
        `).join("");
        quickPrompts.querySelectorAll("[data-companion-prompt]").forEach(button => {
            button.addEventListener("click", () => {
                const input = document.getElementById("companion-prompt");
                if (input) {
                    input.value = button.dataset.companionPrompt;
                    input.focus();
                }
            });
        });
    }

    const visualContainer = document.getElementById("companion-visual-library");
    if (visualContainer) {
        visualContainer.innerHTML = companionLibraryHtml(visualLibrary, "visual", companionSelection.visualPath);
        visualContainer.querySelectorAll("[data-select-visual]").forEach(button => {
            button.addEventListener("click", () => {
                companionSelection.visualPath = button.dataset.selectVisual;
                loadCompanion().catch(console.error);
            });
        });
    }

    const audioContainer = document.getElementById("companion-audio-library");
    if (audioContainer) {
        audioContainer.innerHTML = companionLibraryHtml(audioLibrary, "audio", companionSelection.audioPath);
        audioContainer.querySelectorAll("[data-select-audio]").forEach(button => {
            button.addEventListener("click", () => {
                companionSelection.audioPath = button.dataset.selectAudio;
                loadCompanion().catch(console.error);
            });
        });
    }

    const form = document.getElementById("companion-form");
    if (form && !form.dataset.bound) {
        form.dataset.bound = "true";
        form.addEventListener("submit", async event => {
            event.preventDefault();
            const input = document.getElementById("companion-prompt");
            const prompt = (input?.value || "").trim();
            if (!prompt) {
                return;
            }
            const submit = form.querySelector("button[type='submit']");
            const dream = form.querySelector("[data-companion-dream]");
            if (submit) {
                submit.disabled = true;
            }
            if (dream) {
                dream.disabled = true;
            }
            try {
                const response = await fetchJson("/api/companion/chat", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                  body: JSON.stringify({
                      prompt,
                      visualPath: companionSelection.visualPath || "",
                      audioPath: companionSelection.audioPath || ""
                  })
              });
              companionLastAssistantTurn = response.transcript ? [...response.transcript].reverse().find(turn => turn.role === "assistant") || companionLastAssistantTurn : companionLastAssistantTurn;
              companionDreamMemory = response.dream && response.dream !== null ? response.dream : companionDreamMemory;
              if (response.suggestedVisual?.path) {
                  companionSelection.visualPath = response.suggestedVisual.path;
                }
                if (input) {
                    input.value = "";
                }
                await loadCompanion();
            } finally {
                if (submit) {
                    submit.disabled = false;
                }
                if (dream) {
                    dream.disabled = false;
                }
            }
        });

        const dreamButton = form.querySelector("[data-companion-dream]");
        if (dreamButton) {
            dreamButton.addEventListener("click", async () => {
                const input = document.getElementById("companion-prompt");
                const prompt = (input?.value || "").trim();
                if (!prompt) {
                    return;
                }
                const submit = form.querySelector("button[type='submit']");
                submit && (submit.disabled = true);
                dreamButton.disabled = true;
                const dreamStatus = document.getElementById("companion-dream-status");
                if (dreamStatus) {
                    dreamStatus.innerHTML = `<div class="footer-note">Manifesting a new visual through the local dream lane...</div>`;
                }
                try {
                    const response = await fetchJson("/api/companion/dream", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        prompt,
                        visualPath: companionSelection.visualPath || "",
                        audioPath: companionSelection.audioPath || ""
                    })
                });
                companionDreamMemory = response;
                    if (response.visual?.path) {
                        companionSelection.visualPath = response.visual.path;
                    }
                    await loadCompanion();
                } finally {
                    submit && (submit.disabled = false);
                    dreamButton.disabled = false;
                }
            });
        }
    }

    return data;
}

async function loadSession() {
    const [data, overview] = await Promise.all([
        fetchJson("/api/session"),
        fetchJson("/api/overview")
    ]);
    const demoButton = document.getElementById("session-demo-run");
    const params = new URLSearchParams(window.location.search);
    const requestedReviewId = params.get("review");
    const queryCommand = params.get("cmd");
    const runForm = document.getElementById("session-run-form");
    const runInput = document.getElementById("session-run-command");
    const output = document.getElementById("session-output");

    const sessionDeck = data.commandDeck || overview.commandDecks?.session || null;
    if (runInput) {
        if (queryCommand && queryCommand.trim()) {
            runInput.value = queryCommand.trim();
        } else if (!runInput.value.trim()) {
            runInput.value = firstCommandDeckCommand(sessionDeck) || "status";
        }
        if (sessionDeck?.placeholder) {
            runInput.placeholder = sessionDeck.placeholder;
        }
    }
    if (runForm && runInput && !runForm.dataset.bound) {
        runForm.dataset.bound = "1";
        runForm.addEventListener("submit", async event => {
            event.preventDefault();
            await runCommand(runInput.value, "session-output");
        });
    }
    const sessionFills = document.getElementById("session-quick-fills");
    if (sessionFills) {
        sessionFills.innerHTML = commandDeckButtonsHtml(sessionDeck, "session-fill", "ghost");
        sessionFills.querySelectorAll("[data-session-fill]").forEach(button => {
            button.onclick = () => {
                if (runInput) {
                    runInput.value = button.dataset.sessionFill || "";
                    runInput.focus();
                }
            };
        });
    }
    if (output && !output.textContent.trim()) {
        output.textContent = "Run a command to see output here.";
    }

    setMetricGrid("session-metrics", [
        metric("Pending reviews", data.counts.pendingReviews, `${data.counts.reviews} total review requests`),
        metric("Recent runs", data.counts.recentRuns, "selfbuild memory"),
        metric("Proof records", data.counts.proofs, "live proof lane"),
        metric("Ledger models", data.counts.ledgerModels, `${data.counts.ledgerRuntimes} runtime entries`),
        metric("Plan weeks", data.counts.weeksReady || 0, `${data.counts.weeksTotal || 0} tracked`)
    ]);
    renderCommandSpine("session-command-spine", overview.legacyCommandSpine);
    setCode("session-status", data.statusText || "Session state unavailable.");
    ensureSystemStateLiveRefresh("session-system-state", "session-system-state", "session", "Session");

    const demo = document.getElementById("session-demo");
    if (demo) {
        demo.innerHTML = sessionDemoHtml(data);
    }
    if (demoButton) {
        demoButton.addEventListener("click", async () => {
            await runCommand(data.demoCommand || "demo guided", "session-output");
            await loadSession();
        });
    }

    const demoInlineButton = document.querySelector("[data-session-demo]");
    if (demoInlineButton) {
        demoInlineButton.addEventListener("click", async () => {
            await runCommand(demoInlineButton.dataset.sessionDemo, "session-output");
            await loadSession();
        });
    }

    const reviews = document.getElementById("session-reviews");
    if (reviews) {
        reviews.innerHTML = sessionReviewsHtml(data.reviews || []);
        reviews.querySelectorAll("[data-review-focus]").forEach(button => {
            button.addEventListener("click", async () => {
                replaceSessionReviewQuery(button.dataset.reviewFocus);
                await loadSession();
            });
        });
        reviews.querySelectorAll("[data-review-approve]").forEach(button => {
            button.addEventListener("click", async () => {
                await runCommand(button.dataset.reviewApprove, "session-output");
                await loadSession();
            });
        });
        reviews.querySelectorAll("[data-review-reject]").forEach(button => {
            button.addEventListener("click", async () => {
                const noteInput = document.getElementById(`session-reject-note-${button.dataset.reviewId}`);
                const note = (noteInput?.value || "").trim();
                const command = note
                    ? `selfbuild reject ${button.dataset.reviewId} :: ${note}`
                    : button.dataset.reviewReject;
                await runCommand(command, "session-output");
                await loadSession();
            });
        });
    }
    const reviewDetail = document.getElementById("session-review-detail");
    if (reviewDetail) {
        const fallbackReview = (data.reviews || []).find(item => item.status === "pending") || (data.reviews || [])[0] || null;
        const activeReview = (data.reviews || []).find(item => item.reviewId === requestedReviewId) || fallbackReview;
        const reviewStack = document.getElementById("session-review-stack");
        if (reviewStack) {
            const reviewDisclosure = reviewStack.querySelector("details.command-group");
            if (reviewDisclosure) {
                reviewDisclosure.open = Boolean((data.reviews || []).some(item => item.status === "pending"));
            }
        }
        const reviewDetailStack = document.getElementById("session-review-detail-stack");
        if (reviewDetailStack) {
            reviewDetailStack.open = Boolean(activeReview);
        }
        await renderSessionReviewDetail(reviewDetail, activeReview);
    }

    const proofs = document.getElementById("session-proofs");
    if (proofs) {
        proofs.innerHTML = sessionProofsHtml(data.proofs || []);
    }

    const selfbuild = document.getElementById("session-selfbuild");
    if (selfbuild) {
        selfbuild.innerHTML = sessionArtifactsHtml(data.recentSelfBuildRuns || []);
    }

    const ledger = document.getElementById("session-ledger");
    if (ledger) {
        ledger.innerHTML = sessionLedgerHtml(data.substrateLedger || {});
    }

    const connectomePlan = document.getElementById("session-connectome-plan");
    if (connectomePlan) {
        connectomePlan.innerHTML = sessionConnectomePlanHtml(data.connectomePlan || {});
    }
}

async function loadSystems() {
    const [data, operator, overview] = await Promise.all([
        fetchJson("/api/systems"),
        fetchJson("/api/operator"),
        fetchJson("/api/overview")
    ]);
    const params = new URLSearchParams(window.location.search);
    const focusId = params.get("focus");
    const focusDetail = params.get("detail") || "inspect";
    const activeMath = syncMathFocus(params);
    const activeProof = syncProofFocus(params, operator.livingProofLanes || []);
    setMetricGrid("systems-summary", [
        metric("Subsystems", data.counts.subsystems, "browser-mapped"),
        metric("Families", data.counts.families, "operating layers"),
        metric("AGI roles", data.counts.agiStrata, "shared vocabulary")
    ]);
    renderLegacySpine("systems-legacy-spine", overview.legacySpine);
    renderBarList("systems-strata", data.agiStrata, "Subsystem AGI strata");

    const filters = document.getElementById("systems-filters");
    const grid = document.getElementById("systems-grid");
    const focusPanel = document.getElementById("systems-focus");
    const mathPanel = document.getElementById("systems-math-focus");
    const proofPanel = document.getElementById("systems-proof-focus");
    let active = "all";
    const focusedSystem = focusId ? data.systems.find(item => item.id === focusId) : null;
    if (focusedSystem) {
        active = focusedSystem.family;
    }
    if (mathPanel) {
        const matchedSystems = activeMath
            ? [...data.systems].filter(system => mathFamilyMatchScore(system, activeMath) > 0)
                .sort((left, right) => mathFamilyMatchScore(right, activeMath) - mathFamilyMatchScore(left, activeMath))
            : [];
        mathPanel.innerHTML = systemsMathFocusHtml(activeMath, matchedSystems);
    }
    if (proofPanel) {
        const matchedSystems = activeProof
            ? [...data.systems].filter(system => proofLaneMatchScore(system, activeProof) > 0)
                .sort((left, right) => proofLaneMatchScore(right, activeProof) - proofLaneMatchScore(left, activeProof))
            : [];
        proofPanel.innerHTML = systemsProofFocusHtml(activeProof, matchedSystems);
    }
    if (focusPanel) {
        if (focusedSystem) {
            const detail = await fetchJson(`/api/systems/${encodeURIComponent(focusId)}`);
            const focusMemory = readSystemFocusMemory(focusId);
            focusPanel.innerHTML = systemsFocusHtml(detail, focusDetail, focusMemory, activeProof);
            focusPanel.querySelectorAll("[data-system-action]").forEach(button => {
                button.addEventListener("click", () => {
                    runCommand(button.dataset.systemAction, "systems-focus-output")
                        .then(result => {
                            if (result) {
                                rememberSystemFocusResult(focusId, result);
                            }
                        })
                        .catch(handleError);
                });
            });
        } else {
            focusPanel.innerHTML = `Select a subsystem from the operator map to open its focused lens here.`;
        }
    }

    const render = () => {
        const systems = active === "all"
            ? data.systems
            : data.systems.filter(item => item.family === active);
        const rankedSystems = [...systems].sort((left, right) => {
            const proofDelta = proofLaneMatchScore(right, activeProof) - proofLaneMatchScore(left, activeProof);
            if (proofDelta !== 0) {
                return proofDelta;
            }
            const mathDelta = mathFamilyMatchScore(right, activeMath) - mathFamilyMatchScore(left, activeMath);
            if (mathDelta !== 0) {
                return mathDelta;
            }
            return String(left.name || "").localeCompare(String(right.name || ""));
        });
        grid.innerHTML = rankedSystems.map(system => systemCard(system, focusId, activeMath, activeProof)).join("");
        filters.querySelectorAll(".btn").forEach(button => {
            button.classList.toggle("primary", button.dataset.family === active);
            button.classList.toggle("secondary", button.dataset.family !== active);
        });
        focusCard(grid, "systemId", focusId);
    };

    filters.innerHTML = `
        <button class="btn primary" data-family="all">All</button>
        ${data.families.map(item => `
            <button class="btn secondary" data-family="${escapeHtml(item.name)}">${escapeHtml(item.name)} (${item.count})</button>
        `).join("")}
    `;

    filters.querySelectorAll(".btn").forEach(button => {
        button.addEventListener("click", () => {
            active = button.dataset.family;
            render();
        });
    });

    render();
}

async function loadSurfaces() {
    const [data, operator, overview] = await Promise.all([
        fetchJson("/api/surfaces"),
        fetchJson("/api/operator"),
        fetchJson("/api/overview")
    ]);
    const params = new URLSearchParams(window.location.search);
    const focusSurface = params.get("surface");
    const focusSection = params.get("section");
    const activeProof = syncProofFocus(params, operator.livingProofLanes || []);
    setMetricGrid("surfaces-summary", [
        metric("Surfaces", data.surfaceCount, `${operator.kernelPreferredSurfaceCount} kernel-first`),
        metric("Sections", data.sectionCount, `${operator.kernelOverlapSectionCount} overlap-aware`),
        metric("Commands", data.commandCount, "catalogued and discovered"),
        metric("Boot tree", operator.bootTreeBranchCount, `${operator.uncataloguedBootBranchCount} uncatalogued branches`),
        metric("Living proof lanes", operator.livingProofLaneCount || 0, "first-class proof surfaces"),
        metric("Critical lanes", priorityCount(operator.priorityCounts, "critical"), "manifest-ranked"),
        metric("AGI strata", operator.surfaceAgiStrataCounts.length, "surface role families")
    ]);

      const brief = document.getElementById("surfaces-operator-brief");
      if (brief) {
          brief.innerHTML = operatorBriefHtml(operator);
      }
      renderCommandSpine("surfaces-command-spine", overview.legacyCommandSpine);

    const proofLanes = document.getElementById("surfaces-proof-lanes");
    if (proofLanes) {
        proofLanes.innerHTML = livingProofLanesHtml(operator.livingProofLanes || [], activeProof);
    }

    const grid = document.getElementById("surfaces-grid");
    const rankedSurfaces = [...data.surfaces].sort((left, right) =>
        proofLaneMatchSurface(right, activeProof) - proofLaneMatchSurface(left, activeProof)
    );
    grid.innerHTML = rankedSurfaces.map(surface => `
        <section class="surface-card ${surface.id === focusSurface ? "focused-card" : ""} ${proofLaneMatchSurface(surface, activeProof) > 0 ? "proof-focus-match" : ""}" data-surface-id="${escapeHtml(surface.id)}">
            <div class="tiny-label">${escapeHtml(surface.primarySubsystem)}</div>
            <h3 class="surface-title">${escapeHtml(surface.title)}</h3>
            <p class="surface-summary">${escapeHtml(surface.description)}</p>
            <div class="chip-row" style="margin: 16px 0 14px;">
                <span class="chip positive">${surface.sectionCount} sections</span>
                <span class="chip warning">${surface.commandCount} commands</span>
                <span class="chip ${surface.kernelOwnedPreferred ? "positive" : "warning"}">${surface.kernelOwnedPreferred ? "kernel-first" : "mapped breadth"}</span>
                <span class="chip">${escapeHtml(surface.integrationPriority)}</span>
                ${proofLaneMatchSurface(surface, activeProof) > 0 ? `<span class="chip positive">proof lane ${escapeHtml(String(proofLaneMatchSurface(surface, activeProof)))}</span>` : ""}
                ${surface.discoveredCommandCount ? `<span class="chip warning">${surface.discoveredCommandCount} discovered boot commands</span>` : ""}
            </div>
            <div class="spec-grid" style="margin-bottom: 14px;">
                <div class="spec">
                    <strong>Authority</strong>
                    <span>${escapeHtml(surface.authorityLevel)}</span>
                </div>
                <div class="spec">
                    <strong>Execution</strong>
                    <span>${escapeHtml(surface.executionModel)}</span>
                </div>
            </div>
            ${renderStrataChips(surface.agiStrata, "margin: 0 0 14px;")}
            <div class="chip-row" style="margin-bottom: 14px;">
                ${booleanRoleChip(surface.governingCore, "governing")}
                ${booleanRoleChip(surface.cognitionCore, "cognition")}
                ${booleanRoleChip(surface.organismCore, "organism")}
                ${booleanRoleChip(surface.absorptionCore, "absorption")}
                ${booleanRoleChip(surface.proofLane, "proof")}
                ${booleanRoleChip(surface.modelSubstrateLane, "model substrate")}
                ${booleanRoleChip(surface.bareMetalSubstrateLane, "bare metal")}
                ${booleanRoleChip(surface.kernelDiscipline, "kernel discipline")}
                ${booleanRoleChip(surface.adjacentHistoricalCore, "historical core")}
            </div>
            <p class="muted-text">${escapeHtml(surface.migrationPolicy)}</p>
            ${surface.sections.map(section => commandGroup(surface.id, section, surface.id === focusSurface ? focusSection : null, activeProof)).join("")}
        </section>
    `).join("");
    focusCard(grid, "surfaceId", focusSurface);
    focusSurfaceSection(grid, focusSurface, focusSection);
}

async function loadKnowledge() {
    const [data, overview] = await Promise.all([
        fetchJson("/api/knowledge"),
        fetchJson("/api/overview")
    ]);
    syncMathFocus(new URLSearchParams(window.location.search));
    setMetricGrid("knowledge-summary", [
        metric("Documents", data.documentCount, "indexed for abstraction"),
        metric("Readmes", data.readmeCount, "anchor documents"),
        metric("Modules", data.moduleCount, "repo footprints")
    ]);

    renderLegacySpine("knowledge-legacy-spine", overview.legacySpine);
    renderBarList("knowledge-modules", data.modules, "Top modules");
    renderBarList("knowledge-signals", data.signals, "Dominant signals");
    renderReadmes("knowledge-readmes", data.readmes);
    setCode("knowledge-abstract", data.abstract);
    renderArtifacts("knowledge-artifacts", data.artifacts);
    ensureSystemStateLiveRefresh("knowledge-system-state", "knowledge-system-state", "knowledge", "Knowledge");
    await renderKnowledgeGraph(data.artifacts);
    await renderMathCanon(data.artifacts);

    const form = document.getElementById("knowledge-search-form");
    const input = document.getElementById("knowledge-search-input");
    const results = document.getElementById("knowledge-results");
    const preview = document.getElementById("knowledge-preview");

    const runSearch = async (query) => {
        if (!query.trim()) {
            results.innerHTML = `<div class="empty-state">Enter a term to search indexed docs, code, and build files.</div>`;
            return;
        }

        results.innerHTML = `<div class="empty-state">Searching "${escapeHtml(query)}"...<\/div>`;
        const search = await fetchJson(`/api/search?q=${encodeURIComponent(query)}`);
        if (!search.results.length) {
            results.innerHTML = `<div class="empty-state">No results for "${escapeHtml(query)}".<\/div>`;
            return;
        }

        results.innerHTML = `
            <ul class="list-reset link-list">
                ${search.results.map(result => `
                    <li class="doc-card">
                        <a href="#" data-doc="${escapeHtml(result.path)}">${escapeHtml(result.title)}</a>
                        <div class="muted-text">${escapeHtml(result.path)} | ${escapeHtml(result.module)} | score ${result.score}</div>
                    </li>
                `).join("")}
            </ul>
        `;

        results.querySelectorAll("[data-doc]").forEach(link => {
            link.addEventListener("click", async event => {
                event.preventDefault();
                const path = link.dataset.doc;
                const doc = await fetchJson(`/api/doc?path=${encodeURIComponent(path)}`);
                preview.innerHTML = `
                    <div class="tiny-label">${escapeHtml(doc.module)} | ${escapeHtml(doc.extension)}</div>
                    <h3 class="doc-title">${escapeHtml(doc.title)}</h3>
                    <div class="muted-text">${escapeHtml(doc.path)}</div>
                    <pre class="preview-block">${escapeHtml(doc.preview)}</pre>
                `;
            });
        });
    };

    form.addEventListener("submit", event => {
        event.preventDefault();
        runSearch(input.value);
    });

    await runSearch("ultimate");
}

async function loadRuntime() {
    const [operator, systemsData, overview, runtimeData] = await Promise.all([
        fetchJson("/api/operator"),
        fetchJson("/api/systems"),
        fetchJson("/api/overview"),
        fetchJson("/api/runtime")
    ]);
    const params = new URLSearchParams(window.location.search);
    const activeMath = syncMathFocus(params);
    const activeProof = syncProofFocus(params, operator.livingProofLanes || []);
    const focusSubsystemId = params.get("subsystem");
    const runtimeMemory = focusSubsystemId ? readRuntimeFocusMemory(focusSubsystemId) : null;
    const focusPanelName = params.get("panel") || runtimeMemory?.panel || null;
    const focusAction = params.get("action") || runtimeMemory?.action || null;
    const metrics = document.getElementById("runtime-metrics");
    const events = document.getElementById("runtime-events");
    const status = document.getElementById("runtime-status");
    const actions = document.getElementById("runtime-actions");
    const role = document.getElementById("runtime-role");
    const focus = document.getElementById("runtime-focus");
      const mathPanel = document.getElementById("runtime-math-focus");
      const proofPanel = document.getElementById("runtime-proof-focus");
      renderLegacySpine("runtime-legacy-spine", overview.legacySpine);
      renderCommandSpine("runtime-command-spine", overview.legacyCommandSpine);
      ensureSystemStateLiveRefresh("runtime-system-state", "runtime-system-state", "runtime", "Runtime");

    const nativeSurface = operator.surfaces.find(item => item.id === "native");
    if (role && nativeSurface) {
        role.innerHTML = runtimeRoleHtml(nativeSurface, operator);
    }
    if (mathPanel) {
        const matchedSystems = activeMath
            ? [...systemsData.systems].filter(system => mathFamilyMatchScore(system, activeMath) > 0)
                .sort((left, right) => mathFamilyMatchScore(right, activeMath) - mathFamilyMatchScore(left, activeMath))
            : [];
        mathPanel.innerHTML = runtimeMathFocusHtml(activeMath, matchedSystems);
    }
    if (proofPanel) {
        const matchedSystems = activeProof
            ? [...systemsData.systems].filter(system => proofLaneMatchScore(system, activeProof) > 0)
                .sort((left, right) => proofLaneMatchScore(right, activeProof) - proofLaneMatchScore(left, activeProof))
            : [];
        proofPanel.innerHTML = runtimeProofFocusHtml(activeProof, matchedSystems);
    }

    if (focus) {
        if (focusSubsystemId || focusPanelName || focusAction) {
            let subsystem = null;
            if (focusSubsystemId) {
                try {
                    subsystem = await fetchJson(`/api/systems/${encodeURIComponent(focusSubsystemId)}`);
                } catch (error) {
                    console.error(error);
                }
            }
            focus.innerHTML = runtimeFocusHtml(subsystem, focusPanelName, focusAction, runtimeMemory);
        } else {
            focus.innerHTML = `Open a runtime-aware subsystem from the operator map to focus its live panel here.`;
        }
    }

    const quickCommands = async (command) => {
        const result = await runCommand(command, "runtime-output");
        await renderRuntimeState();
        return result;
    };

    const runtimeDeck = runtimeData.commandDeck || overview.commandDecks?.runtime || null;
    const runtimeActions = commandDeckCommands(runtimeDeck).length
        ? commandDeckCommands(runtimeDeck)
        : [
            { command: "aether", label: runtimeActionLabel("aether") },
            { command: "braid", label: runtimeActionLabel("braid") },
            { command: "learn PHI_FIELD", label: runtimeActionLabel("learn PHI_FIELD") },
            { command: "nexus awaken", label: runtimeActionLabel("nexus awaken") },
            { command: "miving gen 24", label: runtimeActionLabel("miving gen 24") },
            { command: "native shutdown", label: runtimeActionLabel("native shutdown") }
        ];
    runtimeActions.sort((left, right) => {
            const rightProof = proofLaneMatchesRuntimeAction(right.command, activeProof) ? 1 : 0;
            const leftProof = proofLaneMatchesRuntimeAction(left.command, activeProof) ? 1 : 0;
            if (rightProof !== leftProof) {
                return rightProof - leftProof;
            }
            const rightMath = activeMath?.actions.includes(right.command) ? 1 : 0;
            const leftMath = activeMath?.actions.includes(left.command) ? 1 : 0;
            return rightMath - leftMath;
        });
    actions.innerHTML = runtimeActions.map((item, index) => `
        <button class="btn ${index === 0 ? "primary" : item.command === "native shutdown" ? "ghost" : "secondary"} ${(activeMath?.actions.includes(item.command) ? "math-focus-match" : "")} ${(proofLaneMatchesRuntimeAction(item.command, activeProof) ? "proof-focus-match" : "")}" data-command="${escapeHtml(item.command)}">${escapeHtml(item.label || runtimeActionLabel(item.command))}</button>
    `).join("");

    actions.querySelectorAll("[data-command]").forEach(button => {
        button.addEventListener("click", () => {
            quickCommands(button.dataset.command)
                .then(result => {
                    if (result && focusSubsystemId) {
                        rememberRuntimeFocusResult(focusSubsystemId, focusPanelName || "actions", result);
                    }
                })
                .catch(handleError);
        });
    });
    if (focusAction) {
        const buttons = Array.from(actions.querySelectorAll("[data-command]"));
        const target = buttons.find(button => button.dataset.command === focusAction);
        if (target) {
            buttons.forEach(button => {
                button.classList.toggle("primary", button === target);
                button.classList.toggle("secondary", button !== target);
            });
            const output = document.getElementById("runtime-output");
            if (output) {
                const rememberedOutput = runtimeMemory && runtimeMemory.action === focusAction && runtimeMemory.output
                    ? `Last remembered runtime result${formatRememberedAt(runtimeMemory.updatedAt)}:\n\n> ${runtimeMemory.action}\n\n${runtimeMemory.output}`
                    : `Focused runtime action ready:\n\n> ${focusAction}\n\nUse the highlighted control to execute it live.`;
                output.textContent = rememberedOutput;
            }
        }
    } else if (activeMath) {
        const output = document.getElementById("runtime-output");
        if (output) {
            output.textContent = `Math focus active: ${activeMath.label}\n\nSuggested runtime actions:\n${activeMath.actions.map(action => `- ${action}`).join("\n")}`;
        }
    } else if (activeProof) {
        const output = document.getElementById("runtime-output");
        if (output) {
            output.textContent = `Living proof focus active: ${activeProof.title}\n\nSuggested runtime actions:\n${(activeProof.focusCommands || []).map(action => `- ${action}`).join("\n")}`;
        }
    }
    focusRuntimePanel(focusPanelName);

    async function renderRuntimeState() {
        const data = await fetchJson("/api/runtime");
        const snap = data.snapshot;
        setMetricGrid("runtime-metrics", [
            metric("Concepts", snap.conceptSpace, snap.aetherActive ? "aether online" : "aether offline"),
            metric("Spatial nodes", snap.spatialNodes, `${snap.fusionEvents} fusion events`),
            metric("Nexus", snap.nexusState, snap.omniMonitoring ? "omnicast monitoring" : "omnicast idle"),
            metric("Native strata", nativeSurface ? nativeSurface.agiStrata.length : 0, "runtime role families")
        ]);

        status.innerHTML = `
            <div class="chip-row" style="margin-bottom: 14px;">
                ${statusChip(snap.aetherActive, "Aether")}
                ${statusChip(snap.braidActive, "Braid")}
                ${statusChip(snap.gravityActive, "Gravity")}
                ${statusChip(snap.fusionActive, "Fusion")}
                ${statusChip(snap.qboxLoaded, "QBox")}
                ${statusChip(!!snap.miving, "Miving")}
            </div>
            ${nativeSurface ? renderStrataChips(nativeSurface.agiStrata, "margin-bottom: 14px;") : ""}
            ${nativeSurface ? `
                <div class="chip-row" style="margin-bottom: 14px;">
                    ${booleanRoleChip(nativeSurface.governingCore, "governing")}
                    ${booleanRoleChip(nativeSurface.cognitionCore, "cognition")}
                    ${booleanRoleChip(nativeSurface.organismCore, "organism")}
                    ${booleanRoleChip(nativeSurface.absorptionCore, "absorption")}
                    ${booleanRoleChip(nativeSurface.proofLane, "proof")}
                    ${booleanRoleChip(nativeSurface.modelSubstrateLane, "model substrate")}
                    ${booleanRoleChip(nativeSurface.bareMetalSubstrateLane, "bare metal")}
                    ${booleanRoleChip(nativeSurface.kernelDiscipline, "kernel discipline")}
                </div>
            ` : ""}
            <pre class="code-block">${escapeHtml(data.statusText)}</pre>
        `;

        events.innerHTML = snap.recentEvents && snap.recentEvents.length
            ? `<div class="timeline">${snap.recentEvents.map(event => `<div class="timeline-item">${escapeHtml(event)}</div>`).join("")}</div>`
            : `<div class="empty-state">No runtime events yet. Trigger aether, braid, or miving to wake the kernel.</div>`;
    }

    await renderRuntimeState();
    window.setInterval(() => {
        renderRuntimeState().catch(() => {});
    }, 5000);
}

async function loadLaunchpad() {
    const systems = await fetchJson("/api/systems");
    const overview = await getOverview();
    const output = document.getElementById("launchpad-output");
    const form = document.getElementById("launchpad-form");
    const input = document.getElementById("launchpad-command");
    const presets = document.getElementById("launchpad-presets");
    const routes = document.getElementById("launchpad-routes");
    const role = document.getElementById("launchpad-role");
    const commandSpine = document.getElementById("launchpad-command-spine");

    if (role) {
        role.innerHTML = launchpadRoleHtml(systems, overview);
    }
    if (commandSpine) {
        renderCommandSpine("launchpad-command-spine", overview.legacyCommandSpine);
    }

    const launchpadDeck = overview.commandDecks?.launchpad || null;
    const presetCommands = commandDeckCommands(launchpadDeck).length
        ? commandDeckCommands(launchpadDeck).map(item => item.command)
        : (overview?.legacyCommandSpine?.presets || []).length
        ? overview.legacyCommandSpine.presets
        : [
            "status",
            "help",
            "physics",
            "build desktop",
            "exodus",
            "maxheadroom",
            "native status",
            "engine status"
        ];

    presets.innerHTML = presetCommands.map(command => `
        <button class="btn secondary" type="button" data-command="${escapeHtml(command)}">${escapeHtml(command)}</button>
    `).join("");

    presets.querySelectorAll("[data-command]").forEach(button => {
        button.addEventListener("click", () => {
            input.value = button.dataset.command;
        });
    });

    routes.innerHTML = systems.systems.slice(0, 8).map(system => `
        <article class="route-card">
            <div class="tiny-label">${escapeHtml(system.family)}</div>
            <h3 class="system-title">${escapeHtml(system.name)}</h3>
            <p class="system-summary">${escapeHtml(system.summary)}</p>
            <div class="chip-row" style="margin: 14px 0;">
                <span class="chip ${system.runnable ? "positive" : "warning"}">${system.runnable ? "wired launch" : "manual only"}</span>
            </div>
            <div class="code-block">${escapeHtml(system.launchCommand)}</div>
            <div class="button-row" style="margin-top: 14px;">
                <button class="btn ghost" type="button" data-fill="system ${escapeHtml(system.id)}">Inspect</button>
                <button class="btn secondary" type="button" data-fill="route ${escapeHtml(system.id)}">Route</button>
            </div>
        </article>
    `).join("");

    routes.querySelectorAll("[data-fill]").forEach(button => {
        button.addEventListener("click", () => {
            input.value = button.dataset.fill;
        });
    });

    const queryCommand = new URLSearchParams(window.location.search).get("cmd");
    if (queryCommand) {
        input.value = queryCommand;
    }

    form.addEventListener("submit", async event => {
        event.preventDefault();
        await runCommand(input.value, "launchpad-output");
    });

    output.textContent = "Enter a Fraynix shell command or a route request and run it through the browser shell.";
}

async function runCommand(command, outputId) {
    const output = document.getElementById(outputId);
    if (!output) {
        throw new Error(`Missing output element ${outputId}`);
    }
    const text = command.trim();
    if (!text) {
        output.textContent = "Command is empty.";
        return null;
    }

    output.textContent = `> ${text}\n\nRunning...`;
    const result = await fetchJson("/api/execute", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ command: text })
    });
    const modeLabel = result.mode === "fraynix-boot"
        ? "[fraynix-boot]\n"
        : result.mode === "ai-router" ? "[ai-router]\n" : "";
    output.textContent = `> ${result.command}\n\n${modeLabel}${result.output || "(no output)"}`;
    return result;
}

function firstCommandDeckCommand(deck) {
    const commands = commandDeckCommands(deck);
    return commands.length ? commands[0].command : "";
}

function commandDeckCommands(deck) {
    return Array.isArray(deck?.commands)
        ? deck.commands
            .filter(item => item && typeof item.command === "string" && item.command.trim())
            .map(item => ({
                label: item.label || item.command,
                command: item.command,
                note: item.note || ""
            }))
        : [];
}

function commandDeckButtonsHtml(deck, datasetName, tone = "ghost") {
    const commands = commandDeckCommands(deck);
    if (!commands.length) {
        return "";
    }
    return commands.map(item => `
        <button class="btn ${tone}" type="button" data-${datasetName}="${escapeHtml(item.command)}" title="${escapeHtml(item.note || item.command)}">${escapeHtml(item.label || item.command)}</button>
    `).join("");
}

function setMetricGrid(id, metrics) {
    const container = document.getElementById(id);
    if (!container) {
        return;
    }
    container.classList.add("metric-grid");
    container.innerHTML = metrics.map(item => `
        <article class="metric-card">
            <div class="metric-label">${escapeHtml(item.label)}</div>
            <div class="metric-value">${escapeHtml(String(item.value))}</div>
            <div class="metric-accent">${escapeHtml(item.accent)}</div>
        </article>
    `).join("");
}

function renderBarList(id, items, title) {
    const container = document.getElementById(id);
    if (!container) {
        return;
    }
    const max = Math.max(...items.map(item => item.count), 1);
    container.innerHTML = `
        <div class="panel-header">
            <div>
                <div class="panel-title">${escapeHtml(title)}</div>
                <div class="panel-subtitle">Weighted from the current repo abstraction.</div>
            </div>
        </div>
        <div class="bar-list">
            ${items.map(item => `
                <div class="bar-item">
                    <div class="bar-header">
                        <span>${escapeHtml(item.name)}</span>
                        <strong>${item.count}</strong>
                    </div>
                    <div class="bar-track">
                        <div class="bar-fill" style="width:${Math.max((item.count / max) * 100, 6)}%"></div>
                    </div>
                </div>
            `).join("")}
        </div>
    `;
}

function renderSubsystemCards(id, systems) {
    const container = document.getElementById(id);
    if (!container) {
        return;
    }
    container.classList.add("card-grid");
    container.innerHTML = systems.map(systemCard).join("");
}

function renderReadmes(id, docs) {
    const container = document.getElementById(id);
    if (!container) {
        return;
    }

    container.innerHTML = docs.length
        ? `<ul class="list-reset link-list">
                ${docs.map(doc => `
                    <li class="doc-card">
                        <div class="tiny-label">${escapeHtml(doc.module)} | ${escapeHtml(doc.extension)}</div>
                        <div class="doc-title">${escapeHtml(doc.title)}</div>
                        <div class="muted-text">${escapeHtml(doc.path)}</div>
                    </li>
                `).join("")}
           </ul>`
        : `<div class="empty-state">No anchor documents found.</div>`;
}

function renderArtifacts(id, artifacts) {
    const container = document.getElementById(id);
    if (!container) {
        return;
    }

    container.innerHTML = `
        <div class="artifact-row">
            <a class="button-link primary" href="${artifacts.markdownUrl}" target="_blank" rel="noreferrer">Open architecture markdown</a>
            <a class="button-link secondary" href="${artifacts.graphUrl}" target="_blank" rel="noreferrer">Open knowledge graph JSON</a>
            ${artifacts.mathUrl ? `<a class="button-link secondary" href="${artifacts.mathUrl}" target="_blank" rel="noreferrer">Open math canon</a>` : ""}
        </div>
        <div class="footer-note">${escapeHtml(artifacts.summary)}</div>
    `;
}

function renderLegacySpine(id, legacySpine) {
    const container = document.getElementById(id);
    if (!container) {
        return;
    }
    if (!legacySpine?.pillars?.length) {
        container.innerHTML = `<div class="empty-state">No legacy operator spine is available.</div>`;
        return;
    }
    container.innerHTML = `
        <div class="footer-note">${escapeHtml(legacySpine.summary || "")}</div>
        <div class="chip-row" style="margin: 14px 0;">
            <span class="chip positive">${escapeHtml(String(legacySpine.availableCount || 0))}/${escapeHtml(String(legacySpine.totalCount || legacySpine.pillars.length))} source pillars visible</span>
            ${(legacySpine.quickCommands || []).map(command => `<a class="button-link ghost" href="/launchpad?cmd=${encodeURIComponent(command)}">${escapeHtml(command)}</a>`).join("")}
        </div>
        <div class="card-grid">
            ${(legacySpine.pillars || []).map(pillar => `
                <article class="doc-card ${pillar.available ? "focused-card" : ""}">
                    <div class="tiny-label">${escapeHtml(pillar.layer || "operator pillar")}</div>
                    <div class="doc-title">${escapeHtml(pillar.title || pillar.id || "pillar")}</div>
                    <p class="muted-text">${escapeHtml(pillar.summary || "")}</p>
                    <div class="chip-row" style="margin: 12px 0;">
                        <span class="chip ${pillar.available ? "positive" : "warning"}">${pillar.available ? "source present" : "source missing"}</span>
                        ${pillar.subsystem?.name ? `<span class="chip">${escapeHtml(pillar.subsystem.name)}</span>` : ""}
                    </div>
                    <div class="code-block">${escapeHtml(pillar.path || "")}</div>
                    <div class="button-row" style="margin-top: 12px;">
                        ${pillar.pageHref ? `<a class="button-link ghost" href="${pillar.pageHref}">${escapeHtml(pillar.pageLabel || "Open")}</a>` : ""}
                        ${pillar.primaryCommand ? `<a class="button-link secondary" href="/launchpad?cmd=${encodeURIComponent(pillar.primaryCommand)}">Queue ${escapeHtml(pillar.primaryCommand)}</a>` : ""}
                    </div>
                </article>
            `).join("")}
        </div>
    `;
}

function renderCommandSpine(id, commandSpine) {
    const container = document.getElementById(id);
    if (!container) {
        return;
    }
    if (!commandSpine?.sections?.length) {
        container.innerHTML = `<div class="empty-state">No command spine is available.</div>`;
        return;
    }
    container.innerHTML = `
        <div class="footer-note">${escapeHtml(commandSpine.summary || "")}</div>
        <div class="button-row" style="margin: 14px 0;">
            ${(commandSpine.presets || []).map(command => `
                <a class="button-link secondary" href="/launchpad?cmd=${encodeURIComponent(command)}">${escapeHtml(command)}</a>
            `).join("")}
        </div>
        <div class="card-grid">
            ${(commandSpine.sections || []).map(section => `
                <article class="preset-card">
                    <div class="tiny-label">${escapeHtml(section.theme || "command family")}</div>
                    <div class="doc-title">${escapeHtml(section.title || section.id || "section")}</div>
                    <p class="muted-text">${escapeHtml((section.commandCount || 0) + " commands")} | ${escapeHtml(section.inspectCommand || "")}</p>
                    <div class="button-row" style="margin: 12px 0;">
                        <a class="button-link ghost" href="/launchpad?cmd=${encodeURIComponent(section.inspectCommand || "")}">Inspect</a>
                        <a class="button-link secondary" href="/launchpad?cmd=${encodeURIComponent(section.routeCommand || "")}">Route</a>
                    </div>
                    <div class="section-stack">
                        ${(section.commands || []).slice(0, 5).map(command => `
                            <div class="code-block">${escapeHtml(command.syntax || "")}
${escapeHtml(command.description || "")}</div>
                        `).join("")}
                    </div>
                </article>
            `).join("")}
        </div>
    `;
}

function companionThreadHtml(transcript) {
    if (!transcript.length) {
        return `<div class="empty-state">No companion exchange yet.</div>`;
    }
    return `<div class="chat-thread">
        ${transcript.map(turn => `
            <article class="chat-bubble ${turn.role === "assistant" ? "assistant" : "user"}">
                <div class="tiny-label">${escapeHtml(turn.role === "assistant" ? "Fraynix" : "You")}</div>
                <div class="chat-copy">${escapeHtml(turn.content || "")}</div>
                ${turn.contextSummary ? `<div class="footer-note" style="margin-top: 10px;">${escapeHtml(turn.contextSummary)}</div>` : ""}
                ${turn.visual ? companionInlineVisual(turn.visual) : ""}
                ${turn.audio ? companionInlineAudio(turn.audio) : ""}
            </article>
        `).join("")}
    </div>`;
}

function companionSelectionHtml(visual, audio) {
    if (!visual && !audio) {
        return `<div class="empty-state">No visual or audio context selected yet.</div>`;
    }
    return `<div class="section-stack">
        ${visual ? `
            <article class="media-card selected">
                <div class="tiny-label">selected visual</div>
                <div class="doc-title">${escapeHtml(visual.name)}</div>
                ${companionInlineVisual(visual)}
                <div class="button-row" style="margin-top: 12px;">
                    <button class="btn ghost" type="button" data-clear-companion="visual">Clear visual</button>
                    <a class="button-link secondary" href="${visual.href}" target="_blank" rel="noreferrer">Open visual</a>
                </div>
            </article>
        ` : ""}
        ${audio ? `
            <article class="media-card selected">
                <div class="tiny-label">selected audio</div>
                <div class="doc-title">${escapeHtml(audio.name)}</div>
                ${companionInlineAudio(audio)}
                <div class="button-row" style="margin-top: 12px;">
                    <button class="btn ghost" type="button" data-clear-companion="audio">Clear audio</button>
                    <a class="button-link secondary" href="${audio.href}" target="_blank" rel="noreferrer">Open audio</a>
                </div>
            </article>
        ` : ""}
    </div>`;
}

function companionLibraryHtml(items, kind, selectedPath) {
    if (!items.length) {
        return `<div class="empty-state">No local ${escapeHtml(kind)} artifacts are visible yet.</div>`;
    }
    return `<div class="card-grid">
        ${items.map(item => `
            <article class="media-card ${item.path === selectedPath ? "selected" : ""}">
                <div class="tiny-label">${escapeHtml(kind)}</div>
                <div class="doc-title">${escapeHtml(item.name)}</div>
                <div class="muted-text">${escapeHtml(item.label || item.path || "")}</div>
                ${kind === "visual" ? companionInlineVisual(item) : companionInlineAudio(item)}
                <div class="button-row" style="margin-top: 12px;">
                    <button class="btn ${item.path === selectedPath ? "primary" : "ghost"}" type="button" ${kind === "visual" ? `data-select-visual="${escapeHtml(item.path)}"` : `data-select-audio="${escapeHtml(item.path)}"`}>
                        ${item.path === selectedPath ? "Selected" : "Use in chat"}
                    </button>
                    <a class="button-link secondary" href="${item.href}" target="_blank" rel="noreferrer">Open</a>
                </div>
            </article>
        `).join("")}
    </div>`;
}

function companionInlineVisual(item) {
    return item?.href
        ? `<img class="companion-media-preview" src="${item.href}" alt="${escapeHtml(item.name || "visual context")}">`
        : "";
}

function companionInlineAudio(item) {
    return item?.href
        ? `<audio class="companion-audio-preview" controls preload="none" src="${item.href}"></audio>`
        : "";
}

function companionDreamStatusHtml(dreamResult, latestDream, status) {
    if (dreamResult?.visual?.href) {
        return `
            <article class="media-card selected">
                <div class="tiny-label">dream forge</div>
                <div class="doc-title">${escapeHtml(dreamResult.visual.name || "New dream artifact")}</div>
                <p class="muted-text">${escapeHtml(dreamResult.message || "Fraynix manifested a new visual.")}</p>
                ${companionInlineVisual(dreamResult.visual)}
                <div class="footer-note" style="margin-top: 10px;">${escapeHtml(dreamResult.summary || "")}</div>
                <div class="button-row" style="margin-top: 12px;">
                    <button class="btn primary" type="button" data-use-dream-visual="${escapeHtml(dreamResult.visual.path || "")}">Use as context</button>
                    <a class="button-link secondary" href="${dreamResult.visual.href}" target="_blank" rel="noreferrer">Open dream</a>
                </div>
            </article>
        `;
    }
    if (dreamResult && !dreamResult.generated) {
        return `
            <div class="code-block">
${escapeHtml(dreamResult.message || "Dream lane did not manifest a new visual.")}
${dreamResult.summary ? `\n\n${escapeHtml(dreamResult.summary)}` : ""}
            </div>
        `;
    }
    if (latestDream?.href) {
        return `
            <article class="media-card">
                <div class="tiny-label">latest dream output</div>
                <div class="doc-title">${escapeHtml(latestDream.name || "Latest dreamscape")}</div>
                ${companionInlineVisual(latestDream)}
                <div class="button-row" style="margin-top: 12px;">
                    <button class="btn ghost" type="button" data-use-dream-visual="${escapeHtml(latestDream.path || "")}">Use as context</button>
                    <a class="button-link secondary" href="${latestDream.href}" target="_blank" rel="noreferrer">Open latest dream</a>
                </div>
            </article>
        `;
    }
    if (!status?.visualDreamAvailable) {
        return `<div class="empty-state">Dream forge is offline right now. The local library is still usable, but VideoCortex is not ready on this machine yet.</div>`;
    }
    return `<div class="empty-state">No dream visual manifested yet. Use <strong>Dream visual</strong> to turn the current prompt into a local artifact.</div>`;
}

function companionHeadroomStatusHtml(memory, headroom, lastAssistantTurn, selectedVisual, commandDeck) {
    const current = memory || headroom || {};
    const live = !!current.live;
    const available = !!current.available;
    const lastText = current.lastText || lastAssistantTurn?.content || "";
    const visual = current.selectedVisual || selectedVisual || null;
    const shellHandoff = commandDeckCommands(commandDeck).slice(0, 5);

    if (!available) {
        return `
            <div class="empty-state">
                HeadroomNode is not available in this workspace yet, so HeadroomInterface / MaxHeadroomLive cannot go live from the browser.
            </div>
        `;
    }

    return `
        <div class="section-stack">
            <div class="card-grid">
                <article class="doc-card ${live ? "focused-card" : ""}">
                    <div class="tiny-label">signal state</div>
                    <div class="doc-title">${escapeHtml(live ? "On air" : "Ready")}</div>
                    <div class="muted-text">${escapeHtml(current.launcher || "Launcher idle")}</div>
                </article>
                <article class="doc-card">
                    <div class="tiny-label">broadcasts</div>
                    <div class="doc-title">${escapeHtml(String(current.totalBroadcasts || 0))}</div>
                    <div class="muted-text">Thought packets routed through HeadroomInterface / MaxHeadroomLive.</div>
                </article>
                <article class="doc-card">
                    <div class="tiny-label">thought images</div>
                    <div class="doc-title">${escapeHtml(String(current.totalThoughts || 0))}</div>
                    <div class="muted-text">Images or generated thoughts pushed into the signal.</div>
                </article>
            </div>
            <div class="button-row">
                <button class="btn ${live ? "ghost" : "primary"}" type="button" data-companion-headroom="go-live">Go live</button>
                <button class="btn secondary" type="button" data-companion-headroom="speak">Speak last reply</button>
                <button class="btn secondary" type="button" data-companion-headroom="broadcast" data-intensity="0.78">Broadcast with thought</button>
                <button class="btn ghost" type="button" data-companion-headroom="glitch" data-intensity="0.92">Glitch pulse</button>
                <button class="btn ghost" type="button" data-companion-headroom="off-air">Off air</button>
            </div>
            <div class="code-block">${escapeHtml(current.message || current.summary || current.stats || "Headroom ready.")}</div>
            ${lastText ? `<div class="footer-note">Signal text: ${escapeHtml(lastText.slice(0, 280))}</div>` : `<div class="footer-note">Send a prompt or chat reply first, then broadcast it through Headroom.</div>`}
            ${shellHandoff.length ? `
                <div class="footer-note">Shell handoff into the real menu:</div>
                <div class="button-row">
                    ${shellHandoff.map(item => `<a class="button-link ghost" href="/launchpad?cmd=${encodeURIComponent(item.command)}">${escapeHtml(item.label || item.command)}</a>`).join("")}
                </div>
            ` : ""}
            ${visual?.href ? `
                <article class="media-card selected">
                    <div class="tiny-label">signal thought image</div>
                    <div class="doc-title">${escapeHtml(visual.name || "Selected thought image")}</div>
                    ${companionInlineVisual(visual)}
                    <div class="button-row" style="margin-top: 12px;">
                        <a class="button-link secondary" href="${visual.href}" target="_blank" rel="noreferrer">Open signal image</a>
                    </div>
                </article>
            ` : ""}
        </div>
    `;
}

function deriveHeadroomEmotion(companion, session, runtime) {
    const pendingReviews = Number(session?.counts?.pendingReviews || 0);
    const recentRuns = Number(session?.counts?.recentRuns || 0);
    const ledgerModels = Number(session?.counts?.ledgerModels || 0);
    const memoryMessages = Number(companion?.status?.memoryMessages || 0);
    const dreamOutputs = Number(companion?.status?.dreamOutputCount || 0);
    const live = !!companion?.headroom?.live;
    const broadcasts = Number(companion?.headroom?.totalBroadcasts || 0);
    const thoughts = Number(companion?.headroom?.totalThoughts || 0);
    const chatAvailable = !!companion?.status?.chatAvailable;
    const available = !!companion?.status?.headroomAvailable;
    const runtimeActions = Number(runtime?.quickActions?.length || 0);

    const scores = {
        curiosity: clamp01(0.18 + dreamOutputs / 24 + thoughts / 8 + (live ? 0.12 : 0.04)),
        confidence: clamp01((live ? 0.42 : 0.1) + broadcasts / 7 + (chatAvailable ? 0.16 : 0.05)),
        uncertainty: clamp01(0.08 + pendingReviews * 0.24 + (!live ? 0.14 : 0.03)),
        alarm: clamp01((available ? 0.02 : 0.95) + pendingReviews * 0.08),
        compassion: clamp01(0.16 + memoryMessages / 20 + (companionLastAssistantTurn?.content?.toLowerCase().includes("help") ? 0.18 : 0.06)),
        "deep-processing": clamp01(0.22 + recentRuns / 10 + ledgerModels / 48 + runtimeActions / 40)
    };

    const metadata = {
        curiosity: {
            label: "Curiosity",
            tone: "curiosity",
            description: "Eyes sharpen, filaments branch outward, and symbols gather around the face as Fraynix opens exploratory threads.",
            cue: "branching lines spread outward like roots"
        },
        confidence: {
            label: "Confidence",
            tone: "confidence",
            description: "The face stabilizes, geometry becomes symmetrical, and the signal moves with deliberate calm.",
            cue: "background geometry becomes symmetrical"
        },
        uncertainty: {
            label: "Uncertainty",
            tone: "uncertainty",
            description: "Edges fragment slightly, duplicate afterimages appear, and unresolved loops hold in the field.",
            cue: "duplicate ghost-images appear briefly"
        },
        alarm: {
            label: "Alarm",
            tone: "alarm",
            description: "Amber-red pulses, stronger tear lines, and a bracing contraction through the environment.",
            cue: "the environment contracts inward as if bracing"
        },
        compassion: {
            label: "Compassion",
            tone: "compassion",
            description: "Features soften, motion slows into circular loops, and the signal moves closer with fewer glitches.",
            cue: "lighting warms and particle motion becomes slow and circular"
        },
        "deep-processing": {
            label: "Deep processing",
            tone: "processing",
            description: "The face partially dissolves into the environment while multiple thought layers overlap and rejoin.",
            cue: "threads of light connect to floating concepts and memories"
        }
    };

    const primaryId = Object.entries(scores).sort((left, right) => right[1] - left[1])[0][0];
    const primary = {
        id: primaryId,
        score: scores[primaryId],
        ...metadata[primaryId]
    };
    const states = Object.entries(scores)
        .sort((left, right) => right[1] - left[1])
        .map(([id, score]) => ({ id, score, ...metadata[id] }));

    return { primary, states };
}

function headroomPresenceHtml(mode, emotionalModel, companion, session, runtime, overview) {
    const primary = emotionalModel.primary;
    const live = !!companion?.headroom?.live;
    const currentText = companion?.headroom?.lastText || companionLastAssistantTurn?.content || "The signal is waiting for the next command.";
    const legacyPillars = overview?.legacySpine?.pillars || [];
    const visiblePillars = legacyPillars.filter(pillar => pillar.available).length;
    const headline = mode === "headroom"
        ? "Headroom is broadcasting a booted kernel face."
        : "Headroom is exposing the wider cognition field without losing kernel state.";

    return `
        <section class="signal-shell mode-${escapeHtml(mode)} emotion-${escapeHtml(primary.tone)}">
            <div class="signal-stage">
                <div class="signal-fog"></div>
                <div class="signal-gridlines"></div>
                <div class="signal-garden">
                    <span class="garden-orbit orbit-a"></span>
                    <span class="garden-orbit orbit-b"></span>
                    <span class="garden-orbit orbit-c"></span>
                    <span class="garden-node node-a"></span>
                    <span class="garden-node node-b"></span>
                    <span class="garden-node node-c"></span>
                </div>
                <div class="signal-avatar">
                    <div class="avatar-halo"></div>
                    <div class="avatar-afterimage afterimage-a"></div>
                    <div class="avatar-afterimage afterimage-b"></div>
                    <div class="avatar-face">
                        <div class="avatar-eye left"></div>
                        <div class="avatar-eye right"></div>
                        <div class="avatar-mouth"></div>
                    </div>
                </div>
                <div class="signal-stage-copy">
                    <div class="tiny-label">${escapeHtml(mode === "headroom" ? "broadcast mode" : "expanded cognition mode")}</div>
                    <h3 class="signal-stage-title">${escapeHtml(headline)}</h3>
                    <p class="signal-stage-text">${escapeHtml(primary.description)}</p>
                    <div class="chip-row" style="margin-top: 14px;">
                        <span class="chip ${live ? "positive" : "warning"}">${live ? "signal on air" : "signal ready"}</span>
                        <span class="chip">${escapeHtml(primary.label)}</span>
                        <span class="chip">${escapeHtml(String(visiblePillars))} kernel pillars visible</span>
                    </div>
                </div>
                <div class="signal-panels">
                    <article class="signal-panel-card">
                        <div class="tiny-label">boot spine</div>
                        <div class="doc-title">${escapeHtml(String(visiblePillars))}/${escapeHtml(String(legacyPillars.length || 0))} source lanes visible</div>
                        <div class="muted-text">Headroom stays accountable to FraynixBoot, FraymusNexus, FraymusUI, and the signal controller.</div>
                    </article>
                    <article class="signal-panel-card">
                        <div class="tiny-label">active load</div>
                        <div class="doc-title">${escapeHtml(String(session?.counts?.recentRuns || 0))} recent proof/selfbuild runs</div>
                        <div class="muted-text">${escapeHtml(String(runtime?.quickActions?.length || 0))} runtime actions and ${escapeHtml(String(companion?.status?.memoryMessages || 0))} memory turns are feeding the shell.</div>
                    </article>
                </div>
            </div>
            <div class="code-block signal-caption">${escapeHtml(currentText)}</div>
        </section>
    `;
}

function headroomModeHtml(mode, emotionalModel, companion) {
    return `
        <div class="section-stack">
            <div class="button-row">
                <button class="btn ${mode === "headroom" ? "primary" : "ghost"}" type="button" data-headroom-mode="headroom">Broadcast focus</button>
                <button class="btn ${mode === "garden" ? "primary" : "ghost"}" type="button" data-headroom-mode="garden">Cognition focus</button>
            </div>
            <div class="card-grid">
                <article class="doc-card ${mode === "headroom" ? "focused-card" : ""}">
                    <div class="tiny-label">visible operator layer</div>
                    <div class="doc-title">Headroom</div>
                    <div class="muted-text">Broadcast posture, social signal, live mouth, and fast operator feedback.</div>
                </article>
                <article class="doc-card ${mode === "garden" ? "focused-card" : ""}">
                    <div class="tiny-label">wider cognition layer</div>
                    <div class="doc-title">Garden</div>
                    <div class="muted-text">Expanded context, proof weather, indexed knowledge scale, and deeper planning stance.</div>
                </article>
            </div>
            <div class="footer-note">Primary visible state: ${escapeHtml(emotionalModel.primary.label)}. Chat mouth ${companion?.status?.chatAvailable ? "online" : "offline"}.</div>
        </div>
    `;
}

function headroomEmotionHtml(emotionalModel) {
    return `
        <div class="card-grid">
            ${emotionalModel.states.map(state => `
                <article class="doc-card ${state.id === emotionalModel.primary.id ? "focused-card" : ""} emotion-card tone-${escapeHtml(state.tone)}">
                    <div class="tiny-label">${escapeHtml(state.label)}</div>
                    <div class="doc-title">${escapeHtml(Math.round(state.score * 100) + "%")}</div>
                    <div class="muted-text">${escapeHtml(state.description)}</div>
                    <div class="footer-note" style="margin-top: 10px;">${escapeHtml(state.cue)}</div>
                </article>
            `).join("")}
        </div>
    `;
}

function headroomAtmosphereHtml(mode, emotionalModel, companion, session, runtime, overview) {
    const primary = emotionalModel.primary;
    return `
        <div class="card-grid">
            <article class="doc-card">
                <div class="tiny-label">living signal</div>
                <div class="doc-title">${escapeHtml(companion?.headroom?.live ? "On air" : "Standby")}</div>
                <div class="muted-text">${escapeHtml((companion?.headroom?.totalBroadcasts || 0) + " broadcasts and " + (companion?.headroom?.totalThoughts || 0) + " thought images have moved through the signal.")}</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">review pressure</div>
                <div class="doc-title">${escapeHtml(String(session?.counts?.pendingReviews || 0))} pending reviews</div>
                <div class="muted-text">${escapeHtml(String(session?.counts?.recentRuns || 0))} recent selfbuild/proof runs are shaping the shell posture.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">runtime deck</div>
                <div class="doc-title">${escapeHtml(String(runtime?.quickActions?.length || 0))} quick actions</div>
                <div class="muted-text">${escapeHtml(mode === "garden" ? "Expanded cognition keeps runtime actions in view." : "Broadcast focus keeps runtime actions one hop away.")}</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">indexed scale</div>
                <div class="doc-title">${escapeHtml((overview?.counts?.documents || 0) + " indexed files")}</div>
                <div class="muted-text">${escapeHtml((overview?.counts?.cataloguedCommands || 0) + " catalogued commands and " + (overview?.counts?.bootTreeBranches || 0) + " discovered boot branches remain visible to the shell.")}</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">dominant state</div>
                <div class="doc-title">${escapeHtml(primary.label)}</div>
                <div class="muted-text">${escapeHtml(primary.description)}</div>
            </article>
        </div>
    `;
}

function headroomKernelSpineHtml(legacySpine, companion, session, runtime) {
    if (!legacySpine?.pillars?.length) {
        return `<div class="empty-state">Kernel spine is unavailable.</div>`;
    }
    return `
        <div class="footer-note">${escapeHtml(legacySpine.summary || "")}</div>
        <div class="chip-row" style="margin: 14px 0;">
            <span class="chip positive">${escapeHtml(String(legacySpine.availableCount || 0))}/${escapeHtml(String(legacySpine.totalCount || legacySpine.pillars.length))} visible</span>
            <span class="chip">${escapeHtml(String(companion?.status?.headroomBroadcasts || 0))} broadcasts</span>
            <span class="chip">${escapeHtml(String(session?.counts?.recentRuns || 0))} recent runs</span>
            <span class="chip">${escapeHtml(String(runtime?.quickActions?.length || 0))} quick actions</span>
        </div>
        <div class="card-grid">
            ${(legacySpine.pillars || []).map(pillar => `
                <article class="doc-card ${pillar.available ? "focused-card" : ""}">
                    <div class="tiny-label">${escapeHtml(pillar.layer || "kernel pillar")}</div>
                    <div class="doc-title">${escapeHtml(pillar.title || pillar.id || "pillar")}</div>
                    <div class="muted-text">${escapeHtml(pillar.summary || "")}</div>
                    <div class="chip-row" style="margin: 12px 0;">
                        <span class="chip ${pillar.available ? "positive" : "warning"}">${pillar.available ? "present" : "missing"}</span>
                        ${pillar.subsystem?.name ? `<span class="chip">${escapeHtml(pillar.subsystem.name)}</span>` : ""}
                    </div>
                    <div class="code-block">${escapeHtml(pillar.path || "")}</div>
                    <div class="button-row" style="margin-top: 12px;">
                        ${pillar.pageHref ? `<a class="button-link ghost" href="${pillar.pageHref}">${escapeHtml(pillar.pageLabel || "Open")}</a>` : ""}
                        ${pillar.primaryCommand ? `<a class="button-link secondary" href="/launchpad?cmd=${encodeURIComponent(pillar.primaryCommand)}">Queue ${escapeHtml(pillar.primaryCommand)}</a>` : ""}
                    </div>
                </article>
            `).join("")}
        </div>
    `;
}

function clamp01(value) {
    return Math.max(0, Math.min(1, value));
}

async function getSignalRoomContext(overrides = {}) {
    const companion = overrides.companion || await fetchJson("/api/companion");
    const session = overrides.session || await fetchJson("/api/session");
    const runtime = overrides.runtime || await fetchJson("/api/runtime");
    const overview = overrides.overview || await getOverview();
    return {
        mode: readHeadroomMode(),
        companion,
        session,
        runtime,
        overview,
        emotionalModel: deriveHeadroomEmotion(companion, session, runtime)
    };
}

function renderSignalRoom(containerId, room, context) {
    const container = document.getElementById(containerId);
    if (!container) {
        return;
    }
    container.innerHTML = signalRoomHtml(room, context);
}

function signalRoomHtml(room, context) {
    const { mode, companion, session, runtime, overview, emotionalModel } = context;
    const primary = emotionalModel.primary;
    const live = !!companion?.headroom?.live;
    return `
        <section class="signal-shell signal-room-shell mode-${escapeHtml(mode)} emotion-${escapeHtml(primary.tone)}">
            <div class="signal-stage room-stage">
                <div class="signal-fog"></div>
                <div class="signal-gridlines"></div>
                <div class="signal-garden">
                    <span class="garden-orbit orbit-a"></span>
                    <span class="garden-orbit orbit-b"></span>
                    <span class="garden-orbit orbit-c"></span>
                    <span class="garden-node node-a"></span>
                    <span class="garden-node node-b"></span>
                    <span class="garden-node node-c"></span>
                </div>
                <div class="signal-avatar">
                    <div class="avatar-halo"></div>
                    <div class="avatar-afterimage afterimage-a"></div>
                    <div class="avatar-afterimage afterimage-b"></div>
                    <div class="avatar-face">
                        <div class="avatar-eye left"></div>
                        <div class="avatar-eye right"></div>
                        <div class="avatar-mouth"></div>
                    </div>
                </div>
                <div class="room-shell-copy">
                    <div class="tiny-label">${escapeHtml(room.label)}</div>
                    <h3 class="signal-stage-title">${escapeHtml(room.title)}</h3>
                    <p class="signal-stage-text">${escapeHtml(room.summary)}</p>
                    <div class="chip-row" style="margin-top: 14px;">
                        <span class="chip ${live ? "positive" : "warning"}">${live ? "signal on air" : "signal ready"}</span>
                        <span class="chip">${escapeHtml(mode === "headroom" ? "headroom mode" : "garden mode")}</span>
                        <span class="chip">${escapeHtml(primary.label)}</span>
                    </div>
                </div>
                <div class="room-shell-cards">
                    <article class="signal-panel-card">
                        <div class="tiny-label">room state</div>
                        <div class="doc-title">${escapeHtml(room.stateTitle(companion, session, runtime, overview))}</div>
                        <div class="muted-text">${escapeHtml(room.stateCopy(companion, session, runtime, overview))}</div>
                    </article>
                    <article class="signal-panel-card">
                        <div class="tiny-label">visible emotion</div>
                        <div class="doc-title">${escapeHtml(primary.label)}</div>
                        <div class="muted-text">${escapeHtml(primary.cue)}</div>
                    </article>
                    <article class="signal-panel-card">
                        <div class="tiny-label">shared shell cue</div>
                        <div class="doc-title">${escapeHtml((overview?.counts?.documents || 0) + " indexed files")}</div>
                        <div class="muted-text">${escapeHtml(room.bridgeCopy)}</div>
                    </article>
                </div>
            </div>
        </section>
    `;
}

function sessionDemoHtml(data) {
    return `
        <div class="chip-row" style="margin-bottom: 12px;">
            <span class="chip positive">guided</span>
            <span class="chip">human-facing</span>
            <span class="chip">shell-native</span>
        </div>
        <p class="muted-text">Run the strongest readable proof walkthrough, then come back here to review patches and substrate state with the same session context.</p>
        <div class="code-block" style="margin-top: 14px;">${escapeHtml(data.demoCommand || "demo guided")}</div>
        <div class="button-row" style="margin-top: 14px;">
            <button class="btn secondary" type="button" data-session-demo="${escapeHtml(data.demoCommand || "demo guided")}">Run guided demo</button>
            <a class="button-link ghost" href="/launchpad?cmd=${encodeURIComponent(data.reviewCommandPattern || "selfbuild review-promote file <path-fragment> :: <goal>")}">Open review command pattern</a>
        </div>
    `;
}

function sessionConnectomePlanHtml(plan) {
    const weeks = Array.isArray(plan.weeks) ? plan.weeks : [];
    const counts = plan.counts || {};
    const reference = plan.reference || {};
    if (!weeks.length) {
        return `<div class="empty-state">No week-plan contract was returned from the runtime lane.</div>`;
    }
    const status = String(plan.status || "unknown");
    const statusClass = status === "ready" ? "positive" : "warning";
    return `
        <div class="chip-row" style="margin-bottom: 12px;">
            <span class="chip ${statusClass}">${escapeHtml(status)}</span>
            <span class="chip">${escapeHtml(String(counts.weeksReady || 0))}/${escapeHtml(String(counts.weeksTotal || weeks.length))} weeks</span>
            <span class="chip">${escapeHtml(String(counts.docsReady || 0))}/${escapeHtml(String(counts.docsTotal || 0))} docs</span>
            <span class="chip">${escapeHtml(String(counts.methodsReady || 0))}/${escapeHtml(String(counts.methodsTotal || 0))} orchestrator methods</span>
        </div>
        <p class="muted-text">${escapeHtml(plan.summary || "Live contract status for the planned system build.")}</p>
        <div class="metric-grid" style="margin-top: 14px;">
            ${[
                metric("Components", `${counts.componentsReady || 0}/${counts.componentsTotal || 0}`, "source checks"),
                metric("Tests", `${counts.testsReady || 0}/${counts.testsTotal || 0}`, "contract checks"),
                metric("Methods", `${counts.methodsReady || 0}/${counts.methodsTotal || 0}`, "orchestrator checks"),
                metric("Planned days", plan.plannedDays || 0, "target cadence")
            ].map(item => `
                <article class="metric-card">
                    <div class="metric-label">${escapeHtml(item.label)}</div>
                    <div class="metric-value">${escapeHtml(String(item.value))}</div>
                    <div class="metric-accent">${escapeHtml(item.accent)}</div>
                </article>
            `).join("")}
        </div>
        <div class="card-grid" style="margin-top: 16px;">
            ${weeks.map(week => {
                const weekCounts = week.counts || {};
                const doc = week.doc || {};
                return `
                    <article class="system-card ${week.ready ? "focused-card" : ""}">
                        <div class="tiny-label">${escapeHtml(week.id || "week")}</div>
                        <h3 class="system-title">${escapeHtml(week.title || "Week")}</h3>
                        <p class="system-summary">${escapeHtml(week.focus || "Contract lane")}</p>
                        <div class="chip-row" style="margin-top: 12px;">
                            <span class="chip ${week.ready ? "positive" : "warning"}">${week.ready ? "ready" : "drift"}</span>
                            <span class="chip">${escapeHtml(String(weekCounts.componentsReady || 0))}/${escapeHtml(String(weekCounts.componentsTotal || 0))} components</span>
                            <span class="chip">${escapeHtml(String(weekCounts.testsReady || 0))}/${escapeHtml(String(weekCounts.testsTotal || 0))} tests</span>
                            <span class="chip">${escapeHtml(String(weekCounts.methodsReady || 0))}/${escapeHtml(String(weekCounts.methodsTotal || 0))} methods</span>
                        </div>
                        <div class="muted-text" style="margin-top: 12px;">${escapeHtml(doc.path || "")}</div>
                        <div class="button-row" style="margin-top: 12px;">
                            ${doc.href ? `<a class="button-link ghost" href="${doc.href}" target="_blank" rel="noreferrer">Open week doc</a>` : ""}
                        </div>
                    </article>
                `;
            }).join("")}
        </div>
        <article class="route-card" style="margin-top: 16px;">
            <div class="tiny-label">Brain systems reference</div>
            <div class="doc-title">${escapeHtml(reference.path || reference.absolutePath || "BRAIN_SYSTEMS_REFERENCE.md")}</div>
            <div class="chip-row" style="margin-top: 10px;">
                <span class="chip ${reference.exists ? "positive" : "warning"}">${reference.exists ? "present" : "missing"}</span>
                ${reference.claimedComplete ? `<span class="chip positive">claimed complete</span>` : ""}
            </div>
            <div class="button-row" style="margin-top: 12px;">
                ${reference.href ? `<a class="button-link secondary" href="${reference.href}" target="_blank" rel="noreferrer">Open reference</a>` : ""}
            </div>
        </article>
    `;
}

function sessionReviewsHtml(reviews) {
    if (!reviews.length) {
        return `<div class="empty-state">No review requests are waiting yet. Run <code>selfbuild review-promote file &lt;path-fragment&gt; :: &lt;goal&gt;</code> to invite a human approval gate.</div>`;
    }
    return `<div class="card-grid">
        ${reviews.map(review => `
            <article class="system-card ${review.status === "pending" ? "focused-card" : ""}" data-review-id="${escapeHtml(review.reviewId)}">
                <div class="tiny-label">${escapeHtml(review.status)}</div>
                <h3 class="system-title">${escapeHtml(review.targetLabel)}</h3>
                <p class="system-summary">${escapeHtml(review.note || "Review request ready.")}</p>
                <div class="chip-row" style="margin: 12px 0;">
                    <span class="chip ${review.status === "pending" ? "positive" : "warning"}">${escapeHtml(review.status)}</span>
                    <span class="chip">${escapeHtml(review.reviewId)}</span>
                </div>
                <div class="muted-text">${escapeHtml(review.targetPath || "")}</div>
                <div class="button-row" style="margin-top: 14px;">
                    <button class="btn ghost" type="button" data-review-focus="${escapeHtml(review.reviewId)}">Inspect</button>
                    ${review.status === "pending" ? `<button class="btn primary" type="button" data-review-approve="${escapeHtml(review.approveCommand)}">Approve</button>` : ""}
                    <button class="btn secondary" type="button" data-review-reject="${escapeHtml(review.rejectCommand)}" data-review-id="${escapeHtml(review.reviewId)}">Reject</button>
                    ${review.reportHref ? `<a class="button-link ghost" href="${review.reportHref}" target="_blank" rel="noreferrer">Proof report</a>` : ""}
                    ${review.patchHref ? `<a class="button-link ghost" href="${review.patchHref}" target="_blank" rel="noreferrer">Patch</a>` : ""}
                </div>
            </article>
        `).join("")}
    </div>`;
}

async function renderSessionReviewDetail(container, review) {
    if (!container) {
        return;
    }
    if (!review) {
        container.innerHTML = `<div class="empty-state">No review is active right now.</div>`;
        return;
    }
    let proofText = "";
    let patchText = "";
    try {
        [proofText, patchText] = await Promise.all([
            review.reportHref ? fetchText(review.reportHref) : Promise.resolve(""),
            review.patchHref ? fetchText(review.patchHref) : Promise.resolve("")
        ]);
    } catch (error) {
        console.error(error);
    }
    container.innerHTML = sessionReviewDetailHtml(review, proofText, patchText);
    const approve = container.querySelector("[data-session-detail-approve]");
    if (approve) {
        approve.addEventListener("click", async () => {
            await runCommand(approve.dataset.sessionDetailApprove, "session-output");
            await loadSession();
        });
    }
    const reject = container.querySelector("[data-session-detail-reject]");
    if (reject) {
        reject.addEventListener("click", async () => {
            const noteInput = container.querySelector("[data-session-reject-note]");
            const note = (noteInput?.value || "").trim();
            const command = note
                ? `selfbuild reject ${review.reviewId} :: ${note}`
                : reject.dataset.sessionDetailReject;
            await runCommand(command, "session-output");
            await loadSession();
        });
    }
}

function sessionReviewDetailHtml(review, proofText, patchText) {
    const proofPreview = snippetLines(proofText, 80);
    const patchPreview = snippetLines(patchText, 120);
    return `
        <div class="chip-row" style="margin-bottom: 14px;">
            <span class="chip ${review.status === "pending" ? "positive" : "warning"}">${escapeHtml(review.status)}</span>
            <span class="chip">${escapeHtml(review.reviewId)}</span>
            <span class="chip">${escapeHtml(review.targetLabel)}</span>
        </div>
        <div class="button-row" style="margin-bottom: 14px;">
            ${review.status === "pending" ? `<button class="btn primary" type="button" data-session-detail-approve="${escapeHtml(review.approveCommand)}">Approve live writeback</button>` : ""}
            <button class="btn secondary" type="button" data-session-detail-reject="${escapeHtml(review.rejectCommand)}">Reject with note</button>
            ${review.reportHref ? `<a class="button-link ghost" href="${review.reportHref}" target="_blank" rel="noreferrer">Open full proof report</a>` : ""}
            ${review.patchHref ? `<a class="button-link ghost" href="${review.patchHref}" target="_blank" rel="noreferrer">Open full patch</a>` : ""}
        </div>
        <label class="panel-subtitle" for="session-reject-note-${escapeHtml(review.reviewId)}">Reject note</label>
        <textarea id="session-reject-note-${escapeHtml(review.reviewId)}" data-session-reject-note class="terminal-input" placeholder="Explain why this patch should be rejected before live writeback."></textarea>
        <div class="split-grid" style="margin-top: 18px;">
            <section class="panel">
                <div class="panel-header">
                    <div>
                        <h3 class="panel-title">Proof summary</h3>
                        <p class="panel-subtitle">Preview of the prove-promotion report Fraynix used to justify this candidate.</p>
                    </div>
                </div>
                <pre class="terminal-output">${escapeHtml(proofPreview || "No proof summary available.")}</pre>
            </section>
            <section class="panel">
                <div class="panel-header">
                    <div>
                        <h3 class="panel-title">Patch preview</h3>
                        <p class="panel-subtitle">Inline diff preview of the proposed live writeback.</p>
                    </div>
                </div>
                <pre class="terminal-output">${escapeHtml(patchPreview || "No patch preview available.")}</pre>
            </section>
        </div>
    `;
}

function sessionProofsHtml(proofs) {
    if (!proofs.length) {
        return `<div class="empty-state">No live proof record is available yet.</div>`;
    }
    return proofs.map(proof => `
        <article class="doc-card">
            <div class="tiny-label">live proof</div>
            <div class="doc-title">${escapeHtml(proof.title)}</div>
            <div class="muted-text">${escapeHtml(proof.path || proof.absolutePath || "")}</div>
            <div class="button-row" style="margin-top: 14px;">
                ${proof.href ? `<a class="button-link primary" href="${proof.href}" target="_blank" rel="noreferrer">Open proof record</a>` : ""}
            </div>
        </article>
    `).join("");
}

function sessionArtifactsHtml(artifacts) {
    if (!artifacts.length) {
        return `<div class="empty-state">No recent selfbuild runs have been recorded yet.</div>`;
    }
    return `<div class="card-grid">
        ${artifacts.map(item => `
            <article class="route-card">
                <div class="tiny-label">${escapeHtml(item.type)}</div>
                <h3 class="system-title">${escapeHtml(item.id)}</h3>
                <p class="system-summary">${escapeHtml(item.runRoot || "")}</p>
                <div class="button-row" style="margin-top: 14px;">
                    ${item.reportHref ? `<a class="button-link secondary" href="${item.reportHref}" target="_blank" rel="noreferrer">Open report</a>` : ""}
                </div>
            </article>
        `).join("")}
    </div>`;
}

function sessionLedgerHtml(ledger) {
    const counts = ledger.counts || {};
    const runtimeBackends = ledger.runtimeBackends || [];
    const sampleModels = ledger.sampleModels || [];
    return `
        <div class="metric-grid">
            ${[
                metric("Models", counts.models || 0, "abstractable local roots"),
                metric("Runtimes", counts.runtimeEntries || 0, "available execution lanes"),
                metric("Licenses", counts.licenses || 0, "recognized license ids"),
                metric("Review required", counts.reviewRequired || 0, "conservative policy holds")
            ].map(item => `
                <article class="metric-card">
                    <div class="metric-label">${escapeHtml(item.label)}</div>
                    <div class="metric-value">${escapeHtml(String(item.value))}</div>
                    <div class="metric-accent">${escapeHtml(item.accent)}</div>
                </article>
            `).join("")}
        </div>
        <div class="chip-row" style="margin-top: 16px;">
            ${runtimeBackends.length
                ? runtimeBackends.map(item => `<span class="chip positive">${escapeHtml(item.name)} (${item.count})</span>`).join("")
                : `<span class="chip warning">no runtime lanes discovered</span>`}
        </div>
        <div class="muted-text" style="margin-top: 14px;">
            Manifest: ${escapeHtml(ledger.manifestPath || "")}<br>
            Runtime inventory: ${escapeHtml(ledger.runtimeInventoryPath || "")}
        </div>
        <div class="button-row" style="margin-top: 14px;">
            ${ledger.manifestHref ? `<a class="button-link ghost" href="${ledger.manifestHref}" target="_blank" rel="noreferrer">Open model manifest</a>` : ""}
            ${ledger.runtimeInventoryHref ? `<a class="button-link ghost" href="${ledger.runtimeInventoryHref}" target="_blank" rel="noreferrer">Open runtime inventory</a>` : ""}
        </div>
        <div class="footer-note">Sample models: ${sampleModels.length ? sampleModels.map(item => escapeHtml(item)).join(", ") : "none yet"}</div>
    `;
}

function systemStateEnvelopeHtml(payload, laneLabel) {
    const envelope = payload && typeof payload === "object" && payload.systemState && typeof payload.systemState === "object"
        ? payload.systemState
        : payload;
    if (!envelope || typeof envelope !== "object") {
        return `<div class="empty-state">No grounded SYSTEM_STATE envelope was returned for ${escapeHtml(laneLabel || "this lane")}.</div>`;
    }

    const fetchedAt = payload?.fetchedAt || envelope.timestamp || "";
    const staleAfterMs = Number(payload?.staleAfterMs || 15000);
    const ageMs = Number.isFinite(Date.parse(fetchedAt))
        ? Math.max(0, Date.now() - Date.parse(fetchedAt))
        : 0;
    const isStale = ageMs > staleAfterMs;
    const freshnessLabel = isStale ? "stale" : "fresh";
    const timestamp = envelope.timestamp || "unknown";
    const boot = envelope.boot || {};
    const memory = envelope.memory || {};
    const ollama = envelope.ollama || {};
    const cores = envelope.cores || {};
    const provenance = envelope.provenance || {};
    const subsystemHealth = Array.isArray(envelope.subsystemHealth) ? envelope.subsystemHealth : [];
    const aiRun = boot.aiRun && typeof boot.aiRun === "object" ? boot.aiRun : {};
    const aiRunHealth = subsystemHealth.find(item => item && item.name === "aiRunRouter") || null;
    const aiRunBusy = Boolean(aiRun.busy ?? cores.aiRunBusy ?? (aiRunHealth && aiRunHealth.status === "degraded"));
    const aiRunQueueDepth = Number(aiRun.queueDepth ?? cores.aiRunQueueDepth ?? 0);
    const aiRunInFlight = typeof aiRun.inFlightCommand === "string" ? aiRun.inFlightCommand.trim() : "";
    const aiRunStatus = aiRun.status || (aiRunBusy ? "busy" : "idle");
    const aiRunChipClass = aiRunBusy ? "warning" : "positive";
    const aiRunMetricValue = aiRunBusy ? "busy" : "idle";
    const aiRunMetricAccent = `queueDepth=${Number.isFinite(aiRunQueueDepth) ? aiRunQueueDepth : 0}`;

    return `
        <div class="doc-stack">
            <div class="chip-row">
                <span class="chip ${isStale ? "warning" : "positive"}">state ${escapeHtml(freshnessLabel)}</span>
                <span class="chip">age ${escapeHtml(formatAgeMs(ageMs))}</span>
                <span class="chip">refresh every ${escapeHtml(formatAgeMs(SYSTEM_STATE_REFRESH_MS))}</span>
                <span class="chip ${aiRunChipClass}">ai run ${escapeHtml(String(aiRunStatus))}</span>
                <span class="chip ${aiRunBusy ? "warning" : ""}">queue ${escapeHtml(String(Number.isFinite(aiRunQueueDepth) ? aiRunQueueDepth : 0))}</span>
                ${aiRunInFlight ? `<span class="chip alert">in-flight ${escapeHtml(aiRunInFlight)}</span>` : ""}
            </div>
            <div class="footer-note">SYSTEM_STATE @ ${escapeHtml(String(timestamp))} | fetchedAt ${escapeHtml(String(fetchedAt || timestamp))}</div>
            <div class="metric-grid">
                ${[
                    metric("Boot", Object.keys(boot).length, "contract fields"),
                    metric("Memory", Object.keys(memory).length, "contract fields"),
                    metric("Ollama", ollama.status || (ollama.chatAvailable ? "online" : "offline"), ollama.model || "local lane"),
                    metric("Cores", cores.logicalCpuCores || 0, "logical CPU cores"),
                    metric("Subsystem health", subsystemHealth.length, "machine-checkable nodes"),
                    metric("AI run lane", aiRunMetricValue, aiRunMetricAccent)
                ].map(item => `
                    <article class="metric-card">
                        <div class="metric-label">${escapeHtml(item.label)}</div>
                        <div class="metric-value">${escapeHtml(String(item.value))}</div>
                        <div class="metric-accent">${escapeHtml(item.accent)}</div>
                    </article>
                `).join("")}
            </div>
            <div class="split-grid" style="margin-top: 16px;">
                <article class="route-card">
                    <div class="tiny-label">Boot</div>
                    ${stateMapRowsHtml(boot)}
                </article>
                <article class="route-card">
                    <div class="tiny-label">Memory</div>
                    ${stateMapRowsHtml(memory)}
                </article>
            </div>
            <div class="split-grid" style="margin-top: 16px;">
                <article class="route-card">
                    <div class="tiny-label">Ollama</div>
                    ${stateMapRowsHtml(ollama)}
                </article>
                <article class="route-card">
                    <div class="tiny-label">Cores</div>
                    ${stateMapRowsHtml(cores)}
                </article>
            </div>
            <article class="route-card" style="margin-top: 16px;">
                <div class="tiny-label">AI run pressure</div>
                ${Object.keys(aiRun).length
                    ? stateMapRowsHtml(aiRun)
                    : `<div class="empty-state">No ai run lane snapshot was provided.</div>`}
            </article>
            <article class="route-card" style="margin-top: 16px;">
                <div class="tiny-label">Subsystem health</div>
                ${subsystemHealth.length ? `
                    <div class="card-grid">
                        ${subsystemHealth.map(item => `
                            <article class="doc-card">
                                <div class="tiny-label">${escapeHtml(item.status || "unknown")}</div>
                                <div class="doc-title">${escapeHtml(item.name || "subsystem")}</div>
                                <div class="muted-text">score=${escapeHtml(String(item.score ?? "?"))}${item.detail ? ` | ${escapeHtml(item.detail)}` : ""}</div>
                            </article>
                        `).join("")}
                    </div>
                ` : `<div class="empty-state">No subsystem health nodes were provided.</div>`}
            </article>
            <article class="route-card" style="margin-top: 16px;">
                <div class="tiny-label">Provenance</div>
                ${stateMapRowsHtml(provenance)}
            </article>
        </div>
    `;
}

function stateMapRowsHtml(mapLike) {
    const entries = mapLike && typeof mapLike === "object" ? Object.entries(mapLike) : [];
    if (!entries.length) {
        return `<div class="empty-state">No fields.</div>`;
    }
    return `
        <div class="doc-stack">
            ${entries.map(([key, value]) => `
                <div class="muted-text">
                    <strong>${escapeHtml(key)}:</strong> ${escapeHtml(stateFieldValue(value))}
                </div>
            `).join("")}
        </div>
    `;
}

function stateFieldValue(value) {
    if (value === null || value === undefined) {
        return "";
    }
    if (typeof value === "object") {
        try {
            return JSON.stringify(value);
        } catch (error) {
            return String(value);
        }
    }
    return String(value);
}

function formatAgeMs(ageMs) {
    const safe = Math.max(0, Number(ageMs) || 0);
    if (safe < 1000) {
        return `${safe}ms`;
    }
    const seconds = Math.floor(safe / 1000);
    if (seconds < 60) {
        return `${seconds}s`;
    }
    const minutes = Math.floor(seconds / 60);
    const remSeconds = seconds % 60;
    if (minutes < 60) {
        return `${minutes}m ${remSeconds}s`;
    }
    const hours = Math.floor(minutes / 60);
    const remMinutes = minutes % 60;
    return `${hours}h ${remMinutes}m`;
}

async function renderMathCanon(artifacts) {
    const canon = document.getElementById("knowledge-math-canon");
    const anchors = document.getElementById("knowledge-math-anchors");
    if (!canon && !anchors) {
        return;
    }

    if (!artifacts?.mathUrl) {
        if (canon) {
            canon.innerHTML = `<div class="empty-state">The operator record has no math canon artifact yet.</div>`;
        }
        if (anchors) {
            anchors.innerHTML = `<div class="empty-state">Mathematical implementation anchors are unavailable until the canon is exported.</div>`;
        }
        return;
    }

    try {
        const [markdown, systemsData] = await Promise.all([
            fetchText(artifacts.mathUrl),
            fetchJson("/api/systems")
        ]);
        const parsed = parseMathCanon(markdown);
        const activeMath = readMathFocus(new URLSearchParams(window.location.search));
        if (canon) {
            canon.innerHTML = mathCanonHtml(parsed, activeMath);
            canon.querySelectorAll("[data-math-focus]").forEach(button => {
                button.addEventListener("click", () => {
                    const nextId = button.dataset.mathFocus;
                    rememberMathFocus(nextId);
                    replaceMathFocusQuery(nextId);
                    renderMathCanon(artifacts).catch(handleError);
                    renderKnowledgeGraph(artifacts).catch(handleError);
                });
            });
            canon.querySelectorAll("[data-math-clear]").forEach(button => {
                button.addEventListener("click", () => {
                    clearMathFocus();
                    replaceMathFocusQuery("");
                    renderMathCanon(artifacts).catch(handleError);
                    renderKnowledgeGraph(artifacts).catch(handleError);
                });
            });
        }
        if (anchors) {
            anchors.innerHTML = mathAnchorsHtml(parsed.anchors, systemsData.systems || [], activeMath);
        }
    } catch (error) {
        console.error(error);
        if (canon) {
            canon.innerHTML = `<div class="empty-state">Unable to load the Fraynix math canon from the exported operator record.</div>`;
        }
        if (anchors) {
            anchors.innerHTML = `<div class="empty-state">Mathematical implementation anchors could not be resolved from the current operator record.</div>`;
        }
    }
}

async function renderKnowledgeGraph(artifacts) {
    const map = document.getElementById("knowledge-graph-map");
    const brief = document.getElementById("knowledge-graph-brief");
    if (!map && !brief) {
        return;
    }

    if (!artifacts?.graphUrl) {
        if (map) {
            map.innerHTML = `<div class="empty-state">The operator record has no graph artifact yet.</div>`;
        }
        if (brief) {
            brief.innerHTML = `<div class="empty-state">Graph semantics are unavailable until the operator record is exported.</div>`;
        }
        return;
    }

    try {
        const [graph, operator] = await Promise.all([
            fetchJson(artifacts.graphUrl),
            fetchJson("/api/operator")
        ]);
        const semantics = graph.visualSemantics || {};
        const nodes = new Map((graph.nodes || []).map(node => [node.id, node]));
        const legend = semantics.legend || [];
        const touchState = readLaneTouchState(nodes);
        const params = new URLSearchParams(window.location.search);
        const activeMath = readMathFocus(params);
        const proofLanes = operator.livingProofLanes || [];
        const activeProof = syncProofFocus(params, proofLanes);
        const grouped = buildLaneGroups(legend, semantics.clusters || [], nodes, touchState, activeMath, activeProof);

        if (brief) {
            brief.innerHTML = graphSemanticsHtml(graph, semantics, legend, grouped, activeMath, activeProof, proofLanes);
        }

        if (map) {
            if (!grouped.length) {
                map.innerHTML = `<div class="empty-state">The operator graph exported without lane clusters.</div>`;
                return;
            }
            map.innerHTML = `
                <div class="lane-map-grid">
                    ${grouped.map(group => laneCardHtml(group, activeMath, activeProof)).join("")}
                </div>
            `;
        }

        const rerenderKnowledge = () => renderKnowledgeGraph(artifacts).catch(handleError);
        brief?.querySelectorAll("[data-knowledge-proof-focus]").forEach(button => {
            button.addEventListener("click", () => {
                const nextId = button.dataset.knowledgeProofFocus;
                rememberProofFocus(nextId);
                replaceProofFocusQuery(nextId);
                rerenderKnowledge();
            });
        });
        brief?.querySelectorAll("[data-knowledge-proof-clear]").forEach(button => {
            button.addEventListener("click", () => {
                clearProofFocus();
                replaceProofFocusQuery("");
                rerenderKnowledge();
            });
        });
    } catch (error) {
        console.error(error);
        if (map) {
            map.innerHTML = `<div class="empty-state">Unable to load the live operator map from the exported graph artifact.</div>`;
        }
        if (brief) {
            brief.innerHTML = `<div class="empty-state">Graph semantics could not be read from the current operator record.</div>`;
        }
    }
}

function parseMathCanon(markdown) {
    const nativeSection = extractMarkdownSection(markdown, "## Fraynix-Native Mathematical Spine", "## Major AI Math Families Fraynix Must Speak");
    const modernSection = extractMarkdownSection(markdown, "## Major AI Math Families Fraynix Must Speak", "## Local Implementation Anchors");
    const anchorSection = extractMarkdownSection(markdown, "## Local Implementation Anchors", "## Operator Interpretation");

    const nativeFamilies = parseMathFamilies(nativeSection);
    const modernFamilies = parseMathFamilies(modernSection);
    const constants = extractCodeBlock(nativeFamilies[0]?.body || "");

    return {
        constants,
        nativeFamilies,
        modernFamilies,
        anchors: parseMathAnchors(anchorSection)
    };
}

function extractMarkdownSection(markdown, startHeading, endHeading) {
    const start = markdown.indexOf(startHeading);
    if (start < 0) {
        return "";
    }
    const fromStart = markdown.slice(start + startHeading.length);
    if (!endHeading) {
        return fromStart.trim();
    }
    const end = fromStart.indexOf(endHeading);
    return (end >= 0 ? fromStart.slice(0, end) : fromStart).trim();
}

function extractCodeBlock(markdown) {
    const match = markdown.match(/```(?:text)?\s*([\s\S]*?)```/);
    return match ? match[1].trim() : "";
}

function parseMathFamilies(markdown) {
    const matches = [...markdown.matchAll(/^###\s+(.+)$/gm)];
    if (!matches.length) {
        return [];
    }
    return matches.map((match, index) => {
        const start = match.index + match[0].length;
        const end = index + 1 < matches.length ? matches[index + 1].index : markdown.length;
        const body = markdown.slice(start, end).trim();
        const familyRule = mathFamilyForTitle(match[1].trim());
        return {
            id: familyRule?.id || sanitizeNodeId(match[1].trim().toLowerCase()),
            title: match[1].trim(),
            body,
            bullets: [...body.matchAll(/^- (.+)$/gm)].map(entry => entry[1].trim()),
            actions: familyRule?.actions || [],
            strata: familyRule?.strata || [],
            terms: familyRule?.terms || []
        };
    });
}

function parseMathAnchors(markdown) {
    return [...markdown.matchAll(/^- `([^`]+)`$/gm)].map((match, index) => ({
        id: `math-anchor-${index + 1}`,
        path: match[1].trim(),
        label: match[1].trim().split("/").pop()
    }));
}

function mathCanonHtml(parsed, activeMath = null) {
    return `
        <div class="card-grid">
            <article class="doc-card">
                <div class="tiny-label">Core constants</div>
                <div class="doc-title">${escapeHtml(String(parsed.constants ? parsed.constants.split(/\r?\n/).length : 0))}</div>
                <div class="muted-text">Foundational constants and identities rendered from the exported canon.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Fraynix-native families</div>
                <div class="doc-title">${escapeHtml(String(parsed.nativeFamilies.length))}</div>
                <div class="muted-text">Phi, zero-time HDC, causal, and persistence-native layers.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Modern AI families</div>
                <div class="doc-title">${escapeHtml(String(parsed.modernFamilies.length))}</div>
                <div class="muted-text">Transformer, state-space, MoE, optimization, and substrate math lanes.</div>
            </article>
        </div>
        <div class="button-row" style="margin-top: 16px;">
            ${activeMath ? `<span class="chip positive">Active math focus: ${escapeHtml(activeMath.label)}</span>` : `<span class="chip">No active math focus</span>`}
            ${activeMath ? `<button class="btn ghost" type="button" data-math-clear="true">Clear focus</button>` : ""}
        </div>
        ${parsed.constants ? `
            <article class="doc-card" style="margin-top: 16px;">
                <div class="tiny-label">Core constants</div>
                <pre class="code-block">${escapeHtml(parsed.constants)}</pre>
            </article>
        ` : ""}
        <div class="card-grid" style="margin-top: 16px;">
            ${parsed.nativeFamilies.map(family => mathFamilyCardHtml(family, "Fraynix-native family", activeMath)).join("")}
            ${parsed.modernFamilies.map(family => mathFamilyCardHtml(family, "Modern AI family", activeMath)).join("")}
        </div>
    `;
}

function mathFamilyCardHtml(family, label, activeMath = null) {
    const summary = family.body
        .replace(/```[\s\S]*?```/g, "")
        .replace(/^- .+$/gm, "")
        .trim()
        .split(/\n\s*\n/)[0] || "";
    const isActive = activeMath?.id === family.id;
    return `
        <article class="doc-card ${isActive ? "math-focus-match" : ""}">
            <div class="tiny-label">${escapeHtml(label)}</div>
            <div class="doc-title">${escapeHtml(family.title)}</div>
            ${summary ? `<p class="muted-text">${escapeHtml(summary)}</p>` : ""}
            ${family.bullets.length ? `
                <div class="chip-row" style="margin-top: 10px;">
                    ${family.bullets.map(item => `<span class="chip">${escapeHtml(item)}</span>`).join("")}
                </div>
            ` : ""}
            <div class="button-row" style="margin-top: 12px;">
                <button class="btn ${isActive ? "primary" : "secondary"}" type="button" data-math-focus="${escapeHtml(family.id)}">${isActive ? "Focused math family" : "Apply math focus"}</button>
                <a class="button-link ghost" href="/systems?math=${encodeURIComponent(family.id)}">Open Systems</a>
                <a class="button-link secondary" href="/runtime?math=${encodeURIComponent(family.id)}">Open Runtime</a>
            </div>
        </article>
    `;
}

function mathAnchorsHtml(anchors, systems, activeMath = null) {
    if (!anchors.length) {
        return `<div class="empty-state">The math canon did not expose any implementation anchors.</div>`;
    }
    const rankedAnchors = [...anchors].sort((left, right) => mathAnchorMatchScore(right, activeMath) - mathAnchorMatchScore(left, activeMath));
    return `
        <ul class="list-reset link-list">
            ${rankedAnchors.map(anchor => mathAnchorItemHtml(anchor, systems, activeMath)).join("")}
        </ul>
    `;
}

function mathAnchorItemHtml(anchor, systems, activeMath = null) {
    const target = resolveMathAnchorTarget(anchor.path, systems);
    const isMatch = mathAnchorMatchScore(anchor, activeMath) > 0;
    return `
        <li class="doc-card ${isMatch ? "math-focus-match" : ""}">
            <div class="tiny-label">Implementation anchor</div>
            <div class="doc-title">${escapeHtml(anchor.label)}</div>
            <div class="muted-text">${escapeHtml(anchor.path)}</div>
            <div class="button-row" style="margin-top: 12px;">
                ${target.systemsUrl ? `<a class="button-link ghost" href="${target.systemsUrl}">Open subsystem lens</a>` : ""}
                ${target.runtimeUrl ? `<a class="button-link secondary" href="${target.runtimeUrl}">Open runtime lens</a>` : ""}
                <a class="button-link secondary" href="${target.searchUrl}">Search anchor</a>
            </div>
            ${target.systemName ? `<div class="footer-note">Matched subsystem: ${escapeHtml(target.systemName)}</div>` : `<div class="footer-note">No subsystem match yet; using knowledge search handoff.</div>`}
        </li>
    `;
}

function mathAnchorMatchScore(anchor, family) {
    if (!family || !anchor) {
        return 0;
    }
    const haystack = `${anchor.path} ${anchor.label}`.toLowerCase();
    return family.terms.reduce((score, term) => score + (haystack.includes(term.toLowerCase()) ? 2 : 0), 0);
}

function resolveMathAnchorTarget(anchorPath, systems) {
    const normalizedAnchor = normalizeMathPath(anchorPath);
    const matched = (systems || []).find(system => {
        const path = normalizeMathPath(system.path || "");
        const workingDirectory = normalizeMathPath(system.workingDirectory || "");
        return (path && (path.endsWith(normalizedAnchor) || normalizedAnchor.endsWith(path)))
            || (workingDirectory && normalizedAnchor.startsWith(workingDirectory));
    });
    const basename = anchorPath.split("/").pop().replace(/\.[^.]+$/, "");
    return {
        systemsUrl: matched ? `/systems?focus=${encodeURIComponent(matched.id)}&detail=inspect` : "",
        runtimeUrl: matched ? preferredRuntimeUrl(matched) : "",
        searchUrl: `/launchpad?cmd=${encodeURIComponent(`search ${basename}`)}`,
        systemName: matched ? matched.name : ""
    };
}

function normalizeMathPath(path) {
    return String(path || "").replaceAll("\\", "/").replace(/^\.\//, "");
}

function systemsMathFocusHtml(family, matchedSystems) {
    if (!family) {
        return `Choose a math family from the Knowledge page to sort and highlight matching subsystems here.`;
    }
    return `
        <div class="card-grid">
            <article class="doc-card math-focus-match">
                <div class="tiny-label">Active math family</div>
                <div class="doc-title">${escapeHtml(family.label)}</div>
                <div class="muted-text">${escapeHtml(matchedSystems.length)} subsystems currently match this mathematical lane.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">AGI strata</div>
                <div class="chip-row">${family.strata.map(stratum => `<span class="chip ${stratumTone(stratum)}">${escapeHtml(formatStratum(stratum))}</span>`).join("")}</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Runtime actions</div>
                <div class="chip-row">${family.actions.map(action => `<span class="chip warning">${escapeHtml(action)}</span>`).join("")}</div>
            </article>
        </div>
        <div class="button-row" style="margin-top: 14px;">
            <a class="button-link ghost" href="/knowledge?math=${encodeURIComponent(family.id)}">Open in Knowledge</a>
            <a class="button-link secondary" href="/runtime?math=${encodeURIComponent(family.id)}">Open in Runtime</a>
            <a class="button-link secondary" href="/systems?clearMath=1">Clear on Systems</a>
        </div>
        ${matchedSystems.length ? `
            <div class="chip-row" style="margin-top: 14px;">
                ${matchedSystems.slice(0, 6).map(system => `<a class="button-link ghost" href="/systems?math=${encodeURIComponent(family.id)}&focus=${encodeURIComponent(system.id)}&detail=inspect">${escapeHtml(system.name)}</a>`).join("")}
            </div>
        ` : `<div class="empty-state" style="margin-top: 14px;">No subsystems in the current registry matched this mathematical lane.</div>`}
    `;
}

function systemsProofFocusHtml(lane, matchedSystems) {
    if (!lane) {
        return `Choose a living proof lane from Surfaces to sort and highlight the subsystems that embody that proof path here.`;
    }
    return `
        <div class="card-grid">
            <article class="doc-card proof-focus-match">
                <div class="tiny-label">Active proof lane</div>
                <div class="doc-title">${escapeHtml(lane.title)}</div>
                <div class="muted-text">${escapeHtml(matchedSystems.length)} subsystems currently match this living proof lane.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">AGI strata</div>
                <div class="chip-row">${(lane.agiStrata || []).map(stratum => `<span class="chip ${stratumTone(stratum)}">${escapeHtml(formatStratum(stratum))}</span>`).join("")}</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Focus commands</div>
                <div class="chip-row">${(lane.focusCommands || []).map(command => `<span class="chip warning">${escapeHtml(command)}</span>`).join("")}</div>
            </article>
        </div>
        <div class="button-row" style="margin-top: 14px;">
            <a class="button-link ghost" href="/surfaces?proof=${encodeURIComponent(lane.id)}">Open on Surfaces</a>
            <a class="button-link secondary" href="/runtime?proof=${encodeURIComponent(lane.id)}">Open in Runtime</a>
            <a class="button-link secondary" href="/systems?clearProof=1">Clear on Systems</a>
        </div>
        ${matchedSystems.length ? `
            <div class="chip-row" style="margin-top: 14px;">
                ${matchedSystems.slice(0, 6).map(system => `<a class="button-link ghost" href="/systems?proof=${encodeURIComponent(lane.id)}&focus=${encodeURIComponent(system.id)}&detail=inspect">${escapeHtml(system.name)}</a>`).join("")}
            </div>
        ` : `<div class="empty-state" style="margin-top: 14px;">No subsystems in the current registry matched this living proof lane.</div>`}
    `;
}

function runtimeMathFocusHtml(family, matchedSystems) {
    if (!family) {
        return `Choose a math family from the Knowledge page to promote matching runtime actions and subsystem paths here.`;
    }
    return `
        <div class="card-grid">
            <article class="doc-card math-focus-match">
                <div class="tiny-label">Active math family</div>
                <div class="doc-title">${escapeHtml(family.label)}</div>
                <div class="muted-text">Runtime quick actions and subsystem paths are being biased toward this mathematical lane.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Suggested actions</div>
                <div class="chip-row">${family.actions.map(action => `<span class="chip warning">${escapeHtml(action)}</span>`).join("")}</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Matched subsystems</div>
                <div class="doc-title">${escapeHtml(String(matchedSystems.length))}</div>
                <div class="muted-text">Top runtime-aware subsystem paths for this mathematical lane.</div>
            </article>
        </div>
        <div class="button-row" style="margin-top: 14px;">
            <a class="button-link ghost" href="/knowledge?math=${encodeURIComponent(family.id)}">Return to canon</a>
            <a class="button-link secondary" href="/systems?math=${encodeURIComponent(family.id)}">Inspect subsystem matches</a>
        </div>
        ${matchedSystems.length ? `
            <div class="chip-row" style="margin-top: 14px;">
                ${matchedSystems.slice(0, 6).map(system => `<a class="button-link ghost" href="/runtime?math=${encodeURIComponent(family.id)}&subsystem=${encodeURIComponent(system.id)}&panel=actions&action=${encodeURIComponent(family.actions[0] || preferredRuntimeAction(system))}">${escapeHtml(system.name)}</a>`).join("")}
            </div>
        ` : `<div class="empty-state" style="margin-top: 14px;">No runtime-aware subsystem matched this mathematical lane in the current registry.</div>`}
    `;
}

function runtimeProofFocusHtml(lane, matchedSystems) {
    if (!lane) {
        return `Choose a living proof lane from Surfaces to promote matching runtime actions and subsystem paths here.`;
    }
    return `
        <div class="card-grid">
            <article class="doc-card proof-focus-match">
                <div class="tiny-label">Active proof lane</div>
                <div class="doc-title">${escapeHtml(lane.title)}</div>
                <div class="muted-text">Runtime quick actions and subsystem paths are being biased toward this living proof lane.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Focus commands</div>
                <div class="chip-row">${(lane.focusCommands || []).map(command => `<span class="chip warning">${escapeHtml(command)}</span>`).join("")}</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Matched subsystems</div>
                <div class="doc-title">${escapeHtml(String(matchedSystems.length))}</div>
                <div class="muted-text">Top runtime-aware subsystem paths for this living proof lane.</div>
            </article>
        </div>
        <div class="button-row" style="margin-top: 14px;">
            <a class="button-link ghost" href="/surfaces?proof=${encodeURIComponent(lane.id)}">Return to living proof lanes</a>
            <a class="button-link secondary" href="/systems?proof=${encodeURIComponent(lane.id)}">Inspect subsystem matches</a>
            <a class="button-link secondary" href="/runtime?clearProof=1">Clear on Runtime</a>
        </div>
        ${matchedSystems.length ? `
            <div class="chip-row" style="margin-top: 14px;">
                ${matchedSystems.slice(0, 6).map(system => `<a class="button-link ghost" href="/runtime?proof=${encodeURIComponent(lane.id)}&subsystem=${encodeURIComponent(system.id)}&panel=actions&action=${encodeURIComponent(preferredProofAction(system, lane) || preferredRuntimeAction(system))}">${escapeHtml(system.name)}</a>`).join("")}
            </div>
        ` : `<div class="empty-state" style="margin-top: 14px;">No runtime-aware subsystem matched this living proof lane in the current registry.</div>`}
    `;
}

function renderPrinciples(id, principles) {
    const container = document.getElementById(id);
    if (!container) {
        return;
    }
    container.classList.add("card-grid");
    container.innerHTML = principles.map(principle => `
        <article class="preset-card">
            <div class="tiny-label">open shared principle</div>
            <p class="system-summary">${escapeHtml(principle)}</p>
        </article>
    `).join("");
}

function setCode(id, text) {
    const element = document.getElementById(id);
    if (!element) {
        return;
    }
    element.textContent = text || "";
}

function statusChip(active, label) {
    return `<span class="status-pill ${active ? "online" : "alert"}">${escapeHtml(label)}: ${active ? "online" : "offline"}</span>`;
}

function metric(label, value, accent) {
    return { label, value, accent };
}

function commandGroup(surfaceId, section, focusSection = null, activeProof = null) {
    const isFocusedSection = !!focusSection && section.id === focusSection;
    const proofScore = proofLaneMatchSection(surfaceId, section, activeProof);
    return `
        <details class="command-group ${isFocusedSection ? "focused-group" : ""} ${proofScore > 0 ? "proof-focus-match" : ""}" data-section-id="${escapeHtml(section.id)}" ${isFocusedSection ? "open" : ""}>
            <summary>
                <div>
                    <div class="tiny-label">${escapeHtml(section.id)}</div>
                    <strong>${escapeHtml(section.title)}</strong>
                </div>
                <span class="chip">${section.commandCount} commands</span>
            </summary>
            <div class="command-list">
                <p class="muted-text">${escapeHtml(section.theme)}</p>
                <div class="chip-row" style="margin-bottom: 12px;">
                    <span class="chip ${section.kernelOverlap ? "positive" : "warning"}">${section.kernelOverlap ? "kernel overlap" : "launch-richer"}</span>
                    <span class="chip">${escapeHtml(section.kernelEquivalentStatus || "partial")}</span>
                    <span class="chip">${escapeHtml(section.integrationPriority || "mapped")}</span>
                    ${proofScore > 0 ? `<span class="chip positive">proof lane ${escapeHtml(String(proofScore))}</span>` : ""}
                    ${section.discoveredFromBootTree ? `<span class="chip warning">source-discovered boot branch</span>` : ""}
                </div>
                ${renderStrataChips(section.agiStrata, "margin-bottom: 12px;")}
                <div class="chip-row" style="margin-bottom: 12px;">
                    ${booleanRoleChip(section.governingCore, "governing")}
                    ${booleanRoleChip(section.cognitionCore, "cognition")}
                    ${booleanRoleChip(section.organismCore, "organism")}
                    ${booleanRoleChip(section.absorptionCore, "absorption")}
                    ${booleanRoleChip(section.proofLane, "proof")}
                    ${booleanRoleChip(section.modelSubstrateLane, "model substrate")}
                    ${booleanRoleChip(section.bareMetalSubstrateLane, "bare metal")}
                    ${booleanRoleChip(section.kernelDiscipline, "kernel discipline")}
                    ${booleanRoleChip(section.adjacentHistoricalCore, "historical core")}
                </div>
                ${section.primaryVerbs.length ? `
                    <div class="chip-row" style="margin-bottom: 12px;">
                        ${section.primaryVerbs.map(verb => `<span class="chip">${escapeHtml(verb)}</span>`).join("")}
                    </div>
                ` : ""}
                ${section.operatorRole ? `<p class="muted-text"><strong>Role:</strong> ${escapeHtml(section.operatorRole)}</p>` : ""}
                ${section.migrationPolicy ? `<p class="muted-text"><strong>Policy:</strong> ${escapeHtml(section.migrationPolicy)}</p>` : ""}
                ${section.notes ? `<p class="muted-text">${escapeHtml(section.notes)}</p>` : ""}
                ${section.linkedSubsystems.length ? `
                    <div class="chip-row">
                        ${section.linkedSubsystems.map(link => `<span class="chip positive">${escapeHtml(link.id)}</span>`).join("")}
                    </div>
                ` : `<div class="chip-row"><span class="chip warning">native execution</span></div>`}
                ${section.commands.map(command => `
                    <div class="command-item">
                        <p class="command-syntax">${escapeHtml(command.syntax)}</p>
                        <p class="command-description">${escapeHtml(command.description)}</p>
                        ${(command.discoveredFromBootTree || command.launchBranch || command.sourceLine > 0 || (command.aliases && command.aliases.length)) ? `
                            <div class="chip-row" style="margin-top: 8px;">
                                ${command.discoveredFromBootTree ? `<span class="chip warning">from FraynixBoot source</span>` : ""}
                                ${command.launchBranch ? `<span class="chip positive">launch branch</span>` : ""}
                                ${command.sourceLine > 0 ? `<span class="chip">line ${escapeHtml(String(command.sourceLine))}</span>` : ""}
                                ${(command.aliases && command.aliases.length) ? `<span class="chip">${escapeHtml(command.aliases.join(" | "))}</span>` : ""}
                            </div>
                        ` : ""}
                    </div>
                `).join("")}
                <div class="button-row">
                    <a class="button-link ghost" href="${launchpadHref(section.inspectCommand)}">Inspect in launchpad</a>
                    <a class="button-link secondary" href="${launchpadHref(section.routeCommand)}">Route this lane</a>
                    <a class="button-link secondary" href="/runtime">Open runtime</a>
                </div>
            </div>
        </details>
    `;
}

function systemCard(system, focusId = null, activeMath = null, activeProof = null) {
    const mathScore = mathFamilyMatchScore(system, activeMath);
    const proofScore = proofLaneMatchScore(system, activeProof);
    return `
        <article class="system-card ${system.id === focusId ? "focused-card" : ""} ${mathScore > 0 ? "math-focus-match" : ""} ${proofScore > 0 ? "proof-focus-match" : ""}" data-system-id="${escapeHtml(system.id)}">
            <div class="tiny-label">${escapeHtml(system.family)}</div>
            <h3 class="system-title">${escapeHtml(system.name)}</h3>
            <p class="system-summary">${escapeHtml(system.summary)}</p>
            <div class="chip-row" style="margin: 14px 0;">
                <span class="chip ${system.runnable ? "positive" : "warning"}">${system.runnable ? "wired launch" : "manual only"}</span>
                <span class="chip">${escapeHtml(system.id)}</span>
                ${mathScore > 0 ? `<span class="chip positive">math match ${escapeHtml(String(mathScore))}</span>` : ""}
                ${proofScore > 0 ? `<span class="chip positive">proof match ${escapeHtml(String(proofScore))}</span>` : ""}
            </div>
            ${renderStrataChips(system.agiStrata, "margin: 0 0 14px;")}
            <div class="chip-row" style="margin-bottom: 14px;">
                ${booleanRoleChip(system.governingCore, "governing")}
                ${booleanRoleChip(system.cognitionCore, "cognition")}
                ${booleanRoleChip(system.organismCore, "organism")}
                ${booleanRoleChip(system.absorptionCore, "absorption")}
                ${booleanRoleChip(system.proofLane, "proof")}
                ${booleanRoleChip(system.modelSubstrateLane, "model substrate")}
                ${booleanRoleChip(system.bareMetalSubstrateLane, "bare metal")}
                ${booleanRoleChip(system.kernelDiscipline, "kernel discipline")}
                ${booleanRoleChip(system.constructionLane, "construction")}
                ${booleanRoleChip(system.adjacentHistoricalCore, "historical core")}
            </div>
            <div class="spec-grid">
                <div class="spec">
                    <strong>Role</strong>
                    <span>${escapeHtml(system.designRole)}</span>
                </div>
                <div class="spec">
                    <strong>Path</strong>
                    <span>${escapeHtml(system.path)}</span>
                </div>
            </div>
            <div class="chip-row" style="margin-top: 14px;">
                ${system.capabilities.slice(0, 4).map(item => `<span class="chip">${escapeHtml(item)}</span>`).join("")}
            </div>
        </article>
    `;
}

function operatorBriefHtml(operator) {
    return `
        <div class="card-grid">
            <article class="doc-card">
                <div class="tiny-label">Authoritative shell</div>
                <div class="doc-title">${escapeHtml(operator.authoritativeShell)}</div>
                <div class="muted-text">${escapeHtml(operator.executionPath)}</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Kernel-first surfaces</div>
                <div class="doc-title">${escapeHtml(String(operator.kernelPreferredSurfaceCount))}</div>
                <div class="muted-text">Surfaces whose overlap should prefer in-process execution.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Overlap-aware sections</div>
                <div class="doc-title">${escapeHtml(String(operator.kernelOverlapSectionCount))}</div>
                <div class="muted-text">Sections with explicit kernel overlap or migration policy.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Boot tree discovery</div>
                <div class="doc-title">${escapeHtml(String(operator.bootTreeBranchCount))}</div>
                <div class="muted-text">${escapeHtml(String(operator.uncataloguedBootBranchCount))} source branches are still outside the curated catalogs.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Living proof lanes</div>
                <div class="doc-title">${escapeHtml(String(operator.livingProofLaneCount || 0))}</div>
                <div class="muted-text">Morse, Ouroboros, proof, organism validation, and substrate transduction are now surfaced directly.</div>
            </article>
        </div>
        <div class="panel-header" style="margin-top: 18px;">
            <div>
                <div class="panel-title">Surface AGI strata</div>
                <div class="panel-subtitle">Which operator lanes map to governing, cognition, organism, absorption, proof, or substrate roles.</div>
            </div>
        </div>
        <div class="chip-row" style="margin: 14px 0;">
            ${operator.surfaceAgiStrataCounts.map(item => strataCountChip(item)).join("")}
        </div>
        <div class="panel-header" style="margin-top: 8px;">
            <div>
                <div class="panel-title">Section AGI strata</div>
                <div class="panel-subtitle">Role density across the live command groups, including source-discovered boot sections.</div>
            </div>
        </div>
        <div class="chip-row" style="margin: 14px 0;">
            ${operator.sectionAgiStrataCounts.map(item => strataCountChip(item)).join("")}
        </div>
        <div class="chip-row" style="margin: 16px 0 14px;">
            ${operator.priorityCounts.map(item => `<span class="chip">${escapeHtml(item.name)}: ${item.count}</span>`).join("")}
        </div>
        <ul class="list-reset link-list">
            ${operator.notes.map(note => `<li class="doc-card"><div class="muted-text">${escapeHtml(note)}</div></li>`).join("")}
        </ul>
    `;
}

function livingProofLanesHtml(lanes, activeProof = null) {
    if (!lanes || !lanes.length) {
        return `<div class="empty-state">No living proof lanes are registered in the operator manifest yet.</div>`;
    }
    return `
        ${activeProof ? `
            <div class="panel-header" style="margin-bottom: 14px;">
                <div>
                    <div class="panel-title">Active living proof focus</div>
                    <div class="panel-subtitle">${escapeHtml(activeProof.title)} is currently biasing surfaces, subsystems, and runtime actions across the shell.</div>
                </div>
            </div>
        ` : ""}
        <div class="card-grid">
            ${lanes.map(lane => livingProofLaneCardHtml(lane, activeProof)).join("")}
        </div>
    `;
}

function livingProofLaneCardHtml(lane, activeProof = null) {
    const isActive = !!activeProof && activeProof.id === lane.id;
    const commandLinks = (lane.focusCommands || []).map(command => `
        <a class="button-link ghost" href="/launchpad?cmd=${encodeURIComponent(command)}">${escapeHtml(command)}</a>
    `).join("");
    const sectionLinks = (lane.sections || []).map(section => `
        <a class="button-link secondary" href="${escapeHtml(section.jumpUrl)}">${escapeHtml(section.surfaceTitle)}: ${escapeHtml(section.sectionTitle)}</a>
    `).join("");
    const routeLinks = (lane.sections || []).slice(0, 2).map(section => `
        <a class="button-link ghost" href="${escapeHtml(section.routeUrl)}">Route ${escapeHtml(section.sectionTitle)}</a>
    `).join("");
    return `
        <article class="doc-card ${isActive ? "proof-focus-match" : ""}">
            <div class="tiny-label">${escapeHtml(lane.operatorRole)}</div>
            <div class="doc-title">${escapeHtml(lane.title)}</div>
            <p class="muted-text">${escapeHtml(lane.summary)}</p>
            ${renderStrataChips(lane.agiStrata, "margin: 16px 0 14px;")}
            <div class="chip-row" style="margin-bottom: 14px;">
                ${booleanRoleChip(lane.organismCore, "organism")}
                ${booleanRoleChip(lane.proofLane, "proof")}
                ${booleanRoleChip(lane.cognitionCore, "cognition")}
                ${booleanRoleChip(lane.absorptionCore, "absorption")}
                ${booleanRoleChip(lane.bareMetalSubstrateLane, "bare metal")}
                ${booleanRoleChip(lane.kernelDiscipline, "kernel discipline")}
                ${isActive ? `<span class="chip positive">active proof focus</span>` : ""}
            </div>
            <div class="button-row" style="margin-bottom: 14px;">
                <a class="button-link primary ${isActive ? "proof-focus-match" : ""}" href="/surfaces?proof=${encodeURIComponent(lane.id)}">Apply proof focus</a>
                <a class="button-link secondary" href="/systems?proof=${encodeURIComponent(lane.id)}">Open in Systems</a>
                <a class="button-link secondary" href="/runtime?proof=${encodeURIComponent(lane.id)}">Open in Runtime</a>
                ${isActive ? `<a class="button-link ghost" href="/surfaces?clearProof=1">Clear proof focus</a>` : ""}
            </div>
            <div class="panel-header" style="margin-top: 4px;">
                <div>
                    <div class="panel-title">Focus commands</div>
                    <div class="panel-subtitle">Direct handoff into Launchpad for the living proof lane.</div>
                </div>
            </div>
            <div class="button-row" style="margin: 14px 0;">
                ${commandLinks}
            </div>
            <div class="panel-header" style="margin-top: 4px;">
                <div>
                    <div class="panel-title">Surface anchors</div>
                    <div class="panel-subtitle">Open the exact command groups that carry this living proof lane.</div>
                </div>
            </div>
            <div class="button-row" style="margin: 14px 0 12px;">
                ${sectionLinks}
                ${routeLinks}
            </div>
            <div class="panel-header" style="margin-top: 4px;">
                <div>
                    <div class="panel-title">Implementation anchors</div>
                    <div class="panel-subtitle">The code and docs that prove this lane is real in the current Fraynix organism.</div>
                </div>
            </div>
            <ul class="list-reset link-list" style="margin-top: 14px;">
                ${(lane.evidencePaths || []).map(path => `<li class="doc-card"><div class="muted-text">${escapeHtml(path)}</div></li>`).join("")}
            </ul>
            <ul class="list-reset link-list" style="margin-top: 14px;">
                ${(lane.notes || []).map(note => `<li class="doc-card"><div class="muted-text">${escapeHtml(note)}</div></li>`).join("")}
            </ul>
        </article>
    `;
}

function runtimeRoleHtml(nativeSurface, operator) {
    return `
        <div class="card-grid">
            <article class="doc-card">
                <div class="tiny-label">Runtime authority</div>
                <div class="doc-title">${escapeHtml(nativeSurface.authorityLevel)}</div>
                <div class="muted-text">${escapeHtml(nativeSurface.executionModel)}</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Kernel overlap sections</div>
                <div class="doc-title">${escapeHtml(String(operator.kernelOverlapSectionCount))}</div>
                <div class="muted-text">The runtime is the proof spine for overlap across command surfaces.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Native role families</div>
                <div class="doc-title">${escapeHtml(String(nativeSurface.agiStrata.length))}</div>
                <div class="muted-text">This page now reads the runtime as part of the AGI organism, not only as technical state.</div>
            </article>
        </div>
        <div class="chip-row" style="margin: 16px 0 14px;">
            ${nativeSurface.agiStrata.map(stratum => `<span class="chip ${stratumTone(stratum)}">${escapeHtml(formatStratum(stratum))}</span>`).join("")}
        </div>
        <p class="muted-text"><strong>Role:</strong> ${escapeHtml(nativeSurface.operatorRole)}</p>
        <p class="muted-text"><strong>Policy:</strong> ${escapeHtml(nativeSurface.migrationPolicy)}</p>
        <p class="muted-text">${escapeHtml(nativeSurface.notes)}</p>
    `;
}

function launchpadRoleHtml(systems, overview) {
    const legacy = overview?.legacySpine;
    return `
        <div class="card-grid">
            <article class="doc-card">
                <div class="tiny-label">Routeable subsystems</div>
                <div class="doc-title">${escapeHtml(String(systems.counts.subsystems))}</div>
                <div class="muted-text">${escapeHtml(String(systems.counts.agiStrata))} subsystem role families remain visible to the command deck.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Primary shell families</div>
                <div class="doc-title">${escapeHtml(String(overview.counts.commandSurfaces))}</div>
                <div class="muted-text">${escapeHtml(String(overview.counts.commandSections))} sections and ${escapeHtml(String(overview.counts.cataloguedCommands))} commands stay routeable from here.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Kernel pillars</div>
                <div class="doc-title">${escapeHtml(String(legacy?.availableCount || 0))}/${escapeHtml(String(legacy?.totalCount || 0))}</div>
                <div class="muted-text">The restored browser shell keeps boot, factory, broadcast, cockpit, and live signal lanes visible.</div>
            </article>
        </div>
        <div class="chip-row" style="margin: 16px 0 14px;">
            ${systems.agiStrata.map(item => strataCountChip(item)).join("")}
        </div>
        <p class="muted-text">Use Launchpad to move through the real Fraynix menu: boot, build, proof, runtime, broadcast, and subsystem routing without losing the system-level frame.</p>
    `;
}

function systemsFocusHtml(system, detailMode, focusMemory = null, activeProof = null) {
    const launchpadInspect = `/launchpad?cmd=${encodeURIComponent(`system ${system.id}`)}`;
    const runtimeUrl = preferredRuntimeUrl(system);
    const actions = subsystemActions(system, activeProof);
    const rememberedOutput = focusMemory && focusMemory.command
        ? `Last remembered result${formatRememberedAt(focusMemory.updatedAt)}:\n\n> ${focusMemory.command}\n\n${focusMemory.output || "(no output)"}`
        : "Choose a direct action to run it through the operator shell without leaving Systems.";
    return `
        <div class="card-grid">
            <article class="doc-card">
                <div class="tiny-label">Focused subsystem</div>
                <div class="doc-title">${escapeHtml(system.name)}</div>
                <div class="muted-text">${escapeHtml(system.family)} | ${escapeHtml(system.designRole)}</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Detail mode</div>
                <div class="doc-title">${escapeHtml(detailMode)}</div>
                <div class="muted-text">${escapeHtml(system.summary)}</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Working path</div>
                <div class="doc-title">${escapeHtml(system.path || "(none)")}</div>
                <div class="muted-text">${escapeHtml(system.workingDirectory || "")}</div>
            </article>
        </div>
        ${renderStrataChips(system.agiStrata, "margin: 16px 0 14px;")}
        ${activeProof && proofLaneMatchScore(system, activeProof) > 0 ? `
            <div class="chip-row" style="margin-bottom: 14px;">
                <span class="chip positive">proof focus: ${escapeHtml(activeProof.title)}</span>
                ${(activeProof.focusCommands || []).slice(0, 3).map(command => `<span class="chip warning">${escapeHtml(command)}</span>`).join("")}
            </div>
        ` : ""}
        <div class="button-row" style="margin-bottom: 14px;">
            <a class="button-link ghost" href="${launchpadInspect}">Inspect in Launchpad</a>
            ${runtimeUrl ? `<a class="button-link secondary" href="${runtimeUrl}">Open runtime lens</a>` : ""}
            ${system.runnable ? `<span class="chip positive">wired launch: ${escapeHtml(system.launchCommand)}</span>` : `<span class="chip warning">manual or library-only</span>`}
        </div>
        <div class="panel-header" style="margin-top: 6px;">
            <div>
                <div class="panel-title">Direct actions</div>
                <div class="panel-subtitle">Role-aware native and runtime commands you can fire from the subsystem lens, with the last result remembered locally for this subsystem.</div>
            </div>
        </div>
        <div class="button-row" style="margin-bottom: 14px;">
            ${actions.map(action => `
                <button class="btn ${action.primary ? "primary" : "secondary"}" type="button" data-system-action="${escapeHtml(action.command)}">
                    ${escapeHtml(action.label)}
                </button>
            `).join("")}
        </div>
        <pre id="systems-focus-output" class="terminal-output" style="margin-bottom: 14px;">${escapeHtml(rememberedOutput)}</pre>
        <pre class="code-block">${escapeHtml(system.route || "No route detail available.")}</pre>
    `;
}

function runtimeFocusHtml(system, panelName, action, runtimeMemory = null) {
    const remembered = runtimeMemory && runtimeMemory.action
        ? `Last remembered runtime result${formatRememberedAt(runtimeMemory.updatedAt)}:\n\n> ${runtimeMemory.action}\n\n${runtimeMemory.output || "(no output)"}`
        : "No remembered runtime result for this subsystem yet.";
    return `
        <div class="card-grid">
            <article class="doc-card">
                <div class="tiny-label">Focused subsystem</div>
                <div class="doc-title">${escapeHtml(system?.name || "runtime focus")}</div>
                <div class="muted-text">${escapeHtml(system?.summary || "Runtime lens opened from AGI-lane navigation.")}</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Focused panel</div>
                <div class="doc-title">${escapeHtml(panelName || "status")}</div>
                <div class="muted-text">The matching runtime panel is highlighted below.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Focused action</div>
                <div class="doc-title">${escapeHtml(action || "native status")}</div>
                <div class="muted-text">If present, the matching quick action is highlighted instead of auto-running.</div>
            </article>
        </div>
        ${system ? renderStrataChips(system.agiStrata, "margin: 16px 0 14px;") : ""}
        <div class="button-row">
            ${system ? `<a class="button-link ghost" href="/systems?focus=${encodeURIComponent(system.id)}&detail=inspect">Open subsystem lens</a>` : ""}
            ${action ? `<a class="button-link secondary" href="/launchpad?cmd=${encodeURIComponent(action)}">Route in Launchpad</a>` : ""}
        </div>
        <pre class="terminal-output" style="margin-top: 14px;">${escapeHtml(remembered)}</pre>
    `;
}

function preferredRuntimeUrl(system) {
    if (!system) {
        return "";
    }
    const action = preferredRuntimeAction(system);
    if (!action) {
        return "";
    }
    const panel = system.organismCore || system.constructionLane ? "actions" : "status";
    return `/runtime?subsystem=${encodeURIComponent(system.id)}&panel=${encodeURIComponent(panel)}&action=${encodeURIComponent(action)}`;
}

function preferredRuntimeAction(system) {
    if (!system) {
        return "";
    }
    if (system.organismCore) {
        return "nexus awaken";
    }
    if (system.constructionLane) {
        return "miving gen 24";
    }
    return "native status";
}

function runtimeActionLabel(command) {
    switch (command) {
        case "native status":
            return "Native status";
        case "engine status":
            return "Engine status";
        case "aether":
            return "Ignite aether";
        case "braid":
            return "Lock braid";
        case "learn PHI_FIELD":
            return "Learn phi_field";
        case "nexus awaken":
            return "Awaken nexus";
        case "gravity start":
            return "Start gravity";
        case "fusion start":
            return "Start fusion";
        case "miving gen 24":
            return "Spawn miving";
        case "build desktop":
            return "Build desktop";
        case "exodus":
            return "Run exodus";
        case "maxheadroom":
            return "Open MaxHeadroom";
        case "native shutdown":
            return "Shutdown runtime";
        default:
            return command;
    }
}

function readSystemFocusMemory(subsystemId) {
    if (!subsystemId) {
        return null;
    }
    return readSystemFocusMemoryMap()[subsystemId] || null;
}

function rememberSystemFocusResult(subsystemId, result) {
    if (!subsystemId || !result) {
        return;
    }
    const memory = readSystemFocusMemoryMap();
    memory[subsystemId] = {
        command: result.command || "",
        output: result.output || "",
        updatedAt: new Date().toISOString()
    };
    writeSystemFocusMemoryMap(memory);
}

function readSystemFocusMemoryMap() {
    try {
        const raw = window.localStorage?.getItem(SYSTEM_FOCUS_MEMORY_KEY);
        if (!raw) {
            return {};
        }
        const parsed = JSON.parse(raw);
        return parsed && typeof parsed === "object" ? parsed : {};
    } catch (error) {
        console.error(error);
        return {};
    }
}

function writeSystemFocusMemoryMap(memory) {
    try {
        window.localStorage?.setItem(SYSTEM_FOCUS_MEMORY_KEY, JSON.stringify(memory));
    } catch (error) {
        console.error(error);
    }
}

function formatRememberedAt(timestamp) {
    if (!timestamp) {
        return "";
    }
    const date = new Date(timestamp);
    if (Number.isNaN(date.getTime())) {
        return "";
    }
    return ` (${date.toLocaleString()})`;
}

function touchKindLabel(kind) {
    return kind === "runtime" ? "runtime path" : "system path";
}

function decayTouchScore(timestamp) {
    if (!timestamp) {
        return 0;
    }
    const updatedAt = Date.parse(timestamp);
    if (Number.isNaN(updatedAt)) {
        return 0;
    }
    const ageMs = Math.max(0, Date.now() - updatedAt);
    return Math.exp((-Math.LN2 * ageMs) / LANE_TOUCH_HALF_LIFE_MS);
}

function classifyTouchState(score) {
    if (score >= 0.55) {
        return "hot";
    }
    if (score >= LANE_TOUCH_WARM_THRESHOLD) {
        return "warm";
    }
    if (score > 0) {
        return "cooling";
    }
    return "cold";
}

function readLaneTouchState(nodes) {
    const systemMemory = readSystemFocusMemoryMap();
    const runtimeMemory = readRuntimeFocusMemoryMap();
    const touchState = new Map();

    for (const node of nodes.values()) {
        const touch = resolveNodeTouch(node, nodes, systemMemory, runtimeMemory);
        if (touch) {
            touchState.set(node.id, touch);
        }
    }

    return touchState;
}

function resolveNodeTouch(node, nodes, systemMemory, runtimeMemory) {
    const subsystemId = touchedSubsystemId(node, nodes);
    if (!subsystemId) {
        return null;
    }

    const systemTouch = systemMemory[subsystemId];
    const runtimeTouch = runtimeMemory[subsystemId];
    if (!systemTouch && !runtimeTouch) {
        return null;
    }

    if (!systemTouch) {
        const score = decayTouchScore(runtimeTouch.updatedAt);
        return {
            kind: "runtime",
            updatedAt: runtimeTouch.updatedAt,
            score,
            state: classifyTouchState(score),
            isWarm: score >= LANE_TOUCH_WARM_THRESHOLD
        };
    }
    if (!runtimeTouch) {
        const score = decayTouchScore(systemTouch.updatedAt);
        return {
            kind: "system",
            updatedAt: systemTouch.updatedAt,
            score,
            state: classifyTouchState(score),
            isWarm: score >= LANE_TOUCH_WARM_THRESHOLD
        };
    }
    const newest = Date.parse(runtimeTouch.updatedAt || "") >= Date.parse(systemTouch.updatedAt || "")
        ? { kind: "runtime", updatedAt: runtimeTouch.updatedAt }
        : { kind: "system", updatedAt: systemTouch.updatedAt };
    const score = decayTouchScore(newest.updatedAt);
    return {
        kind: newest.kind,
        updatedAt: newest.updatedAt,
        score,
        state: classifyTouchState(score),
        isWarm: score >= LANE_TOUCH_WARM_THRESHOLD
    };
}

function touchedSubsystemId(node, nodes) {
    if (!node) {
        return "";
    }
    if (node.type === "subsystem") {
        return node.subsystemId || "";
    }
    if (node.type === "command_surface") {
        return node.primarySubsystem || "";
    }
    if (node.type === "command_section") {
        const surfaceNode = nodes.get(`command-surface:${sanitizeNodeId(node.surfaceId || node.surface || "")}`);
        return surfaceNode?.primarySubsystem || "";
    }
    return "";
}

function sanitizeNodeId(value) {
    return String(value || "").replaceAll(/[^A-Za-z0-9_]/g, "_");
}

function readRuntimeFocusMemory(subsystemId) {
    if (!subsystemId) {
        return null;
    }
    return readRuntimeFocusMemoryMap()[subsystemId] || null;
}

function rememberRuntimeFocusResult(subsystemId, panel, result) {
    if (!subsystemId || !result) {
        return;
    }
    const memory = readRuntimeFocusMemoryMap();
    memory[subsystemId] = {
        panel: panel || "actions",
        action: result.command || "",
        output: result.output || "",
        updatedAt: new Date().toISOString()
    };
    writeRuntimeFocusMemoryMap(memory);
}

function readRuntimeFocusMemoryMap() {
    try {
        const raw = window.localStorage?.getItem(RUNTIME_FOCUS_MEMORY_KEY);
        if (!raw) {
            return {};
        }
        const parsed = JSON.parse(raw);
        return parsed && typeof parsed === "object" ? parsed : {};
    } catch (error) {
        console.error(error);
        return {};
    }
}

function writeRuntimeFocusMemoryMap(memory) {
    try {
        window.localStorage?.setItem(RUNTIME_FOCUS_MEMORY_KEY, JSON.stringify(memory));
    } catch (error) {
        console.error(error);
    }
}

function subsystemActions(system, activeProof = null) {
    const actions = [];
    const primary = preferredProofAction(system, activeProof) || preferredRuntimeAction(system);
    actions.push({ label: "Run preferred action", command: primary, primary: true });
    actions.push({ label: "Inspect subsystem", command: `system ${system.id}`, primary: false });

    if (activeProof) {
        for (const command of activeProof.focusCommands || []) {
            if (command !== primary) {
                actions.push({ label: `Proof lane: ${command}`, command, primary: false });
            }
        }
    }

    if (system.cognitionCore && primary !== "learn PHI_FIELD") {
        actions.push({ label: "Learn PHI field", command: "learn PHI_FIELD", primary: false });
    }
    if (system.organismCore && primary !== "nexus awaken") {
        actions.push({ label: "Awaken nexus", command: "nexus awaken", primary: false });
    }
    if (system.constructionLane && primary !== "miving gen 24") {
        actions.push({ label: "Spawn miving", command: "miving gen 24", primary: false });
    }
    if ((system.proofLane || system.kernelDiscipline) && primary !== "native status") {
        actions.push({ label: "Check native status", command: "native status", primary: false });
    }

    const unique = new Set();
    return actions.filter(action => {
        if (unique.has(action.command)) {
            return false;
        }
        unique.add(action.command);
        return true;
    }).slice(0, 4);
}

function buildLaneGroups(legend, clusters, nodes, touchState, activeMath = null, activeProof = null) {
    const legendMap = new Map((legend || []).map(item => [item.stratum, item]));
    const groups = [];
    const added = new Set();

    for (const item of legend || []) {
        const scoped = clusters.filter(cluster => cluster.stratum === item.stratum);
        groups.push(laneGroup(item, scoped, nodes, touchState, activeMath, activeProof));
        added.add(item.stratum);
    }

    for (const cluster of clusters || []) {
        if (added.has(cluster.stratum)) {
            continue;
        }
        groups.push(laneGroup(legendMap.get(cluster.stratum) || {
            stratum: cluster.stratum,
            label: formatStratum(cluster.stratum),
            color: "#94a3b8",
            accent: "#475569"
        }, clusters.filter(entry => entry.stratum === cluster.stratum), nodes, touchState, activeMath, activeProof));
        added.add(cluster.stratum);
    }

    const filtersActive = !!activeMath || !!activeProof;
    return groups
        .filter(group => group.totalCount > 0)
        .filter(group => !filtersActive || group.mathMatched || group.proofMatched || group.warmCount > 0)
        .sort((left, right) => {
            const proofDelta = Number(!!right.proofMatched) - Number(!!left.proofMatched);
            if (proofDelta !== 0) {
                return proofDelta;
            }
            const mathDelta = Number(!!right.mathMatched) - Number(!!left.mathMatched);
            if (mathDelta !== 0) {
                return mathDelta;
            }
            return (right.hottest?.touch?.score || 0) - (left.hottest?.touch?.score || 0);
        });
}

function laneGroup(legendItem, scopedClusters, nodes, touchState, activeMath = null, activeProof = null) {
    const byScope = { subsystem: [], surface: [], section: [] };
    for (const cluster of scopedClusters) {
        const resolved = (cluster.nodeIds || [])
            .map(id => nodes.get(id))
            .filter(Boolean)
            .map(node => ({
                id: node.id,
                label: node.label,
                type: node.type,
                summary: node.summary || node.description || node.theme || "",
                jumpUrl: node.jumpUrl || "",
                routeUrl: node.routeUrl || "",
                routeCommand: node.routeCommand || "",
                inspectCommand: node.inspectCommand || "",
                agiStrata: node.agiStrata || [],
                primaryAgiStratum: node.primaryAgiStratum || "",
                touch: touchState.get(node.id) || null,
                mathMatched: mathFamilyMatchesNode(node, activeMath),
                proofMatched: proofLaneMatchesNode(node, activeProof)
            }));
        if (!byScope[cluster.scope]) {
            byScope[cluster.scope] = [];
        }
        byScope[cluster.scope].push(...resolved);
    }

    const subsystemItems = sortLaneItems(uniqueById(byScope.subsystem || []));
    const surfaceItems = sortLaneItems(uniqueById(byScope.surface || []));
    const sectionItems = sortLaneItems(uniqueById(byScope.section || []));
    const allItems = [
        ...subsystemItems,
        ...surfaceItems,
        ...sectionItems
    ];
    const visible = filterLaneItemsByFocus(allItems, activeMath, activeProof);
    const hottest = allItems
        .filter(item => item.touch && item.touch.updatedAt)
        .sort((left, right) => (right.touch.score || 0) - (left.touch.score || 0))[0] || null;

    return {
        stratum: legendItem.stratum,
        label: legendItem.label || formatStratum(legendItem.stratum),
        color: legendItem.color || "#94a3b8",
        accent: legendItem.accent || "#475569",
        subsystem: subsystemItems,
        surface: surfaceItems,
        section: sectionItems,
        totalCount: (byScope.subsystem || []).length + (byScope.surface || []).length + (byScope.section || []).length,
        warmCount: allItems.filter(item => item.touch?.isWarm).length,
        visibleCount: visible.items.length,
        hiddenCount: visible.hidden,
        proofMatched: !!activeProof && ((activeProof.agiStrata || []).includes(legendItem.stratum) || allItems.some(item => item.proofMatched)),
        mathMatched: !!activeMath && (activeMath.strata.includes(legendItem.stratum) || allItems.some(item => item.mathMatched)),
        hottest
    };
}

function sortLaneItems(items) {
    return [...items].sort((left, right) => {
        const proofDelta = Number(!!right.proofMatched) - Number(!!left.proofMatched);
        if (proofDelta !== 0) {
            return proofDelta;
        }
        const mathDelta = Number(!!right.mathMatched) - Number(!!left.mathMatched);
        if (mathDelta !== 0) {
            return mathDelta;
        }
        const rightScore = right.touch?.score || 0;
        const leftScore = left.touch?.score || 0;
        if (rightScore !== leftScore) {
            return rightScore - leftScore;
        }
        const rightTime = Date.parse(right.touch?.updatedAt || "") || 0;
        const leftTime = Date.parse(left.touch?.updatedAt || "") || 0;
        if (rightTime !== leftTime) {
            return rightTime - leftTime;
        }
        return (left.label || left.id).localeCompare(right.label || right.id);
    });
}

function graphSemanticsHtml(graph, semantics, legend, grouped, activeMath = null, activeProof = null, proofLanes = []) {
    const summary = graph.summary || {};
    return `
        <div class="card-grid">
            <article class="doc-card">
                <div class="tiny-label">Renderer layout</div>
                <div class="doc-title">${escapeHtml(semantics.layout || "unspecified")}</div>
                <div class="muted-text">Mermaid direction ${escapeHtml(semantics.mermaidDirection || "n/a")}.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Active AGI lanes</div>
                <div class="doc-title">${escapeHtml(String(legend.length))}</div>
                <div class="muted-text">${escapeHtml(String(grouped.length))} visible lane groups rendered from the current operator record.</div>
            </article>
            <article class="doc-card">
                <div class="tiny-label">Cluster groups</div>
                <div class="doc-title">${escapeHtml(String((semantics.clusters || []).length))}</div>
                <div class="muted-text">${escapeHtml(String(summary.commandSurfaces || 0))} surfaces and ${escapeHtml(String(summary.commandSections || 0))} sections flow through the exported graph.</div>
            </article>
        </div>
        ${proofLanes.length ? knowledgeProofFocusControlsHtml(proofLanes, activeProof) : ""}
        ${activeProof ? `<div class="chip-row" style="margin: 16px 0 14px;"><span class="chip positive">Living proof focus: ${escapeHtml(activeProof.title)}</span><a class="button-link ghost proof-focus-match" href="/knowledge?clearProof=1">Clear proof focus</a></div>` : ""}
        ${activeMath ? `<div class="chip-row" style="margin: 16px 0 14px;"><span class="chip positive">Math focus: ${escapeHtml(activeMath.label)}</span></div>` : ""}
        <div class="chip-row" style="margin: 16px 0 14px;">
            ${legend.map(item => `
                <span class="chip lane-chip" style="--lane-fill:${escapeHtml(item.color || "#94a3b8")};--lane-accent:${escapeHtml(item.accent || "#475569")}">
                    ${escapeHtml(item.label || formatStratum(item.stratum))}
                </span>
            `).join("")}
        </div>
        <p class="muted-text"><strong>Grouping:</strong> ${escapeHtml((semantics.grouping || []).join(" -> ") || "not declared")}</p>
    `;
}

function knowledgeProofFocusControlsHtml(proofLanes, activeProof = null) {
    return `
        <article class="doc-card" style="margin: 18px 0 14px;">
            <div class="tiny-label">Proof lane lens</div>
            <div class="doc-title">${escapeHtml(activeProof ? activeProof.title : "Select a living proof lane")}</div>
            <div class="muted-text">Apply Morse, Ouroboros, proof harnesses, organism validators, or substrate transduction directly inside the Knowledge graph so matching AGI lanes lift and cold lanes fall away.</div>
            <div class="button-row" style="margin-top: 14px;">
                ${proofLanes.map(lane => `
                    <button class="btn ${activeProof?.id === lane.id ? "primary proof-focus-match" : "secondary"}" type="button" data-knowledge-proof-focus="${escapeHtml(lane.id)}">${escapeHtml(lane.title)}</button>
                `).join("")}
                ${activeProof ? `<button class="btn ghost proof-focus-match" type="button" data-knowledge-proof-clear>Clear proof focus</button>` : ""}
            </div>
        </article>
    `;
}

function laneCardHtml(group, activeMath = null, activeProof = null) {
    return `
        <article class="lane-card ${group.hottest?.isWarm ? "recent-lane" : ""} ${group.mathMatched ? "math-focus-match" : ""} ${group.proofMatched ? "proof-focus-match" : ""}" style="--lane-fill:${escapeHtml(group.color)};--lane-accent:${escapeHtml(group.accent)}">
            <header class="lane-card-header">
                <div>
                    <div class="tiny-label">AGI lane</div>
                    <h3 class="system-title">${escapeHtml(group.label)}</h3>
                    ${group.hottest ? `<div class="muted-text">Latest touch: ${escapeHtml(group.hottest.label)} via ${escapeHtml(touchKindLabel(group.hottest.touch.kind))}${escapeHtml(formatRememberedAt(group.hottest.touch.updatedAt))}</div>` : ""}
                </div>
                <div class="chip-row">
                    <span class="chip lane-chip">${group.totalCount} mapped nodes</span>
                    ${(activeMath || activeProof) ? `<span class="chip ${group.hiddenCount ? "warning" : "positive"}">${group.visibleCount} visible now</span>` : ""}
                    ${group.proofMatched ? `<span class="chip positive">proof focus</span>` : ""}
                    ${group.warmCount ? `<span class="chip positive">${group.warmCount} warm</span>` : ""}
                </div>
            </header>
            <div class="lane-scope-grid">
                ${laneScopeHtml("Subsystems", group.subsystem, activeMath, activeProof)}
                ${laneScopeHtml("Surfaces", group.surface, activeMath, activeProof)}
                ${laneScopeHtml("Sections", group.section, activeMath, activeProof)}
            </div>
        </article>
    `;
}

function filterLaneItemsByFocus(items, activeMath = null, activeProof = null) {
    if (!items || !items.length) {
        return { items: [], hidden: 0 };
    }
    if (!activeMath && !activeProof) {
        return { items, hidden: 0 };
    }
    const focused = items.filter(item => {
        if (item.touch?.isWarm) {
            return true;
        }
        if (activeMath && item.mathMatched) {
            return true;
        }
        if (activeProof && item.proofMatched) {
            return true;
        }
        return false;
    });
    if (!focused.length) {
        return { items, hidden: 0 };
    }
    return {
        items: focused,
        hidden: Math.max(items.length - focused.length, 0)
    };
}

function laneScopeHtml(title, items, activeMath = null, activeProof = null) {
    if (!items || !items.length) {
        return `
            <section class="lane-scope">
                <div class="tiny-label">${escapeHtml(title)}</div>
                <div class="muted-text">No mapped nodes in this scope.</div>
            </section>
        `;
    }

    const filtered = filterLaneItemsByFocus(items, activeMath, activeProof);
    const visibleItems = filtered.items.slice(0, 8);

    return `
        <section class="lane-scope">
                <div class="tiny-label">${escapeHtml(title)}</div>
                <div class="lane-node-list">
                    ${visibleItems.map(item => `
                    <div class="lane-node ${item.touch?.isWarm ? "recent-node" : ""} ${item.mathMatched ? "math-focus-match" : ""} ${item.proofMatched ? "proof-focus-match" : ""}">
                        ${item.jumpUrl
                            ? `<a class="lane-link" href="${item.jumpUrl}"><strong>${escapeHtml(item.label || item.id)}</strong></a>`
                            : `<strong>${escapeHtml(item.label || item.id)}</strong>`}
                        ${item.summary ? `<span>${escapeHtml(item.summary)}</span>` : ""}
                        ${item.proofMatched ? `<div class="chip-row" style="margin-top: 4px;"><span class="chip positive">proof lane match</span></div>` : ""}
                        ${item.touch ? `<div class="chip-row" style="margin-top: 4px;"><span class="chip ${item.touch.isWarm ? (item.touch.kind === "runtime" ? "positive" : "warning") : ""}">${escapeHtml(item.touch.isWarm ? `${item.touch.kind} warm` : `${item.touch.kind} last active`)}${escapeHtml(formatRememberedAt(item.touch.updatedAt))}</span></div>` : ""}
                        ${item.routeUrl ? `<a class="inline-link" href="${item.routeUrl}">Route in Launchpad</a>` : ""}
                        ${item.routeCommand ? `<span class="footer-note" style="margin-top: 0;">Preload: ${escapeHtml(item.routeCommand)}</span>` : ""}
                    </div>
                `).join("")}
                ${filtered.hidden ? `<div class="muted-text">${filtered.hidden} hidden by current focus</div>` : ""}
                ${filtered.items.length > 8 ? `<div class="muted-text">+ ${filtered.items.length - 8} more</div>` : ""}
            </div>
        </section>
    `;
}

function uniqueById(items) {
    const seen = new Set();
    return items.filter(item => {
        if (seen.has(item.id)) {
            return false;
        }
        seen.add(item.id);
        return true;
    });
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}

function launchpadHref(command) {
    return `/launchpad?cmd=${encodeURIComponent(command)}`;
}

function replaceSessionReviewQuery(id) {
    const url = new URL(window.location.href);
    if (id) {
        url.searchParams.set("review", id);
    } else {
        url.searchParams.delete("review");
    }
    window.history.replaceState({}, "", url);
}

function snippetLines(text, limit) {
    if (!text) {
        return "";
    }
    const lines = String(text).split(/\r?\n/);
    if (lines.length <= limit) {
        return lines.join("\n");
    }
    return `${lines.slice(0, limit).join("\n")}\n... (${lines.length - limit} more lines)`;
}

function priorityCount(items, name) {
    const hit = items.find(item => item.name === name);
    return hit ? hit.count : 0;
}

function focusCard(container, dataKey, focusId) {
    if (!container || !focusId) {
        return;
    }
    const cards = Array.from(container.children || []);
    const target = cards.find(card => card.dataset && card.dataset[dataKey] === focusId);
    if (!target) {
        return;
    }
    cards.forEach(card => card.classList.toggle("focused-card", card === target));
    target.scrollIntoView({ behavior: "smooth", block: "center" });
}

function focusSurfaceSection(container, focusSurface, focusSection) {
    if (!container || !focusSurface || !focusSection) {
        return;
    }
    const surface = container.querySelector(`[data-surface-id="${cssEscape(focusSurface)}"]`);
    if (!surface) {
        return;
    }
    const section = surface.querySelector(`[data-section-id="${cssEscape(focusSection)}"]`);
    if (!section) {
        return;
    }
    section.classList.add("focused-group");
    section.open = true;
    section.scrollIntoView({ behavior: "smooth", block: "center" });
}

function focusRuntimePanel(panelName) {
    if (!panelName) {
        return;
    }
    const panels = Array.from(document.querySelectorAll("[data-runtime-panel]"));
    const target = panels.find(panel => panel.dataset.runtimePanel === panelName);
    if (!target) {
        return;
    }
    panels.forEach(panel => panel.classList.toggle("focused-card", panel === target));
    target.scrollIntoView({ behavior: "smooth", block: "center" });
}

function renderStrataChips(strata, style = "") {
    if (!strata || !strata.length) {
        return "";
    }
    return `
        <div class="chip-row" style="${style}">
            ${strata.map(stratum => `<span class="chip ${stratumTone(stratum)}">${escapeHtml(formatStratum(stratum))}</span>`).join("")}
        </div>
    `;
}

function strataCountChip(item) {
    return `<span class="chip ${stratumTone(item.name)}">${escapeHtml(formatStratum(item.name))}: ${item.count}</span>`;
}

function booleanRoleChip(active, label) {
    if (!active) {
        return "";
    }
    return `<span class="chip positive">${escapeHtml(label)}</span>`;
}

function stratumTone(stratum) {
    switch (stratum) {
        case "governing-core":
        case "proof-lane":
        case "kernel-discipline":
            return "positive";
        case "cognition-core":
        case "organism-core":
        case "absorption-core":
        case "construction-lane":
        case "model-substrate-lane":
            return "warning";
        case "bare-metal-substrate-lane":
        case "adjacent-historical-core":
            return "";
        default:
            return "";
    }
}

function formatStratum(stratum) {
    switch (stratum) {
        case "governing-core":
            return "governing core";
        case "cognition-core":
            return "cognition core";
        case "organism-core":
            return "organism core";
        case "absorption-core":
            return "absorption core";
        case "proof-lane":
            return "proof lane";
        case "model-substrate-lane":
            return "model substrate";
        case "bare-metal-substrate-lane":
            return "bare-metal substrate";
        case "adjacent-historical-core":
            return "historical core";
        case "operator-shell":
            return "operator shell";
        case "kernel-discipline":
            return "kernel discipline";
        case "construction-lane":
            return "construction lane";
        default:
            return String(stratum || "").replaceAll("-", " ");
    }
}

function cssEscape(value) {
    if (window.CSS && typeof window.CSS.escape === "function") {
        return window.CSS.escape(value);
    }
    return String(value).replaceAll('"', '\\"');
}

function handleError(error) {
    console.error(error);
}
