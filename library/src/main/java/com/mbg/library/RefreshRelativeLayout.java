package com.mbg.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mbg.library.DefaultNegativeRefreshers.HorizontalLoadMore;
import com.mbg.library.DefaultNegativeRefreshers.NegativeRefresherWithNodata;
import com.mbg.library.DefaultPositiveRefreshers.HorizontalProgressWithArrow;
import com.mbg.library.DefaultPositiveRefreshers.OverlayProgressWithArrow;
import com.mbg.library.DefaultPositiveRefreshers.PositiveRefresherWithText;
import com.mbg.library.support.ViewScrollHelper.IViewScrollHelper;
import com.mbg.library.support.ViewScrollHelper.ViewScrollHelper;
import com.mbg.library.support.ViewAnimateHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/3/18.
 */

public class RefreshRelativeLayout extends RelativeLayout implements NestedScrollingParent,NestedScrollingChild{

    private static final String TAG="RefreshRelativeLayout";

    public static final int ORIENTATION_HORIZONTAL=0;
    public static final int ORIENTATION_VERTICAL=1;
    private static final int INVALID_POINTER=-1;

    /**
     * PositiveRefresher types
     */
    public static final int POSITIVE_PROGRESSOR=0;
    public static final int POSITIVE_ARROW_WITH_WHITETEXT=1;
    public static final int POSITIVE_ARROW_WITH_GRAYTEXT=2;
    public static final int HORIZONTAL_PROGRESSOR=3;
    /**
     * NegativeRefresher types
     */
    public static final int NEGATIVE_PROGRESS_WITHNODATA=0;
    public static final int NEGATIVE_PROGRESS_WITHNODATA_OVERLAY=1;
    public static final int HORIZONTAL_PROGRESSOR_NEGATIVE=2;
    public static final int HORIZONTAL_LOADMORE=3;

    /**
     * ids
     */
    private static final int VIEW_ID_TARGET=R.id.refresher_TargetView;
    private static final int VIEW_ID_POSITIVE=R.id.refresher_positiveView;
    private static final int VIEW_ID_NEGATIVE=R.id.refresher_negativeView;

    /**
     * Views
     */
    private IRefresher mPositiveRefresher,mNegativeRefresher;
    private View mPositiveRefreshView,mNegativeRefreshView;
    private View mTargetView;

    /**
     * config params
     * @param context
     */
    private boolean orientationIsHorizontal=false;//默认竖直布局
    private boolean positiveOverlayUsed,negativeOverlayUsed;//控件覆盖还是线性布局
    private boolean positiveDragEnable,negativeDragEnable;//控件是否可拖拽
    private boolean positiveEnable,negativeEnable;
    private int positiveRefresherType,negativeRefresherType;

    /**
     * touch params
     * @param context
     */
    private int mTouchSlop;
    private float DRAG_RATE=.5f;
    private int mActivePointerId=INVALID_POINTER;
    private float mInitialDownX,mInitialDownY;
    private float mInitialMotionX,mInitialMotionY;
    private int mPositiveWidth,mPositiveHeight;
    private int mNegativeWidth,mNegativeHeight;

    /**
     * NestedScrollHelper
     */
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private int mDragType=DRAG_TYPE_NULL;
    private ViewScrollHelper mScrollHelper;

    private static final int DRAG_TYPE_NULL=0;
    private static final int DRAG_TYPE_DOWNORRIGHT_NORMAL=1;//非刷新状态下拖拽
    private static final int DRAG_TYPE_UPORLEFT_NORMAL=2;//非刷新状态下拖拽
    private static final int DRAG_TYPE_POSITIVE_REFRESHING=3;//positiverefreshing 时上滑或者右滑
    private static final int DRAG_TYPE_NEGATIVE_REFRESHING=4;

    private ViewAnimateHelper mViewAnimateHelper;
    private ViewAnimateHelper.onAnimateEndListener mPositiveAnimEndListener,mNegativeAnimEndListener;//不是监听刷新控件动画，监听何时开始执行刷新控件的刷新状态
    private ViewAnimateHelper.onAnimateEndListener mAnimEndListenerWithoutRefresh;//所有View复原动画结束之后执行
    private ViewAnimateHelper.onAnimateStartListener mAnimStartListener;
    private boolean mPositiveRefreshing=false,mNegativeRefreshing=false;
    private int[] mInitialPadding;

    private List<ISingleRefreshListener> mPositiveRefreshListeners;
    private List<ISingleRefreshListener> mNegativeRefreshListeners;
    private List<IRefreshListener> mRefreshListeners;
    private ViewScrollHelper.onScrollToEdgeListener onScrollToEdgeListener;


    public void addPositiveRefreshListener(ISingleRefreshListener refreshListener){
        if(null == mPositiveRefreshListeners){
            mPositiveRefreshListeners=new ArrayList<>();
        }
        mPositiveRefreshListeners.add(refreshListener);
    }

    public void addNegativeRefreshListener(ISingleRefreshListener refreshListener){
        if(null == mNegativeRefreshListeners){
            mNegativeRefreshListeners=new ArrayList<>();
        }
        mNegativeRefreshListeners.add(refreshListener);
    }

    public void addRefreshListener(IRefreshListener listener){
        if(null == mRefreshListeners){
            mRefreshListeners=new ArrayList<>();
        }
        mRefreshListeners.add(listener);
    }

    public void setAnimateDuration(int duration){
        if(null == mViewAnimateHelper){
            return;
        }
        mViewAnimateHelper.setAnimateDuration(duration);
    }

    //下拉或者右滑是否可用
    public void setPositiveEnable(boolean enable){
        if(positiveEnable == enable){
            return;
        }
        positiveEnable = enable;
        if(positiveEnable){
            if(null == mPositiveRefresher){
                createDefaultPositiveRefresher();
            }
            else if(null == mPositiveRefreshView){
                mPositiveRefreshView=mPositiveRefresher.getView(getContext(),this);
                addViewDefault(mPositiveRefreshView);
            }
        }else{
            removePositiveRefreshView();
            mPositiveRefreshView = null;
        }
        resetViewsLayout();
    }

    //上滑或者左滑是否可用
    public void setNegativeEnable(boolean enable){
        if(negativeEnable == enable){
            return;
        }
        negativeEnable = enable;

        if(negativeEnable){
            if(null == mNegativeRefresher){
                createDefaultNegativeRefresher();
            }else if(null == mNegativeRefreshView){
                mNegativeRefreshView = mNegativeRefresher.getView(getContext(),this);
                addViewDefault(mNegativeRefreshView);
            }
        }else{
            removeNegativeRefreshView();
            mNegativeRefreshView = null;
        }
        resetViewsLayout();
    }

    //设置布局方式
    public void setOrientation(int orientation){
        boolean tempOrientation = orientation == ORIENTATION_HORIZONTAL;
        if(tempOrientation == orientationIsHorizontal){
            return;
        }
        orientationIsHorizontal = tempOrientation;
        mScrollHelper.setOrientation(mTargetView,orientationIsHorizontal);
        resetViewsLayout();
    }

    public void setPositiveDragEnable(boolean enable){
        this.positiveDragEnable =enable;
        mScrollHelper.setPositiveDragEnable(mTargetView,enable);
    }

    public void setNegativeDragEnable(boolean enable){
        this.negativeDragEnable = enable;
        mScrollHelper.setNegativeDragEnable(mTargetView,enable);
    }

    public void setPositiveRefresher(IRefresher refresher){
        if(null == refresher){
            return;
        }
        removePositiveRefreshView();
        this.mPositiveRefresher = refresher;
        this.mPositiveRefreshView = refresher.getView(getContext(),this);
        addViewDefault(mPositiveRefreshView);
        resetViewsLayout();
    }

    public void setNegativeRefresher(IRefresher refresher){
        if(null == refresher){
            return;
        }
        removeNegativeRefreshView();
        this.mNegativeRefresher = refresher;
        this.mNegativeRefreshView = refresher.getView(getContext(),this);
        addViewDefault(mNegativeRefreshView);
        resetViewsLayout();
    }

    public void setDragrate(float rate){
        if(rate < .1f){
            return;
        }
        DRAG_RATE = rate;
    }

    public void setPositiveOverlayUsed(boolean used){
        if(positiveOverlayUsed == used){
            return;
        }
        positiveOverlayUsed = used;
        resetViewsLayout();
    }

    public void setNegativeOverlayUsed(boolean used){
        if(negativeOverlayUsed == used){
            return;
        }
        negativeOverlayUsed = used;
        resetViewsLayout();
    }

    private void addViewDefault(View view){
        ViewGroup.LayoutParams params=view.getLayoutParams();
        if(null == params){
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        addView(view,params);
    }

    private void removePositiveRefreshView(){
        if(null != mPositiveRefreshView){
            removeView(mPositiveRefreshView);
        }
    }

    private void removeNegativeRefreshView(){
        if(null != mNegativeRefreshView){
            removeView(mNegativeRefreshView);
        }
    }

    public RefreshRelativeLayout(Context context) {
        this(context,null);
    }

    public RefreshRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
        initViews(context);
    }

    private void initAttrs(Context context ,AttributeSet attributeSet){
        TypedArray typedArray=context.obtainStyledAttributes(attributeSet,R.styleable.RefreshRelativeLayout);
        orientationIsHorizontal = typedArray.getInt(R.styleable.RefreshRelativeLayout_orientation,ORIENTATION_VERTICAL) == ORIENTATION_HORIZONTAL;
        positiveDragEnable = typedArray.getBoolean(R.styleable.RefreshRelativeLayout_positiveDragEnable,true);
        negativeDragEnable = typedArray.getBoolean(R.styleable.RefreshRelativeLayout_negativeDragEnable,false);
        positiveEnable = typedArray.getBoolean(R.styleable.RefreshRelativeLayout_positiveEnable,true);
        negativeEnable = typedArray.getBoolean(R.styleable.RefreshRelativeLayout_negativeEnable,true);
        positiveOverlayUsed = typedArray.getBoolean(R.styleable.RefreshRelativeLayout_isPositiveOverlay,true);
        negativeOverlayUsed = typedArray.getBoolean(R.styleable.RefreshRelativeLayout_isNegativeOverlay,false);
        positiveRefresherType =typedArray.getInt(R.styleable.RefreshRelativeLayout_positive_refresher_type,POSITIVE_PROGRESSOR);
        negativeRefresherType=typedArray.getInt(R.styleable.RefreshRelativeLayout_negative_refresher_type,NEGATIVE_PROGRESS_WITHNODATA);
        typedArray.recycle();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
        mViewAnimateHelper=new ViewAnimateHelper();
        mViewAnimateHelper.setAnimateStartListener(getAnimStartListener());
        mViewAnimateHelper.setAnimateEndListener(getAnimEndWithoutRefreshListener());
        mScrollHelper=new ViewScrollHelper(orientationIsHorizontal,positiveDragEnable,negativeDragEnable);
        onScrollToEdgeListener=new IViewScrollHelper.onScrollToEdgeListener() {
            @Override
            public void onScrollToEdge(int edgeType) {
                onScrollEdge(edgeType);
            }
        };
        mScrollHelper.setScrollToEdgeListener(onScrollToEdgeListener);
    }

    private int mScrollStatus=SCROLL_STATUS_NORMAL;

    private static final int SCROLL_STATUS_NORMAL=0;
    private static final int SCROLL_STATUS_TOUCH=1;
    private static final int SCROLL_STATUS_ANIMATE=2;
    private static final int SCROLL_STATUS_TOUCH_END=3;
    private static final int SCROLL_STATUS_ANIM_END=4;

    private void setScrollStatus(int status){
        if(SCROLL_STATUS_ANIMATE == status || SCROLL_STATUS_TOUCH == status){
            mScrollStatus = status;
        }else if(SCROLL_STATUS_TOUCH_END == status && SCROLL_STATUS_ANIMATE != mScrollStatus){
            mScrollStatus=SCROLL_STATUS_NORMAL;
        }else if(SCROLL_STATUS_ANIM_END == status && SCROLL_STATUS_TOUCH != mScrollStatus){
            mScrollStatus = SCROLL_STATUS_NORMAL;
        }
        // Log.i(TAG,"setScrollStatus->status:"+status+",mScrollStatus:"+mScrollStatus+",time:"+System.currentTimeMillis());
        if(SCROLL_STATUS_NORMAL == mScrollStatus){
            mScrollHelper.setScrollToEdgeListener(onScrollToEdgeListener);
        }else{
            mScrollHelper.setScrollToEdgeListener(null);
        }
    }


    private void onScrollEdge(int edgeType){
        //Log.i(TAG,"onScrollEdge->edgeType:"+edgeType+",time:"+System.currentTimeMillis());
        switch (edgeType){
            case ViewScrollHelper.EDGE_UP:
                if(!isNegativeRefreshing() && null != mPositiveRefreshView) {
                    mPositiveRefreshView.bringToFront();
                    showUpRefresher();
                }
                break;
            case ViewScrollHelper.EDGE_DOWN:
                if(!isPositiveRefreshing() && null != mNegativeRefreshView) {
                    mNegativeRefreshView.bringToFront();
                    showDownRefresher();
                }
                break;
            case ViewScrollHelper.EDGE_LEFT:
                if(!isNegativeRefreshing() && null != mPositiveRefreshView) {
                    mPositiveRefreshView.bringToFront();
                    showLeftRefresher();
                }
                break;
            case ViewScrollHelper.EDGE_RIGHT:
                if(!isPositiveRefreshing() && null != mNegativeRefreshView) {
                    mNegativeRefreshView.bringToFront();
                    showRightRefresher();
                }
                break;
        }
    }

    private void initViews(Context context){
        if(positiveEnable){
            createDefaultPositiveRefresher();
        }
        if(negativeEnable){
            createDefaultNegativeRefresher();
        }
        resetViewsLayout();
    }

    private void createDefaultPositiveRefresher(){
        if(null != mPositiveRefresher){
            return;
        }
        switch (positiveRefresherType){
            case POSITIVE_PROGRESSOR:
                mPositiveRefresher=new OverlayProgressWithArrow();
                break;
            case POSITIVE_ARROW_WITH_WHITETEXT:
                mPositiveRefresher = new PositiveRefresherWithText(true);
                break;
            case POSITIVE_ARROW_WITH_GRAYTEXT:
                mPositiveRefresher = new PositiveRefresherWithText(false);
                break;
            case HORIZONTAL_PROGRESSOR:
                mPositiveRefresher =  new HorizontalProgressWithArrow();
                break;
        }
        if(null == mPositiveRefresher){
            return;
        }
        mPositiveRefreshView=mPositiveRefresher.getView(getContext(),this);
        addViewDefault(mPositiveRefreshView);
    }

    private void createDefaultNegativeRefresher(){
        if(null != mNegativeRefresher){
            return;
        }
        switch (negativeRefresherType){
            case NEGATIVE_PROGRESS_WITHNODATA:
                mNegativeRefresher=new NegativeRefresherWithNodata();
                break;
            case NEGATIVE_PROGRESS_WITHNODATA_OVERLAY:
                mNegativeRefresher=new NegativeRefresherWithNodata(true);
                break;
            case HORIZONTAL_PROGRESSOR_NEGATIVE:
                mNegativeRefresher=new HorizontalProgressWithArrow();
                break;
            case HORIZONTAL_LOADMORE:
                mNegativeRefresher=new HorizontalLoadMore();

        }
        if(null == mNegativeRefresher){
            return;
        }
        mNegativeRefreshView=mNegativeRefresher.getView(getContext(),this);
        addViewDefault(mNegativeRefreshView);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        ensureTarget();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDetachedFromWindow() {
        //Log.i(TAG,"onDetachedFromWindow");
        if(null != mViewAnimateHelper){
            mViewAnimateHelper.release();
        }
        if(null != mPositiveRefresher){
            mPositiveRefresher.onStopRefresh();
        }
        if(null != mNegativeRefresher){
            mNegativeRefresher.onStopRefresh();
        }
        super.onDetachedFromWindow();
    }

    private void ensureTarget(){
        if(null != mTargetView){
            return;
        }
        for(int i=0;i<getChildCount();i++){
            View child= getChildAt(i);
            if(null != mPositiveRefreshView && mPositiveRefreshView.equals(child)){
                continue;
            }
            if(null != mNegativeRefreshView && mNegativeRefreshView.equals(child)){
                continue;
            }
            mTargetView=child;
            mTargetView.setId(VIEW_ID_TARGET);
            break;
        }
        mScrollHelper.addViewScroller(mTargetView);
        resetViewsLayout();
    }

    private void resetViewsLayout(){
        if(null == mTargetView){
            return;
        }
        mPositiveHeight= 0;
        mPositiveWidth = 0;
        mNegativeHeight = 0;
        mNegativeWidth = 0;
        if(orientationIsHorizontal){
            resetHorizontalViewsLayout();
        }else{
            resetVerticalViewsLayout();
        }
    }

    private void resetHorizontalViewsLayout(){
        if(null == mInitialPadding){
            mInitialPadding=new int[4];
            mInitialPadding[0]=getPaddingLeft();
            mInitialPadding[1]=getPaddingTop();
            mInitialPadding[2]=getPaddingRight();
            mInitialPadding[3]=getPaddingBottom();
        }
        int paddingLeft=mInitialPadding[0];
        int paddingTop= mInitialPadding[1];
        int paddingRight=mInitialPadding[2];
        int paddingBottom=mInitialPadding[3];
        RelativeLayout.LayoutParams targetLayoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if(null != mPositiveRefreshView){
            mPositiveRefreshView.setId(VIEW_ID_POSITIVE);
            mPositiveWidth= mPositiveRefreshView.getMeasuredWidth();
            //(TAG,"mPositiveWidth:"+mPositiveWidth);
            RelativeLayout.LayoutParams positiveParams=new RelativeLayout.LayoutParams(mPositiveWidth, mPositiveRefreshView.getMeasuredHeight());
            positiveParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
            positiveParams.addRule(CENTER_VERTICAL, TRUE);
            if(positiveOverlayUsed){
                targetLayoutParams.addRule(ALIGN_PARENT_LEFT,TRUE);
                mPositiveRefreshView.setTranslationX(-mPositiveWidth);
            }
            else {
                paddingLeft = -mPositiveWidth;
                mPositiveRefreshView.setTranslationX(0);
                targetLayoutParams.addRule(RelativeLayout.RIGHT_OF, VIEW_ID_POSITIVE);
            }
            mPositiveRefreshView.setLayoutParams(positiveParams);
            //addView(mPositiveRefreshView,positiveParams);
        }
        if(null != mNegativeRefreshView){
            mNegativeRefreshView.setId(VIEW_ID_NEGATIVE);
            mNegativeWidth = mNegativeRefreshView.getMeasuredWidth();
            RelativeLayout.LayoutParams negativeParams=new RelativeLayout.LayoutParams(mNegativeWidth, mNegativeRefreshView.getMeasuredHeight());
            negativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
            negativeParams.addRule(CENTER_VERTICAL, TRUE);
            if(negativeOverlayUsed){
                targetLayoutParams.addRule(ALIGN_PARENT_RIGHT,TRUE);
                mNegativeRefreshView.setTranslationX(mNegativeWidth);
            }
            else {
                paddingRight = -mNegativeWidth;
                mNegativeRefreshView.setTranslationX(0);
                targetLayoutParams.addRule(RelativeLayout.LEFT_OF, VIEW_ID_NEGATIVE);
            }
            mNegativeRefreshView.setLayoutParams(negativeParams);
            //addView(mNegativeRefreshView,negativeParams);
        }
        mTargetView.setLayoutParams(targetLayoutParams);
        setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
    }

    private void resetVerticalViewsLayout(){
        if(null == mInitialPadding){
            mInitialPadding=new int[4];
            mInitialPadding[0]=getPaddingLeft();
            mInitialPadding[1]=getPaddingTop();
            mInitialPadding[2]=getPaddingRight();
            mInitialPadding[3]=getPaddingBottom();
        }
        int paddingLeft=mInitialPadding[0];
        int paddingTop= mInitialPadding[1];
        int paddingRight=mInitialPadding[2];
        int paddingBottom=mInitialPadding[3];
        RelativeLayout.LayoutParams targetLayoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if(null != mPositiveRefreshView){
            mPositiveRefreshView.setId(VIEW_ID_POSITIVE);
            mPositiveHeight=mPositiveRefreshView.getMeasuredHeight();
            RelativeLayout.LayoutParams positiveParams=new RelativeLayout.LayoutParams(mPositiveRefreshView.getMeasuredWidth(),mPositiveHeight);
            positiveParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
            positiveParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            if(positiveOverlayUsed){
                targetLayoutParams.addRule(ALIGN_PARENT_TOP,TRUE);
                mPositiveRefreshView.setTranslationY(-mPositiveHeight);
            }
            else {
                paddingTop= -mPositiveHeight;
                mPositiveRefreshView.setTranslationY(0);
                targetLayoutParams.addRule(RelativeLayout.BELOW, VIEW_ID_POSITIVE);
            }
            mPositiveRefreshView.setLayoutParams(positiveParams);
            //addView(mPositiveRefreshView,positiveParams);
        }
        if(null != mNegativeRefreshView){
            mNegativeRefreshView.setId(VIEW_ID_NEGATIVE);
            mNegativeHeight= mNegativeRefreshView.getMeasuredHeight();
            RelativeLayout.LayoutParams negativeParams=new RelativeLayout.LayoutParams(mNegativeRefreshView.getMeasuredWidth(),mNegativeHeight);
            negativeParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
            negativeParams.addRule(ALIGN_PARENT_BOTTOM,TRUE);
            if(negativeOverlayUsed){
                targetLayoutParams.addRule(ALIGN_PARENT_BOTTOM,TRUE);
                mNegativeRefreshView.setTranslationY(mNegativeHeight);
            }
            else {
                paddingBottom = - mNegativeHeight;
                mNegativeRefreshView.setTranslationY(0);
                targetLayoutParams.addRule(ABOVE, VIEW_ID_NEGATIVE);
            }
            mNegativeRefreshView.setLayoutParams(negativeParams);
            //addView(mNegativeRefreshView,negativeParams);
        }
        mTargetView.setLayoutParams(targetLayoutParams);
        setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
    }


    //非overlay 模式下，上部的refresher是否在显示
    private boolean isUnoverlayoutUpShowing(){
        return getScrollY() < 0;
    }

    private boolean isUnoverlayDownShowing(){
        return getScrollY() > 0;
    }

    private boolean isUnoverlayLeftShowing(){
        return getScrollX() < 0;
    }

    private boolean isUnoverlayRightShowing(){
        return getScrollX() > 0;
    }

    //overlay状态下，上部的refreshView是否在显示
    private boolean isOverlayUpRefresherShowing(){
        if(null == mPositiveRefreshView){
            return false;
        }
        return positiveOverlayUsed && mPositiveRefreshView.getTranslationY() > -mPositiveHeight;
    }

    private boolean isOverlayDownRefresherShowing(){
        if(null == mNegativeRefreshView){
            return true;
        }
        return negativeOverlayUsed && mNegativeRefreshView.getTranslationY() < mNegativeHeight;
    }

    //overlay状态下，左侧refreshView是否在显示
    private boolean isOverlayLeftRefresherShowing(){
        if(null == mPositiveRefreshView){
            return false;
        }
        return positiveOverlayUsed && mPositiveRefreshView.getTranslationX() > - mPositiveWidth;
    }

    private boolean isOverlayRightRefresherShowing(){
        if(null == mNegativeRefreshView){
            return false;
        }
        return negativeOverlayUsed && mNegativeRefreshView.getTranslationX() < mNegativeWidth;
    }

    private void resetInitValueWhenUpShowing(){
        if(null != mPositiveRefresher){
            mPositiveRefresher.onStopRefresh();
        }
        if(isUnoverlayoutUpShowing()){
            mInitialDownY += getScrollY() - mPositiveRefresher.getOverlayOffset();
        }else if(isOverlayUpRefresherShowing()){
            mInitialDownY += -mPositiveRefreshView.getTranslationY()-mPositiveHeight-mPositiveRefresher.getOverlayOffset();
        }
        mInitialMotionY = mInitialDownY;
    }

    private void resetInitValueWhenDownShowing(){
        if(null != mNegativeRefresher){
            mNegativeRefresher.onStopRefresh();
        }
        if(isUnoverlayDownShowing()){
            mInitialMotionY = mInitialDownY +=getScrollY()+mNegativeRefresher.getOverlayOffset();
        }else if(isOverlayDownRefresherShowing()){
            mInitialMotionY = mInitialDownY -= (mNegativeRefreshView.getTranslationY()-mNegativeHeight-mNegativeRefresher.getOverlayOffset());
        }
    }

    private void resetInitValueWhenLeftShowing(){
        if(null != mPositiveRefresher){
            mPositiveRefresher.onStopRefresh();
        }
        if(isUnoverlayLeftShowing()){
            mInitialMotionX = mInitialDownX +=getScrollX()-mPositiveRefresher.getOverlayOffset();
        }else if(isOverlayLeftRefresherShowing()){
            mInitialMotionX = mInitialDownX += -mPositiveRefreshView.getTranslationX() - mPositiveWidth-mPositiveRefresher.getOverlayOffset();
        }
    }

    private void resetInitValueWhenRightShowing(){
        if(null != mNegativeRefresher){
            mNegativeRefresher.onStopRefresh();
        }
        if(isUnoverlayRightShowing()){
            mInitialMotionX = mInitialDownX +=getScrollX()+mNegativeRefresher.getOverlayOffset();
        }else if(isOverlayRightRefresherShowing()){
            mInitialMotionX =mInitialDownX -= mNegativeRefreshView.getTranslationX()-mNegativeWidth-mNegativeRefresher.getOverlayOffset();
        }
    }

    private float[] getMotionEventXY(MotionEvent ev, int activePointerId){
        int index=ev.findPointerIndex(activePointerId);
        if(index < 0){
            return null;
        }
        return new float[]{ev.getX(),ev.getY()};
    }

    private void onSecondartPointerUp(MotionEvent event){
        int pointerIndex = MotionEventCompat.getActionIndex(event);
        int pointerId = event.getPointerId(pointerIndex);
        if(pointerId == mActivePointerId){
            int newPointerIndex= pointerIndex == 0 ? 1 :0;
            mActivePointerId =event.getPointerId(newPointerIndex);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //int action = MotionEventCompat.getActionMasked(ev);
        int action=ev.getAction();
        //Log.i(TAG,"onInterceptTouchEvent:"+ev);
        if(!positiveEnable && !negativeEnable){
            return false;
        }
        ensureTarget();
        boolean intercept=false;
        switch (action){
            case MotionEvent.ACTION_DOWN:
                setScrollStatus(SCROLL_STATUS_TOUCH);
                requestDisallowInterceptTouchEvent(false);
                mActivePointerId = ev.getPointerId(0);
                float[] initialDownXY= getMotionEventXY(ev,mActivePointerId);
                if(null == initialDownXY){
                    //Log.i(TAG,"get null xy onInterceptTouchEvent");
                    return false;
                }
                mInitialDownX=initialDownXY[0];
                mInitialDownY=initialDownXY[1];
                break;
            case MotionEvent.ACTION_MOVE:
                if(INVALID_POINTER == mActivePointerId){
                    //Log.i(TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }
                float[] xy=getMotionEventXY(ev,mActivePointerId);
                if(null == xy){
                    //Log.i(TAG,"get null xy onInterceptTouchEvent");
                    return false;
                }
                float deltaX=xy[0] - mInitialDownX;
                float deltaY= xy[1] - mInitialDownY;
                intercept = canInterceptTouchEvent(deltaX,deltaY);
                if(intercept){
                    mInitialMotionX = mInitialDownX;
                    mInitialMotionY = mInitialDownY;
                    requestDisallowInterceptTouchEvent(true);
                }else{
                    requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondartPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER;
                mDragType = DRAG_TYPE_NULL;
                setScrollStatus(SCROLL_STATUS_TOUCH_END);
                requestDisallowInterceptTouchEvent(false);
                break;
        }
        //Log.i(TAG,"onInterceptTouchEvent:"+intercept);
        return intercept;
    }

    private boolean canInterceptTouchEvent(float offsetX,float offsetY){
        //Log.i(TAG,"canInterceptTouchEvent:"+offsetX+","+offsetY);
        float absX = Math.abs(offsetX),absY = Math.abs(offsetY);
        if(orientationIsHorizontal){
            if(absX < mTouchSlop || absX*2 <absY){
                return false;
            }
            if(offsetX > 0){
                //初动是右滑
                if(negativeEnable && isUnoverlayRightShowing()){//非overlay状态右滑正在刷新
                    mDragType =DRAG_TYPE_NEGATIVE_REFRESHING;
                    resetInitValueWhenRightShowing();
                    return true;
                }
                /*if(positiveEnable && !canChildScrollRight() && !isNegativeRefreshing()){*/
                if(positiveEnable && !mScrollHelper.canViewScrollRight(mTargetView) && !isNegativeRefreshing()){
                    mDragType =DRAG_TYPE_DOWNORRIGHT_NORMAL;
                    resetInitValueWhenLeftShowing();
                    return true;
                }
            }else{
                //初动是左滑
                if(positiveEnable && isUnoverlayLeftShowing()){//非overlay状态左滑正在刷新
                    mDragType =DRAG_TYPE_POSITIVE_REFRESHING;
                    resetInitValueWhenLeftShowing();
                    return true;
                }
                /*if(negativeEnable && !canChildScrollLeft() && !isPositiveRefreshing()){*/
                if(negativeEnable && !mScrollHelper.canViewScrollLeft(mTargetView) && !isPositiveRefreshing()){
                    mDragType =DRAG_TYPE_UPORLEFT_NORMAL;
                    resetInitValueWhenRightShowing();
                    return true;
                }
            }
        }else{
            if(absY < mTouchSlop || absY * 2 < absX){
                return false;
            }
            if(offsetY > 0){
                //初动是下拉
                if(negativeEnable && isUnoverlayDownShowing()){//非overlay状态上滑刷新正在刷新
                    mDragType = DRAG_TYPE_NEGATIVE_REFRESHING;
                    resetInitValueWhenDownShowing();
                    return true;
                }
                /*if(positiveEnable && !canChildScrollDown() && !isNegativeRefreshing()){*/
                if(positiveEnable && !mScrollHelper.canViewScrollDown(mTargetView) && !isNegativeRefreshing()){
                    mDragType =DRAG_TYPE_DOWNORRIGHT_NORMAL;
                    resetInitValueWhenUpShowing();
                    return true;
                }
            }else{
                //初动是上滑
                if(positiveEnable && isUnoverlayoutUpShowing()){
                    mDragType= DRAG_TYPE_POSITIVE_REFRESHING;
                    resetInitValueWhenUpShowing();
                    return true;
                }
                /*if(negativeEnable && !canChildScrollUp() && !isPositiveRefreshing()){*/
                if(negativeEnable && !mScrollHelper.canViewScrollUp(mTargetView) && !isPositiveRefreshing()){
                    mDragType = DRAG_TYPE_UPORLEFT_NORMAL;
                    resetInitValueWhenDownShowing();
                    return true;
                }
            }

        }
        return false;
    }

    //reinit mDragType when use normal View
    private boolean reinitDragType(MotionEvent ev,float x,float y){
        //Log.i(TAG,"reinitDragType");
        float deltaX=x - mInitialDownX;
        float deltaY= y - mInitialDownY;
        if(canInterceptTouchEvent(deltaX,deltaY)) {
            mInitialMotionX = mInitialDownX;
            mInitialMotionY = mInitialDownY;
            float dragX = (x - mInitialMotionX);
            float dragY = (y - mInitialMotionY);
            requestDisallowInterceptTouchEvent(true);
            return dealDragEvent(dragX, dragY);
        }
        requestDisallowInterceptTouchEvent(false);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.i(TAG,"onTouchEvent:"+event);
        //int action=MotionEventCompat.getActionMasked(event);
        int action=event.getAction();
        int pointerIndex = -1;
        boolean canTrans=true;
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = event.getPointerId(0);
                setScrollStatus(SCROLL_STATUS_TOUCH);
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = event.findPointerIndex(mActivePointerId);
                if(pointerIndex < 0){
                    //Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }
                float x = event.getX(pointerIndex);
                float y = event.getY(pointerIndex);
                if(DRAG_TYPE_NULL == mDragType){
                    reinitDragType(event,x,y);
                }else {
                    float dragX = (x - mInitialMotionX);
                    float dragY = (y - mInitialMotionY);
                    canTrans = dealDragEvent(dragX, dragY);
                }
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondartPointerUp(event);
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN:
                pointerIndex = event.getActionIndex();
                if(pointerIndex < 0){
                    //Log.e(TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = event.getPointerId(pointerIndex);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                pointerIndex = event.findPointerIndex(mActivePointerId);
                if(pointerIndex <0 ){
                    //Log.e(TAG, "Got ACTION_UP/ACTION_CANCEL event but don't have an active pointer id.");
                    return false;
                }
                float endX = event.getX(pointerIndex);
                float endY = event.getY(pointerIndex);
                float offsetX=(endX - mInitialMotionX);
                float offsetY=(endY - mInitialMotionY);
                onStopDrag(offsetX,offsetY);
                mActivePointerId = INVALID_POINTER;
                mDragType=0;
                setScrollStatus(SCROLL_STATUS_TOUCH_END);
                requestDisallowInterceptTouchEvent(false);
                canTrans = false;
                break;
        }
        return canTrans;
    }

    private boolean dealDragEvent(float dragX,float dragY){
        //Log.i("RefreshRelativeLayoutDealDrag","dealDragEvent:"+dragX+","+dragY);
        //Log.i("RefreshRelativeLayoutDealDrag","DragType:"+mDragType);
        float absX= Math.abs(dragX);
        float absY= Math.abs(dragY);
        dragX = dragX * DRAG_RATE;
        dragY = dragY * DRAG_RATE;
        if(orientationIsHorizontal &&  absX*2 > absY){
            if(DRAG_TYPE_POSITIVE_REFRESHING == mDragType || DRAG_TYPE_DOWNORRIGHT_NORMAL == mDragType){
                //右滑
                setViewOffsetRight((int) dragX);
                return true;
            }else if(DRAG_TYPE_NEGATIVE_REFRESHING == mDragType || DRAG_TYPE_UPORLEFT_NORMAL == mDragType){
                setViewOffsetLeft((int) dragX);
                return true;
            }
        }else if(!orientationIsHorizontal  && absX <absY*2){
            if(DRAG_TYPE_POSITIVE_REFRESHING == mDragType || DRAG_TYPE_DOWNORRIGHT_NORMAL == mDragType){
                //下拉
                setViewOffsetDown((int) (dragY));
                return true;
            }else if(DRAG_TYPE_NEGATIVE_REFRESHING == mDragType || DRAG_TYPE_UPORLEFT_NORMAL == mDragType){
                //上滑
                setViewOffsetUp((int) dragY);
                return true;
            }
        }
        return false;
    }


    private void setViewOffsetDown(int offset){
        //Log.i("RefreshRelativeLayoutDealDrag","setViewOffsetDown:"+offset);
        if(null == mPositiveRefreshView){
            return;
        }
        if(!positiveDragEnable){
            if(DRAG_TYPE_DOWNORRIGHT_NORMAL == mDragType) {
                autoStartRefreshing(true);
            }
            int showHeight= (int) (mPositiveHeight + mPositiveRefresher.getOverlayOffset());
            if(offset >= showHeight) {
                offset = showHeight;
            }else if(offset < 0){
                offset =0;
            }
        }
        if(positiveOverlayUsed){
            offset = offset < -mPositiveHeight ? -mPositiveHeight : offset;
            mPositiveRefreshView.bringToFront();
            mPositiveRefreshView.setTranslationY(-mPositiveHeight +offset);
        }
        else {
            offset = offset < 0 ? 0 : offset;
            scrollTo(0, -offset);
        }
        mPositiveRefresher.onDrag(offset);

    }

    private void setViewOffsetUp(int offset){
        //Log.i("RefreshRelativeLayoutDealDrag","setViewOffsetUp:"+offset);
        if(null == mNegativeRefreshView){
            return;
        }
        if(!negativeDragEnable){
            if(DRAG_TYPE_UPORLEFT_NORMAL == mDragType) {
                autoStartRefreshing(false);
            }
            int showHeight= (int) (mNegativeHeight + mNegativeRefresher.getOverlayOffset());
            if(offset <= -showHeight) {
                offset = -showHeight;
            }else if(offset >= 0){
                offset =0;
            }
        }
        if(negativeOverlayUsed){
            offset = offset > mNegativeHeight ? mNegativeHeight : offset;
            mNegativeRefreshView.bringToFront();
            mNegativeRefreshView.setTranslationY(mNegativeHeight +offset);
        }
        else {
            offset = offset > 0 ? 0:offset;
            scrollTo(0, -offset);
        }
        mNegativeRefresher.onDrag(-offset);
    }

    private void setViewOffsetLeft(int offset){
        //Log.i(TAG,"setViewOffsetLeft:"+offset);
        if(null == mNegativeRefreshView){
            return;
        }
        if(!negativeDragEnable){
            if(DRAG_TYPE_UPORLEFT_NORMAL == mDragType) {
                autoStartRefreshing(false);
            }
            int showWidth= (int) (mNegativeWidth+mNegativeRefresher.getOverlayOffset());
            if(offset < -showWidth) {
                offset = -showWidth;
            }else if(offset > 0){
                offset = 0;
            }
        }
        if(negativeOverlayUsed){
            offset = offset > mNegativeWidth ? mNegativeWidth :offset;
            mNegativeRefreshView.bringToFront();
            mNegativeRefreshView.setTranslationX(mNegativeWidth + offset);
        }else{
            offset = offset > 0 ? 0 :offset;
            scrollTo(-offset,0);
        }
        mNegativeRefresher.onDrag(-offset);
    }

    private void setViewOffsetRight(int offset){
        //Log.i(TAG,"setViewOffsetRight:"+offset);
        if(null == mPositiveRefreshView){
            return;
        }
        if(!positiveDragEnable){
            if(DRAG_TYPE_DOWNORRIGHT_NORMAL == mDragType) {
                autoStartRefreshing(true);
            }
            int showWidth= (int) (mPositiveWidth+mPositiveRefresher.getOverlayOffset());
            if(offset > showWidth) {
                offset = showWidth;
            }else if(offset < 0){
                offset =0;
            }
        }
        if(positiveOverlayUsed){
            offset = offset < -mPositiveWidth ? -mPositiveWidth : offset;
            mPositiveRefreshView.bringToFront();
            mPositiveRefreshView.setTranslationX(-mPositiveWidth +offset);
        }else{
            offset = offset < 0 ? 0 : offset;
            scrollTo(-offset,0);
        }
        mPositiveRefresher.onDrag(offset);
    }



    private synchronized void autoStartRefreshing(boolean isPositive){
        autoStartRefreshing(isPositive,false);
    }

    private synchronized void autoStartRefreshing(boolean isPositive, boolean withComplete){
        if(isRefreshing()){
            return;
        }
        if(isPositive){
            onPositiveRefresh();
            mPositiveRefreshing=withComplete ?false:true;
        }else{
            onNegativeRefresh();
            mNegativeRefreshing=withComplete ? false:true;
        }
    }


    private void onStopDrag(float offsetX,float offsetY){
        offsetX = offsetX*DRAG_RATE;
        offsetY = offsetY*DRAG_RATE;
        if(orientationIsHorizontal){
            if(DRAG_TYPE_POSITIVE_REFRESHING == mDragType || DRAG_TYPE_DOWNORRIGHT_NORMAL == mDragType){
                //右滑
                stopDragRight(offsetX);
                return;
            }else if(DRAG_TYPE_NEGATIVE_REFRESHING == mDragType || DRAG_TYPE_UPORLEFT_NORMAL == mDragType){
                //左滑
                stopDragLeft(-offsetX);
                return;
            }
            mViewAnimateHelper.horizonalSmoothScrollTo(this,0,200,null,null);
        }else{
            if(DRAG_TYPE_POSITIVE_REFRESHING == mDragType || DRAG_TYPE_DOWNORRIGHT_NORMAL == mDragType){
                //下拉
                stopDragDown(offsetY);
                return;
            }else if(DRAG_TYPE_NEGATIVE_REFRESHING == mDragType || DRAG_TYPE_UPORLEFT_NORMAL == mDragType){
                //上滑
                stopDragUp(-offsetY);
                return;
            }
            mViewAnimateHelper.verticalSmoothScrollTo(this,0,200,null,null);
        }
        //mDragType =0;
    }

    //上滑
    private void stopDragUp(float offset){
        int duration=mViewAnimateHelper.getAnimateDuration();
        if(null == mNegativeRefreshView){
            mViewAnimateHelper.verticalSmoothScrollTo(this,0,duration,null,null);
            return;
        }
        if(offset <= -(mNegativeHeight+mNegativeRefresher.getOverlayOffset()) && !negativeDragEnable){
            mNegativeRefresher.onStartRefresh();
            return;
        }
        ViewAnimateHelper.onAnimateEndListener endListener=null;
        float translateY=0,scrollY=0;
        if((!negativeDragEnable && DRAG_TYPE_UPORLEFT_NORMAL == mDragType) || mNegativeRefresher.canRefresh(offset)){
            endListener=getNegativeAnimEndListener();
            translateY=-mNegativeRefresher.getOverlayOffset();
            scrollY=mNegativeHeight+mNegativeRefresher.getOverlayOffset();
        }else{
            translateY=mNegativeHeight;
            scrollY=0;
        }
        if(negativeOverlayUsed){
            mViewAnimateHelper.smoothTranslateY(mNegativeRefreshView,translateY,duration,false,mNegativeRefresher,endListener);
        }else{
            mViewAnimateHelper.verticalSmoothScrollTo(this,scrollY,duration,mNegativeRefresher,endListener);
        }
    }

    //下拉
    private void stopDragDown(float offset){
        int duration=mViewAnimateHelper.getAnimateDuration();
        if(null == mPositiveRefreshView){
            mViewAnimateHelper.verticalSmoothScrollTo(this,0,duration,null,null);
            return;
        }
        if(offset >= mPositiveHeight+mPositiveRefresher.getOverlayOffset() && !positiveDragEnable){
            mPositiveRefresher.onStartRefresh();
            return;
        }
        ViewAnimateHelper.onAnimateEndListener endListener=null;
        float translateY=0,scrollY=0;
        if((!positiveDragEnable && DRAG_TYPE_DOWNORRIGHT_NORMAL == mDragType) || mPositiveRefresher.canRefresh(offset)){
            endListener=getPositiveAnimEndListener();
            translateY=mPositiveRefresher.getOverlayOffset();
            scrollY=-mPositiveHeight-mPositiveRefresher.getOverlayOffset();
        }else{
            translateY=-mPositiveHeight;
            scrollY=0;
        }
        if(positiveOverlayUsed){
            mViewAnimateHelper.smoothTranslateY(mPositiveRefreshView,translateY,duration,true,mPositiveRefresher,endListener);
        }else{
            mViewAnimateHelper.verticalSmoothScrollTo(this,scrollY,duration,mPositiveRefresher,endListener);
        }
    }

    private void stopDragLeft(float offset){
        int duration=mViewAnimateHelper.getAnimateDuration();
        if(null == mNegativeRefreshView){
            mViewAnimateHelper.horizonalSmoothScrollTo(this,0,duration,null,null);
            return;
        }
        if(offset <= -(mNegativeWidth+mNegativeRefresher.getOverlayOffset()) && !negativeDragEnable){
            mNegativeRefresher.onStartRefresh();
            return;
        }
        ViewAnimateHelper.onAnimateEndListener endListener=null;
        float translateX=0,scrollX=0;
        if((!negativeDragEnable && DRAG_TYPE_UPORLEFT_NORMAL == mDragType) || mNegativeRefresher.canRefresh(offset)){
            endListener=getNegativeAnimEndListener();
            translateX = -mNegativeRefresher.getOverlayOffset();
            scrollX=mNegativeWidth+mNegativeRefresher.getOverlayOffset();
        }else{
            translateX=mNegativeWidth;
            scrollX=0;
        }
        if(negativeOverlayUsed){
            mViewAnimateHelper.smoothTranslateX(mNegativeRefreshView,translateX,duration,false,mNegativeRefresher,endListener);
        }else{
            mViewAnimateHelper.horizonalSmoothScrollTo(this,scrollX,duration,mNegativeRefresher,endListener);
        }
    }

    private void stopDragRight(float offset){
        int duration = mViewAnimateHelper.getAnimateDuration();
        if(null == mPositiveRefreshView){
            mViewAnimateHelper.horizonalSmoothScrollTo(this,0,duration,null,null);
            return;
        }
        if(offset >= mPositiveWidth+mPositiveRefresher.getOverlayOffset() && !positiveDragEnable){
            //已经开始刷新，故直接返回
            mPositiveRefresher.onStartRefresh();
            return;
        }
        ViewAnimateHelper.onAnimateEndListener endListener=null;
        float translateX=0,scrollX=0;
        if((!positiveDragEnable && DRAG_TYPE_DOWNORRIGHT_NORMAL == mDragType)|| mPositiveRefresher.canRefresh(offset)){
            endListener=getPositiveAnimEndListener();
            translateX=mPositiveRefresher.getOverlayOffset();
            scrollX = -mPositiveWidth-mPositiveRefresher.getOverlayOffset();
        }else{
            translateX= -mPositiveWidth;
            scrollX=0;
        }
        if(positiveOverlayUsed){
            mViewAnimateHelper.smoothTranslateX(mPositiveRefreshView,translateX,duration,true,mPositiveRefresher,endListener);
        }else{
            mViewAnimateHelper.horizonalSmoothScrollTo(this,scrollX,duration,mPositiveRefresher,endListener);
        }
    }

    private ViewAnimateHelper.onAnimateStartListener getAnimStartListener(){
        if(null == mAnimStartListener){
            mAnimStartListener=new ViewAnimateHelper.onAnimateStartListener() {
                @Override
                public void onAnimateStart() {
                    setScrollStatus(SCROLL_STATUS_ANIMATE);
                }
            };
        }
        return mAnimStartListener;
    }


    private ViewAnimateHelper.onAnimateEndListener getAnimEndWithoutRefreshListener(){
        if(null == mAnimEndListenerWithoutRefresh){
            mAnimEndListenerWithoutRefresh=new ViewAnimateHelper.onAnimateEndListener() {
                @Override
                public void onAnimateEnd() {
                    //api 23以下使用viewtreeObserver的onScorllChangeListener,略有延时
                    //Log.i(TAG,"onAnimateEnd:"+System.currentTimeMillis());
                    if(Build.VERSION.SDK_INT >= 23 || isRefreshing()) {
                        setScrollStatus(SCROLL_STATUS_ANIM_END);
                    }else{
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setScrollStatus(SCROLL_STATUS_ANIM_END);
                            }
                        },100);
                    }
                }
            };
        }
        return mAnimEndListenerWithoutRefresh;
    }

    private ViewAnimateHelper.onAnimateEndListener getPositiveAnimEndListener(){
        if(null == mPositiveAnimEndListener){
            mPositiveAnimEndListener=new ViewAnimateHelper.onAnimateEndListener() {
                @Override
                public void onAnimateEnd() {
                    //Log.i(TAG,"getPositiveAnimEndListener");
                    if (null == mPositiveRefresher) {
                        return;
                    }
                    boolean withComplete = mPositiveRefresher.onStartRefresh();
                    autoStartRefreshing(true, withComplete);
                }
            };
        }
        return mPositiveAnimEndListener;
    }

    private ViewAnimateHelper.onAnimateEndListener getNegativeAnimEndListener(){
        if(null == mNegativeAnimEndListener){
            mNegativeAnimEndListener = new ViewAnimateHelper.onAnimateEndListener() {
                @Override
                public void onAnimateEnd() {
                    //Log.i(TAG,"getNegativeAnimEndListener");
                    if(null == mNegativeRefresher){
                        return;
                    }
                    boolean withComplete=mNegativeRefresher.onStartRefresh();
                    autoStartRefreshing(false,withComplete);
                }
            };
        }
        return mNegativeAnimEndListener;
    }

    /**
     * refresh
     */

    public void startPositiveRefresh(){
        if(!positiveEnable || null == mPositiveRefreshView){
            return;
        }
        if(isRefreshing()){
            return;
        }
        mPositiveRefreshView.bringToFront();
        if(orientationIsHorizontal){
            showLeftRefresher();
        }else{
            showUpRefresher();
        }
    }

    public void startNegativeRefresh(){
        if(!negativeEnable ||null == mNegativeRefreshView){
            return;
        }
        if(isRefreshing()){
            return;
        }
        mNegativeRefreshView.bringToFront();
        if(orientationIsHorizontal){
            showRightRefresher();
        }else{
            showDownRefresher();
        }

    }

    public void positiveRefreshComplete(){
        mPositiveRefreshing=false;
        if(null == mPositiveRefreshView){
            return;
        }
        long delay=mPositiveRefresher.onRefreshComplete();
        if(orientationIsHorizontal){
            hideLeftRefresher(delay);
            if(!mNegativeRefreshing){
                hideRightRefresher(0);
            }
        }else{
            hideUpRefresher(delay);
            if(!mNegativeRefreshing){
                hideDownRefresher(0);
            }
        }
    }

    public void negativeRefreshComplete(){
        mNegativeRefreshing=false;
        if(null == mNegativeRefreshView){
            return;
        }
        long delay=mNegativeRefresher.onRefreshComplete();
        if(orientationIsHorizontal){
            hideRightRefresher(delay);
            if(!mPositiveRefreshing){
                hideLeftRefresher(0);
            }
        }
        else {
            hideDownRefresher(delay);
            if(!mPositiveRefreshing){
                hideUpRefresher(0);
            }
        }
    }

    private void onPositiveRefresh(){
        if(null != mPositiveRefreshListeners){
            for(int i= mPositiveRefreshListeners.size()-1; i >= 0; i--){
                mPositiveRefreshListeners.get(i).onRefresh();
            }
        }
        if(null != mRefreshListeners){
            for(int i= mRefreshListeners.size()-1 ; i >= 0 ; i--){
                mRefreshListeners.get(i).onPositiveRefresh();
            }
        }
    }

    private void onNegativeRefresh(){
        if(null != mNegativeRefreshListeners){
            for(int i = mNegativeRefreshListeners.size() -1 ; i>= 0; i--){
                mNegativeRefreshListeners.get(i).onRefresh();
            }
        }
        if(null != mRefreshListeners){
            for(int i = mRefreshListeners.size() - 1 ; i>= 0; i--){
                mRefreshListeners.get(i).onNegativeRefresh();
            }
        }
    }

    public boolean isRefreshing(){
        return mPositiveRefreshing || mNegativeRefreshing;
    }

    public boolean isPositiveRefreshing(){
        return mPositiveRefreshing;
    }

    public boolean isNegativeRefreshing(){
        return mNegativeRefreshing;
    }

    private synchronized void hideUpRefresher(long delay){
        int duration=mViewAnimateHelper.getAnimateDuration();
        if(positiveOverlayUsed){
            if(isOverlayUpRefresherShowing()) {
                mViewAnimateHelper.delaySmoothTranslateY(mPositiveRefreshView,-mPositiveHeight,duration,delay,true,mPositiveRefresher,null);
            }
        }else  if(isUnoverlayoutUpShowing()){
            mViewAnimateHelper.delayScrollToY(this,0,duration,delay,mPositiveRefresher,null);
        }
    }

    private synchronized void hideDownRefresher(long delay){
        int duration=mViewAnimateHelper.getAnimateDuration();
        if(negativeOverlayUsed){
            if(isOverlayDownRefresherShowing()) {
                mViewAnimateHelper.delaySmoothTranslateY(mNegativeRefreshView,mNegativeHeight,duration,delay,false,mNegativeRefresher,null);
            }
        }else if(isUnoverlayDownShowing()){
            mViewAnimateHelper.delayScrollToY(this,0,duration,delay,mNegativeRefresher,null);
        }
    }

    private synchronized void hideLeftRefresher(long delay){
        int duration=mViewAnimateHelper.getAnimateDuration();
        if(positiveOverlayUsed){
            if(isOverlayLeftRefresherShowing()) {
                mViewAnimateHelper.delaySmoothTranslateX(mPositiveRefreshView,-mPositiveWidth,duration,delay,true,mPositiveRefresher,null);
            }
        }else if(isUnoverlayLeftShowing()){
            mViewAnimateHelper.delayScrollToX(this,0,duration,delay,mPositiveRefresher,null);
        }
    }

    private synchronized void hideRightRefresher(long delay){
        int duration=mViewAnimateHelper.getAnimateDuration();
        if(negativeOverlayUsed){
            if(isOverlayRightRefresherShowing()) {
                mViewAnimateHelper.delaySmoothTranslateX(mNegativeRefreshView,mNegativeWidth,duration,delay,false,mNegativeRefresher,null);
            }
        }else if(isUnoverlayRightShowing()){
            mViewAnimateHelper.delayScrollToX(this,0,duration,delay,mNegativeRefresher,null);
        }
    }

    private synchronized void showUpRefresher(){
        int duration = mViewAnimateHelper.getAnimateDuration();
        long delay=0;
        if(0 == mPositiveHeight){
            delay=100;
        }
        if(positiveOverlayUsed){
            if(!isOverlayUpRefresherShowing()){
                mViewAnimateHelper.delaySmoothTranslateY(mPositiveRefreshView, (int) mPositiveRefresher.getOverlayOffset(),duration,delay,true,mPositiveRefresher,getPositiveAnimEndListener());
            }
        }else if(!isUnoverlayoutUpShowing()){
            mViewAnimateHelper.delayScrollToYWithRefer(this,mPositiveRefreshView,-mPositiveRefresher.getOverlayOffset(),true,duration,delay,mPositiveRefresher,getPositiveAnimEndListener());
        }
    }

    private synchronized void showDownRefresher(){
        int duration = mViewAnimateHelper.getAnimateDuration();
        long delay=0;
        if(0 == mNegativeHeight){
            delay=100;
        }
        if(negativeOverlayUsed){
            if(!isOverlayDownRefresherShowing()){
                mViewAnimateHelper.delaySmoothTranslateY(mNegativeRefreshView, (int) -mNegativeRefresher.getOverlayOffset(),duration,delay,false,mNegativeRefresher,getNegativeAnimEndListener());
            }
        }else if(!isUnoverlayDownShowing()){
            mViewAnimateHelper.delayScrollToYWithRefer(this,mNegativeRefreshView,mNegativeRefresher.getOverlayOffset(),false,duration,delay,mNegativeRefresher,getNegativeAnimEndListener());
        }
    }

    private synchronized void showLeftRefresher(){
        int duration=mViewAnimateHelper.getAnimateDuration();
        long delay=0;
        if(0 == mPositiveWidth){
            delay=100;
        }
        if(positiveOverlayUsed){
            if(!isOverlayLeftRefresherShowing()){
                mViewAnimateHelper.delaySmoothTranslateX(mPositiveRefreshView, (int) mPositiveRefresher.getOverlayOffset(),duration,delay,true,mPositiveRefresher,getPositiveAnimEndListener());
            }
        }else{
            mViewAnimateHelper.delayScrollToXWithRefer(this,mPositiveRefreshView,-mPositiveRefresher.getOverlayOffset(),true,duration,delay,mPositiveRefresher,getPositiveAnimEndListener());
        }
    }

    private synchronized void showRightRefresher(){
        int duration = mViewAnimateHelper.getAnimateDuration();
        long delay=0;
        if(0 == mNegativeWidth){
            delay =100;
        }
        if(negativeOverlayUsed){
            if(!isOverlayRightRefresherShowing()){
                mViewAnimateHelper.delaySmoothTranslateX(mNegativeRefreshView, -(int) mNegativeRefresher.getOverlayOffset(),duration,delay,false,mNegativeRefresher,getNegativeAnimEndListener());
            }
        }else if(!isUnoverlayRightShowing()){
            mViewAnimateHelper.delayScrollToXWithRefer(this,mNegativeRefreshView,mNegativeRefresher.getOverlayOffset(),false,duration,delay,mNegativeRefresher,getNegativeAnimEndListener());
        }
    }

    /**
     *NestedScrollingParent
     */
    private float mTotalValidUnconsumed=0;
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        //Log.i(TAG,"onStartNestedScroll:"+nestedScrollAxes);
        setScrollStatus(SCROLL_STATUS_TOUCH);
        int offset=0;
        boolean isRefresherShowing;
        if(orientationIsHorizontal){
            offset = nestedScrollAxes & ViewCompat.SCROLL_AXIS_HORIZONTAL;
            isRefresherShowing=isOverlayLeftRefresherShowing() || isOverlayRightRefresherShowing()
                    ||isUnoverlayLeftShowing() || isUnoverlayRightShowing();
        }else{
            offset = nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL;
            isRefresherShowing = isOverlayUpRefresherShowing() || isOverlayDownRefresherShowing()
                    || isUnoverlayoutUpShowing() || isUnoverlayDownShowing();
        }
        return !isRefresherShowing && offset != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        //Log.i(TAG,"onNestedScrollAccepted:"+axes);
        mDragType =0;
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        if(orientationIsHorizontal){
            startNestedScroll(axes & ViewCompat.SCROLL_AXIS_HORIZONTAL);
        }
        else {
            startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        }
        mTotalValidUnconsumed=0;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        //Log.i(TAG,"onNestedPreScroll->dx:"+dx+",dy:"+dy+",consumed:"+consumed+",mTotalValidUnconsumed:"+mTotalValidUnconsumed);
        if(orientationIsHorizontal){
            if(isOverlayLeftRefresherShowing() || isUnoverlayLeftShowing() || isOverlayRightRefresherShowing() || isUnoverlayRightShowing()){
                mTotalValidUnconsumed -=dx;
                consumed[0]=dx;
                dealDragEventWithNested(mTotalValidUnconsumed,0);
                return;
            }
        }else{
            if(isOverlayUpRefresherShowing() || isUnoverlayoutUpShowing() || isOverlayDownRefresherShowing() || isUnoverlayDownShowing()){
                mTotalValidUnconsumed -= dy;
                consumed[1]=dy;
                dealDragEventWithNested(0,mTotalValidUnconsumed);
                return;
            }
        }
        mDragType=0;
        mTotalValidUnconsumed=0;
    }

    private void dealDragEventWithNested(float offsetX,float offsetY){
        //Log.i(TAG,"dealDragEventWithNested:offsetX:"+offsetX+",offsetY:"+offsetY);
        if(mDragType != 0){
            dealDragEvent(offsetX,offsetY);
            return;
        }
        if(orientationIsHorizontal){
            mDragType = offsetX > 0 ? 1: 2;
        }else{
            mDragType = offsetY > 0 ? 1 : 2;
        }
        dealDragEvent(offsetX,offsetY);
    }

    private int[] mParentOffsetInWindow=new int[2];
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        //Log.i(TAG,"onNestedScroll->"+",orientationIsHorizontal:"+orientationIsHorizontal+",dxConsumed:"+dxConsumed+",dyConsumed:"+dyConsumed+",dxUnconsumed:"+dxUnconsumed+",dyUnconsumed:"+dyUnconsumed);
        dispatchNestedScroll(dxConsumed,dyConsumed,dxUnconsumed,dyUnconsumed,mParentOffsetInWindow);
        float offsetX=0,offsetY=0;
        if(orientationIsHorizontal && dxUnconsumed != 0){
            mTotalValidUnconsumed-=dxUnconsumed;
            if(mTotalValidUnconsumed > 0 && isNegativeRefreshing()){
                mTotalValidUnconsumed=0;
            }else if(mTotalValidUnconsumed < 0 && isPositiveRefreshing()){
                mTotalValidUnconsumed=0;
            }
            offsetX = mTotalValidUnconsumed;
        }else if(!orientationIsHorizontal && dyUnconsumed !=0){
            mTotalValidUnconsumed-=dyUnconsumed;
            if(mTotalValidUnconsumed > 0 && isNegativeRefreshing()){
                mTotalValidUnconsumed=0;
            }else if(mTotalValidUnconsumed < 0 && isPositiveRefreshing()){
                mTotalValidUnconsumed=0;
            }
            offsetY = mTotalValidUnconsumed;
        }
        if(0 != offsetX || 0 != offsetY) {
            dealDragEventWithNested(offsetX, offsetY);
        }

    }

    @Override
    public void onStopNestedScroll(View child) {
        //Log.i(TAG,"onStopNestedScroll");
        mNestedScrollingParentHelper.onStopNestedScroll(child);
        setScrollStatus(SCROLL_STATUS_TOUCH_END);
        stopNestedScroll();
        float offsetX=0,offsetY=0;
        if(orientationIsHorizontal){
            offsetX=mTotalValidUnconsumed;
        }else{
            offsetY=mTotalValidUnconsumed;
        }
        if(mTotalValidUnconsumed != 0) {
            onStopDrag(offsetX, offsetY);
        }
        mTotalValidUnconsumed=0;

    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    /**
     *
     * NestedScrollingChild
     */

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX,
                                    float velocityY) {
        //Log.i("RefreshRelativeLayoutNested","onNestedPreFling->velocityX:"+velocityX+",velocityY:"+velocityY);
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY,
                                 boolean consumed) {
        //Log.i("RefreshRelativeLayoutNested","onNestedFling->velocityX:"+velocityX+",velocityY:"+velocityY+",consumed:"+consumed);
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        //Log.i("RefreshRelativeLayoutNested","dispatchNestedFling");
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        //Log.i("RefreshRelativeLayoutNested","dispatchNestedPreFling");
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

}
