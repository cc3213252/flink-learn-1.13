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

public class TimeAndWindowTest2 {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 1 在创建表的DDL中直接定义时间属性
        // FROM_UNIXTIME 长整型转string
        String createDDL = "CREATE TABLE clickTable (" +
                " user_name STRING, " +
                " url STRING, " +
                " ts BIGINT, " +
                " et AS TO_TIMESTAMP( FROM_UNIXTIME(ts / 1000) ), " +
                " WATERMARK FOR et AS et - INTERVAL '1' SECOND " +
                ") WITH (" +
                " 'connector' = 'filesystem', " +
                " 'path' = 'input/clicks.txt'," +
                " 'format' = 'csv'" +
                ")";
        tableEnv.executeSql(createDDL);

        // 2 在流转换成Table的时候定义时间属性
        SingleOutputStreamOperator<Event> clickStream = env.addSource(new ClickSource())
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO)
                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                            @Override
                            public long extractTimestamp(Event element, long recordTimestamp) {
                                return element.timestamp;
                            }
                        }));

        Table clickTable = tableEnv.fromDataStream(clickStream, $("user"), $("url"), $("timestamp"),
                $("et").rowtime());

        // 聚合查询转换
        // 1. 分组聚合
        Table aggTable = tableEnv.sqlQuery("select user_name, count(1) from clickTable group by user_name");

        // 2. 分组窗口聚合， 滚动不会造成更新，可以只用toDataStream
        Table groupWindowResultTable = tableEnv.sqlQuery("select " +
                " user_name, count(1) as cnt, " +
                " TUMBLE_END(et, INTERVAL '10' SECOND) AS entT" +
                " from clickTable " +
                " group by " +
                " user_name, " +
                " TUMBLE(et, INTERVAL '10' SECOND)"
        );

        // 3. 窗口聚合
        // 3.1 滚动窗口  分组聚合窗口用窗口TVF重新实现
        Table tumbleWindowResultTable = tableEnv.sqlQuery("select user_name, COUNT(1) as cnt, " +
                " window_end as endT " +
                "from TABLE(" +
                "  TUMBLE(TABLE clickTable, DESCRIPTOR(et), INTERVAL '10' SECOND)" +
                ")" +
                "GROUP BY user_name, window_end, window_start"
        );

        // 3.2 滑动窗口
        Table hopWindowResultTable = tableEnv.sqlQuery("select user_name, COUNT(1) as cnt, " +
                " window_end as endT " +
                "from TABLE(" +
                "  HOP(TABLE clickTable, DESCRIPTOR(et), INTERVAL '5' SECOND, INTERVAL '10' SECOND)" +
                ")" +
                "GROUP BY user_name, window_end, window_start"
        );

        // 3.3 累积窗口
        Table cumulateWindowResultTable = tableEnv.sqlQuery("select user_name, COUNT(1) as cnt, " +
                " window_end as endT " +
                "from TABLE(" +
                "  CUMULATE(TABLE clickTable, DESCRIPTOR(et), INTERVAL '5' SECOND, INTERVAL '10' SECOND)" +
                ")" +
                "GROUP BY user_name, window_end, window_start"
        );

        // 4. 开窗聚合 Over
        Table overWindowResultTable = tableEnv.sqlQuery("select user_name, " +
                " avg(ts) OVER(" +
                "  PARTITION BY user_name " +
                " ORDER BY et " +
                " ROWS BETWEEN 3 PRECEDING AND CURRENT ROW" +
                ") AS avg_ts " +
                "FROM clickTable"
        );
//        clickTable.printSchema();
//        tableEnv.toChangelogStream(aggTable).print("agg");
//        tableEnv.toDataStream(groupWindowResultTable).print("group window");
//        tableEnv.toDataStream(tumbleWindowResultTable).print("tumble window");
//        tableEnv.toDataStream(hopWindowResultTable).print("hop window");
//        tableEnv.toDataStream(cumulateWindowResultTable).print("cumulate window");
        tableEnv.toDataStream(overWindowResultTable).print("over window");
        env.execute();
    }
}
