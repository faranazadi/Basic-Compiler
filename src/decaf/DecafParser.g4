/*
 * A VERY minimal skeleton for your parser, provIDed by Emma Norling.
 *
 * Your parser should use the tokens provided by your lexer in rules.
 * Even if your lexer appeared to be working perfectly for stage 1,
 * you might need to adjust some of those rules when you implement
 * your parser.
 *
 * Remember to provide documentation too (including replacing this
 * documentation).
 *
 */
parser grammar DecafParser;
options { tokenVocab = DecafLexer; }

// This rule says that a program consists of the tokens CLASS ID LCURLY RCURLY EOF nothing more nothing less,
// in exactly that order. However obviously something (quite a lot of something) needs to go between the curly
// brackets. You need to write the rules (based on the provided grammar) to capture this.

program: CLASS ID LCURLY field_decl* method_decl* RCURLY EOF;
field_name: ID | ID LSQUARE INT_LITERAL RSQUARE;
field_decl: type field_name (COMMA field_name)* SEMICOLON;
method_decl: (type | VOID) ID LBRACKET ((type ID)(COMMA type ID)*)? RBRACKET block;
block: LCURLY var_decl* statement* RCURLY;
var_decl: type ID (COMMA ID)* SEMICOLON;
type: INT | BOOLEAN;
statement: 	location assign_op expr SEMICOLON
		|	method_call SEMICOLON
		|	IF LBRACKET expr RBRACKET block (ELSE block)?
		|	FOR ID ASSIGN expr COMMA expr block
		|	RETURN (expr)? SEMICOLON
		|	BREAK SEMICOLON
		|	CONTINUE SEMICOLON
		|	block;
assign_op: 	ASSIGN
		|	ADDAND
		|	SUBTRACTAND;
method_call:	method_name LBRACKET (expr (COMMA expr)*)? RBRACKET
			|	CALLOUT LBRACKET STRING_LITERAL (COMMA callout_arg (COMMA callout_arg)*)? RBRACKET;
method_name: ID;
location: 	ID
		|	ID LSQUARE expr RSQUARE;
expr:	location
	|	method_call
	|	literal
	|	expr bin_op expr
	|	SUBTRACTION expr
	|	NOT expr
	|	LBRACKET expr RBRACKET;
callout_arg: expr | STRING_LITERAL;
bin_op: arith_op | rel_op | eq_op | cond_op;
arith_op: ADDITION | SUBTRACTION | MULTIPLICATION | DIVISION | MODULUS;
rel_op: GREATERTHAN | LESSTHAN | GREATEROREQUAL | LESSOREQUAL;
eq_op: EQUALSTO | NOTEQUALTO;
cond_op: AND | OR;
literal: INT_LITERAL | CHAR_LITERAL | bool_literal;
bool_literal: TRUE | FALSE;