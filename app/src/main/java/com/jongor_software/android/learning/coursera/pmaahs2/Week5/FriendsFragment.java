package com.jongor_software.android.learning.coursera.pmaahs2.Week5;

import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by jon on 09/05/15.
 */
public class FriendsFragment extends ListFragment {

    // Hosting Activity
    private SelectionListener mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, MainActivity.FRIENDS));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Make sure that the hosting activity has implemented the callback interface
        try {
            mCallback = (SelectionListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SelectionListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Enable user interaction only if data is available
        setAllowUserClicks(mCallback.canAllowUserClicks());
    }

    // Enable/disable user interaction
    void setAllowUserClicks(boolean allowUserInteraction) {
        getListView().setEnabled(allowUserInteraction);
        if (allowUserInteraction) {
            getListView().setBackgroundColor(Color.WHITE);
        }
        else {
            getListView().setBackgroundColor(Color.DKGRAY);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Inform hosting activity of user's selection
        if (mCallback != null) {
            mCallback.onItemSelected(position);
        }
    }
}
