package com.longfor.audioproject.utils.richText;

/**
 * Created by Jesson_Yi on 2016/6/22.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.longfor.audioproject.R;
import com.longfor.audioproject.utils.GlideApp;
import com.longfor.audioproject.utils.GlideRequest;
import com.longfor.audioproject.utils.ScreenUtils;
import com.longfor.audioproject.utils.SizeUtils;

import java.util.HashSet;

import static com.bumptech.glide.load.resource.gif.GifDrawable.LOOP_FOREVER;

/**
 * @author wangjun
 * @date 2021/9/7 13:47
 * @Des :处理器Image标签下载
 */
public class GlideImageGeter implements Html.ImageGetter {

    private HashSet<Target> targets;
    private HashSet<GifDrawable> gifDrawables;
    private final Context mContext;
    private final TextView mTextView;

    public void recycle() {
        targets.clear();
        for (GifDrawable gifDrawable : gifDrawables) {
            gifDrawable.setCallback(null);
            gifDrawable.recycle();
        }
        gifDrawables.clear();
    }

    public GlideImageGeter(Context context, TextView textView) {
        this.mContext = context;
        this.mTextView = textView;
        targets = new HashSet<>();
        gifDrawables = new HashSet<>();
        mTextView.setTag(R.id.tag_extra_id, this);
    }

    @Override
    public Drawable getDrawable(String url) {
        final UrlDrawable urlDrawable = new UrlDrawable();
        GlideRequest load;
        final Target target;
        if (isGif(url)) {
            load = GlideApp.with(mContext).asGif().load(url);
            target = new GifTarget(urlDrawable);
        } else {
            load = GlideApp.with(mContext).asBitmap().load(url);
            target = new BitmapTarget(urlDrawable);
        }
        targets.add(target);
        load.into(target);
        return urlDrawable;
    }

    private static boolean isGif(String path) {
        int index = path.lastIndexOf('.');
        return index > 0 && TextUtils.equals("gif".toUpperCase(), path.substring(index + 1).toUpperCase());
    }

    /**
     * 用于显示gif图
     */
    private class GifTarget extends SimpleTarget<GifDrawable> {
        private final UrlDrawable urlDrawable;


        private GifTarget(UrlDrawable urlDrawable) {
            this.urlDrawable = urlDrawable;

        }


        @Override
        public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
            int w = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(78);
            int hh = resource.getIntrinsicHeight();
            int ww = resource.getIntrinsicWidth();
            int high = hh * w / ww;
            Rect rect = new Rect(0, 0, w, high);
            resource.setBounds(rect);
            urlDrawable.setBounds(rect);
            urlDrawable.setDrawable(resource);
            gifDrawables.add(resource);
            resource.setCallback(mTextView);
            resource.start();
            resource.setLoopCount(LOOP_FOREVER);
            mTextView.setText(mTextView.getText());
            mTextView.invalidate();
        }
    }

    /**
     * 用于显示普通的图片
     */
    private class BitmapTarget extends SimpleTarget<Bitmap> {
        private final UrlDrawable urlDrawable;

        public BitmapTarget(UrlDrawable urlDrawable) {
            this.urlDrawable = urlDrawable;
        }

        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            Drawable drawable = new BitmapDrawable(mContext.getResources(), resource);
            int w = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(78);
            int hh = drawable.getIntrinsicHeight();
            int ww = drawable.getIntrinsicWidth();
            int high = hh * w / ww;
            Rect rect = new Rect(0, 0, w, high);
            drawable.setBounds(rect);
            urlDrawable.setBounds(rect);
            urlDrawable.setDrawable(drawable);
            mTextView.setText(mTextView.getText());
            mTextView.invalidate();
        }
    }
}