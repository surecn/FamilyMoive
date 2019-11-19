package com.surecn.moat.tools;

import android.content.Context;
import android.util.Log;

public class log {
	public static String tag = "";

	public static boolean LOG_DEBUG = true;

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
	public static void i(String str) {
		if (LOG_DEBUG) {
			String name = getFunctionName();
			if (name != null) {
				Log.i(tag, name + " " + str);
			} else {
				Log.i(tag, str);
			}
		}
	}

	/**
	 * The Log Level:d
	 * 
	 * @param str
	 */
	public static void d(String str) {
		if (LOG_DEBUG) {
			String name = getFunctionName();
			if (name != null) {
				Log.d(tag, name + " " + str);
			} else {
				Log.d(tag, str);
			}
		}
	}

	/**
	 * The Log Level:V
	 * 
	 * @param str
	 */
	public static void v(String str) {
		if (LOG_DEBUG) {
			String name = getFunctionName();
			if (name != null) {
				Log.v(tag, name + " " + str);
			} else {
				Log.v(tag, str);
			}
		}
	}

	/**
	 * The Log Level:w
	 * 
	 * @param str
	 */
	public static void w(String str) {
		if (LOG_DEBUG) {
			String name = getFunctionName();
			if (name != null) {
				Log.w(tag, name + " " + str);
			} else {
				Log.w(tag, str);
			}
		}
	}

	/**
	 * The Log Level:e
	 * 
	 * @param str
	 */
	public static void e(String str) {
		if (LOG_DEBUG) {
			String name = getFunctionName();
			if (name != null) {
				Log.e(tag, name + " " + str);
			} else {
				Log.e(tag, str);
			}
		}
	}

	/**
	 * The Log Level:e
	 *
	 */
	public static void e(Exception e) {
		if (LOG_DEBUG) {
			Log.e(tag, "error:", e);
		}
	}

	public static void printStackTrace() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		RuntimeException e = new RuntimeException();
		if (sts != null) {
			e.setStackTrace(sts);
			log.e(e);
		}
	}

}