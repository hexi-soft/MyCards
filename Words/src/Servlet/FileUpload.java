package Servlet;

import common.UFileReader;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

public class FileUpload extends HttpServlet {

	private String uploadPath = "D:\\web temp files\\"; // 用于存放上传文件的目录

	private File tempPath = new File(
			"D:\\web temp files\\1\\"); // 用于存放临时文件的目录

	public static void print(String s){
		System.out.println(s);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		//res.setContentType("text/html; charset=GB18030");
		res.setContentType("text/html;charset=UTF-8");
		req.setCharacterEncoding("utf-8");
		PrintWriter out = res.getWriter();
		//System.out.println(req.getContentLength());
		//System.out.println(req.getContentType());
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// maximum size that will be stored in memory
		// 允许设置内存中存储数据的门限，单位：字节
		factory.setSizeThreshold(4096);
		// the location for saving data that is larger than getSizeThreshold()
		// 如果文件大小大于SizeThreshold，则保存到临时目录
		factory.setRepository(tempPath);

		ServletFileUpload upload = new ServletFileUpload(factory);
		// maximum size before a FileUploadException will be thrown
		// 最大上传文件，单位：字节
		upload.setSizeMax(10000000);
		try {
			List fileItems = upload.parseRequest(req);
			// assume we know there are two files. The first file is a small
			// text file, the second is unknown and is written to a file on
			// the server
			Iterator iter = fileItems.iterator();
			// 
			// 过滤掉的文件类型
			String[] errorType = { ".exe", ".com", ".cgi", ".asp" };
			String itemNo = uploadPath;// 文件存放路径
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();

				// 忽略其他不是文件域的所有表单信息
				if (!item.isFormField()) {
					String name = item.getName();
					long size = item.getSize();
					if ((name == null || name.equals("")) && size == 0)
						continue;
					int idx = name.lastIndexOf('\\');
					if (idx>0)
						name = name.substring(idx+1);
					if (!name.isEmpty()) {
						for (int temp = 0; temp < errorType.length; temp++) {
							if (name.endsWith(errorType[temp])) {
								throw new IOException(name + ": wrong type");
							}
						}
						try {

							// 保存上传的文件到指定的目录
							item.write(new File(uploadPath + name));
							item.getString();
							out.print(name + "&nbsp;&nbsp;" + size + "<br>");
							String text = UFileReader.read(uploadPath + name);
							//text=text.replace("\r\n", "<br>");
							req.getSession().setAttribute("text",text);
							//req.getRequestDispatcher("/American/display_article.jsp").forward(req,res);
							//System.out.println(text.substring(0,100));
							//out.println(text);

						} catch (Exception e) {
							out.println(e);
						}

					} else {
						throw new IOException("fail to upload");
					}
				}
			}
		} catch (IOException e) {
			out.println(e);
		} catch (FileUploadException e) {
			out.println(e);
		}
	}

	public void init() throws ServletException {
		//uploadPath = getServletConfig().getInitParameter("upload_path");
		uploadPath = "d:\\web temp files\\";
		//print("uploadPath: "+uploadPath);
	}
}