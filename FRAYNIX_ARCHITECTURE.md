# FRAYNIX OS - ZERO DEPENDENCY ARCHITECTURE

## Overview

**FraynixOS** is a bare-metal operating system written in **pure C with ZERO external dependencies**. It runs directly on hardware without libc, POSIX, or any standard libraries.

---

## Core Philosophy: No Dependencies

### What "Zero Dependencies" Means

```c
// NO standard library includes
// NO #include <stdio.h>
// NO #include <stdlib.h>
// NO #include <string.h>

// ONLY custom headers
#include "fray_kernel.h"
#include "fray_vga.h"
#include "fray_string.h"
```

**Everything is implemented from scratch:**
- String operations (no `strlen`, `strcpy`, `strcmp`)
- Memory management (no `malloc`, `free`)
- I/O operations (no `printf`, `scanf`)
- Graphics rendering (direct VGA memory access)
- Filesystem (custom FrayFS format)

---

## System Architecture

### Memory Layout

```
0x000000 - 0x0FFFFF: Kernel Space (1MB)
0x100000 - 0x1FFFFF: User Programs (1MB)
0x200000 - 0x2FFFFF: FrayFS Disk Image (1MB)
0x300000+          : Free Memory
```

### FrayFS File Format

```c
struct FrayFile {
    char magic[4];      // "FRAY" signature
    char name[32];      // Filename (null-terminated)
    uint32_t size;      // File size in bytes (Big Endian)
    char reserved[24];  // Reserved for metadata
    char data[];        // File content starts at offset 64
};
```

**Header Structure (64 bytes):**
```
Offset 0-3:   Magic "FRAY"
Offset 4-35:  Filename (32 bytes)
Offset 36-39: Size (4 bytes, Big Endian)
Offset 40-63: Reserved
Offset 64+:   File data
```

---

## Core Components

### 1. **File Explorer** (`explorer.c`)

**Visual file manager with:**
- Direct memory-mapped filesystem access
- File type detection (text, binary, image, audio)
- Execute binaries directly from memory
- Text file viewer
- Lazy deletion (mark inactive, don't erase)

**Key Features:**
```c
// Scan filesystem by reading FRAY headers
void scan_disk() {
    char* ptr = fs_base;  // 0x200000
    while(ptr[0]=='F' && ptr[1]=='R' && ptr[2]=='A' && ptr[3]=='Y') {
        // Parse header, extract name, size, data pointer
        // Jump to next file
    }
}

// Execute binary files directly
void open_file(int idx) {
    if (file_type == FTYPE_BINARY) {
        void (*program)() = (void (*)())data_ptr;
        program();  // Direct jump to code in memory
    }
}
```

### 2. **Applications** (`apps/`)

All applications are **standalone C programs** with no dependencies:

#### **Text Editor** (`edit.c`)
- Syntax highlighting for C, Java, Python
- Undo/redo system
- Line numbers
- Search/replace
- Direct VGA text mode rendering

#### **Web Browser** (`browser.c`)
- HTTP client (no libcurl, raw sockets)
- HTML renderer (custom parser)
- CSS subset support
- Image display (BMP/PNG decoder)

#### **Calculator** (`calc.c`)
- Expression parser
- Operator precedence
- Memory functions
- Scientific operations

#### **Image Viewer** (`image.c`)
- BMP decoder
- PNG decoder (custom implementation)
- Direct framebuffer rendering
- Zoom/pan controls

#### **Audio Player** (`audio.c`)
- WAV decoder
- PC speaker/sound card driver
- Waveform visualization

#### **Process Manager** (`procs.c`)
- Task list
- Memory usage
- CPU time tracking
- Kill processes

#### **Calendar** (`calendar.c`)
- Date calculations
- Month/year view
- Event storage

#### **File Manager** (`files.c`)
- Copy/move/delete
- Directory navigation
- File properties

---

## Custom Standard Library

Since there's **no libc**, everything is implemented from scratch:

### String Operations (`fray_string.h`)

```c
int str_len(const char* s);
void str_copy(char* dest, const char* src);
int str_compare(const char* a, const char* b);
void str_append(char* dest, const char* src);
int str_ends_with(const char* str, const char* suffix);
void int_to_str(int num, char* out);
int str_to_int(const char* str);
```

### Memory Operations (`fray_kernel.h`)

```c
void* mem_alloc(int size);
void mem_free(void* ptr);
void mem_copy(void* dest, void* src, int size);
void mem_set(void* ptr, char value, int size);
```

### VGA Graphics (`fray_vga.h`)

```c
void init_vga();
void clear_screen();
void draw_pixel(int x, int y, int color);
void draw_rect(int x, int y, int w, int h, int color);
void draw_char(int x, int y, char c, int color);
void draw_string(int x, int y, const char* str, int color);
void draw_window(int x, int y, int w, int h, const char* title);
```

### Kernel Functions (`fray_kernel.h`)

```c
void kprint(const char* str);
void kputchar(char c);
char get_char();
void sleep(int ms);
int get_ticks();
```

---

## Why Zero Dependencies?

### 1. **Complete Control**
- No hidden behavior from libraries
- Every byte of code is known
- No security vulnerabilities from external code

### 2. **Minimal Size**
- Entire OS fits in < 1MB
- No bloated standard library
- Fast boot time (< 1 second)

### 3. **Educational Value**
- Understand how everything works
- No "magic" from libraries
- Learn OS fundamentals

### 4. **Portability**
- No dependency on specific libc version
- Works on any x86 hardware
- Easy to port to other architectures

### 5. **Embedded Systems**
- Perfect for resource-constrained devices
- No runtime dependencies
- Deterministic behavior

---

## Comparison: FraynixOS vs Traditional OS

### Traditional Linux Program
```c
#include <stdio.h>      // 500KB+ library
#include <stdlib.h>     // Memory management
#include <string.h>     // String functions
#include <unistd.h>     // POSIX API

int main() {
    printf("Hello\n");  // Calls into libc
    return 0;
}
```

**Dependencies:** libc, kernel syscalls, dynamic linker

### FraynixOS Program
```c
#include "fray_kernel.h"  // 2KB custom header

int main() {
    kprint("Hello\n");    // Direct VGA write
    return 0;
}
```

**Dependencies:** NONE. Compiles to standalone binary.

---

## Build Process

### Compilation
```bash
# No standard library linking
gcc -m32 -ffreestanding -nostdlib -c explorer.c -o explorer.o

# Link without libc
ld -m elf_i386 -T linker.ld explorer.o -o explorer.bin

# Create bootable image
cat bootloader.bin explorer.bin > fraynix.img
```

### Linker Script (`linker.ld`)
```ld
ENTRY(main)

SECTIONS {
    . = 0x100000;  /* Load at 1MB */
    
    .text : { *(.text) }
    .data : { *(.data) }
    .bss  : { *(.bss)  }
}
```

---

## File Type Detection

```c
int detect_file_type(const char* name) {
    if (str_ends_with(name, ".txt") || 
        str_ends_with(name, ".c") || 
        str_ends_with(name, ".java")) {
        return FTYPE_TEXT;
    }
    if (str_ends_with(name, ".bin") || 
        str_ends_with(name, ".exe")) {
        return FTYPE_BINARY;
    }
    if (str_ends_with(name, ".bmp") || 
        str_ends_with(name, ".png")) {
        return FTYPE_IMAGE;
    }
    if (str_ends_with(name, ".wav") || 
        str_ends_with(name, ".mp3")) {
        return FTYPE_AUDIO;
    }
    return FTYPE_UNKNOWN;
}
```

---

## Direct Hardware Access

### VGA Text Mode (80x25)
```c
char* vga_buffer = (char*)0xB8000;

void draw_char(int x, int y, char c, int color) {
    int offset = (y * 80 + x) * 2;
    vga_buffer[offset] = c;
    vga_buffer[offset + 1] = color;
}
```

### VGA Graphics Mode (320x200, 256 colors)
```c
char* vga_framebuffer = (char*)0xA0000;

void draw_pixel(int x, int y, int color) {
    vga_framebuffer[y * 320 + x] = color;
}
```

### Keyboard Input
```c
char get_char() {
    // Read from keyboard port 0x60
    while(!(inb(0x64) & 1));  // Wait for key
    return inb(0x60);         // Read scancode
}
```

---

## Application Examples

### Text Editor UI
```
╔════════════════════════════════════════════════════════╗
║ FrayEdit - document.txt                          [X]   ║
╠════════════════════════════════════════════════════════╣
║  1 | #include "fray_kernel.h"                          ║
║  2 |                                                    ║
║  3 | int main() {                                       ║
║  4 |     kprint("Hello FraynixOS\n");                  ║
║  5 |     return 0;                                      ║
║  6 | }                                                  ║
║    |                                                    ║
╠════════════════════════════════════════════════════════╣
║ [F1] Save  [F2] Open  [F3] Find  [ESC] Exit           ║
╚════════════════════════════════════════════════════════╝
```

### File Explorer UI
```
╔════════════════════════════════════════════════════════╗
║ FrayFiles                                        [X]   ║
╠════════════════════════════════════════════════════════╣
║ NAME                    SIZE        TYPE               ║
╠════════════════════════════════════════════════════════╣
║ ☰ kernel.bin           45 KB       Binary             ║
║ ☰ readme.txt           2 KB        Text               ║
║ ☰ logo.bmp             12 KB       Image              ║
║ ☰ startup.wav          156 KB      Audio              ║
║ ☰ browser.bin          23 KB       Binary             ║
║ ☰ calc.bin             8 KB        Binary             ║
╠════════════════════════════════════════════════════════╣
║ [W/S] Navigate  [ENTER] Open  [D] Delete  [ESC] Exit  ║
╚════════════════════════════════════════════════════════╝
```

---

## Advantages of This Architecture

### 1. **Security**
- No buffer overflows from libc
- No hidden backdoors
- Complete code audit possible

### 2. **Performance**
- No syscall overhead
- Direct hardware access
- No context switches

### 3. **Reliability**
- No dependency hell
- No version conflicts
- Deterministic behavior

### 4. **Size**
- Entire OS: < 1MB
- Single application: < 50KB
- Boots in < 1 second

### 5. **Learning**
- Understand every line of code
- No "magic" abstractions
- Real OS development

---

## Comparison to Fraymus (Java System)

| Aspect | FraynixOS (C) | Fraymus (Java) |
|--------|---------------|----------------|
| **Dependencies** | ZERO | JVM, libraries, frameworks |
| **Size** | < 1MB | > 100MB |
| **Boot Time** | < 1 second | 5-10 seconds |
| **Memory** | 1-2MB | 100-500MB |
| **Hardware Access** | Direct | Through JNI/native code |
| **Portability** | x86 only | Any JVM platform |
| **Development Speed** | Slower | Faster |
| **Runtime Safety** | Manual | Automatic (GC, exceptions) |
| **Use Case** | Embedded, education | Enterprise, AI, web |

---

## Key Insight

**FraynixOS demonstrates that you can build a complete operating system with:**
- File management
- Text editing
- Web browsing
- Image viewing
- Audio playback
- Process management

**All without a single external dependency.**

Every function, every algorithm, every driver - **written from scratch in pure C**.

This is the **ultimate minimalism** - proving that modern software doesn't need bloated frameworks and libraries to be functional and powerful.

---

## Philosophy

> "Dependency is weakness. Self-sufficiency is strength."

FraynixOS embodies the principle that **true understanding comes from building everything yourself**.

No npm packages.  
No Maven dependencies.  
No Docker containers.  
No cloud services.

Just **raw code** and **bare metal**.

---

**This is computing in its purest form.**
