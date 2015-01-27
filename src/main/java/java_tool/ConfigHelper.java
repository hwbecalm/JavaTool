/** 
 * 读取配置文件
 */
package java_tool;

//import java.io.File;
//import java.net.URL;
import java.util.List;

import org.apache.commons.configuration.*;

public class ConfigHelper {
	public ConfigHelper(){
	}
	/**
	 * 读取配置文件
	 * @param strfile
	 * @return
	 */
	public static Configuration getConfig(String strfile){
		Configuration config = null;
		try {
			config = new XMLConfiguration(strfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}

	public static void main(String[] args) {
		try{
			Configuration config = getConfig("config.xml");
			
			List startCriteria = config.getList("start-criteria.criteria");     
			int horsepower = config.getInt("horsepower");   
			System.out.println( "Start Criteria: " + startCriteria );   
			System.out.println(horsepower);   
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
