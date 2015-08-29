package org.power.image.parser;

import org.power.image.bean.Image;

import java.io.IOException;
import java.util.List;


public interface Parser {

    public List<Image> parse(String url) throws IOException;

}
