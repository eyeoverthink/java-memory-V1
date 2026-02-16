/* ═══════════════════════════════════════════════════════════════════════════ */
/* FRAY-EXPLORER: VISUAL FILE MANAGER FOR FRAYNIX OS                          */
/* Reads FrayFS headers directly from Memory (system.img mapped to 0x200000)  */
/* ═══════════════════════════════════════════════════════════════════════════ */

#include "fray_kernel.h"
#include "fray_vga.h"
#include "fray_string.h"

/* ═══════════════════════════════════════════════════════════════════════════ */
/*                              DATA STRUCTURES                                 */
/* ═══════════════════════════════════════════════════════════════════════════ */

struct FileEntry {
    char name[32];
    int size;
    char* data_ptr;
    int active;       /* 1=Exists, 0=Deleted */
    int file_type;    /* 0=Unknown, 1=Text, 2=Binary, 3=Image, 4=Audio */
};

struct FileEntry files[64];  /* Cache up to 64 files */
int file_count = 0;
int selected_idx = 0;
int scroll_offset = 0;
int visible_rows = 8;
char* fs_base = (char*)0x200000;  /* Where system.img is loaded in RAM */

/* File Type Constants */
#define FTYPE_UNKNOWN  0
#define FTYPE_TEXT     1
#define FTYPE_BINARY   2
#define FTYPE_IMAGE    3
#define FTYPE_AUDIO    4

/* Color Palette */
#define COL_WHITE      15
#define COL_BLACK      0
#define COL_GREEN      2
#define COL_YELLOW     14
#define COL_CYAN       11
#define COL_GRAY       7

/* ═══════════════════════════════════════════════════════════════════════════ */
/*                           STRING UTILITIES                                   */
/* ═══════════════════════════════════════════════════════════════════════════ */

int str_ends_with(const char* str, const char* suffix) {
    int str_len = 0, suffix_len = 0;
    while(str[str_len]) str_len++;
    while(suffix[suffix_len]) suffix_len++;
    if (suffix_len > str_len) return 0;
    for(int i=0; i<suffix_len; i++) {
        if (str[str_len - suffix_len + i] != suffix[i]) return 0;
    }
    return 1;
}

int detect_file_type(const char* name) {
    if (str_ends_with(name, ".txt") || str_ends_with(name, ".md") || 
        str_ends_with(name, ".c") || str_ends_with(name, ".h") ||
        str_ends_with(name, ".java") || str_ends_with(name, ".py")) {
        return FTYPE_TEXT;
    }
    if (str_ends_with(name, ".bin") || str_ends_with(name, ".exe") ||
        str_ends_with(name, ".elf") || str_ends_with(name, ".com")) {
        return FTYPE_BINARY;
    }
    if (str_ends_with(name, ".bmp") || str_ends_with(name, ".png") ||
        str_ends_with(name, ".jpg") || str_ends_with(name, ".gif")) {
        return FTYPE_IMAGE;
    }
    if (str_ends_with(name, ".wav") || str_ends_with(name, ".mp3") ||
        str_ends_with(name, ".ogg")) {
        return FTYPE_AUDIO;
    }
    return FTYPE_UNKNOWN;
}

/* ═══════════════════════════════════════════════════════════════════════════ */
/*                           FILESYSTEM PARSER                                  */
/* ═══════════════════════════════════════════════════════════════════════════ */

void scan_disk() {
    file_count = 0;
    char* ptr = fs_base;
    
    /* Scan for FRAY magic headers */
    while(ptr[0] == 'F' && ptr[1] == 'R' && ptr[2] == 'A' && ptr[3] == 'Y') {
        if (file_count >= 64) break;  /* Max capacity */
        
        struct FileEntry* f = &files[file_count];
        
        /* Copy Name (32 bytes starting at offset 4) */
        for(int i=0; i<32; i++) {
            f->name[i] = ptr[4+i];
        }
        
        /* Read Size (Big Endian, 4 bytes at offset 36) */
        unsigned char* size_bytes = (unsigned char*)(ptr + 36);
        f->size = (size_bytes[0]<<24) | (size_bytes[1]<<16) | 
                  (size_bytes[2]<<8) | size_bytes[3];
        
        /* Data starts at offset 64 (after header) */
        f->data_ptr = ptr + 64;
        f->active = 1;
        f->file_type = detect_file_type(f->name);
        
        file_count++;
        
        /* Jump to next file entry */
        ptr += (64 + f->size);
    }
}

/* ═══════════════════════════════════════════════════════════════════════════ */
/*                              FILE ACTIONS                                    */
/* ═══════════════════════════════════════════════════════════════════════════ */

void delete_file(int idx) {
    if (idx < 0 || idx >= file_count) return;
    if (files[idx].active == 0) return;
    
    /* Mark as deleted (lazy deletion - just hide from view) */
    files[idx].active = 0;
    kprint("[FS] FILE DELETED: ");
    kprint(files[idx].name);
    kprint("\n");
}

void open_file(int idx) {
    if (idx < 0 || idx >= file_count) return;
    if (files[idx].active == 0) return;
    
    struct FileEntry* f = &files[idx];
    
    switch(f->file_type) {
        case FTYPE_BINARY:
            /* EXECUTE: Jump to binary code in memory */
            kprint("[OS] EXECUTING BINARY: ");
            kprint(f->name);
            kprint("\n");
            void (*program)() = (void (*)())f->data_ptr;
            program();
            break;
            
        case FTYPE_TEXT:
            /* OPEN TEXT VIEWER */
            kprint("[OS] OPENING DOCUMENT: ");
            kprint(f->name);
            kprint("\n──────────────────────────────────────\n");
            /* Print file content (null-terminated) */
            for(int i=0; i<f->size && f->data_ptr[i]; i++) {
                kputchar(f->data_ptr[i]);
            }
            kprint("\n──────────────────────────────────────\n");
            kprint("Press any key to return...\n");
            get_char();
            break;
            
        case FTYPE_IMAGE:
            /* OPEN IMAGE VIEWER */
            kprint("[OS] OPENING IMAGE: ");
            kprint(f->name);
            kprint("\n");
            /* display_image(f->data_ptr, f->size); */
            kprint("[IMG] Image viewer placeholder\n");
            sleep(1000);
            break;
            
        case FTYPE_AUDIO:
            /* PLAY AUDIO */
            kprint("[OS] PLAYING AUDIO: ");
            kprint(f->name);
            kprint("\n");
            /* play_audio(f->data_ptr, f->size); */
            kprint("[SND] Audio player placeholder\n");
            break;
            
        default:
            kprint("[OS] UNKNOWN FILE TYPE: ");
            kprint(f->name);
            kprint("\n");
            break;
    }
}

/* ═══════════════════════════════════════════════════════════════════════════ */
/*                              UI RENDERER                                     */
/* ═══════════════════════════════════════════════════════════════════════════ */

char get_icon_for_type(int file_type) {
    switch(file_type) {
        case FTYPE_TEXT:   return 0x1D;  /* Document icon */
        case FTYPE_BINARY: return 0x0F;  /* Gear icon */
        case FTYPE_IMAGE:  return 0x02;  /* Picture icon */
        case FTYPE_AUDIO:  return 0x0D;  /* Music note */
        default:           return '?';   /* Unknown */
    }
}

int get_color_for_type(int file_type) {
    switch(file_type) {
        case FTYPE_TEXT:   return COL_WHITE;
        case FTYPE_BINARY: return COL_GREEN;
        case FTYPE_IMAGE:  return COL_CYAN;
        case FTYPE_AUDIO:  return COL_YELLOW;
        default:           return COL_GRAY;
    }
}

void format_size(int bytes, char* out) {
    if (bytes < 1024) {
        /* Display as bytes */
        int_to_str(bytes, out);
        str_append(out, " B");
    } else if (bytes < 1048576) {
        /* Display as KB */
        int_to_str(bytes / 1024, out);
        str_append(out, " KB");
    } else {
        /* Display as MB */
        int_to_str(bytes / 1048576, out);
        str_append(out, " MB");
    }
}

void draw_explorer() {
    /* Clear and draw window frame */
    clear_screen();
    draw_window(40, 30, 260, 170, "FrayFiles");
    
    /* Header bar */
    draw_rect(45, 50, 250, 12, COL_GRAY);
    draw_string(50, 52, "NAME", COL_WHITE);
    draw_string(180, 52, "SIZE", COL_WHITE);
    draw_string(230, 52, "TYPE", COL_WHITE);
    
    /* Draw file list */
    int y = 65;
    int visible_count = 0;
    int actual_idx = 0;
    
    for(int i=0; i<file_count && visible_count < visible_rows; i++) {
        if(files[i].active == 0) continue;  /* Skip deleted */
        
        /* Skip for scroll offset */
        if (actual_idx < scroll_offset) {
            actual_idx++;
            continue;
        }
        
        struct FileEntry* f = &files[i];
        
        /* Selection highlight */
        int text_color = get_color_for_type(f->file_type);
        if(i == selected_idx) {
            draw_rect(45, y, 250, 12, COL_GREEN);
            text_color = COL_BLACK;
        }
        
        /* Icon */
        char icon = get_icon_for_type(f->file_type);
        draw_char(50, y+2, icon, text_color);
        
        /* Filename */
        draw_string(60, y+2, f->name, text_color);
        
        /* Size */
        char size_str[16];
        format_size(f->size, size_str);
        draw_string(180, y+2, size_str, text_color);
        
        y += 12;
        visible_count++;
        actual_idx++;
    }
    
    /* Scroll indicator */
    if (file_count > visible_rows) {
        int bar_height = (visible_rows * 100) / file_count;
        int bar_y = 65 + (scroll_offset * 96) / file_count;
        draw_rect(298, bar_y, 4, bar_height, COL_WHITE);
    }
    
    /* Status bar */
    draw_rect(45, 180, 250, 14, COL_GRAY);
    draw_string(50, 182, "[W/S] Navigate  [ENTER] Open  [D] Delete  [ESC] Exit", COL_WHITE);
    
    /* File count */
    char count_str[32];
    int_to_str(file_count, count_str);
    str_append(count_str, " files");
    draw_string(250, 35, count_str, COL_CYAN);
}

/* ═══════════════════════════════════════════════════════════════════════════ */
/*                              MAIN LOOP                                       */
/* ═══════════════════════════════════════════════════════════════════════════ */

void start_explorer() {
    /* Initialize */
    scan_disk();
    init_vga();
    selected_idx = 0;
    scroll_offset = 0;
    
    kprint("[FS] FRAY EXPLORER INITIALIZED. Found ");
    char count_str[8];
    int_to_str(file_count, count_str);
    kprint(count_str);
    kprint(" files.\n");
    
    while(1) {
        draw_explorer();
        
        char key = get_char();
        
        /* Navigation */
        if (key == 'w' || key == 'W') {
            /* Move up */
            do {
                selected_idx--;
                if (selected_idx < 0) {
                    selected_idx = 0;
                    break;
                }
            } while (files[selected_idx].active == 0);
            
            /* Adjust scroll */
            if (selected_idx < scroll_offset) {
                scroll_offset = selected_idx;
            }
        }
        
        if (key == 's' || key == 'S') {
            /* Move down */
            do {
                selected_idx++;
                if (selected_idx >= file_count) {
                    selected_idx = file_count - 1;
                    break;
                }
            } while (files[selected_idx].active == 0);
            
            /* Adjust scroll */
            if (selected_idx >= scroll_offset + visible_rows) {
                scroll_offset = selected_idx - visible_rows + 1;
            }
        }
        
        /* Actions */
        if (key == '\n' || key == '\r') {
            open_file(selected_idx);
        }
        
        if (key == 'd' || key == 'D') {
            delete_file(selected_idx);
        }
        
        /* Refresh */
        if (key == 'r' || key == 'R') {
            scan_disk();
            selected_idx = 0;
            scroll_offset = 0;
        }
        
        /* Exit */
        if (key == 0x1B) {  /* ESC */
            return;
        }
    }
}

/* ═══════════════════════════════════════════════════════════════════════════ */
/*                           ENTRY POINT                                        */
/* ═══════════════════════════════════════════════════════════════════════════ */

int main() {
    start_explorer();
    return 0;
}
