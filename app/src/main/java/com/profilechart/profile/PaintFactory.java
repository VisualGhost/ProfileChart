package com.profilechart.profile;

import android.graphics.Paint;

public interface PaintFactory {

    Paint getPaint(int colorIndex);

    Paint getSelectedPaint(int colorIndex);

    Paint getPLInstrumentNamePaint(); // USDILS P/L

    Paint getIncPLValuePaint(); // 3.57

    Paint getDecPLValuePaint(); // -3.57

    Paint getPercentagePaint(); // 18.13%

    Paint getInstrumentNamePaint(); // USDILS

}
