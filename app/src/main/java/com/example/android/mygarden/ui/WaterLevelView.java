package com.example.android.mygarden.ui;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.example.android.mygarden.R;

public class WaterLevelView extends View {

    private float mRadius;
    private float mStrokeWidth;
    private int mValue;
    Context mContext;
    Paint mPaint;
    Path mPath;
    RectF mCircleRec;

    public WaterLevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray attrArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CircleMeter,
                0, 0
        );

        mRadius = attrArray.getDimension(R.styleable.CircleMeter_radius, 50f);
        mValue = attrArray.getInteger(R.styleable.CircleMeter_value, 100);
        mStrokeWidth = mRadius / 20;

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);

        mPath = new Path();
        mCircleRec = new RectF();
        mCircleRec.set(mStrokeWidth, mStrokeWidth, 2 * mRadius, 2 * mRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int colorPrimary = ContextCompat.getColor(mContext, R.color.dark_blue);
        int colorPrimaryLight = ContextCompat.getColor(mContext, R.color.light_blue);

        mPaint.setColor(colorPrimaryLight);
        drawCircle(canvas, mPaint, 0, 360);

        mPaint.setColor(colorPrimary);
        drawCircle(canvas, mPaint, 270, 360f * mValue / 100);

    }

    public void drawCircle(Canvas canvas, Paint paint, float start, float sweep) {
        mPath.reset();
        if (sweep == 360) {
            mPath.addCircle(mRadius + mStrokeWidth / 2, mRadius + mStrokeWidth / 2, mRadius - mStrokeWidth / 2, Path.Direction.CCW);
        } else if (sweep > 0) {
            mPath.arcTo(mCircleRec, start, sweep, false);
        }
        canvas.drawPath(mPath, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = (int) (2 * mRadius + mStrokeWidth);
        int desiredHeight = (int) (2 * mRadius + mStrokeWidth);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else { // UNSPECIFIED
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else { // UNSPECIFIED
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    public void setValue(int value) {
        mValue = value;
        invalidate();
    }

}