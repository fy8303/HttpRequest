package com.example.httprequest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_response;
    private String origin = "https://192.168.219.100/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tv_response = findViewById(R.id.response_text);
        Button btn_get = findViewById(R.id.btn_get);
        Button btn_post = findViewById(R.id.btn_post);

        btn_get.setOnClickListener(this);
        btn_post.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get:
                //sendGetUrl();
                sendGetUrlWithOkHttp();
                break;
            case R.id.btn_post:
                //sendPostUrl();
                sendPostUrlWithOkHttp();
                break;
        }
    }

    //HttpURLConnection方式get请求示例
    private void sendGetUrl() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    HTTPSTrustManager.allowAllSSL();
                    URL url = new URL(origin);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    showResponse(response.toString());
                } catch (Exception e) {

                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    //HttpURLConnection方式post请求示例
    private void sendPostUrl() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    HTTPSTrustManager.allowAllSSL();
                    URL url = new URL(origin + "e/vpn/VPNUser.login.json");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("username=%22a%22&password=%22a%22");
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    showResponse(response.toString());
                } catch (Exception e) {

                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    //OkHttp方式get请求示例
    private void sendGetUrlWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.sslSocketFactory(TrustAllCerts.createSSLSocketFactory(), TrustAllCerts.createTrustManager());
                    builder.hostnameVerifier(new TrustAllCerts.TrustAllHostnameVerifier());
                    OkHttpClient client = builder.build();

                    Request.Builder request = new Request.Builder();
                    request.url(origin);
                    Response response = client.newCall(request.build()).execute();

                    if (response.isSuccessful()) {
                        showResponse(response.body().string());
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //OkHttp方式post请求示例
    private void sendPostUrlWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.sslSocketFactory(TrustAllCerts.createSSLSocketFactory(), TrustAllCerts.createTrustManager());
                    builder.hostnameVerifier(new TrustAllCerts.TrustAllHostnameVerifier());
                    OkHttpClient client = builder.build();

                    FormBody.Builder params = new FormBody.Builder();
                    params.add("username", "\"a\"");
                    params.add("password", "\"a\"");

                    Request.Builder request = new Request.Builder();
                    request.url(origin + "e/vpn/VPNUser.login.json");
                    request.post(params.build());
                    Response response = client.newCall(request.build()).execute();

                    if (response.isSuccessful()) {
                        showResponse(response.body().string());
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_response.setText(response);
            }
        });
    }
}
