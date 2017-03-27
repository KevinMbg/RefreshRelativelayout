package com.mbg.refreshlayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mbg.library.DefaultNegativeRefreshers.HorizontalLoadMore;
import com.mbg.library.IRefreshListener;
import com.mbg.library.RefreshRelativeLayout;
import com.mbg.refreshlayout.adapter.HorizontalAdapter;

import java.util.ArrayList;
import java.util.List;

public class NestedHorizontalRecyclerActivity extends BaseActivity {

    private RefreshRelativeLayout refreshRelativeLayout;
    private RecyclerView recyclerView;
    private HorizontalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_horizontal_recycler);
        initView();
    }

    @Override
    protected boolean setNegativeOverlayUsedChecked() {
        return false;
    }

    @Override
    protected boolean setNegativeDragEnableChecked() {
        return true;
    }

    private void initView(){
        refreshRelativeLayout=getRefreshRelativeLayout();
        refreshRelativeLayout.setNegativeDragEnable(true);
        refreshRelativeLayout.setNegativeOverlayUsed(false);
        refreshRelativeLayout.setNegativeRefresher(new HorizontalLoadMore());
        refreshRelativeLayout.setAnimateDuration(500);
        refreshRelativeLayout.addRefreshListener(new IRefreshListener() {
            @Override
            public void onPositiveRefresh() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setDataList(getList());
                        refreshRelativeLayout.positiveRefreshComplete();
                    }
                },1000);
            }

            @Override
            public void onNegativeRefresh() {
                Intent intent=new Intent(NestedHorizontalRecyclerActivity.this,MorePageActivity.class);
                startActivity(intent);
            }
        });
        recyclerView=(RecyclerView)findViewById(R.id.horizontal_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        adapter=new HorizontalAdapter();
        recyclerView.setAdapter(adapter);
        refreshRelativeLayout.startPositiveRefresh();
    }

    private List<Integer> getList(){
        List<Integer> data=new ArrayList<>();
        for(int i =0 ; i< 10;i++){
            data.add(i);
        }
        return data;
    }

    @Override
    protected boolean isHorizontal() {
        return true;
    }

    @Override
    protected String getActivityTitle() {
        return "水平RecyclerView";
    }

}
