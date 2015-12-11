package com.profilechart.profile;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

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
        //canvas.drawLine(-mWidth / 2, 0, mWidth / 2, 0, mDebugPaint);
    }

    @Override
    public void drawYAxis(final Canvas canvas) {
        //canvas.drawLine(0, -mWidth / 2, 0, mWidth / 2, mDebugPaint);
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
        float radius = (float) Math.sqrt(mWidth * mWidth + mHeight * mHeight);
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

        float height = PortfolioChartUtils.getHeight(mDebugPaint, instrumentName) +
                PortfolioChartUtils.getHeight(mDebugPaint, percentage) + mPercentageBottomMargin;

        RectF rectF1 = new RectF(rectF);
        float delta = rectF.height() - height;
        rectF1.top = rectF1.top + delta / 2;
        rectF1.bottom = rectF1.bottom - delta / 2;
        canvas.drawRect(rectF1, mDebugPaint);
        //canvas.drawCircle(rectF.centerX(), rectF.centerY(), rectF.width() / 2, mDebugPaint);

        float x = rectF.left;
        float y = rectF.centerY() - mPercentageBottomMargin;
        canvas.drawText(percentage, x, y, mDebugPaint);

        y = rectF.centerY() + PortfolioChartUtils.getHeight(mDebugPaint, instrumentName) + mPercentageBottomMargin;
        canvas.drawText(instrumentName, x, y, mDebugPaint);
        //------------------Find max length ----------------------------
        float cX = rectF1.centerX();
        float cY = rectF1.centerY();
        float radiusToCenterOfBox = (float) Math.sqrt(cX * cX + cY * cY);
        float angle = startAngle + sweetAngle / 2;
        float hypot;
        if (Math.sin(angle * Math.PI / 180) != 0) {
            hypot = (float) (Math.abs(rectF1.height() / 2 / Math.sin(angle * Math.PI / 180)));
        } else {
            hypot = rectF1.height() / 2;
        }
        float d = radiusToCenterOfBox - hypot;
        Log.e("Test", instrumentName + ", " + percentage + ", " + (d - radius));
    }

    @Override
    public void drawSector(Canvas canvas, float startAngle, float sweetAngle) {
        float startX = 0;
        float startY = 0;
        float radius = (float) Math.sqrt(mWidth * mWidth + mHeight * mHeight);
        float endX = PortfolioChartUtils.getX(startAngle, radius);
        float endY = PortfolioChartUtils.getY(startAngle, radius);
        //canvas.drawLine(startX, startY, endX, endY, mDebugPaint);
    }

    @Override
    public void drawBoxInsideCircle(final Canvas canvas) {
        RectF rectF = PortfolioChartUtils.getRectFAroundCircle(mArcRadius, mSelectedArcWidth);
        //canvas.drawRect(rectF, mDebugPaint);
    }
}
