package com.keimons.deepjson.test.codec.date;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 抽象的数组类型
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.8
 **/
public class LocalDateTimeTest {

	@Test
	public void test() {
		LocalDateTime now = LocalDateTime.now();
		System.out.println(now.toString());
		System.out.println(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.nnnnnnnnn]");
		System.out.println(now.format(formatter));
		LocalDateTime.parse("2021-09-22 11:44:25", formatter);
	}
}