package com.zenbarrier.guessthecelebrity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    List<Celebrity> celebrities;
    ImageView celebImageView;

    class Celebrity{
        private URL celebImageUrl;
        private String celebName;

        Celebrity(String name, String imageUrl){
            try {
                celebImageUrl = new URL(imageUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            celebName = name;
        }

        String getCelebrityName(){
            return celebName;
        }

        void setCelebrityBitmap(){

            class GetBitmap extends AsyncTask<Void, Void, Bitmap>{
                @Override
                protected Bitmap doInBackground(Void... params) {

                    try {
                        InputStream inputStream = (InputStream)celebImageUrl.getContent();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                        return bitmap;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);

                    celebImageView.setImageBitmap(bitmap);
                }
            }

            GetBitmap getBitmap = new GetBitmap();
            getBitmap.execute();
        }
    }

    public void answerClick(View view){
        String answer = view.getTag().toString();
        String text = ((Button)view).getText().toString();
        if(answer.equals(text)){
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Wrong! It's "+answer, Toast.LENGTH_SHORT).show();
        }
        createCelebrityQuestion();
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
                        String name = m.group(2);
                        String imageUrl = m.group(1);
                        celebrities.add(new Celebrity(name, imageUrl));
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
            createCelebrityQuestion();
        }
    }

    void extractCelebrities(){
        celebrities = new ArrayList<>();
        String siteName = "http://www.posh24.com/celebrities";
        BuildCelebrityList buildCelebrityList = new BuildCelebrityList();

        buildCelebrityList.execute(siteName);
    }

    void createCelebrityQuestion(){
        Collections.shuffle(celebrities);
        List<Celebrity> questionSet = celebrities.subList(0,4);

        Celebrity answer;

        answer = questionSet.get(0);
        answer.setCelebrityBitmap();

        Collections.shuffle(questionSet);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        for(int i = 0, j = 0; i < linearLayout.getChildCount(); i++){
            View view = linearLayout.getChildAt(i);
            if(view instanceof Button){
                Celebrity celeb = questionSet.get(j);
                ((Button) view).setText(celeb.getCelebrityName());
                view.setTag(answer.celebName);
                j++;
            }
        }

        Button celebButton1 = (Button)findViewById(R.id.celebButton1);
        celebButton1.setText(questionSet.get(0).getCelebrityName());
        Button celebButton2 = (Button)findViewById(R.id.celebButton2);
        celebButton2.setText(questionSet.get(1).getCelebrityName());
        Button celebButton3 = (Button)findViewById(R.id.celebButton3);
        celebButton3.setText(questionSet.get(2).getCelebrityName());
        Button celebButton4 = (Button)findViewById(R.id.celebButton4);
        celebButton4.setText(questionSet.get(3).getCelebrityName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        celebImageView = (ImageView)findViewById(R.id.celebrityImageView);

        extractCelebrities();
    }
}
