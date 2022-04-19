# wordcount

## 快捷键

psvm  创建main函数  
env.readTextFile("input/words.txt").var 自动联想返回类型

## fix issue

![NoClassDefFoundError](doc/error-msg.png)  
默认引入的pom不是compile，会报找不到路径的错误，最佳实践是设置Include Dependencies with Provided scope。  
但是idea新版本界面变化了，现在在： Run-Edit configuation-modify option-Include Dependencies with Provided scope
![设置Provided scope](doc/set-provided.png)  

找不到words.txt  
默认根路径是父目录，有子module情况下要加module路径  

提交到flink总报错  
groupId创建根项目时要倒着写  
 
input无法成功，不知道应该放哪里  
提交job多次报错，应从日志最后开始看起  
web ui里面提交参数方法：  --host=host1 --port=7777  

## 输出

5> (world,1)
3> (hello,1)
3> (hello,2)
2> (java,1)
7> (flink,1)
3> (hello,3)
5表示是本地第五个线程，有多少个取决于并行度，默认是cpu核心数，比如我8核，只会出现1到8  
同一个词会分配到同一个线程中  

## StreamWordCount

nc -lk 7777 保持当前连接，持续监听  

