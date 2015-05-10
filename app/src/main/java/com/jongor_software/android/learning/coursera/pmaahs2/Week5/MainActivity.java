package com.jongor_software.android.learning.coursera.pmaahs2.Week5;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.jongor_software.android.learning.coursera.pmaahs2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jon on 09/05/15.
 */
public class MainActivity extends Activity implements SelectionListener, DownloadFinishedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String TAG_NAME = "name";
    private static final String TAG_USER = "user";
    private static final String TAG_TEXT = "text";
    private static final String TAG_FRIENDS_FRAGMENT = "friends_fragment";
    private static final String TAG_FEED_FRAGMENT = "feed_fragment";
    private static final String TAG_DOWNLOADER_FRAGMENT = "downloader_fragment";
    private static final String TAG_IS_DATA_AVAILABLE = "is_data_available";
    private static final String TAG_PROCESSED_FEEDS = "processed_feeds";

    static final String TAG_TWEET_DATA = "data";
    static final String TAG_FRIEND_RES_IDS = "friends";

    public static final String[] FRIENDS = {
            "taylorswift13", "msrebeccablack", "ladygaga"
    };

    // Raw feed file IDs used to reference stored tweet data
    public static final ArrayList<Integer> sRawTextFeedIds = new ArrayList<>(Arrays.asList(
            R.raw.tswift, R.raw.rblack, R.raw.lgaga
    ));

    private FragmentManager mFragmentManager;
    private FriendsFragment mFriendsFragment;
    private FeedFragment mFeedFragment;
    private DownloaderTaskFragment mDownloaderFragment;
    private boolean mIsInteractionEnabled;
    private String[] mFormattedFeeds = new String[sRawTextFeedIds.size()];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week5_activity_main);

        mFragmentManager = getFragmentManager();

        // Reset instance state on reconfiguration
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }
        else {
            setupFragments();
        }
    }

    // One time setup of UI and retained (headless) fragment
    private void setupFragments() {
        installFriendsFragment();
        installDownloaderTaskFragment();
    }

    // Add FriendsFragment to Activity
    private void installFriendsFragment() {

        // Make new fragment
        mFriendsFragment = new FriendsFragment();

        // Give Fragment to the FragmentManager
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.week5_fragment_container, mFriendsFragment, TAG_FRIENDS_FRAGMENT);
        transaction.commit();
    }

    // Add DownlaoderTaskFragment to Activity
    private void installDownloaderTaskFragment() {

        // Make new fragment
        mDownloaderFragment = new DownloaderTaskFragment();

        // Set DownloaderTaskFragment arguments
        Bundle args = new Bundle();
        args.putIntegerArrayList(TAG_FRIEND_RES_IDS, sRawTextFeedIds);
        mDownloaderFragment.setArguments(args);

        // Give Fragment to the FragmentManager
        mFragmentManager.beginTransaction()
                .add(mDownloaderFragment, TAG_DOWNLOADER_FRAGMENT)
                .commit();
    }

    // Called back by DownloaderTask after data has been loaded
    @Override
    public void notifyDataRefreshed(String[] feeds) {

        // Process downloaded data
        parseJSON(feeds);

        // Enable user interaction
        mIsInteractionEnabled = true;
        allowUserClicks();
    }

    // Enable user interaction with FriendFragment
    private void allowUserClicks() {
        mFriendsFragment.setAllowUserClicks(true);
    }

    // Report whether user interaction is enabled
    @Override
    public boolean canAllowUserClicks() {
        return mIsInteractionEnabled;
    }

    // Installs the FeedFragment when a friend name is selected in the FriendsFragment
    @Override
    public void onItemSelected(int position) {
        installFeedFragment(mFormattedFeeds[position]);
    }

    // Add feed to fragment
    private void installFeedFragment(String tweetData) {
        // Make new fragment
        mFeedFragment = new FeedFragment();

        // Set fragment arguments
        Bundle args = new Bundle();
        args.putString(TAG_TWEET_DATA, tweetData);
        mFeedFragment.setArguments(args);

        // Give fragment to FragmentManager
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.week5_fragment_container, mFeedFragment, TAG_FEED_FRAGMENT);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (mFriendsFragment != null) {
            savedInstanceState.putString(TAG_FRIENDS_FRAGMENT, mFriendsFragment.getTag());
        }
        if (mFeedFragment != null) {
            savedInstanceState.putString(TAG_FEED_FRAGMENT, mFeedFragment.getTag());
        }
        if (mDownloaderFragment != null) {
            savedInstanceState.putString(TAG_DOWNLOADER_FRAGMENT, mDownloaderFragment.getTag());
        }

        savedInstanceState.putBoolean(TAG_IS_DATA_AVAILABLE, mIsInteractionEnabled);
        savedInstanceState.putStringArray(TAG_PROCESSED_FEEDS, mFormattedFeeds);

        super.onSaveInstanceState(savedInstanceState);
    }

    // Restore saved instance state
    private void restoreState(Bundle savedInstanceState) {

        // Fragments were saved in onSaveInstanceState
        mFriendsFragment = (FriendsFragment) mFragmentManager.
                findFragmentByTag(savedInstanceState.getString(TAG_FRIENDS_FRAGMENT));

        mFeedFragment = (FeedFragment) mFragmentManager.
                findFragmentByTag(savedInstanceState.getString(TAG_FEED_FRAGMENT));

        mDownloaderFragment = (DownloaderTaskFragment) mFragmentManager.
                findFragmentByTag(savedInstanceState.getString(TAG_DOWNLOADER_FRAGMENT));

        mIsInteractionEnabled = savedInstanceState.getBoolean(TAG_IS_DATA_AVAILABLE);
        if (mIsInteractionEnabled == true) {
            mFormattedFeeds = savedInstanceState.getStringArray(TAG_PROCESSED_FEEDS);
        }
    }

    // Convert raw data in JSON format into text for display
    private void parseJSON(String[] feeds) {
        JSONArray[] JSONFeeds = new JSONArray[feeds.length];
        for (int i = 0; i < JSONFeeds.length; i++) {
            try {
                JSONFeeds[i] = new JSONArray(feeds[i]);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            String name = "";
            String tweet = "";
            JSONArray tmp = JSONFeeds[i];

            // String buffer for feeds
            StringBuffer tweetRec = new StringBuffer("");
            for (int j = 0; j < tmp.length(); j++) {
                try {
                    tweet = tmp.getJSONObject(j).getString(TAG_TEXT);
                    JSONObject user = (JSONObject) tmp.getJSONObject(j).get(TAG_USER);
                    name = user.getString(TAG_NAME);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                tweetRec.append(name + " - " + tweet + "\n\n");
            }

            mFormattedFeeds[i] = tweetRec.toString();
        }
    }
}
