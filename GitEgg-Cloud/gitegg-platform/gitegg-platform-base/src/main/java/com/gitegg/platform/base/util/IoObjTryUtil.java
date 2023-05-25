package com.gitegg.platform.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.Flushable;

public class IoObjTryUtil {
    private final static Logger log = LoggerFactory
            .getLogger(IoObjTryUtil.class);

    public static void tryClose(Closeable obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (Exception e) {
                log.error("error:", e);
            }
        }
    }

    public static void tryFlush(Flushable obj) {
        if (obj != null) {
            try {
                obj.flush();
            } catch (Exception e) {
                log.error("error:", e);
            }
        }
    }
}
