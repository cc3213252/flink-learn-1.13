package com.blueegg.chapter11;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.functions.AggregateFunction;

public class UdfTest_AggregateFunction {
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
        tableEnv.createTemporarySystemFunction("WeightedAverage", WeightedAverage.class);

        // 3. 调用UDF进行查询转换
        Table resultTable = tableEnv.sqlQuery("select user, WeightedAverage(ts, 1) as w_avg " +
                "from clickTable group by user");

        // 4. 转换成流打印输出
        tableEnv.toChangelogStream(resultTable).print();

        env.execute();
    }

    // 单独定义一个累加器类型
    public static class WeightedAvgAccmulator{
        public long sum = 0;
        public int count = 0;
    }

    // 实现自定义的聚合函数，计算加权平均值
    public static class WeightedAverage extends AggregateFunction<Long, WeightedAvgAccmulator>{
        @Override
        public Long getValue(WeightedAvgAccmulator weightedAvgAccmulator) {
            if (weightedAvgAccmulator.count == 0)
                return null;
            else
                return weightedAvgAccmulator.sum / weightedAvgAccmulator.count;
        }

        @Override
        public WeightedAvgAccmulator createAccumulator() {
            return new WeightedAvgAccmulator();
        }

        // 累加计算的方法
        public void accumulate(WeightedAvgAccmulator accmulator, Long iValue, Integer iWeight){
            accmulator.sum += iValue * iWeight;
            accmulator.count += iWeight;
        }
    }
}
