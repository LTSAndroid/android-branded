package com.pixelmags.android.download;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.util.BaseApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by austincoutinho on 16/11/15.
 */
public class DownloadThumbnails implements Runnable {

    private static final String THUMBNAIL_DIR_PREFIX="/Temp/IssueThumbnails";
    private static final String THUMBNAILS_DOWNLOADEDISSUE_DIR_PREFIX="/Permanent/DownloadedIssueThumbnails";

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

    public static void copyThumbnailOfIssueDownloaded(String issueID) {

        try{

            ContextWrapper cw = new ContextWrapper(BaseApp.getContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

            String sourcePath = directory.getAbsolutePath() + THUMBNAIL_DIR_PREFIX +"/"+issueID+".jpg";
            File source = new File(sourcePath);


            File downloadFolder = new File(directory.getAbsolutePath() + THUMBNAILS_DOWNLOADEDISSUE_DIR_PREFIX);
            downloadFolder.mkdirs();
            String thumbnailFilename = issueID+".jpg";
            File destinationPath = new File(downloadFolder, thumbnailFilename);


            //FileChannel inChannel = new FileInputStream(source).getChannel();
            // FileChannel outChannel = new FileOutputStream(destinationPath).getChannel();

            try {
                //inChannel.transferTo(0, inChannel.size(), outChannel);
                if(source.exists()){

                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(destinationPath);

                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    in.close();
                    out.close();

                }
            } catch (Exception e){
                e.printStackTrace();
            }
            /*
                finally {
                if (inChannel != null)
                    inChannel.close();
                if (outChannel != null)
                    outChannel.close();
            }*/

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static String getIssueDownloadedThumbnailStorageDirectory(String issueID){

        String path = null;

        ContextWrapper cw = new ContextWrapper(BaseApp.getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File folder = new File(directory.getAbsolutePath()+THUMBNAILS_DOWNLOADEDISSUE_DIR_PREFIX);

        if(folder!=null){
            path = folder.getAbsolutePath()+"/"+issueID+".jpg";
        }

        return path;
    }

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
