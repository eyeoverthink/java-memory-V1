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
    n->text[0] = '\0';
    n->href[0] = '\0';
    n->src[0] = '\0';
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
        attr_name[i] = '\0';
        
        if (*p == '=') {
            p++;
            char quote = *p;
            if (quote == '"' || quote == '\'') p++;
            else quote = ' ';
            
            if (kstrcmp(attr_name, name) == 0) {
                i = 0;
                while (*p && *p != quote && *p != '>' && i < max_len - 1) {
                    out[i++] = *p++;
                }
                out[i] = '\0';
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
        while (*ptr == ' ' || *ptr == '\n' || *ptr == '\r' || *ptr == '\t') ptr++;
        
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
            tag_name[i] = '\0';
            
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
                if (*ptr == '\n' || *ptr == '\r' || *ptr == '\t') {
                    if (i > 0 && text[i-1] != ' ') text[i++] = ' ';
                } else {
                    text[i++] = *ptr;
                }
                ptr++;
            }
            text[i] = '\0';
            
            /* Trim */
            while (i > 0 && text[i-1] == ' ') text[--i] = '\0';
            
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
