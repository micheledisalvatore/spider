package it.objectway.stage2014.spider;

import java.io.File;
import java.net.URI;

/**
 * Created by stageusr on 30/04/2014.
 */
public class UrlStringEditor {
    public static String addIndexHtmlIfAbsent(String url) {
        if(url.length() == 0 || url.matches("^.*/$")) {
            url += "index.html";
        }else if(!url.matches("^.*/.+\\..+$")){
            url += "/index.html";
        }
        return url;
    }

    public static String relativizeExternalUrl(String url){
        return url.replaceAll("[^A-Za-z0-9\\.-]","_");
    }

    public static String getLocalFilePath(String dir, String relativeUrl){
        return dir + removeStastSlash(relativeUrl);
    }

    public static String removeStastSlash(String fileName){
        return fileName.replaceAll("^/", "");
    }

    public static String getDirForExternal(String dir){
        return dir + "externalContents/";
    }



    public static boolean isSameHost(String firstHost, String secondHost){
        return firstHost.equals(secondHost);
    }

    public static boolean isSameHost(URI firstUrl, String secondHost){
        return isSameHost(firstUrl.getHost(), secondHost);
    }

    public static boolean isSameHost(URI firstUrl, URI secondUrl){
        return isSameHost(firstUrl.getHost(), secondUrl.getHost());
    }
}
