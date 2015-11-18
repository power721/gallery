package org.power.gallery.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DuoWanUtils {

    private static final Logger LOGGER = LogManager.getLogger(DuoWanUtils.class);
    private static final String BASE_URL = "http://tu.duowan.com/tu";
    private static final Pattern PATTERN = Pattern.compile("http://tu.duowan.com/scroll/(\\d+).html");
    private static int latestImagePageID = 100000;
    private static long lastUpdateTime = -1L;

    public static int getImagePageID() {
        return latestImagePageID;
    }

    public static void updateLatestImagePageID() {
        long time = System.currentTimeMillis();
        if (time - lastUpdateTime > 3600 * 1000L) {
            int imageID = getLatestImagePageID();
            if (imageID > 0) {
                latestImagePageID = imageID;
                lastUpdateTime = time;
            }
        }
    }

    public static int getLatestImagePageID() {
        String html;
        String url = null;
        int imageID = 0;

        try {
            html = HtmlUtils.getHtml(BASE_URL);

            //            LOGGER.info(html);
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select(".box em a[href]");

            LOGGER.info(elements.size());
            for (Element element : elements) {
                String href = element.attr("href");
                if (href.startsWith("http://tu.duowan.com/g/")) {
                    url = href;
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.error("get latest image URL from {} failed!", BASE_URL, e);
        }

        try {
            String location = getLocation(url);
            LOGGER.info(location);
            Matcher matcher = PATTERN.matcher(location);
            if (matcher.matches()) {
                imageID = Integer.parseInt(matcher.group(1));
            }
        } catch (IOException e) {
            LOGGER.error("get image URL from {} failed!", url, e);
        }

        return imageID;
    }

    private static String getLocation(String url) throws IOException {
        final StringHolder stringHolder = new StringHolder(url);
        CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(new RedirectStrategy() {
            @Override public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
                throws ProtocolException {
                int status = response.getStatusLine().getStatusCode();
                return status == 301 || status == 302;
            }

            @Override public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
                throws ProtocolException {
                String location = response.getFirstHeader("Location").getValue().replace("gallery", "scroll");
                stringHolder.setValue(location);
                return new HttpGet(location);
            }
        }).build();
        HttpGet httpget = new HttpGet(url);

        LOGGER.info("Executing request {}", httpget.getRequestLine());

        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            public String handleResponse(final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    return null;
                } else if (status >= 500 && status <= 599) {
                    throw new ServerSideException("Unexpected response status: " + status);
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }

        };

        try {
            httpClient.execute(httpget, responseHandler);
        } catch (ServerSideException e) {
            LOGGER.warn("Parse {} failed: {}, retrying...", url, e.getMessage());
            httpClient.execute(httpget, responseHandler);
        } finally {
            IOUtils.closeQuietly(httpClient);
        }
        return stringHolder.getValue();
    }

    public static String getHtml4DuoWan(final String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(new RedirectStrategy() {
            @Override public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
                throws ProtocolException {
                int status = response.getStatusLine().getStatusCode();
                return status == 301 || status == 302;
            }

            @Override public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
                throws ProtocolException {
                return new HttpGet(response.getFirstHeader("Location").getValue().replace("gallery", "scroll"));
            }
        }).build();
        HttpGet httpget = new HttpGet(url);

        LOGGER.info("Executing request {}", httpget.getRequestLine());

        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            public String handleResponse(final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity, "UTF-8") : null;
                } else if (status >= 500 && status <= 599) {
                    throw new ServerSideException("Unexpected response status: " + status);
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }

        };

        try {
            return httpClient.execute(httpget, responseHandler);
        } catch (ServerSideException e) {
            LOGGER.warn("Parse {} failed: {}, retrying...", url, e.getMessage());
            return httpClient.execute(httpget, responseHandler);
        } finally {
            IOUtils.closeQuietly(httpClient);
        }
    }

    private static class StringHolder {
        private String value;

        public StringHolder() {
        }

        public StringHolder(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
