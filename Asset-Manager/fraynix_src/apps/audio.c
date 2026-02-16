/* ═══════════════════════════════════════════════════════════════════════════
 * FRAY-AUDIO: WAV Player - Gen 151
 * ═══════════════════════════════════════════════════════════════════════════ */

#ifndef FRAY_AUDIO_C
#define FRAY_AUDIO_C

/* WAV Header */
typedef struct __attribute__((packed)) {
    char riff[4];           /* "RIFF" */
    unsigned int file_size;
    char wave[4];           /* "WAVE" */
    char fmt[4];            /* "fmt " */
    unsigned int fmt_size;
    unsigned short format;  /* 1 = PCM */
    unsigned short channels;
    unsigned int sample_rate;
    unsigned int byte_rate;
    unsigned short block_align;
    unsigned short bits_per_sample;
    char data[4];           /* "data" */
    unsigned int data_size;
} WavHeader;

typedef struct {
    unsigned int sample_rate;
    unsigned short channels;
    unsigned short bits;
    unsigned int length;
    unsigned char* samples;
} AudioClip;

static AudioClip current_audio;
static int audio_playing = 0;
static unsigned int audio_position = 0;

/* PC Speaker port */
#define PIT_CMD  0x43
#define PIT_CH2  0x42
#define SPEAKER  0x61

void speaker_on(void) {
    unsigned char tmp = inb(SPEAKER);
    outb(SPEAKER, tmp | 3);
}

void speaker_off(void) {
    unsigned char tmp = inb(SPEAKER);
    outb(SPEAKER, tmp & 0xFC);
}

void speaker_freq(unsigned int freq) {
    if (freq == 0) {
        speaker_off();
        return;
    }
    
    unsigned int div = 1193180 / freq;
    outb(PIT_CMD, 0xB6);
    outb(PIT_CH2, div & 0xFF);
    outb(PIT_CH2, (div >> 8) & 0xFF);
    speaker_on();
}

/* Load WAV file */
int audio_load_wav(const char* filename) {
    unsigned long long hash = phi_hash(filename, kstrlen(filename));
    unsigned char* data = (unsigned char*)get_memory_block_by_hash(hash);
    
    if (!data) {
        kprint_color("[AUDIO] File not found\n", 0x0C);
        return 0;
    }
    
    WavHeader* header = (WavHeader*)data;
    
    /* Verify RIFF/WAVE */
    if (header->riff[0] != 'R' || header->riff[1] != 'I' ||
        header->riff[2] != 'F' || header->riff[3] != 'F') {
        kprint_color("[AUDIO] Not a WAV file\n", 0x0C);
        return 0;
    }
    
    current_audio.sample_rate = header->sample_rate;
    current_audio.channels = header->channels;
    current_audio.bits = header->bits_per_sample;
    current_audio.length = header->data_size;
    current_audio.samples = data + sizeof(WavHeader);
    
    kprint("[AUDIO] Loaded: ");
    kprint_int(current_audio.sample_rate);
    kprint("Hz, ");
    kprint_int(current_audio.channels);
    kprint("ch, ");
    kprint_int(current_audio.bits);
    kprint("bit\n");
    
    return 1;
}

/* Get sample at position */
int audio_get_sample(unsigned int pos) {
    if (pos >= current_audio.length) return 0;
    
    if (current_audio.bits == 8) {
        return (int)current_audio.samples[pos] - 128;
    } else if (current_audio.bits == 16) {
        short* samples = (short*)current_audio.samples;
        return samples[pos / 2] / 256;
    }
    
    return 0;
}

/* Play audio (blocking, simplified) */
void audio_play(void) {
    if (!current_audio.samples) return;
    
    audio_playing = 1;
    audio_position = 0;
    
    unsigned int samples_per_tick = current_audio.sample_rate / 1000;
    
    kprint("[AUDIO] Playing...\n");
    
    while (audio_position < current_audio.length && audio_playing) {
        /* Convert sample to frequency for PC speaker */
        int sample = audio_get_sample(audio_position);
        
        /* Map sample (-128 to 127) to frequency (200-2000 Hz) */
        int freq = 1000 + sample * 7;
        if (freq < 200) freq = 200;
        if (freq > 2000) freq = 2000;
        
        speaker_freq(freq);
        
        audio_position += current_audio.channels;
        
        /* Simple timing */
        for (volatile int i = 0; i < 100; i++);
        
        /* Check for stop */
        if (has_input()) {
            unsigned char key = read_key();
            if (key == 'q' || key == 27 || key == ' ') {
                audio_playing = 0;
            }
        }
    }
    
    speaker_off();
    audio_playing = 0;
    kprint("[AUDIO] Stopped\n");
}

/* Waveform visualization */
void audio_draw_waveform(void) {
    if (!current_audio.samples) return;
    
    clear_screen();
    
    int width = 80;
    int height = 20;
    int center = height / 2;
    
    unsigned int step = current_audio.length / width;
    
    for (int x = 0; x < width; x++) {
        unsigned int pos = x * step;
        int sample = audio_get_sample(pos);
        
        /* Normalize to screen height */
        int y = center - (sample * center / 128);
        if (y < 0) y = 0;
        if (y >= height) y = height - 1;
        
        set_cursor(x, y);
        set_color(0x0A);
        kprint("*");
        
        /* Draw center line */
        set_cursor(x, center);
        set_color(0x08);
        kprint("-");
    }
    
    /* Info */
    set_cursor(0, height + 1);
    set_color(0x0F);
    kprint("Duration: ");
    int seconds = current_audio.length / current_audio.sample_rate / current_audio.channels / (current_audio.bits / 8);
    kprint_int(seconds);
    kprint("s | [SPACE] Play | [Q] Quit");
}

/* Audio player main */
void audio_player_main(const char* filename) {
    if (!audio_load_wav(filename)) {
        return;
    }
    
    audio_draw_waveform();
    
    while (1) {
        unsigned char key = read_key();
        
        switch (key) {
            case 'q':
            case 27:
                return;
                
            case ' ':
                audio_play();
                audio_draw_waveform();
                break;
        }
    }
}

/* Beep (simple tone) */
void audio_beep(unsigned int freq, unsigned int duration_ms) {
    speaker_freq(freq);
    
    unsigned long long start = get_tick_count();
    while (get_tick_count() - start < duration_ms);
    
    speaker_off();
}

/* Play note by name */
void audio_note(const char* note, unsigned int duration_ms) {
    unsigned int freq = 440;  /* A4 default */
    
    /* Parse note (e.g., "C4", "A#5") */
    int base_note = 0;
    int octave = 4;
    int sharp = 0;
    
    switch (note[0]) {
        case 'C': base_note = 0; break;
        case 'D': base_note = 2; break;
        case 'E': base_note = 4; break;
        case 'F': base_note = 5; break;
        case 'G': base_note = 7; break;
        case 'A': base_note = 9; break;
        case 'B': base_note = 11; break;
    }
    
    if (note[1] == '#') {
        sharp = 1;
        if (note[2] >= '0' && note[2] <= '9') octave = note[2] - '0';
    } else if (note[1] >= '0' && note[1] <= '9') {
        octave = note[1] - '0';
    }
    
    /* Calculate frequency: f = 440 * 2^((n-49)/12) where n is key number */
    int key = (octave - 4) * 12 + base_note + sharp - 9;  /* A4 = key 0 */
    
    /* Approximate 2^(key/12) using lookup or calculation */
    freq = 440;
    if (key > 0) {
        for (int i = 0; i < key; i++) freq = freq * 1059 / 1000;  /* ~2^(1/12) */
    } else {
        for (int i = 0; i < -key; i++) freq = freq * 1000 / 1059;
    }
    
    audio_beep(freq, duration_ms);
}

#endif /* FRAY_AUDIO_C */
