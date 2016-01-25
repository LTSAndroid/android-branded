package com.pixelmags.android.datamodels;

import android.graphics.Bitmap;

/**
 * Created by Austin Coutinho on 21/01/16.
 */
public class DownloadedIssue
{
    public int magazineID;
    public int issueID;
    public String issueTitle;
    public String uniqueIssueDownloadTable;
    public long priority;
    public int downloadStatus;

    public Bitmap thumbnailBitmap;
}
