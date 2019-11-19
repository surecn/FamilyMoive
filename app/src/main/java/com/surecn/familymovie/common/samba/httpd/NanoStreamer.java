package com.surecn.familymovie.common.samba.httpd;


import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.surecn.familymovie.common.http.NanoHTTPD;
import com.surecn.familymovie.common.samba.SambaUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.util.transport.TransportException;

public class NanoStreamer extends NanoHTTPD {
    public final static String TAG = "NanoStreamer";
    public final static int DEFAULT_SERVER_PORT = 12315;
    private int serverPort;
    private static NanoStreamer streamer;

    private NanoStreamer() {
        this(DEFAULT_SERVER_PORT);
    }

    private NanoStreamer(int port) {
        super(null, port);
        this.serverPort = port;
    }

    public final static NanoStreamer INSTANCE() {
        if (streamer == null) {
            synchronized (NanoStreamer.class) {
                if (streamer == null) {
                    streamer = new NanoStreamer();
                }
            }
        }
        return streamer;
    }

    @Override
    public void start() {
        try {
            super.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> headers = session.getHeaders();
        String uri = session.getUri();
        return respond(headers, uri);
    }

    private Response respond(Map<String, String> headers, String uri) {
        uri = "smb://" + uri.substring(5);
        int index = uri.lastIndexOf(".");
        String extenstion = uri.substring(index + 1);
        String mimeTypeForFile = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extenstion);
        if (mimeTypeForFile == null) {
            if (extenstion.equals("rmvb")) {
                mimeTypeForFile = "video/vnd.rn-realvideo";
            }
        }
        Log.d(TAG, "respond uri=" + uri);
        String smbUri = SambaUtil.cropStreamSmbURL(uri);
        Response response = null;
        try {
            if (SambaUtil.isSmbUrl(smbUri) && !TextUtils.isEmpty(mimeTypeForFile)) {
                SmbFile smbFile = new SmbFile(smbUri);
                InputStream copyStream = new BufferedInputStream(new SmbFileInputStream(smbFile));
                response = serveSmbFile(smbUri, headers, copyStream, smbFile, mimeTypeForFile);
            } else {
                Log.e(TAG, "NOT A VALID SMBFILE VIDEO URL:" + uri);
            }
        } catch (Exception e) {
            Log.e(TAG, "respond Exception:" + e.getMessage());
        }
        return response != null ? response : createResponse(
                Response.Status.NOT_FOUND, MIME_PLAINTEXT,
                "Error 404, file not found.");
    }

    // Announce that the file server accepts partial content requests
    private Response createResponse(Response.Status status, String mimeType, String message) {
        Response res = new Response(status, mimeType, message);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    private Response createNonBufferedResponse(Response.Status status, String mimeType, InputStream message, Long len) {
        Response res = new StreamResponse(status, mimeType, message, len);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    private Response serveSmbFile(String smbFileUrl, Map<String, String> header, InputStream is, SmbFile smbFile, String mime) {
        Response res;
        try {
            // Calculate etag
            String eTag = Integer.toHexString((new StringBuilder(smbFile.getName()).append(smbFile.getLastModified()).append(smbFile.length())).hashCode());
            // Support (simple) skipping:
            long startFrom = 0;
            long endAt = -1;
            String range = header.get("range");
            if (range != null) {
                if (range.startsWith("bytes=")) {
                    range = range.substring("bytes=".length());
                    int minus = range.indexOf('-');
                    try {
                        if (minus > 0) {
                            startFrom = Long.parseLong(range.substring(0, minus));
                            endAt = Long.parseLong(range.substring(minus + 1));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // Change return code and add Content-Range header when skipping is requested
            long fileLen = smbFile.length();
            if (range != null && startFrom >= 0) {
                if (startFrom >= fileLen) {
                    res = createResponse(Response.Status.RANGE_NOT_SATISFIABLE, MIME_PLAINTEXT, "");
                    res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
                    res.addHeader("ETag", eTag);
                } else {
                    if (endAt < 0) {
                        endAt = fileLen - 1;
                    }
                    is.skip(startFrom);
                    res = createNonBufferedResponse(Response.Status.PARTIAL_CONTENT, mime, is, fileLen);
                    res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
                    res.addHeader("ETag", eTag);
                }
            } else {
                if (eTag.equals(header.get("if-none-match")))
                    res = createResponse(Response.Status.NOT_MODIFIED, mime, "");
                else {
                    res = createNonBufferedResponse(Response.Status.OK, mime, is, fileLen);
                    res.addHeader("ETag", eTag);
                }
            }
        } catch (IOException ioe) {
            res = createResponse(Response.Status.FORBIDDEN, MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
        }
        return res;
    }

    private static class StreamResponse extends Response {
        private long available;

        public StreamResponse(Status status, String mimeType, InputStream data, long available) {
            super(status, mimeType, data);
            this.available = available;
        }

        @Override
        protected void sendContentLengthHeaderIfNotAlreadyPresent(PrintWriter pw, Map<String, String> header, long size) {
            long pending = (getData() != null ? available : 0); // This is to support partial sends, see serveFile()
            String string = header.get("Content-Range"); // Such as bytes 203437551-205074073/205074074
            if (string != null) {
                if (string.startsWith("bytes ")) {
                    string = string.substring("bytes ".length());
                }
                Long start = Long.parseLong(string.split("-")[0]);
                pw.print("Content-Length: " + (pending - start) + "\r\n");
            } else {
                pw.print("Content-Length: " + pending + "\r\n");
            }
        }

        @Override
        protected void sendAsFixedLength(OutputStream outputStream, int pending) throws IOException {
//            super.sendAsFixedLength(outputStream, pending);
            sendAsFixedLength(outputStream);
        }

        private void sendAsFixedLength(OutputStream outputStream) throws IOException {
            long pending = (getData() != null ? available : 0);
            if (getRequestMethod() != Method.HEAD && getData() != null) {
                int BUFFER_SIZE = 200 * 1024;
                byte[] buff = new byte[BUFFER_SIZE];
                while (pending > 0) {
                    try {
                        // Note the ugly cast to int to support > 2gb files. If pending < BUFFER_SIZE we can safely cast anyway.
                        int read = getData().read(buff, 0, ((pending > BUFFER_SIZE) ? BUFFER_SIZE : (int) pending));
                        if (read <= 0) {
                            break;
                        }
                        outputStream.write(buff, 0, read);
                        pending -= read;
                    } catch (TransportException transportException) {
                        transportException.printStackTrace();
                    }
                }
            }
        }

    }

}
