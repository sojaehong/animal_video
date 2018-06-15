package com.jaehong.soo.animalvideo.common;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class HttpClient {
	public static final int METHOD_GET = 0;
	public static final int METHOD_POST = 1;

	private final int SIZE_BUFFER = 4096;
	private final int SIZE_CAPACITY = 16 * 1024;

	private ConcurrentHashMap<String, String> requestHeader = null;

	public HttpClient() {

	}

	/**
	 * get 방식으로 Request 수행
	 * @param req
	 * @return
	 */
	public String get(URLRequest req) {
		String res = null;
		if (req != null) {
			res = request(req, METHOD_GET);
		}

		return res;
	}

	/**
	 * post 방식으로 Request 수행
	 * @param req
	 * @return
	 */
	public String post(URLRequest req) {
		String res = null;
		if (req != null) {
			res = request(req, METHOD_POST);
		}

		return res;
	}

	private void showLog(URLRequest req) {
		try {
			Iterator<String> iter = null;
			if(req.getHeader() != null) {
				iter = req.getHeader().keySet().iterator();
				Log.d("Request Header process", "start");
				while (iter.hasNext()) {
					String key = (String) iter.next();
					String value = String.valueOf(req.getHeader().get(key));
					Log.d("Request Header", key + " - " + value);
				}
			}

			iter = req.keySet().iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				String value = String.valueOf(req.get(key));

				Log.d("Request Body", key + " - " + value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String request(URLRequest req, int method) {
		String ret = null;
		
		String url = req.getUrl();
		requestHeader = req.getHeader();
		HttpURLConnection conn = null;

		if (method == METHOD_GET) {
			url = url + "?" + makeLinearParam(req);
		}

		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
		} catch (MalformedURLException e) {
			Log.d("request exception", e.toString());
		} catch (IOException e) {
			Log.d("request exception", e.toString());
		}

		if (conn == null)
			return null;

		//Request 헤더 처리
		if (requestHeader != null) {
			Iterator<String> iter = requestHeader.keySet().iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				String value = String.valueOf(requestHeader.get(key));

				if (key != null && !key.equalsIgnoreCase("") && value != null && !value.equalsIgnoreCase("")) {
					conn.setRequestProperty(key, value);
				}
			}
		}

		if (method == METHOD_POST) {
			OutputStream os = null;

			try {
				//conn.connect();
				conn.setRequestMethod("POST");
				conn.setUseCaches(false);
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setInstanceFollowRedirects(true);

				//os = conn.getOutputStream();
				os = new BufferedOutputStream(conn.getOutputStream());

				//String  p = null;
				String json = req.getBody();
				
				if (json != null && !json.equals("")) {
					os.write(json.getBytes());
				} else {
					Log.d("request post", "json null");
					os.write(makeLinearParam(req).getBytes());
				}
				os.flush();
			} catch (ProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (Exception e) {}
				}
			}

		}

		ret = getResponse(conn);

		return ret;
	}

	/**
	 * Linear 타입의 파라메터를 구성한다
	 * @param params
	 * @return
	 */
	private String makeLinearParam(HashMap<String, Object> params) {
		if (params == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		Iterator<String> iter = params.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			String value = String.valueOf(params.get(key));

			if (first) {
				first = false;
			} else {
				sb.append("&");
			}
			try {
				sb.append(URLEncoder.encode(key, "utf-8") + "=" + URLEncoder.encode(value, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				sb.append(key + "=" + value);
			}

			key = null;
			value = null;
		}

		return sb.toString();
	}
	
	private String getResponse(HttpURLConnection conn) {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;

		StringBuffer result = new StringBuffer();
		try {
			is = conn.getInputStream();
			isr = new InputStreamReader(is, "utf-8");
			reader = new BufferedReader(isr);
			String data = "";
			while ((data = reader.readLine()) != null) {
				result.append(data);
			}

		} catch (IOException e) {
			Log.d(null, e.toString());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (Exception e) {}
			}
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {}
			}
			if (conn != null) {
				try {
					conn.disconnect();
				} catch (Exception e) {}
			}
		}

		return result.toString();
	}
}
