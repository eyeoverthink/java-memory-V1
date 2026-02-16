/* ═══════════════════════════════════════════════════════════════════════════
 * FRAY-IMAGE: BMP Viewer - Gen 151
 * ═══════════════════════════════════════════════════════════════════════════ */

#ifndef FRAY_IMAGE_C
#define FRAY_IMAGE_C

/* BMP Header structures */
typedef struct __attribute__((packed)) {
    unsigned short type;      /* "BM" */
    unsigned int size;
    unsigned short reserved1;
    unsigned short reserved2;
    unsigned int offset;
} BmpFileHeader;

typedef struct __attribute__((packed)) {
    unsigned int header_size;
    int width;
    int height;
    unsigned short planes;
    unsigned short bpp;
    unsigned int compression;
    unsigned int image_size;
    int x_ppm;
    int y_ppm;
    unsigned int colors_used;
    unsigned int colors_important;
} BmpInfoHeader;

typedef struct {
    int width;
    int height;
    int bpp;
    unsigned char* pixels;
} Image;

static Image current_image;
static int image_offset_x = 0;
static int image_offset_y = 0;
static int image_zoom = 1;

/* Load BMP image */
int image_load_bmp(const char* filename) {
    /* Get file from hash memory */
    unsigned long long hash = phi_hash(filename, kstrlen(filename));
    unsigned char* data = (unsigned char*)get_memory_block_by_hash(hash);
    
    if (!data) {
        kprint_color("[IMAGE] File not found\n", 0x0C);
        return 0;
    }
    
    /* Parse BMP header */
    BmpFileHeader* file_header = (BmpFileHeader*)data;
    if (file_header->type != 0x4D42) {  /* "BM" */
        kprint_color("[IMAGE] Not a BMP file\n", 0x0C);
        return 0;
    }
    
    BmpInfoHeader* info_header = (BmpInfoHeader*)(data + sizeof(BmpFileHeader));
    
    current_image.width = info_header->width;
    current_image.height = llm_abs(info_header->height);
    current_image.bpp = info_header->bpp;
    current_image.pixels = data + file_header->offset;
    
    kprint("[IMAGE] Loaded: ");
    kprint_int(current_image.width);
    kprint("x");
    kprint_int(current_image.height);
    kprint(" @ ");
    kprint_int(current_image.bpp);
    kprint("bpp\n");
    
    return 1;
}

/* Convert 24-bit RGB to VGA 256 color */
unsigned char rgb_to_vga(unsigned char r, unsigned char g, unsigned char b) {
    /* Simple quantization to 6-bit color cube */
    int ri = r / 51;  /* 0-5 */
    int gi = g / 51;
    int bi = b / 51;
    
    return 16 + (ri * 36) + (gi * 6) + bi;
}

/* Draw image to screen */
void image_draw(void) {
    if (!current_image.pixels) return;
    
    int screen_w = 320;
    int screen_h = 200;
    
    int bytes_per_pixel = current_image.bpp / 8;
    int row_size = ((current_image.width * bytes_per_pixel + 3) / 4) * 4;  /* Padded to 4 bytes */
    
    for (int y = 0; y < screen_h; y++) {
        int src_y = (y + image_offset_y) / image_zoom;
        if (src_y < 0 || src_y >= current_image.height) continue;
        
        /* BMP is stored bottom-up */
        int flipped_y = current_image.height - 1 - src_y;
        unsigned char* row = current_image.pixels + (flipped_y * row_size);
        
        for (int x = 0; x < screen_w; x++) {
            int src_x = (x + image_offset_x) / image_zoom;
            if (src_x < 0 || src_x >= current_image.width) continue;
            
            unsigned char color;
            
            if (current_image.bpp == 24) {
                int offset = src_x * 3;
                unsigned char b = row[offset];
                unsigned char g = row[offset + 1];
                unsigned char r = row[offset + 2];
                color = rgb_to_vga(r, g, b);
            }
            else if (current_image.bpp == 8) {
                color = row[src_x];
            }
            else {
                color = 0;
            }
            
            vga_framebuffer[y * screen_w + x] = color;
        }
    }
}

/* Image controls */
void image_pan(int dx, int dy) {
    image_offset_x += dx;
    image_offset_y += dy;
    
    /* Clamp */
    if (image_offset_x < 0) image_offset_x = 0;
    if (image_offset_y < 0) image_offset_y = 0;
    
    image_draw();
}

void image_zoom_in(void) {
    if (image_zoom < 8) {
        image_zoom++;
        image_draw();
    }
}

void image_zoom_out(void) {
    if (image_zoom > 1) {
        image_zoom--;
        image_draw();
    }
}

void image_fit_screen(void) {
    int screen_w = 320;
    int screen_h = 200;
    
    int zoom_w = (current_image.width + screen_w - 1) / screen_w;
    int zoom_h = (current_image.height + screen_h - 1) / screen_h;
    
    image_zoom = (zoom_w > zoom_h) ? zoom_w : zoom_h;
    if (image_zoom < 1) image_zoom = 1;
    
    image_offset_x = 0;
    image_offset_y = 0;
    
    image_draw();
}

/* Slideshow */
void image_slideshow(const char** files, int count, int delay_ms) {
    for (int i = 0; i < count; i++) {
        if (image_load_bmp(files[i])) {
            image_fit_screen();
            
            /* Wait for delay or keypress */
            unsigned long long start = get_tick_count();
            while (get_tick_count() - start < delay_ms) {
                if (has_input()) {
                    unsigned char key = read_key();
                    if (key == 'q' || key == 27) return;
                    if (key == ' ') break;  /* Next */
                }
            }
        }
    }
}

/* Main viewer */
void image_viewer_main(const char* filename) {
    vga_mode_13h();  /* 320x200 256 colors */
    
    if (!image_load_bmp(filename)) {
        return;
    }
    
    image_fit_screen();
    
    /* Controls */
    while (1) {
        unsigned char key = read_key();
        
        switch (key) {
            case 'q':
            case 27:  /* ESC */
                vga_text_mode();
                return;
                
            case 'h': image_pan(-10, 0); break;
            case 'j': image_pan(0, 10); break;
            case 'k': image_pan(0, -10); break;
            case 'l': image_pan(10, 0); break;
            
            case '+':
            case '=': image_zoom_in(); break;
            
            case '-': image_zoom_out(); break;
            
            case 'f': image_fit_screen(); break;
        }
    }
}

#endif /* FRAY_IMAGE_C */
