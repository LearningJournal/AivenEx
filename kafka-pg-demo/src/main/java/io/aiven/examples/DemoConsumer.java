package io.aiven.examples;

import io.aiven.examples.serde.JsonDeserializer;
import io.aiven.examples.types.StockData;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("InfiniteLoopStatement")
public class DemoConsumer {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

        Properties appConfigs = Configs.get(Configs.APP_CONFIG_LOCATION);
        String kafkaConfigFile = appConfigs.getProperty("kafka.consumer.properties");
        String topicName = appConfigs.getProperty("topic.name");
        String tableName = appConfigs.getProperty("pg.table");

        Properties kafkaConfig = Configs.get(kafkaConfigFile);
        kafkaConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        kafkaConfig.put("specific.class.name", StockData.class);

        logger.info("Start Reading Messages from Kafka...");
        try (KafkaConsumer<String, StockData> consumer = new KafkaConsumer<>(kafkaConfig)) {
            consumer.subscribe(Collections.singletonList(topicName));
            PGRepository.connect();
            while (true) {
                ConsumerRecords<String, StockData> records = consumer.poll(Duration.ofMillis(100));
                List<StockData> dataList = new ArrayList<>();
                for (ConsumerRecord<String, StockData> record : records) {
                    dataList.add(record.value());
                }
                if (dataList.size() > 0) {
                    PGRepository.saveStockData(tableName, dataList);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in Consumer");
            throw new RuntimeException(e);
        } finally {
            PGRepository.close();
        }

    }
}
