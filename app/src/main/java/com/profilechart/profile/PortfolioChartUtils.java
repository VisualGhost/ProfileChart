package com.profilechart.profile;

import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

public class PortfolioChartUtils {

    private static final float ANGLE_OF_ONE_PERCENTAGE = 3.6f; // 360o/100% = 3.6o

    private PortfolioChartUtils() {
        // to hide
    }

    /**
     * Converts '0.123456' to '12.35%'
     */
    public static String getReadablePercentage(String percentage) {
        return roundUp(percentage, "%");
    }

    private static String roundUp(String s, String symbol) {
        if (s != null) {
            try {
                Float fPercentage = Float.valueOf(s);
                return symbol != null ? (formatPercentage(100 * fPercentage) + symbol) : formatPercentage(100 * fPercentage);
            } catch (NumberFormatException e) {
                return "";
            } catch (IllegalFormatException e) {
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * Converts '0.123456' to '12.35'
     */
    public static String getReadablePL(String pl) {
        return roundUp(pl, null);
    }

    private static String formatPercentage(float v) {
        return String.format("%.2f", v);
    }

    /**
     * @return The angle of sector on chart.
     */
    public static float percentageToAngle(String percentage) {
        if (percentage != null) {
            try {
                return ANGLE_OF_ONE_PERCENTAGE * Float.valueOf(percentage) * 100;
            } catch (NumberFormatException e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * @return The angle of sector on chart.
     */
    public static float percentageToAngle(float percentage) {
        try {
            return ANGLE_OF_ONE_PERCENTAGE * percentage * 100;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * @param angle The angle in degrees
     */
    public static float getX(float angle, float radius) {
        return (float) Math.cos(angle * Math.PI / 180) * radius;
    }

    /**
     * @param angle The angle in degrees
     */
    public static float getY(float angle, float radius) {
        return (float) Math.sin(angle * Math.PI / 180) * radius;
    }

    /**
     * @param angle The angle in degrees
     */
    public static float getCenterSectorX(float angle, float sweepAngle, float radius) {
        return (float) Math.cos((angle + sweepAngle / 2) * Math.PI / 180) * radius;
    }

    /**
     * @param angle The angle in degrees
     */
    public static float getCenterSectorY(float angle, float sweepAngle, float radius) {
        return (float) Math.sin((angle + sweepAngle / 2) * Math.PI / 180) * radius;
    }

    public static RectF getRectF(Paint paint, float startAngle, float sweetAngle, float radius, String text) {
        float width = paint.measureText(text, 0, text.length());
        radius += width / Math.sqrt(2);
        float x = PortfolioChartUtils.getCenterSectorX(startAngle, sweetAngle, radius);
        float y = PortfolioChartUtils.getCenterSectorY(startAngle, sweetAngle, radius);
        float left = x - width / 2;
        float top = y - width / 2;
        float right = x + width / 2;
        float bottom = y + width / 2;
        return new RectF(left, top, right, bottom);
    }

    public static float getHeight(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    // TODO parse cursor
    public static List<PortfolioBreakdown> getBreakdownList(Cursor cursor) {
        List<PortfolioBreakdown> list = new ArrayList<>();
        list.add(new PortfolioBreakdownImpl("ADIDAS", "0.156339", "0.232918"));
        list.add(new PortfolioBreakdownImpl("AUDJPY", "0.200099", "0.000253"));
        list.add(new PortfolioBreakdownImpl("USDJPY", "0.150099", "0.000253"));
        list.add(new PortfolioBreakdownImpl("USDJPY", "0.300099", "0.000253"));
//        list.add(new PortfolioBreakdownImpl("USDJPY", "0.180099", "0.000253"));
        return list;
    }

}
