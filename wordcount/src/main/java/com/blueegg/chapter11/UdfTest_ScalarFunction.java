package com.blueegg.chapter11;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.functions.ScalarFunction;

public class UdfTest_ScalarFunction {
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

        // 2. 注册自定义标量函数
        tableEnv.createTemporarySystemFunction("MyHash", MyHashFunction.class);

        // 3. 调用UDF进行查询转换
        Table resultTable = tableEnv.sqlQuery("select user, MyHash(user) from clickTable");

        // 4. 转换成流打印输出
        tableEnv.toDataStream(resultTable).print();

        env.execute();
    }

    // 自定义实现ScalarFunction
    public static class MyHashFunction extends ScalarFunction{
        public int eval(String str) {
            return str.hashCode();
        }
    }
}
