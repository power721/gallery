package org.power.gallery.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.power.gallery.bean.Image;
import org.power.gallery.utils.HtmlUtils;
import org.power.gallery.utils.LRUCache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FaceksParser implements Parser {

    private static final Logger LOGGER = LogManager.getLogger(FaceksParser.class);

    private static LRUCache<String, String> cache = new LRUCache<String, String>(10);

    private int page;

    public FaceksParser(int page) {
        this.page = page - 1;
    }

    public List<Image> parse(String url) throws IOException {
        List<Image> images = null;
        String html = cache.get(url);
        if (html == null) {
            html = HtmlUtils.getHtml(url, "GB2312");
            cache.put(url, html);
        }
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
        String html = HtmlUtils.getHtml(url);
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select(".ctc img");

        for (Element element : elements) {
            String imageUrl = element.attr("src");

            LOGGER.debug("gallery url: {}", imageUrl);
            if (imageUrl != null) {
                String title = doc.select(".text p").text();
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
        if (end == -1) {
            end = imageUrl.length();
        }

        return imageUrl.substring(start + 1, end);
    }

}
