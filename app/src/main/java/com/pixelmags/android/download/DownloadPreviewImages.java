package com.pixelmags.android.download;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.pixelmags.android.datamodels.PreviewImage;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by austincoutinho on 16/11/15.
 */
public class DownloadPreviewImages implements Runnable {

    public static final int MAX_PREVIEW_IMAGE_COUNT = 5;
    public static ArrayList<PreviewImage> previewImageArrayList;
    private String url;
    private int previewImageIndex;

    DownloadPreviewImages(String downloadUrl, int index){
        this.previewImageIndex = index;
        this.url = downloadUrl;
    }

    public static ArrayList<PreviewImage> DownloadPreviewImageBitmaps(ArrayList<PreviewImage> previewImageArray) {

        previewImageArrayList = previewImageArray;

        ArrayList<Thread> previewImageDownloadThreads = new ArrayList<Thread>();

        for (int i = 0; i < previewImageArrayList.size() && i < MAX_PREVIEW_IMAGE_COUNT; i++) {
            PreviewImage pImg = previewImageArrayList.get(i);
            DownloadPreviewImages downloadThread = new DownloadPreviewImages(pImg.previewImageURL, i);

            Thread t1 = new Thread(downloadThread);
            previewImageDownloadThreads.add(t1);
        }


        for (Thread thread : previewImageDownloadThreads){
                thread.start();
        }

        try{

            for (Thread joinThread : previewImageDownloadThreads){
                joinThread.join();
            }

        }catch(Exception e){
            e.printStackTrace();
        }


        return previewImageArrayList;
    }

    public static ArrayList<PreviewImage> DownloadPreviewImageBitmapsForIssueView(ArrayList<PreviewImage> previewImageArray) {

        previewImageArrayList = previewImageArray;

        ArrayList<Thread> previewImageDownloadThreads = new ArrayList<Thread>();

        for (int i = 0; i < previewImageArrayList.size(); i++) {
            PreviewImage pImg = previewImageArrayList.get(i);
            DownloadPreviewImages downloadThread = new DownloadPreviewImages(pImg.previewImageURL, i);

            Thread t1 = new Thread(downloadThread);
            previewImageDownloadThreads.add(t1);
        }


        for (Thread thread : previewImageDownloadThreads){
            thread.start();
        }

        try{

            for (Thread joinThread : previewImageDownloadThreads){
                joinThread.join();
            }

        }catch(Exception e){
            e.printStackTrace();
        }


        return previewImageArrayList;
    }


    @Override
    public void run() {

        Bitmap thumbnailBitmap = null;

        try {
            InputStream in = new java.net.URL(url).openStream();
            thumbnailBitmap = BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            e.printStackTrace();
        }

        previewImageArrayList.get(previewImageIndex).previewImageBitmap = thumbnailBitmap;
    }

}
