package batch;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Log;
import common.UFileReader;
import common.UFileWriter;

public class TestRegexp {

	static void testRegexp() {
		String s = " ' ''a   b   c d";
		s = s.replaceAll("'\\s+'","");
		Log.l(s);
	}
	
	static	boolean is_ascii(char c) {
		if(c>0 && c<0x100) {
			return true;
		}else {
			return false;
		}
	}
	
	static boolean is_en(char c) {
		if (is_ascii(c) || c>8207&&c<8266) {
			return true;
		}else {
			return false;
		}
	}

	public static void tmain(String[] args) throws IOException {
		File dir = new File("d:\\tomcat\\bin\\lessons\\Young.Sheldon.1");
		File[] files = dir.listFiles();
		for (File f : files) {
			Log.d(f.getPath());
			String file = f.getAbsolutePath();
			String name = f.getName();
			Pattern pe = Pattern.compile(".+S\\d\\dE(\\d\\d).+$");
			Matcher me = pe.matcher(name);
			String unit = "";
			if (me.find()) {
				unit = me.group(1);
				Log.d(unit);
			}
			
			String s = UFileReader.read(file);
			// s = s.replaceAll("(?<=[A-Za-z])\r\n", "\t");
			String[] lines = s.split("\r\n");
			String result = "Unit "+unit;
			for (String line : lines) {
				Pattern p = Pattern.compile("Dialogue: .+,,(.+)\\\\N\\{.+\\}(.+)$");
				// Pattern p = Pattern.compile("Dialogue: .+?,,(.+)\\\\N.+$");
				Matcher m = p.matcher(line);
				if (m.matches()) {
					String c = m.group(1);
					String e = m.group(2);
					Log.d(e);
					Log.d(c);
					result += "\r\n" + e + "\r\n" + c;
					// Log.d(line);
				} else {
					;// Log.d("not match: "+line);
				}
			}
			//break;
			UFileWriter.write(file + ".txt", result);
		}
	}
	
	public static void main(String[] args) {
		String text = UFileReader.read("e:\\books\\tom.txt");
		Log.d(text.substring(0,300));
		text = text.replace("\r\n", " ");
		String[] sents = text.split("(?<=\\s{2,10})|(?<=(?<!Mrs|Mr)\\.\\s{1,10}|[\\?!]\\s{1,10})");
		for(String s : sents) {
			s = s.trim();
			if (s.length()>0) {
				Log.d(s);
			}
		}
		
	}

}
