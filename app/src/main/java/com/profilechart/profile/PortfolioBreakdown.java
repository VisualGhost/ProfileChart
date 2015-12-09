package com.profilechart.profile;

public interface PortfolioBreakdown {

    String getInstrumentName();

    String getAllocationPercentage();

    String getPLPercentage();

    float getAngle();

    boolean isDrawable();
}
