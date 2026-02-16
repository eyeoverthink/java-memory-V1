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
    kprint("\n  FRAY-CALC (RPN Calculator)\n");
    kprint("  Use: 3 4 + (= 7)\n\n");
    
    char buf[128];
    while (1) {
        kprint("> ");
        read_line(buf, 128);
        if (buf[0] == 'q') return;
        
        double result = calc_eval(buf);
        kprint("= ");
        kprint_double(result);
        kprint("\n");
    }
}
#endif
