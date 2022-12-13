package batch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import common.Log;
import common.UFileReader;

public class Test {

	
	
	static void testDate() throws ParseException{
		String t="2022-10-20 13:42:25";
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date = sdf.parse (t);
        System.out.println (date.getTime());
	}
	
	static void testBigFile() {
		String s = UFileReader.read("I:\\word_freq", 200);
		Log.p(s);
	}
	public static void main(String[] args)  {
		testBigFile();
	}

}
