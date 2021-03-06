package com.profilechart.profile;

import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

// TODO public
public class Utils {

    private static final float ANGLE_OF_ONE_PERCENTAGE = 3.6f; // 360o/100% = 3.6o

    private Utils() {
        // to hide
    }

    /**
     * Converts '0.123456' to '12.35%'
     */
    static String getReadablePercentage(String percentage) {
        return roundUp(percentage, " %");
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
    static String getReadablePL(String pl) {
        return roundUp(pl, null);
    }

    private static String formatPercentage(float v) {
        return String.format("%.2f", v);
    }

    /**
     * @return The angle of sector on chart.
     */
    static float percentageToAngle(String percentage) {
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
    static float percentageToAngle(float percentage) {
        try {
            return ANGLE_OF_ONE_PERCENTAGE * percentage * 100;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Converts e.g. 36o to 10.00%
     */
    static String angleToPercentage(float angle) {
        try {
            return formatPercentage(angle / ANGLE_OF_ONE_PERCENTAGE) + " %";
        } catch (NumberFormatException e) {
            return "";
        } catch (IllegalFormatException e) {
            return "";
        }
    }

    /**
     * @param angle The angle in degrees
     */
    static float getX(float angle, float radius) {
        return (float) Math.cos(angle * Math.PI / 180) * radius;
    }

    /**
     * @param angle The angle in degrees
     */
    static float getY(float angle, float radius) {
        return (float) Math.sin(angle * Math.PI / 180) * radius;
    }

    /**
     * @param angle The angle in degrees
     */
    static float getCenterSectorX(float angle, float sweepAngle, float radius) {
        return (float) Math.cos((angle + sweepAngle / 2) * Math.PI / 180) * radius;
    }

    /**
     * @param angle The angle in degrees
     */
    static float getCenterSectorY(float angle, float sweepAngle, float radius) {
        return (float) Math.sin((angle + sweepAngle / 2) * Math.PI / 180) * radius;
    }

    static RectF getTextRectF(
            float radius,
            float startAngle,
            float sweetAngle,
            float textWidth,
            float textHeight
    ) {
        float x = Utils.getCenterSectorX(startAngle, sweetAngle, radius);
        float y = Utils.getCenterSectorY(startAngle, sweetAngle, radius);
        float left = x - textWidth / 2;
        float top = y - textHeight / 2;
        float right = x + textWidth / 2;
        float bottom = y + textHeight / 2;
        return new RectF(left, top, right, bottom);
    }

    static float getHeight(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    static float getWidth(Paint paint, String text) {
        return paint.measureText(text);
    }

    static RectF getRectFAroundCircle(float arcRadius, float padding) {
        float left = -arcRadius + padding;
        float top = -arcRadius + padding;
        float right = arcRadius - padding;
        float bottom = arcRadius - padding;
        return new RectF(left, top, right, bottom);
    }

    // TODO parse cursor. Remove this method
    public static List<PortfolioBreakdown> getBreakdownList(Cursor cursor) {
        List<PortfolioBreakdown> list = new ArrayList<>();
        list.add(new PortfolioBreakdownImpl("ADIDAS", "0.151", "0.232918"));
        list.add(new PortfolioBreakdownImpl("AUDJPY", "0.20", "-0.000253"));
        list.add(new PortfolioBreakdownImpl("USDJPY", "0.150099", "0.000253"));
        list.add(new PortfolioBreakdownImpl("USDJPY", "0.1500099", "0.00253"));
        list.add(new PortfolioBreakdownImpl("USDJPY", "0.150099", "0.000253"));
        list.add(new PortfolioBreakdownImpl("USDJPY", "0.140099", "0.000253"));
        list.add(new PortfolioBreakdownImpl("BLABLA", "0.160099", "0.000253"));
        list.add(new PortfolioBreakdownImpl("KUKUKU", "0.140099", "0.000253"));
        return list;
    }

}
