package sample.com.utils.ThreadConinfguration;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadConfiguration implements Serializable {

    /**
     * 线程池最大化数量
     */
    public static final Integer THREAD_POOL_COUNT = Configuration.getInstance().getIntValue("thread_pool_count");

    public final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(THREAD_POOL_COUNT);
    public final static String USER = Configuration.getInstance().getValue("user");
    public final static String PASSWORD = Configuration.getInstance().getValue("password");
    public final static String URL = Configuration.getInstance().getValue("url");

    public static List<Future<String>> TASKS = new ArrayList<Future<String>>();
}
