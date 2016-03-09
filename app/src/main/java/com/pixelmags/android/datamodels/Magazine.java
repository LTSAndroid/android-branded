package com.pixelmags.android.datamodels;

import android.graphics.Bitmap;


import com.pixelmags.android.storage.AllDownloadsDataSet;

import java.lang.reflect.Array;
import java.util.Date;

/**
 * Created by Annie on 10/10/15.
 */

/*
    This datamodel represents an issue in the Store View
 */
public class Magazine
{
    public static String STATUS_BUY = "Buy";
    public static String STATUS_DOWNLOAD = "Download";
    public static String STATUS_VIEW = "View";
    public static String STATUS_PRICE = "price";
    public static String STATUS_FREE="free";
    //
    public int id;
    public String title;
    public String synopsis;
//    public int magazineId;
    public String price;
    public String android_store_sku;
    public String type;
    public String manifest;
    public Date lastModified;
    public Date issueDate;
    public boolean isPublished;
    public boolean removeFromSale;
    public String ageRestriction;
    public String exclude_from_subscription;

    public int state;
    public String region;
    public String mediaFormat;
    public String thumbnailURL;
    public Boolean inAnytime;
    public boolean isIssueOwnedByUser = false;
//    public String issueId;
    public String status = Magazine.STATUS_PRICE;
//    public Object data;
    public Number sortOrder;
    public Array thumbs;
    public Object sortedThumbs;
    public Array renditions;

    // runtime variable to store download status
    public int currentDownloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_NONE;

    public Bitmap thumbnailBitmap; // to store temporarily once downloaded
    public String thumbnailDownloadedInternalPath;
    public boolean isThumbnailDownloaded;

    public AllDownloadsIssueTracker issueDownloadData; // for the download fragment

}
