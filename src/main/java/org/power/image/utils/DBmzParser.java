package org.power.image.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DBmzParser implements Parser {

    private static final Logger LOGGER = LogManager.getLogger(DBmzParser.class);

    public List<Image> parse(String url) throws IOException {
        List<Image> images = new ArrayList<Image>();
        String html = HtmlUtils.getHtml(url);
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("img.height_min");

        for (Element image : elements) {
            String imageUrl = image.attr("src");

            LOGGER.debug("image url: {}", imageUrl);
            if (imageUrl != null) {
                String title = image.attr("title");
                String imageId = getImageId(imageUrl);
                imageUrl = imageUrl.replace("/bmiddle/", "/large/");

                images.add(new Image(imageId, imageUrl, title));
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
