/* ═══════════════════════════════════════════════════════════════════════════
 * FRAY-BROWSER: Main Application - Gen 151
 * ═══════════════════════════════════════════════════════════════════════════ */

#ifndef FRAY_BROWSER_C
#define FRAY_BROWSER_C

#include "http.c"
#include "html.c"
#include "render_html.c"

#define MAX_HISTORY 32

static char current_url[512];
static char history[MAX_HISTORY][512];
static int history_count = 0;
static int history_pos = -1;

/* Bookmarks */
#define MAX_BOOKMARKS 16
static char bookmarks[MAX_BOOKMARKS][512];
static int bookmark_count = 0;

void browser_init(void) {
    current_url[0] = '\0';
    history_count = 0;
    history_pos = -1;
    bookmark_count = 0;
    
    /* Default bookmarks */
    kstrcpy(bookmarks[0], "http://localhost/");
    bookmark_count = 1;
}

void browser_add_history(const char* url) {
    if (history_count >= MAX_HISTORY) {
        /* Shift history */
        for (int i = 0; i < MAX_HISTORY - 1; i++) {
            kstrcpy(history[i], history[i + 1]);
        }
        history_count = MAX_HISTORY - 1;
    }
    kstrcpy(history[history_count++], url);
    history_pos = history_count - 1;
}

void browser_navigate(const char* url) {
    kstrcpy(current_url, url);
    browser_add_history(url);
    
    /* Show loading */
    set_cursor(0, 1);
    set_color(0x0E);
    kprint("Loading: ");
    kprint(url);
    kprint("...");
    
    /* Fetch page */
    HttpResponse resp;
    if (!http_get(url, &resp)) {
        clear_screen();
        set_color(0x0C);
        kprint("\n\n  Error: Could not load page\n");
        kprint("  ");
        kprint(url);
        return;
    }
    
    /* Check response */
    if (resp.status_code >= 400) {
        clear_screen();
        set_color(0x0C);
        kprint("\n\n  HTTP Error: ");
        kprint_int(resp.status_code);
        kprint("\n");
        return;
    }
    
    /* Parse and render */
    HtmlNode* doc = html_parse(resp.body);
    render_page(doc);
    
    /* Update address bar */
    set_cursor(17, 0);
    set_color(0x70);
    kprint(url);
}

void browser_back(void) {
    if (history_pos > 0) {
        history_pos--;
        kstrcpy(current_url, history[history_pos]);
        
        HttpResponse resp;
        if (http_get(current_url, &resp)) {
            HtmlNode* doc = html_parse(resp.body);
            render_page(doc);
        }
    }
}

void browser_reload(void) {
    if (current_url[0]) {
        browser_navigate(current_url);
    }
}

void browser_prompt_url(void) {
    /* Simple URL input */
    set_cursor(0, SCREEN_HEIGHT - 1);
    set_color(0x0F);
    kprint("URL: ");
    
    char url[512];
    read_line(url, 512);
    
    if (url[0]) {
        browser_navigate(url);
    }
}

void browser_show_bookmarks(void) {
    clear_screen();
    set_color(0x0E);
    kprint("\n  BOOKMARKS\n");
    kprint("  ==========\n\n");
    
    for (int i = 0; i < bookmark_count; i++) {
        kprint("  ");
        kprint_int(i + 1);
        kprint(". ");
        kprint(bookmarks[i]);
        kprint("\n");
    }
    
    kprint("\n  Enter number or [Q]uit: ");
    
    char c = read_key();
    if (c >= '1' && c <= '9') {
        int idx = c - '1';
        if (idx < bookmark_count) {
            browser_navigate(bookmarks[idx]);
        }
    }
}

void browser_add_bookmark(void) {
    if (bookmark_count >= MAX_BOOKMARKS) return;
    if (current_url[0] == '\0') return;
    
    kstrcpy(bookmarks[bookmark_count++], current_url);
    
    set_cursor(0, SCREEN_HEIGHT - 1);
    set_color(0x0A);
    kprint("Bookmark added!");
}

void browser_main(const char* start_url) {
    browser_init();
    
    if (start_url && start_url[0]) {
        browser_navigate(start_url);
    } else {
        /* Show home page */
        clear_screen();
        set_color(0x0F);
        kprint("\n\n");
        kprint("  ╔═══════════════════════════════════════╗\n");
        kprint("  ║        FRAY-BROWSER v1.0              ║\n");
        kprint("  ║     The Window to the Network         ║\n");
        kprint("  ╚═══════════════════════════════════════╝\n");
        kprint("\n");
        kprint("  [G] Go to URL\n");
        kprint("  [B] Bookmarks\n");
        kprint("  [Q] Quit\n");
    }
    
    /* Main loop */
    while (1) {
        unsigned char key = read_key();
        
        switch (key) {
            case 'q':
            case 'Q':
                return;
                
            case 'g':
            case 'G':
                browser_prompt_url();
                break;
                
            case 'b':
            case 'B':
                browser_show_bookmarks();
                break;
                
            case 'r':
            case 'R':
                browser_reload();
                break;
                
            case 8:  /* Backspace */
                browser_back();
                break;
                
            case 'd':
            case 'D':
                browser_add_bookmark();
                break;
        }
    }
}

#endif /* FRAY_BROWSER_C */
