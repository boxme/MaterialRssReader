package com.desmond.materialrssreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.desmond.materialrssreader.R;
import com.desmond.materialrssreader.rss.models.Item;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by desmond on 9/11/14.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    public static final String TAG = FeedAdapter.class.getSimpleName();

    private DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());

    private List<Item> items;
    private ItemClickListener itemClickListener;

    public FeedAdapter(List<Item> objects, @NonNull ItemClickListener itemClickListener) {
        this.items = objects;
        this.itemClickListener = itemClickListener;
        setHasStableIds(true);
    }

    // Override this method to return the specific view layout ID for
    // the different view type based on its position in the RecyclerView
    @Override
    public int getItemViewType(int position) {
        return R.layout.feed_list_item;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View rootView = LayoutInflater.from(context).inflate(viewType, parent, false);

        return ViewHolder.newInstance(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Item item = items.get(position);
        holder.setTitle(item.getTitle());
        holder.setDescription(Html.fromHtml(item.getDescription()));
        holder.setDate(dateFormat.format(new Date(item.getPubDate())));
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getPubDate();
    }

    public void moveItem(int start, int end) {
        int max = Math.max(start, end);
        int min = Math.min(start, end);

        if (min >= 0 && max < items.size()) {
            Item item = items.remove(min);
            items.add(max, item);
            notifyItemMoved(min, max);
        }
    }

    public int getPositionforID(long id) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getPubDate() == id) {
                return i;
            }
        }
        return -1;
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder {
        private final View parent;
        private final TextView title;
        private final TextView description;
        private final TextView date;

        public static ViewHolder newInstance(View parent) {
            TextView title = (TextView) parent.findViewById(R.id.feed_item_title);
            TextView description = (TextView) parent.findViewById(R.id.feed_item_description);
            TextView date = (TextView) parent.findViewById(R.id.feed_item_date);
            return new ViewHolder(parent, title, description, date);
        }

        private ViewHolder(View parent, TextView title, TextView description, TextView date) {
            super(parent);
            this.parent = parent;
            this.title = title;
            this.description = description;
            this.date = date;
        }

        public void setTitle(CharSequence text) {
            title.setText(text);
        }

        public void setDescription(CharSequence text) {
            description.setText(text);
        }

        public void setDate(CharSequence text) {
            date.setText(text);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            parent.setOnClickListener(listener);
        }

        public View getTitleView() {
            return title;
        }

        public View getDescView() {
            return description;
        }

        public View getDateView() {
            return date;
        }
    }

    public interface ItemClickListener {
        void itemClicked(Item item);
    }
}
