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

import java.util.ArrayList;
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
    private List<InfoHolder> mTextInCircleInfoHolders;
    private List<InfoHolder> mLabelInfoHolders;

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
            mTextInCircleInfoHolders = new ArrayList<>();
            mLabelInfoHolders = new ArrayList<>();
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
        mDebug = new PortfolioDebugImpl(mWidth, mHeight, mArcRadius, mCircleMargin, mScale, mSelectedArcWidth);
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
        drawTextInsideCircle(canvas, index);
        drawLabel(canvas, index);
        if (mIsDebugMode) {
            drawDebugElements(canvas, mAngleManager.getStartAngle(index), mAngleManager.getSweepAngle(index));
        }
    }

    private void drawLabel(Canvas canvas, int index) {
        if (mLabelInfoHolders != null) {
            InfoHolder infoHolder = mLabelInfoHolders.size() > index ? mLabelInfoHolders.get(index) : null;
            if (infoHolder != null) {
                canvas.drawText(infoHolder.text1, infoHolder.xText1, infoHolder.yText1, mPaintFactory.getInstrumentNamePaint(index));
                canvas.drawText(infoHolder.text2, infoHolder.xText2, infoHolder.yText2, mPaintFactory.getInstrumentNamePaint(index));
            }
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

    private void drawTextInsideCircle(Canvas canvas, int index) {
        if (index == mSelectedSectorIndex) {
            InfoHolder drawable = mTextInCircleInfoHolders.size() > index ? mTextInCircleInfoHolders.get(index) : null;
            if (drawable != null) {
                canvas.drawText(drawable.text1, drawable.xText1, drawable.yText1, mPaintFactory.getPLPaint());
                if (!TextUtils.isEmpty(drawable.text2)) {
                    canvas.drawText(drawable.text2, drawable.xText2, drawable.yText2, getPLValuePaint(drawable.text2));
                }
            }
        }
    }

    private void drawOthers(Canvas canvas, int index) {
        drawStroke(canvas, index, mAngleManager.getStartAngle(index), mAngleManager.getTotalSweepAngle());
        float sweepAngle = mAngleManager.getTotalSweepAngle();
        drawLabel(canvas, index);
        if (mIsDebugMode) {
            drawDebugElements(canvas, mAngleManager.getStartAngle(index), sweepAngle);
        }
    }

    private void drawDebugElements(Canvas canvas, float startAngle, float sweepAngle) {
        mDebug.drawCenterOfSector(canvas, startAngle, sweepAngle);
        mDebug.drawSectorLine(canvas, startAngle);
    }

    /**
     * Holds the information to draw on Canvas.
     */
    private static class InfoHolder {
        String text1;
        float xText1;
        float yText1;

        String text2;
        float xText2;
        float yText2;
    }

    private void initInstrumentPlInfoHolders(String instrumentName, String plValue) {
        float width = getPLTextWidth(instrumentName, plValue);
        RectF allowRectF = Utils.getRectFAroundCircle(mArcRadius, mSelectedArcWidth);
        float xShift = -width / 2;
        float yShift = Utils.getHeight(mPaintFactory.getPLPaint(), instrumentName) / 2;
        if (width > allowRectF.width()) {
            initClipDrawable(instrumentName, plValue, allowRectF.width(), yShift);
        } else {
            float instrumentNameWidth = Utils.getWidth(mPaintFactory.getPLPaint(), instrumentName);
            InfoHolder drawable = new InfoHolder();
            drawable.text1 = instrumentName;
            drawable.text2 = plValue;
            drawable.xText1 = xShift;
            drawable.yText1 = yShift;
            drawable.xText2 = xShift + instrumentNameWidth;
            drawable.yText2 = yShift;
            mTextInCircleInfoHolders.add(drawable);
        }
    }

    private void initClipDrawable(final String instrumentName, final String pLValue, float avail, float yShift) {
        String ellipsizedPLText = getEllipsizedPLText(instrumentName, pLValue, avail);
        int indexInString = getIndexOfPLValue(ellipsizedPLText);
        float ellipsizedPLTextWidth = Utils.getWidth(mPaintFactory.getPLPaint(), ellipsizedPLText);
        String clipPLValue = getClipPLValue(indexInString, ellipsizedPLText);
        ellipsizedPLText = getEllipsizedPLTextWithoutPLValue(ellipsizedPLText, indexInString);
        float xShift = -ellipsizedPLTextWidth / 2;
        InfoHolder drawable = new InfoHolder();
        drawable.text1 = ellipsizedPLText;
        drawable.xText1 = xShift;
        drawable.yText1 = yShift;
        if (!TextUtils.isEmpty(clipPLValue)) {
            drawable.text2 = clipPLValue;
            drawable.xText2 = xShift + Utils.getWidth(mPaintFactory.getPLPaint(), ellipsizedPLText);
            drawable.yText2 = yShift;
        }
        mTextInCircleInfoHolders.add(drawable);
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

    public void obtainLabelInfo(final float startAngle, final float sweetAngle,
                                final String instrumentName, final String percentage, Paint paint) {

        float commonWidth = Math.max(Utils.getWidth(paint, instrumentName),
                Utils.getWidth(paint, percentage));
        float instrumentHeight = Utils.getHeight(paint, instrumentName);
        float percentageHeight = Utils.getHeight(paint, percentage);
        float commonHeight = instrumentHeight + percentageHeight + mPercentageBottomMargin;
        RectF textBox = getTextRectF(startAngle, sweetAngle, commonWidth, commonHeight);
        float angle = getAngle(startAngle, sweetAngle);
        float halfOfDiagonal = getHalfOfDiagonal(textBox, angle);
        float distanceToCenterOfTextBox = getDistanceToCenterOfRectF(textBox);
        textBox = getShiftedRectF(textBox, startAngle, sweetAngle, halfOfDiagonal + distanceToCenterOfTextBox);
        float xPercentage = textBox.left;
        float yPercentage = textBox.top + percentageHeight;
        float xShift = getXShift(textBox, angle, getRadius());

        InfoHolder drawable = new InfoHolder();
        drawable.text1 = percentage;
        drawable.xText1 = xPercentage + xShift;
        drawable.yText1 = yPercentage;

        yPercentage += instrumentHeight + mPercentageBottomMargin;

        drawable.text2 = instrumentName;
        drawable.xText2 = xPercentage + xShift;
        drawable.yText2 = yPercentage;
        mLabelInfoHolders.add(drawable);
    }

    private RectF getTextRectF(final float startAngle,
                               final float sweetAngle,
                               final float width,
                               final float height) {
        return Utils.getTextRectF(getRadius(), startAngle, sweetAngle, width, height);
    }

    private float getRadius() {
        return mArcRadius + mCircleMargin;
    }

    private float getHalfOfDiagonal(final RectF rectF, float angle) {
        float diagonal;
        if (Double.compare(Math.sin(angle * Math.PI / 180), 0) != 0) {
            diagonal = (float) (Math.abs(rectF.height() / 2 / Math.sin(angle * Math.PI / 180)));
        } else {
            diagonal = rectF.height() / 2;
        }
        return Math.min(diagonal, rectF.height() / 2);
    }

    private float getAngle(final float startAngle, final float sweetAngle) {
        return startAngle + sweetAngle / 2;
    }

    private float getDistanceToCenterOfRectF(final RectF rectF) {
        float cX = rectF.centerX();
        float cY = rectF.centerY();
        return (float) Math.sqrt(cX * cX + cY * cY);
    }

    private RectF getShiftedRectF(final RectF rectF, final float startAngle, final float sweetAngle, float distance) {
        return Utils.getTextRectF(distance, startAngle, sweetAngle, rectF.width(), rectF.height());
    }

    private float getXCoordinateOfIntersectionBetweenCircleAndRectF(final RectF textBox, final float angle, float radius) {
        float y;
        if (-angle >= 180 && -angle <= 360) {
            y = textBox.top;
        } else {
            y = textBox.bottom;
        }
        return (float) (Math.sqrt(radius * radius - y * y));
    }

    private float getXShift(final RectF textBox, final float angle, float radius) {
        float xIntersection = getXCoordinateOfIntersectionBetweenCircleAndRectF(textBox, angle, radius);

        float xShift;
        if (-angle >= 90 && -angle <= 270) {
            xIntersection = -xIntersection;
            xShift = xIntersection - textBox.right;
        } else {
            xShift = xIntersection - textBox.left;
        }

        if (-angle > 85 && -angle < 105) {
            xShift = 0;
        }

        if (-angle > 265 && -angle < 275) {
            xShift = 0;
        }
        return xShift;
    }

    @Override
    public void draw(final List<PortfolioBreakdown> breakdownList) {
        if (breakdownList != null) {
            mBreakdownList = breakdownList;
            mAngleManager = new AngleManagerImpl(PieDirection.COUNTERCLOCKWISE, breakdownList);
            obtainInfoHolders(breakdownList, mAngleManager);
            if (mPieTouchListener != null) {
                mPieTouchListener.setBreakdownList(mBreakdownList);
                mPieTouchListener.setAngleManager(mAngleManager);
            }
            invalidate();
        }
    }

    private void obtainInfoHolders(final List<PortfolioBreakdown> breakdownList, AngleManager angleManager) {
        if (mTextInCircleInfoHolders != null) {
            mTextInCircleInfoHolders.clear();
            int index = 0;
            for (PortfolioBreakdown breakdown : breakdownList) {
                if (breakdown.isDrawable()) {
                    initInstrumentPlInfoHolders(getPLInstrumentName(breakdown.getInstrumentName()), breakdown.getPLPercentage());
                    float sweepAngle = mAngleManager.getSweepAngle(index);
                    obtainLabelInfo(angleManager.getStartAngle(index), sweepAngle, breakdown.getInstrumentName(), breakdown.getAllocationPercentage(), mPaintFactory.getInstrumentNamePaint(index));
                    index++;
                }
            }
            if (Float.compare(mAngleManager.getTotalSweepAngle(), 0) != 0) {
                float sweepAngle = mAngleManager.getTotalSweepAngle();
                obtainLabelInfo(angleManager.getStartAngle(index), sweepAngle, mOthersString, Utils.angleToPercentage(Math.abs(sweepAngle)), mPaintFactory.getInstrumentNamePaint(index));
            }
        }
    }

    @Override
    public void selectSector(final int index) {
        mSelectedSectorIndex = index;
        invalidate();
    }
}
