package it.objectway.stage2014.spider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by michele-macbookair on 28/04/14.
 */
public class UrlListManager {
    private HashMap<URL, UrlInfoContainer> urls = new HashMap<URL, UrlInfoContainer>();
    private BlockingQueue<URL> urlsQueue = new LinkedBlockingDeque<URL>();
    private String currentHost;
    private String currentDomain;
    private int maxDomainDepth;
    private boolean externalDomain;
    private Logger log = Logger.getLogger("it.objectway.stage2014.spider.UrlListManager");
    private Logger fileLogger = Logger.getLogger("DownloadResult");
    private FileHandler fh;

    public UrlListManager(String startingUrl, int maxDomainDepth, boolean externalDomain, String outputDir) throws URLSkippedException, MalformedURLException {
        try {
            fh = new FileHandler(outputDir + "result.log");
            fileLogger.setUseParentHandlers(false);
            fileLogger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        URL startingURL = new URL(startingUrl);
        this.currentHost = startingURL.getHost();
        this.currentDomain = startingURL.getProtocol() + "://" + this.currentHost;
        this.maxDomainDepth = maxDomainDepth;
        this.externalDomain = externalDomain;

        this.addUrl(startingUrl, startingURL);



        Spider.msg("urlList inizialized: " + currentHost + " | " + currentDomain + " | " + maxDomainDepth + " | " + externalDomain, log);
    }

    public URL getNextUrl() {
        URL takenUrl;
        UrlInfoContainer entry;
        while (!urlsQueue.isEmpty()){
            try {
                takenUrl = urlsQueue.take();
                entry = urls.get(takenUrl);
                if(entry.getStatus().equals("todo")){
                    setUrlStatus(takenUrl, "elaborating");
                    return takenUrl;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setUrlStatus(URL url, String status){
        urls.get(url).setStatus(status);
        Spider.msg("url changed status to: " + status, log);

        if(status.equals("OK") || status.equals("KO") || status.equals("SKIPPED")){
            fileLogger.info(String.valueOf(url) + ": " + status);
        }
    }

    public void addUrls(List<String> urls, URL currentUrl) {
        int c=0;
        for (String url : urls) {
            try {
                addUrl(url, currentUrl);
            } catch (URLSkippedException | MalformedURLException e) {
                System.out.println(e.getMessage());
                c++;
            }
        }

        Spider.msg("added " + (urls.size() - c) + " links of " + urls.size(), log);
    }

    public void addUrl(final String originalUrl, URL currentUrl) throws URLSkippedException, MalformedURLException {
        String modifiedUrl;
        URL newURL;
        boolean addThisUrl = false, isExternal = false;

        try {
            newURL = new URL(originalUrl);
            modifiedUrl = originalUrl;
        }catch (MalformedURLException e){
            if(originalUrl.matches("^//.*")){
                modifiedUrl = currentUrl.getProtocol() + ":" + originalUrl;
            }else if(originalUrl.matches("^/.*")){
                modifiedUrl = currentDomain + originalUrl;
            }else{
                modifiedUrl = currentUrl.toString().replaceAll("/*$", ("/" + originalUrl));
            }
            newURL = new URL(modifiedUrl);
        }

        URI newURI = URI.create(modifiedUrl);

        int depth = currentUrl.toString().equals(originalUrl) ? 0 : 1 + this.urls.get(currentUrl).getDepth();

        if (!this.urls.containsKey(newURL)) {
            if(maxDomainDepth >= depth) {
                if (newURI.isAbsolute() && !UrlStringEditor.isSameHost(newURI.getHost(), currentHost)) {
                    isExternal = true;
                    if (this.externalDomain) {
                        addThisUrl = true;
                    } else {
                        throw new URLSkippedException("External links not permitted: " + originalUrl);
                    }
                } else {
                    addThisUrl = true;
                }
            }else{
                throw new URLSkippedException("over maxDepth");
            }
        } else {
            throw new URLSkippedException("Url exists yet: " + originalUrl);
        }

        if(addThisUrl){
            boolean isInternal = UrlStringEditor.isSameHost(newURI.getHost(), currentHost);
            UrlInfoContainer urlInfo = new UrlInfoContainer("todo", depth, isInternal);
            if(!this.urls.containsKey(newURL)){
                this.urls.put(newURL, urlInfo);
                try {
                    urlsQueue.put(newURL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Spider.msg("addUrl: " + originalUrl + " | " + modifiedUrl + " | " + isExternal + " | " + addThisUrl + " | " + depth + " | " + urlsQueue.size(), log);
    }

    public boolean isInternal(URL url){
        return urls.get(url).isInternal();
    }


    @Override
    public String toString() {
        return "UrlListManager{" +
                "\n\turls=" + urls +
                ",\n\t currentHost='" + currentHost + '\'' +
                ",\n\t maxDomainDepth=" + maxDomainDepth +
                ",\n\t currentDomain=" + currentDomain +
                ",\n\t externalDomain=" + externalDomain + "\n" +
                '}';
    }
}
