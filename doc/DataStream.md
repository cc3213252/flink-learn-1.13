## 执行环境

创建环境时有两种模式： STREAMING、BATCH。默认是STREAMING，只有当希望输出的是一个数时，才需要通过  
setRuntimeMode设置BATCH（不推荐）。

或者可以用命令行方式：  
bin/flink run -Dexecution.runtime-mode=BATCH

## 源算子

command + n自动生成构造函数  
双击shift查找  

数组范围内随机的方法： String user = users[random.nextInt(users.length)];  

### Flink支持的数据类型

POJO类，简单java对象，类似java bean的类  
Flink对POJO类的要求：   
1、类是公共的，都是静态的  
2、有公共无参构造方法  
3、非final  
一般外层定义元祖，内部再定义pojo或其他  

## 转换算子


## 问题

SourceCustomTest会碰到跑几个数据后报错，应用数据源不稳定，网上解答是要设置checkpoint，重试几次都失败才算失败等策略  
