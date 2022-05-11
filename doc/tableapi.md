## dashboard

使用TableEnvironment输出到csv的一个例子：  CommonApiTest  
使用TableEnvironment实现控制台打印： CommonApiTest  
使用TableEnvironment实现聚合查询： CommonApiTest，这个例子比DataStream方式实现简单多了  

## 引入包

flink-table-api-java-bridge_${scala.binary.version}  负责table api和下层datastream api的连接支持  

### 本地ide运行table api和sql

## 程序

result> +I[Mary, ./cart]   I, insert  
eventTable.select($("user"), $("url"))  $表达式引入  
SimpleTableExample， table api和sql两种方式入门，较好  

3> -U[Mary, 1]  U, update -表示更新前的 +表示更新后的
5> +U[Bob, 2]

## 创建表的方式

连接器表  
虚拟表

## 碰到问题

Could not find any format factory for identifier 'csv' in the classpath  
需要引入flink-csv依赖  

## 表和流转换

表转换成流   tableEnv.toDataStream
直接转换会报错，用toChangelogStream，例子SimpleTableExample  

仅插入流而已，可以直接用toDataStream  
对于有聚合操作的，要用toChangelogStream  

流转换成表  
  1. tableEnv.fromDataStream  
  2. 调用createTemporaryView方法  

动态表转换为流

## 动态查询写法

```sql
SELECT 
    user,
    window_end AS endT,
    COUNT(url) AS cnt
FROM TABLE(
    TUMBLE(
        TABLE EventTable,
        DESCRIPTOR(ts), 
        INTERVAL '1' HOUR))
GROUP BY user,
         window_start,
         window_end
```

## 流处理中的表

Table API和SQL支持三种编码方式：  
  1. 仅追加流 Append-only
  2. 撤回流 Retract