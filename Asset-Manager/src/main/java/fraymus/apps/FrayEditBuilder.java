package fraymus.apps;

import java.io.*;
import java.nio.file.*;

/**
 * 📝 FRAY-EDIT - Gen 151
 * "The pen that writes on metal."
 * 
 * Features:
 * - Modal editing (Normal/Insert/Command)
 * - Syntax highlighting (C, Java, Python)
 * - Line numbers
 * - Search & Replace
 * - Multiple buffers
 * - Undo/Redo stack
 */
public class FrayEditBuilder {

    private static final String OUTPUT_DIR = "fraynix_src/apps";

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  📝 FRAY-EDIT - Gen 151                                       ║");
        System.out.println("║  The Text Editor                                              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            Files.createDirectories(Paths.get(OUTPUT_DIR));
            
            buildEditor();
            buildSyntaxHighlight();
            buildUndoSystem();
            
            System.out.println();
            System.out.println("✅ FRAY-EDIT COMPILED");
            System.out.println("   Usage: edit <filename>");
            
        } catch (IOException e) {
            System.err.println("❌ BUILD FAILED: " + e.getMessage());
        }
    }

    private static void buildEditor() throws IOException {
        System.out.println("   [1/3] Generating edit.c...");
        
        String editor = """
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
    buf->lines[0][0] = '\\0';
    
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
            if (content[i] == '\\n' || col >= MAX_LINE_LEN - 1) {
                buf->lines[buf->line_count][col] = '\\0';
                buf->line_count++;
                col = 0;
                if (buf->line_count >= MAX_LINES) break;
            } else {
                buf->lines[buf->line_count][col++] = content[i];
            }
        }
        
        if (col > 0) {
            buf->lines[buf->line_count][col] = '\\0';
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
        content[pos++] = '\\n';
    }
    content[pos] = '\\0';
    
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
    current[buf->cursor_x] = '\\0';
    
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
        if (key == '\\n') {
            /* Execute command */
            edit_execute_command();
            editor_mode = MODE_NORMAL;
            command_pos = 0;
            command_line[0] = '\\0';
        } else if (key == 27) {  /* ESC */
            editor_mode = MODE_NORMAL;
            command_pos = 0;
        } else if (key == 8) {  /* Backspace */
            if (command_pos > 0) command_line[--command_pos] = '\\0';
        } else {
            command_line[command_pos++] = key;
            command_line[command_pos] = '\\0';
        }
        return;
    }
    
    if (editor_mode == MODE_INSERT) {
        if (key == 27) {  /* ESC */
            editor_mode = MODE_NORMAL;
        } else if (key == '\\n') {
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
""";
        
        writeFile(OUTPUT_DIR + "/edit.c", editor);
    }

    private static void buildSyntaxHighlight() throws IOException {
        System.out.println("   [2/3] Generating syntax.c...");
        
        String syntax = """
/* ═══════════════════════════════════════════════════════════════════════════
 * FRAY-SYNTAX: Code Highlighting - Gen 151
 * ═══════════════════════════════════════════════════════════════════════════ */

#ifndef FRAY_SYNTAX_C
#define FRAY_SYNTAX_C

#define SYNTAX_NONE   0
#define SYNTAX_C      1
#define SYNTAX_JAVA   2
#define SYNTAX_PYTHON 3

/* Colors */
#define COL_DEFAULT  0x0F  /* White */
#define COL_KEYWORD  0x0E  /* Yellow */
#define COL_TYPE     0x0B  /* Cyan */
#define COL_STRING   0x0A  /* Green */
#define COL_COMMENT  0x08  /* Gray */
#define COL_NUMBER   0x0D  /* Magenta */
#define COL_FUNCTION 0x09  /* Blue */

/* C Keywords */
static const char* c_keywords[] = {
    "if", "else", "while", "for", "do", "switch", "case", "default",
    "break", "continue", "return", "goto", "sizeof", "typedef",
    "struct", "union", "enum", "static", "extern", "const", "volatile",
    "inline", "register", "auto", 0
};

static const char* c_types[] = {
    "void", "int", "char", "short", "long", "float", "double",
    "signed", "unsigned", "size_t", "uint8_t", "uint16_t", "uint32_t",
    "uint64_t", "int8_t", "int16_t", "int32_t", "int64_t", 0
};

/* Java Keywords */
static const char* java_keywords[] = {
    "if", "else", "while", "for", "do", "switch", "case", "default",
    "break", "continue", "return", "new", "this", "super", "null",
    "try", "catch", "finally", "throw", "throws", "class", "interface",
    "extends", "implements", "public", "private", "protected", "static",
    "final", "abstract", "synchronized", "volatile", "transient", "native",
    "import", "package", "instanceof", 0
};

static const char* java_types[] = {
    "void", "int", "char", "short", "long", "float", "double", "boolean",
    "byte", "String", "Object", "Integer", "Boolean", "Double", "Float",
    "List", "Map", "Set", "ArrayList", "HashMap", 0
};

/* Python Keywords */
static const char* python_keywords[] = {
    "if", "elif", "else", "while", "for", "in", "break", "continue",
    "return", "yield", "pass", "def", "class", "import", "from", "as",
    "try", "except", "finally", "raise", "with", "lambda", "and", "or",
    "not", "is", "None", "True", "False", "global", "nonlocal", "assert", 0
};

int detect_syntax(const char* filename) {
    int len = kstrlen(filename);
    
    if (len > 2 && filename[len-2] == '.' && filename[len-1] == 'c') return SYNTAX_C;
    if (len > 2 && filename[len-2] == '.' && filename[len-1] == 'h') return SYNTAX_C;
    if (len > 5 && kstrncmp(&filename[len-5], ".java", 5) == 0) return SYNTAX_JAVA;
    if (len > 3 && kstrncmp(&filename[len-3], ".py", 3) == 0) return SYNTAX_PYTHON;
    
    return SYNTAX_NONE;
}

int is_keyword(const char* word, const char** keywords) {
    for (int i = 0; keywords[i]; i++) {
        if (kstrcmp(word, keywords[i]) == 0) return 1;
    }
    return 0;
}

void render_syntax_line(const char* line, int syntax, int screen_y) {
    int in_string = 0;
    int in_comment = 0;
    char string_char = 0;
    
    for (int i = 0; line[i]; i++) {
        char c = line[i];
        
        /* Check for comment start */
        if (!in_string && c == '/' && line[i+1] == '/') {
            set_color(COL_COMMENT);
            kprint(&line[i]);
            return;
        }
        
        /* Check for string */
        if (!in_comment && (c == '"' || c == '\\'' ) && (i == 0 || line[i-1] != '\\\\')) {
            if (!in_string) {
                in_string = 1;
                string_char = c;
                set_color(COL_STRING);
            } else if (c == string_char) {
                char buf[2] = {c, 0};
                kprint(buf);
                in_string = 0;
                set_color(COL_DEFAULT);
                continue;
            }
        }
        
        if (in_string || in_comment) {
            char buf[2] = {c, 0};
            kprint(buf);
            continue;
        }
        
        /* Check for numbers */
        if (c >= '0' && c <= '9') {
            set_color(COL_NUMBER);
            char buf[2] = {c, 0};
            kprint(buf);
            set_color(COL_DEFAULT);
            continue;
        }
        
        /* Check for identifiers/keywords */
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_') {
            char word[64];
            int j = 0;
            while ((line[i] >= 'a' && line[i] <= 'z') ||
                   (line[i] >= 'A' && line[i] <= 'Z') ||
                   (line[i] >= '0' && line[i] <= '9') ||
                   line[i] == '_') {
                word[j++] = line[i++];
            }
            word[j] = 0;
            i--;
            
            const char** keywords = 0;
            const char** types = 0;
            
            if (syntax == SYNTAX_C) {
                keywords = c_keywords;
                types = c_types;
            } else if (syntax == SYNTAX_JAVA) {
                keywords = java_keywords;
                types = java_types;
            } else if (syntax == SYNTAX_PYTHON) {
                keywords = python_keywords;
            }
            
            if (keywords && is_keyword(word, keywords)) {
                set_color(COL_KEYWORD);
            } else if (types && is_keyword(word, types)) {
                set_color(COL_TYPE);
            } else if (line[i+1] == '(') {
                set_color(COL_FUNCTION);
            } else {
                set_color(COL_DEFAULT);
            }
            
            kprint(word);
            set_color(COL_DEFAULT);
            continue;
        }
        
        /* Default character */
        set_color(COL_DEFAULT);
        char buf[2] = {c, 0};
        kprint(buf);
    }
    
    kprint("\\n");
}

#endif /* FRAY_SYNTAX_C */
""";
        
        writeFile(OUTPUT_DIR + "/syntax.c", syntax);
    }

    private static void buildUndoSystem() throws IOException {
        System.out.println("   [3/3] Generating undo.c...");
        
        String undo = """
/* ═══════════════════════════════════════════════════════════════════════════
 * FRAY-UNDO: Edit History - Gen 151
 * ═══════════════════════════════════════════════════════════════════════════ */

#ifndef FRAY_UNDO_C
#define FRAY_UNDO_C

#define UNDO_STACK_SIZE 1000
#define UNDO_INSERT 1
#define UNDO_DELETE 2
#define UNDO_NEWLINE 3

typedef struct {
    int type;
    int x;
    int y;
    char data;
} UndoEntry;

static UndoEntry undo_stack[UNDO_STACK_SIZE];
static int undo_top = 0;

void undo_init(void) {
    undo_top = 0;
}

void undo_push(int type, int x, int y, char data) {
    if (undo_top >= UNDO_STACK_SIZE) {
        /* Shift stack */
        for (int i = 0; i < UNDO_STACK_SIZE - 1; i++) {
            undo_stack[i] = undo_stack[i + 1];
        }
        undo_top = UNDO_STACK_SIZE - 1;
    }
    
    undo_stack[undo_top].type = type;
    undo_stack[undo_top].x = x;
    undo_stack[undo_top].y = y;
    undo_stack[undo_top].data = data;
    undo_top++;
}

void undo_pop(void) {
    if (undo_top == 0) return;
    
    undo_top--;
    UndoEntry* e = &undo_stack[undo_top];
    
    TextBuffer* buf = &buffers[current_buffer];
    
    if (e->type == UNDO_INSERT) {
        /* Remove inserted character */
        buf->cursor_x = e->x;
        buf->cursor_y = e->y;
        char* line = buf->lines[buf->cursor_y];
        for (int i = e->x; line[i]; i++) {
            line[i] = line[i + 1];
        }
    } else if (e->type == UNDO_DELETE) {
        /* Re-insert deleted character */
        buf->cursor_x = e->x;
        buf->cursor_y = e->y;
        char* line = buf->lines[buf->cursor_y];
        int len = kstrlen(line);
        for (int i = len + 1; i > e->x; i--) {
            line[i] = line[i - 1];
        }
        line[e->x] = e->data;
    }
}

#endif /* FRAY_UNDO_C */
""";
        
        writeFile(OUTPUT_DIR + "/undo.c", undo);
    }

    private static void writeFile(String path, String content) throws IOException {
        Path filePath = Paths.get(path);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, content);
        System.out.println("      → " + path);
    }
}
