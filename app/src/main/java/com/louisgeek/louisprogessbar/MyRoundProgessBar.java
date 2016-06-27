package com.louisgeek.louisprogessbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Created by louisgeek on 2016/6/27.
 */
public class MyRoundProgessBar extends MyProgessBar{
    private static final int DEFAULT_RADIUS =30;//半径
    private int mRadius =this.dp2px(DEFAULT_RADIUS);
    private  int mSize_w_h;
    private int  mMaxHeight4PaintWidth;
    private RectF mRectF;
    public MyRoundProgessBar(Context context) {
        this(context,null);
    }

    public MyRoundProgessBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyRoundProgessBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MyRoundProgressBarAttrs);
        mRadius = (int) ta.getDimension(R.styleable.MyRoundProgressBarAttrs_radius, mRadius);
        ta.recycle();

        initRound(context);
    }

    private void initRound(Context context) {

    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 根据用户设置的半径加上其他的一些因素形成的宽和高来计算出在程序中要画的图形的宽和高
         */
        mSize_w_h=mRadius*2+this.getPaddingLeft()+this.getPaddingRight();
        int width = resolveSize(mSize_w_h, widthMeasureSpec);// resolveSize 这个方法其实和之前在条形进度条中自己写的是一样的
        int height = resolveSize(mSize_w_h, heightMeasureSpec);
        //从两者中选出一个小的，作为宽度  其实就是直径
        int realSize = Math.min(width, height);
        setMeasuredDimension(realSize, realSize);

        int maxRoundHeight=Math.max(mLeftHeight,mRightHeight);//画笔要绘制的圆环最大高度
        mMaxHeight4PaintWidth=maxRoundHeight;
        //直径减去左右边距，减去画笔宽度（画笔在直径上有两个/边，但是直径是在画笔的中间的，也就是画笔画出来的线其实是横跨在直径的那条线上的），最后除 2，算出真实的半径
        mRadius = (realSize - getPaddingLeft() - getPaddingRight() - (mMaxHeight4PaintWidth/2)*2) / 2;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        //计算文字相关高度以及宽度
        String text = this.getProgress() + "%";
        int textWidth = (int) mPaint.measureText(text);
        int textHeight = (int) ((mPaint.descent() + mPaint.ascent()));

        //计算要画出的角度，度数的百分比乘以 360
        float degree= this.getProgress() * 1.0f / getMax() * 360;

        mRectF= new RectF(0, 0, mRadius * 2, mRadius * 2);

        canvas.save();
        //移动坐标到左上角   左上角的位置为除去 padding 和画笔宽度的一半
        canvas.translate(this.getPaddingLeft() + mMaxHeight4PaintWidth / 2, getPaddingTop() + mMaxHeight4PaintWidth / 2);

        /** 画整个底环*/
        mPaint.setColor(mLeftColor);
        mPaint.setStrokeWidth(mLeftHeight);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);


        /** 画上面的环*/
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mRightColor);
        mPaint.setStrokeWidth(mRightHeight);
        //画圆  第一个参数是圆的外接正方形，第二三个参数分别为其实度数以及要画的度数，第四个参数表示是否过圆心
        canvas.drawArc(mRectF, 0, degree, false, mPaint);


        /**画出文字*/
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setStyle(Paint.Style.FILL);
        //第一个参数是文字，第二、三个参数是画文字的左上角的坐标（mRadius 是圆心, 圆心的左上角的横坐标就是所有文字的宽度/2，高度类似）
        canvas.drawText(text, mRadius - textWidth / 2, mRadius - textHeight / 2, mPaint);

        canvas.restore();
    }
}
