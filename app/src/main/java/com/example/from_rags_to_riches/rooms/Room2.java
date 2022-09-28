package com.example.from_rags_to_riches.rooms;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.from_rags_to_riches.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Room2 extends AppCompatActivity {

    private String stringResponse;

    private class MyExit2Thread implements Runnable{

        @Override
        public void run() {
            Log.e("Отслеживаю потоки", "поток - " + Thread.currentThread() + Thread.currentThread().getName());
            String stringUrl = getString(R.string.server) + "exit2";
            JSONObject register = new JSONObject();
            try {
                register.put("nickname", getIntent().getStringExtra("nickname"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                URL url = new URL(stringUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");

                OutputStream os = conn.getOutputStream();
                os.write(register.toString().getBytes(StandardCharsets.UTF_8));
                os.close();

                InputStream response = conn.getInputStream();
                Scanner s = new Scanner(response).useDelimiter("\\A");
                stringResponse = s.hasNext() ? s.next() : "";
                System.out.println("  _-_-_response_-_-_  " + stringResponse);

                response.close();
                conn.disconnect();
            } catch (Exception e) {
                System.out.println(e);
                Log.e("______________________________________", String.valueOf(e));
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room2);


    }

    @Override
    public void onPause() {
        super.onPause();
        Thread myThread = new Thread(new MyExit2Thread());
        myThread.start();
        try {
            myThread.join();
            Log.e("Отслеживаю потоки", "Мы снова в мэйне " + Thread.currentThread());
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

}
