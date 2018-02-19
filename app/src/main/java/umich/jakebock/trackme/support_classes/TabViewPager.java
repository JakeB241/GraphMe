package umich.jakebock.trackme.support_classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import umich.jakebock.trackme.fragments.GraphFragment;

/**
 * Created by Jake on 2/18/2018.
 */

public class TabViewPager extends ViewPager
{
    public TabViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return !GraphFragment.isFullScreen && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        return !GraphFragment.isFullScreen && super.onInterceptTouchEvent(event);

    }

}
