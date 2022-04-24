package com.blueegg.chapter05;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.ArrayList;
import java.util.Properties;

public class SourceTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 从文件读
//        DataStreamSource<String> stream1 = env.readTextFile("input/clicks.txt");
//
//        // 从集合中读
//        ArrayList<Integer> nums = new ArrayList<>();
//        nums.add(2);
//        nums.add(5);
//        DataStreamSource<Integer> numStream = env.fromCollection(nums);
//        ArrayList<Object> events = new ArrayList<>();
//        events.add(new Event("Mary", "./home", 1000L));
//        events.add(new Event("Bob", "./cart", 2000L));
//        DataStreamSource<Object> stream2 = env.fromCollection(events);
//
//        // 从元素读
//        DataStreamSource<Event> stream3 = env.fromElements(
//                new Event("Mary", "./home", 1000L),
//                new Event("Bob", "./cart", 2000L)
//        );

        // 从socket文本流读取
//        DataStreamSource<String> stream4 = env.socketTextStream("localhost", 7777);

//        stream1.print("1");
//        numStream.print("nums");
//        stream2.print("2");
//        stream3.print("3");
//        stream4.print("4");

        Properties pro = new Properties();
        pro.setProperty("bootstrap.servers", "host1:9092");
        pro.setProperty("group.id", "consumer-group");
        pro.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        pro.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        pro.setProperty("auto.offset.reset", "latest");

        DataStreamSource<String> kafkaStream = env.addSource(new FlinkKafkaConsumer<String>("clicks", new SimpleStringSchema(), pro));
        kafkaStream.print();
        env.execute();
    }
}
