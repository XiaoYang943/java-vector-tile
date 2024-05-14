## 什么是Protocol Buffers
- Protocol Buffers(PBF)是一种与编程语言无关、与平台架构无关、可扩展性强的**序列化数据**机制
## PBF的优点(https://protobuf.dev/overview/#benefits)
### 跨语言兼容性(https://protobuf.dev/overview/#cross-lang)
- 支持多种语言
### 跨平台支持(https://protobuf.dev/overview/#cross-proj)
- 可生成不同语言的代码并在任何环境中运行
### 性能好、扩展性好
- 序列化和反序列化的性能比JSON和XML更快
## PBF的缺点
### 什么情况下不适用PBF(https://protobuf.dev/overview/#not-good-fit)
## PBF是如何工作的(https://protobuf.dev/overview/#work)
### 1. 先定义.proto数据结构
```protobuf
message Person {
  optional string name = 1;
  optional int32 id = 2;
  optional string email = 3;
}
```
### 2. 编译.proto
- 编译`.proto`将创建一个类，可以使用该类来创建新实例
```
Person john = Person.newBuilder()
    .setId(1234)
    .setName("John Doe")
    .setEmail("jdoe@example.com")
    .build();
output = new FileOutputStream(args[0]);
john.writeTo(output);
```
## PBF的数据结构(https://protobuf.dev/overview/#syntax)
### 关键字
- message：代表实体结构，由多个消息字段field组成
- required：表示该字段是必需的 
- optional：表示该字段是可选的 
- repeated：表示该字段的值是一个列表，可以包含0个或多个元素 
- group：用于组织多个字段，但目前该关键字已被废弃，应使用message 
- syntax：用于指定协议版本号，没有指定则默认为proto2版本 
- package：相等于C++中的命名空间，为了防止名称冲突 
- import：引入其他的proto文件，可以使用其他proto文件中定义的消息类型 
- message：用于定义消息类型，相当于C++中的struct 
- enum：用于定义枚举类型，相当于C++中的enum 
- option：用于设置消息字段的选项，如default、packed等 
- extensions：用于定义扩展消息字段，可以在现有消息类型的基础上添加新的字段 
- rpc：用于定义远程过程调用（RPC）服务 
- service：用于定义服务接口 
- default ：用于为字段设置默认值 
- packed ：用于指示字段值应该进行压缩存储 
- max ：用于指定枚举类型的最大值 
- value ：用于指定枚举类型的值 
- returns ：用于指定RPC服务的返回类型
