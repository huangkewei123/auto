package sample.com.utils.ThreadConinfguration;

import java.util.ResourceBundle;

public class Configuration {

	private static ResourceBundle rb = null;
	private static final String CONFIG_FILE = "config";
	
	private static class ConfigurationInstance { 
	    private static final Configuration instance = new Configuration(); 
	} 
	public static Configuration getInstance(){
		return ConfigurationInstance.instance;
	}
	
	private Configuration() {
		rb = ResourceBundle.getBundle(CONFIG_FILE);
	}
	
	
	public String getValue(String key) {
		if(rb.containsKey(key))
			return rb.getString(key);
		return null;
	}

	public Integer getIntValue(String key) {
		if(rb.containsKey(key))
			return  Integer.parseInt(rb.getString(key));
		return null;
	}
}