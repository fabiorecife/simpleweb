package simpleweb;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.LocalReference;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.service.CorsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleweb.services.DBService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

import static simpleweb.config.Config.env;
import static simpleweb.config.Config.getAppName;

/**
 * Created by fabio on 21/04/2017.
 */
public class SimpleWeb extends Application {
    final Logger log = LoggerFactory.getLogger(SimpleWeb.class);

    public SimpleWeb() {
        super();
        setAuthor("José Fábio N. de Almeida <fabiorecife At gmail.com>");
        setName("RESTful "+getAppName()+" Server");
        setDescription("Component "+getAppName());
        setOwner("fabioalmeida.net");
        init();
    }

    public SimpleWeb(Context context) {
        super(context);
        init();
    }

    private void init() {
        initDBService();
        initCors();
    }

    private void initCors() {
        CorsService corsService = new CorsService();
        corsService.setAllowedOrigins(new HashSet<String>(Arrays.asList("*")));
        corsService.setAllowedCredentials(true);
        getServices().add(corsService);
    }

    private void initDBService() {
        DBService db =  new DBService();
        getServices().add(db);
    }

    @Override
    public synchronized Restlet createInboundRoot() {
        Router router = null;
        router = new Router(getContext());

        String userDir = System.getProperty("user.dir");

        Path defaultStaticDir = Paths.get( env("WEB_DIR", userDir)
                , "src","main","resources","web");

        String static_dir = defaultStaticDir.toString();
        log.debug("static.dir : " + defaultStaticDir);



        static_dir = LocalReference.createFileReference(Paths.get(static_dir).toFile()).toString();
        try {
            String urlStaticDir = URLDecoder.decode(static_dir,"UTF-8");
            log.debug("URL static.dir: " + urlStaticDir);
            Directory directory = new Directory(getContext(), urlStaticDir);
            directory.setListingAllowed(true);
            directory.setDeeplyAccessible(true);
            router.attach("/static/", directory);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("SimpleWeb createinbound error - " ,e);
        }

        return router;

    }
}
