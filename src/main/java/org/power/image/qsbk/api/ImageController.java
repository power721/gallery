package org.power.image.qsbk.api;

import com.jfinal.rest.API;
import com.jfinal.rest.GET;
import com.jfinal.rest.RestController;
import org.power.image.qsbk.utils.Parser;
import org.power.image.qsbk.utils.QiushibaikeParser;

import java.io.IOException;

@API("/images")
public class ImageController extends RestController {

    @GET
    public void index() {
        int page = 1;
        if (getPara() != null) {
            page = getParaToInt();
        }

        String url = "http://m2.qiushibaike.com/article/list/imgrank?count=20&page=" + page;
        Parser parser = new QiushibaikeParser();

        try {
            setAttr("images", parser.parse(url));
        } catch (IOException e) {
            setAttr("error", e.getMessage());
        }
    }

}
