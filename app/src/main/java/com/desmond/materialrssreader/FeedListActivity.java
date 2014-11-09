package com.desmond.materialrssreader;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.desmond.materialrssreader.adapter.FeedAdapter;
import com.desmond.materialrssreader.rss.models.Feed;
import com.desmond.materialrssreader.rss.models.Item;


public class FeedListActivity extends ActionBarActivity implements FeedConsumer {

    private static final String DATA_FRAGMENT_TAG = DataFragment.class.getSimpleName();

    private ListView mListView;
    private FeedAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.list_content);

        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(FeedListActivity.this, FeedDetailActivity.class);
                Item item = mAdapter.getItem(position);
                detailIntent.putExtra(FeedDetailActivity.ARG_ITEM, item);
                startActivity(detailIntent);
            }
        });

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
        mAdapter = new FeedAdapter(this, feed.getItems());
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void handleError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
