package com.mbg.refreshlayout;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.mbg.library.DefaultNegativeRefreshers.NegativeRefresherWithNodata;
import com.mbg.library.DefaultPositiveRefreshers.PositiveRefresherWithText;
import com.mbg.library.IRefreshListener;
import com.mbg.library.RefreshRelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class NestedListViewActivity extends BaseActivity {

    private ListView listView;
    private RefreshRelativeLayout refreshRelativeLayout;
    private ListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_list_view);
        initView();
    }
    private void initView(){
        refreshRelativeLayout=getRefreshRelativeLayout();
        refreshRelativeLayout.setPositiveRefresher(new PositiveRefresherWithText(false));
        refreshRelativeLayout.setNegativeRefresher(new NegativeRefresherWithNodata(false));
        refreshRelativeLayout.setNegativeOverlayUsed(true);
        refreshRelativeLayout.setPositiveOverlayUsed(false);
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
        listView=(ListView)findViewById(R.id.listView);
        adapter=new ListAdapter(this);
        listView.setAdapter(adapter);
        refreshRelativeLayout.startPositiveRefresh();
    }

    @Override
    protected boolean setPositiveOverlayUsedChecked() {
        return false;
    }

    @Override
    protected boolean setNegativeOverlayUsedChecked() {
        return true;
    }

    private void positiveRefresh(){
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setList();
                refreshRelativeLayout.positiveRefreshComplete();
            }
        },1000);
    }

    private void negativeRefresh(){
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                addList();
                refreshRelativeLayout.negativeRefreshComplete();
            }
        },1000);
    }

    private void setList(){
        if(null == list){
            list=new ArrayList<>();
        }else{
            list.removeAll(list);
        }
        for(int i=0;i<10;i++){
            HashMap<String,String> map=new HashMap<>();
            map.put("Title",i+"");
            list.add(map);
        }
        adapter.notifyDataSetChanged();
    }

    private void addList(){
        if(null == list){
            list=new ArrayList<>();
        }
        for(int len=list.size(),i=len;i<len+10;i++){
            HashMap<String,String> map=new HashMap<>();
            map.put("Title",i+"");
            list.add(map);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected String getActivityTitle() {
        return "嵌套ListView";
    }

    private ArrayList<HashMap<String, String>> list=new ArrayList<>();
    class ListAdapter extends SimpleAdapter{
        public ListAdapter(Context context){
            super(context,list,R.layout.recycler_item_layout,
                    new String[]{"Title"},new int[]{R.id.recycler_item_text});
        }
    }
}
