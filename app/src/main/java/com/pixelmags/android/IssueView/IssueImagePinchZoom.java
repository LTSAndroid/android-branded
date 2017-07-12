package com.pixelmags.android.IssueView;

/**
 * Created by Likith on 17/10/2016.
 */

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class IssueImagePinchZoom extends ImageView {
    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final int CLICK = 3;
    static boolean b =false, c= false;
    Matrix matrix = new Matrix();
    int mode = NONE;
    //static  NonSwipeableViewPager PagerLeft;
// Remember some things for zooming
    PointF last = new PointF();
    PointF start = new PointF();
    float minScale = 1f;
    float maxScale = 3f;
    float[] m;
    float redundantXSpace, redundantYSpace;
    float width, height;
    float saveScale = 1f;
    float right, bottom, origWidth, origHeight, bmWidth, bmHeight;

    ScaleGestureDetector mScaleDetector;

    Context context;
    ViewGroup vp;

    public IssueImagePinchZoom(Context context) {
        super(context);
        sharedConstructing(context);
    }

    public IssueImagePinchZoom(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }

    public void setChild(ViewGroup vp) {
        this.vp = vp;
    }

    private void sharedConstructing(Context context) {
        super.setClickable(true);
        this.context = context;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        matrix.setTranslate(1f, 1f);
        m = new float[9];
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);
        setOnTouchListener(new OnTouchListener() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleDetector.onTouchEvent(event);
                matrix.getValues(m);
                float x = m[Matrix.MTRANS_X];
                float y = m[Matrix.MTRANS_Y];
            /*Log.v("log_tag",  event.getX()+" matrix "
                    + x);*/
                PointF curr = new PointF(event.getX(), event.getY());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        IssueImagePinchZoom.b = false;
                        Log.v("event","ACTION_DOWN");
                        last.set(event.getX(), event.getY());
                        start.set(last);
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            float deltaX = curr.x - last.x;
                            float deltaY = curr.y - last.y;
                            float scaleWidth = Math.round(origWidth * saveScale);
                            float scaleHeight = Math.round(origHeight * saveScale);
                            if (scaleWidth < width) {
                                deltaX = 0;
                                if (y + deltaY > 0){
                                    IssueImagePinchZoom.b = true;
                                    Log.v("event","ACTION_MOVE  scaleWidth < width   y + deltaY > 0");
                                    deltaY = -y;
                                }
                                else if (y + deltaY < -bottom){
                                    IssueImagePinchZoom.b = false;
                                    Log.v("event","else ACTION_MOVE  scaleWidth < width   y + deltaY < -bottom");
                                    deltaY = -(y + bottom);
                                }else{
                                    IssueImagePinchZoom.b = false;
                                }
                            } else if (scaleHeight < height) {
                                deltaY = 0;
                                if (x + deltaX > 0){
                                    IssueImagePinchZoom.b = true;
                                    Log.v("event","ACTION_MOVE  scaleWidth < height x + deltaX > 0");
                                    deltaX = -x;
                                }
                                else if (x + deltaX < -right){
                                    IssueImagePinchZoom.b = true;
                                    Log.v("event","ACTION_MOVE  scaleWidth < height x + deltaX < -right");
                                    deltaX = -(x + right);
                                }else{
                                    IssueImagePinchZoom.b = false;
                                }
                            }else {
                                if (x + deltaX > 0){
                                    IssueImagePinchZoom.b = true;
                                    Log.v("event","scaleWidth > width");
                                    deltaX = -x;
                                }
                                else if (x + deltaX < -right){
                                    IssueImagePinchZoom.b = true;
                                    Log.v("event","else scaleWidth > width");
                                    deltaX = -(x + right);
                                }
                                else{
                                    IssueImagePinchZoom.b = true;
                                }
                                if (y + deltaY > 0){
                                    IssueImagePinchZoom.b = true;
                                    Log.v("event","y + deltaY");
                                    deltaY = -y;
                                }
                                else if (y + deltaY < -bottom){
                                    IssueImagePinchZoom.b = true;
                                    Log.v("event","y + deltaY < -bottom");
                                    deltaY = -(y + bottom);
                                }else{
                                    IssueImagePinchZoom.b = false;
                                }
                            }
                    /*Log.v("log_tag",height + " matrix "
                            + (int) getHeightFromMatrix(matrix, TouchImageView.this));*/
                            matrix.postTranslate(deltaX, deltaY);
                            last.set(curr.x, curr.y);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        Log.v("event","ACTION_UP");
                        IssueImagePinchZoom.b = true;
                        mode = NONE;
                        int xDiff = (int) Math.abs(curr.x - start.x);
                        int yDiff = (int) Math.abs(curr.y - start.y);
                        if (xDiff < CLICK && yDiff < CLICK){
                            Log.v("event","ACTION_UP  xDiff < CLICK && yDiff < CLICK");
                            performClick();
                        }
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        //TouchImageView.b = false;
                        Log.v("event","ACTION_POINTER_UP");
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_POINTER_3_DOWN:
                        Log.v("event","ACTION_HOVER_MOVE");
                        break;
                }
            /*getLayoutParams().height = (int) getWidthFromMatrix(matrix,
                    TouchImageView.this);
            getLayoutParams().width = (int) getHeightFromMatrix(matrix,
                    TouchImageView.this);*/
                setImageMatrix(matrix);
                invalidate();
                return true;
            }
        });
    }
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if (bm != null) {
            bmWidth = bm.getWidth();
            bmHeight = bm.getHeight();
        }
    }

    public void setMaxZoom(float x) {
        maxScale = x;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        // Fit to screen.
        float scale;
        float scaleX = (float) width / (float) bmWidth;
        float scaleY = (float) height / (float) bmHeight;
        scale = Math.min(scaleX, scaleY);
        matrix.setScale(scale, scale);
        setImageMatrix(matrix);
        saveScale = 1f;

        // Center the image
        redundantYSpace = (float) height - (scale * (float) bmHeight);
        redundantXSpace = (float) width - (scale * (float) bmWidth);
        redundantYSpace /= (float) 2;
        redundantXSpace /= (float) 2;

        matrix.postTranslate(redundantXSpace, redundantYSpace);

        origWidth = width - 2 * redundantXSpace;
        origHeight = height - 2 * redundantYSpace;
        right = width * saveScale - width - (2 * redundantXSpace * saveScale);
        bottom = height * saveScale - height
                - (2 * redundantYSpace * saveScale);
        setImageMatrix(matrix);
    }

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        @SuppressLint("NewApi")
        @TargetApi(8)
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = (float) Math.min(
                    Math.max(.95f, detector.getScaleFactor()), 1.05);
            float origScale = saveScale;
            saveScale *= mScaleFactor;
            if (saveScale > maxScale) {
                saveScale = maxScale;
                mScaleFactor = maxScale / origScale;
            } else if (saveScale < minScale) {
                saveScale = minScale;
                mScaleFactor = minScale / origScale;
            }
            right = width * saveScale - width
                    - (2 * redundantXSpace * saveScale);
            bottom = height * saveScale - height
                    - (2 * redundantYSpace * saveScale);
            if (origWidth * saveScale <= width
                    || origHeight * saveScale <= height) {
                matrix.postScale(mScaleFactor, mScaleFactor, width / 2,
                        height / 2);
                if (mScaleFactor < 1) {
                    matrix.getValues(m);
                    float x = m[Matrix.MTRANS_X];
                    float y = m[Matrix.MTRANS_Y];
                    if (mScaleFactor < 1) {
                        if (Math.round(origWidth * saveScale) < width) {
                            if (y < -bottom)
                                matrix.postTranslate(0, -(y + bottom));
                            else if (y > 0)
                                matrix.postTranslate(0, -y);
                        } else {
                            if (x < -right)
                                matrix.postTranslate(-(x + right), 0);
                            else if (x > 0)
                                matrix.postTranslate(-x, 0);
                        }
                    }
                }
            } else {
                matrix.postScale(mScaleFactor, mScaleFactor,
                        detector.getFocusX(), detector.getFocusY());
                matrix.getValues(m);
                float x = m[Matrix.MTRANS_X];
                float y = m[Matrix.MTRANS_Y];
                if (mScaleFactor < 1) {
                    if (x < -right)
                        matrix.postTranslate(-(x + right), 0);
                    else if (x > 0)
                        matrix.postTranslate(-x, 0);
                    if (y < -bottom)
                        matrix.postTranslate(0, -(y + bottom));
                    else if (y > 0)
                        matrix.postTranslate(0, -y);
                }
            }
            return true;
        }
    }

/*public static void setPager(NonSwipeableViewPager pagerLeft) {
    PagerLeft = pagerLeft;
}*/



}