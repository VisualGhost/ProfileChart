package com.profilechart.profile;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

public class PortfolioDebugImpl implements PortfolioDebug {

    private Paint mDebugPaint;
    private Paint mDushedPaint;

    private float mWidth;
    private float mHeight;
    private float mArcRadius;
    private float mCircleMargin;
    private float mSelectedArcWidth;

    public PortfolioDebugImpl(
            final float width,
            final float height,
            final float arcRadius,
            final float circleMargin,
            final float scale,
            final float selectedArcWidth) {
        mWidth = width;
        mHeight = height;
        mArcRadius = arcRadius;
        mCircleMargin = circleMargin;
        mSelectedArcWidth = selectedArcWidth;
        initDebugPaint(scale);
    }

    private void initDebugPaint(float scale) {
        mDebugPaint = new Paint();
        mDebugPaint.setStyle(Paint.Style.STROKE);
        mDebugPaint.setColor(Color.CYAN);
        mDebugPaint.setAntiAlias(true);

        mDushedPaint = new Paint(mDebugPaint);
        mDushedPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
    }

    @Override
    public void drawXAxis(final Canvas canvas) {
        canvas.drawLine(-mWidth / 2, 0, mWidth / 2, 0, mDebugPaint);
    }

    @Override
    public void drawYAxis(final Canvas canvas) {
        canvas.drawLine(0, -mHeight / 2, 0, mHeight / 2, mDebugPaint);
    }

    @Override
    public void drawCircleAroundPie(final Canvas canvas) {
        float r = getRadius();
        RectF rectF = new RectF(-r, -r, r, r);
        canvas.drawArc(rectF, 0, -360, false, mDebugPaint);
    }

    private float getRadius() {
        return mArcRadius + mCircleMargin;
    }

    @Override
    public void drawCenterOfSector(final Canvas canvas, final float startAngle, final float sweetAngle) {
        float startX = 0;
        float startY = 0;
        float r = getRadius();
        float endX = Utils.getCenterSectorX(startAngle, sweetAngle, r);
        float endY = Utils.getCenterSectorY(startAngle, sweetAngle, r);

        Path path = new Path();
        path.moveTo(startX, startY);
        path.lineTo(endX, endY);
        canvas.drawPath(path, mDushedPaint);
    }

    @Override
    public void drawSectorLine(Canvas canvas, float startAngle) {
        float startX = 0;
        float startY = 0;
        float r = getRadius();
        float endX = Utils.getX(startAngle, r);
        float endY = Utils.getY(startAngle, r);
        canvas.drawLine(startX, startY, endX, endY, mDebugPaint);
    }

    @Override
    public void drawSquareInsideCircle(final Canvas canvas) {
        RectF rectF = Utils.getRectFAroundCircle(mArcRadius, mSelectedArcWidth);
        canvas.drawRect(rectF, mDebugPaint);
    }
}
