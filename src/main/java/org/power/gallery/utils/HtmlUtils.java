package org.power.gallery.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public final class HtmlUtils {

    private static final Logger LOGGER = LogManager.getLogger(HtmlUtils.class);

    public static String getHtml(final String url, final String encoding) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);

        LOGGER.info("Executing request {}", httpget.getRequestLine());

        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            public String handleResponse(final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity, encoding) : null;
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

    public static byte[] getBytes(final String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);

        LOGGER.info("Executing request {}", httpget.getRequestLine());

        // Create a custom response handler
        ResponseHandler<byte[]> responseHandler = new ResponseHandler<byte[]>() {

            public byte[] handleResponse(final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    byte[] buffer = new byte[1024 * 1024];
                    for (Header header : response.getAllHeaders()) {
                        LOGGER.info("{} = {}", header.getName(), header.getValue());
                    }
                    IOUtils.read(entity.getContent(), buffer);
                    //                    IOUtils.readFully(entity.getContent(), buffer);
                    return buffer;
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

    public static String getHtml(final String url) throws IOException {
        return getHtml(url, "UTF-8");
    }

}
