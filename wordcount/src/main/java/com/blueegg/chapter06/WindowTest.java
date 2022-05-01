package com.blueegg.chapter06;

import com.blueegg.chapter05.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import scala.Tuple2;

import java.time.Duration;

public class WindowTest {
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
//        stream.map(new MapFunction<Event, Tuple2<String, Long>>() {
//            @Override
//            public Tuple2<String, Long> map(Event value) throws Exception {
//                return Tuple2.of(value.user);
//            }
//        })
//                .keyBy(data -> data.fo)
////                .countWindow(10, 2) // 滑动计数窗口
////                .window(SlidingEventTimeWindows.of(Time.hours(1), Time.minutes(5)))
//                        .window(TumblingEventTimeWindows.of(Time.seconds(10)))
//                        .reduce(new ReduceFunction<Tuple2<String, Long>>()){
//
//        }// 事件时间滚动窗口
        env.execute();


    }
}
