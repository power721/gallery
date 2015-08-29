package org.power.image.api;

import com.jfinal.rest.API;
import com.jfinal.rest.GET;
import com.jfinal.rest.RestController;
import org.power.image.parser.DBmzParser;
import org.power.image.parser.HahaParser;
import org.power.image.parser.Parser;
import org.power.image.parser.QiushibaikeParser;

import java.io.IOException;

@API("/images")
public class ImageController extends RestController {

    @GET
    public void index() {
        int page = 1;
        if (getPara() != null) {
            page = getParaToInt();
        }

        String url = "http://m2.qiushibaike.com/article/list/imgrank?count=20&rqcnt=4&page=" + page + "&r=" + System.currentTimeMillis();
        Parser parser = new QiushibaikeParser();

        setAttr("page", page);
        try {
            setAttr("images", parser.parse(url));
        } catch (IOException e) {
            setAttr("error", e.getMessage());
        }
    }

    @GET
    @API("/db/images")
    public void db() {
        int page = 1;
        if (getPara() != null) {
            page = getParaToInt();
        }

        String url = "http://www.dbmeinv.com/?pager_offset=" + page + "&r=" + System.currentTimeMillis();
        Parser parser = new DBmzParser();

        setAttr("page", page);
        try {
            setAttr("images", parser.parse(url));
        } catch (IOException e) {
            setAttr("error", e.getMessage());
        }
    }

    @GET
    @API("/haha/images")
    public void haha() {
        int page = 1;
        if (getPara() != null) {
            page = getParaToInt();
        }

        String url = "http://www.haha.mx/pic/new/" + page + "?r=" + System.currentTimeMillis();
        Parser parser = new HahaParser();

        setAttr("page", page);
        try {
            setAttr("images", parser.parse(url));
        } catch (IOException e) {
            setAttr("error", e.getMessage());
        }
    }

}
