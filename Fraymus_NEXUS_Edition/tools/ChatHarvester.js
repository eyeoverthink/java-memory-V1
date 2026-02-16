/**
 * THE DIAMOND MINER
 * "Your AI history is a Billion-Dollar R&D Lab."
 * 
 * Purpose:
 * Extract AI chat history (ChatGPT, Claude, Gemini) into clean, archival PDFs.
 * Every iteration, bug fix, and epiphany is a diamond.
 * If you lose that history, you lose the neural pathway that led to the solution.
 * 
 * Usage:
 * 1. Open any AI chat (ChatGPT, Claude, etc.)
 * 2. Open Browser Console (F12)
 * 3. Paste this entire script and press Enter
 * 4. Click the "💎 HARVEST CHAT" button that appears
 * 5. Save the generated PDF
 * 
 * Features:
 * - Auto-scroll to load all lazy-loaded history
 * - Strips UI buttons, sidebars, avatars
 * - Clean monospace formatting optimized for code
 * - High-contrast green/cyan on black (Fraymus aesthetic)
 * - Automatic PDF export dialog
 * 
 * Alternative: Bookmarklet
 * Create a bookmark with this as the URL (minified):
 * javascript:(function(){/* paste minified code here *\/})();
 */

(function() {
    console.log("💎 FRAYMUS DIAMOND MINER ACTIVATED.");
    console.log("   Preparing to harvest AI chat history...");

    // 1. THE AUTO-SCROLLER (Load all history)
    // We scroll up repeatedly until we hit the top.
    let scrollCount = 0;
    let maxScrolls = 100; // Safety limit
    
    let scrollInterval = setInterval(() => {
        window.scrollTo(0, 0);
        
        // Specialized selectors for common AI UIs
        let scrollable = document.querySelector('div[class*="react-scroll-to-bottom"]') || 
                        document.querySelector('main') ||
                        document.querySelector('.overflow-y-auto');
        
        if(scrollable) {
            scrollable.scrollTop = 0;
        }
        
        scrollCount++;
        if (scrollCount >= maxScrolls) {
            console.log("   Auto-scroll complete (max iterations reached)");
            clearInterval(scrollInterval);
        }
    }, 100);

    // 2. THE EXTRACTOR (Once loaded)
    function harvestDiamonds() {
        clearInterval(scrollInterval);
        
        console.log("💎 HARVESTING DIAMONDS...");
        console.log("   Extracting message blocks...");
        
        // Create a Clean Container
        let printWindow = window.open('', '_blank');
        let d = printWindow.document;
        
        // Get current date for archive metadata
        let now = new Date();
        let timestamp = now.toISOString().replace(/[:.]/g, '-').slice(0, -5);
        let title = document.title || "AI Chat Archive";
        
        d.write(`
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Fraymus AI Log: ${title}</title>
                <style>
                    @page { margin: 1cm; }
                    body { 
                        font-family: 'Courier New', monospace; 
                        background: #0a0a0a; 
                        color: #00ff00; 
                        padding: 40px; 
                        line-height: 1.6;
                    }
                    .user { 
                        border-left: 4px solid #00ffff; 
                        padding-left: 20px; 
                        margin-top: 30px; 
                        color: #00ffff; 
                        background: #0d1117;
                        padding: 15px;
                        page-break-inside: avoid;
                    }
                    .ai { 
                        border-left: 4px solid #00ff00; 
                        padding-left: 20px; 
                        margin-bottom: 30px; 
                        color: #cccccc; 
                        background: #0d1117;
                        padding: 15px;
                        page-break-inside: avoid;
                    }
                    .timestamp { 
                        font-size: 10px; 
                        color: #666; 
                        margin-bottom: 8px; 
                        font-weight: bold;
                    }
                    pre, code { 
                        background: #1a1a1a; 
                        padding: 12px; 
                        border: 1px solid #333; 
                        overflow-x: auto; 
                        white-space: pre-wrap; 
                        word-wrap: break-word;
                        color: #00ff00;
                        font-size: 12px;
                        page-break-inside: avoid;
                    }
                    h1 { 
                        border-bottom: 3px solid #00ff00; 
                        padding-bottom: 15px; 
                        color: #00ff00;
                        text-align: center;
                    }
                    .metadata {
                        background: #1a1a1a;
                        padding: 15px;
                        border: 2px solid #00ff00;
                        margin-bottom: 30px;
                        color: #ffff00;
                    }
                    .warning {
                        color: #ff0000;
                        font-weight: bold;
                    }
                    /* Remove buttons and UI elements */
                    button, .copy-button, .regenerate, svg { display: none !important; }
                </style>
            </head>
            <body>
                <h1>💎 FRAYMUS DIAMOND ARCHIVE</h1>
                <div class="metadata">
                    <strong>SOURCE:</strong> ${title}<br>
                    <strong>HARVESTED:</strong> ${now.toLocaleString()}<br>
                    <strong>TIMESTAMP:</strong> ${timestamp}<br>
                    <strong>PURPOSE:</strong> Neural pathway preservation<br>
                    <strong class="warning">⚠️ EVERY ITERATION IS A DIAMOND</strong>
                </div>
                <div id="content"></div>
                <hr style="border-color: #00ff00; margin: 40px 0;">
                <p style="text-align: center; color: #666;">
                    Generated by The Diamond Miner<br>
                    FRAYMUS NEXUS Edition<br>
                    "Your AI history is a Billion-Dollar R&D Lab"
                </p>
            </body>
            </html>
        `);

        // Find all message blocks (Generic selectors - works on most platforms)
        // ChatGPT: .prose, div[data-message-author-role]
        // Claude: .font-claude-message
        // Gemini: .model-response, .user-query
        let messages = document.querySelectorAll(
            '.prose, div[data-message-author-role], .font-claude-message, .model-response, .user-query, [class*="message"]'
        );
        
        console.log(`   Found ${messages.length} message blocks`);
        
        let userCount = 0;
        let aiCount = 0;
        
        messages.forEach((msg, index) => {
            // Skip if empty or too small
            if (!msg.innerText || msg.innerText.trim().length < 10) return;
            
            let role = "unknown";
            let content = msg.innerHTML;
            
            // Heuristic detection of User vs AI
            // Check data attributes first
            if (msg.getAttribute('data-message-author-role') === 'user') {
                role = "user";
            } else if (msg.getAttribute('data-message-author-role') === 'assistant') {
                role = "ai";
            }
            // Check class names
            else if (msg.className.includes('user') || msg.className.includes('User')) {
                role = "user";
            } else if (msg.className.includes('assistant') || msg.className.includes('model')) {
                role = "ai";
            }
            // Check parent elements
            else if (msg.closest('[data-message-author-role="user"]')) {
                role = "user";
            } else if (msg.closest('[data-message-author-role="assistant"]')) {
                role = "ai";
            }
            // Default to AI if uncertain
            else {
                role = "ai";
            }

            if (role === "user") userCount++;
            else aiCount++;

            // Add to clean document
            let entry = d.createElement('div');
            entry.className = role === "user" ? "user" : "ai";
            entry.innerHTML = `<div class='timestamp'>BLOCK #${index} | ${role.toUpperCase()}</div>` + content;
            d.getElementById('content').appendChild(entry);
        });

        console.log(`   Extracted: ${userCount} user messages, ${aiCount} AI messages`);
        console.log("   Generating PDF...");

        d.close();
        printWindow.focus();
        
        // 3. THE TRIGGER
        setTimeout(() => {
            printWindow.print(); // Opens the "Save as PDF" dialog
            console.log("💎 HARVEST COMPLETE. Save the PDF to preserve your diamonds.");
        }, 1000);
    }

    // Add a floating button to trigger the harvest
    let btn = document.createElement("button");
    btn.innerText = "💎 HARVEST CHAT";
    btn.style = "position:fixed; top:10px; right:10px; z-index:99999; background:#00ff00; color:#000; font-weight:bold; padding:15px 20px; border:3px solid #fff; cursor:pointer; font-family:monospace; font-size:14px; box-shadow: 0 4px 8px rgba(0,255,0,0.3);";
    btn.onclick = harvestDiamonds;
    document.body.appendChild(btn);

    console.log("💎 DIAMOND MINER READY.");
    console.log("   Click the green button to harvest this chat.");
    console.log("   Auto-scrolling to load all history...");

})();
