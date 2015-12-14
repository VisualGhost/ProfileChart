package com.profilechart.profile;

import android.graphics.Paint;
import android.text.TextPaint;

public interface PaintFactory {

    Paint getPaint(int colorIndex);

    Paint getSelectedPaint(int colorIndex);

    Paint getPLPaint(); // USDILS P/L

    Paint getIncPLValuePaint(); // 3.57

    Paint getDecPLValuePaint(); // -3.57

    Paint getPercentagePaint(int index); // 18.13%

    Paint getInstrumentNamePaint(int index); // USDILS

    TextPaint getPLTextPaint();

}
