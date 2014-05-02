package it.objectway.stage2014.spider;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by michele-macbookair on 28/04/14.
 */
public class Spider {

    public static void start(String outputDir) {
        try {
            /*
            1) It gets settings by property file
             */
            PropertyManager properties = new PropertyManager(outputDir);

            /*
            2) It sets the connection settings
             */
            ConnectionManager connection = new ConnectionManager(properties.getConnectionTimeout(), properties.getProxyHost(), properties.getProxyPort(), properties.getProxyUser(), properties.getProxyPassword());

            try {
                /*
                3) It initializes a list of urls
                 */
                UrlListManager urlList = new UrlListManager(properties.getStartingUrl(), properties.getDomainDepth(), properties.isExternalDomain(), properties.getOutputDir());

                /*
                4) It initializes the downloader
                 */
                FileDownloader fileDownloader = new FileDownloader(properties.getStartingUrl(), properties.getOutputDir(), properties.getDownloadMimTypes(), properties.getFileMaxSize(), properties.getFileMinSize(), connection.getConnectionTimeout(), connection.proxy(), connection.authenticator());

                boolean initializeDb = properties.getMysqlDbHost() != null && properties.getMysqlDbName() != null &&  properties.getMysqlDbUsername() != null &&  properties.getMysqlDbPassword() != null;
                DB dbConn = null;
                if(initializeDb){
                    dbConn = new DB(properties.getMysqlDbHost(), properties.getMysqlDbName(), properties.getMysqlDbUsername(), properties.getMysqlDbPassword());
                }

                URI startingUrl = URI.create(properties.getStartingUrl());

                /*
                5) It starts a thread pool
                 */
                ExecutorService exec = Executors.newFixedThreadPool(properties.getMaxThread());
                for (int i = 0; i < properties.getMaxThread(); i++) {
                    if(initializeDb){
                        exec.execute(new SpiderThread(urlList, fileDownloader, dbConn, startingUrl.getHost()));
                    }else{
                        exec.execute(new SpiderThread(urlList, fileDownloader));
                    }
                }
            } catch (URLSkippedException e) {
                Spider.msg("> Err: " + e.getMessage());
            }

        } catch (IOException e) {
            Spider.msg("> Err: " + e.getMessage());
        }

    }

    public static void msg(String msg){
        System.out.println(msg);
    }

    public static void msg(String msg, Logger log){
        System.out.println(msg);
        //log.info(msg);
    }
}
