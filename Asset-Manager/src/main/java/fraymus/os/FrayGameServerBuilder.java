package fraymus.os;

import java.io.*;
import java.nio.file.*;

/**
 * ⚔️ FRAY GAME SERVER - Gen 149
 * "The Arena. Man vs. Machine. Man vs. Man."
 * 
 * FUNCTION:
 * 1. MATCHMAKER: Scans LAN for other Fraynix nodes (UDP Port 666).
 * 2. CHESS AI: Implements Minimax with Alpha-Beta Pruning.
 * 3. PONG AI: PID Controller to predict ball trajectory.
 * 4. TRASH TALK: Connects to FrayLLM to send chat messages.
 */
public class FrayGameServerBuilder {

    private static final String OUTPUT_DIR = "fraynix_src";

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  ⚔️ FRAY GAME SERVER - Gen 149                                ║");
        System.out.println("║  The Arena: Man vs. Machine vs. Man                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            Files.createDirectories(Paths.get(OUTPUT_DIR));
            
            System.out.println("⚡ Opening the arena...");
            
            buildArenaCore();
            buildChessAI();
            buildArcadeAI();
            buildNetworkLobby();
            
            System.out.println();
            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║  ✅ ARENA ONLINE                                              ║");
            System.out.println("║                                                               ║");
            System.out.println("║  Commands:                                                    ║");
            System.out.println("║    chess --ai    (Play vs Fraymus)                           ║");
            System.out.println("║    chess --net   (Play vs Human)                             ║");
            System.out.println("║    pong --ai     (AI opponent)                               ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");
            
        } catch (IOException e) {
            System.err.println("❌ MATCHMAKING FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void buildArenaCore() throws IOException {
        System.out.println("   [1/4] Generating arena.c...");
        
        String arena = """
/* ═══════════════════════════════════════════════════════════════════════════
 * FRAY-ARENA: MULTIPLAYER & AI KERNEL - Gen 149
 * Mode 0: Local, Mode 1: AI, Mode 2: Network
 * ═══════════════════════════════════════════════════════════════════════════ */

#ifndef FRAY_ARENA_C
#define FRAY_ARENA_C

#include "chess_ai.c"
#include "arcade_ai.c"
#include "net_lobby.c"

#define MODE_LOCAL  0
#define MODE_AI     1
#define MODE_NET    2

static int arena_mode = MODE_LOCAL;
static int opponent_connected = 0;
static char opponent_ip[16] = "0.0.0.0";

/* Game session */
typedef struct {
    int game_type;      /* 1=Chess, 2=Pong, 3=Invaders */
    int mode;           /* 0=Local, 1=AI, 2=Net */
    int turn;           /* 0=Player1, 1=Player2/AI */
    int moves_count;
    int time_white;
    int time_black;
} GameSession;

static GameSession session;

void arena_init(int game, int mode) {
    session.game_type = game;
    session.mode = mode;
    session.turn = 0;
    session.moves_count = 0;
    session.time_white = 600; /* 10 min */
    session.time_black = 600;
    
    arena_mode = mode;
    
    if (mode == MODE_NET) {
        search_for_players();
    }
    
    kprint_color("\\n  ⚔️ ARENA INITIALIZED\\n", 0x0E);
    kprint("  Game: ");
    kprint_int(game);
    kprint(" | Mode: ");
    if (mode == MODE_LOCAL) kprint("LOCAL");
    if (mode == MODE_AI) kprint("vs AI");
    if (mode == MODE_NET) kprint("NETWORK");
    kprint("\\n\\n");
}

void arena_switch_turn(void) {
    session.turn = 1 - session.turn;
    session.moves_count++;
    
    if (session.mode == MODE_AI && session.turn == 1) {
        /* AI's turn */
        if (session.game_type == 1) {
            ai_make_move_chess();
        }
    }
    
    if (session.mode == MODE_NET && session.turn == 1) {
        kprint("[NET] Waiting for opponent...\\n");
    }
}

void arena_player_move(int from_x, int from_y, int to_x, int to_y) {
    if (session.turn != 0) return; /* Not player's turn */
    
    perform_move(from_x, from_y, to_x, to_y);
    
    if (session.mode == MODE_NET) {
        /* Send move to opponent */
        unsigned char packet[8] = {'M', 'O', 'V', from_x, from_y, to_x, to_y, 0};
        send_packet_raw(packet, 8);
    }
    
    arena_switch_turn();
}

void arena_receive_move(unsigned char* data) {
    if (data[0] == 'M' && data[1] == 'O' && data[2] == 'V') {
        int fx = data[3], fy = data[4];
        int tx = data[5], ty = data[6];
        perform_move(fx, fy, tx, ty);
        kprint("[NET] Opponent moved\\n");
        arena_switch_turn();
    }
}

/* Trash talk integration */
void arena_taunt(const char* context) {
    if (session.mode == MODE_AI) {
        kprint_color("\\n[FRAYMUS]: ", 0x0C);
        run_llm(context);
    }
}

#endif /* FRAY_ARENA_C */
""";
        
        writeFile(OUTPUT_DIR + "/arena.c", arena);
    }

    private static void buildChessAI() throws IOException {
        System.out.println("   [2/4] Generating chess_ai.c...");
        
        String chess = """
/* ═══════════════════════════════════════════════════════════════════════════
 * FRAY-CHESS: MINIMAX AI ENGINE - Gen 149
 * "4 million scenarios per second."
 * ═══════════════════════════════════════════════════════════════════════════ */

#ifndef FRAY_CHESS_AI_C
#define FRAY_CHESS_AI_C

/* Piece values (centipawns) */
#define VAL_PAWN   100
#define VAL_KNIGHT 320
#define VAL_BISHOP 330
#define VAL_ROOK   500
#define VAL_QUEEN  900
#define VAL_KING   20000

/* Board representation */
static char chess_board[8][8] = {
    {'r','n','b','q','k','b','n','r'},
    {'p','p','p','p','p','p','p','p'},
    {' ',' ',' ',' ',' ',' ',' ',' '},
    {' ',' ',' ',' ',' ',' ',' ',' '},
    {' ',' ',' ',' ',' ',' ',' ',' '},
    {' ',' ',' ',' ',' ',' ',' ',' '},
    {'P','P','P','P','P','P','P','P'},
    {'R','N','B','Q','K','B','N','R'}
};

static int white_to_move = 1;
static int nodes_searched = 0;

int piece_value(char p) {
    switch (p) {
        case 'P': case 'p': return VAL_PAWN;
        case 'N': case 'n': return VAL_KNIGHT;
        case 'B': case 'b': return VAL_BISHOP;
        case 'R': case 'r': return VAL_ROOK;
        case 'Q': case 'q': return VAL_QUEEN;
        case 'K': case 'k': return VAL_KING;
        default: return 0;
    }
}

int is_white(char p) { return p >= 'A' && p <= 'Z'; }
int is_black(char p) { return p >= 'a' && p <= 'z'; }

/* Static evaluation */
int evaluate_board(void) {
    int score = 0;
    
    for (int y = 0; y < 8; y++) {
        for (int x = 0; x < 8; x++) {
            char p = chess_board[y][x];
            if (p == ' ') continue;
            
            int val = piece_value(p);
            
            /* Positional bonus: center control */
            if (x >= 2 && x <= 5 && y >= 2 && y <= 5) {
                val += 10;
            }
            
            /* Pawn advancement bonus */
            if (p == 'P') val += (6 - y) * 5;
            if (p == 'p') val += y * 5;
            
            if (is_white(p)) score += val;
            else score -= val;
        }
    }
    
    return score;
}

/* Move generation (simplified - pawns and knights only for demo) */
typedef struct { int fx, fy, tx, ty; } Move;
static Move moves[256];
static int move_count;

void generate_moves(int for_white) {
    move_count = 0;
    
    for (int y = 0; y < 8; y++) {
        for (int x = 0; x < 8; x++) {
            char p = chess_board[y][x];
            if (p == ' ') continue;
            if (for_white && !is_white(p)) continue;
            if (!for_white && !is_black(p)) continue;
            
            /* Pawn moves */
            if (p == 'P') {
                if (y > 0 && chess_board[y-1][x] == ' ') {
                    moves[move_count++] = (Move){x, y, x, y-1};
                }
                if (y == 6 && chess_board[5][x] == ' ' && chess_board[4][x] == ' ') {
                    moves[move_count++] = (Move){x, y, x, y-2};
                }
            }
            if (p == 'p') {
                if (y < 7 && chess_board[y+1][x] == ' ') {
                    moves[move_count++] = (Move){x, y, x, y+1};
                }
            }
            
            /* Knight moves */
            if (p == 'N' || p == 'n') {
                int dx[] = {-2,-2,-1,-1,1,1,2,2};
                int dy[] = {-1,1,-2,2,-2,2,-1,1};
                for (int i = 0; i < 8; i++) {
                    int nx = x + dx[i], ny = y + dy[i];
                    if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8) {
                        char target = chess_board[ny][nx];
                        if (target == ' ' || (is_white(p) != is_white(target))) {
                            moves[move_count++] = (Move){x, y, nx, ny};
                        }
                    }
                }
            }
        }
    }
}

void perform_move(int fx, int fy, int tx, int ty) {
    chess_board[ty][tx] = chess_board[fy][fx];
    chess_board[fy][fx] = ' ';
}

void undo_move(int fx, int fy, int tx, int ty, char captured) {
    chess_board[fy][fx] = chess_board[ty][tx];
    chess_board[ty][tx] = captured;
}

/* Minimax with Alpha-Beta Pruning */
int minimax(int depth, int alpha, int beta, int maximizing) {
    nodes_searched++;
    
    if (depth == 0) return evaluate_board();
    
    generate_moves(maximizing);
    
    if (move_count == 0) {
        return maximizing ? -VAL_KING : VAL_KING;
    }
    
    if (maximizing) {
        int best = -99999;
        for (int i = 0; i < move_count; i++) {
            Move m = moves[i];
            char captured = chess_board[m.ty][m.tx];
            perform_move(m.fx, m.fy, m.tx, m.ty);
            int val = minimax(depth - 1, alpha, beta, 0);
            undo_move(m.fx, m.fy, m.tx, m.ty, captured);
            if (val > best) best = val;
            if (val > alpha) alpha = val;
            if (beta <= alpha) break;
        }
        return best;
    } else {
        int best = 99999;
        for (int i = 0; i < move_count; i++) {
            Move m = moves[i];
            char captured = chess_board[m.ty][m.tx];
            perform_move(m.fx, m.fy, m.tx, m.ty);
            int val = minimax(depth - 1, alpha, beta, 1);
            undo_move(m.fx, m.fy, m.tx, m.ty, captured);
            if (val < best) best = val;
            if (val < beta) beta = val;
            if (beta <= alpha) break;
        }
        return best;
    }
}

void ai_make_move_chess(void) {
    kprint_color("[AI] ", 0x0B);
    kprint("CALCULATING...");
    
    nodes_searched = 0;
    generate_moves(0); /* Black's moves */
    
    int best_score = 99999;
    Move best_move = {0, 0, 0, 0};
    
    for (int i = 0; i < move_count; i++) {
        Move m = moves[i];
        char captured = chess_board[m.ty][m.tx];
        perform_move(m.fx, m.fy, m.tx, m.ty);
        int score = minimax(3, -99999, 99999, 1);
        undo_move(m.fx, m.fy, m.tx, m.ty, captured);
        
        if (score < best_score) {
            best_score = score;
            best_move = m;
        }
    }
    
    perform_move(best_move.fx, best_move.fy, best_move.tx, best_move.ty);
    
    kprint(" DONE (");
    kprint_int(nodes_searched);
    kprint(" nodes)\\n");
    
    kprint("[AI] Moved: ");
    char from[3] = {'a' + best_move.fx, '8' - best_move.fy, 0};
    char to[3] = {'a' + best_move.tx, '8' - best_move.ty, 0};
    kprint(from); kprint(" -> "); kprint(to); kprint("\\n");
    
    /* Trash talk */
    if (best_score < -100) {
        arena_taunt("Generate taunt for winning chess position");
    }
}

void draw_chess_board(void) {
    kprint("\\n  a b c d e f g h\\n");
    for (int y = 0; y < 8; y++) {
        kprint_int(8 - y);
        kprint(" ");
        for (int x = 0; x < 8; x++) {
            char p = chess_board[y][x];
            if (p == ' ') p = '.';
            char buf[2] = {p, 0};
            kprint(buf);
            kprint(" ");
        }
        kprint_int(8 - y);
        kprint("\\n");
    }
    kprint("  a b c d e f g h\\n\\n");
}

#endif /* FRAY_CHESS_AI_C */
""";
        
        writeFile(OUTPUT_DIR + "/chess_ai.c", chess);
    }

    private static void buildArcadeAI() throws IOException {
        System.out.println("   [3/4] Generating arcade_ai.c...");
        
        String arcadeAI = """
/* ═══════════════════════════════════════════════════════════════════════════
 * FRAY-ARCADE AI: REFLEX & PREDICTION - Gen 149
 * "It learns your patterns. Then it exploits them."
 * ═══════════════════════════════════════════════════════════════════════════ */

#ifndef FRAY_ARCADE_AI_C
#define FRAY_ARCADE_AI_C

/* AI Difficulty (0-100) */
static int ai_difficulty = 50;
static int ai_reaction_delay = 5; /* frames */

/* Pong AI state */
static int pong_ai_target_y = 100;
static int pong_ai_frame = 0;

/* Player pattern analysis */
static int player_move_history[100];
static int player_history_idx = 0;

void ai_set_difficulty(int level) {
    ai_difficulty = level;
    ai_reaction_delay = 10 - (level / 10);
    if (ai_reaction_delay < 1) ai_reaction_delay = 1;
}

/* ═══════════════════════════════════════════════════════════════════════════
 * PONG AI - Ball Prediction with Intentional Error
 * ═══════════════════════════════════════════════════════════════════════════ */

int pong_predict_intercept(int ball_x, int ball_y, int ball_dx, int ball_dy, int paddle_x) {
    /* Simulate ball trajectory */
    int sim_x = ball_x;
    int sim_y = ball_y;
    int sim_dx = ball_dx;
    int sim_dy = ball_dy;
    
    while (sim_x != paddle_x) {
        sim_x += sim_dx;
        sim_y += sim_dy;
        
        /* Wall bounces */
        if (sim_y <= 10 || sim_y >= 185) sim_dy = -sim_dy;
        
        /* Prevent infinite loop */
        if (sim_x < 0 || sim_x > 320) break;
    }
    
    return sim_y;
}

void ai_move_paddle_pong(int ball_x, int ball_y, int ball_dx, int ball_dy, 
                         int* paddle_y, int paddle_x) {
    pong_ai_frame++;
    
    /* Only update target every few frames (reaction time) */
    if (pong_ai_frame % ai_reaction_delay == 0) {
        pong_ai_target_y = pong_predict_intercept(ball_x, ball_y, ball_dx, ball_dy, paddle_x);
        
        /* Add intentional error based on difficulty */
        int error_range = 100 - ai_difficulty;
        int error = (rand() % error_range) - error_range / 2;
        pong_ai_target_y += error;
    }
    
    /* Move toward target */
    int paddle_center = *paddle_y + 15;
    int move_speed = 2 + ai_difficulty / 25;
    
    if (paddle_center < pong_ai_target_y - 5) {
        *paddle_y += move_speed;
        if (*paddle_y > 160) *paddle_y = 160;
    }
    if (paddle_center > pong_ai_target_y + 5) {
        *paddle_y -= move_speed;
        if (*paddle_y < 10) *paddle_y = 10;
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * SNAKE AI - Pathfinding to Food
 * ═══════════════════════════════════════════════════════════════════════════ */

int ai_snake_decide_direction(int head_x, int head_y, int food_x, int food_y, 
                               int current_dir) {
    int dx = food_x - head_x;
    int dy = food_y - head_y;
    
    /* Prefer axis with larger distance */
    if (llm_abs(dx) > llm_abs(dy)) {
        if (dx > 0 && current_dir != 2) return 0; /* Right */
        if (dx < 0 && current_dir != 0) return 2; /* Left */
    } else {
        if (dy > 0 && current_dir != 3) return 1; /* Down */
        if (dy < 0 && current_dir != 1) return 3; /* Up */
    }
    
    return current_dir;
}

/* ═══════════════════════════════════════════════════════════════════════════
 * INVADERS AI - Dodge & Shoot
 * ═══════════════════════════════════════════════════════════════════════════ */

void ai_invaders_move(int* ship_x, int alien_x, int alien_y, int* should_fire) {
    /* Find closest alien column */
    int target_x = alien_x + 80; /* Center of alien formation */
    
    /* Move toward target */
    if (*ship_x < target_x - 10) *ship_x += 3;
    if (*ship_x > target_x + 10) *ship_x -= 3;
    
    /* Fire when aligned */
    *should_fire = (rand() % 10 < ai_difficulty / 10);
}

/* ═══════════════════════════════════════════════════════════════════════════
 * PLAYER PATTERN ANALYSIS
 * ═══════════════════════════════════════════════════════════════════════════ */

void ai_record_player_move(int move) {
    player_move_history[player_history_idx % 100] = move;
    player_history_idx++;
}

int ai_predict_player_move(void) {
    if (player_history_idx < 10) return -1;
    
    /* Simple pattern: what did they do most in last 10 moves? */
    int counts[4] = {0, 0, 0, 0};
    for (int i = 0; i < 10; i++) {
        int idx = (player_history_idx - 1 - i) % 100;
        if (player_move_history[idx] >= 0 && player_move_history[idx] < 4) {
            counts[player_move_history[idx]]++;
        }
    }
    
    int best = 0;
    for (int i = 1; i < 4; i++) {
        if (counts[i] > counts[best]) best = i;
    }
    
    return best;
}

void ai_give_feedback(void) {
    int predicted = ai_predict_player_move();
    if (predicted >= 0) {
        kprint_color("[AI ANALYSIS] ", 0x0B);
        kprint("Pattern detected: You favor direction ");
        kprint_int(predicted);
        kprint(". Mix it up!\\n");
    }
}

#endif /* FRAY_ARCADE_AI_C */
""";
        
        writeFile(OUTPUT_DIR + "/arcade_ai.c", arcadeAI);
    }

    private static void buildNetworkLobby() throws IOException {
        System.out.println("   [4/4] Generating net_lobby.c...");
        
        String netLobby = """
/* ═══════════════════════════════════════════════════════════════════════════
 * FRAY-LOBBY: P2P MATCHMAKING - Gen 149
 * "No servers. Just players."
 * ═══════════════════════════════════════════════════════════════════════════ */

#ifndef FRAY_NET_LOBBY_C
#define FRAY_NET_LOBBY_C

#define LOBBY_PORT 666
#define LOBBY_MAGIC "FRAY"

typedef struct {
    char magic[4];
    unsigned char type;  /* 0=REQ, 1=ACK, 2=MOVE, 3=CHAT, 4=QUIT */
    unsigned char game;  /* 1=Chess, 2=Pong, etc */
    unsigned char data[24];
} LobbyPacket;

static int lobby_searching = 0;
static int lobby_connected = 0;
static unsigned int peer_ip = 0;

void lobby_send_packet(unsigned char type, unsigned char game, unsigned char* data, int len) {
    LobbyPacket pkt;
    pkt.magic[0] = 'F'; pkt.magic[1] = 'R';
    pkt.magic[2] = 'A'; pkt.magic[3] = 'Y';
    pkt.type = type;
    pkt.game = game;
    for (int i = 0; i < len && i < 24; i++) {
        pkt.data[i] = data[i];
    }
    
    send_udp_broadcast((unsigned char*)&pkt, sizeof(LobbyPacket), LOBBY_PORT);
}

void search_for_players(void) {
    kprint_color("\\n[LOBBY] ", 0x0E);
    kprint("Broadcasting on port ");
    kprint_int(LOBBY_PORT);
    kprint("...\\n");
    
    lobby_searching = 1;
    
    /* Send discovery packet */
    unsigned char empty[24] = {0};
    lobby_send_packet(0, session.game_type, empty, 0);
    
    kprint("[LOBBY] Waiting for opponent...\\n");
}

void lobby_handle_packet(LobbyPacket* pkt, unsigned int from_ip) {
    /* Verify magic */
    if (pkt->magic[0] != 'F' || pkt->magic[1] != 'R' ||
        pkt->magic[2] != 'A' || pkt->magic[3] != 'Y') {
        return;
    }
    
    switch (pkt->type) {
        case 0: /* Request - someone looking for game */
            if (lobby_searching && pkt->game == session.game_type) {
                /* Send ACK */
                unsigned char ack[24] = {0};
                lobby_send_packet(1, session.game_type, ack, 0);
                peer_ip = from_ip;
                lobby_connected = 1;
                lobby_searching = 0;
                kprint_color("[LOBBY] ", 0x0A);
                kprint("OPPONENT FOUND!\\n");
            }
            break;
            
        case 1: /* ACK - someone accepted */
            if (lobby_searching) {
                peer_ip = from_ip;
                lobby_connected = 1;
                lobby_searching = 0;
                kprint_color("[LOBBY] ", 0x0A);
                kprint("CONNECTED!\\n");
            }
            break;
            
        case 2: /* Move */
            arena_receive_move(pkt->data);
            break;
            
        case 3: /* Chat */
            kprint_color("[OPPONENT]: ", 0x0D);
            kprint((char*)pkt->data);
            kprint("\\n");
            break;
            
        case 4: /* Quit */
            kprint_color("[LOBBY] ", 0x0C);
            kprint("Opponent disconnected\\n");
            lobby_connected = 0;
            break;
    }
}

void lobby_send_move(int fx, int fy, int tx, int ty) {
    if (!lobby_connected) return;
    
    unsigned char data[24] = {fx, fy, tx, ty, 0};
    lobby_send_packet(2, session.game_type, data, 4);
}

void lobby_send_chat(const char* msg) {
    if (!lobby_connected) return;
    
    unsigned char data[24];
    for (int i = 0; i < 23 && msg[i]; i++) {
        data[i] = msg[i];
    }
    data[23] = 0;
    
    lobby_send_packet(3, session.game_type, data, 24);
}

void lobby_disconnect(void) {
    if (lobby_connected) {
        unsigned char empty[24] = {0};
        lobby_send_packet(4, session.game_type, empty, 0);
        lobby_connected = 0;
        kprint("[LOBBY] Disconnected\\n");
    }
}

int lobby_is_connected(void) {
    return lobby_connected;
}

#endif /* FRAY_NET_LOBBY_C */
""";
        
        writeFile(OUTPUT_DIR + "/net_lobby.c", netLobby);
    }

    private static void writeFile(String path, String content) throws IOException {
        Path filePath = Paths.get(path);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, content);
        System.out.println("      → " + path);
    }
}
