package com.pixelmags.android.download;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;

import java.net.URL;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by austincoutinho on 07/12/15.
 *
 * This class creates pools of background threads for downloading
 * the individual issue pages based on URLs passed on to it.
 *
 * The class is implemented as a singleton; the only way to get an IssueDownloadManager instance is to
 * call {@link #getInstance}.
 *
 * This class actually uses two threadpools in order to limit the number of
 * simultaneous image decoding threads to the number of available processor
 * cores.
 *
 * This class defines a handler that communicates back to the UI
 * thread to change the bitmap to reflect the state.
 *
 */

@SuppressWarnings("unused")
public class IssueDownloadManager {

    /*
     * Status indicators
     */
    static final int DOWNLOAD_FAILED = -1;
    static final int DOWNLOAD_STARTED = 1;
    static final int DOWNLOAD_COMPLETE = 2;
    static final int DECODE_STARTED = 3;
    static final int TASK_COMPLETE = 4;

    // Sets the size of the storage that's used to cache images
    private static final int IMAGE_CACHE_SIZE = 1024 * 1024 * 4;

    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;

    /**
     * NOTE: This is the number of total available cores. On current versions of
     * Android, with devices that use plug-and-play cores, this will return less
     * than the total number of cores. The total number of cores is not
     * available in current Android implementations.
     */

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    /*
     * Creates a cache of byte arrays indexed by image URLs. As new items are added to the
     * cache, the oldest items are ejected and subject to garbage collection.
     */
    // A single instance of IssueDownloadManager, used to implement the singleton pattern
    private static IssueDownloadManager sInstance = null;

    // A static block that sets class fields
    static {

        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        // Creates a single static instance of IssueDownloadManager
        sInstance = new IssueDownloadManager();
    }

    private final LruCache<URL, byte[]> mIssueImageCache;
    // A queue of Runnables for the image download pool
    private final BlockingQueue<Runnable> mDownloadWorkQueue;

    // A managed pool of background decoder threads
    // private final ThreadPoolExecutor mDecodeThreadPool;
    // A queue of IssueDownloadManager tasks. Tasks are handed to a ThreadPool.
    private final Queue<IssueDownloadTask> mDownloadTaskWorkQueue;
    // A managed pool of background download threads
    private final ThreadPoolExecutor mDownloadThreadPool;
    // An object that manages Messages in a Thread
    private Handler mHandler;


    /**
     * Constructs the work queues and thread pools used to download and decode images.
     */
    private IssueDownloadManager() {

        /*
         * Creates a work queue for the pool of Thread objects used for downloading, using a linked
         * list queue that blocks when the queue is empty.
         */
        mDownloadWorkQueue = new LinkedBlockingQueue<Runnable>();

        /*
         * Creates a work queue for the set of of task objects that control downloading and
         * decoding, using a linked list queue that blocks when the queue is empty.
         */
        mDownloadTaskWorkQueue = new LinkedBlockingQueue<IssueDownloadTask>();

        /*
         * Creates a new pool of Thread objects for the download work queue
         */
        mDownloadThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDownloadWorkQueue);


        // Instantiates a new cache based on the cache size estimate
        mIssueImageCache = new LruCache<URL, byte[]>(IMAGE_CACHE_SIZE) {

            /*
             * This overrides the default sizeOf() implementation to return the
             * correct size of each cache entry.
             */

            @Override
            protected int sizeOf(URL paramURL, byte[] paramArrayOfByte) {
                return paramArrayOfByte.length;
            }
        };
        /*
         * Instantiates a new anonymous Handler object and defines its
         * handleMessage() method. The Handler *must* run on the UI thread, because it moves photo
         * Bitmaps from the IssueDownloadTask object to the View object.
         * To force the Handler to run on the UI thread, it's defined as part of the IssueDownloadManager
         * constructor. The constructor is invoked when the class is first referenced, and that
         * happens when the View invokes startDownload. Since the View runs on the UI Thread, so
         * does the constructor and the Handler.
         */
        mHandler = new Handler(Looper.getMainLooper()) {

            /*
             * handleMessage() defines the operations to perform when the
             * Handler receives a new Message to process.
             */
            @Override
            public void handleMessage(Message inputMessage) {

                // Gets the image task from the incoming Message object.
                IssueDownloadTask issueDownloadTask = (IssueDownloadTask) inputMessage.obj;

                if (inputMessage != null) {


                        /*
                         * Chooses the action to take, based on the incoming message
                         */
                        switch (inputMessage.what) {

                            // If the download has started, sets background color to dark green
                            case DOWNLOAD_STARTED:
                                //localView.setStatusResource(R.drawable.imagedownloading);
                                System.out.println("Download Started for ..... x");
                                break;

                            /*
                             * If the download is complete, but the decode is waiting, sets the
                             * background color to golden yellow
                             */
                            case DOWNLOAD_COMPLETE:
                                // Sets background color to golden yellow
                                //localView.setStatusResource(R.drawable.decodequeued);
                                System.out.println("Download COMPLETED for ..... y");
                                break;
                            // If the decode has started, sets background color to orange
                            case DECODE_STARTED:
                                System.out.println("DECODE Started for ..... z");
                                //localView.setStatusResource(R.drawable.decodedecoding);
                                break;
                            /*
                             * The decoding is done, so this sets the
                             * ImageView's bitmap to the bitmap in the
                             * incoming message
                             */
                            case TASK_COMPLETE:
                                //localView.setImageBitmap(issueDownloadTask.getImage());
                                System.out.println("TASK COMPLETE ..... xyz");
                                recycleTask(issueDownloadTask);
                                break;
                            // The download failed, sets the background color to dark red
                            case DOWNLOAD_FAILED:
                               // localView.setStatusResource(R.drawable.imagedownloadfailed);
                                System.out.println("DOWNLOAD_FAILED ..... xyz");
                                // Attempts to re-use the Task object
                                recycleTask(issueDownloadTask);
                                break;
                            default:
                                // Otherwise, calls the super method
                                super.handleMessage(inputMessage);
                        }
                }
            }
        };
    }

    /**
     * Returns the IssueDownloadManager object
     * @return The global IssueDownloadManager object
     */
    public static IssueDownloadManager getInstance() {

        return sInstance;
    }
    
    /**
     * Cancels all Threads in the ThreadPool
     */
    public static void cancelAll() {

        /*
         * Creates an array of tasks that's the same size as the task work queue
         */
        IssueDownloadTask[] taskArray = new IssueDownloadTask[sInstance.mDownloadWorkQueue.size()];

        // Populates the array with the task objects in the queue
        sInstance.mDownloadWorkQueue.toArray(taskArray);

        // Stores the array length in order to iterate over the array
        int taskArraylen = taskArray.length;

        /*
         * Locks on the singleton to ensure that other processes aren't mutating Threads, then
         * iterates over the array of tasks and interrupts the task's current Thread.
         */
        synchronized (sInstance) {

            // Iterates over the array of tasks
            for (int taskArrayIndex = 0; taskArrayIndex < taskArraylen; taskArrayIndex++) {

                // Gets the task's current thread
                Thread thread = taskArray[taskArrayIndex].mThreadThis;

                // if the Thread exists, post an interrupt to it
                if (null != thread) {
                    thread.interrupt();
                }
            }
        }
    }

    /**
     * Stops a download Thread and removes it from the threadpool
     *
     * @param downloaderTask The download task associated with the Thread
     * @param pictureURL The URL being downloaded
     */
    static public void removeDownload(IssueDownloadTask downloaderTask, URL pictureURL) {

        // If the Thread object still exists and the download matches the specified URL
        if (downloaderTask != null && downloaderTask.getImageURL().equals(pictureURL)) {

            /*
             * Locks on this class to ensure that other processes aren't mutating Threads.
             */
            synchronized (sInstance) {

                // Gets the Thread that the downloader task is running on
                Thread thread = downloaderTask.getCurrentThread();

                // If the Thread exists, posts an interrupt to it
                if (null != thread)
                    thread.interrupt();
            }
            /*
             * Removes the download Runnable from the ThreadPool. This opens a Thread in the
             * ThreadPool's work queue, allowing a task in the queue to start.
             */
            sInstance.mDownloadThreadPool.remove(downloaderTask.getHTTPDownloadRunnable());
        }
    }

    /**
     * Starts an image download and decode
     *
     * @param issueImageURL
     * @param cacheFlag Determines if caching should be used
     * @return The task instance that will handle the work
     */
    static public IssueDownloadTask startDownload(
            URL issueImageURL,
            boolean cacheFlag) {

        /*
         * Gets a task from the pool of tasks, returning null if the pool is empty
         */
        IssueDownloadTask downloadTask = sInstance.mDownloadTaskWorkQueue.poll();

        // If the queue was empty, create a new task instead.
        if (null == downloadTask) {
            downloadTask = new IssueDownloadTask();
        }

        // Initializes the task
        downloadTask.initializeDownloaderTask(IssueDownloadManager.sInstance, issueImageURL, cacheFlag);

        /*
         * Provides the download task with the cache buffer corresponding to the URL to be
         * downloaded.
         */
        downloadTask.setByteBuffer(sInstance.mIssueImageCache.get(downloadTask.getImageURL()));

        // If the byte buffer was empty, the image wasn't cached
        if (null == downloadTask.getByteBuffer()) {

            /*
             * "Executes" the tasks' download Runnable in order to download the image. If no
             * Threads are available in the thread pool, the Runnable waits in the queue.
             */
            sInstance.mDownloadThreadPool.execute(downloadTask.getHTTPDownloadRunnable());

        } else {

            /*
             * Signals that the download is "complete", because the byte array already contains the
             * undecoded image. The decoding starts.
             */

            sInstance.handleState(downloadTask, DOWNLOAD_COMPLETE);
        }

        // Returns a task object, either newly-created or one from the task pool
        return downloadTask;
    }

    /**
     * Handles state messages for a particular task object
     * @param issueDownloadTask A task object
     * @param state The state of the task
     */
    @SuppressLint("HandlerLeak")
    public void handleState(IssueDownloadTask issueDownloadTask, int state) {
        switch (state) {

            // The task finished downloading and decoding the image
            case TASK_COMPLETE:

                System.out.println("TASK_COMPLETE handleState ..... xyz");

                // Puts the image into cache
                if (issueDownloadTask.isCacheEnabled()) {
                    // If the task is set to cache the results, put the buffer
                    // that was
                    // successfully decoded into the cache
                    mIssueImageCache.put(issueDownloadTask.getImageURL(), issueDownloadTask.getByteBuffer());
                }

                // Gets a Message object, stores the state in it, and sends it to the Handler
                Message completeMessage = mHandler.obtainMessage(state, issueDownloadTask);
                completeMessage.sendToTarget();
                break;

            // The task finished downloading the image
            case DOWNLOAD_COMPLETE:
                System.out.println("DOWNLOAD_COMPLETE handleState ..... xyz");
                /*
                 * Decodes the image, by queuing the decoder object to run in the decoder
                 * thread pool
                 */
               // mDecodeThreadPool.execute(issueDownloadTask.getPhotoDecodeRunnable());

            // In all other cases, pass along the message without any other action.
            default:
                mHandler.obtainMessage(state, issueDownloadTask).sendToTarget();
                break;
        }

    }

    /**
     * Recycles tasks by calling their internal recycle() method and then putting them back into
     * the task queue.
     * @param downloadTask The task to recycle
     */
    void recycleTask(IssueDownloadTask downloadTask) {
        
        // Frees up memory in the task
        downloadTask.recycle();
        
        // Puts the task object back into the queue for re-use.
        mDownloadTaskWorkQueue.offer(downloadTask);
    }
}
