package com.pixelmags.android.download;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.Issue;
import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.datamodels.Page;
import com.pixelmags.android.datamodels.PageTypeImage;
import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.util.BaseApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import com.pixelmags.android.pixelmagsapp.R;

/**
 * Created by austincoutinho on 17/12/15.
 */
public class DownloadIssueThreaded implements Runnable {

    // form Issues/(Magazine_number)/(issue number)/PDF/

    private static final String ISSUE_DIR_PREFIX_1 = "/Issues/"+ Config.Magazine_Number+"/";
    private static final String ISSUE_DIR_PREFIX_PDF = "/PDF";

    private int issuePageIndex;
    private String url;
    private String issuePageFileName;

    private boolean isDownloaded;

    DownloadIssueThreaded(String downloadUrl, int pageIndex, String pageName){
        this.url = downloadUrl;
        this.issuePageIndex = pageIndex;
        this.issuePageFileName = pageName;
        this.isDownloaded = false;
    }

    public static Issue mIssue;

    public static boolean DownloadIssuePages(Issue issue) {

        mIssue = issue;

        AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
        if(mDbReader.issueDownloadPreChecksAndDownload(mDbReader.getWritableDatabase(), issue)){
            System.out.println("ISSUE " +issue.issueID+ " QUEUED for download");
            return true;
        }else{
            System.out.println("ISSUE " +issue.issueID+ " Download Init Failed");
            return false;
        }


        // This works!!!

    /*
        ArrayList<Thread> pagesDownloadThreads = new ArrayList<Thread>();


        for (int i = 0; i < mIssue.pages.size(); i++) {

            String fileName = String.valueOf(i)+".jpg";

            PageTypeImage page = (PageTypeImage) mIssue.pages.get(i);
            PageTypeImage.PageDetails pageDetails = page.getPageDetails(PageTypeImage.MediaType.LARGE);

            String pageURL = pageDetails.url;

            DownloadIssueThreaded downloadThread = new DownloadIssueThreaded(pageURL, i, fileName);
            Thread t1 = new Thread(downloadThread);
            pagesDownloadThreads.add(t1);

        }

        for (Thread thread : pagesDownloadThreads){
                thread.start();
        }
    */

    }


    public static void updateIssuePageData(int index, String savedPath, boolean isDownloaded){

        mIssue.pages.get(index).savedPath = savedPath;
        mIssue.pages.get(index).isDownloaded = isDownloaded;

    }

    @Override
    public void run() {

        try {


            System.out.println("----- Download :: run ---- "+ issuePageIndex);

            InputStream in = new java.net.URL(url).openStream();

            if(in != null){

                ContextWrapper cw = new ContextWrapper(BaseApp.getContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                String pageImageDir = ISSUE_DIR_PREFIX_1 + mIssue.issueID + ISSUE_DIR_PREFIX_PDF;

                File folder = new File(directory.getAbsolutePath() + pageImageDir);
                folder.mkdirs();

                // Create page image file, specifying the path, and the filename which we want to save the file as.
                File pageImage = new File(folder, this.issuePageFileName);

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

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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
