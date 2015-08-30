package org.power.gallery.api;

import com.jfinal.rest.API;
import com.jfinal.rest.GET;
import com.jfinal.rest.RestController;
import org.power.gallery.parser.DBmzParser;
import org.power.gallery.parser.HahaParser;
import org.power.gallery.parser.Parser;
import org.power.gallery.parser.QiushibaikeParser;

import java.io.IOException;

@API("/")
public class ImageController extends RestController {

    @GET
    @API("/qsbk/images")
    public void qsbk() {
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
    @API("/dbmz/images")
    public void dbmz() {
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
