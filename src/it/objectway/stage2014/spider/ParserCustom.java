package it.objectway.stage2014.spider;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by michele-macbookair on 28/04/14.
 */
public class ParserCustom {
    private Document doc;
    private Logger log = Logger.getLogger("it.objectway.stage2014.spider.ParserCustom");

    public ParserCustom(URL url) throws IOException {
        doc = Jsoup.connect(String.valueOf(url)).get();
        Spider.msg("parser inizialized", log);
    }

    public ParserCustom(String localFilePath, URL url) throws IOException {
        File input = new File(localFilePath);
        doc = Jsoup.parse(input, "UTF-8", String.valueOf(url));
    }
    
    public List<String> getSourcesAndLinks(){
        String link;
        List<String> links = new ArrayList<String>();
        Elements newsHeadlines = doc.select("[src],[href]");
        for (Element item : newsHeadlines) {
            link = item.hasAttr("src") ? item.attr("src") : item.attr("href");
            links.add(link);
        }

        Spider.msg("Found links: " + String.valueOf(links.size()), log);
        return links;
    }

    public void convertLinks(URL currentUrl){
        String link;
        Elements newsHeadlines = doc.select("[src],[href]");
        for (Element item : newsHeadlines) {
            link = item.hasAttr("src") ? item.absUrl("src") : item.absUrl("href");
            // current = http://www.google.it/ciao/index.html
            // link = http://www.google.it/salve/index.html
            // newlink = ../salve/index.html

            URI uriLink = URI.create(link);
            boolean isInternalLink = uriLink.getHost().equals(currentUrl.getHost());
            String newLink = ".";
            if(isInternalLink){
                newLink += uriLink.getPath();
            }else{
                newLink += UrlStringEditor.relativizeExternalUrl(uriLink.toString());
            }

            if(item.hasAttr("src")){
                item.attr("src", newLink);
            }else{
                item.attr("href", newLink);
            }
        }
    }
}
