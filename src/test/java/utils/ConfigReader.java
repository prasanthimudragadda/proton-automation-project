package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

     Properties prop = new Properties();

    public ConfigReader() throws IOException {
        // in CI there is no config.properties, all values come from environment variables
        File file = new File("src/test/resources/config.properties");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                prop.load(fis);
            }
        }
    }

    // an environment variable like PROTON_PASSWORD overrides the file value (useful for CI)
    public String getProperty(String key){
        String fromEnv = System.getenv(key.replace('.', '_').toUpperCase());
        if (fromEnv != null) {
            return fromEnv;
        }
        return prop.getProperty(key);
    }
}
