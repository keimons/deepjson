module deepjson {
	requires com.google.common;
	requires io.netty.all;
	requires java.compiler;
	requires java.management;
	requires jdk.unsupported;
	requires org.jetbrains.annotations;
	requires jdk.management;

	exports com.keimons.deepjson;
	exports com.keimons.deepjson.util;
	exports com.keimons.deepjson.internal.monitor to deepjson.test;
	exports com.keimons.deepjson.internal.util to deepjson.test;
}