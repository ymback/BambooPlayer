package gov.anzong.util;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;

import android.os.Build;
import android.util.Log;

public class HttpUtil {

	public static String iosGetHtml(String uri) {
		InputStream is = null;
		final String ios_ua = "Mozilla/5.0 (iPad; CPU OS 5_0_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A405 Safari/7534.48.3";
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", ios_ua);
			conn.setRequestProperty("Accept-Charset", "GBK");
			conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
		        System.setProperty("http.keepAlive", "false");
		    }else{
				conn.setRequestProperty("Connection", "close");
		    }
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.connect();
			is = conn.getInputStream();
			if( "gzip".equals(conn.getHeaderField("Content-Encoding")) )
				is = new GZIPInputStream(is);
			String encoding =  getCharset( conn, "GBK");
			return IOUtils.toString(is, encoding);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}
		return null;
	}

	public static String bilibiliGetHtml(String uri) {
		InputStream is = null;
		final String ios_ua = "BambooPlayer Android/1.0 (ann@sayyoulove.me)";
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", ios_ua);
			conn.setRequestProperty("Accept-Charset", "GBK");
			conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
		        System.setProperty("http.keepAlive", "false");
		    }else{
				conn.setRequestProperty("Connection", "close");
		    }
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.connect();
			is = conn.getInputStream();
			if( "gzip".equals(conn.getHeaderField("Content-Encoding")) )
				is = new GZIPInputStream(is);
			String encoding =  getCharset( conn, "GBK");
			return IOUtils.toString(is, encoding);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}
		return null;
	}
	
	public static String iosGetHtml(String uri,String encoding) {
		InputStream is = null;
		final String ios_ua = "Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3";
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", ios_ua);
			conn.setRequestProperty("Accept-Charset", "GBK");
			conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
		        System.setProperty("http.keepAlive", "false");
		    }else{
				conn.setRequestProperty("Connection", "close");
		    }
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.connect();
			is = conn.getInputStream();
			if( "gzip".equals(conn.getHeaderField("Content-Encoding")) )
				is = new GZIPInputStream(is);
			return IOUtils.toString(is, encoding);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}
		return null;
	}
	
	private static String getCharset(HttpURLConnection conn, String defaultValue){
		if(conn== null)
			return defaultValue;
		String contentType = conn.getHeaderField("Content-Type");
		if(StringUtil.isEmpty(contentType))
			return defaultValue;
		String startTag = "charset=";
		String endTag = " ";
		int start = contentType.indexOf(startTag);
		if( -1 == start)
			return defaultValue;
		start += startTag.length();
		int end = contentType.indexOf( endTag, start);
		if(-1==end)
			end = contentType.length();
		if(contentType.substring(start, end).equals("no")){
			return "utf-8";
		}
		return contentType.substring(start, end);
	}
}
