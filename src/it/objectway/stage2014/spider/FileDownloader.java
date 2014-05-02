package it.objectway.stage2014.spider;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by michele-macbookair on 28/04/14.
 */
public class FileDownloader {
    private Proxy proxy;
    private Authenticator authenticator;
    private int connectionTimeout;
    private List<String> downloadMimType;
    private String outputDir;
    private int fileMaxSize;
    private int fileMinSize;
    private int fileSize;
    private String mimType;
    private String localFilePath;
    private Logger log = Logger.getLogger("it.objectway.stage2014.spider.FileDownloader");
    private String currentHost;

    private static int htmlFilesCounter = 0;
    private static int imageFilesCounter = 0;

    public FileDownloader(String startingUrl, String outputDir, List<String> downloadMimType, int fileMaxSize, int fileMinSize, int connectionTimeout, Proxy proxy, Authenticator authenticator) {
        this.proxy = proxy;
        this.authenticator = authenticator;
        this.connectionTimeout = connectionTimeout;
        this.downloadMimType = downloadMimType;
        this.outputDir = outputDir;
        this.fileMaxSize = fileMaxSize;
        this.fileMinSize = fileMinSize;

        URI startingURI = URI.create(startingUrl);
        this.currentHost = startingURI.getHost();

        Spider.msg("downloader inizialized: " + outputDir + " | " + downloadMimType + " | " + fileMaxSize + " | " + fileMinSize + " | " + connectionTimeout + " | " + proxy + " | " + authenticator, log);
    }


    public void download(URL url) throws IOException, FileDownloaderException {
        InputStream inputStream = inputStream(url); //url.openStream();

        if(fileSize > fileMinSize && fileSize < fileMaxSize && isValidMimType()){
            String folderPath, fileName, originalFileName;

            boolean isInternal = url.getHost().equals(currentHost);
            if(isInternal) {
                originalFileName = url.getPath();
                fileName = UrlStringEditor.addIndexHtmlIfAbsent(originalFileName);

                localFilePath = UrlStringEditor.getLocalFilePath(this.outputDir, fileName);

                folderPath = localFilePath.replaceAll("(.*)/.*$", "$1");
            }else{
                originalFileName = url.toString();
                folderPath = UrlStringEditor.getDirForExternal(this.outputDir);
                fileName = UrlStringEditor.relativizeExternalUrl(originalFileName);
                localFilePath = UrlStringEditor.getLocalFilePath(folderPath, fileName);
            }

            if(folderPath.length() > 0){
                File folder = new File(folderPath);
                folder.mkdirs();
            }

            FileOutputStream localFile = new FileOutputStream(localFilePath);

            ReadableByteChannel remoteFile = Channels.newChannel(inputStream);
            localFile.getChannel().transferFrom(remoteFile, 0, Long.MAX_VALUE);

            localFile.close();

            if(mimType.matches("^.*/html$")){
                htmlFilesCounter++;
            }else if(mimType.matches("^image/.*$")){
                imageFilesCounter++;
            }

            Spider.msg(url.toString() + " | " + originalFileName + " | " + fileName + " | " + folderPath + " | " + localFilePath, log);
        }else {
            if(fileSize < fileMinSize || fileSize > fileMaxSize) {
                Spider.msg("Invalid file size: " + fileSize, log);
                throw new FileDownloaderException("skipped - Invalid file size: " + fileSize);
            }else {
                Spider.msg("Invalid mimtype", log);
                throw new FileDownloaderException("skipped - Invalid mimtype");
            }
        }
    }

    private boolean isValidMimType(){
        String regex;
        for (String validMimType : downloadMimType) {
            if(validMimType.matches(".*/\\*")){
                regex = "^" + validMimType.replaceAll("\\*$", "") + ".+$";
            }else{
                regex = "^" + validMimType + "$";
            }
            if(mimType.matches(regex)){
                return true;
            }
        }
        return false;
    }

    private InputStream inputStream(URL url) throws IOException, FileDownloaderException {
        try {
            URLConnection connection = proxy != null ? url.openConnection(proxy) : url.openConnection();

            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(connectionTimeout);

            if (authenticator != null)
                Authenticator.setDefault(authenticator);

            try {
                mimType = connection.getHeaderField("Content-Type").replaceAll(";.*$", "");
            } catch (NullPointerException e) {
                Spider.msg("content type error", log);
                throw new FileDownloaderException("content type error");
            }
            try {
                fileSize = Integer.parseInt(connection.getHeaderField("content-Length"));
            } catch (NumberFormatException e) {
                Spider.msg("filesize error", log);
                fileSize = 0;
            }

            return connection.getInputStream();
        }catch (FileNotFoundException e){
            throw new FileDownloaderException("connection error");
        }
    }

    public String getLocalFilePath(){
        return localFilePath;
    }

    public String getMimType(){
        return this.mimType;
    }

    public int getHtmlFilesCounter(){
        return htmlFilesCounter;
    }

    public int getImageFilesCounter(){
        return imageFilesCounter;
    }

    @Override
    public String toString() {
        return "FileDownloader{" +
                "proxy=" + proxy +
                ",\n\t authenticator=" + authenticator +
                ",\n\t connectionTimeout=" + connectionTimeout +
                ",\n\t downloadMimType=" + downloadMimType +
                ",\n\t outputDir='" + outputDir + '\'' +
                ",\n\t fileMaxSize=" + fileMaxSize +
                ",\n\t fileMinSize=" + fileMinSize + "\n" +
                '}';
    }
}
