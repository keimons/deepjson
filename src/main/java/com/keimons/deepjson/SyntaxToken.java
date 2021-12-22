package com.keimons.deepjson;

/**
 * 语法中的关键值枚举
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public enum SyntaxToken {

	// {
	LBRACE(true),
	// }
	RBRACE(false),
	// [
	LBRACKET(true),
	// ]
	RBRACKET(false),
	// ,
	COMMA(false),
	// :
	COLON(false),
	// true
	TRUE(true),
	// false
	FALSE(true),
	// null
	NULL(true),
	// "string"
	STRING(true),
	NAN(true),
	Infinity(true),
	_Infinity(true),
	// 0-9
	NUMBER(true),
	// eof
	EOF(false),
	// error
	ERROR(false);

	public static final SyntaxToken[] OBJECTS = {
			LBRACE, LBRACKET, TRUE, FALSE, NULL, STRING, NUMBER
	};

	private final boolean object;

	SyntaxToken(boolean object) {
		this.object = object;
	}

	public boolean isObject() {
		return object;
	}
}