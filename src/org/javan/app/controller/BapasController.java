/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javan.app.controller;

import android.widget.EditText;
import android.widget.Toast;
import java.util.Hashtable;
import org.javan.app.main.BapasAPI;
import org.javan.app.main.BapasMobile;
import org.javan.app.main.BapasServerAction;
import org.javan.app.model.BapasObject;
import org.javan.app.util.ServerConnection;
import org.javan.app.view.BapasScreen;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author 144key
 **/
public class BapasController {
    /** ATTRIBUTES **/
        private BapasMobile mainApps;
        private BapasScreen bs = null;
        private BapasObject[] bapasObjectList = null;        
    /** METHODS **/
    public BapasController(BapasMobile mainProcess) {
        this.mainApps = mainProcess;
        bs = new BapasScreen(mainProcess);         
    }
    
    public void showListBapas() {
        BapasMobile.internalState = BapasMobile.LISTSCREEN;
        mainApps.setContentView(bs.getBapasListScreen());
        bapasObjectList = null;
        new BapasServerAction() {
            @Override
            protected void prepareDataBody(ServerConnection conn) throws Exception {
                GA_serverResponse = new String[1];
                GA_serverResponse[0] = (new BapasAPI(conn, BapasMobile.SERVER)).bapasList();
            }
            @Override
            protected void processDataBody(String[] responseFromServer) throws Exception {
                try {
                    JSONObject mainObj = new JSONObject(responseFromServer[0]);
                    JSONArray list = mainObj.getJSONArray("arrayOfObject");
                    int nbBapasObj = list.length();
                    bapasObjectList = new BapasObject[nbBapasObj];
                    for (int i = 0; i < nbBapasObj; i++) {
                        JSONObject obj = list.getJSONObject(i);
                        bapasObjectList[i] = new BapasObject(obj.getInt("id"), obj.getString("kode"), obj.getString("nama"));
                    }
                } catch (Exception e) {
                    bapasObjectList = null;
                }
            }
            @Override
            protected void updateUIBody(int mode) {
                bs.initBapasListScreen(bapasObjectList);           
            }            
        }.process(mainApps, true);
    }
    
    public void showDetailBapas(final int id) {
        BapasMobile.internalState = BapasMobile.DETAILSCREEN;
        mainApps.setContentView(bs.getBapasDetailScreen());
        new BapasServerAction() {
            @Override
            protected void prepareDataBody(ServerConnection conn) throws Exception {
                GA_serverResponse = new String[1];
                GA_serverResponse[0] = (new BapasAPI(conn, BapasMobile.SERVER)).bapasGet(id);
            }
            @Override
            protected void processDataBody(String[] responseFromServer) throws Exception {
                try {
                    JSONObject mainObj = new JSONObject(responseFromServer[0]);
                    if (!mainObj.getBoolean("error")) {
                        JSONObject obj = mainObj.getJSONObject("object");
                        BapasObject bo = new BapasObject(obj.getInt("id"), obj.getString("kode"), obj.getString("nama"));
                        GA_objectResult = new Hashtable();
                        GA_objectResult.put("bo", bo);
                        intermediateState = 1;
                    } else intermediateState = 0;
                } catch (Exception e) {
                    GA_objectResult = null;
                    intermediateState = 0;
                }
            }
            @Override
            protected void updateUIBody(int mode) {
                if (intermediateState == 1) {
                    BapasObject bo = (BapasObject) GA_objectResult.get("bo");
                    if (bo != null)
                        bs.initBapasDetailScreen(bo.getId(), bo.getKode(), bo.getNama());                       
                } else {
                    bs.initBapasDetailScreen(0, "can't retrieved", "can't retrieved"); 
                }       
            }            
        }.process(mainApps, true);
    }
    
    public void showAddBapas() {
        BapasMobile.internalState = BapasMobile.ADDSCREEN;
        mainApps.setContentView(bs.getAndInitBapasAddScreen());
    }
    
    public void addBapas() {
        final String kode = ((EditText) mainApps.interObject.get("input_kode")).getText().toString();
        final String nama = ((EditText) mainApps.interObject.get("input_nama")).getText().toString();
        new BapasServerAction() {
            @Override
            protected void prepareDataBody(ServerConnection conn) throws Exception {
                GA_serverResponse = new String[1];
                GA_serverResponse[0] = (new BapasAPI(conn, BapasMobile.SERVER)).bapasAdd(kode, nama);
            }
            @Override
            protected void processDataBody(String[] responseFromServer) throws Exception {
                intermediateState = 0;
                try {
                    JSONObject obj = new JSONObject(responseFromServer[0]);
                    intermediateState = obj.getBoolean("error") ? 1 : 2;
                } catch (Exception e) {
                }
            }
            @Override
            protected void updateUIBody(int mode) {
                // Back To List of Bapas
                mainApps.runOnUiThread(new Runnable() {
                    public void run() {
                        showListBapas();
                        if (intermediateState == 2) {
                            Toast.makeText(mainApps, "Bapas succesfully added !", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mainApps, "Error ! Can't add Bapas !", Toast.LENGTH_LONG).show();
                        }
                    }
                });          
            }            
        }.process(mainApps, true);            
    }
    
    public void showEditBapas(final int id) {
        BapasMobile.internalState = BapasMobile.EDITSCREEN;
        mainApps.setContentView(bs.getBapasEditScreen());
        new BapasServerAction() {
            @Override
            protected void prepareDataBody(ServerConnection conn) throws Exception {
                GA_serverResponse = new String[1];
                GA_serverResponse[0] = (new BapasAPI(conn, BapasMobile.SERVER)).bapasGet(id);
            }
            @Override
            protected void processDataBody(String[] responseFromServer) throws Exception {
                try {
                    JSONObject mainObj = new JSONObject(responseFromServer[0]);
                    JSONObject obj = mainObj.getJSONObject("object");
                    BapasObject bo = new BapasObject(obj.getInt("id"), obj.getString("kode"), obj.getString("nama"));
                    GA_objectResult = new Hashtable();
                    GA_objectResult.put("bo", bo);
                } catch (Exception e) {
                    GA_objectResult = null;
                }
            }
            @Override
            protected void updateUIBody(int mode) {
                if (GA_objectResult != null) {
                    BapasObject bo = (BapasObject) GA_objectResult.get("bo");
                    if (bo != null)
                        bs.initBapasEditScreen(bo.getId(), bo.getKode(), bo.getNama());                       
                } else {                    
                }       
            }            
        }.process(mainApps, true);
    }
    
    public void saveEditedBapas(int id) {
        final String nomor = ((EditText) mainApps.interObject.get("input_nomor")).getText().toString();
        final String kode = ((EditText) mainApps.interObject.get("input_kode")).getText().toString();
        final String nama = ((EditText) mainApps.interObject.get("input_nama")).getText().toString();
        new BapasServerAction() {
            @Override
            protected void prepareDataBody(ServerConnection conn) throws Exception {
                GA_serverResponse = new String[1];
                int id = 0;
                try {
                    id = Integer.parseInt(nomor);
                } catch (Exception e) {}
                GA_serverResponse[0] = (new BapasAPI(conn, BapasMobile.SERVER)).bapasSave(id, kode, nama);
            }
            @Override
            protected void processDataBody(String[] responseFromServer) throws Exception {
                intermediateState = 0;
                try {
                    JSONObject obj = new JSONObject(responseFromServer[0]);
                    intermediateState = obj.getBoolean("error") ? 1 : 2;
                } catch (Exception e) {
                }
            }
            @Override
            protected void updateUIBody(int mode) {
                // Back To List of Bapas
                mainApps.runOnUiThread(new Runnable() {
                    public void run() {
                        showListBapas();
                        if (intermediateState == 2) {
                            Toast.makeText(mainApps, "Bapas succesfully saved !", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mainApps, "Error ! Can't save Bapas !", Toast.LENGTH_LONG).show();
                        }
                    }
                });          
            }            
        }.process(mainApps, true);
    }
    
    public void deleteBapas(final int id) {
        new BapasServerAction() {
            @Override
            protected void prepareDataBody(ServerConnection conn) throws Exception {
                GA_serverResponse = new String[1];
                GA_serverResponse[0] = (new BapasAPI(conn, BapasMobile.SERVER)).bapasDel(id);
            }
            @Override
            protected void processDataBody(String[] responseFromServer) throws Exception {
                intermediateState = 0;
                try {
                    JSONObject obj = new JSONObject(responseFromServer[0]);
                    intermediateState = obj.getBoolean("error") ? 1 : 2;
                } catch (Exception e) {
                }
            }
            @Override
            protected void updateUIBody(int mode) {
                // Back To List of Bapas
                mainApps.runOnUiThread(new Runnable() {
                    public void run() {                        
                        if (intermediateState == 2) {
                            showListBapas();
                            Toast.makeText(mainApps, "Bapas succesfully deleted!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mainApps, "Error ! Can't delete Bapas !", Toast.LENGTH_LONG).show();
                        }
                    }
                });          
            }            
        }.process(mainApps, true);
    }
}
