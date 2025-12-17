package org.LunaTelecom.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;

public class ConfigLoader {
    private static Config config;
    public static Config load(String path) {
        if (config != null) return config;
        Yaml yaml = new Yaml();
        try (InputStream in = new FileInputStream(path)) {
            config = yaml.loadAs(in, Config.class);
            return config;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config file", e);
        }
    }

    public static Config getConfig() {
        if (config == null) {
            throw new IllegalStateException("Config not loaded yet");
        }
        return config;
    }
}
