package com.pixelmags.android.download;

import com.pixelmags.android.datamodels.Issue;
import com.pixelmags.android.datamodels.Page;
import com.pixelmags.android.datamodels.PageTypeImage;

import java.net.URL;

/**
 * Created by austincoutinho on 13/12/15.
 */
public class DownloadIssue {

    private static final String THUMBNAIL_DIR_PREFIX="/All_Download/"; //add the additional path parameters later '/(magid/(issueid)/(renditiontype)'

    private Issue mIssue;
    // The Thread that will be used to download the image for this ImageView
    private IssueDownloadTask mDownloadThread;

    public DownloadIssue(Issue issue){
        this.mIssue = issue;
    }

    public void initDownload(){

        try{

            for (int i = 0; i < mIssue.pages.size(); i++) {

                PageTypeImage page = (PageTypeImage) mIssue.pages.get(i);
                PageTypeImage.PageDetails pageDetails = page.getPageDetails(PageTypeImage.MediaType.LARGE);

                URL pageURL = new java.net.URL(pageDetails.url);

                mDownloadThread = IssueDownloadManager.startDownload(pageURL, false);;

            }

        }catch (Exception e){
            System.out.println("Exception in DownloadIssue, initDownload ::- " + e.getMessage());

        }

    }



}
