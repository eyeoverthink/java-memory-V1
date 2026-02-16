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
        if (!in_comment && (c == '"' || c == '\'') && (i == 0 || line[i-1] != '\\')) {
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
    
    kprint("\n");
}

#endif /* FRAY_SYNTAX_C */
