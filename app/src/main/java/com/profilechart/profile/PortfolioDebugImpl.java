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
        mDebugPaint.setStrokeWidth(1 * scale); // TODO
        mDebugPaint.setTextSize(12 * scale);// TODO
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
    public void drawText(final Canvas canvas, final float startAngle, final float sweetAngle,
                         final String instrumentName, final String percentage) {

        float commonWidth = Math.max(Utils.getWidth(mDebugPaint, instrumentName),
                Utils.getWidth(mDebugPaint, percentage));
        float instrumentHeight = Utils.getHeight(mDebugPaint, instrumentName);
        float percentageHeight = Utils.getHeight(mDebugPaint, percentage);
        float commonHeight = instrumentHeight + percentageHeight + mPercentageBottomMargin;
        RectF textBox = getTextRectF(startAngle, sweetAngle, commonWidth, commonHeight);
        float angle = getAngle(startAngle, sweetAngle);
        float halfOfDiagonal = getHalfOfDiagonal(textBox, angle);
        float distanceToCenterOfTextBox = getDistanceToCenterOfRectF(textBox);
        textBox = getShiftedRectF(textBox, startAngle, sweetAngle, halfOfDiagonal + distanceToCenterOfTextBox);
        float xPercentage = textBox.left;
        float yPercentage = textBox.top + percentageHeight;
        float xShift = getXShift(textBox, angle, getRadius());
        canvas.drawText(percentage, xPercentage + xShift, yPercentage, mDebugPaint);
        yPercentage += instrumentHeight + mPercentageBottomMargin;
        canvas.drawText(instrumentName, xPercentage + xShift, yPercentage, mDebugPaint);
    }

    private RectF getTextRectF(final float startAngle,
                               final float sweetAngle,
                               final float width,
                               final float height) {
        return Utils.getTextRectF(getRadius(), startAngle, sweetAngle, width, height);
    }

    private float getHalfOfDiagonal(final RectF rectF, float angle) {
        float diagonal;
        if (Double.compare(Math.sin(angle * Math.PI / 180), 0) != 0) {
            diagonal = (float) (Math.abs(rectF.height() / 2 / Math.sin(angle * Math.PI / 180)));
        } else {
            diagonal = rectF.height() / 2;
        }
        return Math.min(diagonal, rectF.height() / 2);
    }

    private float getAngle(final float startAngle, final float sweetAngle) {
        return startAngle + sweetAngle / 2;
    }

    private float getDistanceToCenterOfRectF(final RectF rectF) {
        float cX = rectF.centerX();
        float cY = rectF.centerY();
        return (float) Math.sqrt(cX * cX + cY * cY);
    }

    private RectF getShiftedRectF(final RectF rectF, final float startAngle, final float sweetAngle, float distance) {
        return Utils.getTextRectF(distance, startAngle, sweetAngle, rectF.width(), rectF.height());
    }

    private float getXCoordinateOfIntersectionBetweenCircleAndRectF(final RectF textBox, final float angle, float radius) {
        float y;
        if (-angle >= 180 && -angle <= 360) {
            y = textBox.top;
        } else {
            y = textBox.bottom;
        }
        return (float) (Math.sqrt(radius * radius - y * y));
    }

    private float getXShift(final RectF textBox, final float angle, float radius) {
        float xIntersection = getXCoordinateOfIntersectionBetweenCircleAndRectF(textBox, angle, radius);

        float xShift;
        if (-angle >= 90 && -angle <= 270) {
            xIntersection = -xIntersection;
            xShift = xIntersection - textBox.right;
        } else {
            xShift = xIntersection - textBox.left;
        }

        if (-angle > 85 && -angle < 105) {
            xShift = 0;
        }

        if (-angle > 265 && -angle < 275) {
            xShift = 0;
        }
        return xShift;
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
