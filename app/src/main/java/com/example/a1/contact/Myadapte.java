package com.example.a1.contact;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.List;
import java.util.zip.Inflater;

import static android.R.attr.data;
import static android.R.attr.targetActivity;
import static android.content.ContentValues.TAG;

/**
 * Created by 1 on 2017/4/19.
 */

public class Myadapte extends BaseAdapter implements AbsListView.OnScrollListener{
    private LayoutInflater minflater;
    private List<NewData> dataList;
    private Imageloader mimageloader;//保证只有一个LruCache 不用每次创建一个新的LruCache
    private int mstart,mend;
   private MainActivity m;
    public static String[] URLS;//取出全部图片url
    private boolean mFirstIn;
   public Myadapte(Context context, List<NewData> dataList, ListView listView){
       mimageloader = new Imageloader(listView);
        this.dataList = dataList;
       minflater = LayoutInflater.from(context);
       //把得到的所有图片地址 添加到数组里面  后面只显示本页的时候用
       URLS = new String[dataList.size()];
       for (int i = 0; i < dataList.size(); i++) {
          URLS[i] = dataList.get(i).iconurl;//给数组里面添加URL值

       }
       mFirstIn = true;
       //注册监听滑动事件
       listView.setOnScrollListener(this);
   }
    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder v = null;
       NewData m = dataList.get(position);
        if (convertView ==null){
            v = new ViewHolder();
            convertView =  minflater.inflate(R.layout.itme,parent,false);
            v.textView = (TextView) convertView.findViewById(R.id.id_text);
            v.imageView = (ImageView) convertView.findViewById(R.id.id_image);
            v.titleView = (TextView) convertView.findViewById(R.id.id_title);
            convertView.setTag(v);

        }else {
            v= (ViewHolder) convertView.getTag();
        }
        v.titleView.setText(dataList.get(position).title);
        v.textView.setText(dataList.get(position).conten);
        String url = dataList.get(position).iconurl;
        v.imageView.setTag(url);
//      v.imageView.setImageResource(R.mipmap.ic_launcher);//与showImageByasyncTask中的方法重复
//        new Imageloader().showImageByThread(v.imageView,url);
      mimageloader.showImageByasyncTask(v.imageView,url);

        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //滑动停止的时候
        if (scrollState == SCROLL_STATE_IDLE){
            //加载可见项
            mimageloader.loadImages(mstart,mend);

        }
        
        else {
            //停止所有任务
            mimageloader.cancelAllTast();

        }
        if (view.getLastVisiblePosition() == (view.getCount()-1)) {//滑动到底部
//        System.exit(0); 退出


            Toast.makeText(MainActivity.mactivity,"已到达底部！！！",Toast.LENGTH_LONG).show();




        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        mstart = firstVisibleItem;
        mend = firstVisibleItem + visibleItemCount;
        if (mFirstIn==true && visibleItemCount>0){//第一次加载手动加载
            mimageloader.loadImages(mstart,mend);
            mFirstIn = false;
        }
    }

    class ViewHolder{
        TextView textView,titleView;
        ImageView imageView;

    }
}
