package com.profilechart.profile;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;

public class PaintFactoryImpl implements PaintFactory {

    private final Paint mArcPaint;
    private final Paint mSelectedArcPaint;
    private final Paint mPercentagePaint;
    private final Paint mInstrumentNamePaint;
    private final Paint mPLInstrumentNamePaint;
    private final Paint mPLValuePaint;
    private final TextPaint mPLInstrumentNameTextPaint;

    private ColorFactory mColorFactory;
    private int mPLValueIncTextColor;
    private int mPLValueDecTextColor;

    private PaintFactoryImpl(Context context, Builder builder) {
        mColorFactory = new ColorFactoryImpl(context);

        mArcPaint = new Paint();
        mSelectedArcPaint = new Paint();
        mPLInstrumentNamePaint = new Paint();
        mPLValuePaint = new Paint();
        mPercentagePaint = new Paint();
        mInstrumentNamePaint = new Paint();
        mPLInstrumentNameTextPaint = new TextPaint(mPLInstrumentNamePaint);

        configArcPaint(mArcPaint, builder.mArcWidth);
        configArcPaint(mSelectedArcPaint, builder.mSelectedArcWidth);
        configPLInstrumentNamePaint(builder);
        configPLValuePaint(builder);
        configInstrumentNamePaint(builder);
        configPercentagePaint(builder);

        mPLValueIncTextColor = builder.mPLValueIncTextColor;
        mPLValueDecTextColor = builder.mPLValueDecTextColor;
    }

    private void configArcPaint(Paint paint, float strokeWidth) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
    }

    private void configPLInstrumentNamePaint(Builder builder) {
        mPLInstrumentNamePaint.setTextSize(builder.mPLInstrumentNameTextSize);
        mPLInstrumentNamePaint.setAntiAlias(true);
        mPLInstrumentNamePaint.setColor(builder.mPLTextColor);
    }

    private void configPLValuePaint(Builder builder) {
        mPLValuePaint.setTextSize(builder.mPLInstrumentNameTextSize);
        mPLValuePaint.setAntiAlias(true);
    }

    private void configInstrumentNamePaint(Builder builder) {
        mInstrumentNamePaint.setAntiAlias(true);
        mInstrumentNamePaint.setTextSize(builder.mInstrumentNameTextSize);
    }

    private void configPercentagePaint(Builder builder) {
        mPercentagePaint.setAntiAlias(true);
        mPercentagePaint.setTextSize(builder.mPercentageTextSize);
    }

    @Override
    public Paint getPaint(int colorIndex) {
        mArcPaint.setColor(mColorFactory.getColor(colorIndex));
        return mArcPaint;
    }

    @Override
    public Paint getSelectedPaint(int colorIndex) {
        mSelectedArcPaint.setColor(mColorFactory.getColor(colorIndex));
        return mSelectedArcPaint;
    }

    public Paint getPLInstrumentNamePaint() {
        return mPLInstrumentNamePaint;
    }

    @Override
    public Paint getIncPLValuePaint() {
        mPLValuePaint.setColor(mPLValueIncTextColor);
        return mPLValuePaint;
    }

    @Override
    public Paint getDecPLValuePaint() {
        mPLValuePaint.setColor(mPLValueDecTextColor);
        return mPLValuePaint;
    }

    @Override
    public Paint getPercentagePaint() {
        return mPercentagePaint;
    }

    @Override
    public Paint getInstrumentNamePaint() {
        return mInstrumentNamePaint;
    }

    @Override
    public TextPaint getPLInstrumentNameTextPaint() {
        return mPLInstrumentNameTextPaint;
    }

    public static class Builder {
        private float mArcWidth;
        private float mSelectedArcWidth;
        private float mPercentageTextSize;
        private float mInstrumentNameTextSize;
        private float mPLInstrumentNameTextSize;
        private int mPLTextColor;
        private int mPLValueIncTextColor;
        private int mPLValueDecTextColor;

        public Builder setArcWidth(final float arcWidth) {
            mArcWidth = arcWidth;
            return this;
        }

        public Builder setSelectedArcWidth(final float selectedArcWidth) {
            mSelectedArcWidth = selectedArcWidth;
            return this;
        }

        public Builder setPercentageTextSize(final float percentageTextSize) {
            mPercentageTextSize = percentageTextSize;
            return this;
        }

        public Builder setInstrumentNameTextSize(final float instrumentNameTextSize) {
            mInstrumentNameTextSize = instrumentNameTextSize;
            return this;
        }

        public Builder setPLInstrumentNameTextSize(final float PLInstrumentNameTextSize) {
            mPLInstrumentNameTextSize = PLInstrumentNameTextSize;
            return this;
        }

        public Builder setPLTextColor(final int PLTextColor) {
            mPLTextColor = PLTextColor;
            return this;
        }

        public Builder setPLValueIncTextColor(final int pLValueIncTextColor) {
            mPLValueIncTextColor = pLValueIncTextColor;
            return this;
        }

        public Builder setPLValueDecTextColor(final int pLValueDecTextColor) {
            mPLValueDecTextColor = pLValueDecTextColor;
            return this;
        }

        public PaintFactoryImpl build(Context context) {
            return new PaintFactoryImpl(context, this);
        }
    }
}
