package com.desmond.materialrssreader;

import com.desmond.materialrssreader.rss.models.Feed;

/**
 * Created by desmond on 9/11/14.
 */
public interface FeedConsumer {
    void setFeed(Feed feed);

    void handleError(String message);
}
