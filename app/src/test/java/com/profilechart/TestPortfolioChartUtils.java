package com.profilechart;

import com.profilechart.profile.Utils;

import org.junit.Assert;
import org.junit.Test;

public class TestPortfolioChartUtils {

    @Test
    public void angleToPercentage_isCorrect() throws Exception {
        Assert.assertEquals(Utils.angleToPercentage(72), "20.00 %");
        Assert.assertEquals(Utils.angleToPercentage(0), "0.00 %");
        Assert.assertEquals(Utils.angleToPercentage(360), "100.00 %");
    }
}
