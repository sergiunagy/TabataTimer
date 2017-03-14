package com.moonapps.moonmoon.tabatatimer.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Moon Moon on 3/14/2017.
 */

public class NoPaddingTextView extends AppCompatTextView {

    private final Paint mPaint = new Paint();

    private final Rect mBounds = new Rect();


    public NoPaddingTextView(Context context) {
        super(context);
    }

    /**
     * constructor with attributes needed for preview
     * @param context
     * @param attrs
     */
    public NoPaddingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoPaddingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final String text = calculateTextParams();

        final int left = mBounds.left;
        final int bottom = mBounds.bottom;
        final int top = mBounds.top;

        mBounds.offset(-left, -top);

        mPaint.setAntiAlias(true);

        mPaint.setColor(getCurrentTextColor());
        Log.i("MEASURES " , "" +left + "/"+ mBounds.bottom+"/" + bottom);
        canvas.drawText(text, -left, mBounds.bottom-bottom, mPaint);

    }

    private String calculateTextParams() {
        final String text = getText().toString();
        final int textLength = text.length();
        mPaint.setTextSize(getTextSize());
        mPaint.getTextBounds(text, 0, textLength, mBounds);
        if (textLength == 0) {
            mBounds.right = mBounds.left;
        }
        return text;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calculateTextParams();
        setMeasuredDimension(mBounds.width()+30, -mBounds.top+5);
    }


}
