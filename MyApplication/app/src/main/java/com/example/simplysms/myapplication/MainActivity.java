package com.example.simplysms.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    TextView securityCode,expiration;

    private static final int MILLISECONDS = 1000;
    private static final int SECONDS = 60;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        securityCode = (TextView) findViewById(R.id.editText);
        expiration = (TextView) findViewById(R.id.alive);

        securityCode.post(new Runnable() {
            public void run() {
                new MyAsyncTask().execute();
                myTimer.start();
                securityCode.postDelayed(this, MILLISECONDS*SECONDS); //now is every 2 minutes
            }
        });


    }


    CountDownTimer myTimer = new CountDownTimer(SECONDS*MILLISECONDS, 500) {

        public void onTick(long millisUntilFinished) {
            expiration.setText("seconds remaining: " + millisUntilFinished / MILLISECONDS);
        }

        public void onFinish() {
            expiration.setText("Expired!");
        }

    };

    public String  postData() {
        HttpClient httpclient = new DefaultHttpClient();
        // specify the URL you want to post to
        HttpGet httppost = new HttpGet("http://192.168.1.9:8000/getcode");
        try {

            // send the variable and value, in other words post, to the URL
            HttpResponse response = httpclient.execute(httppost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String json = reader.readLine();
            JSONObject rootObj = new JSONObject(json);
            if(rootObj.has("code")) {
                return rootObj.getString("code");
            }
        } catch (ClientProtocolException e) {
            // process execption
        } catch (IOException e) {
            // process execption
            e.printStackTrace();
        } catch (JSONException e) {
            // process execption
            e.printStackTrace();
        }
        return null;
    }



    private class MyAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return postData();
        }

        protected void onPostExecute(String result){
            if(result!=null)
                securityCode.setText(result);
        }

        protected void onProgressUpdate(Integer... progress){
        }
    }
}
