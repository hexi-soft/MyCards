package hexi.dbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import common.Json;
import common.Log;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class Neo4jHttp {

	static Neo4jHttp sNeo = null;
    static HttpURLConnection sConnection = null;
    static String sIP = "";
    static String sUser = "";
    static String sPassword = "";
    
    public static Neo4jHttp getInstance(String IP, String user, String password) {
    	if (sNeo == null) {
    		sNeo = new Neo4jHttp(IP, user, password);
    	}
    	return sNeo;
    }
    
    private Neo4jHttp(String IP, String user, String password )
    {
    	Scanner scanner = null;
    	String input = user+":"+password;
        try {
			BASE64Encoder en = new BASE64Encoder();
			//String encoding = Base64.getEncoder().encode(input.getBytes("utf-8")).toString();
			String encoding = en.encode(input.getBytes("utf-8"));
			URL url = new URL("http://"+IP+":7474/db/data/");
			sConnection = (HttpURLConnection) url.openConnection();
			sConnection.setDoOutput(true);
			sConnection.setDoInput(true);
			sConnection.setRequestMethod("POST");
			sConnection.setUseCaches(false);
			sConnection.setInstanceFollowRedirects(false);
			sConnection.setRequestProperty("Accept", "application/json");
			sConnection.setRequestProperty("Authorization", "Basic "+encoding);
			Log.l("connecting to "+url);
			sConnection.connect();
			Log.l("connected");
			sIP = IP;
			sUser = user;
			sPassword = password;
			scanner = new Scanner(sConnection.getInputStream());
			while(scanner.hasNext()) {
				Log.l(scanner.nextLine());
			}
		} catch (Exception e) {
			Log.d(e);
		}finally {
			if (scanner!=null) {
				scanner.close();
			}
		}
        
    }
    
    public String exeCypher( String cypher )
    {
    	String res = "";
    	Scanner scanner = null;
    	String input = sUser+":"+sPassword;
        try {
			BASE64Encoder en = new BASE64Encoder();
			String encoding = en.encode(input.getBytes("utf-8"));
			URL url = new URL("http://"+sIP+":7474/db/data/transaction/commit");
			sConnection = (HttpURLConnection) url.openConnection();
			sConnection.setDoOutput(true);
			sConnection.setDoInput(true);
			sConnection.setRequestMethod("POST");
			sConnection.setUseCaches(false);
			sConnection.setInstanceFollowRedirects(false);
			sConnection.setRequestProperty("Accept", "application/json");
			sConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			sConnection.setRequestProperty("Authorization", "Basic "+encoding);
			Log.l("connecting to "+url);
			sConnection.connect();
			//sConnection.
			Log.l("connected");
			//璁剧疆璇锋眰浣�
            HashMap<String,Object> map=new HashMap<String,Object>();
            ArrayList<HashMap<String,Object>> ss = new ArrayList();
            HashMap<String,Object> sMap = new HashMap<String,Object>();
            sMap.put("statement", cypher);
            ArrayList<String> cols = new ArrayList<String>();
            cols.add("row");
            cols.add("graph");
            sMap.put("resultDataContents", cols);
            ss.add(sMap);
            map.put("statements",ss);
            String s = Json.toJson(map);
            Log.l("s: "+s);
            String result = getReturn(sConnection, s);
            Log.l("result:"+result);
		} catch (Exception e) {
			Log.d(e);
		}finally {
			if (scanner!=null) {
				scanner.close();
			}
		}
        return res;
    }
    
    public static String getReturn(HttpURLConnection connection) throws IOException {
        StringBuffer buffer = new StringBuffer();
        //灏嗚繑鍥炵殑杈撳叆娴佽浆鎹㈡垚瀛楃涓�
        try(InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);){
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            String result = buffer.toString();
            return result;
        }
    }

    //post璇锋眰鐨勬柟娉曢噸杞�
    public static String getReturn(HttpURLConnection connection,String jsr){
        try{
            StringBuffer buffer = new StringBuffer();
            byte[] bytes = jsr.getBytes();
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();

            //灏嗚繑鍥炵殑杈撳叆娴佽浆鎹㈡垚瀛楃涓�
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            String result = buffer.toString();
            return result;
        }catch (Exception e){
            Log.d(e);
        }
        return null;
    }

    public static String executeCypher(String cypher) {
    	String res = "";
    	
    	return res;
    }
    
	public static void main(String[] args) {
		Neo4jHttp neo = Neo4jHttp.getInstance("192.168.1.110","neo4j","hexi521");
		neo.exeCypher("match (n) return n");
	}

}
