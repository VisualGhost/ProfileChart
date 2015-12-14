package com.profilechart.profile;

import java.util.List;

public class AngleManagerImpl implements AngleManager {

    private final float[] mAngles;
    private PieDirection mPieDirection;
    private float mSum = 0;

    public AngleManagerImpl(PieDirection pieDirection, final List<PortfolioBreakdown> breakdownList) {
        mPieDirection = pieDirection;
        mAngles = new float[breakdownList.size() + 1];
        int i = 0;
        for (PortfolioBreakdown portfolioBreakdown : breakdownList) {
            if (portfolioBreakdown.isDrawable()) {
                mAngles[i++] = mSum;
                mSum += portfolioBreakdown.getAngle();
            }
        }
        mAngles[i] = mSum;
    }

    private int getMultiplier() {
        return mPieDirection == PieDirection.COUNTERCLOCKWISE ? -1 : 1;
    }

    @Override
    public float getStartAngle(final int index) {
        return mAngles[index] * getMultiplier();
    }

    @Override
    public float getAbsoluteStartAngle(int index) {
        if (mPieDirection == PieDirection.COUNTERCLOCKWISE) {
            return Math.abs(getStartAngle(index));
        } else {
            return 360 - Math.abs(getEndAngle(index));
        }
    }

    @Override
    public float getAbsoluteEndAngle(int index) {
        if (mPieDirection == PieDirection.COUNTERCLOCKWISE) {
            return Math.abs(getEndAngle(index));
        } else {
            return 360 - Math.abs(getStartAngle(index));
        }
    }

    @Override
    public float getEndAngle(int index) {
        return mAngles[index + 1] * getMultiplier();
    }

    @Override
    public float getSweepAngle(final int index) {
        return (mAngles[index + 1] - mAngles[index]) * getMultiplier();
    }

    @Override
    public float getTotalSweepAngle() {
        if (mPieDirection == PieDirection.COUNTERCLOCKWISE) {
            return mSum - 360;
        } else {
            return 360 - mSum;
        }
    }

    @Override
    public PieDirection getDirection() {
        return mPieDirection;
    }
}
