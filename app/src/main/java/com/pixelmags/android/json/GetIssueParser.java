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

    public Object mData;
    public ArrayList<Issue> getIssueList;

    public GetIssueParser(String Data){
        super(Data);
        getIssueList = new ArrayList<Issue>();
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            JSONObject unit = baseJSON.getJSONObject("issue");

                Issue issue = new Issue();
                issue.issueID = unit.getInt("ID");
                issue.magazineID = unit.getInt("magazine_id");
                issue.title = unit.getString("title");
                issue.thumbnailURL = unit.getString("thumbnailURL");
                issue.issueDate = unit.getString("issueDate");
                issue.created = unit.getString("created");
                issue.lastModified = unit.getString("lastModified");
                issue.media_format = unit.getString("media_format");

                int pageCount = unit.getInt("pageCount");
                issue.pageCount = pageCount;

                System.out.println("RETRIEVED ISSUE===" +unit.getString("title"));

                JSONObject pageManifestArrayData = unit.getJSONObject("page_manifest");
                for(int pageNo=1; pageNo<=pageCount; pageNo++)
                {
                    String pg = String.valueOf(pageNo);
                    JSONObject pageUnit = pageManifestArrayData.getJSONObject(pg);


                    //   pages.add(0,base);
                    String pageId = pageUnit.getString("id");
                    String pageMedia = pageUnit.getJSONObject("media").toString();

                        System.out.println("PAGE ID ==== "+pageId);
                        System.out.println("PAGE Media ==== "+pageMedia);

                    Page pageImage = new PageTypeImage(pageNo, pageId, pageMedia);

                    issue.pages.add(pageImage);
                }


            getIssueList.add(issue);


        }catch(Exception e){
            e.printStackTrace();
        }


        return true;
    }

}



