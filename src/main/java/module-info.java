open module deepjson {
	requires java.compiler; // 编译java代码
	requires jdk.unsupported;
	exports com.keimons.deepjson.filler;
	exports com.keimons.deepjson.serializer;
	exports com.keimons.deepjson.compiler;
}