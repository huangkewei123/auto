package sample.com.utils;
 
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
 
public class ExpressionChanged {
	private static final String AND ="&&";
	private static final String OR ="||";
	private static final String UNEQUAL = "!=";
	private static final String EQUAL = "==";
	
	private static Map<String, String> map;
	private static Map<String, String> params;
    public static boolean isEnable(String enable, Map<String, String> map,
                            Map<String,String> params){
    	map = map;
    	params = params;
        if(StringUtils.isEmpty(enable)){
            return true;
        }
        
        Stack<String> operation = new Stack<String>();
        int tmpEnd = enable.length();
        for(int i = enable.length();i>1 ; i--){
        	String twoChatter = enable.substring(i-2,i);
        	if (twoChatter.equals(AND)||twoChatter.equals(OR)) {
            	operation.add(enable.substring(i, tmpEnd).trim().replaceAll(" ", ""));
            	operation.add(twoChatter);
            	tmpEnd = i-2;
            }
        }
        operation.add(enable.substring(0,tmpEnd).replaceAll(" ", ""));
        System.out.println(operation);
        return judge(operation);
    }
    private static boolean judge(Stack<String> operation) {
    	if(operation.size()==1) {
    		return isTrue(operation.pop());
    	}else {
    		boolean value1 = isTrue(operation.pop());
    		String releation = operation.pop();
    		boolean value2 = isTrue(operation.pop());
    		if (releation.equals(AND)) {
    			boolean operationResult =  value1 && value2;
    			operation.add(String.valueOf(operationResult));
    			return judge(operation);
    		}
    		else if(releation.equals(OR)) {
    			boolean operationResult =  value1 || value2;
    			operation.add(String.valueOf(operationResult));
    			return judge(operation);
    		}
    		else {
    			//LOG.error("the logical is wrong")
    			return true;
    		}
    	}
    	
    }
    
 
    private static boolean isTrue(String condition) {
		if(condition.toLowerCase().equals("true")) {
			return true;
		}else if(condition.toLowerCase().equals("false")) {
			return false;
		}else {
			if(condition.contains(EQUAL)) {
				int index = condition.indexOf(EQUAL);
				String stringOfValue1 = condition.substring(0, index);
				String stringOfValue2 = condition.substring(index+2,condition.length());
				
				Object value1 = getValuFromString(stringOfValue1,params,map);
				Object value2 = getValuFromString(stringOfValue2,params,map);
				System.out.println(stringOfValue1+":"+value1);
				System.out.println(stringOfValue2+":"+value2);
				if(value1==null || value2 ==null) {
					//LOG.error();
					return false;
				}
				return value1.equals(value2);
			}
			else if (condition.contains(UNEQUAL)) {
				int index = condition.indexOf(UNEQUAL);
				String stringOfValue1 = condition.substring(0, index);
				String stringOfValue2 = condition.substring(index+2,condition.length());
			
				Object value1 = getValuFromString(stringOfValue1,params,map);
				Object value2 = getValuFromString(stringOfValue2,params,map);
				System.out.println(stringOfValue1+":"+value1);
				System.out.println(stringOfValue2+":"+value2);
				if(value1==null || value2 ==null) {
					//LOG.error();
					System.out.println("wrong");
					return false;
				}
				return !value1.equals(value2);
			}
			else {
				System.out.println("wrong");
				return false;
			}
		}
		
	}
	private static Object getValuFromString(String string, Map<String, String> params, Map<String, String> map) {
		// TODO 这个函数主要是根据configString的知道，从其他参数里面获取想要的Value
		String POINT ="\\.";
		String PARAMS ="params";
		String MAP ="map";
		String result = null;
        if (!StringUtils.isEmpty(string) && string.split(POINT).length == 1) {
            result = string;
        } else if (!StringUtils.isEmpty(string) &&
				string.split(POINT).length == 2) {
            String source = string.split(POINT)[0];
            String value = string.split(POINT)[1];
            if (source.equals(PARAMS)) {
                result = params.get(value);
            } else if (source.equals(MAP)) {
                result = map.get(value);
            } 
        }
        return result;
	}
	public static void main(String[] args) {
    	
    	Map<String ,String> map =new HashMap<>();
    	Map<String ,String> params =new HashMap<>();
    	map.put("name", "kangyucheng");
    	params.put("name", "kangyucheng");
    	map.put("id", "https://blog.csdn.net/kangyucheng");
    	params.put("id", "https://blog.csdn.net/kangyucheng");
    	
    	ExpressionChanged getEnableService =new ExpressionChanged();
//    	String string1 ="params.name == map.name || params.id != map.id";
//    	System.out.println(getEnableService.isEnable(string1,map,params));
//    	String string2 ="params.name == map.id || params.id != map.id";
//    	System.out.println(getEnableService.isEnable(string2,map,params));
//    	String string3 ="params.name == map.id || params.id != true";
//    	System.out.println(getEnableService.isEnable(string3,map,params));

		String string4 ="1 == 1";
		System.out.println(getEnableService.isEnable(string4,null,null));
    }
 
}