package com.example.from_rags_to_riches.entriesActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.from_rags_to_riches.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class RegisterActivity extends AppCompatActivity {

    private EditText nickname;
    private EditText password;
    private Button register_button;

    private String stringResponse;

    private class MyRegisterThread implements Runnable {

        @Override
        public void run() {
            Log.e("Отслеживаю потоки", "поток - " + Thread.currentThread() + Thread.currentThread().getName());
            String registerUrl = getString(R.string.server) + "register";
            JSONObject register = new JSONObject();
            try {
                register.put("nickname", nickname.getText().toString());
                register.put("password", password.getText().toString());
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
                System.out.println("  _-_-_response_-_-_  " + response);

                response.close();
                conn.disconnect();
            } catch (Exception e) {
                System.out.println(e);
                Log.e("______________________________________", String.valueOf(e));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        nickname = findViewById(R.id.nickname_register);
        password = findViewById(R.id.password_register);
        register_button = findViewById(R.id.btn_register);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //hide keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(register_button.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                //end of hide

                Thread myThread = new Thread(new MyRegisterThread());
                myThread.start();
                try{
                    myThread.join();
                    Log.e("Отслеживаю потоки", "Мы снова в мэйне " + Thread.currentThread());
                } catch(InterruptedException e){
                }

                switch (stringResponse) {
                    case "\"already_exist\"\n":
                        Toast.makeText(RegisterActivity.this, "Логин уже занят", Toast.LENGTH_SHORT).show();
                        break;
                    case "\"done\"\n":
                        Snackbar RegisterSnackBar = Snackbar.make(getCurrentFocus(), "Регистрация успешна!", Snackbar.LENGTH_LONG);
                        RegisterSnackBar.show();
                        RegisterSnackBar.setAction("Вход", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                        break;
                }
            }
        });
    }
}