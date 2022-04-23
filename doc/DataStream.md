## 执行环境

创建环境时有两种模式： STREAMING、BATCH。默认是STREAMING，只有当希望输出的是一个数时，才需要通过  
setRuntimeMode设置BATCH（不推荐）。

或者可以用命令行方式：  
bin/flink run -Dexecution.runtime-mode=BATCH

## 源算子


