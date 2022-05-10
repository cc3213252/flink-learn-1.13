## dashboard

使用TableEnvironment输出到csv的一个例子：  CommonApiTest

## 引入包

flink-table-api-java-bridge_${scala.binary.version}  负责table api和下层datastream api的连接支持  

### 本地ide运行table api和sql

## 程序

result> +I[Mary, ./cart]   I, insert  
eventTable.select($("user"), $("url"))  $表达式引入  
SimpleTableExample， table api和sql两种方式入门，较好  

## 创建表的方式

连接器表  
虚拟表

## 碰到问题

Could not find any format factory for identifier 'csv' in the classpath  
需要引入flink-csv依赖  
