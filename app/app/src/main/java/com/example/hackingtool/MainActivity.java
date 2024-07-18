package com.example.hackingtool;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import android.content.Intent;
import android.widget.Toast;

public  class MainActivity extends AppCompatActivity {
    private EditText IpEditText;
    private Button IpButton;
    private String ipaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IpEditText = findViewById(R.id.editTextText4);
        IpButton = findViewById(R.id.button3);
        IpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 ipaddress = IpEditText.getText().toString().trim();
                if (ipaddress.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter IP", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                intent.putExtra("ipad",ipaddress);
                startActivity(intent);
            }
        });
    }



    }


