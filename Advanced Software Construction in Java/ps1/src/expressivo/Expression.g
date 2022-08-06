/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */

// grammar Expression;
root ::= sum | product;

@skip whitespace{
	sum ::= primitive ('+' primitive)*;
	product ::= primitive ('*' primitive)*;
	primitive ::= number | '(' sum ')' | '(' product ')';
}

number ::= [0-9]+;
whitespace ::= [ ]+;
