package com.blueegg.chapter09;

import apple.laf.JRSUIState;
import com.blueegg.chapter05.ClickSource;
import com.blueegg.chapter05.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.time.Duration;

// 例子说明状态是按key保存的，之前这个值没有的情况下，输出null，例子不太好
public class StateTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        SingleOutputStreamOperator<Event> stream = env.addSource(new ClickSource())
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO)
                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                            @Override
                            public long extractTimestamp(Event element, long recordTimestamp) {
                                return element.timestamp;
                            }
                        }));

        stream.keyBy(data -> data.user)
                .flatMap(new MyFlatMap())
                .print();

        env.execute();
    }

    // 实现自定义的flatMapFunction, 用于keyed state测试
    public static class MyFlatMap extends RichFlatMapFunction<Event, String>{
        // 定义状态
        ValueState<Event> myValueState;

        @Override
        public void open(Configuration parameters) throws Exception {
            myValueState = getRuntimeContext().getState(new ValueStateDescriptor<Event>("my-state", Event.class));

        }

        @Override
        public void flatMap(Event value, Collector<String> out) throws Exception {
            // 访问和更新状态
            System.out.println(myValueState.value());
            myValueState.update(value);
            System.out.println("my value: " + myValueState.value());

        }
    }
}
