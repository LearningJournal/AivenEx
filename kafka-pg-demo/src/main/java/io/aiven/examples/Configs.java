package io.aiven.examples;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Configs {
    private static final Logger logger = LogManager.getLogger();
    public static final String APP_CONFIG_LOCATION = "configs/app.configs";

    public static Properties get(String fileLocation){
        Properties configs = new Properties();
        try (FileReader configReader = new FileReader(fileLocation)) {
            configs.load(configReader);
            return configs;
        } catch (IOException e) {
            logger.error("Config File Not Found");
            throw new RuntimeException(e);
        }
    }
}
