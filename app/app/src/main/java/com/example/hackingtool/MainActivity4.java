package com.example.hackingtool;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity4 extends AppCompatActivity {
    private Socket socket;
    private TextView text;
    private EditText CmdEditText;
    private String ipaddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        Intent intent  = getIntent();
        ipaddress = intent.getStringExtra("ipad1");
        Button nmap = findViewById(R.id.button6);
         text = findViewById(R.id.textView5);
        CmdEditText = findViewById(R.id.editTextText2);
        nmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command = CmdEditText.getText().toString();
                if (!command.isEmpty()) {
                    toserver(command);
                }
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
//                            Toast.makeText(MainActivity4.this, "Connected to server", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(MainActivity4.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }).start();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (socket != null && !socket.isClosed()) {
                    try {
                        socket.close();
                        Toast.makeText(MainActivity4.this, "Connection closed", Toast.LENGTH_SHORT).show();
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
                try {socket = new Socket(ipaddress,4444);
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