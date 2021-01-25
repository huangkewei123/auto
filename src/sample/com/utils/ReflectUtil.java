package sample.com.utils;

import sample.com.main.controller.HandleController;
import org.apache.commons.beanutils.PropertyUtilsBean;

import javax.annotation.Nullable;
import java.awt.*;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * java反射操作的工具类
 *
 * @author administrator 2013-06-13
 *
 */
public class ReflectUtil {

	/**
	 * 利用反射获取指定对象的指定属性
	 *
	 * @param obj
	 *            目标对象
	 * @param fieldName
	 *            目标属性
	 * @return 目标属性的值
	 */
	public static Object getFieldValue(Object obj, String fieldName) {
		Object result = null;
		Field field = ReflectUtil.getField(obj, fieldName);
		if (field != null) {
			field.setAccessible(true);
			try {
				result = field.get(obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 获取对象的指定属性名的getter方法返回值
	 *
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	public static Object getFieldValueByGetter(Object obj, String fieldName) {
		Object result = null;
		Method getter = getGetter(obj.getClass(), fieldName);
		if (getter != null) {
			try {
				result = getter.invoke(obj, new Object[] {});
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 利用反射获取指定对象里面的指定属性
	 *
	 * @param obj
	 *            目标对象
	 * @param fieldName
	 *            目标属性
	 * @return 目标字段
	 */
	private static Field getField(Object obj, String fieldName) {
		Field field = null;
		for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz
				.getSuperclass()) {
			try {
				field = clazz.getDeclaredField(fieldName);
				break;
			} catch (NoSuchFieldException e) {
				// 这里不用做处理，子类没有该字段可能对应的父类有，都没有就返回null。
			}
		}
		return field;
	}

	/**
	 * 利用反射设置指定对象的指定属性为指定的值
	 *
	 * @param obj
	 *            目标对象
	 * @param fieldName
	 *            目标属性
	 * @param fieldValue
	 *            目标值
	 */
	public static void setFieldValue(Object obj, String fieldName,
									 String fieldValue) {
		Field field = ReflectUtil.getField(obj, fieldName);
		if (field != null) {
			try {
				field.setAccessible(true);
				field.set(obj, fieldValue);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static Method getGetter(Class<?> clasz, String fieldName) {
		String getterName = "get" + fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1);
		try {
			return clasz.getDeclaredMethod(getterName, new Class[] {});
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		return null;
	}

	public static Method findMethod(Class<?> clazz, String name) throws UnsupportedEncodingException {
		return findMethod(clazz, name, new Class[0]);
	}

	public static Method findMethod(Class<?> clazz, String name,
									Class<?> paramTypes[]) throws UnsupportedEncodingException {
//		name = new String(name.getBytes("ISO-8859-1") , "UTF-8");
		ReflectUtil.notNull(clazz, "Class must not be null");
		ReflectUtil.notNull(name, "Method name must not be null");
		String methodName = null;
		for (Class<?> searchType = clazz; searchType != null; searchType = searchType
				.getSuperclass()) {
			Method methods[] = searchType.isInterface() ? searchType
					.getMethods() : searchType.getDeclaredMethods();
			Method amethod[];

			int j = (amethod = methods).length;
			for (int i = 0; i < j; i++) {
				Method method = amethod[i];
//				methodName = method.getName();
//				if (name.equals(method.getName())
//						&& (paramTypes == null || Arrays.equals(paramTypes,
//						method.getParameterTypes())))
//					return method;
				if (name.equals(method.getName())) {
					if(paramTypes == null || Arrays.equals(paramTypes,
							method.getParameterTypes()))
						return method;
				}

			}
		}

		return null;
	}

	public static void notNull(@Nullable Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static <T> T invokeMethod(Method method, Object target) {
		return invokeMethod(method, target, new Object[0]);
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Method method, Object target,
									 Object args[]) {
		try {
			return (T) method.invoke(target, args);
		} catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	public static void handleReflectionException(Exception ex) {
		if (ex instanceof NoSuchMethodException)
			throw new IllegalStateException((new StringBuilder(
					"Method not found: ")).append(ex.getMessage()).toString());
		if (ex instanceof IllegalAccessException)
			throw new IllegalStateException((new StringBuilder(
					"Could not access method: ")).append(ex.getMessage())
					.toString());
		if (ex instanceof InvocationTargetException)
			handleInvocationTargetException((InvocationTargetException) ex);
		if (ex instanceof RuntimeException)
			throw (RuntimeException) ex;
		else
			throw new UndeclaredThrowableException(ex);
	}

	public static void handleInvocationTargetException(
			InvocationTargetException ex) {
		rethrowRuntimeException(ex.getTargetException());
	}

	public static void rethrowRuntimeException(Throwable ex) {
		if (ex instanceof RuntimeException)
			throw (RuntimeException) ex;
		if (ex instanceof Error)
			throw (Error) ex;
		else
			throw new UndeclaredThrowableException(ex);
	}

	//将javabean实体类转为map类型，然后返回一个map类型的值
	public static Map<String, Object> beanToMap(Object obj) {
		Map<String, Object> params = new HashMap<String, Object>(0);
		try {
			PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
			PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
			for (int i = 0; i < descriptors.length; i++) {
				String name = descriptors[i].getName();
				if (!"class".equals(name)) {
					params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}

	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException, AWTException, UnsupportedEncodingException {

//		Class<?> clazz = Class.forName("com.main.controller.HandleController");
//		Method method = clazz.getMethod("keyPressWithWin", Integer.class);
//		HandleController.getRobot();
//		Object obj = method.invoke(clazz.newInstance(), KeyEvent.VK_E);
//		System.out.println(obj);


		Class [] clazz =  new Class[2];
		clazz[0] = String.class;
		clazz[1] = String.class;
		Method method = ReflectUtil.findMethod(HandleController.class ,"mouseLocation"  , clazz);

		System.out.println(method.getName());
		//HandleController.getRobot();
		//报错，参数格式转换错误
		String result = ReflectUtil.invokeMethod(method ,HandleController.class.newInstance() ,  new String[]{"112" , "223"} );
		System.out.println(result);
	}
}