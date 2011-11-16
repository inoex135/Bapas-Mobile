/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javan.app.view;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import org.javan.app.main.BapasMobile;
import org.javan.app.main.R;
import org.javan.app.model.BapasObject;
import org.javan.app.view.adapter.BapasObjectAdapter;

/**
 *
 * @author 144key
 */
public class BapasScreen {
    /** ATTRIBUTES **/
        private BapasMobile mainApps;
        private LinearLayout bapasListScreen;
        private LinearLayout bapasDetailScreen;
        private LinearLayout bapasAddScreen;
        private LinearLayout bapasEditScreen;
    /** METHODS **/
    public BapasScreen(BapasMobile _mainApps) {
        mainApps = _mainApps;
    }
    
    public LinearLayout getBapasListScreen() {
        bapasListScreen = (LinearLayout) LinearLayout.inflate(mainApps, R.layout.main, null);
        TableLayout TL = (TableLayout) bapasListScreen.findViewById(R.id.tabelbapas);
        TL.setStretchAllColumns(true);
        ((Button) bapasListScreen.findViewById(R.id.addbutton)).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                mainApps.bc.showAddBapas();
            }
        });
        ((ImageButton) bapasListScreen.findViewById(R.id.datareload)).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                mainApps.bc.showListBapas();
            }
        });
        return bapasListScreen;
    }
    
    public void initBapasListScreen(final BapasObject[] listObj) {
        if (bapasListScreen != null) {
            mainApps.runOnUiThread(new Runnable() {
                public void run() {
                    TableLayout TL = (TableLayout) bapasListScreen.findViewById(R.id.tabelbapas);
                    TL.setStretchAllColumns(true);
                    if (listObj != null && listObj.length > 0) {
                        for (int i = 0; i < listObj.length; i++) {
                            TableRow TR = new BapasObjectAdapter(mainApps, listObj[i]).getView();
                            final int ii = i;
                            TR.setOnClickListener(new OnClickListener() {
                                public void onClick(View arg0) {
                                    mainApps.bc.showDetailBapas(listObj[ii].getId());
                                }
                            });
                            TL.addView(TR);
                        }
                    } else {
                        TL.addView(new BapasObjectAdapter(mainApps, null).getView());
                    }  
                }
            });      
        }
    }
    
    public LinearLayout getAndInitBapasAddScreen() {
        bapasAddScreen = (LinearLayout) LinearLayout.inflate(mainApps, R.layout.addscreen, null);
        mainApps.interObject.put("input_kode", ((EditText) bapasAddScreen.findViewById(R.id.kode_data)));
        mainApps.interObject.put("input_nama", ((EditText) bapasAddScreen.findViewById(R.id.nama_data)));
        ((Button) bapasAddScreen.findViewById(R.id.addbutton)).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                mainApps.bc.addBapas();
            }
        });
        return bapasAddScreen;
    }
    
    public LinearLayout getBapasEditScreen() {
        bapasEditScreen = (LinearLayout) LinearLayout.inflate(mainApps, R.layout.editscreen, null);
        return bapasEditScreen;
    }
    
    public void initBapasEditScreen(final int nomor, final String kode, final String nama) {        
        if (bapasEditScreen != null) {
            mainApps.interObject.put("input_nomor", ((EditText) bapasEditScreen.findViewById(R.id.nomor_data)));
            mainApps.interObject.put("input_kode", ((EditText) bapasEditScreen.findViewById(R.id.kode_data)));
            mainApps.interObject.put("input_nama", ((EditText) bapasEditScreen.findViewById(R.id.nama_data)));
            ((Button) bapasEditScreen.findViewById(R.id.savebutton)).setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    mainApps.bc.saveEditedBapas(nomor);
                }
            });
            mainApps.runOnUiThread(new Runnable() {
                public void run() {
                    ((EditText) bapasEditScreen.findViewById(R.id.nomor_data)).setText(""+nomor);
                    ((EditText) bapasEditScreen.findViewById(R.id.kode_data)).setText(kode);
                    ((EditText) bapasEditScreen.findViewById(R.id.nama_data)).setText(nama);
                }
            });  
        }
    }
    
    public LinearLayout getBapasDetailScreen() {
        bapasDetailScreen = (LinearLayout) LinearLayout.inflate(mainApps, R.layout.detailscreen, null);
        return bapasDetailScreen;
    }
    
    public void initBapasDetailScreen(final int nomor, final String kode, final String nama) {
        if (bapasDetailScreen != null) {
            ((Button) bapasDetailScreen.findViewById(R.id.editbutton)).setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    // Edit Action
                    mainApps.bc.showEditBapas(nomor);
                }
            });
            ((Button) bapasDetailScreen.findViewById(R.id.deletebutton)).setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    // Delete Action
                    mainApps.bc.deleteBapas(nomor);
                }
            });
            mainApps.runOnUiThread(new Runnable() {
                public void run() {
                    ((TextView) bapasDetailScreen.findViewById(R.id.nomor_data)).setText(""+nomor);
                    ((TextView) bapasDetailScreen.findViewById(R.id.kode_data)).setText(kode);
                    ((TextView) bapasDetailScreen.findViewById(R.id.nama_data)).setText(nama);
                }
            });  
        }
    }
}
