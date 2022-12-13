package common;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	 public static String md5s (String plainText) {
	  StringBuffer buf = new StringBuffer("");
	  if (plainText == null || plainText.length() == 0) {
		   throw new IllegalArgumentException("String to encript cannot be null or zero length");
	}
	  
	  try {
	   MessageDigest md5 = MessageDigest.getInstance("MD5");
	   md5.update(plainText.getBytes());
	   byte b[] = md5.digest();

	   int i;
	   for (int offset = 0; offset < b.length; offset++) {
	    i = b[offset];
	    if (i < 0)
	     i += 256;
	    if (i < 16)
	     buf.append("0");
	    buf.append(Integer.toHexString(i));
	   	}
	  } catch (NoSuchAlgorithmException e) {
		  e.printStackTrace();
	  }
	   return buf.toString().substring(8,24);
	 }

	 public static String s2hex(String str){
		 StringBuffer buf = new StringBuffer("");
		 byte[] b = str.getBytes();
		 for(int i=0;i<b.length;++i){
			 int c = b[i];
			 if (c<0)
				 c += 256;
			 if(c<16)
				 buf.append("0");
			 buf.append(Integer.toHexString(c));
		 }
		 return buf.toString();
	 }
	 
	 public static void main(String agrs[]) {
		  System.out.println(MD5.md5s("a"));
		  System.out.println(s2hex("a"));
		  System.out.println(s2hex("a \\/\r\n"));
		  System.out.println(s2hex("°¢Ò»"));
	 }
	}
