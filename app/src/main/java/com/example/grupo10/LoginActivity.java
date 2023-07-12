package com.example.grupo10;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.grupo10.database.model.UserDatabase;
import com.example.grupo10.database.model.model.User;
import com.example.grupo10.ui.gallery.GalleryFragment;
import com.example.grupo10.ui.home.HomeFragment;
import com.example.grupo10.util.Constant;
import com.example.grupo10.util.Utilidades;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_login, btn_registro, btn_recontraseña;
    private EditText edt_usuario, edt_contrasena;

    private final int ACTIVITY_REGISTRO = 1;

    private SharedPreferences mispreferencias;

    private UserDatabase database;

    private List<User> listaUsuarios;

    MediaPlayer player;

    FirebaseAuth mAuth;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btn_login);
        btn_registro = findViewById(R.id.btn_registro);
        edt_usuario = findViewById(R.id.edt_usuario);
        edt_contrasena = findViewById(R.id.edt_contrasena);
        btn_recontraseña = findViewById(R.id.btncontra);
        btn_login.setOnClickListener(this);
        btn_registro.setOnClickListener(this);
        mispreferencias = getSharedPreferences(Constant.PREFERENCE, MODE_PRIVATE);
        database = UserDatabase.getInstance(this);


        String nombre = mispreferencias.getString("nombre", "");
        String usuario = mispreferencias.getString("usuario", "");
        String contrasena = mispreferencias.getString("contrasena", "");
        int usertipe = mispreferencias.getInt("usertipe", 2);
        String imagen = mispreferencias.getString("imagen", "");

        if (!usuario.equals("") && !contrasena.equals("")) {
            //toLogin(usuario, contrasena, nombre, imagen);
        }


        new GetUserTask(this).execute();

        mAuth = FirebaseAuth.getInstance();

        btn_recontraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player = MediaPlayer.create(LoginActivity.this,R.raw.risasmas);
                player.start();
                EditText resetMail= new EditText(v.getContext());
                AlertDialog.Builder passwordReset = new AlertDialog.Builder(LoginActivity.this);
                passwordReset.setTitle("Olvidaste la contraseña?");
                passwordReset.setMessage("Escribe tu correo electronico");
                passwordReset.setView(resetMail);
                passwordReset.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        String mail = resetMail.getText().toString();
                        Intent intent = new Intent(LoginActivity.this, recuperar_contra.class);
                        intent.putExtra("correo", mail);
                        startActivity(intent);
                        finish();
                        /*mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this,"Revisa tu corrreo para reiniciar contraseña", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(LoginActivity.this,"Error al reiniciar contraseña", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });*/
                    }
                });
                passwordReset.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                passwordReset.create().show();
            }
        });
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                String usuario = edt_usuario.getText().toString().trim();
                String contrasena = edt_contrasena.getText().toString().trim();

                Log.e("USUARIO", usuario);
                Log.e("CONTRASENA", contrasena);

                if (usuario.equals("admin@admin.com") && contrasena.equals("admin")) {
                    toLogin(usuario, contrasena, "admin", "",0 );

                }else if (usuario.isEmpty()){
                    edt_usuario.setError("Por favor introduce un correo electronico");
                    edt_usuario.requestFocus();
                }else if (contrasena.isEmpty()){
                    edt_contrasena.setError("Por favor introduce una contraseña");
                    edt_contrasena.requestFocus();
                }else {
                    inicioSesionFirestore(usuario, contrasena);
                    //inicioSesionFirebase(usuario, Utilidades.md5(contrasena));
                    //new GetUserLoginTask(this, usuario, Utilidades.md5(contrasena));
                    //Toast.makeText(this, "ERROR INICIANDO SESIÓN", Toast.LENGTH_SHORT).show();
                    //iniciosesionconcotra(usuario, contrasena);
                }
                break;
            case R.id.btn_registro:
                player = MediaPlayer.create(LoginActivity.this,R.raw.tacobell);
                player.start();
                Intent intent = new Intent(this, RegistroActivity.class);
                startActivityForResult(intent, ACTIVITY_REGISTRO);
                break;
        }
    }

    public void iniciosesionconcotra(String usuario, String contrasena){
        mAuth.signInWithEmailAndPassword(usuario, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "iniciando sesion",Toast.LENGTH_SHORT).show();
                    //toLogin(usuario, contrasena, "", "",);
                }else {
                    Toast.makeText(LoginActivity.this, "DATOS INCORRECTOS",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void inicioSesionFirestore(String correo, String contrasena) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("usuarios").document(correo);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.e("TAG", "DocumentSnapshot data: " + document.getData());

                        String userContrasena = document.getData().get("contrasena").toString();
                        int usertpie = Integer.parseInt(document.getData().get("usertipe").toString());
                        String userNombre = document.getData().get("nombre").toString();
                        String userImagen= "";
                        try {
                             userImagen = document.getData().get("imagen").toString();
                        }catch (Exception e){
                            userImagen = "";
                        }

                        if (contrasena.equals(userContrasena)) {
                            toLogin(correo, contrasena, userNombre, userImagen, usertpie);
                            player = MediaPlayer.create(LoginActivity.this,R.raw.impact);
                            player.start();

                        } else {
                            Toast.makeText(LoginActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                            player = MediaPlayer.create(LoginActivity.this,R.raw.sus);
                            player.start();
                        }


                    } else {
                        Log.e("TAG", "No such document");
                        Toast.makeText(LoginActivity.this, "Correo o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    public void inicioSesionFirebase(String usuario, String contrasena) {
        mAuth.signInWithEmailAndPassword(usuario, contrasena)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //toLogin(usuario, contrasena, "", "");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("TAG", "signInWithEmail:failure", task.getException());

                        }
                    }
                });
    }

    public void toLogin(String usuario, String contrasena, String nombre, String imagen, int usertipe){
        Toast.makeText(this, "SE HA INICIADO SESIÓN", Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = mispreferencias.edit();
        editor.putString("usuario", usuario);
        editor.putString("contrasena", contrasena);
        editor.putString("nombre", nombre);
        editor.putString("imagen", imagen);
        editor.putInt("usertipe", usertipe);

        editor.commit();

        if (usertipe==1){
            Intent intent = new Intent(this, MenuActivityuser.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("usertipe", usertipe);
            intent.putExtra("contrasena", contrasena);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("usertipe", usertipe);
            intent.putExtra("contrasena", contrasena);

            startActivity(intent);
            finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_REGISTRO) {
            if (resultCode == Activity.RESULT_OK){


                String usuario = data.getStringExtra("correo");
                String contrasena = data.getStringExtra("contrasena");

                toLogin(usuario, contrasena, "", "",0);
            }
        }

    }

    private static class GetUserTask extends AsyncTask<Void, Void, List<User>> {

        private final WeakReference<LoginActivity> loginActivityWeakReference;

        GetUserTask(LoginActivity context) {
            this.loginActivityWeakReference = new WeakReference<>(context);
        }

        @Override
        protected List<User> doInBackground(Void... voids) {
            if (loginActivityWeakReference.get() != null) {
                List<User> users = loginActivityWeakReference.get().database.getUserdao().getUser();
                return users;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            if (users != null && users.size() > 0) {
                loginActivityWeakReference.get().listaUsuarios = users;
            }
            super.onPostExecute(users);
        }
    }

    private static class GetUserLoginTask extends AsyncTask<Void, Void, User> {

        private final String email;
        private final String password;
        private final WeakReference<LoginActivity> loginActivityWeakReference;

        GetUserLoginTask(LoginActivity context, String email, String password) {
            this.loginActivityWeakReference = new WeakReference<>(context);
            this.email = email;
            this.password = password;
            doInBackground();
        }

        @SuppressLint("WrongThread")
        @Override
        protected User doInBackground(Void... voids) {
            if (loginActivityWeakReference.get() != null) {
                User user = loginActivityWeakReference.get().database.getUserdao().getUserLogin(email, password);
                onPostExecute(user);
                return user;
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                //loginActivityWeakReference.get().listaUsuarios = users;
                //loginActivityWeakReference.get().toLogin(email, password, "", "");
            } else {
                Log.e("LOGINTASK", "INICIO DE SESION INVALIDO");
            }
            super.onPostExecute(user);
        }
    }
}