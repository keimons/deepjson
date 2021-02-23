open module deepjson {
	requires java.base;
	requires java.sql;
	requires java.compiler; // 编译java代码
	requires jdk.unsupported;
	requires fastjson;
	exports com.keimons.deepjson;
}