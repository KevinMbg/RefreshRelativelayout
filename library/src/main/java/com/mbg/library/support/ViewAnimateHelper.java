package com.mbg.library.support;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.mbg.library.IRefresher;


/**
 * Created by Administrator on 2017/3/19.
 */

public class ViewAnimateHelper {
    private int ANIMATE_DURATION = 200;
    private DecelerateInterpolator decelerateInterpolator;
    private ValueAnimator mAnimator;

    public void setAnimateDuration(int duration){
        if(duration < 0){
            return;
        }
        duration = duration > 5000 ? 5000 : duration;
        ANIMATE_DURATION = duration;
    }

    public int getAnimateDuration(){
        return ANIMATE_DURATION;
    }

    private DecelerateInterpolator getDecelerateInterpolator(){
        if(null == decelerateInterpolator){
            decelerateInterpolator=new DecelerateInterpolator(2f);
        }
        return decelerateInterpolator;
    }



    public void horizonalSmoothScrollTo(final View view, float endOffset, long duration, final IRefresher refresher, onAnimateEndListener endListener){
        int startOffset=view.getScrollX();
        startAnimatorOfInt(startOffset, (int) endOffset, duration, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int offset= (int) animation.getAnimatedValue();
                view.scrollTo( offset,0);
                if(null != refresher) {
                    refresher.onDrag(Math.abs(offset));
                }
            }
        },endListener);
    }

    public void verticalSmoothScrollTo(final View view, float endOffset, long duration, final IRefresher refresher, onAnimateEndListener endListener){
        int startOffset=view.getScrollY();
        startAnimatorOfInt( startOffset, (int) endOffset, duration, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int offset= (int) animation.getAnimatedValue();
                view.scrollTo(0, offset);
                if(null != refresher) {
                    refresher.onDrag(Math.abs(offset));
                }
            }
        },endListener);
    }

    public void smoothTranslateX(final View view, float endX, long duration,boolean isPositive, final IRefresher refresher, onAnimateEndListener endListener){
        float startX=view.getTranslationX();
        int width=view.getMeasuredWidth();
        if(isPositive){
            width*=-1;
        }
        final int trueWidth=width;
        startAnimatorOfFloat(startX, endX, duration, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translateX= (float) animation.getAnimatedValue();
                view.setTranslationX(translateX);
                if(null != refresher) {
                    refresher.onDrag(Math.abs(translateX-trueWidth));
                }
            }
        },endListener);
    }

    public void smoothTranslateY(final View view, float endY, long duration,boolean isPositive, final IRefresher refresher, onAnimateEndListener listener){
        float startY=view.getTranslationY();
        int height=view.getMeasuredHeight();
        if(isPositive){
            height *=-1;
        }
        final int trueHeight=height;
        startAnimatorOfFloat(startY, endY, duration, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translateY= (float) animation.getAnimatedValue();
                view.setTranslationY(translateY);
                if(null != refresher) {
                    refresher.onDrag(Math.abs(translateY-trueHeight));
                }
            }
        },listener);
    }

    public void startAnimatorOfFloat(float startOffset, float endOffset, long duration, ValueAnimator.AnimatorUpdateListener animateupdateListener, final onAnimateEndListener endListener){
        if(null != mAnimator && mAnimator.isStarted()){
            return;
        }
        mAnimator = ValueAnimator.ofFloat(startOffset,endOffset);
        mAnimator.setDuration(duration);
        mAnimator.addUpdateListener(animateupdateListener);
        mAnimator.setInterpolator(getDecelerateInterpolator());
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(null != endListener){
                    endListener.onAnimateEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.start();
    }

    public void startAnimatorOfInt(int startOffset, int endOffset, long duration, ValueAnimator.AnimatorUpdateListener animateupdateListener, final onAnimateEndListener endListener){
        if(null != mAnimator && mAnimator.isStarted()){
            return;
        }
        mAnimator = ValueAnimator.ofInt(startOffset,endOffset);
        mAnimator.setDuration(duration);
        mAnimator.addUpdateListener(animateupdateListener);
        mAnimator.setInterpolator(getDecelerateInterpolator());
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(null != endListener){
                    endListener.onAnimateEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.start();
    }


    public void delaySmoothTranslateY(final View view, final int endoffset, final long duration, long delay, final boolean isPositive, final IRefresher refresher, final onAnimateEndListener endListener){
        if(delay == 0){
            smoothTranslateY(view,endoffset,duration,isPositive,refresher,endListener);
        }else{
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    smoothTranslateY(view,endoffset,duration,isPositive,refresher,endListener);
                }
            },delay);
        }
    }

    public void delaySmoothTranslateX(final View view, final int endoffset, final long duration, long delay,final boolean isPositive, final IRefresher refresher, final onAnimateEndListener endListener){
        if(delay == 0){
            smoothTranslateX(view,endoffset,duration,isPositive,refresher,endListener);
        }else{
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    smoothTranslateX(view,endoffset,duration,isPositive,refresher,endListener);
                }
            },delay);
        }
    }

    public void delayScrollToY(final View view, final float endOffset, final long duration, long delay, final IRefresher refresher, final onAnimateEndListener endListener){
        if(0 == delay){
            verticalSmoothScrollTo(view,endOffset,duration,refresher,endListener);
        }else {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    verticalSmoothScrollTo(view,endOffset,duration,refresher,endListener);
                }
            }, delay);
        }
    }

    public void delayScrollToX(final View view, final float endOffset, final long duration, long delay, final IRefresher refresher, final onAnimateEndListener endListener){
        if(0 == delay){
            horizonalSmoothScrollTo(view,endOffset,duration,refresher,endListener);
        }else{
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    horizonalSmoothScrollTo(view,endOffset,duration,refresher,endListener);
                }
            },delay);
        }
    }

    /**
     * 有参考view的水平延时滑动
     * @param view
     * @param referView
     * @param withOffset
     * @param isPositive
     * @param duration
     * @param delay
     * @param refresher
     * @param endListener
     */
    public void delayScrollToXWithRefer(final View view, final View referView, final float withOffset, final boolean isPositive, final long duration, long delay, final IRefresher refresher, final onAnimateEndListener endListener){
        if(delay == 0){
            startScrooToXAnimator(view, referView,withOffset, isPositive, duration, refresher, endListener);
        }else {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startScrooToXAnimator(view, referView,withOffset, isPositive, duration, refresher, endListener);
                }
            }, delay);
        }
    }

    private void startScrooToXAnimator(View view,View referView,float withOffset,boolean isPositive,long duration,IRefresher refresher,onAnimateEndListener endListener){
        if(referView == null || view == null){
            return;
        }
        float width=0;
        if(isPositive){
            width = -referView.getMeasuredWidth()+withOffset;
        }else{
            width=referView.getMeasuredWidth()+withOffset;
        }
        horizonalSmoothScrollTo(view,width,duration,refresher,endListener);
    }

    private void startScrooToYAnimator(View view,View referView,float withOffset,boolean isPositive,long duration,IRefresher refresher,onAnimateEndListener endListener){
        if(referView == null || view == null){
            return;
        }
        float height=0;
        if(isPositive){
            height = -referView.getMeasuredHeight()+withOffset ;
        }else{
            height=referView.getMeasuredHeight()+withOffset;
        }
        verticalSmoothScrollTo(view,height,duration,refresher,endListener);
    }



    /**
     * 有参考View的延时滑动
     * @param view
     * @param referView
     * @param withOffset
     * @param isPositive
     * @param duration
     * @param delay
     * @param refresher
     * @param endListener
     */
    public void delayScrollToYWithRefer(final View view, final View referView, final float withOffset, final boolean isPositive, final long duration, final long delay, final IRefresher refresher, final onAnimateEndListener endListener){
       if(delay == 0){
           startScrooToYAnimator(view,referView,withOffset,isPositive,duration,refresher,endListener);
       }else {
           view.postDelayed(new Runnable() {
               @Override
               public void run() {
                   startScrooToYAnimator(view, referView,withOffset, isPositive, duration, refresher, endListener);
               }
           }, delay);
       }
    }

    /**
     * release ValueAnimator when context is finish
     */
    public void release(){
        if(null != mAnimator && mAnimator.isStarted()){
            mAnimator.cancel();
        }
    }

    public interface onAnimateEndListener{
        void onAnimateEnd();
    }
}
