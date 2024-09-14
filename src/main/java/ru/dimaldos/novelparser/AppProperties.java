package ru.dimaldos.novelparser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public class AppProperties {

    private static Properties properties;

    public static String getProperty(String key) {
        if (properties == null) {
            try {
                properties = new Properties();
                properties.load(new InputStreamReader(new FileInputStream(
                        Objects.requireNonNull(Main.class.getClassLoader()
                                        .getResource("app.properties"))
                                .getPath()),
                        StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return properties.getProperty(key);
    }

}
