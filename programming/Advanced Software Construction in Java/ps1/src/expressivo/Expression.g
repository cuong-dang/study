/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */

// grammar Expression;
root ::= sum;

@skip whitespace{
	sum ::= term ('+' term)*;
	term ::= product | primitive;
	product ::= primitive ('*' primitive)*;
	primitive ::= integer | double | variable | '(' sum ')';
}

integer ::= [0-9]+;
double ::= [0-9]*'.'[0-9]+;
variable ::= [a-zA-Z]+;
whitespace ::= [ ]+;
