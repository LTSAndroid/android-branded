package com.pixelmags.android.pixelmagsapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


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
- Download via wifi only (make this setting avaiable to the user)

 */

public class DownloadService extends Service {

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("DownloadService Started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("DownloadService Stopped");
    }

}
