package fraymus.os;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 📂 FRAY EXPLORER BUILDER
 * "The Lens through which we view the Memory."
 * 
 * FUNCTION:
 * 1. PARSE: Reads the 'system.img' raw bytes from RAM.
 * 2. RENDER: Draws a file list with icons.
 * 3. INTERACT: Arrow keys to scroll, Enter to open, 'D' to delete.
 */
public class FrayExplorerBuilder {

    public static void main(String[] args) {
        System.out.println("⚡ INSTALLING FILE EXPLORER...");
        try {
            buildExplorer();
            System.out.println("✅ FRAY-EXPLORER INSTALLED. Run 'explore' or click the Folder icon.");
        } catch (IOException e) {
            System.err.println("❌ INSTALL FAILED: " + e.getMessage());
        }
    }

    private static void buildExplorer() throws IOException {
        String cCode = 
            "/* ═══════════════════════════════════════════════════════════════════════════ */\n" +
            "/* FRAY-EXPLORER: VISUAL FILE MANAGER FOR FRAYNIX OS                          */\n" +
            "/* Reads FrayFS headers directly from Memory (system.img mapped to 0x200000)  */\n" +
            "/* ═══════════════════════════════════════════════════════════════════════════ */\n\n" +
            
            "#include \"fray_kernel.h\"\n" +
            "#include \"fray_vga.h\"\n" +
            "#include \"fray_string.h\"\n\n" +
            
            "/* ═══════════════════════════════════════════════════════════════════════════ */\n" +
            "/*                              DATA STRUCTURES                                 */\n" +
            "/* ═══════════════════════════════════════════════════════════════════════════ */\n\n" +
            
            "struct FileEntry {\n" +
            "    char name[32];\n" +
            "    int size;\n" +
            "    char* data_ptr;\n" +
            "    int active;       /* 1=Exists, 0=Deleted */\n" +
            "    int file_type;    /* 0=Unknown, 1=Text, 2=Binary, 3=Image, 4=Audio */\n" +
            "};\n\n" +

            "struct FileEntry files[64];  /* Cache up to 64 files */\n" +
            "int file_count = 0;\n" +
            "int selected_idx = 0;\n" +
            "int scroll_offset = 0;\n" +
            "int visible_rows = 8;\n" +
            "char* fs_base = (char*)0x200000;  /* Where system.img is loaded in RAM */\n\n" +

            "/* File Type Constants */\n" +
            "#define FTYPE_UNKNOWN  0\n" +
            "#define FTYPE_TEXT     1\n" +
            "#define FTYPE_BINARY   2\n" +
            "#define FTYPE_IMAGE    3\n" +
            "#define FTYPE_AUDIO    4\n\n" +

            "/* Color Palette */\n" +
            "#define COL_WHITE      15\n" +
            "#define COL_BLACK      0\n" +
            "#define COL_GREEN      2\n" +
            "#define COL_YELLOW     14\n" +
            "#define COL_CYAN       11\n" +
            "#define COL_GRAY       7\n\n" +

            "/* ═══════════════════════════════════════════════════════════════════════════ */\n" +
            "/*                           STRING UTILITIES                                   */\n" +
            "/* ═══════════════════════════════════════════════════════════════════════════ */\n\n" +

            "int str_ends_with(const char* str, const char* suffix) {\n" +
            "    int str_len = 0, suffix_len = 0;\n" +
            "    while(str[str_len]) str_len++;\n" +
            "    while(suffix[suffix_len]) suffix_len++;\n" +
            "    if (suffix_len > str_len) return 0;\n" +
            "    for(int i=0; i<suffix_len; i++) {\n" +
            "        if (str[str_len - suffix_len + i] != suffix[i]) return 0;\n" +
            "    }\n" +
            "    return 1;\n" +
            "}\n\n" +

            "int detect_file_type(const char* name) {\n" +
            "    if (str_ends_with(name, \".txt\") || str_ends_with(name, \".md\") || \n" +
            "        str_ends_with(name, \".c\") || str_ends_with(name, \".h\") ||\n" +
            "        str_ends_with(name, \".java\") || str_ends_with(name, \".py\")) {\n" +
            "        return FTYPE_TEXT;\n" +
            "    }\n" +
            "    if (str_ends_with(name, \".bin\") || str_ends_with(name, \".exe\") ||\n" +
            "        str_ends_with(name, \".elf\") || str_ends_with(name, \".com\")) {\n" +
            "        return FTYPE_BINARY;\n" +
            "    }\n" +
            "    if (str_ends_with(name, \".bmp\") || str_ends_with(name, \".png\") ||\n" +
            "        str_ends_with(name, \".jpg\") || str_ends_with(name, \".gif\")) {\n" +
            "        return FTYPE_IMAGE;\n" +
            "    }\n" +
            "    if (str_ends_with(name, \".wav\") || str_ends_with(name, \".mp3\") ||\n" +
            "        str_ends_with(name, \".ogg\")) {\n" +
            "        return FTYPE_AUDIO;\n" +
            "    }\n" +
            "    return FTYPE_UNKNOWN;\n" +
            "}\n\n" +

            "/* ═══════════════════════════════════════════════════════════════════════════ */\n" +
            "/*                           FILESYSTEM PARSER                                  */\n" +
            "/* ═══════════════════════════════════════════════════════════════════════════ */\n\n" +

            "void scan_disk() {\n" +
            "    file_count = 0;\n" +
            "    char* ptr = fs_base;\n" +
            "    \n" +
            "    /* Scan for FRAY magic headers */\n" +
            "    while(ptr[0] == 'F' && ptr[1] == 'R' && ptr[2] == 'A' && ptr[3] == 'Y') {\n" +
            "        if (file_count >= 64) break;  /* Max capacity */\n" +
            "        \n" +
            "        struct FileEntry* f = &files[file_count];\n" +
            "        \n" +
            "        /* Copy Name (32 bytes starting at offset 4) */\n" +
            "        for(int i=0; i<32; i++) {\n" +
            "            f->name[i] = ptr[4+i];\n" +
            "        }\n" +
            "        \n" +
            "        /* Read Size (Big Endian, 4 bytes at offset 36) */\n" +
            "        unsigned char* size_bytes = (unsigned char*)(ptr + 36);\n" +
            "        f->size = (size_bytes[0]<<24) | (size_bytes[1]<<16) | \n" +
            "                  (size_bytes[2]<<8) | size_bytes[3];\n" +
            "        \n" +
            "        /* Data starts at offset 64 (after header) */\n" +
            "        f->data_ptr = ptr + 64;\n" +
            "        f->active = 1;\n" +
            "        f->file_type = detect_file_type(f->name);\n" +
            "        \n" +
            "        file_count++;\n" +
            "        \n" +
            "        /* Jump to next file entry */\n" +
            "        ptr += (64 + f->size);\n" +
            "    }\n" +
            "}\n\n" +

            "/* ═══════════════════════════════════════════════════════════════════════════ */\n" +
            "/*                              FILE ACTIONS                                    */\n" +
            "/* ═══════════════════════════════════════════════════════════════════════════ */\n\n" +

            "void delete_file(int idx) {\n" +
            "    if (idx < 0 || idx >= file_count) return;\n" +
            "    if (files[idx].active == 0) return;\n" +
            "    \n" +
            "    /* Mark as deleted (lazy deletion - just hide from view) */\n" +
            "    files[idx].active = 0;\n" +
            "    kprint(\"[FS] FILE DELETED: \");\n" +
            "    kprint(files[idx].name);\n" +
            "    kprint(\"\\n\");\n" +
            "}\n\n" +

            "void open_file(int idx) {\n" +
            "    if (idx < 0 || idx >= file_count) return;\n" +
            "    if (files[idx].active == 0) return;\n" +
            "    \n" +
            "    struct FileEntry* f = &files[idx];\n" +
            "    \n" +
            "    switch(f->file_type) {\n" +
            "        case FTYPE_BINARY:\n" +
            "            /* EXECUTE: Jump to binary code in memory */\n" +
            "            kprint(\"[OS] EXECUTING BINARY: \");\n" +
            "            kprint(f->name);\n" +
            "            kprint(\"\\n\");\n" +
            "            void (*program)() = (void (*)())f->data_ptr;\n" +
            "            program();\n" +
            "            break;\n" +
            "            \n" +
            "        case FTYPE_TEXT:\n" +
            "            /* OPEN TEXT VIEWER */\n" +
            "            kprint(\"[OS] OPENING DOCUMENT: \");\n" +
            "            kprint(f->name);\n" +
            "            kprint(\"\\n──────────────────────────────────────\\n\");\n" +
            "            /* Print file content (null-terminated) */\n" +
            "            for(int i=0; i<f->size && f->data_ptr[i]; i++) {\n" +
            "                kputchar(f->data_ptr[i]);\n" +
            "            }\n" +
            "            kprint(\"\\n──────────────────────────────────────\\n\");\n" +
            "            kprint(\"Press any key to return...\\n\");\n" +
            "            get_char();\n" +
            "            break;\n" +
            "            \n" +
            "        case FTYPE_IMAGE:\n" +
            "            /* OPEN IMAGE VIEWER */\n" +
            "            kprint(\"[OS] OPENING IMAGE: \");\n" +
            "            kprint(f->name);\n" +
            "            kprint(\"\\n\");\n" +
            "            /* display_image(f->data_ptr, f->size); */\n" +
            "            kprint(\"[IMG] Image viewer placeholder\\n\");\n" +
            "            sleep(1000);\n" +
            "            break;\n" +
            "            \n" +
            "        case FTYPE_AUDIO:\n" +
            "            /* PLAY AUDIO */\n" +
            "            kprint(\"[OS] PLAYING AUDIO: \");\n" +
            "            kprint(f->name);\n" +
            "            kprint(\"\\n\");\n" +
            "            /* play_audio(f->data_ptr, f->size); */\n" +
            "            kprint(\"[SND] Audio player placeholder\\n\");\n" +
            "            break;\n" +
            "            \n" +
            "        default:\n" +
            "            kprint(\"[OS] UNKNOWN FILE TYPE: \");\n" +
            "            kprint(f->name);\n" +
            "            kprint(\"\\n\");\n" +
            "            break;\n" +
            "    }\n" +
            "}\n\n" +

            "/* ═══════════════════════════════════════════════════════════════════════════ */\n" +
            "/*                              UI RENDERER                                     */\n" +
            "/* ═══════════════════════════════════════════════════════════════════════════ */\n\n" +

            "char get_icon_for_type(int file_type) {\n" +
            "    switch(file_type) {\n" +
            "        case FTYPE_TEXT:   return 0x1D;  /* Document icon */\n" +
            "        case FTYPE_BINARY: return 0x0F;  /* Gear icon */\n" +
            "        case FTYPE_IMAGE:  return 0x02;  /* Picture icon */\n" +
            "        case FTYPE_AUDIO:  return 0x0D;  /* Music note */\n" +
            "        default:           return '?';   /* Unknown */\n" +
            "    }\n" +
            "}\n\n" +

            "int get_color_for_type(int file_type) {\n" +
            "    switch(file_type) {\n" +
            "        case FTYPE_TEXT:   return COL_WHITE;\n" +
            "        case FTYPE_BINARY: return COL_GREEN;\n" +
            "        case FTYPE_IMAGE:  return COL_CYAN;\n" +
            "        case FTYPE_AUDIO:  return COL_YELLOW;\n" +
            "        default:           return COL_GRAY;\n" +
            "    }\n" +
            "}\n\n" +

            "void format_size(int bytes, char* out) {\n" +
            "    if (bytes < 1024) {\n" +
            "        /* Display as bytes */\n" +
            "        int_to_str(bytes, out);\n" +
            "        str_append(out, \" B\");\n" +
            "    } else if (bytes < 1048576) {\n" +
            "        /* Display as KB */\n" +
            "        int_to_str(bytes / 1024, out);\n" +
            "        str_append(out, \" KB\");\n" +
            "    } else {\n" +
            "        /* Display as MB */\n" +
            "        int_to_str(bytes / 1048576, out);\n" +
            "        str_append(out, \" MB\");\n" +
            "    }\n" +
            "}\n\n" +

            "void draw_explorer() {\n" +
            "    /* Clear and draw window frame */\n" +
            "    clear_screen();\n" +
            "    draw_window(40, 30, 260, 170, \"FrayFiles\");\n" +
            "    \n" +
            "    /* Header bar */\n" +
            "    draw_rect(45, 50, 250, 12, COL_GRAY);\n" +
            "    draw_string(50, 52, \"NAME\", COL_WHITE);\n" +
            "    draw_string(180, 52, \"SIZE\", COL_WHITE);\n" +
            "    draw_string(230, 52, \"TYPE\", COL_WHITE);\n" +
            "    \n" +
            "    /* Draw file list */\n" +
            "    int y = 65;\n" +
            "    int visible_count = 0;\n" +
            "    int actual_idx = 0;\n" +
            "    \n" +
            "    for(int i=0; i<file_count && visible_count < visible_rows; i++) {\n" +
            "        if(files[i].active == 0) continue;  /* Skip deleted */\n" +
            "        \n" +
            "        /* Skip for scroll offset */\n" +
            "        if (actual_idx < scroll_offset) {\n" +
            "            actual_idx++;\n" +
            "            continue;\n" +
            "        }\n" +
            "        \n" +
            "        struct FileEntry* f = &files[i];\n" +
            "        \n" +
            "        /* Selection highlight */\n" +
            "        int text_color = get_color_for_type(f->file_type);\n" +
            "        if(i == selected_idx) {\n" +
            "            draw_rect(45, y, 250, 12, COL_GREEN);\n" +
            "            text_color = COL_BLACK;\n" +
            "        }\n" +
            "        \n" +
            "        /* Icon */\n" +
            "        char icon = get_icon_for_type(f->file_type);\n" +
            "        draw_char(50, y+2, icon, text_color);\n" +
            "        \n" +
            "        /* Filename */\n" +
            "        draw_string(60, y+2, f->name, text_color);\n" +
            "        \n" +
            "        /* Size */\n" +
            "        char size_str[16];\n" +
            "        format_size(f->size, size_str);\n" +
            "        draw_string(180, y+2, size_str, text_color);\n" +
            "        \n" +
            "        y += 12;\n" +
            "        visible_count++;\n" +
            "        actual_idx++;\n" +
            "    }\n" +
            "    \n" +
            "    /* Scroll indicator */\n" +
            "    if (file_count > visible_rows) {\n" +
            "        int bar_height = (visible_rows * 100) / file_count;\n" +
            "        int bar_y = 65 + (scroll_offset * 96) / file_count;\n" +
            "        draw_rect(298, bar_y, 4, bar_height, COL_WHITE);\n" +
            "    }\n" +
            "    \n" +
            "    /* Status bar */\n" +
            "    draw_rect(45, 180, 250, 14, COL_GRAY);\n" +
            "    draw_string(50, 182, \"[W/S] Navigate  [ENTER] Open  [D] Delete  [ESC] Exit\", COL_WHITE);\n" +
            "    \n" +
            "    /* File count */\n" +
            "    char count_str[32];\n" +
            "    int_to_str(file_count, count_str);\n" +
            "    str_append(count_str, \" files\");\n" +
            "    draw_string(250, 35, count_str, COL_CYAN);\n" +
            "}\n\n" +

            "/* ═══════════════════════════════════════════════════════════════════════════ */\n" +
            "/*                              MAIN LOOP                                       */\n" +
            "/* ═══════════════════════════════════════════════════════════════════════════ */\n\n" +

            "void start_explorer() {\n" +
            "    /* Initialize */\n" +
            "    scan_disk();\n" +
            "    init_vga();\n" +
            "    selected_idx = 0;\n" +
            "    scroll_offset = 0;\n" +
            "    \n" +
            "    kprint(\"[FS] FRAY EXPLORER INITIALIZED. Found \");\n" +
            "    char count_str[8];\n" +
            "    int_to_str(file_count, count_str);\n" +
            "    kprint(count_str);\n" +
            "    kprint(\" files.\\n\");\n" +
            "    \n" +
            "    while(1) {\n" +
            "        draw_explorer();\n" +
            "        \n" +
            "        char key = get_char();\n" +
            "        \n" +
            "        /* Navigation */\n" +
            "        if (key == 'w' || key == 'W') {\n" +
            "            /* Move up */\n" +
            "            do {\n" +
            "                selected_idx--;\n" +
            "                if (selected_idx < 0) {\n" +
            "                    selected_idx = 0;\n" +
            "                    break;\n" +
            "                }\n" +
            "            } while (files[selected_idx].active == 0);\n" +
            "            \n" +
            "            /* Adjust scroll */\n" +
            "            if (selected_idx < scroll_offset) {\n" +
            "                scroll_offset = selected_idx;\n" +
            "            }\n" +
            "        }\n" +
            "        \n" +
            "        if (key == 's' || key == 'S') {\n" +
            "            /* Move down */\n" +
            "            do {\n" +
            "                selected_idx++;\n" +
            "                if (selected_idx >= file_count) {\n" +
            "                    selected_idx = file_count - 1;\n" +
            "                    break;\n" +
            "                }\n" +
            "            } while (files[selected_idx].active == 0);\n" +
            "            \n" +
            "            /* Adjust scroll */\n" +
            "            if (selected_idx >= scroll_offset + visible_rows) {\n" +
            "                scroll_offset = selected_idx - visible_rows + 1;\n" +
            "            }\n" +
            "        }\n" +
            "        \n" +
            "        /* Actions */\n" +
            "        if (key == '\\n' || key == '\\r') {\n" +
            "            open_file(selected_idx);\n" +
            "        }\n" +
            "        \n" +
            "        if (key == 'd' || key == 'D') {\n" +
            "            delete_file(selected_idx);\n" +
            "        }\n" +
            "        \n" +
            "        /* Refresh */\n" +
            "        if (key == 'r' || key == 'R') {\n" +
            "            scan_disk();\n" +
            "            selected_idx = 0;\n" +
            "            scroll_offset = 0;\n" +
            "        }\n" +
            "        \n" +
            "        /* Exit */\n" +
            "        if (key == 0x1B) {  /* ESC */\n" +
            "            return;\n" +
            "        }\n" +
            "    }\n" +
            "}\n\n" +

            "/* ═══════════════════════════════════════════════════════════════════════════ */\n" +
            "/*                           ENTRY POINT                                        */\n" +
            "/* ═══════════════════════════════════════════════════════════════════════════ */\n\n" +

            "int main() {\n" +
            "    start_explorer();\n" +
            "    return 0;\n" +
            "}\n";

        writeFile("fraynix_src/explorer.c", cCode);
    }
    
    private static void writeFile(String path, String content) throws IOException {
        java.io.File file = new java.io.File(path);
        file.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
        }
        System.out.println("   → " + path + " (" + content.length() + " bytes)");
    }
}
