package com.desmond.materialrssreader.rss.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by desmond on 9/11/14.
 */
public class Feed implements Serializable {
    public static final Feed NONE = new Feed();

    private final List<Item> items = new ArrayList<Item>();

    public void addItem(Item item) {
        items.add(item);
    }

    public List<Item> getItems() {
        return items;
    }
}
