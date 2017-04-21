package simpleweb.services;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import org.restlet.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static simpleweb.config.Config.env;

/**
 * Created by fabio on 21/04/2017.
 */
public class DBService extends Service {
    final Logger log = LoggerFactory.getLogger(DBService.class);
    private BoneCPDataSource pool;
    private BoneCPConfig boneConfig;


    @Override
    public synchronized void start() throws Exception {
        super.start();
        if (notHasPool()) {
            createConfiguration();
            createPool();
        }
        log.info("DBService Connection started");
    }

    @Override
    public synchronized void stop() throws Exception {
        pool.close();
        super.stop();
    }

    private void createPool() {
        try {
            pool = new BoneCPDataSource(boneConfig);   ;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("createPool error " ,e);
            throw new RuntimeException("Database was not initialized");
        }
    }

    private boolean notHasPool() {
        return pool == null;
    }



    private void createConfiguration() {
        boneConfig = new BoneCPConfig();

        String url = env("JDBC_URL", "jdbc:jdbc:postgresql://localhost/mydb");
        String user = env("DB_USER");
        String password = env("DB_PASSWORD");
        boneConfig.setJdbcUrl(url);	            // set the JDBC url
        boneConfig.setUsername(user);			// set the username
        boneConfig.setPassword(password);		// set the password

        configureNumberOfConnections();

        logBuildConfiguration();

    }

    private void logBuildConfiguration() {
        log.info("setting up connection pool");
        log.info("Connections available: " + boneConfig.getPartitionCount()* boneConfig.getMaxConnectionsPerPartition());
        log.info("Particion count - " + boneConfig.getPartitionCount());
        log.info("Max connection per partition - " + boneConfig.getMaxConnectionsPerPartition());
        log.info(String.format("configuration:\n url:%s \n user:%s ", boneConfig.getJdbcUrl(), boneConfig.getUsername()));
    }

    private void configureNumberOfConnections() {
        int particionCount = Integer.valueOf(env("BoneCPConfig.particionCount","0"));
        if (particionCount>0) {
            boneConfig.setPartitionCount(particionCount);
        }
        int maxConnectionsPerPartition = Integer.valueOf(env("BoneCPConfig.maxConnectionsPerPartition","0"));
        if (maxConnectionsPerPartition>0) {
            boneConfig.setMaxConnectionsPerPartition(maxConnectionsPerPartition);
        }
    }
}
