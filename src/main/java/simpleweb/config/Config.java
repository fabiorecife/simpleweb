package simpleweb.config;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by fabio on 21/04/2017.
 */
public class Config {

    private static Properties config = new Properties();


    public static void load() {
        try {
            config.load(new FileReader(Paths.get("./.env").toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String env(String name) {
        return env(name,"");
    }

    public static String env(String name, String defaultValue) {
        return config.getProperty(name, defaultValue);
    }

    public static String getAppName() {
        return env("APP_NAME","App");
    }

}
