package com.mbg.refreshlayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;

import com.mbg.library.IRefreshListener;
import com.mbg.library.RefreshRelativeLayout;
import com.mbg.refreshlayout.adapter.MorePicAdapter;

public class MorePageActivity extends BaseActivity {

    private RefreshRelativeLayout refreshRelativeLayout;
    private RecyclerView recyclerView;
    private MorePicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_page);
        initView();
    }

    private void initView(){
        refreshRelativeLayout=getRefreshRelativeLayout();
        refreshRelativeLayout.addRefreshListener(new IRefreshListener() {
            @Override
            public void onPositiveRefresh() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(null != adapter){
                            adapter.setDataList();
                        }
                        refreshRelativeLayout.positiveRefreshComplete();
                    }
                },1000);
            }

            @Override
            public void onNegativeRefresh() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(null != adapter){
                            adapter.addDataList();
                        }
                        refreshRelativeLayout.negativeRefreshComplete();
                    }
                },1000);
            }
        });
        recyclerView=(RecyclerView)findViewById(R.id.more_page_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter=new MorePicAdapter();
        recyclerView.setAdapter(adapter);
        refreshRelativeLayout.startPositiveRefresh();
    }

    @Override
    protected String getActivityTitle() {
        return "查看更多";
    }

    public static void skipToMoreInfo(Context context){
        Intent intent= new Intent(context,MorePageActivity.class);
        context.startActivity(intent);
    }
}
