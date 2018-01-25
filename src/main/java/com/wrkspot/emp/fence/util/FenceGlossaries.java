package com.wrkspot.emp.fence.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Debasish on 21-Jan-18 6:20 PM
 */
public class FenceGlossaries {
    private static  final Logger LOGGER = LoggerFactory.getLogger(FenceGlossaries.class);
    public static boolean isObjectEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else if (obj instanceof CharSequence) {
            return ((CharSequence)obj).length() == 0;
        } else if (obj instanceof Collection) {
            return ((Collection)obj).isEmpty();
        } else {
            return obj instanceof Map ? ((Map)obj).isEmpty() : false;
        }
    }

    public static Properties getProperties(){
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File("src/main/resources/application.properties")));
        } catch (Exception e) {
            LOGGER.error("Error getting properties. ", e.getMessage());
        }
        return properties;
    }
}
