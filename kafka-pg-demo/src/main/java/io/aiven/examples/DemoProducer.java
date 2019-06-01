package io.aiven.examples;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import io.aiven.examples.serde.JsonSerializer;
import io.aiven.examples.types.StockData;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class DemoProducer {
    private static final Logger logger = LogManager.getLogger();

    private static List<StockData> getStocks(String dataFile) throws IOException {
        File file = new File(dataFile);
        MappingIterator<StockData> stockDataIterator = new CsvMapper().readerWithTypedSchemaFor(StockData.class).readValues(file);
        return stockDataIterator.readAll();
    }

    public static void main(String[] args) {

        Properties appConfigs = Configs.get(Configs.APP_CONFIG_LOCATION);
        String kafkaConfigFile = appConfigs.getProperty("kafka.producer.properties");
        String dataFile = appConfigs.getProperty("data.file.location");
        String topicName = appConfigs.getProperty("topic.name");
        Properties kafkaConfig = Configs.get(kafkaConfigFile);
        kafkaConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        kafkaConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        logger.info("Start Sending Messages to Kafka...");
        try (KafkaProducer<String, StockData> producer = new KafkaProducer<>(kafkaConfig)) {
            for (StockData stockItem : getStocks(dataFile)) {
                producer.send(new ProducerRecord<>(topicName, stockItem.getSymbol(), stockItem));
            }
        } catch (IOException e) {
            logger.error("Cannot open data file " + dataFile);
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("Exception in Producer");
            throw new RuntimeException(e);
        }

        logger.info("Finished Sending Messages to Kafka");

    }
}
