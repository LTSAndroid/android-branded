package com.pixelmags.android.pixelmagsapp.service;

import android.content.Context;
import android.content.ContextWrapper;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.datamodels.SingleDownloadIssueTracker;
import com.pixelmags.android.storage.SingleIssueDownloadDataSet;
import com.pixelmags.android.util.BaseApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by austincoutinho on 17/12/15.
 */
public class DownloadSinglePageThread implements Runnable {

    // form Issues/(Magazine_number)/(issue number)/PDF/

    private static final String ISSUE_DIR_PREFIX_1 = "/Issues/"+ Config.Magazine_Number+"/";
    private static final String ISSUE_DIR_PREFIX_PDF = "/PDF";


    private boolean isPriority;
    private boolean isDownloaded;
    private AllDownloadsIssueTracker issueAllDownloadsTracker;
    private SingleDownloadIssueTracker pageSingleDownloadTracker;

    DownloadSinglePageThread(AllDownloadsIssueTracker allDownloadsTracker, SingleDownloadIssueTracker pageTracker, boolean setAsPriority){

        this.issueAllDownloadsTracker = allDownloadsTracker;
        this.pageSingleDownloadTracker = pageTracker;
        this.isPriority = setAsPriority; // this is the value that will be used in the Comparator to download the image as priority
        this.isDownloaded = false;

    }

    public boolean getPriority(){
        return isPriority;
    }

    public int getPageNo(){
        return pageSingleDownloadTracker.pageNo;
    }

    private String getPageFileName(){

        String fileName = String.valueOf(pageSingleDownloadTracker.pageNo)+".jpg";
        return fileName;
    }

    @Override
    public void run() {

        try {


            System.out.println("Download :: downloading page ---- "+ pageSingleDownloadTracker.pageNo );

            InputStream in = new URL(pageSingleDownloadTracker.urlPdfLarge).openStream();

            if(in != null){

                ContextWrapper cw = new ContextWrapper(BaseApp.getContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

                String pageImageDir = ISSUE_DIR_PREFIX_1 + issueAllDownloadsTracker.issueID + ISSUE_DIR_PREFIX_PDF;

                File folder = new File(directory.getAbsolutePath() + pageImageDir);
                folder.mkdirs();

                // Create page image file, specifying the path, and the filename which we want to save the file as.
                File pageImage = new File(folder, getPageFileName());

                //this will be used to write the downloaded data into the file we created
                FileOutputStream fileOutput = new FileOutputStream(pageImage);

                //create a buffer
                byte[] buffer = new byte[1024];
                int bufferLength = 0; //used to store a temporary size of the buffer

                //read through the input buffer and write the contents to the file
                while ( (bufferLength = in.read(buffer)) > 0 ) {
                    //add the data in the buffer to the file in the file output stream (the file on the sd card)
                    fileOutput.write(buffer, 0, bufferLength);
                }
                //close the output stream when done
                fileOutput.close();

                registerDownloadAsComplete();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Update the UniqueDownloadTable of the Issue after each page download
    private void registerDownloadAsComplete(){

        this.isDownloaded = true;
        pageSingleDownloadTracker.downloadStatusPdfLarge = SingleIssueDownloadDataSet.DOWNLOAD_STATUS_COMPLETED;

        try{

            /* // DO not do update after every page as that locks the db out for a long time.
            SingleIssueDownloadDataSet mDbDownloadTableWriter = new SingleIssueDownloadDataSet(BaseApp.getContext());
            boolean result = mDbDownloadTableWriter.updateIssuePageEntry(mDbDownloadTableWriter.getWritableDatabase(), pageSingleDownloadTracker, issueAllDownloadsTracker.uniqueIssueDownloadTable);
            mDbDownloadTableWriter.close();
            */

            System.out.println("Download Complete :: " + pageSingleDownloadTracker.downloadedLocationPdfLarge);

        }catch(Exception e){
            e.printStackTrace();
        }

    }

/*


    private String saveToInternalSorage(Bitmap bitmapImage){


        System.out.println("----- saveToInternalSorage ---- ");

        ContextWrapper cw = new ContextWrapper(BaseApp.getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        String pageImageDir = ISSUE_DIR_PREFIX_1 + mIssue.issueID + ISSUE_DIR_PREFIX_PDF;

        File folder = new File(directory.getAbsolutePath() + pageImageDir);
        folder.mkdirs();

        // Create imageDir
        File myPath=new File(folder, this.issuePageFileName);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(myPath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            isDownloaded = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return myPath.getAbsolutePath();
    }

*/

/*

    private static void createAndClearStorageDirectory(){

        ContextWrapper cw = new ContextWrapper(BaseApp.getContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File folder = new File(directory.getAbsolutePath()+THUMBNAIL_DIR_PREFIX);
        folder.mkdirs();

        for(File file: folder.listFiles()) {
            file.delete();
        }

    }
*/

}
