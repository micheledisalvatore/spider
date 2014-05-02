package it.objectway.stage2014.spider;

import java.net.*;
import java.util.logging.Logger;


/**
 * Created by michele-macbookair on 28/04/14.
 */
public class ConnectionManager extends Authenticator {
    private int connectionTimeout = 0;
    private String proxyHost = null;
    private int proxyPort = 0;
    private String proxyUser = null;
    private String proxyPassword = null;
    private Logger log = Logger.getLogger("it.objectway.stage2014.spider.ConnectionManager");


    /*
    The overloaded constructor sets the timeout and proxy settings
     */
    public ConnectionManager(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public ConnectionManager(int connectionTimeout, String proxyHost, int proxyPort) {
        this(connectionTimeout);
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    public ConnectionManager(int connectionTimeout, String proxyHost, int proxyPort, String proxyUser, String proxyPassword) {
        this(connectionTimeout, proxyHost, proxyPort);
        this.proxyUser = proxyUser;
        this.proxyPassword = proxyPassword;
        Spider.msg("connection set: " + connectionTimeout + " | " + proxyHost + " | " + proxyPort + " | " + proxyUser + " | " + proxyPassword, log);
        Spider.msg("connection set: " + connectionTimeout + " | " + proxyHost + " | " + proxyPort + " | " + proxyUser + " | " + proxyPassword, log);
    }

    //conn = new URL(urlString).openConnection(proxy);
    public Proxy proxy (){
        if(proxyHost != null && proxyPort != 0)
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        return null;
    }

    //Authenticator.setDefault(authenticator);
    public Authenticator authenticator(){
        if (proxyUser != null && proxyPassword != null) {
            return new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return (new PasswordAuthentication(proxyUser, proxyPassword.toCharArray()));
                }
            };
        }

        return null;
    }

    //testConnection.setConnectTimeout(TIMEOUT_VALUE);
    //testConnection.setReadTimeout(TIMEOUT_VALUE);
    public int getConnectionTimeout(){
        return connectionTimeout * 1000;
    }

    @Override
    public String toString() {
        return "ConnectionManager{" +
                "\n\t" + "connectionTimeout=" + connectionTimeout +
                ",\n\t" + " proxyHost='" + proxyHost + '\'' +
                ",\n\t" + " proxyPort=" + proxyPort +
                ",\n\t" + " proxyUser='" + proxyUser + '\'' +
                ",\n\t" + " proxyPassword='" + proxyPassword + '\'' +
                "\n" + '}';
    }
}