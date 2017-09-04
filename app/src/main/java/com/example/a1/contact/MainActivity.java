package com.example.a1.contact;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class MainActivity extends AppCompatActivity {
    private static  String URL = "http://www.imooc.com/api/teacher?type=4&num=30";
    private   ListView listview;
    private static ListView listView2;
    private List<NewData> listdata;
    public static MainActivity mactivity;//myadapter中tost中要使用本activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_main);
        new NewAsyncTask().execute(URL);//调用异步加载传入URL
        listview = (ListView) findViewById(R.id.id_listview);
        mactivity=this;


    }
    //将url对应的json数据转化为我们所封装的newdata对象
    private List<NewData> getjsondata(String url) {
        listdata = new ArrayList<NewData>();
      NewData newData;
        InputStream is = null;
        try {
//            InputStream is = new URL(url).openStream();//直接从url'获取流了
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
//            URLConnection con = new URL(url).openConnection();
             is = con.getInputStream();
            String jsonstring = readStream(is);
            JSONObject jsonObject;

            jsonObject  = new JSONObject(jsonstring);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i=0;i<jsonArray.length();i++){
                jsonObject = jsonArray.getJSONObject(i);
                newData = new NewData();
                newData.iconurl = jsonObject.getString("picSmall");
                newData.conten = jsonObject.getString("description");
                newData.title = jsonObject.getString("name");
                listdata.add(newData);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return  listdata;
    }
/**
 * 通过InputStream解析网页返回的数据
 *
 */

    private String readStream(InputStream is) {
        String result ="";
        InputStreamReader isr;
        String line="";
        try {
//            isr = new InputStreamReader(is,"utf-8");//字节流变成字符流
//            BufferedReader br = new BufferedReader(isr);//从字符输入流中读取文本，缓冲各个字符，从而提供字符、数组和行的高效读取。
           BufferedReader br =new BufferedReader(new InputStreamReader(is,"utf-8"));
            while ((line = br.readLine())!=null){
                result += line;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;
    }

    //实现网络的异步访问
    //　Params 启动任务执行的输入参数，比如HTTP请求的URL。
    //Progress 后台任务执行的百分比。
    // 　　Result 后台执行任务最终返回的结果，比如String。
    class  NewAsyncTask extends  AsyncTask<String,Void,List<NewData>>{

      @Override
      protected List<NewData> doInBackground(String... params) {
            return getjsondata(params[0]);
        }

        //生成的listdata设置给listview  onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(List<NewData> dataList) {//对ui操作
            super.onPostExecute(dataList);

            Myadapte my = new Myadapte(MainActivity.this,listdata,listview);
            listview.setAdapter(my);



        }
    }

}
