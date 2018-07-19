package com.pixelmags.android.datamodels;

import android.graphics.Bitmap;

/**
 * Created by Austin Coutinho on 21/01/16.
 *
 * This is designed to contain a single row from the AllDownloadsDataSet
 * i.e the 'All downloads' table.
 *
 */
public class AllDownloadsIssueTracker
{
    public int magazineID;
    public int issueID;
    public String issueTitle;
    public String uniqueIssueDownloadTable;
    public long priority;
    public int downloadStatus;
    public int progressCompleted;
    public String regionData;
    public Bitmap thumbnailBitmap;
}
