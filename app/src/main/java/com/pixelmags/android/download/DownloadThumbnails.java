package com.pixelmags.android.download;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.util.BaseApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by austincoutinho on 16/11/15.
 */
public class DownloadThumbnails implements Runnable {

    private static final String THUMBNAIL_DIR_PREFIX="/Temp/IssueThumbnails";

    private int issueIndex;
    private String url;
    private String issueFileName;
    private String savedPath;

    private boolean isDownloaded;

    DownloadThumbnails(String downloadUrl, int magIndex, String issue){
        this.url = downloadUrl;
        this.issueIndex = magIndex;
        this.issueFileName = issue;
        this.isDownloaded = false;
    }

    public static ArrayList<Magazine> allIssues;

    public static ArrayList<Magazine> DownloadAllThumbnailData(ArrayList<Magazine> allIssuesList) {

        allIssues = allIssuesList;

        createAndClearStorageDirectory();

        ArrayList<Thread> thumbnailDownloadThreads = new ArrayList<Thread>();

        for (int i = 0; i < allIssuesList.size(); i++) {
            Magazine mag = allIssuesList.get(i);
            String fileName = String.valueOf(mag.id)+".jpg";
            DownloadThumbnails downloadThread = new DownloadThumbnails(mag.thumbnailURL, i, fileName);

            Thread t1 = new Thread(downloadThread);
            thumbnailDownloadThreads.add(t1);
        }


        for (Thread thread : thumbnailDownloadThreads){
                thread.start();
        }

        try{

            for (Thread joinThread : thumbnailDownloadThreads){
                joinThread.join();
            }

        }catch(Exception e){
            e.printStackTrace();
        }


        return allIssues;
    }


    public static void updateIssueData(int index, String savedPath, boolean isDownloaded){

        allIssues.get(index).thumbnailDownloadedInternalPath = savedPath;
        allIssues.get(index).isThumbnailDownloaded = isDownloaded;

    }

    @Override
    public void run() {

        try {
            InputStream in = new java.net.URL(url).openStream();
            Bitmap thumbnailBitmap = BitmapFactory.decodeStream(in);

            savedPath = saveToInternalSorage(thumbnailBitmap);
            updateIssueData(issueIndex, savedPath, isDownloaded);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String saveToInternalSorage(Bitmap bitmapImage){


        ContextWrapper cw = new ContextWrapper(BaseApp.getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File folder = new File(directory.getAbsolutePath()+THUMBNAIL_DIR_PREFIX);

        // Create imageDir
        File myPath=new File(folder,this.issueFileName);

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


}
