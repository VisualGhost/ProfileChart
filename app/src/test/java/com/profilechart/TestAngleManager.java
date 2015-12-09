package com.profilechart;

import com.profilechart.profile.AngleManager;
import com.profilechart.profile.AngleManagerImpl;
import com.profilechart.profile.PieDirection;
import com.profilechart.profile.PortfolioBreakdown;
import com.profilechart.profile.PortfolioBreakdownImpl;
import com.profilechart.profile.PortfolioChartUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestAngleManager {

    private AngleManager mClockWiseAngleManager;
    private AngleManager mCounterClockWiseAngleManager;

    @Before
    public void setup() throws Exception {
        List<PortfolioBreakdown> breakdownList = new ArrayList<>();
        breakdownList.add(new PortfolioBreakdownImpl("ADIDAS", "0.106339", "0.232918"));
        breakdownList.add(new PortfolioBreakdownImpl("AUDJPY", "0.300099", "0.000253"));
        mClockWiseAngleManager = new AngleManagerImpl(PieDirection.CLOCKWISE, breakdownList);
        mCounterClockWiseAngleManager = new AngleManagerImpl(PieDirection.COUNTERCLOCKWISE, breakdownList);
    }

    @Test
    public void getStartAngle_isCorrect() throws Exception {
        Assert.assertEquals(mClockWiseAngleManager.getStartAngle(0), 0f, 0.00001);
        Assert.assertEquals(mClockWiseAngleManager.getStartAngle(1), PortfolioChartUtils.percentageToAngle("0.106339"), 0.00001);
        Assert.assertEquals(mCounterClockWiseAngleManager.getStartAngle(0), 0f, 0.00001);
        Assert.assertEquals(mCounterClockWiseAngleManager.getStartAngle(1), -PortfolioChartUtils.percentageToAngle("0.106339"), 0.00001);
    }

    @Test
    public void getEndAngle_isCorrect() throws Exception {
        Assert.assertEquals(mClockWiseAngleManager.getSweepAngle(0), PortfolioChartUtils.percentageToAngle("0.106339"), 0.00001);
        Assert.assertEquals(mClockWiseAngleManager.getSweepAngle(1), PortfolioChartUtils.percentageToAngle("0.300099"), 0.00001);
        Assert.assertEquals(mCounterClockWiseAngleManager.getSweepAngle(0), -PortfolioChartUtils.percentageToAngle("0.106339"), 0.00001);
        Assert.assertEquals(mCounterClockWiseAngleManager.getSweepAngle(1), -PortfolioChartUtils.percentageToAngle("0.300099"), 0.00001);

    }
}
