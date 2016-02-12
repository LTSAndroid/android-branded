package com.pixelmags.android.datamodels;

import com.pixelmags.android.storage.SingleIssueDownloadDataSet;

/**
 * Created by austincoutinho on 06/02/16.
 *
 * This is designed to contain a single row from the SingleIssueDownloadDataSet
 * i.e the rows from the Unique download table that is created for each issue.
 *
 * e.g. all the rows of 'Issue_Download_Table_120974' where 120974 is the issue id
 *
 */
public class SingleDownloadIssueTracker {

    public int pageNo;
    public String urlPdfLarge;
    public String md5ChecksumLarge;
    public String downloadedLocationPdfLarge;
    public int downloadStatusPdfLarge;

    // for post download processing
    public String uniqueTable;

    public SingleDownloadIssueTracker(){
        this.downloadStatusPdfLarge = SingleIssueDownloadDataSet.DOWNLOAD_STATUS_PENDING;
    }

    public SingleDownloadIssueTracker(PageTypeImage.PageDetails page, int pageNo){

        this.pageNo = pageNo;
        this.urlPdfLarge = page.url;
        this.md5ChecksumLarge = page.checksum_md5;
        this.downloadStatusPdfLarge = SingleIssueDownloadDataSet.DOWNLOAD_STATUS_PENDING;

    }

}
