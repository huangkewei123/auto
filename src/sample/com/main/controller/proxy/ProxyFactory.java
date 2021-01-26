package sample.com.main.controller.proxy;

import sample.com.main.controller.service.HandleService;
import sample.com.utils.LoggerUtils;
import sample.com.utils.StringUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Cglib子类代理工厂
 * 对类在内存中动态构建一个子类对象
 */
public class ProxyFactory implements MethodInterceptor {
    //维护目标对象
    private Object target;

    //获取操作界面的标准延时
    public static String nomalDelay;

    public ProxyFactory(Object target) {
        this.target = target;
    }

    //给目标对象创建一个代理对象
    public Object getProxyInstance(){
        //1.工具类
        Enhancer en = new Enhancer();
        //2.设置父类
        en.setSuperclass(target.getClass());
        //3.设置回调函数
        en.setCallback(this);
        //4.创建子类(代理对象)
        return en.create();

    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
//        System.out.println("开始事务...");
        LoggerUtils.info(ProxyFactory.class ,"正在调用方法：" + method.getName());
        nomalDelay = StringUtils.isEmpty(nomalDelay) ? "0" : nomalDelay;
        Integer delay = Integer.parseInt(nomalDelay);
        //监听的方法全部延迟5秒操作
        if(delay > 0){
            HandleService.getRobot().delay(delay * 1000);
        }

        //执行目标对象的方法
        Object returnValue = method.invoke(target, args);
//        System.out.println("提交事务..." + returnValue);

        return returnValue;
    }
}