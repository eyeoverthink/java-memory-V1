/* ═══════════════════════════════════════════════════════════════════════════
 * FRAY-HTTP: HTTP/1.1 Client - Gen 151
 * ═══════════════════════════════════════════════════════════════════════════ */

#ifndef FRAY_HTTP_C
#define FRAY_HTTP_C

#define HTTP_PORT 80
#define HTTPS_PORT 443
#define MAX_RESPONSE_SIZE 65536

typedef struct {
    int status_code;
    char content_type[64];
    int content_length;
    char* body;
    int body_length;
} HttpResponse;

typedef struct {
    char host[128];
    int port;
    char path[256];
    char query[256];
} ParsedUrl;

/* Parse URL */
int parse_url(const char* url, ParsedUrl* out) {
    out->port = 80;
    out->path[0] = '/';
    out->path[1] = '\0';
    out->query[0] = '\0';
    
    const char* ptr = url;
    
    /* Skip protocol */
    if (kstrncmp(ptr, "http://", 7) == 0) {
        ptr += 7;
    } else if (kstrncmp(ptr, "https://", 8) == 0) {
        ptr += 8;
        out->port = 443;
    }
    
    /* Extract host */
    int i = 0;
    while (*ptr && *ptr != '/' && *ptr != ':' && *ptr != '?' && i < 127) {
        out->host[i++] = *ptr++;
    }
    out->host[i] = '\0';
    
    /* Extract port if present */
    if (*ptr == ':') {
        ptr++;
        out->port = 0;
        while (*ptr >= '0' && *ptr <= '9') {
            out->port = out->port * 10 + (*ptr++ - '0');
        }
    }
    
    /* Extract path */
    if (*ptr == '/') {
        i = 0;
        while (*ptr && *ptr != '?' && i < 255) {
            out->path[i++] = *ptr++;
        }
        out->path[i] = '\0';
    }
    
    /* Extract query */
    if (*ptr == '?') {
        ptr++;
        i = 0;
        while (*ptr && i < 255) {
            out->query[i++] = *ptr++;
        }
        out->query[i] = '\0';
    }
    
    return 1;
}

/* DNS Resolution (simplified - uses hosts file or broadcast) */
unsigned int dns_resolve(const char* hostname) {
    /* Check local hosts */
    if (kstrcmp(hostname, "localhost") == 0) return 0x7F000001;
    
    /* For now, assume IP is provided or use broadcast discovery */
    /* Real implementation would send DNS query to 8.8.8.8 */
    
    /* Parse IP if numeric */
    unsigned int ip = 0;
    int part = 0, parts = 0;
    
    for (int i = 0; hostname[i] && parts < 4; i++) {
        if (hostname[i] >= '0' && hostname[i] <= '9') {
            part = part * 10 + (hostname[i] - '0');
        } else if (hostname[i] == '.') {
            ip = (ip << 8) | (part & 0xFF);
            part = 0;
            parts++;
        }
    }
    ip = (ip << 8) | (part & 0xFF);
    
    return (parts == 3) ? ip : 0;
}

/* Build HTTP Request */
int build_request(char* buf, int buf_size, const char* method, ParsedUrl* url) {
    int len = 0;
    
    len += ksprintf(&buf[len], "%s %s", method, url->path);
    if (url->query[0]) {
        len += ksprintf(&buf[len], "?%s", url->query);
    }
    len += ksprintf(&buf[len], " HTTP/1.1\r\n");
    len += ksprintf(&buf[len], "Host: %s\r\n", url->host);
    len += ksprintf(&buf[len], "User-Agent: FrayBrowser/1.0\r\n");
    len += ksprintf(&buf[len], "Accept: text/html,text/plain,*/*\r\n");
    len += ksprintf(&buf[len], "Connection: close\r\n");
    len += ksprintf(&buf[len], "\r\n");
    
    return len;
}

/* Parse HTTP Response */
int parse_response(const char* data, int data_len, HttpResponse* resp) {
    resp->status_code = 0;
    resp->content_length = 0;
    resp->body = 0;
    
    /* Parse status line */
    if (kstrncmp(data, "HTTP/1.", 7) != 0) return 0;
    
    const char* ptr = data + 9;
    while (*ptr == ' ') ptr++;
    
    resp->status_code = 0;
    while (*ptr >= '0' && *ptr <= '9') {
        resp->status_code = resp->status_code * 10 + (*ptr++ - '0');
    }
    
    /* Parse headers */
    ptr = kstrstr(data, "\r\n") + 2;
    
    while (ptr < data + data_len && *ptr != '\r') {
        if (kstrncmp(ptr, "Content-Type:", 13) == 0) {
            ptr += 13;
            while (*ptr == ' ') ptr++;
            int i = 0;
            while (*ptr != '\r' && i < 63) {
                resp->content_type[i++] = *ptr++;
            }
            resp->content_type[i] = '\0';
        }
        else if (kstrncmp(ptr, "Content-Length:", 15) == 0) {
            ptr += 15;
            while (*ptr == ' ') ptr++;
            resp->content_length = 0;
            while (*ptr >= '0' && *ptr <= '9') {
                resp->content_length = resp->content_length * 10 + (*ptr++ - '0');
            }
        }
        
        /* Skip to next line */
        while (ptr < data + data_len && *ptr != '\n') ptr++;
        ptr++;
    }
    
    /* Find body */
    const char* body_start = kstrstr(data, "\r\n\r\n");
    if (body_start) {
        body_start += 4;
        resp->body = (char*)body_start;
        resp->body_length = data_len - (body_start - data);
    }
    
    return 1;
}

/* Perform HTTP GET */
int http_get(const char* url, HttpResponse* resp) {
    ParsedUrl parsed;
    if (!parse_url(url, &parsed)) {
        return 0;
    }
    
    /* Resolve hostname */
    unsigned int ip = dns_resolve(parsed.host);
    if (ip == 0) {
        kprint_color("[HTTP] DNS resolution failed\n", 0x0C);
        return 0;
    }
    
    /* Connect */
    int sock = tcp_connect(ip, parsed.port);
    if (sock < 0) {
        kprint_color("[HTTP] Connection failed\n", 0x0C);
        return 0;
    }
    
    /* Send request */
    char request[512];
    int req_len = build_request(request, sizeof(request), "GET", &parsed);
    tcp_send(sock, request, req_len);
    
    /* Receive response */
    static char response_buf[MAX_RESPONSE_SIZE];
    int total = 0;
    int received;
    
    while ((received = tcp_recv(sock, &response_buf[total], MAX_RESPONSE_SIZE - total)) > 0) {
        total += received;
    }
    response_buf[total] = '\0';
    
    tcp_close(sock);
    
    /* Parse response */
    return parse_response(response_buf, total, resp);
}

#endif /* FRAY_HTTP_C */
