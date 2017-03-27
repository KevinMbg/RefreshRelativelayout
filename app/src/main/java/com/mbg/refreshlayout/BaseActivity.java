package com.mbg.refreshlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mbg.library.RefreshRelativeLayout;

/**
 * Created by Administrator on 2017/3/24.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RefreshRelativeLayout refreshRelativeLayout;
    private LinearLayout linearLayout;
    private boolean isInit=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isHorizontal()){
            super.setContentView(R.layout.activity_horizontal_base);
        }else {
            super.setContentView(R.layout.activity_base);
        }
        initToolbar();
        initView();
    }
    protected boolean isHorizontal(){
        return false;
    }

    private void initToolbar(){
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getActivityTitle());
        setSupportActionBar(toolbar);
    }

    private void initView(){
        linearLayout=(LinearLayout)findViewById(R.id.base_parent);
        refreshRelativeLayout=(RefreshRelativeLayout)findViewById(R.id.base_refresh);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view= LayoutInflater.from(this).inflate(layoutResID,linearLayout,false);
        refreshRelativeLayout.addView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onMenuClick(item);
        return true;
    }
    public void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    private void onMenuClick(MenuItem item){
        int menuItemId=item.getItemId();
        if(refreshRelativeLayout.isRefreshing()){
            showToast("请等待刷新完成执行其他操作");
            return;
        }
        if(item.isChecked()){
            item.setChecked(false);
        }else{
            item.setChecked(true);
        }
        boolean ischecked=item.isChecked();
        switch (menuItemId){
            case R.id.menu_positive_enable:
                refreshRelativeLayout.setPositiveEnable(ischecked);
                break;
            case R.id.menu_negative_enable:
                refreshRelativeLayout.setNegativeEnable(ischecked);
                break;
            case R.id.menu_positive_drag_enable:
                refreshRelativeLayout.setPositiveDragEnable(ischecked);
                break;
            case R.id.menu_negative_drag_enable:
                refreshRelativeLayout.setNegativeDragEnable(ischecked);
                break;
            case R.id.menu_positive_overlay_used:
                refreshRelativeLayout.setPositiveOverlayUsed(ischecked);
                break;
            case R.id.menu_negative_overlay_used:
                refreshRelativeLayout.setNegativeOverlayUsed(ischecked);
                break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(!isInit) {
            isInit=true;
            MenuItem positiveEnable = menu.findItem(R.id.menu_positive_enable);
            positiveEnable.setChecked(setPositiveEnableChecked());
            MenuItem negativeEnable = menu.findItem(R.id.menu_negative_enable);
            negativeEnable.setChecked(setNegativeEnableChecked());
            MenuItem positiveDragEnable = menu.findItem(R.id.menu_positive_drag_enable);
            positiveDragEnable.setChecked(setPositiveDragEnableChecked());
            MenuItem negativeDragEnable = menu.findItem(R.id.menu_negative_drag_enable);
            negativeDragEnable.setChecked(setNegativeDragEnableChecked());
            MenuItem positiveOverlayUsed = menu.findItem(R.id.menu_positive_overlay_used);
            positiveOverlayUsed.setChecked(setPositiveOverlayUsedChecked());
            MenuItem negativeOverlayUsed = menu.findItem(R.id.menu_negative_overlay_used);
            negativeOverlayUsed.setChecked(setNegativeOverlayUsedChecked());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    protected boolean setPositiveEnableChecked(){
        return true;
    }

    protected boolean setNegativeEnableChecked(){
        return true;
    }

    protected boolean setPositiveDragEnableChecked(){
        return true;
    }

    protected boolean setNegativeDragEnableChecked(){
        return false;
    }

    protected boolean setPositiveOverlayUsedChecked(){
        return true;
    }

    protected boolean setNegativeOverlayUsedChecked(){
        return false;
    }

    protected RefreshRelativeLayout getRefreshRelativeLayout(){
        return refreshRelativeLayout;
    }

    protected abstract String getActivityTitle();

}
