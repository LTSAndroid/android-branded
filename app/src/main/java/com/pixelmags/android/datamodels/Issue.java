package com.pixelmags.android.datamodels;

import java.util.ArrayList;

/**
 * Created by austincoutinho on 04/11/15.
 */

/**
*Apply the
*
*
*
*
*
 */

public class Issue {
    public int issueID;
    public int magazineID;
    public String title;
    public String thumbnailURL;
    public String issueDate;
    public int pageCount;
    public String created;
    public String lastModified;
    public String media_format;
    public String region;
    public ArrayList<Page> pages;

//    public JSONArray pageManifest;
//    public String manifest;
//    public JSONArray searchTerms;

    public Issue(){
        pages = new ArrayList<Page>();
    }


    public void insertPage(Page page){
        pages.add(page);
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
