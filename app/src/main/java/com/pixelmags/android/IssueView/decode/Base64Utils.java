package com.pixelmags.android.IssueView.decode;

import static java.lang.Character.digit;

/**
 * Created by austincoutinho on 22/05/16.
 *
 * This contains all functions that decode the base64 into it's hex form to be used in issue decryption
 *
 * NOTE : UNLESS YOU ABSOLUTELY KNOW WHAT YOU'RE DOING, TO A VERY HIGH DEGREE OF CERTAINTY, DO NOT
 *
 * ::::::::::::::::::::: (I REPEAT DO NOT) TOUCH THIS CLASS !!!!!!!!!!!!!!!!!!!!
 *
 */

public class Base64Utils {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    // Mapping table from 6-bit nibbles to Base64 characters.
    private static final char[] map1 = new char[64];
    // Mapping table from Base64 characters to 6-bit nibbles.
    private static final byte[] map2 = new byte[128];

    static {
        int i=0;
        for (char c='A'; c<='Z'; c++) map1[i++] = c;
        for (char c='a'; c<='z'; c++) map1[i++] = c;
        for (char c='0'; c<='9'; c++) map1[i++] = c;
        map1[i++] = '+'; map1[i++] = '/'; }

    static {
        for (int i=0; i<map2.length; i++) map2[i] = -1;
        for (int i=0; i<64; i++) map2[map1[i]] = (byte)i; }

/**
 *  Main accessor function used to convert the Base64 key to its Hex form and returned as a byte array
 *
 */
public byte[] getDocumentKeyDecryptedArray(String key){

    return stringToBytes(convertBase64ToHex(key));

}

/*
*   Functional test
*   "pBBiBXvT96IffZ+gVFRd3EAqyA1juCV2pfNebwZsWbo="
*   converts to
*   "A41062057BD3F7A21F7D9FA054545DDC402AC80D63B82576A5F35E6F066C59BA"
*
*/
    private String convertBase64ToHex(String base64Key){
        return bytesToHex(decode(base64Key));
    }

    private byte[] stringToBytes(String input) {

        int length = input.length();
        byte[] output = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            output[i / 2] = (byte) ((digit(input.charAt(i), 16) << 4) | digit(input.charAt(i+1), 16));
        }
        return output;

    }

    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public byte[] decode (String s) {
        return decode(s.toCharArray()); }

    private byte[] decode (char[] in) {
        return decode(in, 0, in.length); }


    private byte[] decode (char[] in, int iOff, int iLen) {

        if (iLen%4 != 0) throw new IllegalArgumentException ("Length of Base64 encoded input string is not a multiple of 4.");
        while (iLen > 0 && in[iOff+iLen-1] == '=') iLen--;
        int oLen = (iLen*3) / 4;
        byte[] out = new byte[oLen];
        int ip = iOff;
        int iEnd = iOff + iLen;
        int op = 0;
        while (ip < iEnd) {
            int i0 = in[ip++];
            int i1 = in[ip++];
            int i2 = ip < iEnd ? in[ip++] : 'A';
            int i3 = ip < iEnd ? in[ip++] : 'A';
            if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127)
                throw new IllegalArgumentException ("Illegal character in Base64 encoded data.");
            int b0 = map2[i0];
            int b1 = map2[i1];
            int b2 = map2[i2];
            int b3 = map2[i3];
            if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0)
                throw new IllegalArgumentException ("Illegal character in Base64 encoded data.");
            int o0 = ( b0       <<2) | (b1>>>4);
            int o1 = ((b1 & 0xf)<<4) | (b2>>>2);
            int o2 = ((b2 &   3)<<6) |  b3;
            out[op++] = (byte)o0;
            if (op<oLen) out[op++] = (byte)o1;
            if (op<oLen) out[op++] = (byte)o2;
        }
        return out;
    }

}
