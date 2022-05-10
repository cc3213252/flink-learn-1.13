package com.blueegg.chapter11;

import com.blueegg.chapter05.ClickSource;
import com.blueegg.chapter05.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.Duration;

import static org.apache.flink.table.api.Expressions.$;

public class SimpleTableExample {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        SingleOutputStreamOperator<Event> eventStream = env.addSource(new ClickSource())
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO)
                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                            @Override
                            public long extractTimestamp(Event element, long recordTimestamp) {
                                return element.timestamp;
                            }
                        }));

        // 2. 创建表执行环境
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 3. 将DataStream转换成Table
        Table eventTable = tableEnv.fromDataStream(eventStream);

        // 4. 直接写sql进行转换， 这种方式更好一点
        Table resultTable1 = tableEnv.sqlQuery("select user, url, `timestamp` from " + eventTable);

        // 5. 基于Table直接转换
        Table resultTable2 = eventTable.select($("user"), $("url"))
                .where($("user").isEqual("Alice"));

        // 6. 转换成流打印输出
        tableEnv.toDataStream(resultTable1).print("result1");
        tableEnv.toDataStream(resultTable2).print("result2");
        env.execute();
    }
}
