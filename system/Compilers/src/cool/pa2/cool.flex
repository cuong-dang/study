/*
 *  The scanner definition for COOL.
 */
%{
#include <cool-parse.h>
#include <stringtab.h>
#include <utilities.h>

/* The compiler assumes these identifiers. */
#define yylval cool_yylval
#define yylex  cool_yylex

/* Max size of string constants */
#define MAX_STR_CONST 1025
#define YY_NO_UNPUT   /* keep g++ happy */

extern FILE *fin; /* we read from this file */

/* define YY_INPUT so we read from the FILE fin:
 * This change makes it possible to use this scanner in
 * the Cool compiler.
 */
#undef YY_INPUT
#define YY_INPUT(buf,result,max_size) \
	if ( (result = fread( (char*)buf, sizeof(char), max_size, fin)) < 0) \
		YY_FATAL_ERROR( "read() in flex scanner failed");

char string_buf[MAX_STR_CONST]; /* to assemble string constants */
char *string_buf_ptr;

extern int curr_lineno;
extern int verbose_flag;

extern YYSTYPE cool_yylval;

int string_has_error = 0;
int comment_nested_lvl = 0;
%}

DIGIT           [0-9]
ID              [a-z][a-zA-Z0-9_]*
TYPE            [A-Z][a-zA-Z0-9_]*

%x str comment

%%
  /* Whitespaces */
\n {
  curr_lineno++;
}

[ \f\r\t\v]+   {}

  /* Comments */
--.* {}

\(\* {
  comment_nested_lvl++;
  BEGIN(comment);
}

<comment>\(\* {
  comment_nested_lvl++;
}

<comment>\*\) {
  if (--comment_nested_lvl == 0) {
    BEGIN(INITIAL);
  }
}

<comment>[^(*\n]+ {}
<comment>\(       {}
<comment>\*       {}

<comment>\n {
  curr_lineno++;
}

<comment><<EOF>> {
  cool_yylval.error_msg = "EOF in comment";
  BEGIN(INITIAL);
  return ERROR;
}

\*\) {
  cool_yylval.error_msg = "Unmatched *)";
  return ERROR;
}

  /* Single-char tokens */
[+\-*/<={}();:,.@~] { return yytext[0]; }

  /* Multi-char operators */
=> { return DARROW; }
\<- { return ASSIGN; }
\<= { return LE; }

  /* Keywords */
[cC][lL][aA][sS][sS] { return CLASS; }
[eE][lL][sS][eE] { return ELSE; }
[fF][iI] { return FI; }
[iI][fF] { return IF; }
[iI][nN] { return IN; }
[iI][nN][hH][eE][rR][iI][tT][sS] { return INHERITS; }
[iI][sS][vV][oO][iI][dD] { return ISVOID; }
[lL][eE][tT] { return LET; }
[lL][oO][oO][pP] { return LOOP; }
[pP][oO][oO][lL] { return POOL; }
[tT][hH][eE][nN] { return THEN; }
[wW][hH][iI][lL][eE] { return WHILE; }
[cC][aA][sS][eE] { return CASE; }
[eE][sS][aA][cC] { return ESAC; }
[nN][eE][wW] { return NEW; }
[oO][fF] { return OF; }
[nN][oO][tT] { return NOT; }

t[rR][uU][eE] {
  cool_yylval.boolean = 1;
  return BOOL_CONST;
}

f[aA][lL][sS][eE] {
  cool_yylval.boolean = 0;
  return BOOL_CONST;
}

  /* Integers */
{DIGIT}+ {
  cool_yylval.symbol = inttable.add_string(yytext);
  return INT_CONST;
}

  /* Identifiers */
{ID} {
  cool_yylval.symbol = inttable.add_string(yytext);
  return OBJECTID;
}

{TYPE} {
  cool_yylval.symbol = inttable.add_string(yytext);
  return TYPEID;
}

  /* Strings */
\" {
  string_has_error = 0;
  string_buf[0] = '\0';
  BEGIN(str);
}

<str>([^"\n\\\0]|\\\n|\\.)+ {
  /* processing \* and copy into string_buff */
  size_t len = yyleng, i = 0, j = 0;
  while (i < len) {
    if (j >= MAX_STR_CONST-1) {
      cool_yylval.error_msg = "String constant too long";
      string_has_error = 1;
      break;
    }
    if (yytext[i] == '\\' && i < len-1) {
      switch (yytext[i+1]) {
        case 'b': string_buf[j++] = '\b'; break;
        case 't': string_buf[j++] = '\t'; break;
        case 'n': string_buf[j++] = '\n'; break;
        case 'f': string_buf[j++] = '\f'; break;
        case '\n': string_buf[j++] = '\n'; curr_lineno++; break;
        case '\0': {
          cool_yylval.error_msg = "String contains escaped null character.";
          string_has_error = 1;
          break;
        }
        default: string_buf[j++] = yytext[i+1]; break;
      }
      i += 2;
    } else {
      string_buf[j++] = yytext[i++];
    }
  }
  string_buf[j] = '\0';
}

<str>\" {
  BEGIN(INITIAL);
  if (string_has_error) {
    return ERROR;
  }
  cool_yylval.symbol = inttable.add_string(string_buf);
  return STR_CONST;
}

<str>\0 {
  cool_yylval.error_msg = "String contains null character";
  string_has_error = 1;
}

<str>\n {
  curr_lineno++;
  cool_yylval.error_msg = "Unterminated string constant";
  BEGIN(INITIAL);
  return ERROR;
}

<str><<EOF>> {
  cool_yylval.error_msg = "EOF in string constant";
  BEGIN(INITIAL);
  return ERROR;
}

<<EOF>> { return 0; }

  /* Unrecognized */
. {
  cool_yylval.error_msg = yytext;
  return ERROR;
}

%%

#undef yylex

extern "C" int yylex(void) {
    return cool_yylex();
}
