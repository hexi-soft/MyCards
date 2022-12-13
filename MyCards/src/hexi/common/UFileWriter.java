package hexi.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class UFileWriter {
	public File file;
	
	public UFileWriter (String filename){
		file = new File (filename);
	}
	
	public String getPath () {
		return file.getPath();
	}
	
	public void write (String s) 
	{
		try {
			FileOutputStream fos = new FileOutputStream (file);
			OutputStreamWriter osw = new OutputStreamWriter (fos);
			osw.write(s);
			osw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static public void write (String file, String s) 
	{
		try {
			FileOutputStream fos = new FileOutputStream (file);
			OutputStreamWriter osw = new OutputStreamWriter (fos);
			osw.write(s);
			osw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
//		UFileWriter uw = new UFileWriter(null);
		File file = new File("d:\\tomcat\\webapps\\ROOT\\waves\\words\\");
		File[] fs = file.listFiles();
		for(File f : fs){
			String name = f.getName();
			if (!name.equals(name.toLowerCase())){
				String path = f.getAbsolutePath();
				path = path.toLowerCase();
				File nf = new File(path);
				f.renameTo(nf);
				System.out.println(f.getAbsolutePath());
			}
		}
	}

}
