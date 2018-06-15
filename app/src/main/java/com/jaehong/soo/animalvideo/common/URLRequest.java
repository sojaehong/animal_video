package com.jaehong.soo.animalvideo.common;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class URLRequest extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	private String url = null;
	private ConcurrentHashMap<String, String> requestHeader = null;
	private String requestBody = null;

	public URLRequest(String url) {
		this.url = url;
	}

	public void putUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void putHeader(ConcurrentHashMap<String, String> requestHeader) {
		this.requestHeader = requestHeader;
	}

	public ConcurrentHashMap<String, String> getHeader() {
		return requestHeader;
	}

	public void putBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getBody() {
		return requestBody;
	}

}
