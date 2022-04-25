package com.blueegg.chapter05;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TransformFilterTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Event> stream = env.fromElements(
                new Event("Mary", "./home", 1000L),
                new Event("Bob", "./cart", 2000L)
        );

        // 1. 传入一个实现了FilterFunction的类的对象
        SingleOutputStreamOperator<Event> result1 = stream.filter(new MyFilter());

        // 2. 传入一个匿名类
        SingleOutputStreamOperator<Event> result2 = stream.filter(new FilterFunction<Event>() {
            @Override
            public boolean filter(Event event) throws Exception {
                return event.user.equals("Bob");
            }
        });

        // 3. lambda表达式
        stream.filter(data -> data.user.equals("Bob")).print("lambda: ");
//        result1.print();
//        result2.print();
        env.execute();
    }

    // 实现一个自定义的FilterFunction
    public static class MyFilter implements FilterFunction<Event> {
        @Override
        public boolean filter(Event event) throws Exception {
            return event.user.equals("Mary");
        }
    }
}
