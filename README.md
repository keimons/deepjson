
## 项目背景

DeepJson致力于节省序列化和反序列化时的内存占用。

```
+------------------------------------+
| +-------+---------+ +------------+ |
| |       |         | |            | |
| |       | context | | transcoder | |
| |       |         | |            | |
| | codec +---------+ |------------| |
| |       |         | |            | |
| |       |   buf   | | converter  | |
| |       |         | |            | |
| +-------+---------+ +------------+ |
+------------------------------------+
```

使用编解码器串联`context`和`buffer`，使用`transcoder`生成最后数据。

此库的目的：

1. 支持复合缓冲区以节省内存和不必要的拷贝。
2. 支持内存敏感型的缓存系统，根据内存占用动态拓展内存。
3. 支持对象循环引用（包括键）。
4. 支持模块化中未开放的模块。
5. 对于泛型的解析，尽可能的是在解析时暴露异常，而不是运行时。

## 安装

#### Maven

```
<dependency>
    <groupId>com.keimons.deepjson</groupId>
    <artifactId>deepjson</artifactId>
    <version>0.1.2</version>
</dependency>
```

#### Gradle

```
compile 'com.keimons.deepjson:deepjson:0.1.2'
```

## 使用

##### 序列化

`String json = DeepJson.toJsonString(obj);`

##### 反序列化

`T result = DeepJson.parseObject(context, T.class);`

##### 自定义序列化

使用指定的上下文环境、缓冲区、写入策略对对象进行编码。

`DeepJson.encode(obj, context, buffer, writer, options)`

## License

[Apache 2.0](LICENSE) 