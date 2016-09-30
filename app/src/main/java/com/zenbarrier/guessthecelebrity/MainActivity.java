package com.zenbarrier.guessthecelebrity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    List<Celebrity> celebrities;

    class Celebrity{
        private URL celebImageUrl;
        private String celebName;

        public Celebrity(String name, URL imageUrl){
            celebImageUrl = imageUrl;
            celebName = name;
        }

        public Celebrity(String name, String imageUrl){
            try {
                celebImageUrl = new URL(imageUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            celebName = name;
        }

        public String getCelebrityName(){
            return celebName;
        }

        public Bitmap getCelebrityBitmap(){
            try {
                InputStream inputStream = (InputStream)celebImageUrl.getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void answerClick(View view){

    }

    class DownloadWebPage extends AsyncTask<String,Void,String>{

        ProgressDialog progDailog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection;
            String webPage = "";

            String celebrityWebsite = "http://www.posh24.com/celebrities";
            try {
                URL website = new URL(celebrityWebsite);

                BufferedReader in = new BufferedReader(new InputStreamReader(website.openStream()));
                String str= in.readLine();
                while (str  != null)
                {
                    Log.i("Web",str);

                    

                    str = in.readLine();
                }
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return webPage;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progDailog.dismiss();
        }
    }

    void extractCelebrities(){
        celebrities = new ArrayList<Celebrity>();
        String webPage;
        String siteName = "http://www.posh24.com/celebrities";

        DownloadWebPage downloadWebPage = new DownloadWebPage();

        downloadWebPage.execute(siteName);

    }

    void createCelebrityQuestion(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        extractCelebrities();
        createCelebrityQuestion();
    }
}
