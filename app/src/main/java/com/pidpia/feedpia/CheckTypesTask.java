package com.pidpia.feedpia;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by jenorain on 2017-02-13.
 */

public class CheckTypesTask extends AsyncTask<Void, Void, Void> {

    ProgressDialog asyncDialog ;
    CheckTypesTask(Context context){
        asyncDialog = new ProgressDialog(context);

    }
    @Override
    protected void onPreExecute() {
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("로딩중입니다..");

        // show dialog
        asyncDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            for (int i = 0; i < 5; i++) {
                //asyncDialog.setProgress(i * 30);
                Thread.sleep(300);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        asyncDialog.dismiss();
        super.onPostExecute(result);
    }

}
