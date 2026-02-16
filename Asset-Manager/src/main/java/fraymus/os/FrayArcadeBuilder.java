package fraymus.os;

import java.io.*;
import java.nio.file.*;

/**
 * 🕹️ FRAY ARCADE BUILDER - Gen 149
 * "The Infinite Quarter. Every classic, rebuilt from memory."
 * 
 * FUNCTION:
 * 1. GAME LOOP: A single 'while(1)' that handles all games.
 * 2. RENDERER: Uses ASCII/VGA to draw game states.
 * 3. AI ANALYST: Logs every death and high score to FrayLLM.
 * 
 * Games:
 *   1. Pong - Classic paddle tennis
 *   2. Snake - Grow the serpent
 *   3. Space Invaders - Defend Earth
 *   4. Breakout - Brick breaker
 *   5. Tetris - Block stacker
 *   6. Asteroids - Space survival
 */
public class FrayArcadeBuilder {

    private static final String OUTPUT_DIR = "fraynix_src";

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  🕹️ FRAY ARCADE BUILDER - Gen 149                             ║");
        System.out.println("║  The Infinite Quarter                                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            Files.createDirectories(Paths.get(OUTPUT_DIR));
            
            System.out.println("⚡ Inserting coin... Building arcade suite...");
            
            buildArcadeCore();
            buildPong();
            buildSnake();
            buildInvaders();
            buildBreakout();
            buildTetris();
            buildAsteroids();
            
            System.out.println();
            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║  ✅ ARCADE INSTALLED                                          ║");
            System.out.println("║                                                               ║");
            System.out.println("║  Shell: Type 'arcade' to play                                ║");
            System.out.println("║  Games: Pong, Snake, Invaders, Breakout, Tetris, Asteroids   ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");
            
        } catch (IOException e) {
            System.err.println("❌ GAME OVER: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void buildArcadeCore() throws IOException {
        System.out.println("   [1/7] Generating arcade.c...");
        
        String arcade = """
/* ═══════════════════════════════════════════════════════════════════════════
 * FRAY-ARCADE: MULTI-GAME SYSTEM - Gen 149
 * One Engine. Infinite Fun.
 * ═══════════════════════════════════════════════════════════════════════════ */

#ifndef FRAY_ARCADE_C
#define FRAY_ARCADE_C

#include "pong.c"
#include "snake.c"
#include "invaders.c"
#include "breakout.c"
#include "tetris.c"
#include "asteroids.c"

/* Game States */
#define GAME_MENU      0
#define GAME_PONG      1
#define GAME_SNAKE     2
#define GAME_INVADERS  3
#define GAME_BREAKOUT  4
#define GAME_TETRIS    5
#define GAME_ASTEROIDS 6

static int game_state = GAME_MENU;
static int global_score = 0;
static int global_highscore = 0;
static int game_tick = 0;

/* AI Analyst */
static int deaths_this_session = 0;
static int left_moves = 0;
static int right_moves = 0;

void log_game_event(const char* game, const char* event, int value) {
    kprint_color("[AI] ", 0x0B);
    kprint(game);
    kprint(" -> ");
    kprint(event);
    kprint(": ");
    kprint_int(value);
    kprint("\\n");
    
    /* AI Analysis */
    if (event[0] == 'D' && event[1] == 'E' && event[2] == 'A') {
        deaths_this_session++;
        if (deaths_this_session >= 3) {
            kprint_color("[AI] TIP: ", 0x0E);
            kprint("Take a breath. Reaction time degrades with frustration.\\n");
        }
    }
    
    /* Bias Detection */
    if (left_moves > right_moves * 2) {
        kprint_color("[AI] PATTERN: ", 0x0E);
        kprint("You favor the left. Try balancing your positioning.\\n");
    }
}

void draw_arcade_menu(void) {
    vga_clear(1);
    
    vga_fill_rect(80, 20, 160, 160, 0);
    vga_rect(80, 20, 160, 160, 14);
    
    /* Title */
    int y = 30;
    kprint_at(120, y, "FRAY ARCADE", 14);
    y += 20;
    
    kprint_at(100, y, "1. PONG", 15); y += 15;
    kprint_at(100, y, "2. SNAKE", 15); y += 15;
    kprint_at(100, y, "3. SPACE INVADERS", 15); y += 15;
    kprint_at(100, y, "4. BREAKOUT", 15); y += 15;
    kprint_at(100, y, "5. TETRIS", 15); y += 15;
    kprint_at(100, y, "6. ASTEROIDS", 15); y += 15;
    
    y += 10;
    kprint_at(100, y, "Q. QUIT", 8);
    
    y += 20;
    kprint_at(90, y, "HIGH SCORE: ", 11);
    kprint_int_at(170, y, global_highscore, 11);
}

void arcade_handle_input(char key) {
    if (game_state == GAME_MENU) {
        if (key == '1') { game_state = GAME_PONG; pong_init(); }
        if (key == '2') { game_state = GAME_SNAKE; snake_init(); }
        if (key == '3') { game_state = GAME_INVADERS; invaders_init(); }
        if (key == '4') { game_state = GAME_BREAKOUT; breakout_init(); }
        if (key == '5') { game_state = GAME_TETRIS; tetris_init(); }
        if (key == '6') { game_state = GAME_ASTEROIDS; asteroids_init(); }
    } else {
        /* Track directional bias */
        if (key == 'a' || key == 'A') left_moves++;
        if (key == 'd' || key == 'D') right_moves++;
        
        /* Game-specific input */
        switch (game_state) {
            case GAME_PONG: pong_input(key); break;
            case GAME_SNAKE: snake_input(key); break;
            case GAME_INVADERS: invaders_input(key); break;
            case GAME_BREAKOUT: breakout_input(key); break;
            case GAME_TETRIS: tetris_input(key); break;
            case GAME_ASTEROIDS: asteroids_input(key); break;
        }
        
        /* Universal quit */
        if (key == 'q' || key == 'Q') {
            if (global_score > global_highscore) {
                global_highscore = global_score;
            }
            global_score = 0;
            game_state = GAME_MENU;
        }
    }
}

void start_arcade(void) {
    vga_init();
    vga_init_palette();
    
    kprint_color("\\n  FRAY ARCADE INITIALIZED\\n", 0x0A);
    kprint("  Press 1-6 to select game, Q to quit\\n\\n");
    
    while (1) {
        game_tick++;
        
        char key = get_char_non_blocking();
        if (key) arcade_handle_input(key);
        
        switch (game_state) {
            case GAME_MENU:
                draw_arcade_menu();
                break;
            case GAME_PONG:
                pong_update();
                pong_draw();
                break;
            case GAME_SNAKE:
                if (game_tick % 5 == 0) snake_update();
                snake_draw();
                break;
            case GAME_INVADERS:
                invaders_update();
                invaders_draw();
                break;
            case GAME_BREAKOUT:
                breakout_update();
                breakout_draw();
                break;
            case GAME_TETRIS:
                if (game_tick % 10 == 0) tetris_update();
                tetris_draw();
                break;
            case GAME_ASTEROIDS:
                asteroids_update();
                asteroids_draw();
                break;
        }
        
        vga_wait_vsync();
    }
}

#endif /* FRAY_ARCADE_C */
""";
        
        writeFile(OUTPUT_DIR + "/arcade.c", arcade);
    }

    private static void buildPong() throws IOException {
        System.out.println("   [2/7] Generating pong.c...");
        
        String pong = """
/* FRAY-PONG - Gen 149 */

#ifndef FRAY_PONG_C
#define FRAY_PONG_C

static int p1_y = 85, p2_y = 85;
static int ball_x = 160, ball_y = 100;
static int ball_dx = 2, ball_dy = 1;
static int p1_score = 0, p2_score = 0;

#define PADDLE_H 30
#define PADDLE_W 8
#define BALL_SIZE 6

void pong_init(void) {
    p1_y = p2_y = 85;
    ball_x = 160; ball_y = 100;
    ball_dx = 2; ball_dy = 1;
    p1_score = p2_score = 0;
    log_game_event("PONG", "START", 0);
}

void pong_update(void) {
    /* Move ball */
    ball_x += ball_dx;
    ball_y += ball_dy;
    
    /* Wall bounce */
    if (ball_y <= 10 || ball_y >= 185) ball_dy = -ball_dy;
    
    /* Paddle collision P1 */
    if (ball_x <= 20 && ball_y >= p1_y && ball_y <= p1_y + PADDLE_H) {
        ball_dx = -ball_dx;
        ball_dx += (ball_dx > 0) ? 1 : -1; /* Speed up */
        global_score += 10;
    }
    
    /* Paddle collision P2 (AI) */
    if (ball_x >= 295 && ball_y >= p2_y && ball_y <= p2_y + PADDLE_H) {
        ball_dx = -ball_dx;
    }
    
    /* Score */
    if (ball_x < 0) {
        p2_score++;
        ball_x = 160; ball_y = 100;
        ball_dx = 2;
        log_game_event("PONG", "DEATH", p1_score);
    }
    if (ball_x > 320) {
        p1_score++;
        ball_x = 160; ball_y = 100;
        ball_dx = -2;
        global_score += 50;
    }
    
    /* Simple AI for P2 */
    if (p2_y + PADDLE_H/2 < ball_y) p2_y += 2;
    if (p2_y + PADDLE_H/2 > ball_y) p2_y -= 2;
}

void pong_draw(void) {
    vga_clear(0);
    
    /* Court lines */
    for (int y = 10; y < 190; y += 10) {
        vga_fill_rect(158, y, 4, 5, 8);
    }
    
    /* Paddles */
    vga_fill_rect(10, p1_y, PADDLE_W, PADDLE_H, 15);
    vga_fill_rect(302, p2_y, PADDLE_W, PADDLE_H, 15);
    
    /* Ball */
    vga_fill_rect(ball_x, ball_y, BALL_SIZE, BALL_SIZE, 14);
    
    /* Scores */
    kprint_int_at(140, 2, p1_score, 15);
    kprint_int_at(175, 2, p2_score, 15);
}

void pong_input(char key) {
    if (key == 'w' || key == 'W') { p1_y -= 8; if (p1_y < 10) p1_y = 10; }
    if (key == 's' || key == 'S') { p1_y += 8; if (p1_y > 160) p1_y = 160; }
}

#endif /* FRAY_PONG_C */
""";
        
        writeFile(OUTPUT_DIR + "/pong.c", pong);
    }

    private static void buildSnake() throws IOException {
        System.out.println("   [3/7] Generating snake.c...");
        
        String snake = """
/* FRAY-SNAKE - Gen 149 */

#ifndef FRAY_SNAKE_C
#define FRAY_SNAKE_C

#define SNAKE_MAX 200
#define GRID_SIZE 8

static int snake_x[SNAKE_MAX], snake_y[SNAKE_MAX];
static int snake_len = 5;
static int snake_dir = 0; /* 0=R, 1=D, 2=L, 3=U */
static int food_x, food_y;
static int snake_alive = 1;

void snake_spawn_food(void) {
    food_x = 20 + (rand() % 35) * GRID_SIZE;
    food_y = 20 + (rand() % 20) * GRID_SIZE;
}

void snake_init(void) {
    snake_len = 5;
    snake_dir = 0;
    snake_alive = 1;
    for (int i = 0; i < snake_len; i++) {
        snake_x[i] = 160 - i * GRID_SIZE;
        snake_y[i] = 100;
    }
    snake_spawn_food();
    log_game_event("SNAKE", "START", 0);
}

void snake_update(void) {
    if (!snake_alive) return;
    
    /* Move body */
    for (int i = snake_len - 1; i > 0; i--) {
        snake_x[i] = snake_x[i-1];
        snake_y[i] = snake_y[i-1];
    }
    
    /* Move head */
    if (snake_dir == 0) snake_x[0] += GRID_SIZE;
    if (snake_dir == 1) snake_y[0] += GRID_SIZE;
    if (snake_dir == 2) snake_x[0] -= GRID_SIZE;
    if (snake_dir == 3) snake_y[0] -= GRID_SIZE;
    
    /* Wall collision */
    if (snake_x[0] < 10 || snake_x[0] > 305 || 
        snake_y[0] < 10 || snake_y[0] > 185) {
        snake_alive = 0;
        log_game_event("SNAKE", "DEATH", global_score);
    }
    
    /* Self collision */
    for (int i = 1; i < snake_len; i++) {
        if (snake_x[0] == snake_x[i] && snake_y[0] == snake_y[i]) {
            snake_alive = 0;
            log_game_event("SNAKE", "DEATH", global_score);
        }
    }
    
    /* Eat food */
    if (snake_x[0] == food_x && snake_y[0] == food_y) {
        snake_len++;
        global_score += 10;
        snake_spawn_food();
    }
}

void snake_draw(void) {
    vga_clear(1);
    
    /* Border */
    vga_rect(5, 5, 310, 185, 15);
    
    /* Food */
    vga_fill_rect(food_x, food_y, GRID_SIZE-1, GRID_SIZE-1, 4);
    
    /* Snake */
    for (int i = 0; i < snake_len; i++) {
        unsigned char color = (i == 0) ? 14 : 2;
        vga_fill_rect(snake_x[i], snake_y[i], GRID_SIZE-1, GRID_SIZE-1, color);
    }
    
    /* Score */
    kprint_at(10, 192, "SCORE: ", 15);
    kprint_int_at(60, 192, global_score, 14);
    
    if (!snake_alive) {
        kprint_at(120, 100, "GAME OVER", 4);
    }
}

void snake_input(char key) {
    if ((key == 'w' || key == 'W') && snake_dir != 1) snake_dir = 3;
    if ((key == 's' || key == 'S') && snake_dir != 3) snake_dir = 1;
    if ((key == 'a' || key == 'A') && snake_dir != 0) snake_dir = 2;
    if ((key == 'd' || key == 'D') && snake_dir != 2) snake_dir = 0;
}

#endif /* FRAY_SNAKE_C */
""";
        
        writeFile(OUTPUT_DIR + "/snake.c", snake);
    }

    private static void buildInvaders() throws IOException {
        System.out.println("   [4/7] Generating invaders.c...");
        
        String invaders = """
/* FRAY-INVADERS - Gen 149 */

#ifndef FRAY_INVADERS_C
#define FRAY_INVADERS_C

#define ALIEN_ROWS 4
#define ALIEN_COLS 8
#define MAX_BULLETS 10

static int ship_x = 160;
static int aliens[ALIEN_ROWS][ALIEN_COLS];
static int alien_x = 40, alien_y = 20;
static int alien_dir = 1;
static int bullets_x[MAX_BULLETS], bullets_y[MAX_BULLETS];
static int bullet_count = 0;

void invaders_init(void) {
    ship_x = 160;
    alien_x = 40; alien_y = 20;
    alien_dir = 1;
    bullet_count = 0;
    for (int r = 0; r < ALIEN_ROWS; r++)
        for (int c = 0; c < ALIEN_COLS; c++)
            aliens[r][c] = 1;
    log_game_event("INVADERS", "START", 0);
}

void invaders_update(void) {
    /* Move bullets */
    for (int i = 0; i < bullet_count; i++) {
        bullets_y[i] -= 4;
        if (bullets_y[i] < 0) {
            bullets_x[i] = bullets_x[bullet_count-1];
            bullets_y[i] = bullets_y[bullet_count-1];
            bullet_count--;
            i--;
        }
    }
    
    /* Move aliens */
    alien_x += alien_dir * 2;
    if (alien_x > 200 || alien_x < 20) {
        alien_dir = -alien_dir;
        alien_y += 8;
    }
    
    /* Collision detection */
    for (int i = 0; i < bullet_count; i++) {
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                if (aliens[r][c]) {
                    int ax = alien_x + c * 20;
                    int ay = alien_y + r * 15;
                    if (bullets_x[i] >= ax && bullets_x[i] <= ax + 12 &&
                        bullets_y[i] >= ay && bullets_y[i] <= ay + 10) {
                        aliens[r][c] = 0;
                        global_score += 20;
                        bullets_x[i] = bullets_x[bullet_count-1];
                        bullets_y[i] = bullets_y[bullet_count-1];
                        bullet_count--;
                        i--;
                        break;
                    }
                }
            }
        }
    }
    
    /* Game over check */
    if (alien_y > 160) {
        log_game_event("INVADERS", "DEATH", global_score);
        invaders_init();
    }
}

void invaders_draw(void) {
    vga_clear(0);
    
    /* Ship */
    vga_fill_rect(ship_x - 8, 180, 16, 8, 2);
    vga_fill_rect(ship_x - 2, 175, 4, 5, 2);
    
    /* Aliens */
    for (int r = 0; r < ALIEN_ROWS; r++) {
        for (int c = 0; c < ALIEN_COLS; c++) {
            if (aliens[r][c]) {
                int ax = alien_x + c * 20;
                int ay = alien_y + r * 15;
                vga_fill_rect(ax, ay, 12, 10, 10 + r);
            }
        }
    }
    
    /* Bullets */
    for (int i = 0; i < bullet_count; i++) {
        vga_fill_rect(bullets_x[i], bullets_y[i], 2, 6, 14);
    }
    
    /* Score */
    kprint_at(10, 2, "SCORE: ", 15);
    kprint_int_at(60, 2, global_score, 14);
}

void invaders_input(char key) {
    if (key == 'a' || key == 'A') { ship_x -= 6; if (ship_x < 20) ship_x = 20; }
    if (key == 'd' || key == 'D') { ship_x += 6; if (ship_x > 300) ship_x = 300; }
    if (key == ' ' && bullet_count < MAX_BULLETS) {
        bullets_x[bullet_count] = ship_x;
        bullets_y[bullet_count] = 170;
        bullet_count++;
    }
}

#endif /* FRAY_INVADERS_C */
""";
        
        writeFile(OUTPUT_DIR + "/invaders.c", invaders);
    }

    private static void buildBreakout() throws IOException {
        System.out.println("   [5/7] Generating breakout.c...");
        
        String breakout = """
/* FRAY-BREAKOUT - Gen 149 */

#ifndef FRAY_BREAKOUT_C
#define FRAY_BREAKOUT_C

#define BRICK_ROWS 5
#define BRICK_COLS 10

static int paddle_x = 140;
static int bo_ball_x = 160, bo_ball_y = 150;
static int bo_dx = 2, bo_dy = -2;
static int bricks[BRICK_ROWS][BRICK_COLS];
static int bricks_left = BRICK_ROWS * BRICK_COLS;

void breakout_init(void) {
    paddle_x = 140;
    bo_ball_x = 160; bo_ball_y = 150;
    bo_dx = 2; bo_dy = -2;
    bricks_left = BRICK_ROWS * BRICK_COLS;
    for (int r = 0; r < BRICK_ROWS; r++)
        for (int c = 0; c < BRICK_COLS; c++)
            bricks[r][c] = 1;
    log_game_event("BREAKOUT", "START", 0);
}

void breakout_update(void) {
    bo_ball_x += bo_dx;
    bo_ball_y += bo_dy;
    
    /* Wall bounce */
    if (bo_ball_x <= 5 || bo_ball_x >= 310) bo_dx = -bo_dx;
    if (bo_ball_y <= 5) bo_dy = -bo_dy;
    
    /* Paddle bounce */
    if (bo_ball_y >= 175 && bo_ball_y <= 180 &&
        bo_ball_x >= paddle_x && bo_ball_x <= paddle_x + 40) {
        bo_dy = -bo_dy;
        int offset = bo_ball_x - (paddle_x + 20);
        bo_dx = offset / 5;
    }
    
    /* Miss */
    if (bo_ball_y > 195) {
        log_game_event("BREAKOUT", "DEATH", global_score);
        bo_ball_x = 160; bo_ball_y = 150;
        bo_dy = -2;
    }
    
    /* Brick collision */
    for (int r = 0; r < BRICK_ROWS; r++) {
        for (int c = 0; c < BRICK_COLS; c++) {
            if (bricks[r][c]) {
                int bx = 15 + c * 30;
                int by = 20 + r * 12;
                if (bo_ball_x >= bx && bo_ball_x <= bx + 28 &&
                    bo_ball_y >= by && bo_ball_y <= by + 10) {
                    bricks[r][c] = 0;
                    bricks_left--;
                    bo_dy = -bo_dy;
                    global_score += 10;
                }
            }
        }
    }
    
    /* Win */
    if (bricks_left == 0) {
        log_game_event("BREAKOUT", "WIN", global_score);
        breakout_init();
    }
}

void breakout_draw(void) {
    vga_clear(0);
    
    /* Bricks */
    for (int r = 0; r < BRICK_ROWS; r++) {
        for (int c = 0; c < BRICK_COLS; c++) {
            if (bricks[r][c]) {
                int bx = 15 + c * 30;
                int by = 20 + r * 12;
                vga_fill_rect(bx, by, 28, 10, 9 + r);
            }
        }
    }
    
    /* Paddle */
    vga_fill_rect(paddle_x, 180, 40, 6, 15);
    
    /* Ball */
    vga_fill_rect(bo_ball_x - 3, bo_ball_y - 3, 6, 6, 14);
    
    /* Score */
    kprint_at(10, 192, "SCORE: ", 15);
    kprint_int_at(60, 192, global_score, 14);
}

void breakout_input(char key) {
    if (key == 'a' || key == 'A') { paddle_x -= 8; if (paddle_x < 5) paddle_x = 5; }
    if (key == 'd' || key == 'D') { paddle_x += 8; if (paddle_x > 275) paddle_x = 275; }
}

#endif /* FRAY_BREAKOUT_C */
""";
        
        writeFile(OUTPUT_DIR + "/breakout.c", breakout);
    }

    private static void buildTetris() throws IOException {
        System.out.println("   [6/7] Generating tetris.c...");
        
        String tetris = """
/* FRAY-TETRIS - Gen 149 */

#ifndef FRAY_TETRIS_C
#define FRAY_TETRIS_C

#define BOARD_W 10
#define BOARD_H 20
#define CELL_SIZE 8

static int board[BOARD_H][BOARD_W];
static int piece_x, piece_y, piece_type, piece_rot;
static int tetris_level = 1;
static int lines_cleared = 0;

/* Tetromino shapes (4 rotations each) */
static const int PIECES[7][4][4] = {
    {{0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0}}, /* I */
    {{1,1,0,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}}, /* O */
    {{0,1,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}}, /* T */
    {{1,0,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}}, /* J */
    {{0,0,1,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}}, /* L */
    {{0,1,1,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}}, /* S */
    {{1,1,0,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}}  /* Z */
};

void tetris_spawn_piece(void) {
    piece_x = 3;
    piece_y = 0;
    piece_type = rand() % 7;
    piece_rot = 0;
}

void tetris_init(void) {
    for (int y = 0; y < BOARD_H; y++)
        for (int x = 0; x < BOARD_W; x++)
            board[y][x] = 0;
    tetris_level = 1;
    lines_cleared = 0;
    tetris_spawn_piece();
    log_game_event("TETRIS", "START", 0);
}

int tetris_check_collision(int px, int py) {
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            if (PIECES[piece_type][y][x]) {
                int nx = px + x;
                int ny = py + y;
                if (nx < 0 || nx >= BOARD_W || ny >= BOARD_H) return 1;
                if (ny >= 0 && board[ny][nx]) return 1;
            }
        }
    }
    return 0;
}

void tetris_lock_piece(void) {
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            if (PIECES[piece_type][y][x]) {
                int ny = piece_y + y;
                int nx = piece_x + x;
                if (ny >= 0 && ny < BOARD_H && nx >= 0 && nx < BOARD_W) {
                    board[ny][nx] = piece_type + 1;
                }
            }
        }
    }
    
    /* Clear lines */
    for (int y = BOARD_H - 1; y >= 0; y--) {
        int full = 1;
        for (int x = 0; x < BOARD_W; x++) {
            if (!board[y][x]) full = 0;
        }
        if (full) {
            for (int yy = y; yy > 0; yy--) {
                for (int x = 0; x < BOARD_W; x++) {
                    board[yy][x] = board[yy-1][x];
                }
            }
            lines_cleared++;
            global_score += 100;
            y++; /* Recheck this row */
        }
    }
    
    tetris_spawn_piece();
    
    if (tetris_check_collision(piece_x, piece_y)) {
        log_game_event("TETRIS", "DEATH", global_score);
        tetris_init();
    }
}

void tetris_update(void) {
    if (!tetris_check_collision(piece_x, piece_y + 1)) {
        piece_y++;
    } else {
        tetris_lock_piece();
    }
}

void tetris_draw(void) {
    vga_clear(0);
    
    int ox = 120, oy = 10;
    
    /* Border */
    vga_rect(ox - 2, oy - 2, BOARD_W * CELL_SIZE + 4, BOARD_H * CELL_SIZE + 4, 15);
    
    /* Board */
    for (int y = 0; y < BOARD_H; y++) {
        for (int x = 0; x < BOARD_W; x++) {
            if (board[y][x]) {
                vga_fill_rect(ox + x * CELL_SIZE, oy + y * CELL_SIZE, 
                              CELL_SIZE - 1, CELL_SIZE - 1, 8 + board[y][x]);
            }
        }
    }
    
    /* Current piece */
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            if (PIECES[piece_type][y][x]) {
                int px = ox + (piece_x + x) * CELL_SIZE;
                int py = oy + (piece_y + y) * CELL_SIZE;
                vga_fill_rect(px, py, CELL_SIZE - 1, CELL_SIZE - 1, 9 + piece_type);
            }
        }
    }
    
    /* Score */
    kprint_at(10, 50, "SCORE:", 15);
    kprint_int_at(10, 60, global_score, 14);
    kprint_at(10, 80, "LINES:", 15);
    kprint_int_at(10, 90, lines_cleared, 14);
}

void tetris_input(char key) {
    if ((key == 'a' || key == 'A') && !tetris_check_collision(piece_x - 1, piece_y)) piece_x--;
    if ((key == 'd' || key == 'D') && !tetris_check_collision(piece_x + 1, piece_y)) piece_x++;
    if ((key == 's' || key == 'S') && !tetris_check_collision(piece_x, piece_y + 1)) piece_y++;
    if (key == 'w' || key == 'W') { /* Rotate - simplified */ }
    if (key == ' ') { /* Hard drop */
        while (!tetris_check_collision(piece_x, piece_y + 1)) piece_y++;
        tetris_lock_piece();
    }
}

#endif /* FRAY_TETRIS_C */
""";
        
        writeFile(OUTPUT_DIR + "/tetris.c", tetris);
    }

    private static void buildAsteroids() throws IOException {
        System.out.println("   [7/7] Generating asteroids.c...");
        
        String asteroids = """
/* FRAY-ASTEROIDS - Gen 149 */

#ifndef FRAY_ASTEROIDS_C
#define FRAY_ASTEROIDS_C

#define MAX_ASTEROIDS 20
#define MAX_SHOTS 10

static int ship_ax = 160, ship_ay = 100;
static int ship_angle = 0;
static int ship_vx = 0, ship_vy = 0;

static int ast_x[MAX_ASTEROIDS], ast_y[MAX_ASTEROIDS];
static int ast_dx[MAX_ASTEROIDS], ast_dy[MAX_ASTEROIDS];
static int ast_size[MAX_ASTEROIDS]; /* 0=dead, 1=small, 2=med, 3=large */

static int shot_x[MAX_SHOTS], shot_y[MAX_SHOTS];
static int shot_dx[MAX_SHOTS], shot_dy[MAX_SHOTS];
static int shot_life[MAX_SHOTS];

void asteroids_spawn(int idx, int size) {
    ast_x[idx] = rand() % 320;
    ast_y[idx] = rand() % 200;
    ast_dx[idx] = (rand() % 5) - 2;
    ast_dy[idx] = (rand() % 5) - 2;
    ast_size[idx] = size;
}

void asteroids_init(void) {
    ship_ax = 160; ship_ay = 100;
    ship_angle = 0;
    ship_vx = ship_vy = 0;
    
    for (int i = 0; i < MAX_ASTEROIDS; i++) {
        if (i < 5) {
            asteroids_spawn(i, 3);
        } else {
            ast_size[i] = 0;
        }
    }
    
    for (int i = 0; i < MAX_SHOTS; i++) shot_life[i] = 0;
    
    log_game_event("ASTEROIDS", "START", 0);
}

void asteroids_update(void) {
    /* Update ship position */
    ship_ax += ship_vx;
    ship_ay += ship_vy;
    
    /* Wrap */
    if (ship_ax < 0) ship_ax = 319;
    if (ship_ax > 319) ship_ax = 0;
    if (ship_ay < 0) ship_ay = 199;
    if (ship_ay > 199) ship_ay = 0;
    
    /* Friction */
    ship_vx = ship_vx * 98 / 100;
    ship_vy = ship_vy * 98 / 100;
    
    /* Update asteroids */
    for (int i = 0; i < MAX_ASTEROIDS; i++) {
        if (ast_size[i] > 0) {
            ast_x[i] += ast_dx[i];
            ast_y[i] += ast_dy[i];
            
            /* Wrap */
            if (ast_x[i] < 0) ast_x[i] = 319;
            if (ast_x[i] > 319) ast_x[i] = 0;
            if (ast_y[i] < 0) ast_y[i] = 199;
            if (ast_y[i] > 199) ast_y[i] = 0;
            
            /* Ship collision */
            int radius = ast_size[i] * 8;
            int dx = ship_ax - ast_x[i];
            int dy = ship_ay - ast_y[i];
            if (dx * dx + dy * dy < radius * radius) {
                log_game_event("ASTEROIDS", "DEATH", global_score);
                asteroids_init();
                return;
            }
        }
    }
    
    /* Update shots */
    for (int i = 0; i < MAX_SHOTS; i++) {
        if (shot_life[i] > 0) {
            shot_x[i] += shot_dx[i];
            shot_y[i] += shot_dy[i];
            shot_life[i]--;
            
            /* Asteroid collision */
            for (int j = 0; j < MAX_ASTEROIDS; j++) {
                if (ast_size[j] > 0) {
                    int dx = shot_x[i] - ast_x[j];
                    int dy = shot_y[i] - ast_y[j];
                    int radius = ast_size[j] * 8;
                    if (dx * dx + dy * dy < radius * radius) {
                        shot_life[i] = 0;
                        global_score += ast_size[j] * 20;
                        
                        /* Split asteroid */
                        if (ast_size[j] > 1) {
                            for (int k = 0; k < MAX_ASTEROIDS; k++) {
                                if (ast_size[k] == 0) {
                                    ast_x[k] = ast_x[j];
                                    ast_y[k] = ast_y[j];
                                    ast_dx[k] = (rand() % 5) - 2;
                                    ast_dy[k] = (rand() % 5) - 2;
                                    ast_size[k] = ast_size[j] - 1;
                                    break;
                                }
                            }
                        }
                        ast_size[j]--;
                        break;
                    }
                }
            }
        }
    }
}

void asteroids_draw(void) {
    vga_clear(0);
    
    /* Ship (triangle) */
    vga_fill_rect(ship_ax - 3, ship_ay - 3, 6, 6, 15);
    
    /* Asteroids */
    for (int i = 0; i < MAX_ASTEROIDS; i++) {
        if (ast_size[i] > 0) {
            int r = ast_size[i] * 6;
            vga_rect(ast_x[i] - r, ast_y[i] - r, r * 2, r * 2, 7);
        }
    }
    
    /* Shots */
    for (int i = 0; i < MAX_SHOTS; i++) {
        if (shot_life[i] > 0) {
            vga_fill_rect(shot_x[i] - 1, shot_y[i] - 1, 2, 2, 14);
        }
    }
    
    /* Score */
    kprint_int_at(10, 5, global_score, 15);
}

void asteroids_input(char key) {
    if (key == 'a' || key == 'A') ship_angle -= 15;
    if (key == 'd' || key == 'D') ship_angle += 15;
    if (key == 'w' || key == 'W') {
        /* Thrust (simplified - just move forward) */
        ship_vx += 1;
    }
    if (key == ' ') {
        /* Fire */
        for (int i = 0; i < MAX_SHOTS; i++) {
            if (shot_life[i] == 0) {
                shot_x[i] = ship_ax;
                shot_y[i] = ship_ay;
                shot_dx[i] = 4;
                shot_dy[i] = 0;
                shot_life[i] = 50;
                break;
            }
        }
    }
}

#endif /* FRAY_ASTEROIDS_C */
""";
        
        writeFile(OUTPUT_DIR + "/asteroids.c", asteroids);
    }

    private static void writeFile(String path, String content) throws IOException {
        Path filePath = Paths.get(path);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, content);
        System.out.println("      → " + path);
    }
}
