package it.objectway.stage2014.spider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by michele-macbookair on 28/04/14.
 */
public class PropertyManager {
    private String startingUrl;
    private List<String> downloadMimTypes;
    private int domainDepth;
    private String outputDir;
    private int fileMaxSize;
    private int fileMinSize;
    private int maxThread;
    private boolean externalDomain;
    private int connectionTimeout;
    private String proxyHost;
    private int proxyPort;
    private String proxyUser;
    private String proxyPassword;
    private Logger log = Logger.getLogger("it.objectway.stage2014.spider.PropertyManager");

    private String mysqlDbUsername;
    private String mysqlDbPassword;
    private String mysqlDbHost;
    private String mysqlDbName;

    /*
    The constructor gets the property file and imports the settings
     */
    public PropertyManager(String fileName) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(fileName));

        this.startingUrl = properties.getProperty("starting.url");
        String downloadMimTypesTemp = properties.getProperty("download.mimtype");
        String[] downloadMimTypesTempArray;
        if (downloadMimTypesTemp.contains(",")) {
            downloadMimTypesTempArray  = downloadMimTypesTemp.split(",");
        }else{
            downloadMimTypesTempArray = new String[1];
            downloadMimTypesTempArray[0] = downloadMimTypesTemp;
        }
        this.downloadMimTypes = Arrays.asList(downloadMimTypesTempArray);
        this.domainDepth = Integer.parseInt(properties.getProperty("domain.depth"));
        this.outputDir = properties.getProperty("output.dir") + (properties.getProperty("output.dir").matches("^.*"+File.separator + "$") ? "" : File.separator);
        this.fileMaxSize = Integer.parseInt(properties.getProperty("file.maxsize"));
        this.fileMinSize = Integer.parseInt(properties.getProperty("file.minsize"));
        this.maxThread = Integer.parseInt(properties.getProperty("max.thread"));
        this.externalDomain = Boolean.parseBoolean(properties.getProperty("external.domain"));
        this.connectionTimeout = Integer.parseInt(properties.getProperty("connection.timeout"));
        this.proxyHost = properties.getProperty("proxy.host");
        this.proxyPort = properties.getProperty("proxy.port").length() > 0 ? Integer.parseInt(properties.getProperty("proxy.port")) : 0;
        this.proxyUser = properties.getProperty("proxy.user");
        this.proxyPassword = properties.getProperty("proxy.password");

        this.mysqlDbUsername = properties.getProperty("mysql.db.username");
        this.mysqlDbPassword = properties.getProperty("mysql.db.password");
        this.mysqlDbHost = properties.getProperty("mysql.db.host");
        this.mysqlDbName = properties.getProperty("mysql.db.name");

        Spider.msg("properties red: " + startingUrl + " | " + downloadMimTypes + " | " + domainDepth + " | " + outputDir + " | " +
                fileMaxSize + " | " + fileMinSize + " | " + maxThread + " | " + externalDomain + " | " + connectionTimeout + " | " +
                proxyHost + " | " + proxyPort + " | " + proxyUser + " | " + proxyPassword, log);
    }

    public String getStartingUrl() {
        return startingUrl;
    }

    public List<String> getDownloadMimTypes() {
        return downloadMimTypes;
    }

    public int getDomainDepth() {
        return domainDepth;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public int getFileMaxSize() {
        return fileMaxSize;
    }

    public int getFileMinSize() {
        return fileMinSize;
    }

    public int getMaxThread() {
        return maxThread;
    }

    public boolean isExternalDomain() {
        return externalDomain;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public String getMysqlDbUsername() {
        return mysqlDbUsername;
    }
    public String getMysqlDbPassword() {
        return mysqlDbPassword;
    }
    public String getMysqlDbHost() {
        return mysqlDbHost;
    }
    public String getMysqlDbName() {
        return mysqlDbName;
    }
    @Override
    public String toString() {
        return "PropertyManager{" +
                "\n\tstartingUrl='" + startingUrl + '\'' +
                ",\n\t downloadMimTypes=" + downloadMimTypes +
                ",\n\t domainDepth=" + domainDepth +
                ",\n\t outputDir='" + outputDir + '\'' +
                ",\n\t fileMaxSize=" + fileMaxSize +
                ",\n\t fileMinSize=" + fileMinSize +
                ",\n\t maxThread=" + maxThread +
                ",\n\t externalDomain=" + externalDomain +
                ",\n\t connectionTimeout=" + connectionTimeout +
                ",\n\t proxyHost='" + proxyHost + '\'' +
                ",\n\t proxyPort=" + proxyPort +
                ",\n\t proxyUser='" + proxyUser + '\'' +
                ",\n\t proxyPassword='" + proxyPassword + '\'' + "\n" +
                '}';
    }
}
