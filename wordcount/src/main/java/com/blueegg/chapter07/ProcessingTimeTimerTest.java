package com.blueegg.chapter07;

import com.blueegg.chapter05.ClickSource;
import com.blueegg.chapter05.Event;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;

public class ProcessingTimeTimerTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Event> stream = env.addSource(new ClickSource());

        stream.keyBy(data -> data.user)
                        .process(new KeyedProcessFunction<String, Event, String>() {
                            @Override
                            public void processElement(Event value, Context ctx, Collector<String> out) throws Exception {
                                long currTs = ctx.timerService().currentProcessingTime();
                                out.collect(ctx.getCurrentKey() + " 数据到达，到达时间： " + new Timestamp(currTs));

                                // 注册一个10秒后的定时器
                                ctx.timerService().registerProcessingTimeTimer(currTs + 10*1000L);
                            }

                            @Override
                            public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
                                out.collect(ctx.getCurrentKey() + "定时器触发，触发时间： " + new Timestamp(timestamp));
                            }
                        }).print();
        env.execute();
    }
}
