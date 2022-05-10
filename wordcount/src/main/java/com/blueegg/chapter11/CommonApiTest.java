package com.blueegg.chapter11;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static org.apache.flink.table.api.Expressions.$;

public class CommonApiTest {
    public static void main(String[] args) throws Exception {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);
//
//        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 1. 定义环境配置来创建表执行环境

        // 基于blink版本planner进行流处理
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .inStreamingMode()
                .useBlinkPlanner()
                .build();

        TableEnvironment tableEnv = TableEnvironment.create(settings);

//        // 1.1 基于老版本planner进行流处理
//        EnvironmentSettings settings1 = EnvironmentSettings.newInstance()
//                .inStreamingMode()
//                .useOldPlanner()
//                .build();
//
//        TableEnvironment tableEnv1 = TableEnvironment.create(settings1);
//
//        // 1.2 基于老版本planner进行批处理
//        ExecutionEnvironment batchEnv = ExecutionEnvironment.getExecutionEnvironment();
//        BatchTableEnvironment batchTableEnv = BatchTableEnvironment.create(batchEnv);
//
//        // 1.3 基于blink版本planner进行批处理
//        EnvironmentSettings settings3 = EnvironmentSettings.newInstance()
//                .inBatchMode()
//                .useBlinkPlanner()
//                .build();
//        TableEnvironment tableEnv3 = TableEnvironment.create(settings3);

        // 2. 创建表（连接器表）
        String createDDL = "CREATE TABLE clickTable (" +
                " user_name STRING, " +
                " url STRING, " +
                " ts BIGINT " +
                ") WITH (" +
                " 'connector' = 'filesystem', " +
                " 'path' = 'input/clicks.txt'," +
                " 'format' = 'csv'" +
                ")";

        tableEnv.executeSql(createDDL);
        // 调用table api进行表的查询转换
        Table clickTable = tableEnv.from("clickTable");
        Table resultTable = clickTable.where($("user_name").isEqual("Bob"))
                .select($("user_name"), $("url"));
//        tableEnv.createTemporaryView("result", resultTable);

        // 执行sql进行表的查询转换
//        Table resultTable2 = tableEnv.sqlQuery("select user_name from result");

        // 创建一张用于输出的表
        String createOutDDL = "CREATE TABLE outTable (" +
                " url STRING, " +
                " user_name STRING " +
                ") WITH (" +
                " 'connector' = 'filesystem', " +
                " 'path' = 'output'," +
                " 'format' = 'csv'" +
                ")";
        tableEnv.executeSql(createOutDDL);

        // 输出表
        resultTable.executeInsert("outTable");
    }
}
