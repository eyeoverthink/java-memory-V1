/* ═══════════════════════════════════════════════════════════════════════════
 * FRAY-EDIT: Modal Text Editor - Gen 151
 * "Every keystroke is an intent."
 * ═══════════════════════════════════════════════════════════════════════════ */

#ifndef FRAY_EDIT_C
#define FRAY_EDIT_C

#include "syntax.c"
#include "undo.c"

#define MAX_LINES 10000
#define MAX_LINE_LEN 256
#define MAX_BUFFERS 8

/* Editor modes */
#define MODE_NORMAL  0
#define MODE_INSERT  1
#define MODE_COMMAND 2
#define MODE_VISUAL  3

/* Buffer structure */
typedef struct {
    char lines[MAX_LINES][MAX_LINE_LEN];
    int line_count;
    int cursor_x;
    int cursor_y;
    int scroll_y;
    int modified;
    char filename[64];
    int syntax_type;  /* 0=none, 1=C, 2=Java, 3=Python */
} TextBuffer;

static TextBuffer buffers[MAX_BUFFERS];
static int current_buffer = 0;
static int buffer_count = 0;
static int editor_mode = MODE_NORMAL;
static char command_line[256];
static int command_pos = 0;
static char status_msg[128];

/* Initialize editor */
void edit_init(void) {
    buffer_count = 0;
    current_buffer = 0;
    editor_mode = MODE_NORMAL;
    undo_init();
    kstrcpy(status_msg, "FRAY-EDIT | Press 'i' to insert, ':' for commands");
}

/* Create new buffer */
int edit_new_buffer(const char* filename) {
    if (buffer_count >= MAX_BUFFERS) return -1;
    
    TextBuffer* buf = &buffers[buffer_count];
    buf->line_count = 1;
    buf->cursor_x = 0;
    buf->cursor_y = 0;
    buf->scroll_y = 0;
    buf->modified = 0;
    buf->lines[0][0] = '\0';
    
    if (filename) {
        kstrcpy(buf->filename, filename);
        buf->syntax_type = detect_syntax(filename);
    } else {
        kstrcpy(buf->filename, "[unnamed]");
        buf->syntax_type = 0;
    }
    
    return buffer_count++;
}

/* Load file into buffer */
int edit_load_file(const char* filename) {
    int idx = edit_new_buffer(filename);
    if (idx < 0) return -1;
    
    TextBuffer* buf = &buffers[idx];
    
    /* Read from hash memory */
    unsigned long long hash = phi_hash(filename, kstrlen(filename));
    char* content = (char*)get_memory_block_by_hash(hash);
    
    if (content) {
        buf->line_count = 0;
        int col = 0;
        
        for (int i = 0; content[i]; i++) {
            if (content[i] == '\n' || col >= MAX_LINE_LEN - 1) {
                buf->lines[buf->line_count][col] = '\0';
                buf->line_count++;
                col = 0;
                if (buf->line_count >= MAX_LINES) break;
            } else {
                buf->lines[buf->line_count][col++] = content[i];
            }
        }
        
        if (col > 0) {
            buf->lines[buf->line_count][col] = '\0';
            buf->line_count++;
        }
        
        kstrcpy(status_msg, "Loaded: ");
        kstrcat(status_msg, filename);
    } else {
        kstrcpy(status_msg, "New file: ");
        kstrcat(status_msg, filename);
    }
    
    return idx;
}

/* Save buffer to file */
void edit_save(void) {
    TextBuffer* buf = &buffers[current_buffer];
    
    /* Calculate total size */
    int total = 0;
    for (int i = 0; i < buf->line_count; i++) {
        total += kstrlen(buf->lines[i]) + 1;
    }
    
    /* Allocate and concatenate */
    char* content = (char*)kmalloc(total + 1);
    int pos = 0;
    
    for (int i = 0; i < buf->line_count; i++) {
        for (int j = 0; buf->lines[i][j]; j++) {
            content[pos++] = buf->lines[i][j];
        }
        content[pos++] = '\n';
    }
    content[pos] = '\0';
    
    /* Store in hash memory */
    unsigned long long hash = phi_hash(buf->filename, kstrlen(buf->filename));
    store_memory_block(content, pos, hash);
    
    buf->modified = 0;
    kstrcpy(status_msg, "Saved: ");
    kstrcat(status_msg, buf->filename);
}

/* Insert character at cursor */
void edit_insert_char(char c) {
    TextBuffer* buf = &buffers[current_buffer];
    char* line = buf->lines[buf->cursor_y];
    int len = kstrlen(line);
    
    if (len >= MAX_LINE_LEN - 1) return;
    
    /* Save for undo */
    undo_push(UNDO_INSERT, buf->cursor_x, buf->cursor_y, c);
    
    /* Shift right and insert */
    for (int i = len + 1; i > buf->cursor_x; i--) {
        line[i] = line[i - 1];
    }
    line[buf->cursor_x] = c;
    buf->cursor_x++;
    buf->modified = 1;
}

/* Insert new line */
void edit_insert_line(void) {
    TextBuffer* buf = &buffers[current_buffer];
    
    if (buf->line_count >= MAX_LINES) return;
    
    /* Shift lines down */
    for (int i = buf->line_count; i > buf->cursor_y + 1; i--) {
        kstrcpy(buf->lines[i], buf->lines[i - 1]);
    }
    
    /* Split current line */
    char* current = buf->lines[buf->cursor_y];
    char* next = buf->lines[buf->cursor_y + 1];
    
    kstrcpy(next, &current[buf->cursor_x]);
    current[buf->cursor_x] = '\0';
    
    buf->line_count++;
    buf->cursor_y++;
    buf->cursor_x = 0;
    buf->modified = 1;
}

/* Delete character */
void edit_delete_char(void) {
    TextBuffer* buf = &buffers[current_buffer];
    char* line = buf->lines[buf->cursor_y];
    
    if (buf->cursor_x > 0) {
        undo_push(UNDO_DELETE, buf->cursor_x - 1, buf->cursor_y, line[buf->cursor_x - 1]);
        
        for (int i = buf->cursor_x - 1; line[i]; i++) {
            line[i] = line[i + 1];
        }
        buf->cursor_x--;
        buf->modified = 1;
    } else if (buf->cursor_y > 0) {
        /* Join with previous line */
        char* prev = buf->lines[buf->cursor_y - 1];
        int prev_len = kstrlen(prev);
        
        kstrcat(prev, line);
        
        /* Shift lines up */
        for (int i = buf->cursor_y; i < buf->line_count - 1; i++) {
            kstrcpy(buf->lines[i], buf->lines[i + 1]);
        }
        
        buf->line_count--;
        buf->cursor_y--;
        buf->cursor_x = prev_len;
        buf->modified = 1;
    }
}

/* Cursor movement */
void edit_move_cursor(int dx, int dy) {
    TextBuffer* buf = &buffers[current_buffer];
    
    buf->cursor_y += dy;
    if (buf->cursor_y < 0) buf->cursor_y = 0;
    if (buf->cursor_y >= buf->line_count) buf->cursor_y = buf->line_count - 1;
    
    buf->cursor_x += dx;
    int line_len = kstrlen(buf->lines[buf->cursor_y]);
    if (buf->cursor_x < 0) buf->cursor_x = 0;
    if (buf->cursor_x > line_len) buf->cursor_x = line_len;
    
    /* Scroll if needed */
    int visible_lines = 23;
    if (buf->cursor_y < buf->scroll_y) {
        buf->scroll_y = buf->cursor_y;
    }
    if (buf->cursor_y >= buf->scroll_y + visible_lines) {
        buf->scroll_y = buf->cursor_y - visible_lines + 1;
    }
}

/* Render editor */
void edit_render(void) {
    TextBuffer* buf = &buffers[current_buffer];
    int visible_lines = 23;
    
    clear_screen();
    
    /* Draw lines with syntax highlighting */
    for (int i = 0; i < visible_lines && buf->scroll_y + i < buf->line_count; i++) {
        int line_num = buf->scroll_y + i;
        
        /* Line number */
        set_color(0x08);
        kprint_int_padded(line_num + 1, 4);
        kprint(" ");
        
        /* Line content with syntax highlighting */
        render_syntax_line(buf->lines[line_num], buf->syntax_type, i);
    }
    
    /* Status bar */
    set_cursor(0, 24);
    set_color(0x70);  /* Inverted */
    
    /* Mode indicator */
    if (editor_mode == MODE_NORMAL) kprint(" NORMAL ");
    else if (editor_mode == MODE_INSERT) kprint(" INSERT ");
    else if (editor_mode == MODE_COMMAND) kprint(" COMMAND ");
    else if (editor_mode == MODE_VISUAL) kprint(" VISUAL ");
    
    /* File info */
    kprint(" | ");
    kprint(buf->filename);
    if (buf->modified) kprint(" [+]");
    
    /* Position */
    kprint(" | Ln ");
    kprint_int(buf->cursor_y + 1);
    kprint(", Col ");
    kprint_int(buf->cursor_x + 1);
    
    /* Status message */
    set_cursor(0, 25);
    set_color(0x0F);
    if (editor_mode == MODE_COMMAND) {
        kprint(":");
        kprint(command_line);
    } else {
        kprint(status_msg);
    }
    
    /* Position cursor */
    int screen_y = buf->cursor_y - buf->scroll_y;
    set_cursor(buf->cursor_x + 5, screen_y);
}

/* Handle input */
void edit_handle_key(unsigned char key) {
    TextBuffer* buf = &buffers[current_buffer];
    
    if (editor_mode == MODE_COMMAND) {
        if (key == '\n') {
            /* Execute command */
            edit_execute_command();
            editor_mode = MODE_NORMAL;
            command_pos = 0;
            command_line[0] = '\0';
        } else if (key == 27) {  /* ESC */
            editor_mode = MODE_NORMAL;
            command_pos = 0;
        } else if (key == 8) {  /* Backspace */
            if (command_pos > 0) command_line[--command_pos] = '\0';
        } else {
            command_line[command_pos++] = key;
            command_line[command_pos] = '\0';
        }
        return;
    }
    
    if (editor_mode == MODE_INSERT) {
        if (key == 27) {  /* ESC */
            editor_mode = MODE_NORMAL;
        } else if (key == '\n') {
            edit_insert_line();
        } else if (key == 8) {  /* Backspace */
            edit_delete_char();
        } else if (key >= 32 && key < 127) {
            edit_insert_char(key);
        }
        return;
    }
    
    /* Normal mode */
    switch (key) {
        case 'i': editor_mode = MODE_INSERT; break;
        case 'a': buf->cursor_x++; editor_mode = MODE_INSERT; break;
        case 'o': 
            edit_move_cursor(0, 1);
            edit_insert_line();
            editor_mode = MODE_INSERT;
            break;
        case ':': editor_mode = MODE_COMMAND; break;
        case 'h': edit_move_cursor(-1, 0); break;
        case 'j': edit_move_cursor(0, 1); break;
        case 'k': edit_move_cursor(0, -1); break;
        case 'l': edit_move_cursor(1, 0); break;
        case 'x': edit_delete_char(); break;
        case 'u': undo_pop(); break;
        case 'g': buf->cursor_y = 0; buf->scroll_y = 0; break;
        case 'G': buf->cursor_y = buf->line_count - 1; break;
        case '/': editor_mode = MODE_COMMAND; command_line[0] = '/'; command_pos = 1; break;
    }
}

/* Execute command */
void edit_execute_command(void) {
    if (command_line[0] == 'w') {
        edit_save();
    } else if (command_line[0] == 'q') {
        if (buffers[current_buffer].modified && command_line[1] != '!') {
            kstrcpy(status_msg, "Unsaved changes! Use :q! to force quit");
        } else {
            /* Exit editor */
            edit_cleanup();
        }
    } else if (kstrncmp(command_line, "wq", 2) == 0) {
        edit_save();
        edit_cleanup();
    } else if (command_line[0] == '/') {
        edit_search(&command_line[1]);
    } else if (command_line[0] == 'e' && command_line[1] == ' ') {
        edit_load_file(&command_line[2]);
    }
}

/* Search */
void edit_search(const char* pattern) {
    TextBuffer* buf = &buffers[current_buffer];
    
    for (int y = buf->cursor_y; y < buf->line_count; y++) {
        char* found = kstrstr(buf->lines[y], pattern);
        if (found) {
            buf->cursor_y = y;
            buf->cursor_x = found - buf->lines[y];
            kstrcpy(status_msg, "Found: ");
            kstrcat(status_msg, pattern);
            return;
        }
    }
    
    kstrcpy(status_msg, "Not found: ");
    kstrcat(status_msg, pattern);
}

/* Main editor loop */
void edit_main(const char* filename) {
    edit_init();
    edit_load_file(filename);
    current_buffer = 0;
    
    while (1) {
        edit_render();
        
        unsigned char key = read_key();
        if (key == 0) continue;
        
        edit_handle_key(key);
    }
}

void edit_cleanup(void) {
    /* Return to shell */
}

#endif /* FRAY_EDIT_C */
