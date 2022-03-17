package com.ethanchris.android.healthpet.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.ethanchris.android.healthpet.R;

import java.io.InputStream;

public class PetView extends View {
    private Movie mMovie;

    long movieStart;

    public PetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics mMetrics = new DisplayMetrics();
        if (attrs != null) {
            TypedArray mAttrs = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PetView, 0, 0);
            InputStream stream = context.getResources().openRawResource(+ mAttrs.getResourceId(R.styleable.PetView_state, R.drawable.pet_idle));
            if (stream != null) {
                mMovie = Movie.decodeStream(stream);
            }
        } else {
            InputStream stream = context.getResources().openRawResource(+ R.drawable.pet_idle);
            mMovie = Movie.decodeStream(stream);
        }
        final WindowManager w = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        if (w != null) {
            final Display d = w.getDefaultDisplay();
            if (d != null) {
                d.getMetrics(mMetrics);
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (canvas != null) {
            super.onDraw(canvas);
            long now = android.os.SystemClock.uptimeMillis();
            if (movieStart == 0) { movieStart = now; }
            int gifTime;
            if (mMovie == null) {
                gifTime = 0;
            } else {
                gifTime = (int)((now - movieStart) % mMovie.duration());
            }
            if (mMovie != null) {
                mMovie.setTime(gifTime);
                mMovie.draw(canvas, 0, 0);
                this.invalidate();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("HealthPet", "pet touched");
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth, desiredHeight;
        if (mMovie != null) {
            desiredWidth = mMovie.width();
            desiredHeight = mMovie.height();
        } else {
            desiredWidth = 768;
            desiredHeight = 768;
        }

        Log.d("HealthPet", "width: " + desiredWidth + ", height: " + desiredHeight);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(widthSize, desiredWidth);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            height = Math.min(heightSize, desiredHeight);
        } else {
            height = desiredHeight;
        }
        Log.d("HealthPet", "actualWidth: " + width + ", actualHeight: " + height);

        setMeasuredDimension(width, height);
    }

    public void setGif(Context context, int id) {
        InputStream stream = context.getResources().openRawResource(+ id);
        mMovie = Movie.decodeStream(stream);
        movieStart = 0;
    }
}