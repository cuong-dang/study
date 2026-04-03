#include "../lib/csapp.h"

static sem_t mutex;
static char **copy_array(char **src);
static int get_array_length(char **a);

static void init(void)
{
    Sem_init(&mutex, 0, 1);
}

struct hostent *gethostbyname_ts(const char *name)
{
    struct hostent *h;
    struct hostent *hh;
    static pthread_once_t once = PTHREAD_ONCE_INIT;
    int n_aliases, i, len;
    char *s;

    Pthread_once(&once, init);
    hh = (struct hostent *)Malloc(sizeof(struct hostent));
    P(&mutex);
    if (!(h = gethostbyname(name)))
        return NULL;
    /* h_name */
    hh->h_name = (char *)Malloc(strlen(h->h_name));
    memcpy(hh->h_name, h->h_name, strlen(h->h_name));
    /* h_aliases */
    hh->h_aliases = copy_array(h->h_aliases);
    /* h_addrtype */
    hh->h_addrtype = h->h_addrtype;
    /* h_length */
    hh->h_length = h->h_length;
    /* h_addr_list */
    hh->h_addr_list = copy_array(h->h_addr_list);
    V(&mutex);
    return hh;
}

static char **copy_array(char **src)
{
    int n, i, len;
    char **dest;

    n = get_array_length(src);
    dest = (char **)Malloc(sizeof(char *) * n);
    for (i = 0; src[i]; ++i) {
        len = strlen(src[i]);
        dest[i] = (char *)Malloc(len + 1);
        memcpy(dest[i], src[i], len + 1);
        dest[i][len] = '\0';
    }
    dest[i] = NULL;
    return dest;
}

int main(int argc, char **argv)
{
    struct hostent *h = gethostbyname_ts(argv[1]);
    char buf[INET_ADDRSTRLEN];
    char **p;

    if (!h) {
        printf("error: host not found\n");
        return 1;
    }
    printf("name: %s\n", h->h_name);
    for (p = h->h_aliases; *p; ++p)
        printf("alias: %s\n", *p);
    for (p = h->h_addr_list; *p; ++p)
        if (h->h_addrtype == AF_INET) {
            inet_ntop(AF_INET, *p, buf, INET6_ADDRSTRLEN);
            printf("addr: %s\n", buf);
        }
    return 0;
}

static int get_array_length(char **a)
{
    int n;

    for (n = 1; *a; ++a)
        ++n;
    return n;
}
