package org.power.gallery.utils;

import java.io.IOException;

/**
 * Created by power on 7/26/15.
 */
public class ServerSideException extends IOException {
    public ServerSideException() {
    }

    public ServerSideException(String message) {
        super(message);
    }

    public ServerSideException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerSideException(Throwable cause) {
        super(cause);
    }
}
