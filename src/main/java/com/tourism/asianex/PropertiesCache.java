package com.tourism.asianex;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

public class PropertiesCache {
    private static final Logger LOGGER = Logger.getLogger(PropertiesCache.class.getName());
    private final Properties configProp = new Properties();

    private PropertiesCache() {
        InputStream in = ResourceLoader.getResourceStream("application.properties");
        try {
            configProp.load(in);
        } catch (IOException e) {
            LOGGER.severe("Error while reading properties from the file" + e.getMessage());
        }
    }

    public static PropertiesCache getInstance() {
        return LazyHolder.INSTANCE;
    }

    public String getProperty(String key) {
        return configProp.getProperty(key);
    }

    public Set<String> getAllPropertyNames() {
        return configProp.stringPropertyNames();
    }

    public boolean containsKey(String key) {
        return configProp.containsKey(key);
    }

    public void setProperty(String key, String value) {
        configProp.setProperty(key, value);
        flush();
    }

    public void flush() {
        try (final OutputStream outputstream
                     = new FileOutputStream("target/classes/com/tourism/asianex/application.properties")) {
            configProp.store(outputstream, "File Updated");
        } catch (IOException e) {
            LOGGER.severe("Error while flushing properties to the file" + e.getMessage());
        }
    }

    public void removeProperty(String key) {
        configProp.remove(key);
        flush();
    }

    public void clear() {
        Collection<String> keys = getAllPropertyNames();
        for (String key : keys) {
            if (!key.equals("mongodb.uri")) {
                configProp.remove(key);
            }
        }
        flush();
    }

    private static class LazyHolder {
        private static final PropertiesCache INSTANCE = new PropertiesCache();
    }
}
