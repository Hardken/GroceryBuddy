package com.example.grupo10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class recuperar_contra extends AppCompatActivity {
    private Button btn_recontra;
    private EditText edt_recontrasena, edt_repregunta;
    private EditText edt_recorreo;
    private Spinner spnpre;
    String nombre;
    String correobd;
    String apellido;
    String contraseñabd;
    String preguntase;
    String respregun;
    String imagen;
    String urlImage;
    String id;
     FirebaseAuth mAuth;
     FirebaseFirestore firestore;
     Activity miActividad;
     String usrdi, idpregunta, respusegu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contra);
        Intent intent = new Intent(recuperar_contra.this, recuperar_contra.class);
        Bundle bundle = getIntent().getExtras();
        correobd = bundle.getString("correo");
        btn_recontra=findViewById(R.id.btn_recontra);
        edt_recorreo=findViewById(R.id.reedt_correo);
        edt_repregunta=findViewById(R.id.edt_repregunta);
        edt_recontrasena=findViewById(R.id.edt_recontrasena);
        spnpre=findViewById(R.id.respinnerpre);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        edt_recorreo.setText(correobd);
        String[] pregunta = new String[]{"Color favorito?", "Nombre de primera mascota", "Mejor amigo de la infancia"};
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(recuperar_contra.this, android.R.layout.simple_spinner_dropdown_item,
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
        btn_recontra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String correo = edt_recorreo.getText().toString();
                String contrasena = edt_recontrasena.getText().toString();
                String respuesta = edt_repregunta.getText().toString();
                String idpreg = spnpre.getSelectedItem().toString();
                if (correo.isEmpty()){
                    edt_recorreo.setError("Por favor introduce un correo");
                    edt_recorreo.requestFocus();
                }else if (contrasena.isEmpty()){
                    edt_recontrasena.setError("Por favor introduce una contraseña");
                    edt_recontrasena.requestFocus();
                }else if (respuesta.isEmpty()){
                    edt_repregunta.setError("Por favor introduce una respuesta");
                    edt_repregunta.requestFocus();
                }else{
                    DocumentReference docRef = db.collection("usuarios").document(correo);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.e("TAG", "DocumentSnapshot data: " + document.getData());

                                    String userContrasena = document.getData().get("contrasena").toString();
                                    String userNombre = document.getData().get("nombre").toString();
                                    String userpregunta = document.getData().get("pregunta").toString();
                                    String userres = document.getData().get("respuestaseguridad").toString();

                                    if (idpreg.equals(userpregunta) && respuesta.equals(userres)) {
                                        Map<String, Object> usuario = new HashMap<>();
                                        usuario.put("contrasena", contrasena);
                                        db.collection("usuarios")
                                                .whereEqualTo("correo", correo)
                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful() && !task.getResult().isEmpty()){
                                                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                                            String documentID = documentSnapshot.getId();
                                                            db.collection("usuarios")
                                                                    .document(documentID)
                                                                    .update(usuario)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            edt_recorreo.setText("");
                                                                            edt_recontrasena.setText("");
                                                                            edt_repregunta.setText("");
                                                                            Toast.makeText(recuperar_contra.this, "Actualizacion Correcta", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(recuperar_contra.this, "Error Actualizacion no Correcta", Toast.LENGTH_SHORT).show();

                                                                        }
                                                                    });
                                                        }else{
                                                            Toast.makeText(recuperar_contra.this, "Fallo", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });

                                    } else {
                                        Toast.makeText(recuperar_contra.this, "Error", Toast.LENGTH_SHORT).show();

                                    }


                                } else {
                                    Log.e("TAG", "No such document");
                                    Toast.makeText(recuperar_contra.this, "Correo o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("TAG", "get failed with ", task.getException());
                            }
                        }
                    });



                }
            }
        });


    }
    public void xd(String usrdi){
        DocumentReference documentReference = firestore.collection("usuarios").document(usrdi);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                idpregunta=documentSnapshot.getString("pregunta");
                respusegu=documentSnapshot.getString("respuestaseguridad");
            }
        });
    }

    public void inicioSesionFirestore(String correo, String contrasena, String idpre) {


    }
}