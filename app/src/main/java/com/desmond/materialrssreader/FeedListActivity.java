package com.desmond.materialrssreader;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import com.desmond.materialrssreader.adapter.FeedAdapter;
import com.desmond.materialrssreader.rss.models.Feed;
import com.desmond.materialrssreader.rss.models.Item;


public class FeedListActivity extends ActionBarActivity
        implements FeedConsumer, FeedAdapter.ItemClickListener {

    public static final String TAG = FeedListActivity.class.getSimpleName();

    private static final String DATA_FRAGMENT_TAG = DataFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private FeedAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        ImageView overlay = (ImageView) findViewById(R.id.overlay);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnItemTouchListener(new DragController(mRecyclerView, overlay));

        DataFragment dataFragment =
                (DataFragment) getFragmentManager().findFragmentByTag(DATA_FRAGMENT_TAG);

        if (dataFragment == null) {
            dataFragment = (DataFragment) Fragment.instantiate(this, DataFragment.class.getName());
            dataFragment.setRetainInstance(true);
            getFragmentManager().beginTransaction()
                    .add(dataFragment, DATA_FRAGMENT_TAG)
                    .commit();
        }
    }


    @Override
    public void setFeed(Feed feed) {
        mAdapter = new FeedAdapter(feed.getItems(), this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void handleError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void itemClicked(Item item) {
        Intent detailIntent = new Intent(FeedListActivity.this, FeedDetailActivity.class);
        detailIntent.putExtra(FeedDetailActivity.ARG_ITEM, item);
        startActivity(detailIntent);
    }
}
