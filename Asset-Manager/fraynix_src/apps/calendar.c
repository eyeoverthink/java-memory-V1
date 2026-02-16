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
    kprint("\n     ");
    kprint(month_names[month - 1]);
    kprint(" ");
    kprint_int(year);
    kprint("\n");
    kprint(" Su Mo Tu We Th Fr Sa\n");
    
    int first = day_of_week(year, month, 1);
    int days = days_in_month[month - 1];
    if (month == 2 && is_leap(year)) days = 29;
    
    for (int i = 0; i < first; i++) kprint("   ");
    
    for (int d = 1; d <= days; d++) {
        if (d < 10) kprint(" ");
        kprint_int(d);
        kprint(" ");
        if ((first + d) % 7 == 0) kprint("\n");
    }
    kprint("\n");
}

void calendar_main(int year, int month) {
    if (month < 1 || month > 12) month = 1;
    if (year < 1) year = 2025;
    
    while (1) {
        clear_screen();
        calendar_draw(year, month);
        kprint("\n[<] Prev  [>] Next  [Q] Quit\n");
        
        char k = read_key();
        if (k == 'q') return;
        if (k == '<' || k == ',') { month--; if (month < 1) { month = 12; year--; } }
        if (k == '>' || k == '.') { month++; if (month > 12) { month = 1; year++; } }
    }
}
#endif
