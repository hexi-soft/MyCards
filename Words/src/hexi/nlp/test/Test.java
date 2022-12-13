package hexi.nlp.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
try {
	File outFile = new File("F:\\WordFreq");
	FileOutputStream fos = new FileOutputStream(outFile);
	File file = new File("F:\\word_freq");
	FileInputStream fis = new FileInputStream(file);
	Scanner scanner = new Scanner(fis);
	Pattern pattern = Pattern.compile("^[a-zA-Z-]+\t\\d+$");
	while(scanner.hasNextLine()) {
		String line = scanner.nextLine();
		if (line.length()>32)
			continue;
		Matcher matcher = pattern.matcher(line);
		if (matcher.matches()) {
			line += "\r\n";
		fos.write(line.getBytes());
		fos.flush();
		}
//		break;
	}
	fos.close();
	fis.close();
	scanner.close();
	
}catch(Exception e) {
	;
}
		System.out.println("Hello");
	}

}
