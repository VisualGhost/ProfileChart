package com.profilechart;

import com.profilechart.profile.PortfolioBreakdown;
import com.profilechart.profile.PortfolioBreakdownImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestPortfolioBreakDown {

    private PortfolioBreakdown drawableBreakDown;
    private PortfolioBreakdown notDrawableBreakDown;

    @Before
    public void setup() throws Exception {
        drawableBreakDown = new PortfolioBreakdownImpl("ADIDAS", "0.106339", "0.232918");
        notDrawableBreakDown = new PortfolioBreakdownImpl("AUDJPY", "0.000099", "0.000253");
    }

    @Test
    public void getName_isCorrect() throws Exception {
        Assert.assertEquals(drawableBreakDown.getInstrumentName(), "ADIDAS");
        Assert.assertEquals(notDrawableBreakDown.getInstrumentName(), "AUDJPY");
    }

    @Test
    public void getAllocationPercentage_isCorrect() throws Exception {
        Assert.assertEquals(drawableBreakDown.getAllocationPercentage(), "10.63%");
        Assert.assertEquals(notDrawableBreakDown.getAllocationPercentage(), "0.01%");
    }

    @Test
    public void getPLPercentage_isCorrect() throws Exception {
        Assert.assertEquals(drawableBreakDown.getPLPercentage(), "23.29");
        Assert.assertEquals(notDrawableBreakDown.getPLPercentage(), "0.03");
    }

    @Test
    public void getAngle_isCorrect() throws Exception {
        Assert.assertEquals(drawableBreakDown.getAngle(), 38.28204f, 0.00001);
        Assert.assertEquals(notDrawableBreakDown.getAngle(), 0.03564f, 0.00001);
    }

    @Test
    public void isDrawable_isCorrect() throws Exception {
        Assert.assertTrue(drawableBreakDown.isDrawable());
        Assert.assertFalse(notDrawableBreakDown.isDrawable());
    }
}