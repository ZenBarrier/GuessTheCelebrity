package com.zenbarrier.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

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

    void extractCelebrities(){

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
