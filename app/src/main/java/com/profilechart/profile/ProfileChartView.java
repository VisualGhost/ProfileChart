package com.profilechart.profile;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

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
    private String mOthersString;

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
            mOthersString = context.getString(R.string.others);
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
        drawChartElements(canvas);

        if (mIsDebugMode) {
            drawDebugElements(canvas);
        }
    }

    /**
     * Don't use DEBUG mode in production!
     */
    private void drawDebugElements(Canvas canvas) {
        mDebug.drawCircleAroundPie(canvas);
        mDebug.drawSquareInsideCircle(canvas);
        mDebug.drawXAxis(canvas);
        mDebug.drawYAxis(canvas);
    }

    private void drawChartElements(Canvas canvas) {
        if (mBreakdownList != null && mBreakdownList.size() > 0 && mAngleManager != null) {
            int index = 0;
            for (PortfolioBreakdown breakdown : mBreakdownList) {
                if (breakdown != null && breakdown.isDrawable()) {
                    drawInstrumentSector(canvas, index, breakdown);
                    index++;
                }
            }
            if (Float.compare(mAngleManager.getTotalSweepAngle(), 0) != 0) {
                drawOthers(canvas, index);
            }
        }
    }

    private void drawInstrumentSector(Canvas canvas, int index, PortfolioBreakdown breakdown) {
        drawStroke(canvas, index, mAngleManager.getStartAngle(index), mAngleManager.getSweepAngle(index));
        if (index == mSelectedSectorIndex) {
            drawPLTextInsideCircle(canvas, getPLInstrumentName(breakdown.getInstrumentName()), breakdown.getPLPercentage());
        }
        if (mIsDebugMode) {
            drawDebugElements(canvas, breakdown.getInstrumentName(), breakdown.getAllocationPercentage(), mAngleManager.getStartAngle(index), mAngleManager.getSweepAngle(index));
        }
    }

    private void drawStroke(Canvas canvas, int index, float startAngle, float sweepAngle) {
        Paint paint;
        if (index == mSelectedSectorIndex) {
            paint = mPaintFactory.getSelectedPaint(index);
        } else {
            paint = mPaintFactory.getPaint(index);
        }
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, paint);
    }

    private void drawOthers(Canvas canvas, int index) {
        drawStroke(canvas, index, mAngleManager.getStartAngle(index), mAngleManager.getTotalSweepAngle());
        if (mIsDebugMode) {
            float sweepAngle = mAngleManager.getTotalSweepAngle();
            drawDebugElements(canvas, mOthersString, PortfolioChartUtils.angleToPercentage(Math.abs(sweepAngle)), mAngleManager.getStartAngle(index), sweepAngle);
        }
    }

    private void drawDebugElements(Canvas canvas, String instrumentName, String percentage, float startAngle, float sweepAngle) {
        mDebug.drawCenterOfSector(canvas, startAngle, sweepAngle);
        mDebug.drawTextBox(canvas, startAngle, sweepAngle, instrumentName, percentage);
        mDebug.drawSector(canvas, startAngle, sweepAngle);
    }

    private void drawPLTextInsideCircle(Canvas canvas, String instrumentName, String plValue) {
        float width = getPLTextWidth(instrumentName, plValue);
        RectF allowRectF = PortfolioChartUtils.getRectFAroundCircle(mArcRadius, mSelectedArcWidth);
        float xShift = -width / 2;
        float yShift = PortfolioChartUtils.getHeight(mPaintFactory.getPLPaint(), instrumentName) / 2;
        if (width > allowRectF.width()) {
            drawClipPL(canvas, instrumentName, plValue, allowRectF.width(), yShift);
        } else {
            canvas.drawText(instrumentName, xShift, yShift, mPaintFactory.getPLPaint());
            float instrumentNameWidth = PortfolioChartUtils.getWidth(mPaintFactory.getPLPaint(), instrumentName);
            canvas.drawText(plValue, xShift + instrumentNameWidth, yShift, getPLValuePaint(plValue));
        }
    }

    private void drawClipPL(Canvas canvas, final String instrumentName, final String pLValue, float avail, float yShift) {
        String ellipsizedPLText = getEllipsizedPLText(instrumentName, pLValue, avail);
        int index = getIndexOfPLValue(ellipsizedPLText);
        float ellipsizedPLTextWidth = PortfolioChartUtils.getWidth(mPaintFactory.getPLPaint(), ellipsizedPLText);
        String clipPLValue = getClipPLValue(index, ellipsizedPLText);
        ellipsizedPLText = getEllipsizedPLTextWithoutPLValue(ellipsizedPLText, index);
        float xShift = -ellipsizedPLTextWidth / 2;
        canvas.drawText(ellipsizedPLText, xShift, yShift, mPaintFactory.getPLPaint());
        if (!TextUtils.isEmpty(clipPLValue)) {
            canvas.drawText(clipPLValue, xShift + PortfolioChartUtils.getWidth(mPaintFactory.getPLPaint(), ellipsizedPLText), yShift, getPLValuePaint(pLValue));
        }
    }

    private String getEllipsizedPLText(final String instrumentName, final String pLValue, float avail) {
        String plText = instrumentName + pLValue;
        CharSequence ellipsized = TextUtils.ellipsize(plText, mPaintFactory.getPLTextPaint(), avail, TextUtils.TruncateAt.END);
        return ellipsized.toString();
    }

    private int getIndexOfPLValue(String ellipsizedPLText) {
        return ellipsizedPLText.lastIndexOf(mPlString);
    }

    private String getClipPLValue(int indexOfPLValue, String ellipsizedPLText) {
        if (indexOfPLValue != -1) {
            indexOfPLValue += mPlString.length() + 1;
            return ellipsizedPLText.substring(indexOfPLValue, ellipsizedPLText.length());
        }
        return "";
    }

    private String getEllipsizedPLTextWithoutPLValue(String ellipsizedPLText, int indexOfPLValue) {
        if (indexOfPLValue != -1) {
            indexOfPLValue += mPlString.length() + 1;
            return ellipsizedPLText.substring(0, indexOfPLValue);
        }
        return ellipsizedPLText;
    }

    private String getPLInstrumentName(String instrumentName) {
        return instrumentName + " " + mPlString + " ";
    }

    private float getPLTextWidth(String instrumentName, String plValue) {
        return mPaintFactory.getPLPaint().measureText(instrumentName) +
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
