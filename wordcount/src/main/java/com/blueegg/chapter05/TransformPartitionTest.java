package com.blueegg.chapter05;

import org.apache.flink.api.common.functions.RichAggregateFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class TransformPartitionTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Event> stream = env.fromElements(
                new Event("Mary", "./home", 1000L),
                new Event("Bob", "./cart", 2000L),
                new Event("Bob", "./prod?id=1", 3000L),
                new Event("Mary", "./prod?id=2", 3200L),
                new Event("Bob", "./home", 3300L),
                new Event("Bob", "./prod?id=2", 3800L),
                new Event("Bob", "./prod?id=3", 4200L)
        );

        // 1.随机分区
//        stream.shuffle().print().setParallelism(4);

        // 2.轮询分区，是默认的，
//        stream.rebalance().print().setParallelism(4);

        // 3.rescale重缩放分区
//        stream.rescale().print().setParallelism(4);

        env.addSource(new RichParallelSourceFunction<Integer>() {
            @Override
            public void run(SourceContext<Integer> ctx) throws Exception {
                for (int i=0; i<8; i++){
                    // 将奇偶数分别发送到0号和1号并行分区
                    if (i % 2 == getRuntimeContext().getIndexOfThisSubtask())
                        ctx.collect(i);
                }
            }

            @Override
            public void cancel() {

            }
        }).setParallelism(2)
                        .rescale()
                                .print().setParallelism(4);

        env.execute();
    }
}