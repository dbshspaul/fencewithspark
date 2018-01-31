package com.wrkspot.emp.fence.spark;

import com.wrkspot.emp.fence.FenceService;
import com.wrkspot.emp.fence.locateMeAlgorithms.algorithm7.IOTDataHandlerAlg7;
import com.wrkspot.emp.fence.locateMeAlgorithms.algorithm8.IOTDataHandlerAlg8;
import com.wrkspot.emp.fence.util.FenceGlossaries;
import kafka.serializer.StringDecoder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SparkStreamJobProcessor implements Serializable{
    private static final Logger LOGGER = LoggerFactory.getLogger(SparkStreamJobProcessor.class);

    private IOTDataHandlerAlg7 iotDataHandlerAlg7 = IOTDataHandlerAlg7.getInstance();
    private IOTDataHandlerAlg8 iotDataHandlerAlg8 = IOTDataHandlerAlg8.getInstance();
    private FenceService fenceService = FenceService.getInstance();

    public void receive(){
        BasicConfigurator.configure();
//        org.apache.log4j.Logger.getLogger("org").setLevel(Level.ERROR);
//        org.apache.log4j.Logger.getLogger("akka").setLevel(Level.ERROR);
        System.setProperty("hadoop.home.dir", "D:/hadoop-3.0.0/hadoop-3.0.0");
        SparkConf conf = new SparkConf()
                .setAppName("spark-streaming")
                .setMaster("local[*]");
        JavaStreamingContext ssc = new JavaStreamingContext(conf, new Duration(10000));

        Map<String, String> kafkaParams = new HashMap<>();
        kafkaParams.put("metadata.broker.list", FenceGlossaries.getProperties().getProperty("kafka.server.name"));
        Set<String> topics = Collections.singleton(FenceGlossaries.getProperties().getProperty("kafka.topic.name"));

        JavaPairInputDStream<String, String> directKafkaStream = KafkaUtils.createDirectStream(ssc,
                String.class, String.class, StringDecoder.class, StringDecoder.class, kafkaParams, topics);






        directKafkaStream.foreachRDD(rdd -> {
            rdd.foreach(record -> {
                LOGGER.debug("received message from checkin topic='{}'", record._2());
                try {
                    fenceService.findById(record._2());
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error(" Failed in use GEN 1 Fence", e);
                }

                try {
                    LOGGER.debug("Message ID is " +  record._2());
                    iotDataHandlerAlg7.locateMe( record._2());
                } catch (Exception e) {
                    LOGGER.info(" Failed in Alg 7");
                    LOGGER.error(" Failed in Alg7");
                }

                try {
                    LOGGER.debug("Message ID is " +  record._2());
                    iotDataHandlerAlg8.locateme( record._2());
                } catch (Exception e) {
                    LOGGER.info(" Failed in Alg 8");
                    LOGGER.error(" Failed in Alg8");
                }
            });
        });





        ssc.start();
        try {
            ssc.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        SparkStreamJobProcessor jobProcessor = new SparkStreamJobProcessor();
        jobProcessor.receive();
    }

}
