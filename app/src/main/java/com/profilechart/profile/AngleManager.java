package com.profilechart.profile;

public interface AngleManager {

    float getStartAngle(int index);

    float getAbsoluteStartAngle(int index);

    float getAbsoluteEndAngle(int index);

    float getEndAngle(int index);

    float getSweepAngle(int index);

    float getTotalSweepAngle();

    PieDirection getDirection();

}
