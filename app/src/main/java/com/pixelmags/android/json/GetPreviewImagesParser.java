package com.pixelmags.android.json;

import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.datamodels.PreviewImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class GetPreviewImagesParser extends JSONParser {

    public Object mData;
    public ArrayList<PreviewImage> previewImagesList;

    public GetPreviewImagesParser(String Data){
        super(Data);
        previewImagesList = new ArrayList<PreviewImage>();
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            mData = baseJSON.getJSONArray("data");
            JSONArray arrayData = baseJSON.getJSONArray("data");
            for(int i=0;i<arrayData.length();i++)
            {
                PreviewImage pm = new PreviewImage();
                JSONObject unit = arrayData.getJSONObject(i);

                pm.setId(unit.getInt("ID"));
                pm.setChecksum_md5(unit.getString("checksum_md5"));
                pm.setEncryption(unit.getString("encryption"));
                pm.setThumb(unit.getString("url"));
                pm.setMimeType(unit.getString("mime_type"));
                pm.setObjWidth(unit.getInt("width"));
                pm.setObjHeight(unit.getInt("height"));

                previewImagesList.add(pm);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }

}
