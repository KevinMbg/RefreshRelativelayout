package com.mbg.refreshlayout;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mbg.library.DefaultNegativeRefreshers.NegativeRefresherWithNodata;
import com.mbg.library.DefaultPositiveRefreshers.PositiveRefresherWithText;
import com.mbg.library.IRefreshListener;
import com.mbg.library.RefreshRelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class NestedRecyclerViewActivity extends BaseActivity {

    private RefreshRelativeLayout refreshRelativeLayout;
    private RecyclerView recyclerView;
    private RefreshAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_refresh);
        initView();
    }

    private void initView(){
        refreshRelativeLayout=getRefreshRelativeLayout();
        refreshRelativeLayout.setBackgroundColor(Color.parseColor("#313031"));
        refreshRelativeLayout.setNegativeRefresher(new NegativeRefresherWithNodata(true));
        refreshRelativeLayout.setPositiveRefresher(new PositiveRefresherWithText(true));
        refreshRelativeLayout.setPositiveOverlayUsed(false);
        recyclerView=(RecyclerView)findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mAdapter=new RefreshAdapter();
        recyclerView.setAdapter(mAdapter);
        refreshRelativeLayout.addRefreshListener(new IRefreshListener() {
            @Override
            public void onPositiveRefresh() {
                onActivityPositiveRefresh();
            }

            @Override
            public void onNegativeRefresh() {
                onActivityNegativeRefresh();
            }
        });
        refreshRelativeLayout.startPositiveRefresh();
    }

    @Override
    protected boolean setPositiveOverlayUsedChecked() {
        return false;
    }

    private void onActivityPositiveRefresh(){
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.setDataList(getList());
                refreshRelativeLayout.positiveRefreshComplete();
            }
        },1000);
    }


     private List<Integer> getList(){
         List<Integer> data=new ArrayList<>();
         for(int i =0 ; i< 10;i++){
             data.add(i);
         }
         return data;
     }

    private void onActivityNegativeRefresh(){
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.addList(getList());
                refreshRelativeLayout.negativeRefreshComplete();
            }
        },1000);
    }

    @Override
    protected String getActivityTitle() {
        return "嵌套RecyclerView";
    }

    class RefreshAdapter extends RecyclerView.Adapter<MyViewHolder>{

        private List<Integer> dataList;

        public RefreshAdapter(){
            dataList=new ArrayList<>();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_layout,parent,false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.textView.setText((position+1)+"");
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public void addList(List<Integer> list){
            if(null == list){
                return;
            }
            dataList.addAll(list);
            notifyDataSetChanged();
        }

        public void setDataList(List<Integer> list){
            if(null == list){
                return;
            }
            dataList = list;
            notifyDataSetChanged();
        }
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        public MyViewHolder(View itemView) {
            super(itemView);
            textView=(TextView)itemView.findViewById(R.id.recycler_item_text);
        }
        public TextView textView;
    }

}
