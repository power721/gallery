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

public class HahaParser implements Parser {

    private static final Logger LOGGER = LogManager.getLogger(HahaParser.class);

    public List<Image> parse(String url) throws IOException {
        List<Image> images = new ArrayList<Image>();
        String html = HtmlUtils.getHtml(url);
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("#joke-grid img");

        for (Element element : elements) {
            String imageUrl = element.attr("src");

            LOGGER.debug("gallery url: {}", imageUrl);
            if (imageUrl != null) {
                if (imageUrl.contains("/pic_ad/")) {
                    continue;
                }

                String title = element.attr("alt");
                String imageId = getImageId(imageUrl);
                imageUrl = imageUrl.replace("/normal/", "/middle/");
                String href = "http://www.haha.mx" + element.parent().attr("href");
                if (href.contains("/joke/")) {
                    Image image = new Image(imageId, imageUrl, title);
                    image.setHref(href);
                    images.add(image);
                }
            }
        }

        LOGGER.info("Get {} images url", images.size());
        return images;
    }

    private String getImageId(String imageUrl) {
        int start = imageUrl.lastIndexOf('/');
        int end = imageUrl.lastIndexOf('.');

        return imageUrl.substring(start + 1, end);
    }

}
