package com.blueegg.chapter11;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

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

        // 1.1 基于老版本planner进行流处理
        EnvironmentSettings settings1 = EnvironmentSettings.newInstance()
                .inStreamingMode()
                .useOldPlanner()
                .build();

        TableEnvironment tableEnv1 = TableEnvironment.create(settings1);

        // 1.2 基于老版本planner进行批处理
        ExecutionEnvironment batchEnv = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment batchTableEnv = BatchTableEnvironment.create(batchEnv);

        // 1.3 基于blink版本planner进行批处理
        EnvironmentSettings settings3 = EnvironmentSettings.newInstance()
                .inBatchMode()
                .useBlinkPlanner()
                .build();
        TableEnvironment tableEnv3 = TableEnvironment.create(settings3);
    }
}
