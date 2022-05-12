package com.blueegg.chapter11;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class TopNExample2 {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 1 在创建表的DDL中直接定义时间属性
        // FROM_UNIXTIME 长整型转string
        String createDDL = "create table clickTable (" +
                "    `user` string, " +
                "     url string, " +
                "     ts bigint, " +
                "     et as to_timestamp(from_unixtime(ts / 1000)), " +
                "     watermark for et as et - interval '1' second " +
                ") WITH (" +
                "     'connector' = 'filesystem', " +
                "     'path' = 'input/clicks.txt'," +
                "     'format' = 'csv'" +
                ")";
        tableEnv.executeSql(createDDL);

        // 窗口Top N， 统计一段时间内的（前2名）活跃用户
        String subQuery = "select user, count(url) as cnt, window_start, window_end " +
                "from table (tumble(table clickTable, descriptor(et), interval '10' second))" +
                "group by user, window_start, window_end";

        Table windowTopNResult = tableEnv.sqlQuery("select user, cnt, row_num, window_end " +
                "from (" +
                "  select *, row_number() over (partition by window_start, window_end order by cnt desc) AS row_num " +
                "  from ( " + subQuery + " )" +
                ") where row_num <= 2");

        tableEnv.toDataStream(windowTopNResult).print("window top n:");
        env.execute();
    }
}
