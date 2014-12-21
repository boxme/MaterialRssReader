package com.desmond.materialrssreader;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

        // Get the ViewHolder corresponding to the list item which has been clicked
        FeedAdapter.ViewHolder viewHolder =
                (FeedAdapter.ViewHolder) mRecyclerView.findViewHolderForItemId(item.getPubDate());

        String titleName = getString(R.string.transition_title);
        String dateName = getString(R.string.transition_date);
        String bodyName = getString(R.string.transition_body);

        // Pair the relevant View objects from the ViewHolder to the Views with the transition name
        // corresponding to the String value.
        Pair<View, String> titlePair = Pair.create(viewHolder.getTitleView(), titleName);
        Pair<View, String> datePair = Pair.create(viewHolder.getDateView(), dateName);
        Pair<View, String> bodyPair = Pair.create(viewHolder.getDescView(), bodyName);

        // Use ActivityOptionsCompat.makeSceneTransitionAnimation() to generate
        // an ActivityOptionsCompat object containing these mappings
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this, titlePair, datePair, bodyPair);
        ActivityCompat.startActivity(this, detailIntent, options.toBundle());
    }
}
