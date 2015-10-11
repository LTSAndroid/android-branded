package com.pixelmags.android.datamodels;

import java.util.Objects;

/**
 * Created by Annie on 10/10/15.
 */
public class PreviewImage
{
    private String _thumb;
    private Number _objWidth;
    private Number _objHeight;
    private int _id;
    private String _checksum_md5;
    private String _encryption;
    private String _mimeType;

    public PreviewImage()
    {
        super();
    }

    public String issueId;

    public void setChecksum_md5(String value)
    {
        _thumb = value;
    }

    public String getChecksum_md5()
    {
        return _thumb;
    }
    public void setEncryption(String value)
    {
        _thumb = value;
    }

    public String getEncryption()
    {
        return _thumb;
    }

    public void setMimeType(String value)
    {
        _thumb = value;
    }

    public String getMimeType()
    {
        return _thumb;
    }


    public void setObjWidth(Number value)
    {
        _objWidth = value;
    }

    public void setObjHeight(Number value)
    {
        _objHeight = value;
    }

    public Number getObjWidth()
    {
        return _objWidth;
    }

    public Number getObjHeight()
    {
        return _objHeight;
    }

    public void setThumb(String value)
    {
        _thumb = value;
    }

    public String getThumb()
    {
        return _thumb;
    }

    public void setId(int value)
    {
        _id = value;
    }

    public int getId()
    {
        return _id;
    }

    public void flush()
    {
        _thumb = null;
        _objWidth = null;
        _objHeight = null;
        _id = 0;
    }
}

