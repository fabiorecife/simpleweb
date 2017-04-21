package simpleweb;

import org.apache.commons.lang.StringUtils;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.ext.jetty.HttpServerHelper;
import org.restlet.service.MetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleweb.config.Config;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;

import static simpleweb.config.Config.env;
import static simpleweb.config.Config.load;

/**
 * Created by fabio on 21/04/2017.
 */
public class App {

    final static Logger log = LoggerFactory.getLogger(App.class);
    private final static int PORT = 3000;
    private static Component component = null;

    public static void main(String [] args) {
        Config.load();
        log.info(" start app :: " + env("APP_NAME"));
        component =	createComponent();
        try {
            component.start();
            log.info(env("APP_NAME") + " was started.");
            HttpServerHelper help =  new HttpServerHelper(component.getServers().get(0));
            log.info(" max threads- " + help.getThreadPoolMaxThreads());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("App.Main" , e);
            throw new RuntimeException("the service is not started");
        }

    }


    private static Component createComponent() {
        MetadataService media = new MetadataService();
        media.getAllMediaTypes(null);


        initRetletLogLevel();

        String port = env("SERVER_PORT","");
        int configPort = PORT;
        if (!port.isEmpty() && StringUtils.isNumeric(port)) {
            configPort = Integer.valueOf(port);
        }
        log.info("Server port: " + Integer.toString(configPort));

        //Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        Component component = new Component();
        String httpHelper = "org.restlet.ext.jetty.HttpServerHelper";
        Server server = new Server(null, Arrays.asList(Protocol.HTTP), null,configPort, component, httpHelper);

        component.getServers().add(server);
        component.getClients().add(Protocol.FILE);
        component.getClients().add(Protocol.CLAP);

        Application application = new SimpleWeb();

        String uriPattern = env("APP_URI","/simpleweb");
        log.info("Service start uri: " + uriPattern);
        component.getDefaultHost().attach(uriPattern, application);
        server.getContext().getParameters().add("useForwardedForHeader", "true");
        return component;
    }


    private static void initRetletLogLevel() {
        String logLevelRestlet = env("log.restlet.level", "FINEST");
        Level level = null;
        if (!logLevelRestlet.isEmpty()) {
            level = Level.parse(logLevelRestlet);
        } else {
            level = Level.OFF;
        }
        org.restlet.engine.Engine.setLogLevel(level);
        log.info("restlet loglevel " + level);
    }

}
