package com.surecn.moat.utils;

import android.content.Context;
import android.util.Log;

public class log {
	public static String tag = "";

	private final static int logLevel = Log.VERBOSE;
	private static boolean LOG_DEBUG = true;

	public static void init(Context context, boolean debug) {
		LOG_DEBUG = debug;
		tag = context.getPackageName();
	}

	public static void init(String tag, boolean debug) {
		LOG_DEBUG = debug;
		log.tag = tag;
	}

	/**
	 * Get The Current Function Name
	 * 
	 * @return
	 */
	private static String getFunctionName() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		if (sts == null) {
			return null;
		}

		for (StackTraceElement st : sts) {
			if (st.isNativeMethod()) {
				continue;
			}
			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}
			if (st.getClassName().equals(log.class.getName())) {
				continue;
			}
			return "[" + st.getFileName() + ":" + st.getLineNumber() + "]";
		}
		return null;
	}



	/**
	 * The Log Level:i
	 * 
	 * @param str
	 */
	public static void i(String str, Object ... params) {
		if (LOG_DEBUG) {
			if (logLevel <= Log.INFO) {
				String name = getFunctionName();
				if (name != null) {
					Log.i(tag, String.format(name + " " + str, params));
				} else {
					Log.i(tag, String.format(str.toString()));
				}
			}
		}
	}

	/**
	 * The Log Level:d
	 * 
	 * @param str
	 */
	public static void d(String str, Object ... params) {
		if (LOG_DEBUG) {
			if (logLevel <= Log.DEBUG) {
				String name = getFunctionName();
				if (name != null) {
					Log.d(tag, String.format(name + " " + str, params));
				} else {
					Log.d(tag, String.format(str, params));
				}
			}
		}
	}

	/**
	 * The Log Level:V
	 * 
	 * @param str
	 */
	public static void v(String str, Object ... params) {
		if (LOG_DEBUG) {
			if (logLevel <= Log.VERBOSE) {
				String name = getFunctionName();
				if (name != null) {
					Log.v(tag, String.format(name + " " + str, params));
				} else {
					Log.v(tag, String.format(str, params));
				}
			}
		}
	}

	/**
	 * The Log Level:w
	 * 
	 * @param str
	 */
	public static void w(String str, Object ... params) {
		if (LOG_DEBUG) {
			if (logLevel <= Log.WARN) {
				String name = getFunctionName();
				if (name != null) {
					Log.w(tag, String.format(name + " " + str, params));
				} else {
					Log.w(tag, String.format(str, params));
				}
			}
		}
	}

	/**
	 * The Log Level:e
	 * 
	 * @param str
	 */
	public static void e(String str, Object ... params) {
		if (LOG_DEBUG) {
			if (logLevel <= Log.ERROR) {
				String name = getFunctionName();
				if (name != null) {
					Log.e(tag, String.format(name + " " + str, params));
				} else {
					Log.e(tag, String.format(str, params));
				}
			}
		}
	}

	/**
	 * The Log Level:e
	 * 
	 * @param e
	 */
	public static void e(Exception e) {
		if (LOG_DEBUG) {
			if (logLevel <= Log.ERROR) {
				String name = getFunctionName();
				Log.e(tag, name + " ", e);
			}
		}
	}

}