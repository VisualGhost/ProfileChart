package com.profilechart.profile;

import android.graphics.Canvas;

public interface PortfolioDebug {

    void drawXAxis(Canvas canvas);

    void drawYAxis(Canvas canvas);

    void drawCircleAroundPie(Canvas canvas);

    void drawLine(Canvas canvas, float startAngle, float sweetAngle);

    void drawTextBox(Canvas canvas, float startAngle, float sweetAngle, String instrumentName, String percentage);

    void drawSector(Canvas canvas, float startAngle, float sweetAngle);

    void drawBoxInsideCircle(Canvas canvas);

}
