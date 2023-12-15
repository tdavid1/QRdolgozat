package com.example.qrdolgozat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private Button scan;
    private Button listazas;
    private TextView textview;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        listazas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ListaAdatok.class);
                startActivity(intent);
                finish();
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("QR Code olvasó");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.initiateScan();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result !=null){
            if(result.getContents() == null){
                Toast.makeText(this, "Kiléptél a qrcode olvasóból", Toast.LENGTH_SHORT).show();
            }
            else{
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("restapi",result.getContents());
                editor.apply();
                textview.setText(result.getContents());
                Uri uri = Uri.parse(result.getContents());
                if(URLUtil.isValidUrl(result.getContents())){
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void init(){
        listazas = findViewById(R.id.listazas);
        scan =  findViewById(R.id.Scan);
        textview = findViewById(R.id.textviewmain);
        sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
    }
}