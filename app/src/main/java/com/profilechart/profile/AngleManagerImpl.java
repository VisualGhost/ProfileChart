package com.profilechart.profile;

import java.util.List;

public class AngleManagerImpl implements AngleManager {

    private final float[] mAngles;
    private PieDirection mPieDirection;

    public AngleManagerImpl(PieDirection pieDirection, final List<PortfolioBreakdown> breakdownList) {
        mPieDirection = pieDirection;
        mAngles = new float[breakdownList.size() + 1];
        float sum = 0;
        int i = 0;
        for (PortfolioBreakdown portfolioBreakdown : breakdownList) {
            if (portfolioBreakdown.isDrawable()) {
                mAngles[i++] = sum;
                sum += portfolioBreakdown.getAngle();
            }
        }
        mAngles[i] = sum;
    }

    private int getMultiplier() {
        return mPieDirection == PieDirection.COUNTERCLOCKWISE ? -1 : 1;
    }

    @Override
    public float getStartAngle(final int index) {
        return mAngles[index] * getMultiplier();
    }

    @Override
    public float getSweepAngle(final int index) {
        return (mAngles[index + 1] - mAngles[index]) * getMultiplier();
    }

    @Override
    public float getTotalSweepAngle() {
        return mAngles[mAngles.length - 1] - 360;
    }
}
