package common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class MyXML {

	DocumentBuilderFactory factory = null;
	DocumentBuilder builder = null;
	Document doc = null;
	
	String urlBase = "http://192.168.1.111:1979/";
	
	public MyXML() {
		try{
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
		}catch(ParserConfigurationException e) {
			Log.d(e);	
		}
	}
	
	public MyXML(File f) {
		try{
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.parse(f);
		}catch(Exception e) {
			Log.d(e);	
		}
	}
	
	public MyXML(String xmlString) {
		try{
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.parse(new InputSource(new StringReader(xmlString)));
		}catch(Exception e) {
			Log.d(e);	
		}
	}
	
	public Document get_document() {
		return doc;
	}
	
	public void xml2file_tree(String baseDir) {
		Element root = doc.getDocumentElement();
		String root_dir = baseDir+root.getNodeName();
		File f = new File(root_dir);
		f.mkdirs();
		create_file_tree(root_dir, root);
	}
	
	private void create_file_tree(String root_dir, Node root_ele) {
		NodeList nodes = root_ele.getChildNodes();
		for(int i=0; i<nodes.getLength(); ++i) {
			Node node = nodes.item(i);
			if(node.getNodeName().contentEquals("file")){
				String filename = node.getFirstChild().getNodeValue();
				long lastModified = Long.parseLong(node.getAttributes().getNamedItem("lastModified").getNodeValue());
				File  f = new File(root_dir+File.separator+filename);
				Log.d(f.getPath());
				if (!f.exists()||lastModified>f.lastModified()) {
					String path = f.getPath().replace("\\", "/");
					try {
						path = URLEncoder.encode(path,"utf-8");
						path = path.replace("%2F", "/");
						path = path.replace("+", "%20");
						download_file(urlBase+path, f, lastModified);
					}catch(Exception e) {
						Log.d(e);
					}
				}
			}else if(node.getNodeName().contentEquals("dir")) {
				String dir = node.getAttributes().getNamedItem("name").getNodeValue();
				Log.d("Got dir:"+dir);
				String sub_dir = root_dir+File.separator+dir;
				File file_dir = new File(sub_dir);
				file_dir.mkdirs();
				create_file_tree(sub_dir, node);				
			}
		}
	}
	
	public void file_tree2xml(String dir, String resultFile) {
		File f = new File(dir);
		if (f.exists() && f.isDirectory()) {
			String name = f.getName();
			Log.d(name);
			Element ele = doc.createElement(name);
			file_tree2xml(ele, f);
			doc.appendChild(ele);
		}
		save2file(resultFile);
	}
	
	public String get_file_tree_xml(String dir) {
		File f = new File(dir);
		if (f.exists() && f.isDirectory()) {
			String[] dirs = dir.split("\\\\");
			Log.d(dirs.length);
			Element root = doc.createElement(dirs[1]);
			Element category = root;
			for(int i=2; i<dirs.length; ++i) {
				if (dirs[i].isEmpty()) {
					break;
				}
				Element ele = doc.createElement("dir");
				ele.setAttribute("name", dirs[i]);
				category.appendChild(ele);
				category = ele;
			}
			file_tree2xml(category, f);
			doc.appendChild(root);
		}
		return get_xml();
	}
	
	public void file_tree2xml(Element ele, File dir) {
		File[] fs = dir.listFiles();
		for(File f : fs) {
			if (f.isDirectory()) {
				String name = f.getName();
				Log.d(name);
				Element dir_ele = doc.createElement("dir");
				dir_ele.setAttribute("name", name);
				ele.appendChild(dir_ele);
				file_tree2xml(dir_ele, f);
			}else{
				String name = f.getName();
				Log.d(name);
				Element file_ele = doc.createElement("file");
				file_ele.setAttribute("lastModified", f.lastModified()+"");
				file_ele.appendChild(doc.createTextNode(name));
				ele.appendChild(file_ele);
			}
		}
	}
	
	public void save2file(String filepath) {
		if (doc == null) {
			return;
		}
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = null;
		try {
			t = tf.newTransformer();
		}catch(TransformerConfigurationException e1) {
			Log.d(e1);
		}
		t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		doc.setXmlStandalone(true);
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(filepath));
		try {
			 t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(source,  result);
		}catch(TransformerException e3) {
			Log.d(e3);;
		}		
	}
	
	public String get_xml() {
		String xml = "";
		if (doc == null) {
			return xml;
		}
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = null;
		try {
			t = tf.newTransformer();
		}catch(TransformerConfigurationException e1) {
			Log.d(e1);
		}
		t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		doc.setXmlStandalone(true);
		DOMSource source = new DOMSource(doc);
		StringWriter strWriter = new StringWriter();
		StreamResult xml_result = new StreamResult(strWriter);
		try {
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(source, xml_result);
			xml = xml_result.getWriter().toString();
		}catch(TransformerException e3) {
			Log.d(e3);;
		}
		return xml;
	}
	
	public void download_file(String url, File file, long lastModified) {
		FileOutputStream fos = null;
		try {
			URL _url = new URL(url);
			URLConnection connection = _url.openConnection();
			// connection.setUseCaches(false);
			connection.connect();
			InputStream inStream = connection.getInputStream();
			BufferedInputStream bufIn = new BufferedInputStream(inStream);
			//ByteArrayOutputStream baos = new ByteArrayOutputStream();
			fos = new FileOutputStream(file);
			byte tmpBytes[] = new byte[4096];
			int len;
			while ((len=bufIn.read(tmpBytes))!=-1) {
				//baos.write(tmpBytes,0,len);
				fos.write(tmpBytes,0, len);
			}
		} catch (Exception e) {
			Log.d(e);
		}finally {
			if (fos != null) {
				try {
					fos.close();
					file.setLastModified(lastModified);
				}catch(IOException ie) {
					Log.d(ie);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyXML xml = new MyXML();
		String dir = "f:"+File.separator+"my_library\\English";
		String str_xml = xml.get_file_tree_xml(dir);
		MyXML xml2 = new MyXML(str_xml);
		Log.d(str_xml);
		xml2.xml2file_tree("");
	}

}
