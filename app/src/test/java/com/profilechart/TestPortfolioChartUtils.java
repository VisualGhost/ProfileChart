package com.profilechart;

import com.profilechart.profile.PortfolioChartUtils;

import org.junit.Assert;
import org.junit.Test;

public class TestPortfolioChartUtils {

    @Test
    public void angleToPercentage_isCorrect() throws Exception {
        Assert.assertEquals(PortfolioChartUtils.angleToPercentage(72), "20.00 %");
        Assert.assertEquals(PortfolioChartUtils.angleToPercentage(0), "0.00 %");
        Assert.assertEquals(PortfolioChartUtils.angleToPercentage(360), "100.00 %");
    }
}
