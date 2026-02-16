/* FRAY-FILES - Gen 151 */
#ifndef FRAY_FILES_C
#define FRAY_FILES_C

#define MAX_ENTRIES 256

typedef struct {
    char name[64];
    int is_dir;
    unsigned int size;
    unsigned long long hash;
} FileEntry;

static FileEntry file_list[MAX_ENTRIES];
static int file_count = 0;
static int selected = 0;
static int scroll = 0;
static char current_path[256] = "/";

void files_scan_dir(const char* path) {
    file_count = 0;
    kstrcpy(current_path, path);
    
    /* Add parent dir */
    if (kstrcmp(path, "/") != 0) {
        kstrcpy(file_list[file_count].name, "..");
        file_list[file_count].is_dir = 1;
        file_list[file_count].size = 0;
        file_count++;
    }
    
    /* Scan hash memory for entries starting with this path */
    /* (simplified - real impl would iterate hash table) */
}

void files_draw(void) {
    clear_screen();
    kprint("  FRAY-FILES | ");
    kprint(current_path);
    kprint("\n");
    kprint("  ════════════════════════════════════════\n");
    
    int visible = 20;
    for (int i = 0; i < visible && scroll + i < file_count; i++) {
        FileEntry* f = &file_list[scroll + i];
        
        if (scroll + i == selected) {
            set_color(0x70);  /* Highlight */
        } else {
            set_color(f->is_dir ? 0x0B : 0x0F);
        }
        
        kprint("  ");
        if (f->is_dir) kprint("[D] "); else kprint("    ");
        kprint(f->name);
        
        if (!f->is_dir) {
            kprint(" (");
            kprint_int(f->size);
            kprint(")");
        }
        kprint("\n");
        set_color(0x0F);
    }
    
    kprint("\n  [Enter] Open  [D]elete  [R]ename  [Q]uit\n");
}

void files_open(void) {
    FileEntry* f = &file_list[selected];
    
    if (f->is_dir) {
        if (kstrcmp(f->name, "..") == 0) {
            /* Go up */
            int len = kstrlen(current_path);
            while (len > 1 && current_path[len-1] != '/') len--;
            if (len > 1) len--;
            current_path[len] = '\0';
        } else {
            kstrcat(current_path, "/");
            kstrcat(current_path, f->name);
        }
        files_scan_dir(current_path);
        selected = 0;
    } else {
        /* Open file in editor */
        char full_path[320];
        kstrcpy(full_path, current_path);
        kstrcat(full_path, "/");
        kstrcat(full_path, f->name);
        edit_main(full_path);
    }
}

void files_main(void) {
    files_scan_dir("/");
    
    while (1) {
        files_draw();
        
        char k = read_key();
        if (k == 'q') return;
        if (k == 'j' || k == 2) { selected++; if (selected >= file_count) selected = file_count - 1; }
        if (k == 'k' || k == 3) { selected--; if (selected < 0) selected = 0; }
        if (k == '\n') files_open();
    }
}
#endif
