package it.objectway.stage2014.spider;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by michele-macbookair on 29/04/14.
 */
public class SpiderThread implements Runnable {
    private UrlListManager urlList;
    private FileDownloader fileDownloader;
    private DB dbConn;
    private String website;

    public SpiderThread(UrlListManager urlList, FileDownloader fileDownloader) {
        this.urlList = urlList;
        this.fileDownloader = fileDownloader;
        this.dbConn = null;
    }
    public SpiderThread(UrlListManager urlList, FileDownloader fileDownloader, DB dbConn, String website) {
        this(urlList, fileDownloader);
        this.dbConn = dbConn;
        this.website = website;
    }

    @Override
    public void run() {
        URL currentUrl;
        while(true){
            if((currentUrl = urlList.getNextUrl()) != null) {
                Spider.msg(">>>>>>>>>>>>> Thread n° " + Thread.currentThread().getId());
                try {
                    fileDownloader.download(currentUrl);

                    urlList.setUrlStatus(currentUrl, "OK");

                    if (fileDownloader.getMimType().equals("text/html") && urlList.isInternal(currentUrl)) {
                        ParserCustom parser = new ParserCustom(fileDownloader.getLocalFilePath(), currentUrl);
                        //parser.convertLinks(currentUrl);

                        List<String> links = parser.getSourcesAndLinks();

                        urlList.addUrls(links, currentUrl);
                    } else {
                        Spider.msg("* Parser skipped cause mimtype: " + fileDownloader.getMimType());
                    }

                    if(dbConn != null){
                        dbConn.insertOrUpadate(website, fileDownloader.getHtmlFilesCounter(), fileDownloader.getImageFilesCounter());
                    }

                } catch (FileDownloaderException e) {
                    if (e.getMessage().matches("^skipped.*")) {
                        urlList.setUrlStatus(currentUrl, "SKIPPED");
                    } else {
                        urlList.setUrlStatus(currentUrl, "KO");
                    }
                    Spider.msg("> Except: " + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    urlList.setUrlStatus(currentUrl, "KO");
                }
            }
        }
    }
}
