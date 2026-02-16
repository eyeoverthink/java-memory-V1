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
