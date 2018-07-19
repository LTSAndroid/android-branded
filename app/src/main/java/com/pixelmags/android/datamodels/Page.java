package com.pixelmags.android.datamodels;

/**
 * Created by austincoutinho on 04/11/15.
 */

/*
    This datamodel represents the base of the page of an issue.
 */
public class Page {

    public int pageNo;
    public String pageID;
    public String PageJSONData;
    public String pageRegionData;

    // holds the JSON data as string from the media block of page_manifest

    public boolean isDownloaded;
    public String savedPath;


    public Page(int pgNo, String pgID, String JSONData,String pageRegionDatas){
        this.pageNo = pgNo;
        this.pageID = pgID;
        this.PageJSONData = JSONData;
        this.pageRegionData = pageRegionDatas;

    }

    public int getPageNo(){
        return pageNo;
    }

    public String getPageID(){
        return pageID;
    }

    public String getPageJSONData(){
        return PageJSONData;
    }

    public String getRegions(){
        return pageRegionData;

    }

}

