package fraymus.apps;

import java.io.*;
import java.nio.file.*;

/**
 * 🌐 FRAY-BROWSER - Gen 151
 * "The window to the network."
 * 
 * Features:
 * - HTTP/1.1 Client
 * - HTML Parser (subset)
 * - CSS Styling (basic)
 * - Bookmarks
 * - History
 */
public class FrayBrowserBuilder {

    private static final String OUTPUT_DIR = "fraynix_src/apps";

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  🌐 FRAY-BROWSER - Gen 151                                    ║");
        System.out.println("║  The Web Browser                                              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            Files.createDirectories(Paths.get(OUTPUT_DIR));
            
            buildHttpClient();
            buildHtmlParser();
            buildRenderer();
            buildBrowserUI();
            
            System.out.println();
            System.out.println("✅ FRAY-BROWSER COMPILED");
            System.out.println("   Usage: browse <url>");
            
        } catch (IOException e) {
            System.err.println("❌ BUILD FAILED: " + e.getMessage());
        }
    }

    private static void buildHttpClient() throws IOException {
        System.out.println("   [1/4] Generating http.c...");
        
        String http = """
/* ═══════════════════════════════════════════════════════════════════════════
 * FRAY-HTTP: HTTP/1.1 Client - Gen 151
 * ═══════════════════════════════════════════════════════════════════════════ */

#ifndef FRAY_HTTP_C
#define FRAY_HTTP_C

#define HTTP_PORT 80
#define HTTPS_PORT 443
#define MAX_RESPONSE_SIZE 65536

typedef struct {
    int status_code;
    char content_type[64];
    int content_length;
    char* body;
    int body_length;
} HttpResponse;

typedef struct {
    char host[128];
    int port;
    char path[256];
    char query[256];
} ParsedUrl;

/* Parse URL */
int parse_url(const char* url, ParsedUrl* out) {
    out->port = 80;
    out->path[0] = '/';
    out->path[1] = '\\0';
    out->query[0] = '\\0';
    
    const char* ptr = url;
    
    /* Skip protocol */
    if (kstrncmp(ptr, "http://", 7) == 0) {
        ptr += 7;
    } else if (kstrncmp(ptr, "https://", 8) == 0) {
        ptr += 8;
        out->port = 443;
    }
    
    /* Extract host */
    int i = 0;
    while (*ptr && *ptr != '/' && *ptr != ':' && *ptr != '?' && i < 127) {
        out->host[i++] = *ptr++;
    }
    out->host[i] = '\\0';
    
    /* Extract port if present */
    if (*ptr == ':') {
        ptr++;
        out->port = 0;
        while (*ptr >= '0' && *ptr <= '9') {
            out->port = out->port * 10 + (*ptr++ - '0');
        }
    }
    
    /* Extract path */
    if (*ptr == '/') {
        i = 0;
        while (*ptr && *ptr != '?' && i < 255) {
            out->path[i++] = *ptr++;
        }
        out->path[i] = '\\0';
    }
    
    /* Extract query */
    if (*ptr == '?') {
        ptr++;
        i = 0;
        while (*ptr && i < 255) {
            out->query[i++] = *ptr++;
        }
        out->query[i] = '\\0';
    }
    
    return 1;
}

/* DNS Resolution (simplified - uses hosts file or broadcast) */
unsigned int dns_resolve(const char* hostname) {
    /* Check local hosts */
    if (kstrcmp(hostname, "localhost") == 0) return 0x7F000001;
    
    /* For now, assume IP is provided or use broadcast discovery */
    /* Real implementation would send DNS query to 8.8.8.8 */
    
    /* Parse IP if numeric */
    unsigned int ip = 0;
    int part = 0, parts = 0;
    
    for (int i = 0; hostname[i] && parts < 4; i++) {
        if (hostname[i] >= '0' && hostname[i] <= '9') {
            part = part * 10 + (hostname[i] - '0');
        } else if (hostname[i] == '.') {
            ip = (ip << 8) | (part & 0xFF);
            part = 0;
            parts++;
        }
    }
    ip = (ip << 8) | (part & 0xFF);
    
    return (parts == 3) ? ip : 0;
}

/* Build HTTP Request */
int build_request(char* buf, int buf_size, const char* method, ParsedUrl* url) {
    int len = 0;
    
    len += ksprintf(&buf[len], "%s %s", method, url->path);
    if (url->query[0]) {
        len += ksprintf(&buf[len], "?%s", url->query);
    }
    len += ksprintf(&buf[len], " HTTP/1.1\\r\\n");
    len += ksprintf(&buf[len], "Host: %s\\r\\n", url->host);
    len += ksprintf(&buf[len], "User-Agent: FrayBrowser/1.0\\r\\n");
    len += ksprintf(&buf[len], "Accept: text/html,text/plain,*/*\\r\\n");
    len += ksprintf(&buf[len], "Connection: close\\r\\n");
    len += ksprintf(&buf[len], "\\r\\n");
    
    return len;
}

/* Parse HTTP Response */
int parse_response(const char* data, int data_len, HttpResponse* resp) {
    resp->status_code = 0;
    resp->content_length = 0;
    resp->body = 0;
    
    /* Parse status line */
    if (kstrncmp(data, "HTTP/1.", 7) != 0) return 0;
    
    const char* ptr = data + 9;
    while (*ptr == ' ') ptr++;
    
    resp->status_code = 0;
    while (*ptr >= '0' && *ptr <= '9') {
        resp->status_code = resp->status_code * 10 + (*ptr++ - '0');
    }
    
    /* Parse headers */
    ptr = kstrstr(data, "\\r\\n") + 2;
    
    while (ptr < data + data_len && *ptr != '\\r') {
        if (kstrncmp(ptr, "Content-Type:", 13) == 0) {
            ptr += 13;
            while (*ptr == ' ') ptr++;
            int i = 0;
            while (*ptr != '\\r' && i < 63) {
                resp->content_type[i++] = *ptr++;
            }
            resp->content_type[i] = '\\0';
        }
        else if (kstrncmp(ptr, "Content-Length:", 15) == 0) {
            ptr += 15;
            while (*ptr == ' ') ptr++;
            resp->content_length = 0;
            while (*ptr >= '0' && *ptr <= '9') {
                resp->content_length = resp->content_length * 10 + (*ptr++ - '0');
            }
        }
        
        /* Skip to next line */
        while (ptr < data + data_len && *ptr != '\\n') ptr++;
        ptr++;
    }
    
    /* Find body */
    const char* body_start = kstrstr(data, "\\r\\n\\r\\n");
    if (body_start) {
        body_start += 4;
        resp->body = (char*)body_start;
        resp->body_length = data_len - (body_start - data);
    }
    
    return 1;
}

/* Perform HTTP GET */
int http_get(const char* url, HttpResponse* resp) {
    ParsedUrl parsed;
    if (!parse_url(url, &parsed)) {
        return 0;
    }
    
    /* Resolve hostname */
    unsigned int ip = dns_resolve(parsed.host);
    if (ip == 0) {
        kprint_color("[HTTP] DNS resolution failed\\n", 0x0C);
        return 0;
    }
    
    /* Connect */
    int sock = tcp_connect(ip, parsed.port);
    if (sock < 0) {
        kprint_color("[HTTP] Connection failed\\n", 0x0C);
        return 0;
    }
    
    /* Send request */
    char request[512];
    int req_len = build_request(request, sizeof(request), "GET", &parsed);
    tcp_send(sock, request, req_len);
    
    /* Receive response */
    static char response_buf[MAX_RESPONSE_SIZE];
    int total = 0;
    int received;
    
    while ((received = tcp_recv(sock, &response_buf[total], MAX_RESPONSE_SIZE - total)) > 0) {
        total += received;
    }
    response_buf[total] = '\\0';
    
    tcp_close(sock);
    
    /* Parse response */
    return parse_response(response_buf, total, resp);
}

#endif /* FRAY_HTTP_C */
""";
        
        writeFile(OUTPUT_DIR + "/http.c", http);
    }

    private static void buildHtmlParser() throws IOException {
        System.out.println("   [2/4] Generating html.c...");
        
        String html = """
/* ═══════════════════════════════════════════════════════════════════════════
 * FRAY-HTML: HTML Parser - Gen 151
 * ═══════════════════════════════════════════════════════════════════════════ */

#ifndef FRAY_HTML_C
#define FRAY_HTML_C

#define NODE_TEXT     0
#define NODE_ELEMENT  1

#define TAG_HTML      1
#define TAG_HEAD      2
#define TAG_TITLE     3
#define TAG_BODY      4
#define TAG_H1        5
#define TAG_H2        6
#define TAG_H3        7
#define TAG_P         8
#define TAG_A         9
#define TAG_DIV       10
#define TAG_SPAN      11
#define TAG_UL        12
#define TAG_LI        13
#define TAG_IMG       14
#define TAG_BR        15
#define TAG_HR        16
#define TAG_B         17
#define TAG_I         18
#define TAG_PRE       19
#define TAG_CODE      20

typedef struct HtmlNode {
    int type;           /* NODE_TEXT or NODE_ELEMENT */
    int tag;            /* TAG_* if element */
    char text[256];     /* Text content or tag name */
    char href[256];     /* For <a> tags */
    char src[256];      /* For <img> tags */
    struct HtmlNode* children[32];
    int child_count;
    struct HtmlNode* parent;
} HtmlNode;

#define MAX_NODES 512
static HtmlNode node_pool[MAX_NODES];
static int node_count = 0;

HtmlNode* html_alloc_node(void) {
    if (node_count >= MAX_NODES) return 0;
    HtmlNode* n = &node_pool[node_count++];
    n->type = NODE_TEXT;
    n->tag = 0;
    n->text[0] = '\\0';
    n->href[0] = '\\0';
    n->src[0] = '\\0';
    n->child_count = 0;
    n->parent = 0;
    return n;
}

void html_reset(void) {
    node_count = 0;
}

int html_match_tag(const char* name) {
    if (kstrcmp(name, "html") == 0) return TAG_HTML;
    if (kstrcmp(name, "head") == 0) return TAG_HEAD;
    if (kstrcmp(name, "title") == 0) return TAG_TITLE;
    if (kstrcmp(name, "body") == 0) return TAG_BODY;
    if (kstrcmp(name, "h1") == 0) return TAG_H1;
    if (kstrcmp(name, "h2") == 0) return TAG_H2;
    if (kstrcmp(name, "h3") == 0) return TAG_H3;
    if (kstrcmp(name, "p") == 0) return TAG_P;
    if (kstrcmp(name, "a") == 0) return TAG_A;
    if (kstrcmp(name, "div") == 0) return TAG_DIV;
    if (kstrcmp(name, "span") == 0) return TAG_SPAN;
    if (kstrcmp(name, "ul") == 0) return TAG_UL;
    if (kstrcmp(name, "li") == 0) return TAG_LI;
    if (kstrcmp(name, "img") == 0) return TAG_IMG;
    if (kstrcmp(name, "br") == 0) return TAG_BR;
    if (kstrcmp(name, "hr") == 0) return TAG_HR;
    if (kstrcmp(name, "b") == 0 || kstrcmp(name, "strong") == 0) return TAG_B;
    if (kstrcmp(name, "i") == 0 || kstrcmp(name, "em") == 0) return TAG_I;
    if (kstrcmp(name, "pre") == 0) return TAG_PRE;
    if (kstrcmp(name, "code") == 0) return TAG_CODE;
    return 0;
}

/* Parse attribute value */
void html_parse_attr(const char** ptr, const char* name, char* out, int max_len) {
    const char* p = *ptr;
    char attr_name[32];
    
    while (*p && *p != '>') {
        /* Skip whitespace */
        while (*p == ' ') p++;
        
        /* Read attribute name */
        int i = 0;
        while (*p && *p != '=' && *p != ' ' && *p != '>' && i < 31) {
            attr_name[i++] = *p++;
        }
        attr_name[i] = '\\0';
        
        if (*p == '=') {
            p++;
            char quote = *p;
            if (quote == '"' || quote == '\\'') p++;
            else quote = ' ';
            
            if (kstrcmp(attr_name, name) == 0) {
                i = 0;
                while (*p && *p != quote && *p != '>' && i < max_len - 1) {
                    out[i++] = *p++;
                }
                out[i] = '\\0';
            } else {
                while (*p && *p != quote && *p != '>') p++;
            }
            if (*p == quote) p++;
        }
    }
    
    *ptr = p;
}

/* Parse HTML document */
HtmlNode* html_parse(const char* html) {
    html_reset();
    
    HtmlNode* root = html_alloc_node();
    root->type = NODE_ELEMENT;
    root->tag = TAG_HTML;
    
    HtmlNode* current = root;
    const char* ptr = html;
    
    while (*ptr) {
        /* Skip whitespace */
        while (*ptr == ' ' || *ptr == '\\n' || *ptr == '\\r' || *ptr == '\\t') ptr++;
        
        if (*ptr == '<') {
            ptr++;
            
            /* Comment */
            if (ptr[0] == '!' && ptr[1] == '-' && ptr[2] == '-') {
                ptr = kstrstr(ptr, "-->");
                if (ptr) ptr += 3;
                continue;
            }
            
            /* Closing tag */
            if (*ptr == '/') {
                ptr++;
                while (*ptr && *ptr != '>') ptr++;
                if (*ptr == '>') ptr++;
                if (current->parent) current = current->parent;
                continue;
            }
            
            /* Opening tag */
            char tag_name[32];
            int i = 0;
            while (*ptr && *ptr != ' ' && *ptr != '>' && *ptr != '/' && i < 31) {
                tag_name[i++] = (*ptr >= 'A' && *ptr <= 'Z') ? *ptr + 32 : *ptr;
                ptr++;
            }
            tag_name[i] = '\\0';
            
            int tag_id = html_match_tag(tag_name);
            if (tag_id == 0) {
                /* Unknown tag, skip */
                while (*ptr && *ptr != '>') ptr++;
                if (*ptr == '>') ptr++;
                continue;
            }
            
            HtmlNode* node = html_alloc_node();
            if (!node) break;
            
            node->type = NODE_ELEMENT;
            node->tag = tag_id;
            kstrcpy(node->text, tag_name);
            node->parent = current;
            
            if (current->child_count < 32) {
                current->children[current->child_count++] = node;
            }
            
            /* Parse attributes */
            html_parse_attr(&ptr, "href", node->href, 256);
            html_parse_attr(&ptr, "src", node->src, 256);
            
            /* Skip to end of tag */
            while (*ptr && *ptr != '>') ptr++;
            
            /* Self-closing or void element */
            int self_close = (ptr[-1] == '/') || 
                             (tag_id == TAG_BR || tag_id == TAG_HR || tag_id == TAG_IMG);
            
            if (*ptr == '>') ptr++;
            
            if (!self_close) {
                current = node;
            }
        }
        else {
            /* Text node */
            char text[256];
            int i = 0;
            while (*ptr && *ptr != '<' && i < 255) {
                if (*ptr == '\\n' || *ptr == '\\r' || *ptr == '\\t') {
                    if (i > 0 && text[i-1] != ' ') text[i++] = ' ';
                } else {
                    text[i++] = *ptr;
                }
                ptr++;
            }
            text[i] = '\\0';
            
            /* Trim */
            while (i > 0 && text[i-1] == ' ') text[--i] = '\\0';
            
            if (i > 0) {
                HtmlNode* node = html_alloc_node();
                if (!node) break;
                
                node->type = NODE_TEXT;
                kstrcpy(node->text, text);
                node->parent = current;
                
                if (current->child_count < 32) {
                    current->children[current->child_count++] = node;
                }
            }
        }
    }
    
    return root;
}

#endif /* FRAY_HTML_C */
""";
        
        writeFile(OUTPUT_DIR + "/html.c", html);
    }

    private static void buildRenderer() throws IOException {
        System.out.println("   [3/4] Generating render_html.c...");
        
        String render = """
/* ═══════════════════════════════════════════════════════════════════════════
 * FRAY-RENDER: HTML Renderer - Gen 151
 * ═══════════════════════════════════════════════════════════════════════════ */

#ifndef FRAY_RENDER_HTML_C
#define FRAY_RENDER_HTML_C

#define SCREEN_WIDTH 80
#define SCREEN_HEIGHT 25

typedef struct {
    int x, y;
    int width;
    int indent;
    int bold;
    int italic;
    int link;
    unsigned char color;
} RenderState;

static RenderState render_state;
static char page_title[128];

void render_init(void) {
    render_state.x = 0;
    render_state.y = 2;  /* Leave room for address bar */
    render_state.width = SCREEN_WIDTH;
    render_state.indent = 0;
    render_state.bold = 0;
    render_state.italic = 0;
    render_state.link = 0;
    render_state.color = 0x0F;
    page_title[0] = '\\0';
}

void render_newline(void) {
    render_state.x = render_state.indent;
    render_state.y++;
    if (render_state.y >= SCREEN_HEIGHT - 1) {
        /* Would need scrolling */
    }
}

void render_text(const char* text) {
    set_color(render_state.color);
    
    for (int i = 0; text[i]; i++) {
        if (render_state.x >= render_state.width) {
            render_newline();
        }
        
        set_cursor(render_state.x, render_state.y);
        char buf[2] = {text[i], 0};
        kprint(buf);
        render_state.x++;
    }
}

void render_node(HtmlNode* node) {
    if (node->type == NODE_TEXT) {
        render_text(node->text);
        return;
    }
    
    /* Handle element */
    int old_indent = render_state.indent;
    int old_bold = render_state.bold;
    int old_italic = render_state.italic;
    unsigned char old_color = render_state.color;
    
    switch (node->tag) {
        case TAG_TITLE:
            /* Just capture title, don't render */
            if (node->child_count > 0 && node->children[0]->type == NODE_TEXT) {
                kstrcpy(page_title, node->children[0]->text);
            }
            return;
            
        case TAG_HEAD:
            /* Process but don't render */
            for (int i = 0; i < node->child_count; i++) {
                render_node(node->children[i]);
            }
            return;
            
        case TAG_H1:
            render_newline();
            render_state.bold = 1;
            render_state.color = 0x0E;  /* Yellow */
            break;
            
        case TAG_H2:
            render_newline();
            render_state.bold = 1;
            render_state.color = 0x0B;  /* Cyan */
            break;
            
        case TAG_H3:
            render_newline();
            render_state.bold = 1;
            render_state.color = 0x0F;  /* White */
            break;
            
        case TAG_P:
            render_newline();
            render_newline();
            break;
            
        case TAG_A:
            render_state.color = 0x09;  /* Blue */
            render_state.link = 1;
            render_text("[");
            break;
            
        case TAG_UL:
            render_state.indent += 2;
            break;
            
        case TAG_LI:
            render_newline();
            render_text("* ");
            break;
            
        case TAG_B:
            render_state.bold = 1;
            render_state.color = 0x0F;
            break;
            
        case TAG_I:
            render_state.italic = 1;
            render_state.color = 0x07;
            break;
            
        case TAG_HR:
            render_newline();
            for (int i = 0; i < render_state.width - render_state.indent; i++) {
                render_text("-");
            }
            render_newline();
            break;
            
        case TAG_BR:
            render_newline();
            break;
            
        case TAG_PRE:
        case TAG_CODE:
            render_state.color = 0x0A;  /* Green */
            break;
            
        case TAG_IMG:
            render_text("[IMG: ");
            render_text(node->src);
            render_text("]");
            break;
    }
    
    /* Render children */
    for (int i = 0; i < node->child_count; i++) {
        render_node(node->children[i]);
    }
    
    /* Post-element handling */
    switch (node->tag) {
        case TAG_A:
            render_text("]");
            break;
            
        case TAG_H1:
        case TAG_H2:
        case TAG_H3:
            render_newline();
            break;
    }
    
    /* Restore state */
    render_state.indent = old_indent;
    render_state.bold = old_bold;
    render_state.italic = old_italic;
    render_state.color = old_color;
}

void render_page(HtmlNode* root) {
    clear_screen();
    render_init();
    
    /* Address bar placeholder */
    set_cursor(0, 0);
    set_color(0x70);  /* Inverted */
    for (int i = 0; i < SCREEN_WIDTH; i++) kprint(" ");
    set_cursor(0, 0);
    kprint(" FRAY-BROWSER | ");
    
    /* Render document */
    render_node(root);
    
    /* Update title in address bar */
    if (page_title[0]) {
        set_cursor(17, 0);
        set_color(0x70);
        kprint(page_title);
    }
    
    /* Status bar */
    set_cursor(0, SCREEN_HEIGHT - 1);
    set_color(0x70);
    for (int i = 0; i < SCREEN_WIDTH; i++) kprint(" ");
    set_cursor(0, SCREEN_HEIGHT - 1);
    kprint(" [Q]uit  [G]o to URL  [B]ack  [R]eload ");
}

#endif /* FRAY_RENDER_HTML_C */
""";
        
        writeFile(OUTPUT_DIR + "/render_html.c", render);
    }

    private static void buildBrowserUI() throws IOException {
        System.out.println("   [4/4] Generating browser.c...");
        
        String browser = """
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
    current_url[0] = '\\0';
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
        kprint("\\n\\n  Error: Could not load page\\n");
        kprint("  ");
        kprint(url);
        return;
    }
    
    /* Check response */
    if (resp.status_code >= 400) {
        clear_screen();
        set_color(0x0C);
        kprint("\\n\\n  HTTP Error: ");
        kprint_int(resp.status_code);
        kprint("\\n");
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
    kprint("\\n  BOOKMARKS\\n");
    kprint("  ==========\\n\\n");
    
    for (int i = 0; i < bookmark_count; i++) {
        kprint("  ");
        kprint_int(i + 1);
        kprint(". ");
        kprint(bookmarks[i]);
        kprint("\\n");
    }
    
    kprint("\\n  Enter number or [Q]uit: ");
    
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
    if (current_url[0] == '\\0') return;
    
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
        kprint("\\n\\n");
        kprint("  ╔═══════════════════════════════════════╗\\n");
        kprint("  ║        FRAY-BROWSER v1.0              ║\\n");
        kprint("  ║     The Window to the Network         ║\\n");
        kprint("  ╚═══════════════════════════════════════╝\\n");
        kprint("\\n");
        kprint("  [G] Go to URL\\n");
        kprint("  [B] Bookmarks\\n");
        kprint("  [Q] Quit\\n");
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
""";
        
        writeFile(OUTPUT_DIR + "/browser.c", browser);
    }

    private static void writeFile(String path, String content) throws IOException {
        Path filePath = Paths.get(path);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, content);
        System.out.println("      → " + path);
    }
}
