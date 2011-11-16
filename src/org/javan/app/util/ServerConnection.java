/*
 * Class that handle Server Connection
 * (Controlable vs NonControlable)
 */
package org.javan.app.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author 144key (Joel)
 */
public class ServerConnection {
    private HttpURLConnection mainConn;
    private static long counterData = 0;
    public static final String GET  = "GET";
    public static final String POST = "POST";
    public static final String HEAD = "HEAD";
    public static final int URLENCODED = 0;
    public static final int MULTIPART = 1;
    private static final String BOUNDARY = "----------Q2atHFl144hkeyZCaTO7jz";

    public ServerConnection() {
        mainConn = null;
    }

    public static long getDataStreamUsage() {
        return counterData;
    }

    public static byte[] getHttpMultipartRequestData (Hashtable params, String[] fileField, String[] fileName, String[] fileType, byte[][] fileBytes) throws Exception {
        // Boundary Tag
        String boundary = ServerConnection.BOUNDARY;
        String endBoundary = "\r\n--" + boundary + "--\r\n";
        // Non Binary Data
        StringBuffer res = new StringBuffer("--").append(boundary).append("\r\n");
        Enumeration keys = params.keys();
        while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = (String)params.get(key);
            res.append("Content-Disposition: form-data; name=\"").append(key).append("\"\r\n")
                    .append("\r\n").append(value)
                    .append("\r\n--").append(boundary).append("\r\n");
        }
        String boundaryMessage = res.toString();
        // Begin to Write
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(boundaryMessage.getBytes());
        // ... begin to write binary data
        if ((fileField != null && fileName != null && fileType != null && fileBytes != null) &&
            (fileField.length > 0 && fileField.length == fileName.length && fileName.length == fileType.length && fileType.length == fileBytes.length)) {
            int nbFiles = fileField.length;
            for (int i=0; i<nbFiles; i++) {
                res = new StringBuffer();
                res.append("Content-Disposition: form-data; name=\"").append(fileField[i]).append("\"; filename=\"").append(fileName[i]).append("\"\r\n")
                    .append("Content-Type: ").append(fileType[i]).append("\r\n\r\n");
                bos.write(res.toString().getBytes());
                bos.write(fileBytes[i]);
                if ((i+1) != nbFiles) {
                    res = new StringBuffer("\r\n--").append(boundary).append("\r\n");
                    bos.write(res.toString().getBytes());
                }
            }
        }
        bos.write(endBoundary.getBytes());
        byte[] postBytes = bos.toByteArray();
        bos.close();
        return postBytes;
    }

    public String getBoundaryString() {
        return BOUNDARY;
    }

    public void makeControlableConnection(String url, Hashtable params, String[] fileField, String[] fileName, String[] fileType, byte[][] fileBytes) {
        try {
            if (fileField == null || fileName == null || fileType == null || fileBytes == null) {
                StringBuffer str = new StringBuffer();
                Enumeration keys = params.keys();
                while(keys.hasMoreElements()) {
                    String key = (String)keys.nextElement();
                    str.append(key+"=");
                    str.append((String)params.get(key));
                    if (keys.hasMoreElements()) str.append("&");
                }
                makeControlableConnection(url, str.toString().getBytes());
            } else makeControlableConnection(url, getHttpMultipartRequestData(params, fileField, fileName, fileType, fileBytes), ServerConnection.MULTIPART);
        } catch (Exception ex) {
            ex.printStackTrace();
            mainConn = null;
        }
    }

    public void makeControlableConnection(String url, byte[] postData) {
        makeControlableConnection(url, postData, ServerConnection.URLENCODED);
    }

    public void makeControlableConnection(String url, byte[] postData, int mode) {
        OutputStream out = null;
        try {
            mainConn = (HttpURLConnection) (new URL(url)).openConnection();
            mainConn.setRequestProperty("Accept-Encoding", "gzip");
            if (postData == null) {
                mainConn.setRequestMethod(GET);
                counterData += url.length();
            } else {
                // System.out.println(">>>>> POST DATA : "+new String(postData));
                mainConn.setRequestMethod(POST);
                mainConn.setAllowUserInteraction(false); // no user interact [like pop up]
                mainConn.setDoOutput(true); // want to send
                if (mode == ServerConnection.URLENCODED) mainConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                else mainConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + ServerConnection.BOUNDARY);
                mainConn.setRequestProperty("Content-Length", String.valueOf(postData.length));
                out = mainConn.getOutputStream();
                out.write(postData);
                counterData += url.length() + postData.length;
            }
        } catch (IOException e1) {
            //System.out.println(">>>>> "+e1.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e2) {
                }
            }
        }
    }

    public void stopControlableConnection() {
        if (mainConn != null) {
            mainConn.disconnect(); // don't forget
            mainConn = null;
        }
    }

    public boolean isControlableConnectionIdle() {
        return (mainConn == null);
    }

    public byte[] getControlableConnectionResponse() {
        if (mainConn == null) {
            return null;
        } else {
            try {                
                if (mainConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    //System.out.println("HTTP_OK");
                    DataInputStream is = new DataInputStream(mainConn.getInputStream());
                    String contentEncoding = mainConn.getHeaderField("Content-Encoding");
                    GZIPInputStream GZS = null;
                    if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
                        GZS = new GZIPInputStream(is);
                        //System.out.println("[GZIP] "+mainConn.getURL()+" : Get GZIP Format");
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] b = new byte[2048];
                    int count = 0;
                    byte[] response = null;
                    while (count != -1) {
                        count = (GZS != null ? GZS : is).read(b, 0, 2048);
                        if (count != -1) {
                            baos.write(b, 0, count);
                        }
                    }
                    response = baos.toByteArray();
                    if (GZS != null) GZS.close();
                    is.close();
                    baos.close();
                    counterData += response.length;
                    return response;
                } else {
                    //System.out.println("HTTP ERROR :: "+ mainConn.getResponseCode());
                    return null;
                }
            } catch (Exception e) {
                //e.printStackTrace();
                return null;
            }
        }
    }

    public static HttpURLConnection makeConnection(String url, Hashtable params, String[] fileField, String[] fileName, String[] fileType, byte[][] fileBytes) {
        try {
            return makeConnection(url, getHttpMultipartRequestData(params, fileField, fileName, fileType, fileBytes), ServerConnection.MULTIPART);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static HttpURLConnection makeConnection(String url, byte[] postData) {
        return makeConnection(url, postData, ServerConnection.URLENCODED);
    }

    public static HttpURLConnection makeConnection(String url, byte[] postData, int mode) {
        HttpURLConnection conn = null;
        OutputStream out = null;
        try {
            conn = (HttpURLConnection) (new URL(url)).openConnection();
            conn.setRequestProperty("Accept-Encoding", "gzip");
            if (postData == null) {
                conn.setRequestMethod(GET);
                counterData += url.length();
            } else {
                // System.out.println(">>>>> POST DATA : "+new String(postData));
                conn.setRequestMethod(POST);
                conn.setAllowUserInteraction(false); // no user interact [like pop up]
                conn.setDoOutput(true); // want to send
                if (mode == ServerConnection.URLENCODED) conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                else conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + ServerConnection.BOUNDARY);
                conn.setRequestProperty("Content-Length", String.valueOf(postData.length));
                out = conn.getOutputStream();
                out.write(postData);
                counterData += url.length() + postData.length;
            }
        } catch (IOException e1) {
            //System.out.println(">>>>> "+e1.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e2) {
                }
            }
        }
        return conn;
    }

    public static byte[] getResponse(HttpURLConnection conn) {
        if (conn == null) {
            return null;
        } else {
            try {
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    //System.out.println("HTTP_OK");
                    DataInputStream is = new DataInputStream(conn.getInputStream());
                    String contentEncoding = conn.getHeaderField("Content-Encoding");
                    GZIPInputStream GZS = null;
                    if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
                        GZS = new GZIPInputStream(is);
                        //System.out.println("[GZIP] "+conn.getURL()+" : Get GZIP Format");
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] b = new byte[2048];
                    int count = 0;
                    byte[] response = null;
                    while (count != -1) {
                        count = (GZS != null ? GZS : is).read(b, 0, 2048);
                        if (count != -1) {
                            baos.write(b, 0, count);
                        }
                    }
                    response = baos.toByteArray();
                    if (GZS != null) GZS.close();
                    is.close();
                    baos.close();
                    conn.disconnect(); // don't forget
                    counterData += response.length;
                    return response;
                } else {
                    //System.out.println("HTTP ERROR :: "+ conn.getResponseCode());
                    conn.disconnect(); // don't forget
                    return null;
                }
            } catch (Exception e) {
                //e.printStackTrace();
                conn.disconnect(); // don't forget
                return null;
            }
        }
    }
}
