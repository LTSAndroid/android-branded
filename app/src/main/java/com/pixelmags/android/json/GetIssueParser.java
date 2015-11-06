package com.pixelmags.android.json;

import com.pixelmags.android.datamodels.Issue;
import com.pixelmags.android.datamodels.Page;
import com.pixelmags.android.datamodels.PageTypeImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Annie on 11/10/15.
 */
public class GetIssueParser extends JSONParser {

    public Issue mIssue;

    public GetIssueParser(String Data){
        super(Data);
        mIssue = new Issue();
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            JSONObject unit = baseJSON.getJSONObject("issue");

            mIssue.issueID = unit.getInt("ID");
            mIssue.magazineID = unit.getInt("magazine_id");
            mIssue.title = unit.getString("title");
            mIssue.thumbnailURL = unit.getString("thumbnailURL");
            mIssue.issueDate = unit.getString("issueDate");
            mIssue.created = unit.getString("created");
            mIssue.lastModified = unit.getString("lastModified");
            mIssue.media_format = unit.getString("media_format");

            int pageCount = unit.getInt("pageCount");
            mIssue.pageCount = pageCount;

                    System.out.println("RETRIEVED ISSUE===" +unit.getString("title"));

            JSONObject pageManifestArrayData = unit.getJSONObject("page_manifest");
            for(int pageNo=1; pageNo<=pageCount; pageNo++)
            {
                String pg = String.valueOf(pageNo);
                JSONObject pageUnit = pageManifestArrayData.getJSONObject(pg);

                String pageId = pageUnit.getString("id");
                String pageMedia = pageUnit.getJSONObject("media").toString();

                    System.out.println("PAGE ID ==== "+pageId);
                    // System.out.println("PAGE Media ==== "+pageMedia);

                Page pageImage = new PageTypeImage(pageNo, pageId, pageMedia);

                mIssue.pages.add(pageImage);
            }


        }catch(Exception e){
            e.printStackTrace();
        }


        return true;
    }

}



