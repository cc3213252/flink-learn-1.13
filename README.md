# flink v1.13练习

java版本  
[参考资料](https://www.bilibili.com/video/BV133411s7Sa)  

## 创建工程

1、创建主工程，用Maven创建    

2、右键创建maven子工程

3、在另外单独的目录执行

```bash
mvn archetype:generate                               \
  -DarchetypeGroupId=org.apache.flink              \
  -DarchetypeArtifactId=flink-quickstart-java      \
  -DarchetypeVersion=1.13.0
```
4、把需要的pom文件内容拷贝到创建的maven工程来

5、在Run/Debug Configuation里面，把Include Dependencies with Provided scope打上勾

6、注意pom中transformers，需要改一个入口类

## 打包测试

mvn clean package  
在web页面提交tar文件即可测试  
也可以在右侧maven双击package  