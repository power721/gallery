package org.power.image.qsbk.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QiushibaikeParser implements Parser {

    private static final Logger LOGGER = LogManager.getLogger(Parser.class);

    public List<Image> parse(String url) throws IOException {
//        List<String> imagesUrl = new ArrayList<String>();
        List<Image> images = new ArrayList<Image>();
        String html = HtmlUtils.getHtml(url);

        JSONParser parser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) parser.parse(html);
        } catch (ParseException e) {
            throw new IOException("parse JSON failed", e);
        }

        JSONArray items = (JSONArray) jsonObject.get("items");

        LOGGER.debug("count: " + jsonObject.get("count"));
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = (JSONObject) items.get(i);
            String image = (String) item.get("image");
            String imageId = String.valueOf(item.get("id"));
            String prefix = imageId.substring(0, imageId.length() - 4);
            String content = (String) item.get("content");

            String imageUrl = "http://pic.qiushibaike.com/system/pictures/" + prefix + "/" + imageId + "/medium/" + image;
//            imagesUrl.add(imageUrl);
            LOGGER.debug(imageUrl);
            images.add(new Image(imageUrl, content));
        }

        LOGGER.info("Get {} images url", images.size());
        return images;
    }

}
