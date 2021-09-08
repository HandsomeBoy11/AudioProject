package com.longfor.audioproject.utils.richText;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * @author wangjun
 * @date 2021/9/7 13:47
 * @Des :
 */
public class UrlDrawable extends BitmapDrawable implements Drawable.Callback {
    private Drawable drawable;

    @SuppressWarnings("deprecation")
    public UrlDrawable() {
    }

    @Override
    public void draw(Canvas canvas) {
        if (drawable != null) {
            drawable.draw(canvas);

        }
    }

    public Drawable getDrawable() {
        return drawable;
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        scheduleSelf(what, when);
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        unscheduleSelf(what);
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        invalidateSelf();
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}