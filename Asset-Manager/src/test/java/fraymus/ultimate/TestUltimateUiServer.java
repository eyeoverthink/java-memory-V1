package fraymus.ultimate;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestUltimateUiServer {

    @TempDir
    Path tempDir;

    @Test
    void servesBrowserUiAndApiFromUnifiedWorkspace() throws Exception {
        Path repoRoot = tempDir.resolve("Java-Memory");
        Path assetManager = repoRoot.resolve("Asset-Manager");
        Path universal = assetManager.resolve("fraymus-universal");
        Path aiCore = repoRoot.resolve("fraymus-ai-core");

        Files.createDirectories(assetManager.resolve("src/main/java/fraymus"));
        Files.createDirectories(assetManager.resolve("src/main/java/fraymus/core"));
        Files.createDirectories(assetManager.resolve("src/main/java/fraymus/senses"));
        Files.createDirectories(assetManager.resolve("src/main/java/fraymus/ultimate"));
        Files.createDirectories(tempDir.resolve(".northstar-kernel/generated/substrates"));
        Files.createDirectories(universal.resolve("src/main/java/fraynix"));
        Files.createDirectories(aiCore.resolve("src/main/java/io/fraymus/ai"));

        Files.writeString(repoRoot.resolve("README.md"), "# Fraymus Nexus\nPhi memory runtime");
        Files.writeString(tempDir.resolve("FRAYNIX_LIVE_EXECUTION_PROOF_2026-04-21.md"), "# Live proof\nFraynix moved.\n");
        Files.writeString(assetManager.resolve("build.gradle"), "plugins { id 'java' }");
        Files.writeString(assetManager.resolve("HeadroomNode.py"), "print('headroom fixture')\n");
        Files.writeString(assetManager.resolve("brain.txt"), "Willow and Transmudder are living operator lanes.");
        Files.write(assetManager.resolve("test_image.png"), new byte[]{(byte)137, 80, 78, 71, 13, 10, 26, 10});
        Files.write(assetManager.resolve("test_sound.wav"), new byte[]{82, 73, 70, 70, 1, 0, 0, 0, 87, 65, 86, 69});
        Files.writeString(assetManager.resolve("src/main/java/fraymus/FraymusConvergence.java"), "class FraymusConvergence {}");
        Files.writeString(assetManager.resolve("src/main/java/fraymus/FraymusUI.java"), "class FraymusUI {}");
        Files.writeString(assetManager.resolve("src/main/java/fraymus/MaxHeadroomLive.java"), "class MaxHeadroomLive {}");
        Files.writeString(assetManager.resolve("src/main/java/fraymus/FraynixBoot.java"), sampleFraynixBootSource());
        Files.writeString(assetManager.resolve("src/main/java/fraymus/core/FraymusNexus.java"), "class FraymusNexus {}");
        Files.writeString(assetManager.resolve("src/main/java/fraymus/senses/HeadroomInterface.java"), "class HeadroomInterface {}");
        Files.writeString(assetManager.resolve("src/main/java/fraymus/ultimate/UltimateSystem.java"), "class UltimateSystem {}");
        Files.writeString(tempDir.resolve(".northstar-kernel/generated/substrates/MODEL_SUBSTRATE_MANIFEST.tsv"),
            "modelId\tfamily\tlicenseId\tprovenance\tsourceUri\tbaseModel\tderivedFrom\tallowedOperations\tsourcePath\tsizeBytes\tattributes\n"
                + "fixture-model\tgpt2_bundle\tunknown\thuggingface_cache_bundle\thuggingface://fixture\tfixture\tfixture\tinspect,abstract,license_review_required\tC:/fixture\t1\tlicense.id=unknown\n");
        Files.writeString(tempDir.resolve(".northstar-kernel/generated/substrates/LOCAL_MODEL_RUNTIME_INVENTORY.md"),
            "# Local Model Runtime Inventory\n\n- `fixture-model` backend=`python_transformers` available=`true` target=`C:/fixture`\n");
        Files.writeString(universal.resolve("src/main/java/fraynix/FraynixOS.java"), "class FraynixOS {}");
        Files.writeString(aiCore.resolve("src/main/java/io/fraymus/ai/FraymusAI.java"), "class FraymusAI {}");

        UltimateUiServer server = new UltimateUiServer(assetManager, 0);
        HttpClient client = HttpClient.newHttpClient();

        try {
            int port = server.start();
            HttpResponse<String> overview = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/overview")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> operator = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/operator")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> systemsPage = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/systems")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> companionApi = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/companion")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> runtimeApi = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/runtime")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> companionChat = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/companion/chat"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"prompt\":\"Use the local library.\"}"))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> companionProofChat = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/companion/chat"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"prompt\":\"What are the strongest living proof lanes active right now?\"}"))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> companionRouterChat = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/companion/chat"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                        "{\"prompt\":\"connect all systems brains intellect and digest "
                            + assetManager.resolve("brain.txt").toString().replace("\\", "/")
                            + " with transmudder willow babel native kernel\"}"
                    ))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> companionRunChat = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/companion/chat"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"prompt\":\"run native status\"}"))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> companionBabelInjectionChat = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/companion/chat"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"prompt\":\"run babel status && whoami\"}"))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> companionDream = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/companion/dream"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"prompt\":\"Manifest a phi-harmonic dreamscape.\",\"previewOnly\":true}"))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> companionHeadroom = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/companion/headroom"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"action\":\"broadcast\",\"text\":\"Signal test\",\"previewOnly\":true}"))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> companionPage = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/companion")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> headroomPage = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/headroom")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> sessionApi = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/session")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> systemStateApi = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/system-state?consumer=session")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> sessionPage = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/session")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> surfacesPage = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/surfaces")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> knowledgePage = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/knowledge")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> runtimePage = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/runtime")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> launchpadPage = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/launchpad")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> graphArtifact = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/artifacts/ultimate-knowledge-graph.json")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> mathArtifact = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/artifacts/fraynix-math-canon.md")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> appJs = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/assets/app.js")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> execute = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/execute"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"command\":\"native status\"}"))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            HttpResponse<String> routedExecute = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/execute"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"command\":\"What are the strongest living proof lanes active right now?\"}"))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, overview.statusCode());
            assertEquals(200, operator.statusCode());
            assertEquals(200, systemsPage.statusCode());
            assertEquals(200, companionApi.statusCode());
            assertEquals(200, runtimeApi.statusCode());
            assertEquals(200, companionChat.statusCode());
            assertEquals(200, companionProofChat.statusCode());
            assertEquals(200, companionRouterChat.statusCode());
            assertEquals(200, companionRunChat.statusCode());
            assertEquals(200, companionBabelInjectionChat.statusCode());
            assertEquals(200, companionDream.statusCode());
            assertEquals(200, companionHeadroom.statusCode());
            assertEquals(200, companionPage.statusCode());
            assertEquals(200, headroomPage.statusCode());
            assertEquals(200, sessionApi.statusCode());
            assertEquals(200, systemStateApi.statusCode());
            assertEquals(200, sessionPage.statusCode());
            assertEquals(200, surfacesPage.statusCode());
            assertEquals(200, knowledgePage.statusCode());
            assertEquals(200, runtimePage.statusCode());
            assertEquals(200, launchpadPage.statusCode());
            assertEquals(200, graphArtifact.statusCode());
            assertEquals(200, mathArtifact.statusCode());
            assertEquals(200, appJs.statusCode());
            assertEquals(200, execute.statusCode());
            assertEquals(200, routedExecute.statusCode());
            assertTrue(overview.body().contains("\"productName\": \"Fraymus Nexus // Fraynix Offline AGI Operator Surface\""));
            assertTrue(overview.body().contains("\"subsystemAgiStrata\""));
            assertTrue(overview.body().contains("\"mathUrl\": \"/artifacts/fraynix-math-canon.md\""));
            assertTrue(overview.body().contains("\"axiomLedgerUrl\": \"/artifacts/fraynix-axiom-ledger.md\""));
            assertTrue(overview.body().contains("\"truthRegistryUrl\": \"/artifacts/fraynix-truth-registry.json\""));
            assertTrue(overview.body().contains("\"continuityMemoryUrl\": \"/artifacts/fraynix-continuity-memory.md\""));
            assertTrue(overview.body().contains("\"tagline\": \"Kernel, boot, factory, broadcast, and native runtime surface for the Fraynix offline AGI organism."));
            assertTrue(overview.body().contains("\"title\": \"Session\""));
            assertTrue(overview.body().contains("\"title\": \"Companion\""));
            assertTrue(overview.body().contains("\"title\": \"Headroom\""));
            assertTrue(overview.body().contains("\"legacySpine\""));
            assertTrue(overview.body().contains("\"legacyCommandSpine\""));
            assertTrue(overview.body().contains("\"commandDecks\""));
            assertTrue(overview.body().contains("\"launchpad\""));
            assertTrue(overview.body().contains("\"session\""));
            assertTrue(overview.body().contains("\"runtime\""));
            assertTrue(overview.body().contains("\"broadcast\""));
            assertTrue(overview.body().contains("\"factory\""));
            assertTrue(overview.body().contains("\"FraynixBoot\""));
            assertTrue(overview.body().contains("\"FraymusNexus\""));
            assertTrue(overview.body().contains("\"HeadroomInterface\""));
            assertTrue(overview.body().contains("\"FraymusUI\""));
            assertTrue(overview.body().contains("\"MaxHeadroomLive\""));
            assertTrue(overview.body().contains("\"availableCount\": 5"));
            assertTrue(overview.body().contains("\"summary\": \"The original operator spine is boot, factory, broadcast, cockpit, and live signal control. The browser shell should expose those lanes first."));
            assertTrue(operator.body().contains("\"authoritativeShell\": \"ultimate-ui\""));
            assertTrue(operator.body().contains("\"kernelPreferredSurfaceCount\": 1"));
            assertTrue(operator.body().contains("\"bootTreeBranchCount\": 3"));
            assertTrue(operator.body().contains("\"livingProofLaneCount\": 5"));
            assertTrue(operator.body().contains("\"livingProofLanes\""));
            assertTrue(operator.body().contains("\"title\": \"Morse\""));
            assertTrue(operator.body().contains("\"title\": \"Ouroboros\""));
            assertTrue(operator.body().contains("willow proof 8"));
            assertTrue(operator.body().contains("transmudder proof <path>"));
            assertTrue(operator.body().contains("\"surfaceAgiStrataCounts\""));
            assertTrue(operator.body().contains("\"livingProofLaneAgiStrataCounts\""));
            assertTrue(operator.body().contains("\"governingCore\": true"));
            assertTrue(operator.body().contains("\"operatorShell\": true"));
            assertTrue(overview.body().contains("\"markdownUrl\": \"/artifacts/ultimate-architecture.md\""));
            assertTrue(systemsPage.body().contains("Read the machine from its organs."));
            assertTrue(systemsPage.body().contains("Operator origin"));
            assertTrue(systemsPage.body().contains("Focused subsystem lens"));
            assertTrue(systemsPage.body().contains("Math family focus"));
            assertTrue(systemsPage.body().contains("Living proof focus"));
            assertTrue(sessionApi.body().contains("\"demoCommand\": \"demo guided\""));
            assertTrue(sessionApi.body().contains("\"substrateLedger\""));
            assertTrue(sessionApi.body().contains("\"reviewCommandPattern\": \"selfbuild review-promote file <path-fragment> :: <goal>\""));
            assertTrue(sessionApi.body().contains("\"runtimeBackends\""));
            assertTrue(sessionApi.body().contains("\"proofs\""));
            assertTrue(sessionApi.body().contains("\"href\": \"/artifacts/_session/"));
            assertTrue(sessionApi.body().contains("\"commandDeck\""));
            assertTrue(sessionApi.body().contains("\"Build desktop\""));
            assertTrue(sessionApi.body().contains("\"Exodus\""));
            assertTrue(sessionApi.body().contains("\"Proof lanes\""));
            assertTrue(sessionApi.body().contains("\"systemState\""));
            assertTrue(sessionApi.body().contains("\"subsystemHealth\""));
            assertTrue(sessionApi.body().contains("\"route\": \"SYSTEM_STATE\""));
            assertTrue(sessionApi.body().contains("\"connectomePlan\""));
            assertTrue(sessionApi.body().contains("\"weeksReady\""));
            assertTrue(sessionApi.body().contains("\"weeksTotal\""));
            assertTrue(sessionApi.body().contains("WEEK1_NERVOUS_SYSTEM_COMPLETE.md"));
            assertTrue(sessionApi.body().contains("WEEK4_INTEGRATION_OPTIMIZATION_COMPLETE.md"));
            assertTrue(sessionApi.body().contains("BRAIN_SYSTEMS_REFERENCE.md"));
            assertTrue(systemStateApi.body().contains("\"fetchedAt\""));
            assertTrue(systemStateApi.body().contains("\"staleAfterMs\""));
            assertTrue(systemStateApi.body().contains("\"cacheState\""));
            assertTrue(systemStateApi.body().contains("\"systemState\""));
            assertTrue(systemStateApi.body().contains("\"weekContractStatus\""));
            assertTrue(systemStateApi.body().contains("\"weekContractWeeksReady\""));
            assertTrue(systemStateApi.body().contains("\"weekContractWeeksTotal\""));
            assertTrue(systemStateApi.body().contains("\"weekContractSource\""));
            assertTrue(companionApi.body().contains("\"title\": \"Fraynix Companion\""));
            assertTrue(companionApi.body().contains("\"visualLibrary\""));
            assertTrue(companionApi.body().contains("\"audioLibrary\""));
            assertTrue(companionApi.body().contains("\"chatAvailable\""));
            assertTrue(companionApi.body().contains("\"dreamOutputCount\""));
            assertTrue(companionApi.body().contains("\"headroomAvailable\""));
            assertTrue(companionApi.body().contains("\"headroom\""));
            assertTrue(companionApi.body().contains("\"commandDeck\""));
            assertTrue(companionApi.body().contains("\"systemState\""));
            assertTrue(companionApi.body().contains("\"subsystemHealth\""));
            assertTrue(companionApi.body().contains("\"route\": \"SYSTEM_STATE\""));
            assertTrue(runtimeApi.body().contains("\"commandDeck\""));
            assertTrue(runtimeApi.body().contains("\"Build desktop\""));
            assertTrue(runtimeApi.body().contains("\"Exodus\""));
            assertTrue(runtimeApi.body().contains("\"MaxHeadroom\""));
            assertTrue(companionChat.body().contains("\"reply\""));
            assertTrue(companionChat.body().contains("\"transcript\""));
            assertTrue(companionProofChat.body().contains("Grounded operator answer from runtime manifest"));
            assertTrue(companionProofChat.body().contains("Strongest living proof lanes active in this shell right now"));
            assertTrue(companionProofChat.body().contains("\"reply\""));
            assertTrue(companionRouterChat.body().contains("AI PROMPT ORCHESTRATION"));
            assertTrue(companionRouterChat.body().contains("Action: ai-unison-plan"));
            assertTrue(companionRouterChat.body().contains("Action: ai-unison-run"));
            assertTrue(companionRouterChat.body().contains("Action: transmudder-proof"));
            assertTrue(companionRouterChat.body().contains("Action: willow"));
            assertTrue(companionRouterChat.body().contains("Action: native-status"));
            assertTrue(companionRouterChat.body().contains("Action: system-bridge"));
            assertTrue(companionRunChat.body().contains("AI RUN EXECUTION"));
            assertTrue(companionRunChat.body().contains("Command: native status"));
            assertTrue(companionRunChat.body().contains("fraynix.ai-run.v1"));
            assertTrue(companionRunChat.body().contains("fraynix.ai-run.policy.v2"));
            assertTrue(companionRunChat.body().contains("executionId"));
            assertTrue(companionRunChat.body().contains("airun-"));
            assertTrue(companionRunChat.body().contains("timeoutSeconds"));
            assertTrue(companionRunChat.body().contains("outputSha256"));
            assertTrue(companionRunChat.body().contains("ULTIMATE IMMORTAL KERNEL"));
            assertTrue(companionBabelInjectionChat.body().contains("AI RUN EXECUTION"));
            assertTrue(companionBabelInjectionChat.body().toLowerCase().contains("blocked"));
            assertTrue(companionBabelInjectionChat.body().contains("POLICY_BABEL_PAYLOAD_BLOCKED"));
            assertTrue(companionDream.body().contains("\"status\": \"preview\""));
            assertTrue(companionDream.body().contains("\"state\""));
            assertTrue(companionHeadroom.body().contains("\"status\": \"preview\""));
            assertTrue(companionHeadroom.body().contains("\"action\": \"broadcast\""));
            assertTrue(companionPage.body().contains("Ground the organism before it broadcasts."));
            assertTrue(companionPage.body().contains("Companion spine"));
            assertTrue(companionPage.body().contains("Headroom handoff"));
            assertTrue(companionPage.body().contains("Open Headroom"));
            assertTrue(companionPage.body().contains("Local visual library"));
            assertTrue(companionPage.body().contains("Local audio library"));
            assertTrue(companionPage.body().contains("Dream visual"));
            assertTrue(companionPage.body().contains("Grounded system state contract"));
            assertTrue(headroomPage.body().contains("Boot face for the offline organism."));
            assertTrue(headroomPage.body().contains("Kernel spine"));
            assertTrue(headroomPage.body().contains("Headroom signal"));
            assertTrue(headroomPage.body().contains("Companion thread"));
            assertTrue(headroomPage.body().contains("Signal presence"));
            assertTrue(headroomPage.body().contains("Mode transition"));
            assertTrue(sessionPage.body().contains("Operator bench for the booted machine."));
            assertTrue(sessionPage.body().contains("Session shell spine"));
            assertTrue(sessionPage.body().contains("Operator command lane"));
            assertTrue(sessionPage.body().contains("id=\"session-quick-fills\""));
            assertTrue(sessionPage.body().contains("build desktop"));
            assertTrue(sessionPage.body().contains("exodus"));
            assertTrue(sessionPage.body().contains("maxheadroom"));
            assertTrue(sessionPage.body().contains("Run guided demo"));
            assertTrue(sessionPage.body().contains("Review queue"));
            assertTrue(sessionPage.body().contains("Review lens"));
            assertTrue(sessionPage.body().contains("Read the proposed patch and proof summary before approving or rejecting."));
            assertTrue(sessionPage.body().contains("Substrate ledger"));
            assertTrue(sessionPage.body().contains("Grounded system state contract"));
            assertTrue(sessionPage.body().contains("7-day connectome contract"));
            assertTrue(sessionPage.body().contains("Week 1-4 nervous/sensory/memory/integration commitments, pinned to live source and tests."));
            assertTrue(surfacesPage.body().contains("The menu is the organism's language."));
            assertTrue(surfacesPage.body().contains("Primary command spine"));
            assertTrue(surfacesPage.body().contains("Operator authority"));
            assertTrue(surfacesPage.body().contains("Living proof lanes"));
            assertTrue(surfacesPage.body().contains("Morse, Ouroboros, proof harnesses, organism validators, and substrate transduction"));
            assertTrue(knowledgePage.body().contains("Docs should answer to the machine."));
            assertTrue(knowledgePage.body().contains("Operator anchors"));
            assertTrue(knowledgePage.body().contains("Live operator map"));
            assertTrue(knowledgePage.body().contains("Graph semantics"));
            assertTrue(knowledgePage.body().contains("Math canon"));
            assertTrue(knowledgePage.body().contains("Implementation anchors"));
            assertTrue(knowledgePage.body().contains("Open math canon"));
            assertTrue(knowledgePage.body().contains("Grounded system state contract"));
            assertTrue(runtimePage.body().contains("Native body under the shell."));
            assertTrue(runtimePage.body().contains("Kernel spine"));
            assertTrue(runtimePage.body().contains("Runtime command spine"));
            assertTrue(runtimePage.body().contains("Runtime / factory actions"));
            assertTrue(runtimePage.body().contains("Runtime AGI role"));
            assertTrue(runtimePage.body().contains("Math family focus"));
            assertTrue(runtimePage.body().contains("Living proof focus"));
            assertTrue(runtimePage.body().contains("Runtime focus lens"));
            assertTrue(runtimePage.body().contains("Grounded system state contract"));
            assertTrue(launchpadPage.body().contains("Launchpad role map"));
            assertTrue(launchpadPage.body().contains("Run the menu like Fraynix means it."));
            assertTrue(launchpadPage.body().contains("Primary menu spine"));
            assertTrue(graphArtifact.body().contains("\"visualSemantics\""));
            assertTrue(graphArtifact.body().contains("\"layout\": \"agi_strata_first\""));
            assertTrue(graphArtifact.body().contains("\"primaryAgiStratum\""));
            assertTrue(graphArtifact.body().contains("\"systemsUrl\": \"/systems?focus="));
            assertTrue(graphArtifact.body().contains("\"runtimeUrl\": \"/runtime?subsystem="));
            assertTrue(graphArtifact.body().contains("\"jumpUrl\": \"/surfaces?surface="));
            assertTrue(graphArtifact.body().contains("&section="));
            assertTrue(graphArtifact.body().contains("\"routeUrl\": \"/launchpad?cmd="));
            assertTrue(mathArtifact.body().contains("# Fraynix Math Canon"));
            assertTrue(mathArtifact.body().contains("## Major AI Math Families Fraynix Must Speak"));
            assertTrue(appJs.body().contains("Direct actions"));
            assertTrue(appJs.body().contains("systems-focus-output"));
            assertTrue(appJs.body().contains("data-system-action"));
            assertTrue(appJs.body().contains("fraynix.systemFocusMemory"));
            assertTrue(appJs.body().contains("Last remembered result"));
            assertTrue(appJs.body().contains("fraynix.runtimeFocusMemory"));
            assertTrue(appJs.body().contains("Last remembered runtime result"));
            assertTrue(appJs.body().contains("LANE_TOUCH_HALF_LIFE_MS"));
            assertTrue(appJs.body().contains("decayTouchScore"));
            assertTrue(appJs.body().contains("mathUrl"));
            assertTrue(appJs.body().contains("fraynix.mathFocus"));
            assertTrue(appJs.body().contains("fraynix.proofFocus"));
            assertTrue(appJs.body().contains("clearMath"));
            assertTrue(appJs.body().contains("clearProof"));
            assertTrue(appJs.body().contains("replaceProofFocusQuery"));
            assertTrue(appJs.body().contains("systemsMathFocusHtml"));
            assertTrue(appJs.body().contains("systemsProofFocusHtml"));
            assertTrue(appJs.body().contains("runtimeMathFocusHtml"));
            assertTrue(appJs.body().contains("runtimeProofFocusHtml"));
            assertTrue(appJs.body().contains("Apply math focus"));
            assertTrue(appJs.body().contains("Apply proof focus"));
            assertTrue(appJs.body().contains("data-knowledge-proof-focus"));
            assertTrue(appJs.body().contains("Proof lane lens"));
            assertTrue(appJs.body().contains("renderMathCanon"));
            assertTrue(appJs.body().contains("parseMathCanon"));
            assertTrue(appJs.body().contains("Implementation anchor"));
            assertTrue(appJs.body().contains("loadSession"));
            assertTrue(appJs.body().contains("sessionDemoHtml"));
            assertTrue(appJs.body().contains("sessionConnectomePlanHtml"));
            assertTrue(appJs.body().contains("session-connectome-plan"));
            assertTrue(appJs.body().contains("session-quick-fills"));
            assertTrue(appJs.body().contains("commandDeckButtonsHtml"));
            assertTrue(appJs.body().contains("commandDeckCommands"));
            assertTrue(appJs.body().contains("overview.commandDecks?.launchpad"));
            assertTrue(appJs.body().contains("overview.commandDecks?.broadcast"));
            assertTrue(appJs.body().contains("result.mode === \"ai-router\""));
            assertTrue(appJs.body().contains("renderLegacySpine"));
            assertTrue(appJs.body().contains("renderCommandSpine"));
            assertTrue(appJs.body().contains("headroomKernelSpineHtml"));
            assertTrue(appJs.body().contains("source pillars visible"));
            assertTrue(appJs.body().contains("companion-legacy-spine"));
            assertTrue(appJs.body().contains("session-command-spine"));
            assertTrue(appJs.body().contains("systems-legacy-spine"));
            assertTrue(appJs.body().contains("runtime-legacy-spine"));
            assertTrue(appJs.body().contains("runtime-command-spine"));
            assertTrue(appJs.body().contains("surfaces-command-spine"));
            assertTrue(appJs.body().contains("knowledge-legacy-spine"));
            assertTrue(appJs.body().contains("Broadcast focus"));
            assertTrue(appJs.body().contains("Cognition focus"));
            assertTrue(appJs.body().contains("fetchSystemState"));
            assertTrue(appJs.body().contains("/api/system-state?consumer="));
            assertTrue(appJs.body().contains("ensureSystemStateLiveRefresh"));
            assertTrue(appJs.body().contains("SYSTEM_STATE_REFRESH_MS = 5000"));
            assertTrue(appJs.body().contains("const freshnessLabel = isStale ? \"stale\" : \"fresh\""));
            assertTrue(appJs.body().contains("const aiRun = boot.aiRun"));
            assertTrue(appJs.body().contains("ai run ${escapeHtml(String(aiRunStatus))}"));
            assertTrue(appJs.body().contains("AI run lane"));
            assertTrue(appJs.body().contains("AI run pressure"));
            assertTrue(appJs.body().contains("refresh every"));
            assertTrue(appJs.body().contains("data-review-approve"));
            assertTrue(appJs.body().contains("data-review-focus"));
            assertTrue(appJs.body().contains("renderSessionReviewDetail"));
            assertTrue(appJs.body().contains("sessionReviewDetailHtml"));
            assertTrue(appJs.body().contains("data-session-detail-approve"));
            assertTrue(appJs.body().contains("data-session-detail-reject"));
            assertTrue(appJs.body().contains("data-session-reject-note"));
            assertTrue(appJs.body().contains("loadCompanion"));
            assertTrue(appJs.body().contains("companionThreadHtml"));
            assertTrue(appJs.body().contains("companionHeadroomStatusHtml"));
            assertTrue(appJs.body().contains("data-companion-headroom"));
            assertTrue(appJs.body().contains("data-select-visual"));
            assertTrue(appJs.body().contains("data-select-audio"));
            assertTrue(appJs.body().contains("companionSelectionHtml"));
            assertTrue(appJs.body().contains("companionDreamStatusHtml"));
            assertTrue(appJs.body().contains("data-companion-dream"));
            assertTrue(appJs.body().contains("systemStateEnvelopeHtml"));
            assertTrue(appJs.body().contains("session-system-state"));
            assertTrue(appJs.body().contains("companion-system-state"));
            assertTrue(appJs.body().contains("Patch preview"));
            assertTrue(appJs.body().contains("Proof summary"));
            assertTrue(appJs.body().contains("snippetLines"));
            assertTrue(appJs.body().contains("session-ledger"));
            assertTrue(appJs.body().contains("readLaneTouchState"));
            assertTrue(appJs.body().contains("recent-lane"));
            assertTrue(appJs.body().contains("recent-node"));
            assertTrue(appJs.body().contains("Latest touch:"));
            assertTrue(appJs.body().contains("runtime path"));
            assertTrue(appJs.body().contains("warmCount"));
            assertTrue(appJs.body().contains("proofLaneMatchScore"));
            assertTrue(appJs.body().contains("filterLaneItemsByFocus"));
            assertTrue(appJs.body().contains("hidden by current focus"));
            assertTrue(appJs.body().contains("proof-focus-match"));
            assertTrue(appJs.body().contains("Living proof focus active:"));
            assertTrue(appJs.body().contains("livingProofLanesHtml"));
            assertTrue(appJs.body().contains("Focus commands"));
            assertTrue(appJs.body().contains("Surface anchors"));
            assertTrue(appJs.body().contains("Implementation anchors"));
            assertTrue(execute.body().contains("\"mode\": \"direct-command\""));
            assertTrue(execute.body().contains("ULTIMATE IMMORTAL KERNEL"));
            assertTrue(routedExecute.body().contains("\"mode\": \"ai-router\""));
            assertTrue(routedExecute.body().contains("Strongest living proof lanes active in this shell right now"));
            assertTrue(operator.body().contains("\"boot-tree-launches\""));
            assertTrue(operator.body().contains("\"discoveredFromBootTree\": true"));

            JSONObject systemStateJson = new JSONObject(systemStateApi.body());
            assertSystemStateEnvelopeShape(systemStateJson);

            JSONObject sessionJson = new JSONObject(sessionApi.body());
            assertSystemStateEnvelopeShape(sessionJson.getJSONObject("systemState"));

            JSONObject companionJson = new JSONObject(companionApi.body());
            assertSystemStateEnvelopeShape(companionJson.getJSONObject("systemState"));
        } finally {
            server.stop();
        }
    }

    private static void assertSystemStateEnvelopeShape(JSONObject payload) {
        assertTrue(payload.has("fetchedAt"));
        assertTrue(payload.has("staleAfterMs"));
        assertTrue(payload.optLong("staleAfterMs", -1L) > 0L);
        assertTrue(payload.has("cacheState"));
        assertTrue(payload.has("systemState"));

        JSONObject envelope = payload.getJSONObject("systemState");
        assertTrue(envelope.has("timestamp"));
        assertTrue(envelope.has("boot"));
        assertTrue(envelope.has("memory"));
        assertTrue(envelope.has("ollama"));
        assertTrue(envelope.has("cores"));
        assertTrue(envelope.has("subsystemHealth"));
        assertTrue(envelope.has("provenance"));

        JSONObject boot = envelope.getJSONObject("boot");
        assertTrue(boot.has("aiRun"));
        JSONObject aiRun = boot.getJSONObject("aiRun");
        assertTrue(aiRun.has("status"));
        assertTrue(aiRun.has("busy"));
        assertTrue(aiRun.has("queueDepth"));
        assertTrue(aiRun.has("lockWaitMs"));
        assertTrue(aiRun.has("timeoutSeconds"));
        assertTrue(aiRun.has("policyVersion"));

        JSONObject cores = envelope.getJSONObject("cores");
        assertTrue(cores.has("aiRunBusy"));
        assertTrue(cores.has("aiRunQueueDepth"));

        JSONArray health = envelope.getJSONArray("subsystemHealth");
        assertTrue(hasHealthNode(health, "aiRunRouter"));

        JSONObject provenance = envelope.getJSONObject("provenance");
        assertEquals("SYSTEM_STATE", provenance.optString("route"));
        assertTrue(provenance.optBoolean("wrappersBypassed", false));
        assertTrue(provenance.optBoolean("metaSynthesisBypassed", false));
        assertTrue(provenance.optBoolean("machineCheckable", false));
    }

    private static boolean hasHealthNode(JSONArray health, String name) {
        for (int i = 0; i < health.length(); i++) {
            JSONObject node = health.optJSONObject(i);
            if (node != null && name.equals(node.optString("name"))) {
                return true;
            }
        }
        return false;
    }

    private static String sampleFraynixBootSource() {
        return """
            class FraynixBoot {
                void boot(String arg) {
                    switch (arg) {
                        case "dreamforge", "deepdream" -> {
                            System.out.println("Dream forge online");
                        }
                        case "portalx" -> {
                            Portal.main(new String[0]);
                        }
                        case "deepscan" -> {
                            new ProbeShell().startREPL();
                        }
                    }
                }
            }
            """;
    }
}
