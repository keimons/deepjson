module deepjson.test {
	requires deepjson;
	requires org.junit.jupiter.api;
	exports com.keimons.deepjson.test.codec;
	exports com.keimons.deepjson.test.codec.adder;
	exports com.keimons.deepjson.test.codec.array;
	exports com.keimons.deepjson.test.codec.atomic;
	exports com.keimons.deepjson.test.codec.clazz;
	exports com.keimons.deepjson.test.codec.collection;
	exports com.keimons.deepjson.test.codec.date;
	exports com.keimons.deepjson.test.map;
	exports com.keimons.deepjson.test.object;
	exports com.keimons.deepjson.test.util;
}