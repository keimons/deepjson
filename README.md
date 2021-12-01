## 项目背景

DeepJson是一个Java对象和Json相互转化的工具库。序列化时，利用复合缓冲区实现Zero Copy，引入内存敏感型的缓存方案对缓冲区进行缓存，采用深度优先搜索算法二次构建，解决`key`的循环引用问题。反序列化时，缓冲区比对技术，以节省内存。

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

此库特性：

1. 支持复合缓冲区以节省内存和不必要的拷贝。
2. 支持内存敏感型的缓存系统，根据内存占用动态拓展缓存。
3. 支持对象循环引用（包括键）。
4. 支持模块化中未开放的模块。
5. 添加虚拟序列化，对于泛型的解析，尽可能在解析时暴露异常，而不是运行时。

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