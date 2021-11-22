module deepjson {
	requires com.google.common;
	requires io.netty.all;
	requires java.compiler;
	requires jdk.unsupported;
	requires org.jetbrains.annotations;

	exports com.keimons.deepjson;
	exports com.keimons.deepjson.util;
	exports com.keimons.deepjson.monitor;
}