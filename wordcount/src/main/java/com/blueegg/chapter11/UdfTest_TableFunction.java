package com.blueegg.chapter11;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.functions.TableFunction;

public class UdfTest_TableFunction {
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

        // 2. 注册自定义表函数
        tableEnv.createTemporarySystemFunction("MySplit", MySplit.class);

        // 3. 调用UDF进行查询转换
        Table resultTable = tableEnv.sqlQuery("select user, url, word, length " +
                "from clickTable, lateral table(MySplit(url)) as T(word, length)");

        // 4. 转换成流打印输出
        tableEnv.toDataStream(resultTable).print();

        env.execute();
    }

    // 实现自定义的表函数
    public static class MySplit extends TableFunction<Tuple2<String, Integer>>{
        public void eval(String str){
            String[] fields = str.split("\\?"); // 根据问号拆分
            for (String field: fields){
                collect(Tuple2.of(field, field.length()));
            }
        }
    }
}
