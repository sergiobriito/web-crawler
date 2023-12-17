package org.example.utils;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;

import org.bson.Document;
import org.example.configs.Constants;
import org.example.configs.MongoDBConnector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlDownloader {

    private final MongoDBConnector mongoDBConnector;

    public HtmlDownloader(MongoDBConnector mongoDBConnector) {
        this.mongoDBConnector = mongoDBConnector;
    }

    public Elements getLinksFromHtmlFile(String htmlFile){
        File html = new File(htmlFile);
        Elements links = null;
        HashSet<Element> linksToRemove = new HashSet<>();

        try {
            org.jsoup.nodes.Document doc = Jsoup.parse(html, "UTF-8", "");
            links = doc.select("a[href]");

            for (Element link : links) {
                String urlStr = link.attr("href");
                try {
                    new URL(urlStr);
                } catch (MalformedURLException e) {
                    linksToRemove.add(link);
                }
            }

            for (Element linkToRemove : linksToRemove) {
                linkToRemove.remove();
            }

        } catch (IOException ignored) {
        }

        return links;
    };

    public ArrayList<String> getContentMetaData(String content){
        org.jsoup.nodes.Document contentMetaData = Jsoup.parse(content);
        String title = contentMetaData.title();
        Elements metaTags = contentMetaData.select("meta");
        StringBuilder metaData = new StringBuilder();
        for (Element metaTag : metaTags) {
            metaData.append(metaTag.attr("content"));
        }
        ArrayList<String> metadataList = new ArrayList<>();
        metadataList.add(title);
        metadataList.add(metaData.toString());
        return metadataList;
    };

    public void getHtmlContent(String htmlUrl, String fileName) {
        try {
            URL url = new URL(htmlUrl);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();

            ArrayList<String> metaData = getContentMetaData(content.toString());

            Document document = new Document("url", htmlUrl)
                    .append("fileName", fileName)
                    .append("title", metaData.get(0))
                    .append("metaData", metaData.get(1));

            insertDocInDb(document);
            donwloadHtmlFile(fileName, content.toString());

        } catch (IOException ignored) {
        }
    }

    public void donwloadHtmlFile(String fileName, String content) throws IOException {
        String fullPath = Constants.HTML_FILES_PATH + fileName + ".html";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath));
        writer.write(content);
        writer.close();
    }

    public void insertDocInDb(Document document){
        mongoDBConnector.insertData(document);
    };

    public void disconnectFromDb(){
        this.mongoDBConnector.disconnect();
    };
}

