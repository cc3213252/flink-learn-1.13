没有yarn，资源不够用了，需要自己去扩充

## 部署模式

三种模式不同主要在于生命周期不同  

Session，集群生命周期超越作业生命周期，资源是启动时固定的  
适合单个规模小，执行时间短的大量作业，因不用分配资源启动集群了  

per-job，单作业模式，把资源和作业隔离开，每提交一个作业就启动一个集群 
客户端运行应用程序，启动集群，作业被提交给JobManager，分发给TaskManager，作业完成，集群关闭，资源释放。  
必须要借助外部资源管理平台，是实际应用的首选模式

Application，应用模式，为了节省资源，不用客户端，直接在JobManager上提交作业

单作业是作业跟集群一对一，应用是应用跟集群一对一

## 独立模式standalone

不使用外部资源管理平台  
standalone下一般使用会话模式，无法使用单作业，应用模式太复杂    

用应用模式提交作业  
cp ./wordcount-1.0-SNAPSHOT.jar lib/
./bin/standalone-job.sh start --job-classname com.blueegg.wc.StreamWordCount
./bin/taskmanager.sh start  
./bin/standalone-job.sh stop
./bin/taskmanager.sh stop

## yarn模式

### yarn session模式

先要启动session服务  
flink 1.11之前，yarn支持的话，需要把官网hadoop jar包下载到lib目录下，之后就只需要环境变量配置即可  
yarn模式下提交作业不需要-m了：  
scp wordcount-1.0-SNAPSHOT.jar vagrant@host1:/vagrant/tools/flink/  
vagrant ssh host1  
cd /vagrant/tools/flink/  
nc -lk 7777  
./bin/flink run -c com.blueegg.wc.StreamWordCount -p 1 ./wordcount-1.0-SNAPSHOT.jar --host host1 --port 7777