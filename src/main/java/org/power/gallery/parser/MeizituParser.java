package org.power.gallery.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.power.gallery.bean.Image;
import org.power.gallery.utils.HtmlUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MeizituParser implements Parser {

    private static final Logger LOGGER = LogManager.getLogger(MeizituParser.class);

    private int page;

    public MeizituParser(int page) {
        this.page = page - 1;
    }

    // curl -X POST -d 'queryType=rank&pageIndex=1&m=images' 'http://api.xiaojianjian.net/api/dbmeinv.htm'
    public List<Image> parse(String url) throws IOException {
        List<Image> images = null;
        String html = HtmlUtils.getHtml(url, "GB2312");
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select(".pic img");

        int i = 0;
        for (Element element : elements) {
            if (page % 30 == i) {
                String imageUrl = element.attr("src");

                LOGGER.debug("gallery url: {}", imageUrl);
                if (imageUrl != null) {
                    String href = element.parent().attr("href");

                    images = getImages(href);
                }
                break;
            }
            i++;
        }

        return images;
    }

    private List<Image> getImages(String url) throws IOException {
        List<Image> images = new ArrayList<Image>();
        String html = HtmlUtils.getHtml(url, "GB2312");
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("#picture img");

        for (Element element : elements) {
            String imageUrl = element.attr("src");

            LOGGER.debug("gallery url: {}", imageUrl);
            if (imageUrl != null) {
                String title = element.attr("alt");
                String imageId = getImageId(imageUrl);

                Image image = new Image(imageId, imageUrl, title);
                images.add(image);
            }
        }

        LOGGER.info("Get {} images url", images.size());
        return images;
    }

    private String getImageId(String imageUrl) {
        int start = imageUrl.lastIndexOf('/');
        int end = imageUrl.lastIndexOf('.');
        String[] parts = imageUrl.substring(0, end).split("/");
        int n = parts.length;

        return parts[n - 4] + parts[n - 3] + parts[n - 2] + parts[n - 1];
    }

}
