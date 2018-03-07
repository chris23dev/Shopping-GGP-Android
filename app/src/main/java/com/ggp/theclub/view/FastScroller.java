package com.ggp.theclub.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FastScroller extends LinearLayout {
    private int layoutHeight;
    private RecyclerView recyclerView;
    private ObjectAnimator objectAnimator;
    @Bind(R.id.fast_scroller_bubble) TextView fastScrollerBubbleView;
    @Bind(R.id.fast_scroller_handle) View fastScrollerHandleView;

    public FastScroller(Context context) {
        this(context, null);
    }

    public FastScroller(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FastScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.fast_scroller, this);
        ButterKnife.bind(this, view);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        layoutHeight = h - (recyclerView != null ? recyclerView.getPaddingBottom() : 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (event.getX() >= fastScrollerHandleView.getX()) {
                        if (objectAnimator != null) {
                            objectAnimator.cancel();
                        }
                        if (fastScrollerBubbleView.getVisibility() == INVISIBLE) {
                            showScrollerBubble();
                        }
                        fastScrollerHandleView.setSelected(true);
                    } else {
                        return false;
                    }
                case MotionEvent.ACTION_MOVE:
                    setFastScrollerPosition(event.getY());
                    setRecyclerViewPosition(event.getY());
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    fastScrollerHandleView.setSelected(false);
                    hideScrollerBubble();
                    return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private int getValueInRange(int min, int max, int value) {
        return Math.min(Math.max(min, value), max);
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!fastScrollerHandleView.isSelected()) {
                    float proportion = (float) recyclerView.computeVerticalScrollOffset() / ((float) recyclerView.computeVerticalScrollRange() - layoutHeight);
                    setFastScrollerPosition(layoutHeight * proportion);
                }
            }
        });
    }

    private void setRecyclerViewPosition(float y) {
        if (recyclerView != null) {
            float proportion;
            int itemCount = recyclerView.getAdapter().getItemCount();
            if (fastScrollerHandleView.getY() == 0) {
                proportion = 0;
            } else if (fastScrollerHandleView.getY() + fastScrollerHandleView.getHeight() >= layoutHeight) {
                proportion = 1;
            } else {
                proportion = y / (float) layoutHeight;
            }
            int targetPosition = getValueInRange(0, itemCount - 1, (int) (proportion * (float) itemCount));
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(targetPosition, 0);

            setScrollerBubbleText(targetPosition);
        }
    }

    private void setFastScrollerPosition(float y) {
        int scrollerHandleHeight = fastScrollerHandleView.getHeight();
        int scrollerBubbleHeight = fastScrollerBubbleView.getHeight();
        fastScrollerHandleView.setY(getValueInRange(0, layoutHeight - scrollerHandleHeight, (int) (y - scrollerHandleHeight / 2)));
        fastScrollerBubbleView.setY(getValueInRange(0, layoutHeight - scrollerBubbleHeight - scrollerHandleHeight / 2, (int) (y - scrollerBubbleHeight)));
    }

    private void showScrollerBubble() {
        fastScrollerBubbleView.setVisibility(VISIBLE);
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        objectAnimator = ObjectAnimator.ofFloat(fastScrollerBubbleView, "alpha", 0, 1).setDuration(125);
        objectAnimator.start();
    }

    private void hideScrollerBubble() {
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        objectAnimator = ObjectAnimator.ofFloat(fastScrollerBubbleView, "alpha", 1, 0).setDuration(250);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fastScrollerBubbleView.setVisibility(INVISIBLE);
                objectAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                fastScrollerBubbleView.setVisibility(INVISIBLE);
                objectAnimator = null;
            }
        });
        objectAnimator.start();
    }

    private void setScrollerBubbleText(int position) {
        String scrollerBubbleText = ((FastScrollerListener) recyclerView.getAdapter()).getFastScrollerBubbleText(position);
        fastScrollerBubbleView.setText(scrollerBubbleText);
    }

    public interface FastScrollerListener {
        String getFastScrollerBubbleText(int position);
    }
}