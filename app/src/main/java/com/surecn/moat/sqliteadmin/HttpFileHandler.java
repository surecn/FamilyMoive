package com.surecn.moat.sqliteadmin;

import com.surecn.familymoive.common.http.NanoHTTPD;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

public class HttpFileHandler implements HttpServer.OnHttpRequest {

	private String webRoot;

	public HttpFileHandler(final String webRoot) {
		this.webRoot = webRoot;
	}

	@Override
	public NanoHTTPD.Response onHttpRequest(String uri, Map<String, String> paramter) {
		final File file = new File(this.webRoot, uri);
		String text = null;
		if (!file.exists()) { // 不存在
			text = "<html><body><h1>Error 404, file not found.</h1></body></html>";
		} else if (file.canRead()) {
			if (file.isDirectory()) { // 文件夹
				try {
					text = createDirListHtml(file, uri);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else { // 文件
				String contentType = URLConnection
						.guessContentTypeFromName(file.getAbsolutePath());
				contentType = null == contentType ? "charset=UTF-8"
						: contentType + "; charset=UTF-8";
			}
		} else { // 不可读
			text = "<html><body><h1>Error 403, access denied.</h1></body></html>";
		}
		NanoHTTPD.Response response = new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, "text/html", text);
		response.addHeader("Content-Type", "text/html;charset=UTF-8");
		return response;
	}


	/** 创建文件列表浏览网页 */
	private String createDirListHtml(File dir, String target)
			throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<title>");
		sb.append(null == target ? dir.getAbsolutePath() : target);
		sb.append(" 的索引</title>\n");
		sb.append("<link rel=\"shortcut icon\" href=\"/mnt/sdcard/.wfs/img/favicon.ico\">\n");
		sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/mnt/sdcard/.wfs/css/wsf.css\">\n");
		sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/mnt/sdcard/.wfs/css/examples.css\">\n");
		sb.append("<script type=\"text/javascript\" src=\"/mnt/sdcard/.wfs/js/jquery-1.7.2.min.js\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"/mnt/sdcard/.wfs/js/jquery-impromptu.4.0.min.js\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"/mnt/sdcard/.wfs/js/wsf.js\"></script>\n");
		sb.append("</head>\n<body>\n<h1 id=\"header\">");
		sb.append(null == target ? dir.getAbsolutePath() : target);
		sb.append(" 的索引</h1>\n<table id=\"table\">\n");
		sb.append("<tr class=\"header\">\n<td>名称</td>\n<td class=\"detailsColumn\">大小</td>\n<td class=\"detailsColumn\">修改日期</td>\n<td class=\"detailsColumn\">处理操作</td>\n</tr>\n");
		/* 上级目录 */
		if (!isSamePath(dir.getAbsolutePath(), this.webRoot)) {
			sb.append("<tr>\n<td><a class=\"icon up\" href=\"..\">[上级目录]</a></td>\n<td></td>\n<td></td>\n<td></td>\n</tr>\n");
		}
		/* 目录列表 */
		File[] files = dir.listFiles();
		if (null != files) {
			sort(files); // 排序
			for (File f : files) {
				appendRow(sb, f);
			}
		}
		sb.append("</table>\n<hr noshade>\n<em>Welcome to <a target=\"_blank\" href=\"http://vaero.blog.51cto.com/\">winorlose2000's blog</a>!</em>\n</body>\n</html>");
		return sb.toString();
	}

	private boolean isSamePath(String a, String b) {
		String left = a.substring(b.length(), a.length()); // a以b开头
		if (left.length() >= 2) {
			return false;
		}
		if (left.length() == 1 && !left.equals("/")) {
			return false;
		}
		return true;
	}

	/** 排序：文件夹、文件，再各安字符顺序 */
	private void sort(File[] files) {
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				if (f1.isDirectory() && !f2.isDirectory()) {
					return -1;
				} else if (!f1.isDirectory() && f2.isDirectory()) {
					return 1;
				} else {
					return f1.toString().compareToIgnoreCase(f2.toString());
				}
			}
		});
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd ahh:mm");

	private void appendRow(StringBuffer sb, File f) {
		String clazz, link, size;
		if (f.isDirectory()) {
			clazz = "icon dir";
			link = f.getName() + "/";
			size = "";
		} else {
			clazz = "icon file";
			link = f.getName();
			size = formatFileSize(f.length());
		}
		sb.append("<tr>\n<td><a class=\"");
		sb.append(clazz);
		sb.append("\" href=\"");
		sb.append(link);
		sb.append("\">");
		sb.append(link);
		sb.append("</a></td>\n");
		sb.append("<td class=\"detailsColumn\">");
		sb.append(size);
		sb.append("</td>\n<td class=\"detailsColumn\">");
		sb.append(sdf.format(new Date(f.lastModified())));
		sb.append("</td>\n<td class=\"operateColumn\">");
		sb.append("<span><a href=\"");
		sb.append(link);
		//sb.append(WebServer.SUFFIX_ZIP);
		sb.append("\">下载</a></span>");
		if (f.canWrite() && !hasWfsDir(f)) {
			sb.append("<span><a href=\"");
			sb.append(link);
			//sb.append(WebServer.SUFFIX_DEL);
			sb.append("\" onclick=\"return confirmDelete('");
			sb.append(link);
			//sb.append(WebServer.SUFFIX_DEL);
			sb.append("')\">删除</a></span>");
		}
		sb.append("</td>\n</tr>\n");
	}

	public static boolean hasWfsDir(File f) {
		String path = f.isDirectory() ? f.getAbsolutePath() + "/" : f
				.getAbsolutePath();
		return path.indexOf("/.wfs/") != -1;
	}

	/** 获得文件大小表示 */
	private String formatFileSize(long len) {
		if (len < 1024)
			return len + " B";
		else if (len < 1024 * 1024)
			return len / 1024 + "." + (len % 1024 / 10 % 100) + " KB";
		else if (len < 1024 * 1024 * 1024)
			return len / (1024 * 1024) + "." + len % (1024 * 1024) / 10 % 100
					+ " MB";
		else
			return len / (1024 * 1024 * 1024) + "." + len
					% (1024 * 1024 * 1024) / 10 % 100 + " MB";
	}


}
