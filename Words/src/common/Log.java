package common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log {
	public static Logger log = Logger.getLogger("spiderLog");
	static File myLog = new File("myLog.txt");
	static FileOutputStream fos = null;
	
	//public static Logger log = Logger.getRootLogger();
	static{
		PropertyConfigurator.configure("log.properties");
		try {
			fos = new FileOutputStream(myLog);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void a(Object msg){
		try {
			fos.write((msg.toString()+"\r\n").getBytes());
			fos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void i(Object msg){
			log.info(msg);
	}
	
	public static void e(Object msg){
		log.error(msg);
	}
	
	public static void f(Object msg){
		log.fatal(msg);
	}
	
	public static String joinStrings(String[] strings) {
    	String r = "";
    	for (String s : strings) {
    		r += s + " ";
    	}
    	return r;
	}
	
	public static String joinStrings(String[] strings, String delimiter) {
    	String r = "";
    	for (String s : strings) {
    		r += s + delimiter;
    	}
    	if (!r.isEmpty()) {
    		r = r.substring(0, r.length()-delimiter.length());
    	}
    	return r;
	}
	
	public static void debug(Object msg){
		StackTraceElement[] temp=Thread.currentThread().getStackTrace();
		String className = temp[2].getClassName();
		StackTraceElement a=(StackTraceElement)temp[2];
		String method = a.getMethodName();
		log.warn(className+"."+method+": "+msg);
	}
	
	public static void d(Object msg){
		log.debug(msg);
	}
	
	public static void p(Object msg){
		System.out.println(msg);
	}
	
	public static void l(Object msg){
		System.out.println(msg);
	}
	
	public static void print_map(Map<String,Object> map) {
		System.out.println("properties: "+map.keySet().size());
		for(String key : map.keySet()){
			Object value = map.get(key);
			System.out.println(key+": "+value);
		}
	}
	
	public static void main(String[] args){
		log.debug("debug");
		log.info("info");
		log.warn("warn");
		log.error("error");
		log.fatal("fatal");
		debug("o");
	}
}
