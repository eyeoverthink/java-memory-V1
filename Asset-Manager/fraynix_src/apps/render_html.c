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
    page_title[0] = '\0';
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
