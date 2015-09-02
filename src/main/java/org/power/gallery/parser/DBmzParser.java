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

public class DBmzParser implements Parser {

    private static final Logger LOGGER = LogManager.getLogger(DBmzParser.class);

    // curl -X POST -d 'queryType=rank&pageIndex=1&m=images' 'http://api.xiaojianjian.net/api/dbmeinv.htm'
    public List<Image> parse(String url) throws IOException {
        List<Image> images = new ArrayList<Image>();
        String html = HtmlUtils.getHtml(url);
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("img.height_min");

        for (Element element : elements) {
            String imageUrl = element.attr("src");

            LOGGER.debug("gallery url: {}", imageUrl);
            if (imageUrl != null) {
                String title = element.attr("title");
                String imageId = getImageId(imageUrl);
                imageUrl = imageUrl.replace("/bmiddle/", "/large/");
                String href = element.parent().attr("href");

                Image image = new Image(imageId, imageUrl, title);
                image.setHref(href);
                images.add(image);
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
