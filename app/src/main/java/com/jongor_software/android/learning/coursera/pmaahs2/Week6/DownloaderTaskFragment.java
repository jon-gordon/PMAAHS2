package com.jongor_software.android.learning.coursera.pmaahs2.Week6;

import android.app.Activity;
import android.app.Fragment;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jongor_software.android.learning.coursera.pmaahs2.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class DownloaderTaskFragment extends Fragment {

    private DownloadFinishedListener mCallback;
    private Context mContext;
    private final int NOTIFICATION_ID = 13012014;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Preserve across reconfigurations
        setRetainInstance(true);

        // TODO:
        // Create new downloader task that "downloads" data

        // TODO:
        // Retrieve arguments from DownloaderTaskFragment and prepare for use with downloader task

        // TODO:
        // Start the downloader task
    }

    // Assign current hosting activity to mCallback
    // Store application context for use by downloadTweets().  Ultimately, it must also pass
    // newly available data back to the hosting activity using the DownloadFinishedListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mContext = activity.getApplicationContext();

        // Make sure the hosting activity has implemented the correct callback interface
        try {
            mCallback = (DownloadFinishedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement DownloadFinishedListener");
        }
    }

    // Null out mCallback
    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }


    // TODO:
    // Implement an AsyncTask subclass called DownloaderTask.
    // This class must use the downloadTweets method.
    public class DownloaderTask extends AsyncTask<int[], Void, String[]> {

        // Simulates downloading Twitter data from the network
        private String[] downloadTweets(int resourceIds[]) {

            final int simulatedDelay = 2000;
            String[] feeds = new String[resourceIds.length];
            boolean downloadCompleted = false;

            try {
                for (int index = 0; index < resourceIds.length; index++) {
                    InputStream inputStream;
                    BufferedReader reader;
                    try {
                        // Pretend downloading takes a long time
                        Thread.sleep(simulatedDelay);
                    } catch (InterruptedException e) {
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

                downloadCompleted = true;
                saveTweetsToFile(feeds);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Notify user that downloading has completed
            notify(downloadCompleted);

            return feeds;
        }

        // If necessary, notifies user that the tweet downloads are complete.  Sends an ordered
        // broadcast back to the BroadcastReceiver in MainActivity to determine whether notification
        // is necessary
        private void notify(final boolean success) {

            final Intent restartMainActivityIntent = new Intent(mContext, MainActivity.class);
            restartMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Sends an ordered broadcast to determine whether MainActivity is active and in the
            // foreground.  Creates a new BroadcastReceiver to receive a result indicating state
            // of MainActivity

            // The action for this Intent is MainActivity.DATA_REFRESHED_ACTION
            // The result, MainActivity.IS_ALIVE, indicates that MainActivity is active and in the
            // foreground.
            mContext.sendOrderedBroadcast(new Intent(MainActivity.DATA_REFRESHED_ACTION), null,
                    new BroadcastReceiver() {
                        final String failMsg = mContext
                                .getString(R.string.download_failed);
                        final String successMsg = mContext
                                .getString(R.string.download_success);
                        final String notificationSentMsg = mContext
                                .getString(R.string.notification_sent);

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            // TODO:
                            // Check whether the MainActivity received the broadcast or not
                            boolean msgReceived = false;

                            if (!msgReceived) {
                                // TODO:
                                // If not, create a PendingIntent using the
                                // restartMainActivityIntent and set its flags to
                                // FLAG_UPDATE_CURRENT

                                // Uses R.layout.custom_notification for the layout of the
                                // notification View.
                                RemoteViews mContentView = new RemoteViews(
                                        mContext.getPackageName(),
                                        R.layout.custom_notification);

                                // TODO:
                                // Set the notification's text to reflect whether download completed
                                // successfully

                                // TODO:
                                // Use the Notification.Builder class to create the Notification.
                                // Use android.R.drawable.stat_sys_warning for the icon and
                                // setAutoCancel(true)
                                Notification.Builder notification = null;

                                // TODO:
                                // Send the notification
                                Toast.makeText(mContext, notificationSentMsg, Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                Toast.makeText(mContext, success ? successMsg : failMsg,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }, null, 0, null, null);
        }

        @Override
        protected String[] doInBackground(int[]... ints) {
            return downloadTweets(ints[0]);
        }

        // Save the tweets to a file
        private void saveTweetsToFile(String[] result) {
            PrintWriter writer = null;
            try {
                FileOutputStream fos = mContext.openFileOutput(MainActivity.TWEET_FILENAME,
                        Context.MODE_PRIVATE);
                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)));

                for (String s : result) {
                    writer.println(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }
}
