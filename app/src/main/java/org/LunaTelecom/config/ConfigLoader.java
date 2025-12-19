package org.LunaTelecom.config;

import org.LunaTelecom.util.JWTUtil;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ConfigLoader {
    private static Config config;
    public static Config load(String path) {
        if (config != null) return config;
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer(options);
        representer.addClassTag(Config.class, Tag.MAP);


        Yaml yaml = new Yaml(representer, options);
        try (InputStream in = new FileInputStream(path)) {
            config = yaml.loadAs(in, Config.class);
            config.secret.authSecret = JWTUtil.initConfig(config.secret.authSecret);
            // save yaml
            String dumpedConfig = yaml.dump(config);
            FileOutputStream out = new FileOutputStream(path);
            out.write(dumpedConfig.getBytes());
            out.close();
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
