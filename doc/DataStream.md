## 执行环境

创建环境时有两种模式： STREAMING、BATCH。默认是STREAMING，只有当希望输出的是一个数时，才需要通过  
setRuntimeMode设置BATCH（不推荐）。

或者可以用命令行方式：  
bin/flink run -Dexecution.runtime-mode=BATCH

## 源算子

command + n自动生成构造函数  
双击shift查找  
ctrl + shift + R 当前文件运行程序  
当类返回值发生变化，需要重新生成实现方法时，把要重新生成的函数删掉，ctrl + i  
ctrl + o 覆盖方法（重写父类方法）
command + 7 显示类方法  
command + 空格快速打开应用  
ctx.timestamp().sout 快捷生成打印  

数组范围内随机的方法： String user = users[random.nextInt(users.length)];  

### Flink支持的数据类型

POJO类，简单java对象，类似java bean的类  
Flink对POJO类的要求：   
1、类是公共的，都是静态的  
2、有公共无参构造方法  
3、非final  
一般外层定义元祖，内部再定义pojo或其他  

## 转换算子

map: 分别作用每个元素，比如组合成新字段等  
filter: 过滤  
flatMap: 一个拆成多个，加强版的map加filter    
returns(new TypeHint<String>() {}) 用TypeHint可以自动填充最简单  

## 聚合算子

max: 数据会窜  
maxBy: 会保留完整的一条  
TransformReduceTest, 归约聚合，reduce 累加    

## 用户自定义函数UDF

rich Function有生命周期概念，open开始，close结束  
因为他函数不止一个，所以不适合用lamdba实现  

数据倾斜的场景：如果按名字keyBy，有的非常忙，有的很闲，资源就分配不均
如何解决：
1、对key优化，尽可能分配均匀  
2、指定物理分区，指定数据到哪个分区  

## 物理分区

shuffle， 随机分区，均匀分区  
rebalance, 轮询分区，是默认的    
rescale，重缩放分区，与轮询的区别是针对组内轮询，建立的连接通道少了，网络效率高了    
broadcast，广播分区，一对多  
global，全局分区，并行度没用了，全部都合并到一个分区  
自定义分区，数据优化时用  

rescale例子说明：source分成两个区，按奇数偶数区分分到不同区  

## 输出算子

### kafka实验

kafka-console-producer.sh --broker-list localhost:9092 --topic clicks
kafka-console-consumer.sh --bootstrap-server host1:9092 --topic events  
启动程序，输入：
Mary, ./home, 1000
Alice, ./cart, 2000
Bob, ./prod?id=100, 3000

### redis实验

hgetall clicks  

### mysql实验

CREATE DATABASE IF NOT EXISTS sink_test DEFAULT CHARSET utf8 COLLATE utf8_general_ci;  
create table clicks(user varchar(20) not null, url varchar(100) not null);  

## 问题

【ok代码问题】SourceCustomTest会碰到跑几个数据后报错，应用数据源不稳定，网上解答是要设置checkpoint，重试几次都失败才算失败等策略  
【ok】Redis实验也碰到应用数据不稳定问题，Recovery is suppressed by NoRestartBackoffTimeStrategy  