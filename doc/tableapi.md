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

Collector引入错误，应引入：
import org.apache.flink.util.Collector;

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
  3. 更新插入流 Upsert，主要看连接器是否支持  

## 时间属性和窗口

proctime 处理时间  
sql中滑动窗口历史原因叫HOP  

定义一个滚动时间窗口： TUMBLE(ts, INTERVAL '1' HOUR)
滑动时间窗口： HOP(TABLE EventTable, DESCRIPTOR(ts), INTERVAL '5' MINUTES, INTERVAL '1' HOURS)
累积窗口： CUMULATE(TABLE EventTable, DESCRIPTOR(ts), INTERVAL '1' HOURS, INTERVAL '1' DAYS)

1、分组窗口，弃用  
2、窗口表值函数 TVFs，flink提供了四种TVFs：
  1. 滚动窗口
  2. 滑动窗口
  3. 累积窗口
  4. 会话窗口，尚未完全支持

## 聚合查询

### 分组聚合

内置函数实现，SUM(), MIN(), MAX(), AVG(), COUNT()  
典型用法： SELECT user, COUNT(url) as cnt FROM EventTable GROUP BY user

为了防止状态无限增长耗尽资源，可以配置状态的生存时间TTL，如：
TableConfig tableConfig = tableEnv.getConfig();
tableConfig.setIdleStateRetention(Duration.ofMiniutes(60));

流要打印输出，要加env.execute()  

### 窗口聚合

分组聚合窗口用窗口TVF重新实现，相互对比较好实验： TimeAndWindowTest2  

### 开窗聚合

可以针对每一行计算一个聚合值

### 应用实例--TopN

## 联结查询

## 函数（会查资料）

flink sql系统函数分两类：   
标量函数（Scalar Functions）， 只有数值没有方向  
  1. 比较函数
  2. 逻辑函数
  3. 算术函数
  4. 字符串函数
  5. 时间函数
聚合函数（Aggregate functions）  

自定义函数：  
  1. 标量函数
  2. 表函数，多行数据  
  3. 聚合函数
  4. 表聚合函数，多行数据  

## SQL客户端

设置流处理模式  
set 'execution.runtime-mode'='streaming';

设置执行结果模式：  
set 'sql-client.execution.result-mode'='table'; 

table:表处理模式
tableau：可视化表模式

## 连接到外部系统

连接es时，必须要定义主键，才能以Upsert模式写数据  
Flink  SQL提供的连接器： kafka，文件系统，jdbc，es, hbase，hive  
hive连接器只支持用flink sql  

hive的sql跟标准sql不一样，叫sql的方言dialect，可以直接设置就可使用标准sql：  
set table.sql-dialect=hive;

或者配置文件sql-cli-defaults.yaml中：  
configuration:
  table.sql-dialect: hive

或者代码中：  
tableEnv.getConfig().setSqlDialect(SqlDialect.HIVE);