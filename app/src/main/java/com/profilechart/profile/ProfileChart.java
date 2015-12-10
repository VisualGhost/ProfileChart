package com.profilechart.profile;

import java.util.List;

public interface ProfileChart {

    void draw(List<PortfolioBreakdown> breakdownList);

    void selectSector(int index);

}
