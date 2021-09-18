package com.keimons.deepjson;

/**
 * 运行模式
 * <p>
 * 根据不同的运行环境选用不同的运行模式。当{@link #CHAR}和{@link #SAFE}不可用时，
 * 自动切换至{@link #SAFE}模式。
 * <ul>
 *     <li>{@link #CHAR} 适用于java 1.6-1.8，内部采用{@code char[]}编码。</li>
 *     <li>{@link #BYTE} 适用于java 9+，内部采用{@code char[]}编码。</li>
 *     <li>{@link #SAFE} 不关心内部编码，但是最浪费内存。</li>
 * </ul>
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public enum RunMode {

	BYTE, CHAR, SAFE
}