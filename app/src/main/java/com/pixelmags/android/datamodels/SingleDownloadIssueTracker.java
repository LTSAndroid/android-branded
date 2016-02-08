package com.pixelmags.android.datamodels;

import com.pixelmags.android.storage.SingleIssueDownloadDataSet;

/**
 * Created by austincoutinho on 06/02/16.
 */
public class SingleDownloadIssueTracker {

    public int pageNo;
    public String urlPdfLarge;
    public String md5ChecksumLarge;
    public String downloadedLocationPdfLarge;
    public int downloadStatusPdfLarge;

    public SingleDownloadIssueTracker(){
        this.downloadStatusPdfLarge = SingleIssueDownloadDataSet.DOWNLOAD_STATUS_PENDING;
    }

    public SingleDownloadIssueTracker(PageTypeImage.PageDetails page, int num){

        this.pageNo = num;
        this.urlPdfLarge = page.url;
        this.md5ChecksumLarge = page.checksum_md5;
        this.downloadStatusPdfLarge = SingleIssueDownloadDataSet.DOWNLOAD_STATUS_PENDING;

    }

}
