package com.jongor_software.android.learning.coursera.pmaahs2.Week5;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by jon on 09/05/15.
 */
public class DownloaderTaskFragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();

    private DownloadFinishedListener mCallback;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Preserve across configurations
        setRetainInstance(true);

        // Create new DownloaderTask that "downloads" data
        DownloaderTask downloader = new DownloaderTask();

        // Retrieve arguments from DownloaderTaskFragment and prepare them for use with DownloaderTask
        ArrayList<Integer> resource_ids = getArguments().getIntegerArrayList(MainActivity.TAG_FRIEND_RES_IDS);
        int[] ids = new int[resource_ids.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = resource_ids.get(i);
        }

        // Start the DownloaderTask
        downloader.execute(ids);
    }

    // Assign current hosting activity to mCallback
    // Store application context for use by downloadTweets()

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mContext = activity.getApplicationContext();

        // Make sure that the hosting activity has implemented the correct callback interface
        try {
            mCallback = (DownloadFinishedListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DownloadFinishedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    // This class must use the downloadTweets method. Ultimately, it must also pass newly available
    // data back to the hosting Activity using the DownloadFinishedListener interface
    public class DownloaderTask extends AsyncTask<int[], Void, String[]> {

        // Simulates downloading Twitter data from the network
        private String[] downloadTweets(int resourceIds[]) {
            final int simulatedDelay = 2000;
            String[] feeds = new String[resourceIds.length];
            try {
                for (int index = 0; index < resourceIds.length; index++) {
                    InputStream inputStream;
                    BufferedReader reader;
                    try {
                        // Pretend downloading takes a long time
                        Thread.sleep(simulatedDelay);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    inputStream = mContext.getResources().openRawResource(resourceIds[index]);
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String readLine;
                    StringBuffer buffer = new StringBuffer();

                    while ((readLine = reader.readLine()) != null) {
                        buffer.append(readLine);
                    }

                    feeds[index] = buffer.toString();

                    if (reader != null) {
                        reader.close();
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return feeds;
        }

        @Override
        protected String[] doInBackground(int[]... ints) {
            return downloadTweets(ints[0]);
        }

        @Override
        protected void onPostExecute(String[] strings) {
            mCallback.notifyDataRefreshed(strings);
        }
    }
}
