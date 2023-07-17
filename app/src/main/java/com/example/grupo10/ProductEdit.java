package com.example.grupo10;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grupo10.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductEdit#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductEdit extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText edt_producto_nombre;
    private EditText edt_producto_categoria;
    private EditText edt_producto_precio;
    private CheckBox chb_producto_enstock;
    private Button btn_producto_guardar;
    private Button btn_producto_seleccionar;
    private Button btn_producto_subir;
    private ImageView img_produto_imagen;
    private Button btn_producto_ubicacion;
    private TextView tev_producto_ubicacion, IDPROD;
    private EditText edt_producto_cantidad;

    String nombre;
    String precio;
    String imagen;
    String categoria;
    boolean entock;
    Double latitud;
    Double longitud;
    String id;
    Uri data1;
    String urlImage;
    FirebaseFirestore db;

    private SharedPreferences mispreferencias;
    final  int OPEN_GALLERY = 1;
    final  int OPEN_MAPA = 2;



    public ProductEdit() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductEdit.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductEdit newInstance(String param1, String param2) {
        ProductEdit fragment = new ProductEdit();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_product, container, false);
        //IDPROD = root.findViewById(R.id.codigofire);
        edt_producto_nombre = root.findViewById(R.id.edt_producto_nombre);
        edt_producto_categoria = root.findViewById(R.id.edt_categoria);
        edt_producto_precio = root.findViewById(R.id.edt_precio);
        chb_producto_enstock = root.findViewById(R.id.chb_producto_enstock);
        btn_producto_guardar = root.findViewById(R.id.btn_producto_guardar);
        btn_producto_seleccionar = root.findViewById(R.id.btn_producto_seleccionar);
        img_produto_imagen = root.findViewById(R.id.img_produto_imagen);
        btn_producto_subir = root.findViewById(R.id.btn_producto_subir);
//        btn_producto_ubicacion = root.findViewById(R.id.btn_producto_ubicacion2);
//        tev_producto_ubicacion = root.findViewById(R.id.tev_producto_ubicacion);
        //edt_producto_cantidad = root.findViewById(R.id.edt_producto_cantidad);
        //edt_producto_cantidad = root.findViewById(R.id.edt_producto_cantidad);

        Bundle data = getArguments();

        if (data != null){
            id = data.getString("codigo");
            nombre = data.getString("nombre");
            categoria = data.getString("categoria");
            precio = data.getString("precio");
            entock = data.getBoolean("entock");
            //latitud = data.getDouble("latitud");
            //longitud = data.getDouble("longitud");
        }
        edt_producto_nombre.setText(nombre);
        edt_producto_categoria.setText(categoria);
        edt_producto_precio.setText(precio);
        chb_producto_enstock.setChecked(entock);
        //tev_producto_ubicacion.setText("Ubicación: " + latitud + " - " + longitud);
        btn_producto_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mispreferencias = getActivity().getSharedPreferences(Constant.PREFERENCE, Context.MODE_PRIVATE);
                String usuario = mispreferencias.getString("usuario", "NO HAY USUARIO");
                FirebaseFirestore db = FirebaseFirestore.getInstance();


                String newnombre = edt_producto_nombre.getText().toString();
                String newcategoria = edt_producto_categoria.getText().toString();
                int newprecio = Integer.parseInt(edt_producto_precio.getText().toString());
                boolean newenstock = chb_producto_enstock.isChecked();

                if (newnombre.isEmpty()){
                    edt_producto_nombre.setError("Por favor introduce un nombre");
                    edt_producto_nombre.requestFocus();
                }else if (newcategoria.isEmpty()){
                    edt_producto_categoria.setError("Por favor introduce una categoria");
                    edt_producto_categoria.requestFocus();
                }else if (newprecio == 0 || newprecio <0 || newprecio>99999999){
                    edt_producto_categoria.setError("Por favor introduce un precio valido");
                    edt_producto_categoria.requestFocus();
                }else{
                    Map<String, Object> producto = new HashMap<>();
                    producto.put("nombre", newnombre);
                    producto.put("categoria", newcategoria);
                    producto.put("precio", newprecio);
                    producto.put("entock", newenstock);
                    producto.put("imagen", urlImage);
                    //producto.put("latitud", latitud);
                    //producto.put("longitud", longitud);

                    db.collection("producto")
                            .whereEqualTo("nombre", nombre)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()){
                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                        String documentID = documentSnapshot.getId();
                                        db.collection("producto")
                                                .document(documentID)
                                                .update(producto)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        edt_producto_nombre.setText("");
                                                        edt_producto_categoria.setText("");
                                                        edt_producto_precio.setText("");
                                                        chb_producto_enstock.setChecked(false);
                                                        img_produto_imagen.setImageDrawable(getActivity().getDrawable(R.drawable.default_image));
                                                        Toast.makeText(getActivity(), "Actualizacion Correcta", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getActivity(), "Actualizacion no Correcta", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                    }else{
                                        Toast.makeText(getActivity(), "Fallo", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }




            }
        });

        btn_producto_seleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "seleccione una imagen"), OPEN_GALLERY);
            }
        });

        btn_producto_subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subirImagen();
            }
        });

/*        btn_producto_ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapaActivity.class);
                startActivityForResult(intent, OPEN_MAPA);
            }
        });
*/
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {

                data1 = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data1);
                    img_produto_imagen.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getActivity(), "ERROR CON LA IMAGEN", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == OPEN_MAPA) {
            if (resultCode == Activity.RESULT_OK) {
                latitud = data.getDoubleExtra("latitud", 0);
                longitud = data.getDoubleExtra("longitud", 0);

                tev_producto_ubicacion.setText("Ubicación: " + latitud + " - " + longitud);
            }
        }
    }

    public void subirImagen(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        //if there is a file to upload
        if (data1 != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Subiendo");
            progressDialog.show();

            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String strDate = sdf.format(c.getTime());
            String nombreImagen = strDate + ".jpg";

            mispreferencias = getActivity().getSharedPreferences(Constant.PREFERENCE, MODE_PRIVATE);

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
                            Toast.makeText(getActivity(), "File Uploaded ", Toast.LENGTH_LONG).show();

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
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
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
            //you can display an error toast
        }
    }
}