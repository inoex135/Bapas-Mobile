/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javan.app.main;

import java.util.Hashtable;
import org.javan.app.util.ServerConnection;

/**
 *
 * @author 144key
 */
public class BapasAPI {
    /** ATTRIBUTES **/
        private static String server = "";
        private ServerConnection connection = null;
        private final static boolean debugMode = true;
    /** METHODS **/
    /***
     * GilkorAPI Constructor
     * @param _conn
     * @param _server 
     ***/
    public BapasAPI(ServerConnection _conn, String _server) {
        connection = _conn;
        server = _server;
    }
    
    public String bapasList() {
        String url = server+"/api/list.htm";
        return post(url, null);
    }

    public String bapasAdd(String _kode, String _nama) {
        String url = server+"/api/doAdd.htm";
        StringBuffer params = new StringBuffer();
        params.append("kode="+_kode);
        params.append("&nama="+_nama);
        return post(url, params);
    }
    
    public String bapasDel(int _id) {
        String url = server+"/api/delete.htm";
        StringBuffer params = new StringBuffer();
        params.append("id="+_id);
        return post(url, params);
    }

    public String bapasSave(int _id, String _kode, String _nama) {
        String url = server+"/api/doSave.htm";
        StringBuffer params = new StringBuffer();
        params.append("id="+_id);
        params.append("&kode="+_kode);
        params.append("&nama="+_nama);
        return post(url, params);
    }
    
    public String bapasGet(int _id) {
        String url = server+"/api/get.htm";
        StringBuffer params = new StringBuffer();
        params.append("id="+_id);
        return post(url, params);
    }
    
    /**
     * API EXECUTOR
     **/
    private String post(String server, Hashtable params, String[] fileField, String[] fileName, String[] fileType, byte[][] fileByte) {
        if (debugMode) {
            if (fileName != null && fileByte != null) System.out.println("File To Send ... "+fileName[0]);
            System.out.println("DEBUG API REQUEST :: "+(server != null ? server : "")+"?"+(params != null ? params.toString() : ""));
        }
        connection.makeControlableConnection(server, params, fileField, fileName, fileType, fileByte);
        byte[] tmpResponse = connection.getControlableConnectionResponse();
        connection.stopControlableConnection();
        if (debugMode) System.out.println("DEBUG API RESPONSE :: " + ((tmpResponse == null) ? "" : new String(tmpResponse)));
        return ((tmpResponse == null) ? "" : new String(tmpResponse));
    }

    private String post(String server, StringBuffer params) {
        if (debugMode) System.out.println("DEBUG API REQUEST :: "+(server != null ? server : "")+"?"+(params != null ? params.toString() : ""));
        connection.makeControlableConnection(server, params == null ? null : params.toString().getBytes());
        byte[] tmpResponse = connection.getControlableConnectionResponse();
        connection.stopControlableConnection();
        if (debugMode) System.out.println("DEBUG API RESPONSE :: " + ((tmpResponse == null) ? "" : new String(tmpResponse)));
        return ((tmpResponse == null) ? "" : new String(tmpResponse));
    }
}
