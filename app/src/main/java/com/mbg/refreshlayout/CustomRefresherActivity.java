package com.mbg.refreshlayout;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.mbg.library.IRefreshListener;
import com.mbg.library.RefreshRelativeLayout;
import com.mbg.refreshlayout.adapter.MorePicAdapter;
import com.mbg.refreshlayout.refreshers.LoadMoreWithNomoreRefresher;

public class CustomRefresherActivity extends BaseActivity {

    private RefreshRelativeLayout refreshRelativeLayout;
    private LoadMoreWithNomoreRefresher mRefresher;
    private RecyclerView recyclerView;
    private MorePicAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_refresh);
        initView();
    }

    private int loadMoreTime=0;

    private void initView(){
        refreshRelativeLayout=getRefreshRelativeLayout();
        mRefresher=new LoadMoreWithNomoreRefresher();
        refreshRelativeLayout.setNegativeRefresher(mRefresher);
        refreshRelativeLayout.addRefreshListener(new IRefreshListener() {
            @Override
            public void onPositiveRefresh() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadMoreTime =0;
                        mRefresher.setHasMore(true);
                        if(null != mAdapter){
                            mAdapter.setDataList();
                        }
                        refreshRelativeLayout.positiveRefreshComplete();
                    }
                },1000);
            }

            @Override
            public void onNegativeRefresh() {
                if(mRefresher.getHasMore()) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadMoreTime++;
                            if (null != mAdapter) {
                                mAdapter.addDataList();
                            }
                            refreshRelativeLayout.negativeRefreshComplete();
                            if (loadMoreTime > 2) {
                                mRefresher.setHasMore(false);
                            } else {
                                mRefresher.setHasMore(true);
                            }
                        }
                    }, 1000);
                }else{
                    refreshRelativeLayout.negativeRefreshComplete();

                }
            }
        });
        recyclerView=(RecyclerView)findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mAdapter=new MorePicAdapter();
        recyclerView.setAdapter(mAdapter);
        refreshRelativeLayout.startPositiveRefresh();

    }

    @Override
    protected String getActivityTitle() {
        return "自定义Refresher";
    }
}
