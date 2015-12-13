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

    void drawText(Canvas canvas, float startAngle, float sweetAngle, String instrumentName, String percentage);

    /**
     * Draws the line indicating the new sector.
     *
     * @param startAngle The angle of starting sector.
     */
    void drawSectorLine(Canvas canvas, float startAngle);

    /**
     * This square shows us the area where we can put the text inside the circle and it won't cross with stroke.
     */
    void drawSquareInsideCircle(Canvas canvas);

}
