package org.javan.app.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Hashtable;
import org.javan.app.controller.BapasController;

public class BapasMobile extends Activity
{
    /** INNER CLASS **/
    public static class ViewHolder {
        public TextView text1;
        public TextView text2;
        public TextView text3;
    }
    /** ATTRIBUTES **/
    public final static String SERVER = "http://javan.web.id:8080/Bapas"; // Host SERVER
    private AlertDialog.Builder DD = null;
    private AlertDialog alert = null;
    protected final int ABOUT_DIALOG_ID = 1;
    private LinearLayout aboutApp = null;
    public BapasController bc = null;
    public Hashtable interObject = null;
    public static int internalState = 0;
    public final static int LISTSCREEN = 1;
    public final static int ADDSCREEN = 2;
    public final static int DETAILSCREEN = 3;
    public final static int EDITSCREEN = 4;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        interObject = new Hashtable();
        bc = new BapasController(this);
        bc.showListBapas();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == ABOUT_DIALOG_ID) {
            DD = new AlertDialog.Builder(this);
            DD.setView(aboutApp)
                .setTitle("About Application")
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // NONE ACTION
                        alert.dismiss();
                    }
                });
            alert = DD.create();
            return alert;
        }
        return null;
    }
    
    private void showAboutDialog () {
        aboutApp = new LinearLayout(this);
        aboutApp.setOrientation(LinearLayout.VERTICAL);
        aboutApp.setPadding(5, 3, 3, 3);
        aboutApp.setGravity(Gravity.CENTER);
        ScrollView descScrollArea = new ScrollView(this);
        descScrollArea.setFillViewport(false);
        descScrollArea.setHorizontalScrollBarEnabled(false);
        descScrollArea.setVerticalScrollBarEnabled(true);
            LinearLayout linearArea = new LinearLayout(this);
            linearArea.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.FILL_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
            linearArea.setOrientation(LinearLayout.VERTICAL);
            linearArea.setVerticalGravity(Gravity.CENTER_HORIZONTAL);
                ImageButton logo = new ImageButton(this); logo.setImageResource(R.drawable.about);
                logo.setBackgroundColor(Color.TRANSPARENT);
                TextView logoLabel = new TextView(this);
                logoLabel.setTextColor(Color.WHITE);
                logoLabel.setTextSize(14);
                logoLabel.setGravity(Gravity.CENTER);
                logoLabel.setText("Bapas Mobile Prototype");
                TextView subLogoLabel = new TextView(this);
                subLogoLabel.setTextColor(Color.argb(255, 148, 169, 244));
                subLogoLabel.setTextSize(14);
                subLogoLabel.setTypeface(Typeface.DEFAULT_BOLD);
                subLogoLabel.setGravity(Gravity.CENTER);
                subLogoLabel.setText("Copyright (c) 2011\nJavan IT Services\nwww.javan.co.id");
                subLogoLabel.setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.javan.co.id"));
                            startActivity(intent);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            linearArea.addView(logo);
            linearArea.addView(logoLabel);
            linearArea.addView(subLogoLabel);
        descScrollArea.addView(linearArea);
        aboutApp.addView(descScrollArea);
        // Show Up ...
        removeDialog(ABOUT_DIALOG_ID);
        showDialog(ABOUT_DIALOG_ID);
    }
        
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (internalState) {
                case LISTSCREEN     :   moveTaskToBack(true);
                                        break;
                case ADDSCREEN      :   bc.showListBapas();
                                        break;
                case EDITSCREEN     :   bc.showListBapas();
                                        break;
                case DETAILSCREEN   :   bc.showListBapas();
                                        break;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
        
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equals("About")) {
            showAboutDialog();
        } else if (item.getTitle().toString().equals("Exit")) {
            shootSelf();
        }
        return true;
    }
            
    public static void shootSelf() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    android.os.Process.killProcess(android.os.Process.myPid());
                } catch (Throwable e) {
                    System.out.println("Problem killing self : "+e.getMessage());
                }
            }
        }).start();
    }
}
