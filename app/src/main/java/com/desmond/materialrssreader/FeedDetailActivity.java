package com.desmond.materialrssreader;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.desmond.materialrssreader.rss.models.Item;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FeedDetailActivity extends ActionBarActivity {

    public static final String ARG_ITEM = "ARG_ITEM";
    public static final String NEWLINE = "\\n";
    public static final String BR = "<br />";
    public static final String HTML_MIME_TYPE = "text/html";

    private DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Item item = (Item) getIntent().getSerializableExtra(ARG_ITEM);

        TextView title = (TextView) findViewById(R.id.feed_detail_title);
        TextView date = (TextView) findViewById(R.id.feed_detail_date);
        WebView webView = (WebView) findViewById(R.id.feed_detail_body);

        // Give the views transition names to identify them when generating
        // the transition animation.
        // Possible to do this in XML, but would need to create a separate
        // layout in res/layout-v21 with the additional attributes to avoid
        // lint warnings.
        ViewCompat.setTransitionName(title, getString(R.string.transition_title));
        ViewCompat.setTransitionName(date, getString(R.string.transition_date));
        ViewCompat.setTransitionName(webView, getString(R.string.transition_body));

        title.setText(item.getTitle());
        date.setText(dateFormat.format(new Date(item.getPubDate())));
        String html = item.getContent();

        html = html.replaceAll(NEWLINE, BR);
        webView.loadData(html, HTML_MIME_TYPE, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == android.R.id.home) {
            ActivityCompat.finishAfterTransition(this);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
