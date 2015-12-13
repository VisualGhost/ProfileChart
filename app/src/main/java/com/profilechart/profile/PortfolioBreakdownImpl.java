package com.profilechart.profile;

public class PortfolioBreakdownImpl implements PortfolioBreakdown {

    private static final float MIN_ANGLE_TO_DRAW = 54f; // We don't draw the pie if it's less than 15% (24 = 360*0.15)

    private String mInstrumentName; // ADIDAS
    private String mAllocationPercentage; // 10.64
    private String mPLPercentage; // 23.29
    private float mAngle;

    /**
     * We suppose that the data from Cursor put to the constructor without any manipulation.
     * It means we get instrumentName = "ADIDAS", AllocationPercentage =  0.106339 and PLPercentage = 0.232918
     **/
    public PortfolioBreakdownImpl(final String instrumentName,
                                  final String allocationPercentage,
                                  final String PLPercentage) {
        mInstrumentName = instrumentName;
        mAllocationPercentage = allocationPercentage;
        mPLPercentage = PLPercentage;
        mAngle = Utils.percentageToAngle(allocationPercentage);
    }

    @Override
    public String getInstrumentName() {
        return mInstrumentName;
    }

    @Override
    public String getAllocationPercentage() {
        return Utils.getReadablePercentage(mAllocationPercentage); // 12.22%
    }

    @Override
    public String getPLPercentage() {
        return Utils.getReadablePL(mPLPercentage); // 12.22
    }

    @Override
    public float getAngle() {
        return mAngle;
    }

    @Override
    public boolean isDrawable() {
        return Float.compare(mAngle, MIN_ANGLE_TO_DRAW) >= 0;
    }
}
