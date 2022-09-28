package com.example.from_rags_to_riches;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.from_rags_to_riches.entriesActivity.RegisterActivity;
import com.example.from_rags_to_riches.rooms.Room2;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PlaygroundActivity extends AppCompatActivity {

    private String stringResponse;
    private String visitors;

    private class MyCount2Thread implements Runnable{

        @Override
        public void run() {
            Log.e("Отслеживаю потоки", "поток " + Thread.currentThread() + Thread.currentThread().getName());
            String url = getString(R.string.server) + "count2";
            Looper.prepare();
            try {
                URL obj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setRequestProperty("Accept-Charset", "UTF-8");

                InputStream response = conn.getInputStream();
                Scanner s = new Scanner(response).useDelimiter("\\A");
                stringResponse = s.hasNext() ? s.next() : "";
                Log.e("  _-_-_response_-_-_  ", stringResponse);

                response.close();
                conn.disconnect();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    private class MyEnter2Thread implements Runnable {

        @Override
        public void run() {
            Log.e("Отслеживаю потоки", "поток - " + Thread.currentThread() + Thread.currentThread().getName());
            String registerUrl = getString(R.string.server) + "enter2";
            JSONObject register = new JSONObject();
            try {
                register.put("nickname", getIntent().getStringExtra("nickname"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                URL url = new URL(registerUrl);
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
    private class MyVisitors2Thread implements Runnable {

        @Override
        public void run() {
            Log.e("Отслеживаю потоки", "поток " + Thread.currentThread() + Thread.currentThread().getName());
            String loginUrl = getString(R.string.server) + "visitors2";
            try {
                URL obj = new URL(loginUrl);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setRequestProperty("Accept-Charset", "UTF-8");

                InputStream response = conn.getInputStream();
                Scanner s = new Scanner(response).useDelimiter("\\A");
                stringResponse = s.hasNext() ? s.next() : "";
                Log.e("  _-_-_response_-_-_  ", stringResponse);

                response.close();
                conn.disconnect();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public String checkVisitors() {
        Thread myThread = new Thread(new MyCount2Thread());
        myThread.start();
        try {
            myThread.join();
            Log.e("Отслеживаю потоки", "Мы снова в мэйне " + Thread.currentThread());
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        return stringResponse;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);

        TextView statusRoom2 = findViewById(R.id.room2tv);
        Button room2 = findViewById(R.id.room2);
        ImageButton refresh = findViewById(R.id.btn_refresh);

        String amountVisitors = checkVisitors();
        amountVisitors = amountVisitors.replaceAll("\n", "");
        int num = Integer.parseInt(amountVisitors);

        if (num > 0) {
            if (num >= 2)
                room2.setEnabled(false);
            Thread myThread = new Thread(new MyVisitors2Thread());
            myThread.start();
            try {
                myThread.join();
                Log.e("Отслеживаю потоки", "Мы снова в мэйне " + Thread.currentThread());
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            statusRoom2.setText("Сейчас в комнате " + num + " человеков.\nА именно: " + stringResponse);
        } else {
            statusRoom2.setText("Сейчас в комнате " + num + " человеков.");
        }


        room2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(checkVisitors().replaceAll("\n", "")) >= 2) {
                    recreate();
                    Toast.makeText(PlaygroundActivity.this, "Ыыыыыыыы не успел", Toast.LENGTH_SHORT).show();
                } else {
                    Intent enterRoom2 = new Intent(PlaygroundActivity.this, Room2.class);
                    enterRoom2.putExtra("nickname", getIntent().getStringExtra("nickname"));
                    startActivity(enterRoom2);

                    Thread myThread = new Thread(new MyEnter2Thread());
                    myThread.start();
                    try {
                        myThread.join();
                        Log.e("Отслеживаю потоки", "Мы снова в мэйне " + Thread.currentThread());
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }

                    finish();
                }
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
    }
}
