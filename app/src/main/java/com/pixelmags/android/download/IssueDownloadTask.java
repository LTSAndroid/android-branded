package com.pixelmags.android.download;

import android.graphics.Bitmap;

import com.pixelmags.android.download.IssueDownloadRunnable.TaskRunnableDownloadMethods;

import java.lang.ref.WeakReference;
import java.net.URL;

/**
 *
 * Created by austincoutinho on 07/12/15.
 *
 * This class manages IssueDownloadRunnable objects.  It does not perform
 * the download; instead, it manages persistent storage for the tasks that do the work.
 * It does this by implementing the interfaces that the download classes define, and
 * then passing itself as an argument to the constructor of a download or decode object. In effect,
 * this allows IssueDownloadTask to start on a Thread, run a download in a delegate object,
 * and then start over again. This class can be pooled and reused as necessary.
 *
 */


public class IssueDownloadTask implements TaskRunnableDownloadMethods {

    // The image's URL
    private URL mImageURL;

    // Is the cache enabled for this transaction?
    private boolean mCacheEnabled;

    // Field containing the Thread this task is running on.
    Thread mThreadThis;

    //References to the runnable objects that handle downloading of the image.
    private Runnable mDownloadRunnable;

    // A buffer for containing the bytes that make up the image
    byte[] mImageBuffer;

    // The bitmap of image
    private Bitmap mImageBitMap;

    // The Thread on which this task is currently running.
    private Thread mCurrentThread;

    /*
     * An object that contains the ThreadPool singleton.
     */
    private static IssueDownloadManager sIssueDownloadManager;

    /**
     * Creates an PhotoTask containing a download object and a decoder object.
     */
    IssueDownloadTask() {
        // Create the runnables
        mDownloadRunnable = new IssueDownloadRunnable(this);
        sIssueDownloadManager = IssueDownloadManager.getInstance();
    }
    
    /**
     * Initializes the Task
     *
     * @param issueDownloadManager A ThreadPool object
     * @param imageURL url for the downloaded image
     * @param cacheFlag Whether caching is enabled
     */
    void initializeDownloaderTask(
            IssueDownloadManager issueDownloadManager,
            URL imageURL,
            boolean cacheFlag)
    {
        // Sets this object's ThreadPool field to be the input argument
        sIssueDownloadManager = issueDownloadManager;
        
        // Gets the URL
        mImageURL = imageURL;

        // Sets the cache flag to the input argument
        mCacheEnabled = cacheFlag;
    }
    
    // Implements HTTPDownloaderRunnable.getByteBuffer
    @Override
    public byte[] getByteBuffer() {
        
        // Returns the global field
        return mImageBuffer;
    }
    
    /**
     * Recycles an PhotoTask object before it's put back into the pool. One reason to do
     * this is to avoid memory leaks.
     */
    void recycle() {

        // Releases references to the byte buffer and the BitMap
        mImageBuffer = null;
    }

    // Detects the state of caching
    boolean isCacheEnabled() {
        return mCacheEnabled;
    }

    // Implements IssueDownloadRunnable.getImageURL. Returns the global Image URL.
    @Override
    public URL getImageURL() {
        return mImageURL;
    }

    // Implements IssueDownloadRunnable.setByteBuffer. Sets the image buffer to a buffer object.
    @Override
    public void setByteBuffer(byte[] imageBuffer) {
        mImageBuffer = imageBuffer;
    }
    
    // Delegates handling the current state of the task to the IssueDownloadManager object
    void handleState(int state) {
        sIssueDownloadManager.handleState(this, state);
    }

    // Returns the image bitmap
    Bitmap getImage() {
        return mImageBitMap;
    }

    // Returns the instance that downloaded the image
    Runnable getHTTPDownloadRunnable() {
        return mDownloadRunnable;
    }


    /*
     * Returns the Thread that this Task is running on. The method must first get a lock on a
     * static field, in this case the ThreadPool singleton. The lock is needed because the
     * Thread object reference is stored in the Thread object itself, and that object can be
     * changed by processes outside of this app.
     */

    public Thread getCurrentThread() {
        synchronized(sIssueDownloadManager) {
            return mCurrentThread;
        }
    }

    /*
     * Sets the identifier for the current Thread. This must be a synchronized operation; see the
     * notes for getCurrentThread()
     */
    public void setCurrentThread(Thread thread) {
        synchronized(sIssueDownloadManager) {
            mCurrentThread = thread;
        }
    }

    // Implements IssueDownloadRunnable.setHTTPDownloadThread(). Calls setCurrentThread().
    @Override
    public void setDownloadThread(Thread currentThread) {
        setCurrentThread(currentThread);
    }

    /*
     * Implements IssueDownloadRunnable.handleHTTPState(). Passes the download state to the
     * ThreadPool object.
     */
    
    @Override
    public void handleDownloadState(int state) {
        int outState;
        
        // Converts the download state to the overall state
        switch(state) {
            case IssueDownloadRunnable.HTTP_STATE_COMPLETED:
                outState = IssueDownloadManager.DOWNLOAD_COMPLETE;
                break;
            case IssueDownloadRunnable.HTTP_STATE_FAILED:
                outState = IssueDownloadManager.DOWNLOAD_FAILED;
                break;
            default:
                outState = IssueDownloadManager.DOWNLOAD_STARTED;
                break;
        }
        // Passes the state to the ThreadPool object.
        handleState(outState);
    }

}
