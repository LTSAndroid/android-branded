package com.pixelmags.android.datamodels;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by austincoutinho on 04/11/15.
 */

/*
    This datamodel represents an actual issue, it's contents and it's pages
 */

public class Issue
{
    public int issueID;
    public int magazineID;
    public String title;
    public String thumbnailURL;
    public String issueDate;
    public int pageCount;
    public String created;
    public String lastModified;
    public String media_format;

    public ArrayList<Page> pages;

//    public JSONArray pageManifest;
//    public String manifest;
//    public JSONArray searchTerms;

    public Issue(){
        pages = new ArrayList<Page>();
    }


    public void getSmallPage(){

      //  Page base = null;
      //  base = new PageTypeImage(0,"","test");
     //   pages.add(0,base);

    }

    public void getAllMediumPages(){

    }

    public void getAllLargePages(){

    }




}
