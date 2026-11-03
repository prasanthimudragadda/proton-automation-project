package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private final Properties prop = new Properties();

    public ConfigReader() throws IOException {
        File file = new File("src/test/resources/config.properties");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                prop.load(fis);
            }
        }
    }

    public String getProperty(String key) {
        String fromEnv = System.getenv(key.replace('.', '_').toUpperCase());
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        String value = prop.getProperty(key);
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return value;
    }
}
