package lk.hd192.project.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class NonSwappableViewPager extends ViewPager {

    boolean Swappable = true;

    public NonSwappableViewPager(@NonNull Context context) {
        super(context);
    }

    public NonSwappableViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        if (Swappable) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (Swappable) {
            return super.onTouchEvent(ev);
        }
        return false;
    }

    public void setSwappable(boolean swappable) {
        Swappable = swappable;
    }
}
