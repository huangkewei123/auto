package sample.com.utils.ThreadConinfguration;

import sample.com.utils.ReflectUtil;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 用于多线程池处理汇付响应(同时粒化MVCC控制),有返回结果,不获取
 * @author minz
 *
 */
public class CallableTask implements Callable<Object>{
	private String targetName;
	private String methodName;
	private Object args[];
	private Class<?> paramTypes[];
	
	public CallableTask(String targetName, String methodName, Object... args) {
		this.targetName =  targetName;
		this.methodName =  methodName;
		this.args =  args;
		if(args!=null && args.length > 0){
			paramTypes = new Class[args.length];
			int i = 0;
			for (Object obj : args) {
				paramTypes[i] = obj.getClass();
				i++;
			}
		}else
			paramTypes = new Class[0];
	}

	@Override
	public Object call()  throws Exception {
		Class target = Class.forName(targetName);
		Method method = ReflectUtil.findMethod(target , methodName, paramTypes);
		return ReflectUtil.invokeMethod(method, target.newInstance(), args);
	}

	static volatile private ReentrantLock lock = new ReentrantLock();

	public static void main(String[] args) throws InterruptedException, ExecutionException {
//		String [] s = {"automation"};
		Future<Object> f = ThreadConfiguration.THREAD_POOL.submit(new CallableTask("com.main.controller.HandleController", "cut", null));
		System.out.println(f.get());////不对Future结果进行操作不能串行化
		System.out.println("--------------");
		System.out.println(f.isDone());
//		System.out.println(f.cancel(true));
		Thread ta[] = new Thread[Thread.activeCount()];
		Thread.enumerate(ta);
		System.out.println("Active thread's number:"+ta.length);
		for(Thread t:ta){
			synchronized (t){
				if(t!=null){
					System.out.println("Thread's name:"+t.getName()+" id:"+t.getId());
//					lock.lock();//只有一个线程可以执行下面的代码
					t.wait();
//					lock.unlock();
				}
			}

		}
//		System.out.println("暂停开");
//		lock.lock();//只有一个线程可以执行下面的代码
//		//线程池等待执行 3 ms
//		ThreadConfiguration.THREAD_POOL.awaitTermination(5000, TimeUnit.MILLISECONDS);
//		System.out.println("暂停");
//		lock.unlock();
//		ThreadConfiguration.THREAD_POOL.shutdown();
	}
}
