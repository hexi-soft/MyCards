package common;

import java.io.*;

public class UFileReader {
	
	public static void testRead () throws IOException {
		String text = read ("d:\\test_data_ar.sql",1000);
		System.out.print(text);
	}
	
	public static String read (String filepath) {
		String res = "";
		char[] s = new char[4096];
		StringBuffer str_buf = new StringBuffer();
		int len;
	try {
		File file = new File (filepath);
		FileInputStream fis = new FileInputStream (file);
		InputStreamReader buf_rdr;
	    int mark1 = fis.read();
	    if (mark1==-1)
	    	return res;
		int mark2 = fis.read();
		if (mark2==-1){
			char c = (char)mark1;
			res += c;
			return res;
		}
		if(mark1==0xFF && mark2==0xFE){
			buf_rdr = new InputStreamReader(fis,"UTF-16LE");
			while ((len=buf_rdr.read(s))>0){
				str_buf.append(new String(s,0,len));
			}
			return str_buf.toString();
		}
		else if (mark1==0xFE && mark2==0xFF){
			buf_rdr = new InputStreamReader(fis,"UTF-16BE");
			while ((len=buf_rdr.read(s))>0){
				str_buf.append(new String(s,0,len));
			}
			return str_buf.toString();		
		}
		else if (mark1==0xEF && mark2==0xBB){
			int mark3 = fis.read();
			if (mark3==-1){
				res += (char)mark1;
				res += (char)mark2;
				return res;
			}
			if (mark3==0xBF){
			buf_rdr = new InputStreamReader(fis,"UTF-8");
			while ((len=buf_rdr.read(s))>0){
				str_buf.append(new String (s,0,len));
			}
			return str_buf.toString();
			}
		}
		fis.close();
		fis = new FileInputStream(file);
	    buf_rdr = new InputStreamReader(fis);
		while ((len =buf_rdr.read(s))>0){
			str_buf.append(new String(s,0,len));
		}
	}
	catch (UnsupportedEncodingException uee) {
		uee.printStackTrace();
	}
	catch (IOException ioe) {
		ioe.printStackTrace();
	}
		return str_buf.toString();
	}

	public static String read (String filepath,int maxChars) {
		String res = "";
		char[] s = new char[4096];
		int numOfChars = 0;
		StringBuffer str_buf = new StringBuffer();
		int len;
	try {
		File file = new File (filepath);
		FileInputStream fis = new FileInputStream (file);
		BufferedReader buf_rdr;
	    int mark1 = fis.read();
	    if (mark1==-1)
	    	return res;
		int mark2 = fis.read();
		if (mark2==-1){
			char c = (char)mark1;
			res += c;
			return res.substring(0, Math.min(1, maxChars));
		}
		if(mark1==0xFF && mark2==0xFE){
			buf_rdr = new BufferedReader (new InputStreamReader(fis,"UTF-16LE"));
			while (numOfChars<maxChars && (len=buf_rdr.read(s))>0){
				str_buf.append(new String(s,0,len));
				numOfChars += len;
			}
			return str_buf.substring(0, Math.min(numOfChars, maxChars));
		}
		else if (mark1==0xFE && mark2==0xFF){
			buf_rdr = new BufferedReader (new InputStreamReader(fis,"UTF-16BE"));
			while (numOfChars < maxChars && (len=buf_rdr.read(s))>0){
				str_buf.append(new String(s,0,len));
				numOfChars += len;
			}
			return str_buf.substring(0, Math.min(numOfChars, maxChars));		
		}
		else if (mark1==0xEF && mark2==0xBB){
			int mark3 = fis.read();
			if (mark3==-1){
				res += (char)mark1;
				res += (char)mark2;
				return res.substring(0,Math.min(2, maxChars));
			}
			if (mark3==0xBF){
			buf_rdr = new BufferedReader (new InputStreamReader(fis,"UTF-8"));
			while (numOfChars < maxChars && (len=buf_rdr.read(s))>0){
				str_buf.append(new String (s,0,len));
				numOfChars += len;
			}
			return str_buf.substring(0, Math.min(numOfChars, maxChars));
			}
		}
		fis.close();
		fis = new FileInputStream(file);
	    buf_rdr = new BufferedReader (new InputStreamReader(fis));
		while (numOfChars<maxChars &&(len =buf_rdr.read(s))>0){
			str_buf.append(new String(s,0,len));
			numOfChars += len;
		}
	}
	catch (UnsupportedEncodingException uee) {
		uee.printStackTrace();
	}
	catch (IOException ioe) {
		ioe.printStackTrace();
	}
	return str_buf.substring(0, Math.min(numOfChars, maxChars));
	}
	
	public static String fix_number(int n){
		String r = Integer.toString(n);
		if (n<10)
			r = "000"+n;
		else if(n<100)
			r = "00"+n;
		else if(n<1000)
			r = "0"+n;
		return r;
	}
	
	public static String read_recursively(String dir, String ext) {
		String res = "";
		File file = new File(dir);
		File[] fs = file.listFiles();
		for(File f : fs ) {
			if (f.isDirectory()) {
				res += read_recursively(f.getAbsolutePath(), ext);
			}else if (f.getName().endsWith(ext)) {
				String s = read(f.getAbsolutePath());
				if (s.length()>0) {
					res += s + "\r\n";
				}
			}
		}
		return res;
	}
	
	public static void main(String[] args){
		String s = read_recursively("d:\\tmp", ".txt");
		Log.d(s);
	}
}
