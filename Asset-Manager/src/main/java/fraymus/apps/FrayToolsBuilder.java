package fraymus.apps;

import java.io.*;
import java.nio.file.*;

/**
 * 🔧 FRAY-TOOLS - Gen 151
 * Calculator, Calendar, Process Manager, File Manager
 */
public class FrayToolsBuilder {

    private static final String OUTPUT_DIR = "fraynix_src/apps";

    public static void main(String[] args) throws IOException {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  🔧 FRAY-TOOLS - Gen 151                                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        
        Files.createDirectories(Paths.get(OUTPUT_DIR));
        buildCalculator();
        buildCalendar();
        buildProcessManager();
        buildFileManager();
        
        System.out.println("\n✅ FRAY-TOOLS COMPILED");
    }

    private static void buildCalculator() throws IOException {
        System.out.println("   [1/4] Generating calc.c...");
        
        String calc = """
/* FRAY-CALC - Gen 151 */
#ifndef FRAY_CALC_C
#define FRAY_CALC_C

static double calc_stack[32];
static int calc_sp = 0;

void calc_push(double v) { if (calc_sp < 32) calc_stack[calc_sp++] = v; }
double calc_pop(void) { return calc_sp > 0 ? calc_stack[--calc_sp] : 0; }

double calc_eval(const char* expr) {
    calc_sp = 0;
    double num = 0;
    int in_num = 0;
    double decimal = 0;
    int has_decimal = 0;
    
    for (int i = 0; expr[i]; i++) {
        char c = expr[i];
        
        if ((c >= '0' && c <= '9') || c == '.') {
            if (c == '.') { has_decimal = 1; decimal = 0.1; }
            else if (has_decimal) { num += (c - '0') * decimal; decimal *= 0.1; }
            else { num = num * 10 + (c - '0'); }
            in_num = 1;
        } else {
            if (in_num) { calc_push(num); num = 0; in_num = 0; has_decimal = 0; }
            
            if (c == '+') { double b = calc_pop(), a = calc_pop(); calc_push(a + b); }
            else if (c == '-') { double b = calc_pop(), a = calc_pop(); calc_push(a - b); }
            else if (c == '*') { double b = calc_pop(), a = calc_pop(); calc_push(a * b); }
            else if (c == '/') { double b = calc_pop(), a = calc_pop(); calc_push(b != 0 ? a / b : 0); }
            else if (c == '^') { double b = calc_pop(), a = calc_pop(); 
                double r = 1; for (int j = 0; j < (int)b; j++) r *= a; calc_push(r); }
        }
    }
    if (in_num) calc_push(num);
    return calc_pop();
}

void calc_main(void) {
    kprint("\\n  FRAY-CALC (RPN Calculator)\\n");
    kprint("  Use: 3 4 + (= 7)\\n\\n");
    
    char buf[128];
    while (1) {
        kprint("> ");
        read_line(buf, 128);
        if (buf[0] == 'q') return;
        
        double result = calc_eval(buf);
        kprint("= ");
        kprint_double(result);
        kprint("\\n");
    }
}
#endif
""";
        writeFile(OUTPUT_DIR + "/calc.c", calc);
    }

    private static void buildCalendar() throws IOException {
        System.out.println("   [2/4] Generating calendar.c...");
        
        String cal = """
/* FRAY-CALENDAR - Gen 151 */
#ifndef FRAY_CALENDAR_C
#define FRAY_CALENDAR_C

static const char* month_names[] = {"January","February","March","April","May","June",
    "July","August","September","October","November","December"};
static const int days_in_month[] = {31,28,31,30,31,30,31,31,30,31,30,31};

int is_leap(int y) { return (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0); }

int day_of_week(int y, int m, int d) {
    int t[] = {0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4};
    if (m < 3) y--;
    return (y + y/4 - y/100 + y/400 + t[m-1] + d) % 7;
}

void calendar_draw(int year, int month) {
    kprint("\\n     ");
    kprint(month_names[month - 1]);
    kprint(" ");
    kprint_int(year);
    kprint("\\n");
    kprint(" Su Mo Tu We Th Fr Sa\\n");
    
    int first = day_of_week(year, month, 1);
    int days = days_in_month[month - 1];
    if (month == 2 && is_leap(year)) days = 29;
    
    for (int i = 0; i < first; i++) kprint("   ");
    
    for (int d = 1; d <= days; d++) {
        if (d < 10) kprint(" ");
        kprint_int(d);
        kprint(" ");
        if ((first + d) % 7 == 0) kprint("\\n");
    }
    kprint("\\n");
}

void calendar_main(int year, int month) {
    if (month < 1 || month > 12) month = 1;
    if (year < 1) year = 2025;
    
    while (1) {
        clear_screen();
        calendar_draw(year, month);
        kprint("\\n[<] Prev  [>] Next  [Q] Quit\\n");
        
        char k = read_key();
        if (k == 'q') return;
        if (k == '<' || k == ',') { month--; if (month < 1) { month = 12; year--; } }
        if (k == '>' || k == '.') { month++; if (month > 12) { month = 1; year++; } }
    }
}
#endif
""";
        writeFile(OUTPUT_DIR + "/calendar.c", cal);
    }

    private static void buildProcessManager() throws IOException {
        System.out.println("   [3/4] Generating procs.c...");
        
        String procs = """
/* FRAY-PROCS - Gen 151 */
#ifndef FRAY_PROCS_C
#define FRAY_PROCS_C

#define MAX_PROCS 64

typedef struct {
    int pid;
    char name[32];
    int state;  /* 0=free, 1=running, 2=sleeping, 3=blocked */
    unsigned int memory;
    unsigned int cpu_ticks;
} Process;

static Process proc_table[MAX_PROCS];
static int proc_count = 0;

void procs_init(void) {
    proc_count = 1;
    proc_table[0].pid = 1;
    kstrcpy(proc_table[0].name, "kernel");
    proc_table[0].state = 1;
    proc_table[0].memory = 4096;
    proc_table[0].cpu_ticks = 0;
}

int procs_spawn(const char* name) {
    if (proc_count >= MAX_PROCS) return -1;
    Process* p = &proc_table[proc_count];
    p->pid = proc_count + 1;
    kstrcpy(p->name, name);
    p->state = 1;
    p->memory = 1024;
    p->cpu_ticks = 0;
    return proc_count++;
}

void procs_kill(int pid) {
    for (int i = 0; i < proc_count; i++) {
        if (proc_table[i].pid == pid && pid != 1) {
            proc_table[i].state = 0;
            kprint("[KILL] Process "); kprint_int(pid); kprint(" terminated\\n");
            return;
        }
    }
}

void procs_list(void) {
    kprint("\\n  PID  STATE    MEM     CPU    NAME\\n");
    kprint("  ---  -----    ---     ---    ----\\n");
    
    const char* states[] = {"free", "run ", "slp ", "blk "};
    
    for (int i = 0; i < proc_count; i++) {
        Process* p = &proc_table[i];
        if (p->state == 0) continue;
        
        kprint("  ");
        kprint_int_padded(p->pid, 3);
        kprint("  ");
        kprint(states[p->state]);
        kprint("     ");
        kprint_int_padded(p->memory, 5);
        kprint("   ");
        kprint_int_padded(p->cpu_ticks, 5);
        kprint("  ");
        kprint(p->name);
        kprint("\\n");
    }
}

void procs_main(void) {
    while (1) {
        clear_screen();
        kprint("  FRAY-PROCS | Process Manager\\n");
        procs_list();
        kprint("\\n[K]ill  [R]efresh  [Q]uit\\n> ");
        
        char k = read_key();
        if (k == 'q') return;
        if (k == 'r') continue;
        if (k == 'k') {
            kprint("Kill PID: ");
            char buf[8]; read_line(buf, 8);
            int pid = 0;
            for (int i = 0; buf[i]; i++) pid = pid * 10 + (buf[i] - '0');
            procs_kill(pid);
        }
    }
}
#endif
""";
        writeFile(OUTPUT_DIR + "/procs.c", procs);
    }

    private static void buildFileManager() throws IOException {
        System.out.println("   [4/4] Generating files.c...");
        
        String files = """
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
    kprint("\\n");
    kprint("  ════════════════════════════════════════\\n");
    
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
        kprint("\\n");
        set_color(0x0F);
    }
    
    kprint("\\n  [Enter] Open  [D]elete  [R]ename  [Q]uit\\n");
}

void files_open(void) {
    FileEntry* f = &file_list[selected];
    
    if (f->is_dir) {
        if (kstrcmp(f->name, "..") == 0) {
            /* Go up */
            int len = kstrlen(current_path);
            while (len > 1 && current_path[len-1] != '/') len--;
            if (len > 1) len--;
            current_path[len] = '\\0';
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
        if (k == '\\n') files_open();
    }
}
#endif
""";
        writeFile(OUTPUT_DIR + "/files.c", files);
    }

    private static void writeFile(String path, String content) throws IOException {
        Path filePath = Paths.get(path);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, content);
        System.out.println("      → " + path);
    }
}
