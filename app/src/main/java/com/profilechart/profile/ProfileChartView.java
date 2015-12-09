package com.profilechart.profile;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.profilechart.R;

import java.util.List;

public class ProfileChartView extends View implements ProfileChart {

    private PaintFactory mPaintFactory;
    private AngleManager mAngleManager;
    private PortfolioDebug mDebug;

    private List<PortfolioBreakdown> mBreakdownList;

    private RectF mRectF;
    private float mWidth;
    private float mHeight;

    private float mScale;

    private float mArcWidth;
    private float mSelectedArcWidth;

    private float mArcRadius;
    private boolean mIsDebugMode;
    private float mCircleMargin;
    private float mPercentageBottomMargin;

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
            mArcWidth = mScale * array.getDimension(R.styleable.ProfileChart_strokeWidth, 0f);
            mSelectedArcWidth = mScale * array.getDimension(R.styleable.ProfileChart_strokeWidthSelected, 0f);
            mArcRadius = mScale * array.getDimension(R.styleable.ProfileChart_strokeRadius, 0f);
            mIsDebugMode = array.getBoolean(R.styleable.ProfileChart_profileChartDebugMode, false);
            mCircleMargin = mScale * array.getDimension(R.styleable.ProfileChart_circleMargin, 0f);
            mPercentageBottomMargin = mScale * array.getDimension(R.styleable.ProfileChart_percentageBottomMargin, 0f);
            initWidgetParams(context, mScale);
            initPaintFactory(mArcWidth, mSelectedArcWidth);
            initDebug();
        } finally {
            if (array != null) {
                array.recycle();
            }
        }
        //TODO delete
        setBackgroundColor(Color.GRAY);
    }

    private void initDebug() {
        mDebug = new PortfolioDebugImpl(mWidth, mArcRadius, mCircleMargin, mScale, mPercentageBottomMargin);
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
        mPaintFactory = new PaintFactoryImpl(getContext(), strokeWidth, selectedStrokeWidth);
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
    }

    private void drawStrokes(Canvas canvas) {
        if (mBreakdownList != null && mBreakdownList.size() > 0 && mAngleManager != null) {
            int index = 0;
            for (PortfolioBreakdown portfolioBreakdown : mBreakdownList) {
                if (portfolioBreakdown != null && portfolioBreakdown.isDrawable()) {
                    drawStroke(canvas, index++,
                            portfolioBreakdown.getInstrumentName(),
                            portfolioBreakdown.getAllocationPercentage());
                }
            }
            // todo Others
            drawOthers(canvas, index, "Others");
        }
    }

    private void drawStroke(Canvas canvas, int index, String instrumentName, String percentage) {
        //TODO remove if clause
        Paint paint = index == 2 ? mPaintFactory.getSelectedPaint(index) : mPaintFactory.getPaint(index);
        float startAngle = mAngleManager.getStartAngle(index);
        float sweepAngle = mAngleManager.getSweepAngle(index);
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, paint);
        if (mIsDebugMode) {
            mDebug.drawLine(canvas, startAngle, sweepAngle);
            mDebug.drawBox(canvas, startAngle, sweepAngle, instrumentName, percentage);
        }
    }


    private void drawOthers(Canvas canvas, int index, String instrumentName) {
        Paint paint = mPaintFactory.getPaint(index);
        float startAngle = mAngleManager.getStartAngle(index);
        float sweepAngle = mAngleManager.getTotalSweepAngle();
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, paint);
        if (mIsDebugMode) {
            mDebug.drawLine(canvas, startAngle, sweepAngle);
            //TODO ?%
            mDebug.drawBox(canvas, startAngle, sweepAngle, instrumentName, "?%");
        }
    }

    @Override
    public void draw(final List<PortfolioBreakdown> breakdownList) {
        if (breakdownList != null) {
            mBreakdownList = breakdownList;
            mAngleManager = new AngleManagerImpl(PieDirection.COUNTERCLOCKWISE, breakdownList);
            invalidate();
        }
    }
}
