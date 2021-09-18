module deepjson {
	requires java.compiler;
	requires org.jetbrains.annotations;
	requires jdk.unsupported;
	requires com.google.common;

	exports com.keimons.deepjson;
	exports com.keimons.deepjson.util;
}