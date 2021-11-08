# deepjson

## 快速开始

##### 编码

`String json = DeepJson.toJsonString(obj);`

##### 高级编码

使用指定的上下文环境、缓冲区、写入策略对对象进行编码。

`DeepJson.encode(obj, context, buffer, writer, options)`

##### 解码

`T result = DeepJson.parseObject(context, T.class);`

## 功能优势

* `Composite Buffer`and`zero copy`
* Custom codec

写入时采用复合缓冲区。

编码方案：

```
+-----------------------------------+
| +-------+---------+ +-----------+ |
| |       |         | |           | |
| |       | context | |           | |
| |       |         | |           | |
| | codec +---------+ | generator | |
| |       |         | |           | |
| |       |   buf   | |           | |
| |       |         | |           | |
| +-------+---------+ +-----------+ |
+-----------------------------------+
```

使用编解码器串联`context`和`buffer`。

序列化过程：

1. 查找线程本地缓存的编解码方案；
2. 查找或生成编解码器；
3. 深度优先算法扫描所有对象，并生成写入树；
4. 按照顺序写入对象；
5. 查找写入策略，并生成最终对象。

End