package org.example;


import org.example.configs.Constants;
import org.example.configs.MongoDBConnectorImpl;
import org.example.utils.HtmlDownloader;
import org.jsoup.select.Elements;


public class Main {
    public static void main(String[] args) {

        String domain = "https://www.google.com.br/";
        String domainName = "google";

        HtmlDownloader htmlDownloader = new HtmlDownloader(new MongoDBConnectorImpl());
        htmlDownloader.getHtmlContent(domain, domainName);

        String htmlFile = Constants.HTML_FILES_PATH + domainName + ".html";
        Elements htmlLinks = htmlDownloader.getLinksFromHtmlFile(htmlFile);

        System.out.println("Processing...");

        for (int i = 0; i < htmlLinks.size(); i++) {
            String url = htmlLinks.get(i).attr("href");
            String fileName = domainName + "_file_" + (i + 1);
            htmlDownloader.getHtmlContent(url, fileName);
        };

        htmlDownloader.disconnectFromDb();
        System.out.println("Completed!");

    }
}