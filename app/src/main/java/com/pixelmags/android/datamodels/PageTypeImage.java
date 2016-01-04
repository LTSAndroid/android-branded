package com.pixelmags.android.datamodels;

import com.pixelmags.android.json.PageTypeImageParser;

/**
 * Created by austincoutinho on 04/11/15.
 */

public class PageTypeImage extends Page {

    private PageTypeImageParser pageParser;


   public enum MediaType {
       SMALL, MEDIUM, LARGE
   }



   public PageTypeImage(int pgNo, String pgID, String JSONData){
        super(pgNo, pgID, JSONData);
   }



    public PageDetails getPageDetails(MediaType pageSize){

        PageDetails pageDetail = null;
        pageParser = new PageTypeImageParser();

       switch(pageSize){
           case SMALL:
               pageDetail = pageParser.getPageDetailsSmall(new PageDetails(pageSize), getPageJSONData());
               break;
           case MEDIUM:
               pageDetail = pageParser.getPageDetailsMedium(new PageDetails(pageSize), getPageJSONData());
               break;
           case LARGE:
               pageDetail = pageParser.getPageDetailsLarge(new PageDetails(pageSize), getPageJSONData());
               break;
           default:
               break;
       }

        return pageDetail;

    }


    public class PageDetails{

        public String mime_type; 	//"image/jpeg"
        public String url; 	// http://cdn.pixel-mags.com/prod/ipc/now/issues/110422/104880/page_0001_medium.jpg
        public String checksum_md5; 	// 2977130915f343fa61b8cef1ffb6e2b6
        public String last_modified; 	//2015-11-02 17:55:06
        public String encryption; 	//aes256
        public int file_size; 	//63456
        public int width; 	//291
        public int height; 	//400
        public MediaType imageSize;

        PageDetails(MediaType sizeOfImage){
            imageSize = sizeOfImage;
        }



    }


/*

   public PageTypeImage(MediaType type){
       this.imageSize=type;
   }

    public class SmallPage extends PageTypeImage {
        SmallPage(){
            super(MediaType.SMALL);
        }
    }

    public class MediumPage extends PageTypeImage {
        MediumPage(){
            super(MediaType.MEDIUM);
        }

    }

    public class LargePage extends PageTypeImage {
         LargePage(){
             super(MediaType.LARGE);
         }
    }

*/

}
