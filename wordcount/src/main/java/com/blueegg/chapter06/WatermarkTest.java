package com.blueegg.chapter06;

import com.blueegg.chapter05.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;

public class WatermarkTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.getConfig().setAutoWatermarkInterval(100);

        SingleOutputStreamOperator<Event> stream = env.fromElements(
                        new Event("Mary", "./home", 1000L),
                        new Event("Bob", "./cart", 2000L),
                        new Event("Bob", "./prod?id=1", 3000L),
                        new Event("Mary", "./prod?id=2", 3200L),
                        new Event("Bob", "./home", 3300L),
                        new Event("Bob", "./prod?id=2", 3800L),
                        new Event("Bob", "./prod?id=3", 4200L)
                )
                // 有序流的watermark生成
//                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forMonotonousTimestamps()
//                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
//                            @Override
//                            public long extractTimestamp(Event element, long recordTimeStamp) {
//                                return element.timestamp * 1000;
//                            }
//                        }))
                // 乱序流的watermark生成
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofSeconds(2))
                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                            @Override
                            public long extractTimestamp(Event element, long recordTimestamp) {
                                return element.timestamp;
                            }
                        }));
        stream.print();
        env.execute();


    }
}
