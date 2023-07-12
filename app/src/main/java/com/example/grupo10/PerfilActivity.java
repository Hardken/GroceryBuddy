package com.example.grupo10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.grupo10.util.Constant;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView usr_nombre;
    private TextView usr_correo;
    private TextView usr_apellido;
    private Button usave, udel, slecimg, upldimg;
    String nombre;
    String correo;
    String apellido;
    String imagen;
    String urlImage;
    String id;
    Uri data1;
    FirebaseFirestore db;
    private Activity miActivity;
    private SharedPreferences mispreferencias;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        miActivity=this;
        usr_nombre=findViewById(R.id.edt_usr_nombre);
        usr_apellido=findViewById(R.id.edt_usr_apellido);
        usr_correo=findViewById(R.id.edt_user_correo);
        imageView=findViewById(R.id.img_usr_img);
        usave=findViewById(R.id.btn_usr_guardar);
        udel=findViewById(R.id.btn_usr_dele);
        slecimg=findViewById(R.id.btn_usr_seleccionar);
        upldimg=findViewById(R.id.btn_usr_subir);
        Bundle usrdata = getIntent().getExtras();
        if(usrdata != null){
            //String producto = bundle.getString("PRODUCTOS");
            Log.e("Usuario Recibido", usrdata.getString("usuario"));
            try {
                //JSONObject json = new JSONObject(producto);
                JSONObject usuario = new JSONObject(usrdata.getString("usuario"));
                id = usuario.getString("codigo");
                nombre = usuario.getString("nombre");
                apellido = usuario.getString("apellido");
                imagen = usuario.getString("imagen");
                correo = usuario.getString("correo");


                usr_nombre.setText(nombre);
                usr_apellido.setText(apellido);
                usr_correo.setText(correo);

                Glide.with(this)
                        .load(imagen)
                        .placeholder(new ColorDrawable(Color.BLACK))
                        .into(imageView);
            } catch (JSONException exception) {
                exception.printStackTrace();
            }

        }

        slecimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "seleccione una imagen"), 1);
            }
        });

        upldimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subirImagen();
            }
        });

        usave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String newnombre = usr_nombre.getText().toString();
                String newapellido = usr_apellido.getText().toString();
                String newcorreo = usr_correo.getText().toString();

                Map<String, Object> usuarioupd = new HashMap<>();
                usuarioupd.put("nombre", newnombre);
                usuarioupd.put("apellido", newapellido);
                usuarioupd.put("correo", newcorreo);
                usuarioupd.put("imagen", urlImage);
                if (newnombre.isEmpty()){
                    usr_nombre.setError("Por favor introduce un nombre");
                    usr_nombre.requestFocus();
                }else if (newapellido.isEmpty()){
                    usr_apellido.setError("Por favor introduce un nombre");
                    usr_apellido.requestFocus();
                }else if (newcorreo.isEmpty()){
                    usr_correo.setError("Por favor introduce un nombre");
                    usr_correo.requestFocus();
                }else{
                    db.collection("usuarios")
                            .whereEqualTo("nombre",nombre)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()){
                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                        String documentID = documentSnapshot.getId();
                                        db.collection("usuarios")
                                                .document(documentID)
                                                .update(usuarioupd)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        usr_nombre.setText("");
                                                        usr_correo.setText("");
                                                        usr_apellido.setText("");
                                                        imageView.setImageDrawable(PerfilActivity.this.getDrawable(R.drawable.default_image));
                                                        Toast.makeText(PerfilActivity.this, "Actualizacion Correcta", Toast.LENGTH_SHORT).show();

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(PerfilActivity.this, "Error: no se pudo hacer actualizacion", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                    }else{
                                        Toast.makeText(PerfilActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }


            }
        });
        udel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("usuarios")
                        .whereEqualTo("nombre", nombre)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()){
                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                    String documentID = documentSnapshot.getId();
                                    db.collection("usuarios")
                                            .document(documentID)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(PerfilActivity.this, "Eliminado correctamente", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(PerfilActivity.this, "Error no se ha eliminado correctamente", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }else{
                                    Toast.makeText(PerfilActivity.this, "ERROR", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                data1 = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data1);
                    imageView.setImageBitmap(bitmap);

                    subirImagen();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(this, "ERROR CON LA IMAGEN", Toast.LENGTH_SHORT).show();
            }
        }

    }
    public void subirImagen(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        //if there is a file to upload
        if (data1 != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(PerfilActivity.this);
            progressDialog.setTitle("Subiendo");
            progressDialog.show();

            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String strDate = sdf.format(c.getTime());
            String nombreImagen = strDate + ".jpg";

            mispreferencias = PerfilActivity.this.getSharedPreferences(Constant.PREFERENCE, MODE_PRIVATE);

            String usuario = mispreferencias.getString("usuario", "NO HAY USUARIO");


            StorageReference riversRef = storageReference.child(usuario + "/" + nombreImagen);
            riversRef.putFile(data1)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(PerfilActivity.this, "File Uploaded ", Toast.LENGTH_LONG).show();

                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urlImage = uri.toString();
                                    Log.e("URL_IMAGE", urlImage);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(PerfilActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            Toast.makeText(PerfilActivity.this, "Error", Toast.LENGTH_LONG).show();
        }
    }
}