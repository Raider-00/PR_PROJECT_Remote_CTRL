package com.example.hackingtool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

public class MainActivity2 extends AppCompatActivity {
    String ipaddress;
    private Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button ls = findViewById(R.id.button);
        Button nmap = findViewById(R.id.button2);
        Button custom = findViewById(R.id.button4);
        Intent intent  = getIntent();
           ipaddress = intent.getStringExtra("ipad");
        ls.setOnClickListener(v -> {
            Intent intent13 = new Intent(MainActivity2.this, MainActivity3.class);
            intent13.putExtra("ipad1",ipaddress);
            Toast.makeText(MainActivity2.this, ipaddress, Toast.LENGTH_SHORT).show();
            startActivity(intent13);
        });
        nmap.setOnClickListener(v -> {
            Intent intent1 = new Intent(MainActivity2.this, MainActivity4.class);
            intent1.putExtra("ipad1", ipaddress);
            startActivity(intent1);
        });
        custom.setOnClickListener(v -> {
            Intent intent12 = new Intent(MainActivity2.this, MainActivity5.class);
            intent12.putExtra("ipad1", ipaddress);
            startActivity(intent12);
        });

    }

}