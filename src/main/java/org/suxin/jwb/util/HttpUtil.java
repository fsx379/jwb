package org.suxin.jwb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang.StringUtils;

public class HttpUtil
{

	public static final int DEFAULT_CONNECT_TIME_OUT = 30000;

	public static final int DEFAULT_READ_TIME_OUT = 30000;

	public static final String DEFAULT_CHARSET = "UTF-8";

	public static String sendRequest(String url, int connectTimeout, int readTimeout, String charset, boolean returnSingle){
		BufferedReader in = null;
		HttpURLConnection conn = null;
		try
		{
			if (StringUtils.isBlank(charset))
			{
				charset = DEFAULT_CHARSET;
			}
			conn = getURLConnection(url, connectTimeout, readTimeout);
			in = new BufferedReader(new InputStreamReader(connect(conn), charset));
			String result = getReturnResult(in, returnSingle);
			if (StringUtils.isBlank(result))
			{
				//throw new RemoteInvocationFailureException("网络异常，" + url + "无法联通");
				//LoggerUtil.alarmInfo("[合作方通讯][普通][网络异常，"+url + "无法联通]");
				return null;
			}
			return result;
		}
		catch (Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}

				if (conn != null)
				{
					conn.disconnect();
				}
			}
			catch (IOException e)
			{
				//logger.error("", e);
			}
		}
	}

	public static String sendRequest(String url, boolean returnSingle) throws Exception
	{
		return sendRequest(url, DEFAULT_CONNECT_TIME_OUT, DEFAULT_READ_TIME_OUT, DEFAULT_CHARSET, returnSingle);
	}

	public static byte[] sendRequest(String url, int connectTimeout, int readTimeout) throws Exception
	{
		InputStream is = null;
		HttpURLConnection conn = null;
		try
		{
			conn = getURLConnection(url, connectTimeout, readTimeout);
			is = connect(conn);
			int size = is.available();
			byte[] result = new byte[size];
			//logger.debug("result length:" + size);
			is.read(result, 0, size);
			return result;
		}
		catch (Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				if (is != null)
				{
					is.close();
				}
				if (conn != null)
				{
					conn.disconnect();
				}
			}
			catch (IOException e)
			{
				//logger.error("", e);
			}
		}
	}

	public static byte[] sendRequest(String url) throws Exception
	{
		return sendRequest(url, DEFAULT_CONNECT_TIME_OUT, DEFAULT_READ_TIME_OUT);
	}

	public static String sendPostRequest(String url, String content, String charset)
	{
		return sendPostRequest(url, content, charset, DEFAULT_CONNECT_TIME_OUT, DEFAULT_READ_TIME_OUT);
	}

	public static String sendPostRequest(String url, String content, String charset, int connectTimeout, int readTimeout)
	{
		return sendPostRequest(url, content, charset, DEFAULT_CONNECT_TIME_OUT, DEFAULT_READ_TIME_OUT, false);
	}

	public static String sendPostRequest(String url, String content, String charset, int connectTimeout, int readTimeout,
			boolean needCompress){
		BufferedReader in = null;
		HttpURLConnection httpConn = null;
		try
		{
			httpConn = getURLConnection(url, connectTimeout, readTimeout);
			if (StringUtils.isBlank(charset))
			{
				charset = DEFAULT_CHARSET;
			}
			//			logger.debug("请求发送地址:" + url);
			//logger.debug("参数:" + content);
			InputStream stream = postConnect(httpConn, content, charset, needCompress);

			in = new BufferedReader(new InputStreamReader(stream, charset));
			String result = getReturnResult(in, false);
			//logger.debug("请求返回结果:" + result);
			if (StringUtils.isBlank(result))
			{
				//throw new RemoteInvocationFailureException("网络异常，" + url + "无法联通");
//				LoggerUtil.alarmInfo("[合作方通讯][普通][网络异常，"+url + "无法联通]");
				return null;
			}
			return result;
		}
		catch (Exception e){
			return null;
		}
		finally{
			try
			{
				if (in != null)
				{
					in.close();
				}

				if (httpConn != null)
				{
					httpConn.disconnect();
				}
			}
			catch (IOException e)
			{
				//logger.error("", e);
			}
		}
	}

	public static String sendPostRequest(String url, String content, String inCharset, String outCharset, int connectTimeout,
			int readTimeout, boolean needCompress){
		BufferedReader in = null;
		HttpURLConnection httpConn = null;
		try
		{
			httpConn = getURLConnection(url, connectTimeout, readTimeout);
			if (StringUtils.isBlank(inCharset))
			{
				inCharset = DEFAULT_CHARSET;
			}
			//			logger.debug("请求发送地址:" + url);
			//logger.debug("参数:" + content);
			InputStream stream = postConnect(httpConn, content, inCharset, needCompress);

			in = new BufferedReader(new InputStreamReader(stream, outCharset));
			String result = getReturnResult(in, false);
			//logger.debug("请求返回结果:" + result);
			if (StringUtils.isBlank(result))
			{
				//throw new RemoteInvocationFailureException("网络异常，" + url + "无法联通");
//				LoggerUtil.alarmInfo("[合作方通讯][普通][网络异常，"+url + "无法联通]");
			}
			return result;
		}
		catch (Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}

				if (httpConn != null)
				{
					httpConn.disconnect();
				}
			}
			catch (IOException e)
			{
				//logger.error("", e);
			}	
		}
	}

	public static String sendPostRequest(String url, String content, String charset, boolean needCompress){
		return sendPostRequest(url, content, charset, DEFAULT_CONNECT_TIME_OUT, DEFAULT_READ_TIME_OUT, needCompress);
	}

	private static InputStream postConnect(HttpURLConnection httpConn, String content, String charset, boolean needCompress) throws IOException
	{
		String urlStr = httpConn.getURL().toString();
			if (StringUtils.isBlank(charset))
			{
				charset = DEFAULT_CHARSET;
			}
			// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true,
			// 默认情况下是false;
			httpConn.setDoOutput(true);
			// Post 请求不能使用缓存
			httpConn.setUseCaches(false);
			// 设定请求的方法为"POST"，默认是GET
			httpConn.setRequestMethod("POST");
			if (needCompress)
			{
				sendCompressRequest(content, charset, httpConn);
			}
			else
			{
				sendNoCompressRequest(content, charset, httpConn);
			}
			// 接收数据
			if (needCompress)
			{
				return new GZIPInputStream(httpConn.getInputStream());
			}
			return httpConn.getInputStream();
	}

	private static void sendCompressRequest(String content, String charset, HttpURLConnection httpConn)
	{
		GZIPOutputStream out = null;
		try
		{
			httpConn.setRequestProperty("Content-Type", "application/x-gzip");
			httpConn.setRequestProperty("Accept", "application/x-gzip");
			out = new GZIPOutputStream(httpConn.getOutputStream());
			out.write(content.getBytes("GBK"));
			out.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (IOException e)
				{
					//logger.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 发送原始消息
	 * @param content
	 * @param charset
	 * @param httpConn
	 */
	private static void sendNoCompressRequest(String content, String charset, HttpURLConnection httpConn)
	{
		PrintWriter out = null;
		try
		{
			out = new PrintWriter(new OutputStreamWriter(httpConn.getOutputStream(), charset));
			out.write(content);
			out.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (out != null)
			{
				out.close();
			}
		}
	}

	/**
	 * 建立远程连接
	 * 
	 * @param urlStr
	 * @param connectTimeout
	 * @param readTimeout
	 * @return
	 * @throws Exception 
	 * @throws IOException 
	 */
	private static InputStream connect(HttpURLConnection httpConn) throws IOException, Exception
	{
		String urlStr = httpConn.getURL().toString();
			if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK)
			{
				//logger.error(urlStr + "|ResponseCode=" + httpConn.getResponseCode());
				throw new Exception("远程访问" + urlStr + "出错，返回结果为：" + httpConn.getResponseCode());
			}
			return httpConn.getInputStream();
		
	}

	/**
	 * 构造URLConnnection
	 * 
	 * @param urlStr
	 * @param connectTimeout
	 * @param readTimeout
	 * @return
	 * @throws RemoteInvocationFailureException
	 */
	private static HttpURLConnection getURLConnection(String urlStr, int connectTimeout, int readTimeout)
			throws Exception
	{
		//	logger.debug("请求URL:" + urlStr);
		try
		{
			URL remoteUrl = new URL(urlStr);
			HttpURLConnection httpConn = (HttpURLConnection) remoteUrl.openConnection();
			httpConn.setConnectTimeout(connectTimeout);
			httpConn.setReadTimeout(readTimeout);
			return httpConn;
		}
		catch (MalformedURLException e)
		{
			//logger.error("", e);
			throw new Exception("远程访问异常[" + urlStr + "]", e);
		}
		catch (IOException e)
		{
			//logger.error("", e);
			throw new Exception("网络IO异常[" + urlStr + "]", e);
		}
	}

	private static String getReturnResult(BufferedReader in, boolean returnSingleLine) throws IOException
	{
		if (returnSingleLine)
		{
			return in.readLine();
		}
		else
		{
			StringBuffer sb = new StringBuffer();
			String result = "";
			while ((result = in.readLine()) != null)
			{
				sb.append(StringUtils.trimToEmpty(result));
			}
			return sb.toString();
		}
	}
	
	
	
	

	
	
	public static String generatorParamString(Map<String, String> parameters) {
		if (parameters == null)
			return null;

		StringBuilder params = new StringBuilder(512);
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();
			params.append(name + "=");
			try {
				params.append(URLEncoder.encode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (Exception e) {
				String message = String.format("'%s'='%s'", name, value);
				throw new RuntimeException(message, e);
			}
			params.append("&");
		}
		params.delete(params.length() - 1, params.length());
		return params.toString();
	}
}
