package com.zenbarrier.guessthecelebrity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    class BuildCelebrityList extends AsyncTask<String,Void,Void>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... urls) {
            String pattern = "src=\"(.*?)\"\\s*alt=\"(.*?)\"";
            Pattern p = Pattern.compile(pattern);
            String celebrityWebsite = urls[0];
            try {
                URL website = new URL(celebrityWebsite);

                BufferedReader in = new BufferedReader(new InputStreamReader(website.openStream()));
                String str= in.readLine();
                while (str  != null)
                {
                    Log.i("Web",str);
                    Matcher m = p.matcher(str);
                    if(m.find()){
                        Log.i("Matched",m.group(1)+" name: "+m.group(2));
                        String imageUrl = m.group(1);
                        String name = m.group(2);
                        celebrities.add(new Celebrity(imageUrl, name));
                    }

                    str = in.readLine();
                }
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            progressDialog.dismiss();
        }
    }

    void extractCelebrities(){
        celebrities = new ArrayList<Celebrity>();
        String siteName = "http://www.posh24.com/celebrities";
        BuildCelebrityList buildCelebrityList = new BuildCelebrityList();

        buildCelebrityList.execute(siteName);
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
