package com.surecn.moat.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

public class StringUtils {
	
	//将hashmap中得参数用url方式连接
	public static String hashMapToUrlString(HashMap<String, String> parameters) {
		StringBuffer urlbuff = new StringBuffer();
		if (parameters != null && parameters.size() > 0) {
			Set<String> keySet = parameters.keySet();
			for (String key : keySet) {
				urlbuff.append(key).append("=").append(parameters.get(key)).append("&");
			}
			urlbuff.deleteCharAt(urlbuff.length() - 1);
		}
		return urlbuff.toString();
	}
}
