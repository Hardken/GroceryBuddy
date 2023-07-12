package com.example.grupo10;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TERMINOS_ACTI extends AppCompatActivity {
    private Button btn_regresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminos);

        btn_regresar = findViewById(R.id.btn_regresar);

        btn_regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent resultintent = new Intent();
                resultintent.putExtra("ESTADO", "ACEPTADO");
                setResult(Activity.RESULT_OK, resultintent);
                finish();
            }
        });
    }
}