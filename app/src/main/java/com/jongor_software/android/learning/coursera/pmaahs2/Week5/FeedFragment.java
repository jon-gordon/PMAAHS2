package com.jongor_software.android.learning.coursera.pmaahs2.Week5;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jongor_software.android.learning.coursera.pmaahs2.R;

/**
 * Created by jon on 09/05/15.
 */
public class FeedFragment extends Fragment {

    private TextView mTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.week5_feed, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTextView = (TextView) getView().findViewById(R.id.week5_feed_view);
        mTextView.setText(getArguments().getString(MainActivity.TAG_TWEET_DATA));
    }
}
