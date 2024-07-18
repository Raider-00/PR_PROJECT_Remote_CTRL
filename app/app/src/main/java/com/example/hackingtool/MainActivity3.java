package com.example.hackingtool;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity3 extends AppCompatActivity {
    private TextView text;
    private Socket socket;
    private String ipaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Intent intent  = getIntent();
        ipaddress = intent.getStringExtra("ipad1");
        Button ls = findViewById(R.id.button6);
        text = findViewById(R.id.textView5);
        ls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command = "dir";
                Toast.makeText(MainActivity3.this, ipaddress, Toast.LENGTH_SHORT).show();
                toserver(command);
            }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    socket = new Socket(ipaddress,4444);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity3.this, "Connected to server", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(MainActivity3.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }).start();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (socket != null && !socket.isClosed()) {
                    try {
                        socket.close();
                        Toast.makeText(MainActivity3.this, "Connection closed", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }



    private void toserver(final String command) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ipaddress,4444);
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    output.println(command); // Send command to server
                    String line;
                    StringBuilder ResponseStringBuilder = new StringBuilder();
                    while ((line = input.readLine()) != null) {
                        ResponseStringBuilder.append(line).append("\n");
                    }
                    final String response = ResponseStringBuilder.toString() ;// Receive output from server

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text.setText(response);
                        }
                    });

                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}