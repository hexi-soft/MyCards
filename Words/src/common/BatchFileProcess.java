package common;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class BatchFileProcess {

	/**
	 * @param args
	 */
	static Logger log = Log.log;
	
	public static String fix_number(int n){
		String r = Integer.toString(n);
		if (n<10)
			r = "00"+n;
		else if(n<100)
			r = "0"+n;
		else if(n<1000)
			r = ""+n;
		return r;
	}
	
	public static void main(String[] args) {
		File f = new File("d:\\words.txt");
		try{
			Scanner scanner = new Scanner(f);
			StringBuilder sb = new StringBuilder();
			int i = 1;
			while(scanner.hasNextLine()){
				String l = scanner.nextLine();
				
				String wave = "_"+fix_number(i)+".wav";
				File wf = new File("d:\\tmp\\"+wave);
				File nwf = new File("d:\\tmp\\"+l.toLowerCase()+".wav");
				wf.renameTo(nwf);
				log.debug(wave);
				/*
				String nl = l+"\r\n\r\n\r\n";
				sb.append(nl);*/
				log.debug(l);
				++i;
			}
			//UFileWriter.write("d:\\nwords.txt", sb.toString());
		}catch(IOException e){
			log.info(e);
		}
	}

}
