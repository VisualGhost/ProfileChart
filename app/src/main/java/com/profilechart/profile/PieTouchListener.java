package com.profilechart.profile;

import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class PieTouchListener implements View.OnTouchListener {

    private final float mWidth;
    private final float mHeight;
    private List<PortfolioBreakdown> mBreakdownList;
    private AngleManager mAngleManager;

    public PieTouchListener(final float width, final float height) {
        mWidth = width;
        mHeight = height;
    }

    public void setBreakdownList(final List<PortfolioBreakdown> breakdownList) {
        mBreakdownList = breakdownList;
    }

    public void setAngleManager(final AngleManager angleManager) {
        mAngleManager = angleManager;
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (mBreakdownList != null && mBreakdownList.size() > 0 && mAngleManager != null) {
            float angle = getAngle(event);
            int index = getSectorIndex(angle);
            if (v instanceof ProfileChart) {
                ((ProfileChart) v).selectSector(index);
            }
        }

        return false;
    }

    private float getAngle(final MotionEvent event) {
        float x = event.getX() - mWidth / 2;
        float y = event.getY() - mHeight / 2;
        float radius = (float) Math.sqrt(x * x + y * y);
        float angle;
        angle = (float) (Math.acos(x / radius) * 180 / Math.PI);
        if (y > 0) {
            angle = 360 - angle;
        }
        return mAngleManager.getDirection() == PieDirection.COUNTERCLOCKWISE ? -angle : angle;
    }

    private int getSectorIndex(float angle) {
        int index = 0;
        for (PortfolioBreakdown breakdown : mBreakdownList) {
            if (breakdown.isDrawable()) {
                if (Math.abs(angle) >= mAngleManager.getAbsoluteStartAngle(index) && Math.abs(angle) <= mAngleManager.getAbsoluteEndAngle(index)) {
                    break;
                }
                index++;
            }
        }
        return index;
    }
}
