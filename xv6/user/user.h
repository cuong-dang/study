struct stat;
struct rtcdate;

// system calls
char *sbrk(int);
int chdir(const char *);
int close(int);
int dup(int);
int exec(char *, char **);
int exit(int) __attribute__((noreturn));
int fork(void);
int fstat(int fd, struct stat *);
int getpid(void);
int kill(int);
int link(const char *, const char *);
int mkdir(const char *);
int mknod(const char *, short, short);
int open(const char *, int);
int pipe(int *);
int read(int, void *, int);
int sleep(int);
int unlink(const char *);
int uptime(void);
int wait(int *);
int write(int, const void *, int);

// ulib.c
int stat(const char *, struct stat *);
char *strcpy(char *, const char *);
void *memmove(void *, const void *, int);
char *strchr(const char *, char c);
int strcmp(const char *, const char *);
void fprintf(int, const char *, ...);
void printf(const char *, ...);
char *gets(char *, int max);
uint strlen(const char *);
void *memset(void *, int, uint);
void *malloc(uint);
void free(void *);
int atoi(const char *);
int memcmp(const void *, const void *, uint);
void *memcpy(void *, const void *, uint);

// syscall wrappers
int close_(int);
int fork_(void);
int pipe_(int *);
