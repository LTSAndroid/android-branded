package com.pixelmags.android.pixelmagsapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.util.BaseApp;


/*

Created : Austin Coutinho

Perform all large downloads in the background

Usage :
- download thumnails in batches

- download the issue in batches
    - pause issue download

- service to run at start
- end when the all the downloads are complete and the app is no longer being used

- run a batch download of a table
- Download via wifi only (make this setting available to the user)




 */

public class DownloadService extends Service {

    AllDownloadsDataSet allDownloads;
    DownloadsManager downloadsManager;

    DownloadManagerAsyncTask mDMTask;
    boolean DMTaskCompleted = true;

    public DownloadService() {
    }


    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {

        public DownloadService getService() {
            return DownloadService.this;
        }

    }

    // This is the object that receives interactions from clients.
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("DownloadService Started");
        initiateDownloadsProcessing();


        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("DownloadService Stopped");
    }


    public void requestServiceShutdown(){
        stopSelf();
    }


    public void newDownloadRequested(){
        System.out.println("<< NEW Download NOTIFICATION Recieved >>");
        initiateDownloadsProcessing();

    }

    private void initiateDownloadsProcessing(){

        // DMTaskCompleted is used to check if the Download task is still running;
        if(DMTaskCompleted)
        {
            // proceed to process the downloads in the background
            mDMTask = new DownloadManagerAsyncTask();
            mDMTask.execute((String)null);

        }else{
            // just let the DownloadsManger know that a request is pending
            downloadsManager = DownloadsManager.getInstance();
            downloadsManager.setRequestPending();
        }
    }

    /**
     *
     * Represents an asynchronous task used to load the issues.
     *
     */
    public class DownloadManagerAsyncTask extends AsyncTask<String, String, Boolean> {

        DownloadManagerAsyncTask() {
            DMTaskCompleted = false;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {

                DMTaskCompleted = false;

                downloadsManager = DownloadsManager.getInstance();
                downloadsManager.processDownloadsTable();

                return true;

            }catch (Exception e){
                e.printStackTrace();
            }

            return false;

        }

        protected void onPostExecute(Boolean result) {
            DMTaskCompleted = true;
        }

        @Override
        protected void onCancelled() {
            mDMTask = null;
        }
    }

}
