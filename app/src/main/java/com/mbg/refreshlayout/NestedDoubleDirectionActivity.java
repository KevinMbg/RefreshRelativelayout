package com.mbg.refreshlayout;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mbg.library.IRefreshListener;
import com.mbg.library.RefreshRelativeLayout;
import com.mbg.refreshlayout.adapter.DoubleAdapter;

import java.util.ArrayList;
import java.util.List;

public class NestedDoubleDirectionActivity extends BaseActivity {

    private RefreshRelativeLayout refreshRelativeLayout;
    private RecyclerView recyclerView;
    private DoubleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_refresh);
        initView();
    }

    private void initView(){
        refreshRelativeLayout=getRefreshRelativeLayout();
        refreshRelativeLayout.addRefreshListener(new IRefreshListener() {
            @Override
            public void onPositiveRefresh() {
                positiveRefresh();
            }

            @Override
            public void onNegativeRefresh() {
                negativeRefresh();
            }
        });
        recyclerView=(RecyclerView)findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter=new DoubleAdapter(this);
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


    private void positiveRefresh(){
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setDataList(getList());
                refreshRelativeLayout.positiveRefreshComplete();
            }
        },1000);
    }

    private void negativeRefresh(){
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.addDataList(getList());
                refreshRelativeLayout.negativeRefreshComplete();
            }
        },1000);
    }

    @Override
    protected String getActivityTitle() {
        return "双向嵌套";
    }


}
