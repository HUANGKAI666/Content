package com.example.a1.contact;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;

import static android.R.attr.bitmap;


/**
 * Created by 1 on 2017/4/19.
 */

public class Imageloader  {
    private ImageView mImageView;
    private String murl;
    private ListView mlistView;
    private Set<asyncTask> mtasks;
    //创建cache缓存机制  实际上是一个Map 键值对
    private LruCache<String,Bitmap> mCache;
    public  Imageloader(ListView listview){
        mlistView =listview;
        mtasks = new HashSet<>();
        //获得最大使用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        mCache = new LruCache<String,Bitmap>(maxMemory/4){//使用最大内春的四分之一
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();//在每次存入缓存的时候调用。返回value值的大小
            }
        };

    }
    //吧数据增加到缓存cache  可以把cache看成是一个Map
    public  void  addBitmapToCache(String url,Bitmap bitmap){
        if (getBitmapFromeCache(url)==null){
            mCache.put(url,bitmap);
        }
    }
    //从缓存中得到数据
    public   Bitmap getBitmapFromeCache(String url){
    return  mCache.get(url);
    }


//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            if (mImageView.getTag().equals(murl)){
//                mImageView.setImageBitmap((Bitmap) msg.obj);
//
//            }
//        }
//    };
//
//    public  void  showImageByThread(ImageView imageView, final String url){
//        mImageView = imageView;
//        murl = url;
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                Bitmap bitmap = getBitmapFromUrl(url);
//                //子线程呢不能改变UI 必须返回主线程  使用通知回去
//                Message message = Message.obtain();
//                message.obj = bitmap;
//                mHandler.sendMessage(message);
//
//            }
//        }.start();
//
//    }
    //Bitmap是Android系统中的图像处理的最重要类之一。用它可以获取图像文件信息，
    public Bitmap getBitmapFromUrl(String urlString){
        Bitmap bitmap;
        InputStream is =  null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream i= con.getInputStream();
            is = new BufferedInputStream(i);//对流添加一些功能
            bitmap = BitmapFactory.decodeStream(is);//把流转换为bitmap
            con.disconnect();//关闭con
            return  bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return  null;
    }
//加载statr 到end图片 滑动停止的时候加载调用 加载本个页面的信息
    public  void loadImages(int start,int end){
        for (int i = start; i < end; i++) {
            String url = Myadapte.URLS[i];//的到每一个图片的url
            Bitmap bitmap = getBitmapFromeCache(url);
            if (bitmap==null){//缓存中没有数据
                asyncTask task = new asyncTask(url);
               task.execute(url);
                mtasks.add(task);//存到集合里面 统一做管理
            }else {
                ImageView imageView = (ImageView) mlistView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);

            }

        }


    }
    //使用asynctask加载url
    public  void  showImageByasyncTask(ImageView imageView,String url) {
        Bitmap bitmap = getBitmapFromeCache(url);
        if (bitmap==null){//缓存中没有数据
           imageView.setImageResource(R.mipmap.ic_launcher);
    }
 else {
            imageView.setImageBitmap(bitmap);//显示头先loadimage缓存的图片

        }

    }

    public void cancelAllTast() {//取消所有加载的
        if (mtasks!=null){
            for (asyncTask task:mtasks){
                task.cancel(false);
            }

        }

    }

    class  asyncTask extends AsyncTask<String,Void,Bitmap> {
//         ImageView mImage;
         String mUrl;
         public asyncTask(String url){
//             mImage = imageView;
             mUrl = url;

         }

         // 将在onPreExecute 方法执行后马上执行，该方法运行在后台线程中。这里将主要负责执行那些很耗时的后台计算工作。
         @Override
         protected Bitmap doInBackground(String... params) {
             String url = params[0];
             Bitmap bitmap = getBitmapFromUrl(url);// params[0]代表连接的url  下载图片
             if (bitmap!=null){
                 addBitmapToCache(url,bitmap);//将网络中获取的图片添加到缓存中

             }
            return bitmap;

         }

         //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
         @Override
         protected void onPostExecute(Bitmap bitmap) {
             super.onPostExecute(bitmap);


             ImageView imageView = (ImageView) mlistView.findViewWithTag(mUrl);
             if (imageView!=null && bitmap!=null){
                 imageView.setImageBitmap(bitmap);
             }

             mtasks.remove(this);



         }
     }

     }




