package org.power.image.utils;

import java.io.IOException;
import java.util.List;


public interface Parser {

    public List<Image> parse(String url) throws IOException;

}
