package com.profilechart.profile;

import android.content.Context;
import android.graphics.Paint;

public class PaintFactoryImpl implements PaintFactory {

    private Paint mPaint;
    private Paint mSelectedPaint;
    private ColorFactory mColorFactory;

    public PaintFactoryImpl(Context context, float strokeWidth, float selectedStrokeWidth) {
        mPaint = new Paint();
        configPaint(mPaint, strokeWidth);
        mSelectedPaint = new Paint();
        configPaint(mSelectedPaint, selectedStrokeWidth);
        mColorFactory = new ColorFactoryImpl(context);
    }

    private void configPaint(Paint paint, float strokeWidth) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
    }

    @Override
    public Paint getPaint(int index) {
        mPaint.setColor(mColorFactory.getColor(index));
        return mPaint;
    }


    @Override
    public Paint getSelectedPaint(int index) {
        mSelectedPaint.setColor(mColorFactory.getColor(index));
        return mSelectedPaint;
    }
}
