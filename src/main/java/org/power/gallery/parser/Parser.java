package org.power.gallery.parser;

import org.power.gallery.bean.Image;

import java.io.IOException;
import java.util.List;


public interface Parser {

    public List<Image> parse(String url) throws IOException;

}
