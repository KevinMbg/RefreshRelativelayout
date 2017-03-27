package com.mbg.library.DefaultPositiveRefreshers;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.mbg.library.IRefresher;
import com.mbg.library.R;

/**
 * Created by Administrator on 2017/3/22.
 */

public class PositiveRefresherWithText implements IRefresher {
    private View view;
    private ImageView arrow,progress,state;
    private TextView textView;
    private ObjectAnimator animator;
    private boolean isSuccess=true;
    private String compleText="刷新成功";
    private int layoutId;
    private int refreshSuccessId,refreshFailId;

    private int height=0;

    public PositiveRefresherWithText(){
        layoutId=R.layout.positive_refresher_qq;
        refreshSuccessId=R.drawable.refresh_success_white;
        refreshFailId=R.drawable.refresh_fail_white;
    }

    public PositiveRefresherWithText(boolean isWhite){
        if(isWhite){
            layoutId=R.layout.positive_refresher_qq;
            refreshSuccessId=R.drawable.refresh_success_white;
            refreshFailId=R.drawable.refresh_fail_white;
        }else{
            layoutId=R.layout.positive_refresher_whiteback;
            refreshSuccessId=R.drawable.refresh_success_gray;
            refreshFailId=R.drawable.refresh_fail_gray;
        }
    }


    @Override
    public View getView(Context context, ViewGroup viewGroup) {
        if(null == view){
            view= LayoutInflater.from(context).inflate(layoutId,viewGroup,false);
            arrow=(ImageView)view.findViewById(R.id.positve_arrow_qq_img);
            progress = (ImageView)view.findViewById(R.id.positve_arrow_qq_pro);
            textView =(TextView)view.findViewById(R.id.positve_arrow_qq_text);
            state=(ImageView)view.findViewById(R.id.qq_refresh_state);
            if(isSuccess){
                state.setImageResource(refreshSuccessId);
            }else{
                state.setImageResource(refreshFailId);
            }
        }
        return view;
    }

    private int getHeight(){
        if(0 == height && null != view){
            height=view.getMeasuredHeight();
        }
        return height;
    }

    @Override
    public void onDrag(float offset) {
        if(getHeight() == 0){
            return;
        }
        state.setVisibility(View.GONE);
        arrow.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        if(offset <= (height+getOverlayOffset())){
            textView.setText("下拉刷新");
            arrow.setRotationX(0);
        }else{
            textView.setText("释放立即刷新");
            arrow.setRotationX(180);
        }
    }

    public void setRefreshCompleteState(boolean isSuccess){
        this.isSuccess=isSuccess;
        if(null == state){
            return;
        }
        if(isSuccess){
            state.setImageResource(refreshSuccessId);
        }else{
            state.setImageResource(refreshFailId);
        }

    }

    public void setRefreshCompleteText(String text){
        if(TextUtils.isEmpty(text)){
            return;
        }
        compleText=text;
        if(null != textView){
            textView.setText(compleText);
        }
    }

    @Override
    public boolean canRefresh(float offset) {
        if(getHeight() == 0){
            return false;
        }
        return offset >= height;
    }

    @Override
    public float getOverlayOffset() {
        return 0;
    }

    @Override
    public boolean onStartRefresh() {
        onStartRefreshInner(1500);
        return false;
    }

    protected void onStartRefreshInner(long duration){
        if(null == arrow || null == progress){
            return;
        }
        arrow.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        state.setVisibility(View.GONE);
        if(null == animator){
            animator=ObjectAnimator.ofFloat(progress,"rotation",360);
            animator.setDuration(duration);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setInterpolator(new LinearInterpolator());
        }
        if(null != textView){
            textView.setText("正在刷新...");
        }
        if(animator.isRunning()){
            return;
        }
        animator.start();
    }

    @Override
    public void onStopRefresh() {
        if(null != animator){
            animator.end();
        }
        if(null == progress || null == arrow){
            return;
        }
        progress.setVisibility(View.GONE);
        arrow.setVisibility(View.VISIBLE);
    }

    @Override
    public long onRefreshComplete() {
        if(null != textView){
            textView.setText(compleText);
        }
        if(null != progress){
            progress.setVisibility(View.GONE);
        }
        if(null != arrow){
            arrow.setVisibility(View.GONE);
        }
        if(null != state){
            state.setVisibility(View.VISIBLE);
        }
        if(null != animator){
            animator.end();
        }
        return 300;
    }
}
