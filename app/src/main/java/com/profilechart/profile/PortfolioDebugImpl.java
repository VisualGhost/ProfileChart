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
        float textWidth = Math.max(PortfolioChartUtils.getWidth(mDebugPaint, instrumentName),
                PortfolioChartUtils.getWidth(mDebugPaint, percentage));
        float instrumentHeight = PortfolioChartUtils.getHeight(mDebugPaint, instrumentName);
        float percentageHeight = PortfolioChartUtils.getHeight(mDebugPaint, percentage);
        float textHeight = instrumentHeight + mPercentageBottomMargin + percentageHeight;
        RectF textBox = PortfolioChartUtils.getTextBoxRectF(radius, startAngle, sweetAngle, textWidth, textHeight);
        //canvas.drawRect(textBox, mDebugPaint);

        float cX = textBox.centerX();
        float cY = textBox.centerY();
        float radiusToCenterOfBox = (float) Math.sqrt(cX * cX + cY * cY);
        float angle = startAngle + sweetAngle / 2;
        float hypot;
        if (Double.compare(Math.sin(angle * Math.PI / 180), 0) != 0) {
            hypot = (float) (Math.abs(textBox.height() / 2 / Math.sin(angle * Math.PI / 180)));
        } else {
            hypot = textBox.height() / 2;
        }
        hypot = Math.min(hypot, textBox.height() / 2);
        float d = radiusToCenterOfBox - hypot;

        Log.e("Test2", " angle: " + angle + ", " + percentage + ", " + (2 * radius - d));

        textBox = PortfolioChartUtils.getTextBoxRectF(2 * radius - d, startAngle, sweetAngle, textWidth, textHeight);
        canvas.drawRect(textBox, mDebugPaint);
        //Log.e("Test", instrumentName + ", " + percentage + ", " + (d - radius));
        float x = textBox.left;
        float y = textBox.top + percentageHeight;

        float x1 = textBox.left;
        float x2 = textBox.right;
        float y1;
        if (-angle >= 180 && -angle <= 360) {
            y1 = textBox.top;
        } else {
            y1 = textBox.bottom;
        }
        float xPoint = (float) (Math.sqrt(radius * radius - y1 * y1));

        float xShift;
        if (-angle >= 90 && -angle <= 270) {
            xPoint = -xPoint;
            xShift = xPoint - textBox.right;
        } else {
            xShift = xPoint - textBox.left;
        }

        //TODO find yShift

        float yShift;

        if (-angle >= 0 && -angle <= 180) {
            yShift = Math.abs(textBox.bottom);
            float r = (float) (radius * Math.sin(angle * Math.PI / 180));
            if (Float.compare(-r, yShift) == 0) {
                //xShift = 0;
            }
            //Log.e("Test", ">>> " + instrumentName + ", " + percentage + ", " + yShift + ", r: " + r+", "+xShift);
        }

        if (xPoint >= textBox.left && xPoint <= textBox.right) {
            Log.e("Test", "x: " + xPoint + ", " + x1 + ", " + x2 + ", " + instrumentName + ", " + percentage + ", " + xShift);
        }

        if (-angle > 85 && -angle < 105) {
            xShift = 0;
        }

        if (-angle > 265 && -angle < 275) {
            xShift = 0;
        }

        canvas.drawText(percentage, x + xShift, y, mDebugPaint);
        y += instrumentHeight + mPercentageBottomMargin;
        canvas.drawText(instrumentName, x + xShift, y, mDebugPaint);
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
