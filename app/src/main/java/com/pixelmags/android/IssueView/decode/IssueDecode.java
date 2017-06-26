package com.pixelmags.android.IssueView.decode;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by austincoutinho on 22/05/16.
 *
 *  Decode/Decrypt an issue page
 *
 *  NOTE : UNLESS YOU ABSOLUTELY KNOW WHAT YOU"RE DOING ::::::::::::::: DO NOT TOUCH THIS CLASS
 *
 */

public class IssueDecode {


    public byte[] getDecodedBitMap(String documentKey, FileInputStream fis) {

        //Base64Utils utils = new Base64Utils();

        return decrypt( documentKey, fis);

    }

    private byte[] decrypt(String documentKeyHex, FileInputStream fis){

       // System.out.println("DOCUMENT KEY :: "+documentKeyHex);
        byte[] decodedKey = Base64.decode(documentKeyHex, Base64.DEFAULT);

        SecretKeySpec skeySpec = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        Cipher cipher;
        byte[] decryptedData=null;
        CipherInputStream cis=null;

        try {

            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding","BC");

            byte[] mIv = new byte[16];
            IvParameterSpec ivSpec = new IvParameterSpec(mIv);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec,ivSpec);

            // Create CipherInputStream to read and decrypt the image data
            cis = new CipherInputStream(fis, cipher);

            // Write encrypted image data to ByteArrayOutputStream
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] data = new byte[1024];

            int r = 0;
            while ((r = cis.read(data)) > 0) {

                buffer.write(data,0,r);
            }

            cis.close();


            decryptedData = buffer.toByteArray();

        }catch(Exception e){
            e.printStackTrace();
        }
        finally{

            try {
                fis.close();

                cis.close();

            } catch (IOException e) {

                // TODO Auto-generated catch block

                e.printStackTrace();
            }
        }
        return decryptedData;

    }


}
