package com.profilechart.profile;

import android.os.Parcelable;

public interface PortfolioBreakdown extends Parcelable{

    String getInstrumentName();

    String getAllocationPercentage();

    String getPLPercentage();

    float getAngle();

    boolean isDrawable();
}
