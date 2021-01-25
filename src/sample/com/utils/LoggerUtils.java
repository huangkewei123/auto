package sample.com.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtils {

    private volatile static Logger logger;

    private LoggerUtils (){}

    public static Logger getSingleton(Class clazz) {
        if (logger == null) {
            synchronized (Logger.class) {
                if (logger == null) {
                    logger = LoggerFactory.getLogger(clazz);
                }
            }
        }
        return logger;
    }


    /**
     *
     * @param str
     */
    public static void warning(Class clazz,String str){
        logger = getSingleton(clazz);
        logger.warn(str);
    }

    /**
     *
     * @param str
     */
    public static void info(Class clazz,String str){
        logger = getSingleton(clazz);
        logger.info(str);
    }

    /**
     *
     * @param str
     */
    public static void error(Class clazz,String str){
        logger = getSingleton(clazz);
        logger.error(str);
    }

    /**
     *
     * @param str
     * @param exc
     */
    public static void error(Class clazz,String str, Throwable exc){
        logger = getSingleton(clazz);
        logger.error(str , exc);
    }

}
