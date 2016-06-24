package com.louisgeek.louisprogessbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * Created by louisgeek on 2016/6/24.
 */
public class MyProgessBar extends ProgressBar{

    private static final int DEFAULT_TEXT_SIZE = 12;//文字大小
    private static final int DEFAULT_TEXT_COLOR = 0xffFC00D1;//文字颜色
    private static final int DEFAULT_TEXT_OFFSET = 10;//文字两边的间距的总和
    private static final int DEFAULT_TEXT_DISTANCE = 12;//文字上下的间距总和
    private static final int DEFAULT_TEXTBORDER_SIZE = 2;//边框宽度
    private static final int DEFAULT_TEXTBORDER_COLOR=0xff14BAF1;

    private static final int DEFAULT_COLOR_RIGHT = 0xFFD3D6DA;//文字右侧进度条颜色
    private static final int DEFAULT_HEIGHT_RIGHT  = 4;//文字右侧进度条高度
    private static final int DEFAULT_COLOR_LEFT = DEFAULT_TEXT_COLOR;//文字左侧进度条颜色
    private static final int DEFAULT_HEIGHT_LEFT = 4;//文字右侧进度条高度


    private int mLeftHeight=this.dp2px(DEFAULT_HEIGHT_LEFT);
    private int mLeftColor=DEFAULT_COLOR_LEFT;
    private int mTextColor=DEFAULT_TEXT_COLOR;
    private int mTextSize=this.dp2px(DEFAULT_TEXT_SIZE);
    private int mTextOffset=this.dp2px(DEFAULT_TEXT_OFFSET);
    private int mTextDistance=this.dp2px(DEFAULT_TEXT_DISTANCE);
    private int mTextBorderColor=DEFAULT_TEXTBORDER_COLOR;
    private int mTextBorderSize=this.dp2px(DEFAULT_TEXTBORDER_SIZE);
    private int mRightHeight=this.dp2px(DEFAULT_HEIGHT_RIGHT);
    private int mRightColor=DEFAULT_COLOR_RIGHT;
    private boolean mTextBorderIsShow=true;
    private String mText;
    private  int mMaxContentHeight;
    private int mRealWidth;
    private Paint mPaint;
    public MyProgessBar(Context context) {
        this(context, null);
    }

    public MyProgessBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyProgessBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.MyProgessBarAttrs);
        /**
         * getDimension和getDimensionPixelOffset的功能差不多,都是获取某个dimen的值,如果是dp或sp的单位,将其乘以density,如果是px,则不乘;
         * 两个函数的区别是一个返回float,一个返回int.
         * getDimensionPixelSize则不管写的是dp还是sp还是px,都会乘以denstiy.
         */
        mLeftColor= ta.getColor(R.styleable.MyProgessBarAttrs_left_color,mLeftColor);
        mLeftHeight= ta.getDimensionPixelOffset(R.styleable.MyProgessBarAttrs_left_height,mLeftHeight);

        mTextSize= ta.getDimensionPixelOffset(R.styleable.MyProgessBarAttrs_text_size,mTextSize);
        mTextColor= ta.getColor(R.styleable.MyProgessBarAttrs_text_color,mTextColor);
        mTextOffset= ta.getDimensionPixelOffset(R.styleable.MyProgessBarAttrs_text_offset,mTextOffset);
        mTextDistance= ta.getDimensionPixelOffset(R.styleable.MyProgessBarAttrs_text_distance,mTextDistance);

        mTextBorderSize= ta.getDimensionPixelOffset(R.styleable.MyProgessBarAttrs_text_border_size,mTextBorderSize);
        mTextBorderColor= ta.getColor(R.styleable.MyProgessBarAttrs_text_border_color,mTextBorderColor);
        mTextBorderIsShow= ta.getBoolean(R.styleable.MyProgessBarAttrs_text_border_isshow,mTextBorderIsShow);

        mRightHeight= ta.getDimensionPixelOffset(R.styleable.MyProgessBarAttrs_right_height,mRightHeight);
        mRightColor= ta.getColor(R.styleable.MyProgessBarAttrs_right_color,mRightColor);

        ta.recycle();

        init(context);
    }

    public void  init(Context context){
        /**
         * 初始化时  先设置文字大小 可以用于计算文字高度和测量宽度
         */
        mPaint=new Paint();
        mPaint.setTextSize(mTextSize);

        //mTextBorderSize=mTextBorderIsShow?mTextBorderSize:0;
    }

    /**
     * onMeasure传入的两个参数是由上一层控件传入的大小，有多种情况，
     * 重写该方法时需要对计算控件的实际大小，然后调用setMeasuredDimension(int, int)设置实际大小。
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       // super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //int  width=dealWidth(widthMeasureSpec);//因为使用进度条肯定要设置成match_parent或者指定宽度，所以不需要测量
        int  width=MeasureSpec.getSize(widthMeasureSpec);//直接返回精确值
        int  height=dealHeight(heightMeasureSpec);
        setMeasuredDimension(width,height);

        mRealWidth=width-this.getPaddingLeft()-this.getPaddingRight();
    }

  /*  private int dealWidth(int widthMeasureSpec) {
        int widthResult=0;
        int specMode=MeasureSpec.getMode(widthMeasureSpec);
        int specSize=MeasureSpec.getSize(widthMeasureSpec);

        int textWidth= (int) mTextPaint.measureText(mText);
        int not_Exactly_Size= this.getPaddingLeft()+this.getPaddingRight()+textWidth;
        switch (specMode) {
            *//**
             * MeasureSpec.EXACTLY是精确尺寸，
             * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid:layout_width="50dip"，
             * 或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
             *//*
            case MeasureSpec.EXACTLY:
                widthResult = specSize;
                break;
            *//**
             * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，
             * 一般都是父控件是AdapterView，通过measure方法传入的模式。
             *//*
            case MeasureSpec.UNSPECIFIED:
                widthResult=not_Exactly_Size;
                break;
            *//**
             * MeasureSpec.AT_MOST是最大尺寸，当控件的layout_width或layout_height指定为WRAP_CONTENT时，
             * 控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可。
             * 因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
             *//*
            case MeasureSpec.AT_MOST:
                widthResult = Math.min(not_Exactly_Size, specSize);
                break;

        }

        return widthResult;

    }*/


    private int dealHeight(int heightMeasureSpec) {
        int heightResult=0;
        int specMode=MeasureSpec.getMode(heightMeasureSpec);
        int specSize=MeasureSpec.getSize(heightMeasureSpec);

        /**
         * ascent是指从一个字的基线(baseline)到最顶部的距离，descent是指一个字的基线到最底部的距离
         * descent有正负数
         */
        int textHeight= (int)(mPaint.ascent()+Math.abs(mPaint.descent()));
         mMaxContentHeight=Math.max(Math.max(mLeftHeight,mRightHeight),(Math.abs(textHeight)+mTextDistance+mTextBorderSize*2));
        int not_Exactly_Size= this.getPaddingTop()+this.getPaddingBottom()+mMaxContentHeight;

        switch (specMode) {
            /**
             * MeasureSpec.EXACTLY是精确尺寸，
             * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid:layout_width="50dip"，
             * 或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
             */
            case MeasureSpec.EXACTLY:
                heightResult = specSize;
                break;
            /**
             * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，
             * 一般都是父控件是AdapterView，通过measure方法传入的模式。
             */
              case MeasureSpec.UNSPECIFIED:
                  heightResult=not_Exactly_Size;
                             break;
            /**
             * MeasureSpec.AT_MOST是最大尺寸，当控件的layout_width或layout_height指定为WRAP_CONTENT时，
             * 控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可。
             * 因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
             */
            case MeasureSpec.AT_MOST:
                heightResult = Math.min(not_Exactly_Size, specSize);
                             break;

                     }

        return heightResult;
    }

    /**
     * 设计的是  进度条总和不包含文字区域和文字间距区域
     * @param canvas
     */
    @Override
    protected synchronized void onDraw(Canvas canvas) {
       // super.onDraw(canvas);
      //  canvas.drawText(mText,0,0,mTextPaint);
        canvas.save();
        canvas.translate(this.getPaddingLeft(),this.getHeight()/2);//移动到横线的paddingleft和自身高度的一半

        boolean needDrawRightProgress=true;

        mText =this.getProgress() + "%";
        float textWidth= mPaint.measureText(mText);

        float allProgressWidth=mRealWidth-textWidth-mTextOffset-mTextBorderSize*2;

        float bfb=this.getProgress() * 1.0f/this.getMax();
        float leftProgressEndX=bfb*allProgressWidth;
       // float leftProgressEndX =leftWidth-mTextOffset/2;//左边进度条宽度

        if (leftProgressEndX>allProgressWidth)
        {
            leftProgressEndX=allProgressWidth;
            needDrawRightProgress = false;
        }
     /**
         * 画左边进度条
         */
        if (leftProgressEndX>0){
            mPaint.setColor(mLeftColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(mLeftHeight);
            canvas.drawLine(0, 0, leftProgressEndX, 0, mPaint);//画左边进度条
        }

        if (mTextBorderIsShow) {
            /**
             * 文字背景
             */
            //mPaint.setAntiAlias(true);
            mPaint.setColor(mTextBorderColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mTextBorderSize);
            Path path = new Path();                       //Path对象
            /**
             * +mTextBorderSize/2  边框的中点
             */
            int middleTextBorderSize = mTextBorderSize / 2;
            //起点
            path.moveTo(leftProgressEndX, -(mMaxContentHeight / 2 - middleTextBorderSize));
            //上  see /screenshots/pic01
            path.lineTo(leftProgressEndX + textWidth + mTextOffset + mTextBorderSize * 2, -(mMaxContentHeight / 2 - middleTextBorderSize));//middleTextBorderSize*2 左边后半个和右边前半个


            path.moveTo(leftProgressEndX, mMaxContentHeight / 2 - middleTextBorderSize);
            //下 see /screenshots/pic02
            path.lineTo(leftProgressEndX + textWidth + mTextOffset + mTextBorderSize * 2, mMaxContentHeight / 2 - middleTextBorderSize);


            path.moveTo(leftProgressEndX + middleTextBorderSize, -(mMaxContentHeight / 2 - mTextBorderSize));
            //左 see /screenshots/pic03
            path.lineTo(leftProgressEndX + middleTextBorderSize, mMaxContentHeight / 2 - mTextBorderSize);

            path.moveTo(leftProgressEndX + textWidth + mTextOffset + mTextBorderSize * 2 - middleTextBorderSize, -(mMaxContentHeight / 2 - mTextBorderSize));
            //右 see /screenshots/pic04
            path.lineTo(leftProgressEndX + textWidth + mTextOffset + mTextBorderSize * 2 - middleTextBorderSize, mMaxContentHeight / 2 - mTextBorderSize);

            canvas.drawPath(path, mPaint);
        }
        /**
         * 画文字
         */
        mPaint.setColor(mTextColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(mTextSize);
        int textHeight= (int)(mPaint.ascent()+Math.abs(mPaint.descent()));
        int y = Math.abs(textHeight)/2;//有些文章int y = (int) (-(mPaint.descent() + mPaint.ascent())/2);
        canvas.drawText(mText, leftProgressEndX+mTextOffset/2+mTextBorderSize, y, mPaint);


       /**
         * 画右边进度条*/


        if (needDrawRightProgress){
            float rightProgressStartX = leftProgressEndX + mTextOffset +mTextBorderSize*2+ textWidth;
            mPaint.setColor(mRightColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(mRightHeight);
            canvas.drawLine(rightProgressStartX, 0, mRealWidth, 0, mPaint);
        }
        canvas.restore();
    }

   public int dp2px(int dpValue){
       int px= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpValue,getResources().getDisplayMetrics());
        return px;
    }
}
