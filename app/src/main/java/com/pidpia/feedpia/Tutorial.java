package com.pidpia.feedpia;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Tutorial extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ImageView tutorial_join = (ImageView) findViewById(R.id.img_tutorial_join);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tutorial, container, false);

           // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            ImageView tutorial_img = (ImageView) rootView.findViewById(R.id.img_tutorial_back);
            ImageView tutorial_join = (ImageView) rootView.findViewById(R.id.img_tutorial_join);

            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case 1 :

                    tutorial_img.setImageResource(R.drawable.tuto1);
                    tutorial_join.setVisibility(rootView.GONE);
                    break;
                case 2 :
                    tutorial_img.setImageResource(R.drawable.tuto2);
                    tutorial_join.setVisibility(rootView.GONE);
                    break;
                case 3 :
                    tutorial_img.setImageResource(R.drawable.tuto3);
                    tutorial_join.setVisibility(rootView.GONE);
                    break;
                case 4 :
                    tutorial_img.setImageResource(R.drawable.tuto4);
                    tutorial_join.setVisibility(rootView.GONE);
                    break;
                case 5 :
                    tutorial_img.setImageResource(R.drawable.tuto5);
                    tutorial_join.setVisibility(rootView.GONE);
                    break;
                case 6 :
                    tutorial_img.setImageResource(R.drawable.tuto6);
                    tutorial_join.setVisibility(rootView.VISIBLE);

                    break;
            }

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 6:
                    ImageView tutorial_join = (ImageView)findViewById(R.id.img_tutorial_join);
                    tutorial_join.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
//                    final WebView tuto_web = (WebView)findViewById(R.id.tuto_web);
//                    final ProgressBar tuto_pro = (ProgressBar)findViewById(R.id.tuto_progress);
//
//                    tutorial_join.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            tuto_web.setVisibility(View.VISIBLE);
//
//
//                            WebSettings webSettings = tuto_web.getSettings();
//                            webSettings.setJavaScriptEnabled(true);
//
//                            tuto_web.setWebViewClient(new WebViewClient() {
//                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                                    view.loadUrl(url);
//                                    return true;
//                                }
//
//                                public void onPageStarted(WebView view, String url,
//                                                          android.graphics.Bitmap favicon) {
//                                    super.onPageStarted(view, url, favicon);
//                                    tuto_pro.setVisibility(View.VISIBLE);
//                                }
//
//
//                                public void onPageFinished(WebView view, String url) {
//                                    super.onPageFinished(view, url);
//                                    tuto_pro.setVisibility(View.INVISIBLE);
//                                }
//
//
//                                public void onReceivedError(WebView view, int errorCode,
//                                                            String description, String failingUrl) {
//                                    super.onReceivedError(view, errorCode, description, failingUrl);
//                                    Toast.makeText(Tutorial.this, "로딩오류" + description,
//                                            Toast.LENGTH_SHORT).show();
//                                }
//
//                            });
//
//                            tuto_web.setWebChromeClient(new WebChromeClient() {
//
//                                @Override
//                                public void onProgressChanged(WebView view, int newProgress) {
//                                    tuto_pro.setProgress(newProgress);
//                                }
//
//                                public boolean onJsAlert(WebView view, String url,
//                                                         String message, final android.webkit.JsResult result) {
//                                    new AlertDialog.Builder(Tutorial.this)
//                                            .setTitle("알림")
//                                            .setMessage(message)
//                                            .setPositiveButton(android.R.string.ok,
//                                                    new AlertDialog.OnClickListener() {
//                                                        public void onClick(
//                                                                DialogInterface dialog,
//                                                                int which) {
//                                                            result.confirm();
//                                                        }
//                                                    }).setCancelable(false).create().show();
//
//                                    return true;
//                                }
//
//                                ;
//
//                                public boolean onJsConfirm(WebView view, String url,
//                                                           String message, final android.webkit.JsResult result) {
//                                    new AlertDialog.Builder(Tutorial.this)
//                                            .setTitle("확인")
//                                            .setMessage(message)
//                                            .setPositiveButton(android.R.string.ok,
//                                                    new AlertDialog.OnClickListener() {
//                                                        public void onClick(
//                                                                DialogInterface dialog,
//                                                                int which) {
//                                                            result.confirm();
//                                                        }
//                                                    })
//                                            .setNegativeButton(android.R.string.cancel,
//                                                    new AlertDialog.OnClickListener() {
//                                                        public void onClick(
//                                                                DialogInterface dialog,
//                                                                int which) {
//                                                            result.cancel();
//                                                        }
//                                                    }).setCancelable(false).create().show();
//                                    return true;
//                                }
//
//                            });
//                            Tutorial.addJavascriptInterface(new Tutorial().AndroidBridge(), "Android");
//                            login_webview.loadUrl("http://110.10.189.232/Join.php");
//
//                        }
//                    });
                                    }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {

            }
            return null;
        }
    }
}
