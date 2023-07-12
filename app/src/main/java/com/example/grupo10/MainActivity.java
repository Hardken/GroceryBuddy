package com.example.grupo10;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_jugar,btn_profile,btn_config,btn_acercade;
    private Activity miActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        miActivity = this;

        btn_jugar = findViewById(R.id.btn_jugar);
        btn_profile = findViewById(R.id.btn_profile);
        btn_config = findViewById(R.id.btn_config);
        btn_acercade = findViewById(R.id.btn_acercade);

        btn_jugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CLICK_MAIN2", "Le he dado al boton PROFILE");
                Toast.makeText(miActivity, "Le he dado al boton PROFILE", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "Le he dado al boton PROFILE", Toast.LENGTH_SHORT).show();
            }
        });

        btn_jugar.setOnClickListener(this);
        btn_profile.setOnClickListener(this);
        btn_config.setOnClickListener(this);
        btn_acercade.setOnClickListener(this);
    }


    //primera forma de agregar acciones a botonoes
    public void clickJugar(View view){
        Log.e("CLICK_MAIN", "Le he dado al boton JUGAR");
        Toast.makeText(this, "Le he dado al boton JUGAR", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_jugar:
                Toast.makeText(this, getString(R.string.txt_btnjugar),Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_profile:
                Toast.makeText(this, "Le he dado al boton Perfil",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_config:
                Toast.makeText(this, "Le he dado al boton JUGAR",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_acercade:
                Toast.makeText(this, "Le he dado al boton JUGAR",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}