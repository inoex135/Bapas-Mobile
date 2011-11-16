/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javan.app.view.adapter;

import android.content.Context;
import android.widget.TableRow;
import android.widget.TextView;
import org.javan.app.main.R;
import org.javan.app.model.BapasObject;

/**
 *
 * @author 144key
 */
public class BapasObjectAdapter {
    private Context ctx;
    private BapasObject bkr = null;

    public BapasObjectAdapter(Context ctx, BapasObject bkr) {
        this.ctx = ctx;
        this.bkr = bkr;
    }

    public TableRow getView() {
        TableRow TR = null;
        if (bkr != null) {
            TR = (TableRow) TableRow.inflate(ctx, R.layout.baristabel, null);
            ((TextView) TR.findViewById(R.id.nomor)).setText(""+bkr.getId());
            ((TextView) TR.findViewById(R.id.kode)).setText(bkr.getKode());
            ((TextView) TR.findViewById(R.id.nama)).setText(bkr.getNama());
        } else {
            TR = (TableRow) TableRow.inflate(ctx, R.layout.bariskosong, null);
        }
        return TR;
    }  
}
