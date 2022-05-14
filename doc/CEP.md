CEP: Complex Event Processing，复杂事件处理

应用场景：
1、风险控制  
2、用户画像  
3、运维监控，灵活配置各种规则  

cep默认不包含，集群环境运行，需要把cep的jar包放到lib目录下

## 模式api

### 个体模式

个体模式量词：  
pattern.times(4)  匹配事件出现4次  
pattern.times(4).optional()  匹配4次或者不出现  
pattern.times(2, 4).optional()  匹配2-4次或不出现
pattern.times(2, 4).greedy()  匹配2-4次，并且尽可能多地匹配  
pattern.oneOrMore() 1次或多次
pattern.timesOrMore()  2次或多次  

个体模式条件：  
简单条件、迭代条件、复合条件、终止条件  
终止条件： .until()，只与oneOrMore()结合使用  

### 组合模式

pattern.begin().where().next().where().followedBy().where()

1、初始模式

严格近邻、宽松近邻（中间可以有间隔）、非确定性宽松近邻（重复出现过的都可以）

notNext 一个匹配事件后不能紧跟某种事件
notFollowedBy() 两个事件之间不会出现某种事件

严格近邻：  .next  
宽松近邻：  .followedBy
非确定性宽松近邻：  .followedByAny  .allowCombinations
不能宽松近邻：  .notNext
时间限制条件： .within

默认是宽松近邻，加.consecutive变严格近邻

### 模式组

### 匹配后跳过策略

跳过冗余的匹配  
