package com.pidpia.feedpia;

/**
 * Created by jenorain on 2017-02-04.
 */


import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

class ImageLoad extends Thread {
    String url;
    Bitmap btimap_img;
    Context context;
    ImageView view;
    int msg_no;
    Handler mHandler;
    int msg;
    ProgressBar pb;
    boolean use_progressbar=false;

    FrameLayout frameLayout;
    boolean back=false;

    public ImageLoad(Context _context, ImageView _view) {
        context = _context;
        view = _view;
    }
    public ImageLoad(Context _context, ImageView _view,String _url) {
        context = _context;
        view = _view;
        url=_url;
    }
    public ImageLoad(Context _context, FrameLayout _view,String _url) {
        context = _context;
        frameLayout = _view;
        url=_url;
        back=true;
    }

    public ImageLoad(Context _context, ImageView _view, Handler _mHandler, int _msg) {
        context = _context;
        view = _view;
        mHandler = _mHandler;
        msg = _msg;
    }


    public ImageLoad(Context _context, ImageView _view,String _url, Handler _mHandler, int _msg) {
        context = _context;
        view = _view;
        mHandler = _mHandler;
        url=_url;
        msg_no = _msg;
    }


    public ImageLoad(Context _context, ImageView _view, ProgressBar _pb) {
        context = _context;
        view = _view;
        pb = _pb;
        use_progressbar=true;
    }

    public void run() {
        try {
            if(use_progressbar)pb.setVisibility(View.VISIBLE);
            URL urls = new URL(url);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;

            btimap_img = BitmapFactory.decodeStream(urls.openStream(), null, options);

            // btimap_img = BitmapFactory.decodeStream(urls.openStream());

            m_img_handler.sendEmptyMessage(0);
        } catch (Exception e) {
            //	Toast.makeText(context, "error2:" + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
    Handler m_img_handler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                //if(use_progressbar)pb.setVisibility(View.GONE);
                if(back && frameLayout !=null){
                    BitmapDrawable background = new BitmapDrawable(new Images(context).Round(btimap_img, 0));
                    frameLayout.setBackgroundDrawable(background);
                }else{

                    view.setImageBitmap(new Images(context).Round(btimap_img, 0));

                }
                if(mHandler != null){
                    mHandler.sendEmptyMessage(msg_no);
                }else{

                }

            } catch (Exception e) {

            }
        }
    };
}