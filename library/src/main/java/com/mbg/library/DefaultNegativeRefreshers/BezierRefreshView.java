package com.mbg.library.DefaultNegativeRefreshers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.mbg.library.R;

/**
 * Created by Administrator on 2017/3/25.
 */

public class BezierRefreshView extends View {

    private Path mPath;
    private Paint mPaint;

    private int mDefalutRectWidth;
    private int mOvalWidth;
    private int mViewWidth;
    private int color;

    public BezierRefreshView(Context context) {
        this(context,null);
    }

    public BezierRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BezierRefreshView);
        this.mDefalutRectWidth = (int) typedArray.getDimension(R.styleable.BezierRefreshView_Rect_Width, 0);
        this.mOvalWidth= (int) typedArray.getDimension(R.styleable.BezierRefreshView_Oval_Width,0);
        this.mViewWidth=mDefalutRectWidth;
        this.color=(int)typedArray.getColor(R.styleable.BezierRefreshView_Back_Color, Color.GRAY);
        typedArray.recycle();
        init();
    }

    public int getDefalutRectWidth(){
        return mDefalutRectWidth;
    }

    private void init(){
        mPath=new Path();
        mPaint=new Paint();
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setDither(true);//防抖动
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        caculate();
        canvas.drawPath(mPath,mPaint);
    }


    private int mDrawOvalWidth=-1;
    /**
     * (viewWidth-rectWidth)/(totalWidth-rectwidth) *ovalWidth
     * @return
     */
    private int getDrawOvalLeftX(){
        double trueDrawWidth=mViewWidth-mDefalutRectWidth;
        double totalDrawWidth=(getWidth() -mDefalutRectWidth);
        double rate=trueDrawWidth/totalDrawWidth;
        mDrawOvalWidth=(int) (rate *mOvalWidth);
        return mDrawOvalWidth;
    }

    public int getDrawOvalWidth(){
        return mDrawOvalWidth < 0 ? 0: mDrawOvalWidth;
    }

    public int getDrawRight(){
        return getLeft()+mViewWidth;
    }

    private void caculate(){

        mViewWidth= mViewWidth > getWidth() ? getWidth() :mViewWidth;//如果设置的宽度大于实际尺寸则设置为实际尺寸

        int left=getLeft();
        int right=getDrawRight();
        int top=getTop();
        int bottom=getBottom();
        mPath.reset();
        if(mViewWidth <= mDefalutRectWidth){
            mPath.moveTo(left,top);
            mPath.lineTo(left,bottom);
            mPath.lineTo(right,bottom);
            mPath.lineTo(right,top);
            mPath.lineTo(left,top);
        }
        else{
            int trueOvalWidth=getDrawOvalLeftX();
            int startLeft=right-mDefalutRectWidth-trueOvalWidth;
            /*int startLeft=left;
            if(mViewWidth >= (mOvalWidth+mDefalutRectWidth)){
                startLeft=right-mOvalWidth-mDefalutRectWidth;
            }*/
            int recLeft=left+mViewWidth-mDefalutRectWidth;
            int ovalCenter=(top+bottom)/2;
            int controlheight=(top-bottom)/4;
            mPath.moveTo(recLeft,top);
            mPath.quadTo(startLeft,top-controlheight,startLeft,ovalCenter);
            mPath.quadTo(startLeft,bottom+controlheight,recLeft,bottom);
            mPath.lineTo(right,bottom);
            mPath.lineTo(right,top);
            mPath.lineTo(startLeft,top);
        }
    }

    public void setWidth(int width){
        if(0 > width){
            return;
        }
        mViewWidth=width;
        invalidate();
    }
}
