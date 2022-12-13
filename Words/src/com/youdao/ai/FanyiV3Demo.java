package com.youdao.ai;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.youdao.ai.YoudaoDictItem.Basic;

import common.Log;
import hexi.dbc.Jdbc;


public class FanyiV3Demo {

    private static final String YOUDAO_URL = "https://openapi.youdao.com/api";

    private static final String APP_KEY = "35ce28ab64674310";

    private static final String APP_SECRET = "yYLi8EtO1kcBxn8LZDLVx8pWBwKMIwUP";
    
    static CloseableHttpClient httpClient = HttpClients.createDefault();


    public static YoudaoDictItem lookup(String query) {
    	YoudaoDictItem r = new YoudaoDictItem();
    	
        Map<String,String> params = new HashMap<String,String>();
        String q = query;
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("from", "en");
        params.put("to", "zh");
        params.put("signType", "v3");
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        params.put("curtime", curtime);
        String signStr = APP_KEY + truncate(q) + salt + curtime + APP_SECRET;
        String sign = getDigest(signStr);
        params.put("appKey", APP_KEY);
        params.put("q", q);
        params.put("salt", salt);
        params.put("sign", sign);
        params.put("vocabId","");
        try {
			r = requestForHttp(YOUDAO_URL,params);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    	return r;
    }
    
    static void test() {
    	String sql = "select top 1* from wordfreq200000 where memo!='ok'";
    	try {
    		int i=0;
    		while(i<200000) {
    			++i;
			ResultSet rs = Jdbc.query(sql);
			String sql2 = "insert words(word,phonetic,explains) values(?,?,?)";
			Jdbc.prepare(sql2);
			if(rs.next()) {
				String word = rs.getString(1);
				YoudaoDictItem item = lookup(word);
				Jdbc.setString(1, item.getReturnPhrase());
				Basic basic = item.getBasic();
				if (basic==null) {
					Jdbc.execute_sql("update wordfreq200000 set memo='ok' where word='"+word+"'");
					continue;
				}
		    	Log.l(Log.joinStrings(item.returnPhrase)+"\t"+item.getTranslation());
				String phonetic = basic.getPhonetic()!=null?item.getBasic().getPhonetic():"";
				Jdbc.setString(2, phonetic);
				String explains = Log.joinStrings(item.getBasic().getExplains(), "|");
				Jdbc.setString(3, explains);
				try {
					Jdbc.execute();
				}catch(SQLException e) {
					Log.l(e);
				}
				Jdbc.execute_sql("update wordfreq200000 set memo='ok' where word='"+word+"'");
				Log.a(item);
			}
    		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    static void test0() {

    	YoudaoDictItem item = lookup("boy");
    	Log.l(item);
    	item = lookup("girl");
    	Log.l(item);
    	item = lookup("man");
    	Log.l(item);
    
    }
    
    public static void main(String[] args) throws IOException {
    	test();
   
    }

    public static YoudaoDictItem requestForHttp(String url,Map<String,String> params) throws IOException {

    	YoudaoDictItem r = new YoudaoDictItem();

        /** httpPost */
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        Iterator<Map.Entry<String,String>> it = params.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String,String> en = it.next();
            String key = en.getKey();
            String value = en.getValue();
            paramsList.add(new BasicNameValuePair(key,value));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(paramsList,"UTF-8"));
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        try{
            Header[] contentType = httpResponse.getHeaders("Content-Type");
            //Log.d("Content-Type:" + contentType[0].getValue());
            if("audio/mp3".equals(contentType[0].getValue())){
                HttpEntity httpEntity = httpResponse.getEntity();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(baos);
                byte[] result = baos.toByteArray();
                EntityUtils.consume(httpEntity);
                if(result != null){//
                    String file = "H:\\"+System.currentTimeMillis() + ".mp3";
                    byte2File(result,file);
                }
            }else{
                HttpEntity httpEntity = httpResponse.getEntity();
                String json = EntityUtils.toString(httpEntity,"UTF-8");
                EntityUtils.consume(httpEntity);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                YoudaoDictItem dictItem = gson.fromJson(json, YoudaoDictItem.class);
                //gson.fromJson(json, dictItem);
                
 //               JSONObject r = (JSONObject)JSONObject.fromObject(json);
                //String q = dictItem.getString("query");
                //Log.d(q);
r = dictItem;
                
            }
        }finally {
            try{
                if(httpResponse!=null){
                    httpResponse.close();
                }
            }catch(IOException e){
                //Log.d("## release resouce error ##" + e);
            }
        }
        
        return r;
    }

    /**
     * ���ɼ����ֶ�
     */
    public static String getDigest(String string) {
        if (string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest mdInst = MessageDigest.getInstance("SHA-256");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
     }

    /**
    *
    * @param result ��Ƶ�ֽ���
    * @param file �洢·��
    */
    private static void byte2File(byte[] result, String file) {
        File audioFile = new File(file);
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(audioFile);
            fos.write(result);

        }catch (Exception e){
            Log.d(e.toString());
        }finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

  public static String truncate(String q) {
        if (q == null) {
            return null;
        }
        int len = q.length();
        String result;
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }
}

