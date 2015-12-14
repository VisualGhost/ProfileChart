package com.profilechart.profile;

import android.os.Parcel;
import android.os.Parcelable;

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

    public PortfolioBreakdownImpl(Parcel in) {
        String[] strings = new String[3];
        in.readStringArray(strings);
        mInstrumentName = strings[0];
        mAllocationPercentage = strings[1];
        mPLPercentage = strings[2];
        mAngle = Utils.percentageToAngle(mAllocationPercentage);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeStringArray(new String[]{mInstrumentName, mAllocationPercentage, mPLPercentage});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PortfolioBreakdownImpl createFromParcel(Parcel in) {
            return new PortfolioBreakdownImpl(in);
        }

        public PortfolioBreakdownImpl[] newArray(int size) {
            return new PortfolioBreakdownImpl[size];
        }
    };
}
