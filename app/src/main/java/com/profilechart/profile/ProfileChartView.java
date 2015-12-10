package com.profilechart.profile;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.profilechart.R;

import java.util.List;

public class ProfileChartView extends View implements ProfileChart {

    private PaintFactory mPaintFactory;
    private AngleManager mAngleManager;
    private PortfolioDebug mDebug;
    private PieTouchListener mPieTouchListener;
    private List<PortfolioBreakdown> mBreakdownList;
    private RectF mRectF;
    private boolean mIsDebugMode;
    private float mWidth;
    private float mHeight;
    private float mScale;
    private float mSelectedArcWidth;
    private float mArcRadius;
    private float mCircleMargin;
    private float mPercentageBottomMargin;
    private float mPLInstrumentNameTextSize;
    private float mPercentageTextSize;
    private float mInstrumentNameTextSize;
    private int mPLTextColor;
    private int mPLIncValueTextColor;
    private int mPLDecValueTextColor;
    private int mSelectedSectorIndex = -1;
    private String mPlString;

    public ProfileChartView(final Context context) {
        super(context);
        init(context, null);
    }

    public ProfileChartView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProfileChartView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = null;
        try {
            array = context.obtainStyledAttributes(attrs, R.styleable.ProfileChart);
            mScale = array.getFloat(R.styleable.ProfileChart_profileChartScale, 1f);
            float arcWidth = mScale * array.getDimension(R.styleable.ProfileChart_strokeWidth, 0f);
            mSelectedArcWidth = mScale * array.getDimension(R.styleable.ProfileChart_strokeWidthSelected, 0f);
            mArcRadius = mScale * array.getDimension(R.styleable.ProfileChart_strokeRadius, 0f);
            mIsDebugMode = array.getBoolean(R.styleable.ProfileChart_profileChartDebugMode, false);
            mCircleMargin = mScale * array.getDimension(R.styleable.ProfileChart_circleMargin, 0f);
            mPercentageBottomMargin = mScale * array.getDimension(R.styleable.ProfileChart_percentageBottomMargin, 0f);
            mPLInstrumentNameTextSize = mScale * array.getDimension(R.styleable.ProfileChart_pLInstrumentNameTextSize, 0f);
            mPLTextColor = array.getColor(R.styleable.ProfileChart_pLTextColor, Color.TRANSPARENT);
            mPLIncValueTextColor = array.getColor(R.styleable.ProfileChart_pLValueIncTextColor, Color.TRANSPARENT);
            mPLDecValueTextColor = array.getColor(R.styleable.ProfileChart_pLValueDecTextColor, Color.TRANSPARENT);
            mPercentageTextSize = mScale * array.getDimension(R.styleable.ProfileChart_percentageTextSize, 0f);
            mInstrumentNameTextSize = mScale * array.getDimension(R.styleable.ProfileChart_instrumentNameTextSize, 0f);
            mPlString = context.getString(R.string.pl);
            initWidgetParams(context, mScale);
            initPaintFactory(arcWidth, mSelectedArcWidth);
            initDebug();
            applyTouchListener();
        } finally {
            if (array != null) {
                array.recycle();
            }
        }
    }

    private void initDebug() {
        mDebug = new PortfolioDebugImpl(mWidth, mHeight, mArcRadius, mCircleMargin, mScale, mPercentageBottomMargin, mSelectedArcWidth);
    }

    private void applyTouchListener() {
        mPieTouchListener = new PieTouchListener(mWidth, mHeight);
        setOnTouchListener(mPieTouchListener);
    }

    private void initWidgetParams(Context context, float scale) {
        mWidth = scale * context.getResources().getDimension(R.dimen.profile_chart_width);
        mHeight = scale * context.getResources().getDimension(R.dimen.profile_chart_height);

        float shift = mSelectedArcWidth / 2;
        float left = -mArcRadius + shift;
        float top = -mArcRadius + shift;
        float right = mArcRadius - shift;
        float bottom = mArcRadius - shift;
        mRectF = new RectF(left, top, right, bottom);
    }

    private void initPaintFactory(float strokeWidth, float selectedStrokeWidth) {
        mPaintFactory = new PaintFactoryImpl.Builder()
                .setArcWidth(strokeWidth)
                .setSelectedArcWidth(selectedStrokeWidth)
                .setPLInstrumentNameTextSize(mPLInstrumentNameTextSize)
                .setPLTextColor(mPLTextColor)
                .setPLValueIncTextColor(mPLIncValueTextColor)
                .setPLValueDecTextColor(mPLDecValueTextColor)
                .setPercentageTextSize(mPercentageTextSize)
                .setInstrumentNameTextSize(mInstrumentNameTextSize)
                .build(getContext());
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int) mWidth, (int) mHeight);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mWidth / 2, mHeight / 2);
        drawStrokes(canvas);

        if (mIsDebugMode) {
            drawDebugElements(canvas);
        }
    }

    /**
     * Don't use DEBUG mode in production!
     */
    private void drawDebugElements(Canvas canvas) {
        mDebug.drawCircleAroundPie(canvas);
        mDebug.drawBoxInsideCircle(canvas);
        mDebug.drawXAxis(canvas);
        mDebug.drawYAxis(canvas);
    }

    //TODO wrong name of method
    private void drawStrokes(Canvas canvas) {
        if (mBreakdownList != null && mBreakdownList.size() > 0 && mAngleManager != null) {
            int index = 0;
            for (PortfolioBreakdown portfolioBreakdown : mBreakdownList) {
                if (portfolioBreakdown != null && portfolioBreakdown.isDrawable()) {
                    drawStroke(canvas, index,
                            portfolioBreakdown.getInstrumentName(),
                            portfolioBreakdown.getAllocationPercentage());
                    if (index == mSelectedSectorIndex) {
                        drawPLText(canvas, getPLInstrumentName(portfolioBreakdown.getInstrumentName()), portfolioBreakdown.getPLPercentage());
                    }
                    index++;
                }
            }
            // todo Others
            drawOthers(canvas, index, "Others");
        }
    }

    private void drawStroke(Canvas canvas, int index, String instrumentName, String percentage) {
        //TODO remove if clause
        Paint paint = index == mSelectedSectorIndex ? mPaintFactory.getSelectedPaint(index) : mPaintFactory.getPaint(index);
        float startAngle = mAngleManager.getStartAngle(index);
        float sweepAngle = mAngleManager.getSweepAngle(index);
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, paint);
        // TODO false
        if (false && mIsDebugMode) {
            mDebug.drawLine(canvas, startAngle, sweepAngle);
            mDebug.drawTextBox(canvas, startAngle, sweepAngle, instrumentName, percentage);
            mDebug.drawSector(canvas, startAngle, sweepAngle);
        }
    }

    private void drawPLText(Canvas canvas, String instrumentName, String plValue) {
//        Paint plInstrumentNamePaint = mPaintFactory.getPLInstrumentNamePaint();
//        Paint plValuePaint = getPLValuePaint(plValue);
//
//
//        float instrumentTextWidth = plInstrumentNamePaint.measureText(instrumentName);
//        float valueTextWidth = plValuePaint.measureText(plValue);
//        float width = instrumentTextWidth + valueTextWidth;
//        float x = -width / 2;
//        float y = PortfolioChartUtils.getHeight(plInstrumentNamePaint, instrumentName) / 2;
//        RectF rectF = PortfolioChartUtils.getRectFAroundCircle(mArcRadius, mSelectedArcWidth);
//        if (width > rectF.width()) {
//            String s = instrumentName + plValue;
//            CharSequence charSequence = TextUtils.ellipsize(s, new TextPaint(plInstrumentNamePaint), rectF.width(), TextUtils.TruncateAt.END);
//            s = charSequence.toString();
//            int lastIndex = s.lastIndexOf(" ");
//            String pl = lastIndex != -1 ? s.substring(lastIndex, s.length()) : "";
//            width = plInstrumentNamePaint.measureText(s);
//            canvas.drawText(s, -width / 2, y, plInstrumentNamePaint);
//            //canvas.drawText(pl, x + instrumentTextWidth, y, plValuePaint);
//        } else {
//            canvas.drawText(instrumentName, x, y, plInstrumentNamePaint);
//            canvas.drawText(plValue, x + instrumentTextWidth, y, plValuePaint);
//        }
        foo(canvas, instrumentName, plValue);
    }

    private void foo(Canvas canvas, String instrumentName, String plValue) {
        float width = getPLTextWidth(instrumentName, plValue);
        RectF allowRectF = PortfolioChartUtils.getRectFAroundCircle(mArcRadius, mSelectedArcWidth);
        if (width > allowRectF.width()) {
            float yShift = PortfolioChartUtils.getHeight(mPaintFactory.getPLInstrumentNamePaint(), instrumentName) / 2;
            drawClipPL(canvas, instrumentName, plValue, allowRectF.width(), yShift, width);
        } else {
            // TODO
        }
    }

    private void drawClipPL(Canvas canvas, String instrumentName, String plValue, float avail, float yShift, float totalWidth) {
        String plText = instrumentName + plValue;
        Toast.makeText(getContext(), avail+", "+getPLTextWidth(instrumentName, plValue), Toast.LENGTH_SHORT).show();
        CharSequence ellipsized = TextUtils.ellipsize(plText, new TextPaint(mPaintFactory.getPLInstrumentNamePaint()), avail, TextUtils.TruncateAt.END);
        String ellipsizedPLText = ellipsized.toString();
        int index = ellipsizedPLText.lastIndexOf(" ");
        plValue = index != -1 ? ellipsizedPLText.substring(index, ellipsizedPLText.length()) : "";
        float instrNameWidth = mPaintFactory.getPLInstrumentNamePaint().measureText(ellipsizedPLText);
        canvas.drawText(ellipsizedPLText, -instrNameWidth / 2, yShift, mPaintFactory.getPLInstrumentNamePaint());
        //canvas.drawText(plValue, -totalWidth / 2 + instrNameWidth, yShift, getPLValuePaint(plValue));
    }

    private String getPLInstrumentName(String instrumentName) {
        return instrumentName + " " + mPlString + " ";
    }

    private float getPLTextWidth(String instrumentName, String plValue) {
        return mPaintFactory.getPLInstrumentNamePaint().measureText(instrumentName) +
                getPLValuePaint(plValue).measureText(plValue);
    }

    private Paint getPLValuePaint(String plValue) {
        boolean isDecValue;
        try {
            float f = Float.valueOf(plValue);
            isDecValue = Float.compare(f, 0) < 0;
        } catch (NumberFormatException e) {
            isDecValue = false;
        }
        return isDecValue ? mPaintFactory.getDecPLValuePaint() : mPaintFactory.getIncPLValuePaint();
    }

    private void drawOthers(Canvas canvas, int index, String instrumentName) {
        Paint paint = index == mSelectedSectorIndex ? mPaintFactory.getSelectedPaint(index) : mPaintFactory.getPaint(index);
        float startAngle = mAngleManager.getStartAngle(index);
        float sweepAngle = mAngleManager.getTotalSweepAngle();
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, paint);
        // TODO false
        if (false && mIsDebugMode) {
            mDebug.drawLine(canvas, startAngle, sweepAngle);
            //TODO ?%
            mDebug.drawTextBox(canvas, startAngle, sweepAngle, instrumentName, "?%");
            mDebug.drawSector(canvas, startAngle, sweepAngle);
        }
    }

    @Override
    public void draw(final List<PortfolioBreakdown> breakdownList) {
        if (breakdownList != null) {
            mBreakdownList = breakdownList;
            mAngleManager = new AngleManagerImpl(PieDirection.COUNTERCLOCKWISE, breakdownList);
            if (mPieTouchListener != null) {
                mPieTouchListener.setBreakdownList(mBreakdownList);
                mPieTouchListener.setAngleManager(mAngleManager);
            }
            invalidate();
        }
    }

    @Override
    public void selectSector(final int index) {
        mSelectedSectorIndex = index;
        invalidate();
    }
}
