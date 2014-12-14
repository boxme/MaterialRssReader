package com.desmond.materialrssreader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.desmond.materialrssreader.adapter.FeedAdapter;

/**
 * Instantiating DragController requires us to pass in a reference to the RecyclerView,
 * and the ImageView that we’re going to use as the overlay.
 *
 * onInterceptTouchEvent() and onTouchEvent() methods will be called when touch events occur.
 * onInterceptTouchEvent() is called before the touch event is processed and enables us to control
 * whether we handle the event, or we defer the handling to another component.
 *
 * In our case we want to take control during drag operations, so we hold boolean indicating the dragging state.
 */
public class DragController implements RecyclerView.OnItemTouchListener {

    public static final String TAG = DragController.class.getSimpleName();
    public static final int ANIMATION_DURATION = 100;

    private RecyclerView mRecyclerView;
    private ImageView mOverlay;
    private final GestureDetectorCompat mGestureDetector;

    private boolean isDragging = false;
    private View mDraggingView;
    private boolean mIsFirst = true;
    private long mDraggingItemID = -1;
    private float mStartY = 0f;
    private Rect mStartBounds = null;

    public DragController(RecyclerView recyclerView, ImageView overlay) {
        mRecyclerView = recyclerView;
        mOverlay = overlay;
        GestureDetector.SimpleOnGestureListener longClickGestureListener =
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public void onLongPress(MotionEvent e) {
                        super.onLongPress(e);
                        isDragging = true;
                        dragStart(e.getX(), e.getY());
                    }
                };
        mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(),
                longClickGestureListener);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (isDragging) {
            return true;
        }
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    /**
     * Process a touch event as part of a gesture that was claimed by returning true from
     * a previous call to {@link #onInterceptTouchEvent}.
     */
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        View view = rv.findChildViewUnder(x, y);
        if (e.getAction() == MotionEvent.ACTION_UP) {
            dragEnd(view);
            isDragging = false;
        }
        else {
            drag(y, view);
        }
    }

    private void dragStart(float x, float y) {
        mDraggingView = mRecyclerView.findChildViewUnder(x, y);

        mStartY = y - mDraggingView.getTop();
        paintViewToOverlay(mDraggingView);

        mOverlay.setTranslationY(mDraggingView.getTop());

        mDraggingView.setVisibility(View.INVISIBLE);
        mDraggingItemID = mRecyclerView.getChildItemId(mDraggingView);

        mStartBounds = new Rect(mDraggingView.getLeft(), mDraggingView.getTop(),
                mDraggingView.getRight(), mDraggingView.getBottom());
    }

    private void drag(int y, View view) {
        mOverlay.setTranslationY(y - mStartY);
        if (!isInPreviousBounds()) {
            if (view != null) {
                swapViews(view);
            }
        }
    }

    private void swapViews(View currentView) {
        long replacementID = mRecyclerView.getChildItemId(currentView);
        FeedAdapter adapter = (FeedAdapter) mRecyclerView.getAdapter();
        int start = adapter.getPositionforID(replacementID);
        int end = adapter.getPositionforID(mDraggingItemID);
        mIsFirst = (start == 0 || end == 0);
        adapter.moveItem(start, end);
        if (mIsFirst) {
            mRecyclerView.scrollToPosition(0);
            mIsFirst = false;
        }
        mStartBounds.top = currentView.getTop();
        mStartBounds.bottom = currentView.getBottom();
    }

    private void dragEnd(View view) {
        mOverlay.setImageBitmap(null);
        mDraggingView.setVisibility(View.VISIBLE);

        float translationY = mOverlay.getTranslationY();
        mDraggingView.setTranslationY(translationY - mStartBounds.top);

        // Note: getTop() of a view wont change after translation, it is the vertical location
        // of the view in its parent after onLayout. To change, use setTop()
        ViewCompat.animate(mDraggingView).translationY(0f).setDuration(ANIMATION_DURATION).start();
    }

    private void paintViewToOverlay(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        mOverlay.setImageBitmap(bitmap);
        mOverlay.setTop(0);
    }

    public boolean isInPreviousBounds() {
        float overlayTop = mOverlay.getTop() + mOverlay.getTranslationY();
        float overLayBottom = mOverlay.getBottom() + mOverlay.getTranslationY();
        return overlayTop < mStartBounds.bottom && overLayBottom > mStartBounds.top;
    }
}
