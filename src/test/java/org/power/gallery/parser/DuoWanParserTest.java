package org.power.gallery.parser;

import org.junit.Test;
import org.power.gallery.utils.DuoWanUtils;

import static org.junit.Assert.*;

public class DuoWanParserTest {

    @Test public void test() throws Exception {
        DuoWanUtils.updateLatestImagePageID();

        int newPage = DuoWanUtils.getImagePageID();
        String url = "http://tu.duowan.com/scroll/" + newPage + ".html";
        Parser parser = new DuoWanParser();
        parser.parse(url);
    }
}
