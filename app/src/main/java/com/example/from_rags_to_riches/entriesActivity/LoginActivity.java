package com.example.from_rags_to_riches.entriesActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.from_rags_to_riches.PlaygroundActivity;
import com.example.from_rags_to_riches.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {
    private EditText nickname;
    private EditText password;

    private String stringResponse;

    private class MyLoginThread implements Runnable{

        @Override
        public void run() {
            Log.e("Отслеживаю потоки", "поток " + Thread.currentThread() + Thread.currentThread().getName());
            String loginUrl = getString(R.string.server) + "login?nickname=" + nickname.getText().toString() + "&password=" + password.getText().toString();
            Looper.prepare();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nickname = findViewById(R.id.nickname_login);
        password = findViewById(R.id.password_login);
        Button enter_button = findViewById(R.id.btn_login);
        Button register_button = findViewById(R.id.btn_register_login);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        enter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread myThread = new Thread(new MyLoginThread());
                myThread.start();
                try {
                    myThread.join();
                    Log.e("Отслеживаю потоки", "Мы снова в мэйне " + Thread.currentThread());
                } catch (InterruptedException e) {
                    System.out.println(e);
                }


                stringResponse = stringResponse.replaceAll("\n", "");

                switch (stringResponse) {
                    case "\"incorrect data\"":
                        Toast.makeText(LoginActivity.this, "Неверные данные", Toast.LENGTH_SHORT).show();
                        break;

                    case "\"correct data\"":
                        Intent loginIntent = new Intent(LoginActivity.this, PlaygroundActivity.class);
                        loginIntent.putExtra("nickname", nickname.getText().toString());
                        startActivity(loginIntent);
                        finish();
                        break;
                }
            }
        });
    }
}