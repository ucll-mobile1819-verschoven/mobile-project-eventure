package com.ucll.eventure;

import android.support.design.widget.AppBarLayout;
import android.util.Log;

public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
    private State mCurrentState = State.IDLE;

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED, 1);
            }
            mCurrentState = State.EXPANDED;
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED, 0);
            }
            mCurrentState = State.COLLAPSED;
        } else {
            //TODO: FADE OUT
            float fraction2 = ((Math.abs(i)) / ((float) appBarLayout.getTotalScrollRange()));
            Log.d("scroll", String.valueOf(fraction2));
            float delay = 0.1f;
            float fraction = Math.max(fraction2 + delay, 0f);
            onStateChanged(appBarLayout, State.IDLE, (1 - fraction));

        }
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, State state, float percent);
}
