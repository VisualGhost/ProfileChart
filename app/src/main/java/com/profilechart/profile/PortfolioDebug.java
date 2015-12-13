package com.profilechart.profile;

import android.graphics.Canvas;

public interface PortfolioDebug {

    /**
     * Draws x-axis.
     */
    void drawXAxis(Canvas canvas);

    /**
     * Draws y-axis.
     */
    void drawYAxis(Canvas canvas);

    /**
     * Draws the circle indicating the distance of labels to the center of the pie.
     */
    void drawCircleAroundPie(Canvas canvas);

    /**
     * Draws the line that points on the senter of sector.
     *
     * @param startAngle The angle of starting sector.
     * @param sweetAngle The range of sector.
     */
    void drawCenterOfSector(Canvas canvas, float startAngle, float sweetAngle);

    void drawTextBox(Canvas canvas, float startAngle, float sweetAngle, String instrumentName, String percentage);

    /**
     * Draws two lines.
     *
     * @param startAngle The angle of starting sector.
     * @param sweetAngle The range of sector.
     */
    void drawSector(Canvas canvas, float startAngle, float sweetAngle);

    /**
     * This square shows us the area where we can put the text inside the circle and it won't cross with stroke.
     */
    void drawSquareInsideCircle(Canvas canvas);

}
