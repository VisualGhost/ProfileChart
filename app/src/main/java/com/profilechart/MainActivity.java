package com.profilechart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.profilechart.profile.PortfolioBreakdown;
import com.profilechart.profile.PortfolioChartUtils;
import com.profilechart.profile.ProfileChart;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProfileChart profileChart = (ProfileChart) findViewById(R.id.profileChartView);
        List<PortfolioBreakdown> breakdownList = PortfolioChartUtils.getBreakdownList(null);
        profileChart.draw(breakdownList);
    }
}
