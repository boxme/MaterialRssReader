package com.desmond.materialrssreader;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.desmond.materialrssreader.net.VolleySingleton;
import com.desmond.materialrssreader.rss.models.Feed;
import com.desmond.materialrssreader.rss.models.RssRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment implements Response.Listener<Feed>, Response.ErrorListener {

    private Feed mFeed;

    private VolleySingleton mVolley;

    private FeedConsumer mFeedConsumer;
    private boolean mIsLoading = false;

    public DataFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        Context context = inflater.getContext();

        initVolley(context);
        update();
        return null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof FeedConsumer) {
            mFeedConsumer = (FeedConsumer) activity;
        }
    }

    @Override
    public void onDetach() {
        mFeedConsumer = null;
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        mVolley.stop();
        super.onDestroyView();
    }

    private void initVolley(Context context) {
        if (mVolley == null) {
            mVolley = VolleySingleton.getInstance(context);
        }
    }

    private void update() {
        if (mFeed == null || !isLoading()) {
            String url = getString(R.string.feed_url);
            mVolley.addToRequestQueue(new RssRequest(Request.Method.GET, url, this, this));
            mIsLoading = true;
        }
        else {
            if (mFeedConsumer != null) {
                mFeedConsumer.setFeed(mFeed);
            }
        }
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (mFeedConsumer != null) {
            mFeedConsumer.handleError(error.getLocalizedMessage());
        }

        mIsLoading = false;
    }

    @Override
    public void onResponse(Feed feed) {
        mFeed = feed;
        if (mFeedConsumer != null) {
            mFeedConsumer.setFeed(feed);
        }

        mIsLoading = false;
    }
}
