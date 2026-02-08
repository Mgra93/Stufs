package hr.algebra.cugomatfx.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static Properties prop = new Properties();

    static {
        try (InputStream is = AppConfig.class.getResourceAsStream("/config.properties")) {
            if (is == null) {
                throw new RuntimeException("config.properties not found in resources");
            }
            prop.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String get(String key) {
        return prop.getProperty(key);
    }
}
