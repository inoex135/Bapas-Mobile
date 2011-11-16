/*
 * File  : BapasServerAction.java
 * Class : BapasServerAction
 */

package org.javan.app.main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import java.util.Hashtable;
import org.javan.app.util.ServerConnection;

/**
 * Bapas Server Action (Abstract Class)
 * @author joel
 */
public class BapasServerAction {
    /** ATTRIBUTES **/
        private ServerConnection GA_serverConnection = null;
        private BapasServerAction currentAction = null;        
        protected String[] GA_serverResponse = null;
        protected Hashtable GA_objectResult = null;
        protected int intermediateState = 0;
        protected ProgressDialog internalDialog = null;
        public final static int ACTION_FAILED = 0;
        public final static int ACTION_SUCCESS = 1;    
        public boolean threadRunning = false;
        private final static boolean debugMode = false;
    /** METHODS **/
    public BapasServerAction() {
        currentAction = BapasServerAction.this;
    }
    public BapasServerAction getCurrentAction() {
        return currentAction;
    }
    private void printServerResponse() {
        if (GA_serverResponse != null && GA_serverResponse.length > 0) {
            int nbReponse = GA_serverResponse.length;
            for (int i = 0; i < nbReponse; i++) {
                if (GA_serverResponse[i] != null)
                    System.out.println("Server Response "+i+" >> "+GA_serverResponse[i]);
            }
        } else {
            System.out.println("No Response From Server !");
        }
    }
    public String[] rawProcess() {
        try {
            GA_serverConnection = new ServerConnection();
            prepareDataBody(GA_serverConnection);
            if (debugMode) printServerResponse();
            if (GA_serverResponse != null && GA_serverResponse.length > 0) {
                return GA_serverResponse;
            }
        } catch (Exception ex) {}
        return null;
    }
    public void cancelCurrentAction() {
        threadRunning = false;
    }
    public boolean isStillRunning() {
        return threadRunning;
    }
    public void process(final BapasMobile _G, final boolean useWaitDialog) {
        GA_serverConnection = new ServerConnection();        
        _G.runOnUiThread(new Runnable() {
            public void run() {
                if (useWaitDialog) {
                    internalDialog = ProgressDialog.show(_G, "", "Please wait ...", true, true, new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface arg0) {
                            GA_serverConnection.stopControlableConnection();
                            cancelCurrentAction();
                        }
                    });
                } else internalDialog = null;
            }
        });
        threadRunning = true;
        new Thread(new Runnable() {
            public void run() {
                try {
                    prepareDataBody(GA_serverConnection);
                    if (debugMode) printServerResponse();
                    if (threadRunning) {
                        processDataBody(GA_serverResponse);
                    }
                    _G.runOnUiThread(new Runnable() {
                        public void run() {
                            if (internalDialog != null) internalDialog.dismiss();
                            if (threadRunning) {
                                threadRunning = false;
                                new Thread(new Runnable() {
                                    public void run() {
                                        updateUIBody(ACTION_SUCCESS);
                                    }
                                }).start();                                
                            }
                        }
                    });
                } catch (final Exception ex) {
                    System.out.println(">> Bapas Action Exception : "+ex.getMessage());
                    _G.runOnUiThread(new Runnable() {
                        public void run() {
                            if (internalDialog != null) internalDialog.dismiss();
                            if (threadRunning) {
                                threadRunning = false;
                                new Thread(new Runnable() {
                                    public void run() {
                                        updateUIBody(ACTION_FAILED);
                                    }
                                }).start();
                                System.gc();
                            }
                        }
                    });
                }
            }
        }).start();
    }
    protected void prepareDataBody(final ServerConnection conn) throws Exception {};
    protected void processDataBody(final String[] responseFromServer) throws Exception {};
    protected void updateUIBody(final int mode) {};
}
