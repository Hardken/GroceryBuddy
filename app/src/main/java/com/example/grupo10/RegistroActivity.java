package com.example.grupo10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grupo10.database.model.UserDatabase;
import com.example.grupo10.database.model.model.User;
import com.example.grupo10.util.Utilidades;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {

    private CheckBox chb_terminos;
    private Button btn_registro_usuario;
    private EditText edt_contrasena, edt_pregunta;
    private EditText edt_correo, edt_nombre, edt_apellido;

    private TextView tev_terminos;

    private Spinner spnpre;

    private Activity miActividad;

    private UserDatabase database;

    private final int ACTIVITY_TERMINOS = 2;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        chb_terminos = findViewById(R.id.chb_terminos);
        btn_registro_usuario = findViewById(R.id.btn_registro_usuario);
        edt_nombre = findViewById(R.id.edt_nombre);
        edt_apellido = findViewById(R.id.edt_apellido);
        edt_correo = findViewById(R.id.edt_correo);
        edt_contrasena = findViewById(R.id.edt_contrasena);
        edt_correo = findViewById(R.id.edt_correo);
        tev_terminos = findViewById(R.id.tev_terminos);
        spnpre = findViewById(R.id.spinnerpre);
        edt_pregunta = findViewById(R.id.edt_pregunta);

        String[] pregunta = new String[]{"Color favorito?", "Nombre de primera mascota", "Mejor amigo de la infancia"};
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(RegistroActivity.this, android.R.layout.simple_spinner_dropdown_item,
                pregunta);
        spnpre.setAdapter(adaptador);

        spnpre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String pregunta = spnpre.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        miActividad = this;

        btn_registro_usuario.setEnabled(false);
        chb_terminos.setEnabled(false);



        chb_terminos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btn_registro_usuario.setEnabled(isChecked);
            }
        });

        btn_registro_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombre = edt_nombre.getText().toString();
                String apellido = edt_apellido.getText().toString();
                String correo = edt_correo.getText().toString();
                String contrasena = edt_contrasena.getText().toString();
                String respuesta = edt_pregunta.getText().toString();
                String idpreg = spnpre.getSelectedItem().toString();
                if (nombre.isEmpty()){
                    edt_nombre.setError("Por favor introduce un nombre");
                    edt_nombre.requestFocus();
                }else if(apellido.isEmpty()){
                    edt_apellido.setError("Por favor introduce un apellido");
                    edt_apellido.requestFocus();
                }else if(correo.isEmpty()){
                    edt_apellido.setError("Por favor introduce un correo valido");
                    edt_apellido.requestFocus();
                }else if (contrasena.isEmpty()){
                    edt_contrasena.setError("Por favor introduce una contraseña valida");
                    edt_contrasena.requestFocus();
                }else if (respuesta.isEmpty()){
                    edt_pregunta.setError("Por favor introduce una respuesta a la pregunta de seguridad");
                    edt_pregunta.requestFocus();
                }else if(contrasena.length() < 8 && !isValidPassword(contrasena)){
                    Toast.makeText(RegistroActivity.this, "La contraseña debe tener minimo 8 caracteres", Toast.LENGTH_SHORT).show();
                }else{
                    registrofirecloud(nombre,apellido,correo,contrasena,respuesta,idpreg);
                    Toast.makeText(RegistroActivity.this, "CONTRASEÑA VALIDA", Toast.LENGTH_SHORT).show();
                    //Intent resultIntent = new Intent();
                    //resultIntent.putExtra("correo", edt_correo.getText().toString());
                    //resultIntent.putExtra("contrasena", edt_contrasena.getText().toString());
                    //setResult(Activity.RESULT_OK, resultIntent);

                    //User user = new User(nombre,apellido,correo, Utilidades.md5(contrasena));
                    //long l = database.getUserdao().insertUser(user);
                    //finish();
                    //registrarusuariofirebase(correo, Utilidades.md5(contrasena));

                    //registroautenticationdatabase(nombre,apellido,correo,contrasena);

                }
            }
        });



        tev_terminos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(RegistroActivity.this, TerminosActivity.class)
                Intent intent = new Intent(miActividad, TERMINOS_ACTI.class);
                startActivityForResult(intent, ACTIVITY_TERMINOS);
            }
        });

        database = UserDatabase.getInstance(this);

        mAuth = FirebaseAuth.getInstance();


    }
    public void registroautenticationdatabase(String nombre, String apellido, String correo, String contrasena, String idpre){
        mAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(nombre,apellido,correo,contrasena);
                            FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(RegistroActivity.this, "Usuario registrado",Toast.LENGTH_SHORT).show();
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                user.sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.e("TAG", "Email sent.");
                                                                }
                                                            }
                                                        });
                                                finish();
                                            }else{
                                                Toast.makeText(RegistroActivity.this, "Usuario no registrado",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                        else{
                            Toast.makeText(RegistroActivity.this, "Usuario no registrado",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void registrofirecloud(String nombre, String apellido, String correo, String contrasena, String respuesta, String idpreg){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", nombre);
        usuario.put("apellido", apellido);
        usuario.put("correo", correo);
        usuario.put("contrasena", contrasena);
        usuario.put("pregunta", idpreg);
        usuario.put("respuestaseguridad", respuesta);
        usuario.put("usertipe", 1);
        usuario.put("cuenta", 0);
        usuario.put("devtoken", "");
        usuario.put("imagen", "https://firebasestorage.googleapis.com/v0/b/grupo10-fae99.appspot.com/o/avatar200.jpg?alt=media&token=81d0d64e-40ae-4526-ba2b-90dfda01c9dc");
        db.collection("usuarios").whereEqualTo("correo", correo)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().isEmpty()){
                                db.collection("usuarios").document(correo)
                                        .set(usuario)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                registrarusuariofirebase(correo, contrasena);
                                                Log.d("TAG", "DocumentSnapshot successfully written!");
                                                Toast.makeText(RegistroActivity.this, "Usuario registrado",Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("TAG", "Error writing document", e);
                                                Toast.makeText(RegistroActivity.this, "Usuario no registrado",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }else{
                                Toast.makeText(RegistroActivity.this, "Usuario con este correo ya existe", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(RegistroActivity.this, "Error al comprobar el correo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void registrarusuariofirebase(String correo, String contrasena){
        mAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.e("TAG", "Email sent.");
                                            }
                                        }
                                    });

                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("TAG", "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_TERMINOS) {
            if (resultCode == Activity.RESULT_OK) {
                String estado = data.getStringExtra("ESTADO");
                //Toast.makeText(miActividad, "Acepto terminos", Toast.LENGTH_SHORT).show();
                chb_terminos.setChecked(true);
            } else {
                chb_terminos.setChecked(false);
                //Toast.makeText(miActividad, "No acepto terminos", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

}