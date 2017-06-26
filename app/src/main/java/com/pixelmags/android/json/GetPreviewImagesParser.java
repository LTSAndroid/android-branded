package com.pixelmags.android.json;

import com.pixelmags.android.datamodels.PreviewImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Annie on 09/10/15.
 */
public class GetPreviewImagesParser extends JSONParser {

    public Object mData;
    public String issueID;
    public ArrayList<PreviewImage> previewImagesList;
    private String TAG = "GetPreviewImagesParser";

    public GetPreviewImagesParser(String Data,String issueID){
        super(Data);
        previewImagesList = new ArrayList<PreviewImage>();
        this.issueID = issueID;
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

//           mData = baseJSON.getJSONArray("data");
            JSONObject data = baseJSON.getJSONObject("data");

            Iterator x = data.keys();
            JSONArray jsonArray = new JSONArray();

            while (x.hasNext()){
                String key = (String) x.next();
                jsonArray.put(data.get(key));
            }

            for(int i=0;i<jsonArray.length();i++)
            {
                PreviewImage pm = new PreviewImage();
                JSONObject unit = jsonArray.getJSONObject(i);

                pm.setPreviewImageURL(unit.getString("url"));
                pm.setImageWidth(unit.getInt("width"));
                pm.setImageHeight(unit.getInt("height"));

                previewImagesList.add(pm);
            }


        }catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }

}
