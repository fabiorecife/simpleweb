package simpleweb.config;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;
import static simpleweb.config.Config.env;
import static simpleweb.config.Config.load;

/**
 * Created by fabio on 21/04/2017.
 */
public class ConfigTest {

    @Test
    public void envTest() {
        load();
        assertEquals("SimpleWeb", env("APP_NAME"));
    }
}
