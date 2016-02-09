package com.alexandrepiveteau.library.tutorial.widgets;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by kevinleperf on 09/02/16.
 */
public class BlockableRightViewPager extends ViewPager {
    private float mPreviousPositionX = 0;
    private boolean mNewState;

    public BlockableRightViewPager(Context context) {
        super(context);
        mNewState = true;
    }

    public BlockableRightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mNewState = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        boolean canCall = true;

        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mPreviousPositionX = (int) ev.getX();
                break;
            }
            case MotionEvent.ACTION_MOVE:
                float newX = ev.getX();

                if (newX < mPreviousPositionX) {
                    canCall = false;
                }
        }

        if (mNewState || canCall) {
            return super.onTouchEvent(ev);
        }
        return true;
    }

    public void setSwipableRight(boolean newState) {
        mNewState = newState;
    }
}
