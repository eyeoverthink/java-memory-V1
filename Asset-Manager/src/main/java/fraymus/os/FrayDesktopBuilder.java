package fraymus.os;

import java.io.*;
import java.nio.file.*;

/**
 * 🖥️ FRAY DESKTOP BUILDER - Gen 146
 * "The Interface is the System."
 * 
 * Fraymus Desktop Environment (FDE)
 * 
 * Features:
 *   - Window compositor with shadows
 *   - PS/2 mouse driver
 *   - Event-driven UI
 *   - Theme: Cyber-Gold (Obsidian + Gold)
 *   - Desktop icons and taskbar
 * 
 * "The Face is on."
 */
public class FrayDesktopBuilder {

    private static final String OUTPUT_DIR = "fraynix_src";

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  🖥️ FRAY DESKTOP BUILDER - Gen 146                            ║");
        System.out.println("║  Fraymus Desktop Environment                                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            Files.createDirectories(Paths.get(OUTPUT_DIR));
            
            System.out.println("⚡ Generating desktop environment...");
            
            buildGUIEngine();
            buildMouseDriver();
            buildWidgets();
            
            System.out.println();
            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║  ✅ FRAY DESKTOP INSTALLED                                    ║");
            System.out.println("║                                                               ║");
            System.out.println("║  Output: fraynix_src/gui.c                                   ║");
            System.out.println("║          fraynix_src/mouse.c                                 ║");
            System.out.println("║          fraynix_src/widgets.c                               ║");
            System.out.println("║                                                               ║");
            System.out.println("║  Theme: Cyber-Gold (Obsidian + Gold)                         ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");
            
        } catch (IOException e) {
            System.err.println("❌ BUILD FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void buildGUIEngine() throws IOException {
        System.out.println("   [1/3] Generating gui.c...");
        
        String gui = """
/* FRAY-GUI: DESKTOP ENVIRONMENT - Gen 146 */

#ifndef FRAY_GUI_C
#define FRAY_GUI_C

#include "mouse.c"
#include "widgets.c"

/* Theme Colors */
#define COLOR_BG        1    /* Deep Blue */
#define COLOR_TASKBAR   23   /* Silver */
#define COLOR_WINDOW    24   /* Dark Gray */
#define COLOR_TITLE     14   /* Gold */
#define COLOR_SHADOW    0    /* Black */
#define COLOR_TEXT      15   /* White */
#define COLOR_BUTTON    8    /* Gray */
#define COLOR_CLOSE     4    /* Red */

/* Desktop state */
static int desktop_running = 1;
static int active_window = -1;

/* Icons */
typedef struct {
    int x, y;
    char name[16];
    unsigned char color;
    void (*action)(void);
} DesktopIcon;

static DesktopIcon icons[8];
static int icon_count = 0;

/* Windows */
typedef struct {
    int x, y, w, h;
    char title[32];
    int visible;
    int dragging;
    int drag_x, drag_y;
} Window;

#define MAX_WINDOWS 8
static Window windows[MAX_WINDOWS];
static int window_count = 0;

/* Forward declarations */
void doom_loop(void);
void cmd_info(void);
void compiler_help(void);

void desktop_add_icon(int x, int y, const char* name, unsigned char color, void (*action)(void)) {
    if (icon_count >= 8) return;
    icons[icon_count].x = x;
    icons[icon_count].y = y;
    for (int i = 0; name[i] && i < 15; i++) icons[icon_count].name[i] = name[i];
    icons[icon_count].color = color;
    icons[icon_count].action = action;
    icon_count++;
}

int desktop_create_window(int x, int y, int w, int h, const char* title) {
    if (window_count >= MAX_WINDOWS) return -1;
    windows[window_count].x = x;
    windows[window_count].y = y;
    windows[window_count].w = w;
    windows[window_count].h = h;
    for (int i = 0; title[i] && i < 31; i++) windows[window_count].title[i] = title[i];
    windows[window_count].visible = 1;
    windows[window_count].dragging = 0;
    return window_count++;
}

void desktop_draw_icon(DesktopIcon* icon) {
    vga_fill_rect(icon->x, icon->y, 32, 32, icon->color);
    vga_rect(icon->x, icon->y, 32, 32, 15);
}

void desktop_draw_window(Window* win) {
    if (!win->visible) return;
    
    /* Shadow */
    vga_fill_rect(win->x + 3, win->y + 3, win->w, win->h, COLOR_SHADOW);
    
    /* Window body */
    vga_fill_rect(win->x, win->y, win->w, win->h, COLOR_WINDOW);
    
    /* Title bar */
    vga_fill_rect(win->x, win->y, win->w, 14, COLOR_TITLE);
    
    /* Close button */
    vga_fill_rect(win->x + win->w - 12, win->y + 2, 10, 10, COLOR_CLOSE);
    
    /* Border */
    vga_rect(win->x, win->y, win->w, win->h, 15);
}

void desktop_draw_taskbar(void) {
    vga_fill_rect(0, 185, 320, 15, COLOR_TASKBAR);
    vga_fill_rect(2, 187, 40, 11, COLOR_TITLE);  /* Start button */
    vga_rect(2, 187, 40, 11, 0);
}

void desktop_draw_cursor(void) {
    int mx = mouse_x;
    int my = mouse_y;
    
    vga_put_pixel(mx, my, 15);
    vga_put_pixel(mx+1, my, 15);
    vga_put_pixel(mx, my+1, 15);
    vga_put_pixel(mx+1, my+1, 0);
    vga_put_pixel(mx+2, my+2, 15);
    vga_put_pixel(mx+3, my+3, 15);
}

int desktop_check_icon_click(int mx, int my) {
    for (int i = 0; i < icon_count; i++) {
        if (mx >= icons[i].x && mx < icons[i].x + 32 &&
            my >= icons[i].y && my < icons[i].y + 32) {
            return i;
        }
    }
    return -1;
}

void desktop_handle_click(int mx, int my, int button) {
    if (button != 1) return;
    
    /* Check icons */
    int icon = desktop_check_icon_click(mx, my);
    if (icon >= 0 && icons[icon].action) {
        icons[icon].action();
        return;
    }
    
    /* Check window title bars for drag */
    for (int i = window_count - 1; i >= 0; i--) {
        Window* w = &windows[i];
        if (!w->visible) continue;
        
        if (mx >= w->x && mx < w->x + w->w &&
            my >= w->y && my < w->y + 14) {
            
            /* Close button? */
            if (mx >= w->x + w->w - 12) {
                w->visible = 0;
                return;
            }
            
            /* Start drag */
            w->dragging = 1;
            w->drag_x = mx - w->x;
            w->drag_y = my - w->y;
            active_window = i;
            return;
        }
    }
}

void desktop_handle_drag(int mx, int my) {
    for (int i = 0; i < window_count; i++) {
        Window* w = &windows[i];
        if (w->dragging) {
            w->x = mx - w->drag_x;
            w->y = my - w->drag_y;
            if (w->x < 0) w->x = 0;
            if (w->y < 0) w->y = 0;
            if (w->x + w->w > 320) w->x = 320 - w->w;
            if (w->y + w->h > 185) w->y = 185 - w->h;
        }
    }
}

void desktop_handle_release(void) {
    for (int i = 0; i < window_count; i++) {
        windows[i].dragging = 0;
    }
}

void start_desktop(void) {
    vga_init();
    vga_init_palette();
    mouse_init();
    
    /* Add desktop icons */
    desktop_add_icon(20, 20, "DOOM", 4, doom_loop);
    desktop_add_icon(20, 60, "INFO", 2, cmd_info);
    desktop_add_icon(20, 100, "CODE", 1, compiler_help);
    
    /* Create default window */
    desktop_create_window(100, 40, 150, 100, "Terminal");
    
    while (desktop_running) {
        /* Background */
        vga_clear(COLOR_BG);
        
        /* Icons */
        for (int i = 0; i < icon_count; i++) {
            desktop_draw_icon(&icons[i]);
        }
        
        /* Windows */
        for (int i = 0; i < window_count; i++) {
            desktop_draw_window(&windows[i]);
        }
        
        /* Taskbar */
        desktop_draw_taskbar();
        
        /* Mouse */
        mouse_update();
        
        if (mouse_buttons & 1) {
            if (!mouse_was_pressed) {
                desktop_handle_click(mouse_x, mouse_y, 1);
                mouse_was_pressed = 1;
            } else {
                desktop_handle_drag(mouse_x, mouse_y);
            }
        } else {
            if (mouse_was_pressed) {
                desktop_handle_release();
                mouse_was_pressed = 0;
            }
        }
        
        desktop_draw_cursor();
        
        vga_wait_vsync();
    }
}

#endif /* FRAY_GUI_C */
""";
        
        writeFile(OUTPUT_DIR + "/gui.c", gui);
    }

    private static void buildMouseDriver() throws IOException {
        System.out.println("   [2/3] Generating mouse.c...");
        
        String mouse = """
/* FRAY-MOUSE: PS/2 MOUSE DRIVER - Gen 146 */

#ifndef FRAY_MOUSE_C
#define FRAY_MOUSE_C

#define MOUSE_PORT   0x60
#define MOUSE_STATUS 0x64
#define MOUSE_CMD    0x64

static int mouse_x = 160;
static int mouse_y = 100;
static int mouse_buttons = 0;
static int mouse_was_pressed = 0;

static unsigned char mouse_cycle = 0;
static signed char mouse_bytes[3];

void mouse_wait_write(void) {
    int timeout = 100000;
    while (timeout-- && (inb(MOUSE_STATUS) & 2));
}

void mouse_wait_read(void) {
    int timeout = 100000;
    while (timeout-- && !(inb(MOUSE_STATUS) & 1));
}

void mouse_write(unsigned char val) {
    mouse_wait_write();
    outb(MOUSE_CMD, 0xD4);
    mouse_wait_write();
    outb(MOUSE_PORT, val);
}

unsigned char mouse_read(void) {
    mouse_wait_read();
    return inb(MOUSE_PORT);
}

void mouse_init(void) {
    /* Enable auxiliary device */
    mouse_wait_write();
    outb(MOUSE_CMD, 0xA8);
    
    /* Enable interrupts */
    mouse_wait_write();
    outb(MOUSE_CMD, 0x20);
    mouse_wait_read();
    unsigned char status = inb(MOUSE_PORT) | 2;
    mouse_wait_write();
    outb(MOUSE_CMD, 0x60);
    mouse_wait_write();
    outb(MOUSE_PORT, status);
    
    /* Use default settings */
    mouse_write(0xF6);
    mouse_read();
    
    /* Enable mouse */
    mouse_write(0xF4);
    mouse_read();
}

void mouse_handle_packet(void) {
    unsigned char status = inb(MOUSE_STATUS);
    if (!(status & 1)) return;
    
    signed char val = inb(MOUSE_PORT);
    
    mouse_bytes[mouse_cycle] = val;
    mouse_cycle++;
    
    if (mouse_cycle == 3) {
        mouse_cycle = 0;
        
        /* Parse packet */
        mouse_buttons = mouse_bytes[0] & 0x07;
        
        int dx = mouse_bytes[1];
        int dy = mouse_bytes[2];
        
        /* Sign extend */
        if (mouse_bytes[0] & 0x10) dx |= 0xFFFFFF00;
        if (mouse_bytes[0] & 0x20) dy |= 0xFFFFFF00;
        
        mouse_x += dx;
        mouse_y -= dy;
        
        /* Clamp */
        if (mouse_x < 0) mouse_x = 0;
        if (mouse_x > 319) mouse_x = 319;
        if (mouse_y < 0) mouse_y = 0;
        if (mouse_y > 199) mouse_y = 199;
    }
}

void mouse_update(void) {
    while (inb(MOUSE_STATUS) & 1) {
        mouse_handle_packet();
    }
}

#endif /* FRAY_MOUSE_C */
""";
        
        writeFile(OUTPUT_DIR + "/mouse.c", mouse);
    }

    private static void buildWidgets() throws IOException {
        System.out.println("   [3/3] Generating widgets.c...");
        
        String widgets = """
/* FRAY-WIDGETS: UI COMPONENTS - Gen 146 */

#ifndef FRAY_WIDGETS_C
#define FRAY_WIDGETS_C

typedef struct {
    int x, y, w, h;
    char label[16];
    unsigned char bg_color;
    unsigned char text_color;
    int pressed;
    void (*on_click)(void);
} Button;

typedef struct {
    int x, y, w, h;
    char text[256];
    int cursor_pos;
} TextBox;

void widget_draw_button(Button* btn) {
    unsigned char color = btn->pressed ? btn->bg_color - 2 : btn->bg_color;
    vga_fill_rect(btn->x, btn->y, btn->w, btn->h, color);
    vga_rect(btn->x, btn->y, btn->w, btn->h, 15);
    
    if (btn->pressed) {
        vga_hline(btn->x, btn->x + btn->w - 1, btn->y, 8);
        vga_vline(btn->x, btn->y, btn->y + btn->h - 1, 8);
    } else {
        vga_hline(btn->x, btn->x + btn->w - 1, btn->y + btn->h - 1, 8);
        vga_vline(btn->x + btn->w - 1, btn->y, btn->y + btn->h - 1, 8);
    }
}

int widget_button_hit(Button* btn, int mx, int my) {
    return (mx >= btn->x && mx < btn->x + btn->w &&
            my >= btn->y && my < btn->y + btn->h);
}

void widget_draw_textbox(TextBox* tb) {
    vga_fill_rect(tb->x, tb->y, tb->w, tb->h, 15);
    vga_rect(tb->x, tb->y, tb->w, tb->h, 0);
}

void widget_draw_progress(int x, int y, int w, int h, int value, int max, unsigned char color) {
    vga_fill_rect(x, y, w, h, 8);
    int fill_w = (w * value) / max;
    vga_fill_rect(x, y, fill_w, h, color);
    vga_rect(x, y, w, h, 0);
}

void widget_draw_scrollbar(int x, int y, int h, int pos, int max) {
    vga_fill_rect(x, y, 12, h, 7);
    int thumb_h = h / 4;
    int thumb_y = y + (pos * (h - thumb_h)) / max;
    vga_fill_rect(x + 1, thumb_y, 10, thumb_h, 15);
    vga_rect(x, y, 12, h, 0);
}

#endif /* FRAY_WIDGETS_C */
""";
        
        writeFile(OUTPUT_DIR + "/widgets.c", widgets);
    }

    private static void writeFile(String path, String content) throws IOException {
        Path filePath = Paths.get(path);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, content);
        System.out.println("      → " + path);
    }
}
