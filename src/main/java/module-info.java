module deepjson {
	requires java.compiler; // 编译java代码
	requires jdk.unsupported;
	requires guava;
	exports com.keimons.deepjson;
}