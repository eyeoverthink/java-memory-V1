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
            kprint("[KILL] Process "); kprint_int(pid); kprint(" terminated\n");
            return;
        }
    }
}

void procs_list(void) {
    kprint("\n  PID  STATE    MEM     CPU    NAME\n");
    kprint("  ---  -----    ---     ---    ----\n");
    
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
        kprint("\n");
    }
}

void procs_main(void) {
    while (1) {
        clear_screen();
        kprint("  FRAY-PROCS | Process Manager\n");
        procs_list();
        kprint("\n[K]ill  [R]efresh  [Q]uit\n> ");
        
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
