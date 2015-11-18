package org.power.gallery.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.power.gallery.bean.Image;
import org.power.gallery.utils.DuoWanUtils;
import org.power.gallery.utils.LRUCache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DuoWanParser implements Parser {

    private static final Logger LOGGER = LogManager.getLogger(DuoWanParser.class);
    private static final String BASE_URL = "http://tu.duowan.com/";

    private static LRUCache<String, List<Image>> cache = new LRUCache<>(50);

    @Override public List<Image> parse(String url) throws IOException {
        if (cache.containsKey(url)) {
            return cache.get(url);
        }

        List<Image> images = new ArrayList<>();
        String pageUrl = url;

        while (true) {
            String html = DuoWanUtils.getHtml4DuoWan(pageUrl);
            //        LOGGER.info(html);
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select(".pic-box img");

            for (Element element : elements) {
                String imageUrl = element.attr("src");

                LOGGER.debug("gallery url: {}", imageUrl);
                if (imageUrl != null) {
                    String imageId = getImageId(imageUrl);
                    String title = element.parent().nextElementSibling().text();
                    Image image = new Image(imageId, imageUrl, title);
                    image.setHref(imageUrl);
                    images.add(image);
                    //                LOGGER.info(image);
                }
            }

            elements = doc.select(".mod-page .current");
            if (elements.isEmpty()) {
//                LOGGER.info("break {}", pageUrl);
                break;
            }
            Element element = elements.get(0).nextElementSibling();
            if (element == null) {
//                LOGGER.info("break next {}", pageUrl);
                break;
            }
            pageUrl = BASE_URL + element.attr("href");
            LOGGER.info("get next {}", pageUrl);
        }

        LOGGER.info("Get {} images url", images.size());
        cache.put(url, images);
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
