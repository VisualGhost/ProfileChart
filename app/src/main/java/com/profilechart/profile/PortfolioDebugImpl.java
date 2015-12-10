package com.profilechart.profile;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class PortfolioDebugImpl implements PortfolioDebug {

    private Paint mDebugPaint;
    private float mWidth;
    private float mHeight;
    private float mArcRadius;
    private float mCircleMargin;
    private float mPercentageBottomMargin;
    private float mSelectedArcWidth;

    public PortfolioDebugImpl(
            final float width,
            final float height,
            final float arcRadius,
            final float circleMargin,
            final float scale,
            final float percentageBottomMargin,
            final float selectedArcWidth) {
        mWidth = width;
        mHeight = height;
        mArcRadius = arcRadius;
        mCircleMargin = circleMargin;
        mPercentageBottomMargin = percentageBottomMargin;
        mSelectedArcWidth = selectedArcWidth;
        initDebugPaint(scale);
    }

    private void initDebugPaint(float scale) {
        mDebugPaint = new Paint();
        mDebugPaint.setStrokeWidth(1 * scale);
        mDebugPaint.setTextSize(12 * scale);
        mDebugPaint.setStyle(Paint.Style.STROKE);
        mDebugPaint.setColor(Color.CYAN);
        mDebugPaint.setAntiAlias(true);
    }

    @Override
    public void drawXAxis(final Canvas canvas) {
        canvas.drawLine(-mWidth / 2, 0, mWidth / 2, 0, mDebugPaint);
    }

    @Override
    public void drawYAxis(final Canvas canvas) {
        canvas.drawLine(0, -mWidth / 2, 0, mWidth / 2, mDebugPaint);
    }

    @Override
    public void drawCircleAroundPie(final Canvas canvas) {
        float r = mArcRadius + mCircleMargin;
        RectF rectF = new RectF(-r, -r, r, r);
        canvas.drawArc(rectF, 0, -360, false, mDebugPaint);
    }

    @Override
    public void drawLine(final Canvas canvas, final float startAngle, final float sweetAngle) {
        float startX = 0;
        float startY = 0;
        float radius = mArcRadius + mCircleMargin;
        float endX = PortfolioChartUtils.getCenterSectorX(startAngle, sweetAngle, radius);
        float endY = PortfolioChartUtils.getCenterSectorY(startAngle, sweetAngle, radius);
        canvas.drawLine(startX, startY, endX, endY, mDebugPaint);
    }

    @Override
    public void drawTextBox(final Canvas canvas, final float startAngle, final float sweetAngle,
                            final String instrumentName, final String percentage) {
        // TODO not a clean code
        float radius = mArcRadius + mCircleMargin;
        RectF rectF = PortfolioChartUtils.getRectF(mDebugPaint, startAngle, sweetAngle, radius, instrumentName);
        canvas.drawRect(rectF, mDebugPaint);
        //canvas.drawCircle(rectF.centerX(), rectF.centerY(), rectF.width()/2, mDebugPaint);

        float x = rectF.left;
        float y = rectF.centerY() - mPercentageBottomMargin;
        canvas.drawText(percentage, x, y, mDebugPaint);

        y = rectF.centerY() + PortfolioChartUtils.getHeight(mDebugPaint, instrumentName) + mPercentageBottomMargin;
        canvas.drawText(instrumentName, x, y, mDebugPaint);
    }

    @Override
    public void drawSector(Canvas canvas, float startAngle, float sweetAngle) {
        float startX = 0;
        float startY = 0;
        float radius = (float) Math.sqrt(mWidth * mWidth + mHeight * mHeight);
        float endX = PortfolioChartUtils.getX(startAngle, radius);
        float endY = PortfolioChartUtils.getY(startAngle, radius);
        canvas.drawLine(startX, startY, endX, endY, mDebugPaint);
    }

    @Override
    public void drawBoxInsideCircle(final Canvas canvas) {
        float left = 0 - mArcRadius + mSelectedArcWidth;
        float top = 0 - mArcRadius + mSelectedArcWidth;
        float right = 0 + mArcRadius - mSelectedArcWidth;
        float bottom = 0 + mArcRadius - mSelectedArcWidth;
        RectF rectF = new RectF(left, top, right, bottom);
        canvas.drawRect(rectF, mDebugPaint);
    }
}
