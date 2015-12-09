package com.profilechart.profile;

import android.graphics.Paint;

public interface PaintFactory {

    Paint getPaint(int index);

    Paint getSelectedPaint(int index);

}
