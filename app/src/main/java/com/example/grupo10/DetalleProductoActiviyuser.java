package com.example.grupo10;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.grupo10.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetalleProductoActiviyuser extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageView;
    private TextView tv_nombre;
    private TextView tv_precio;
    private TextView tv_category;
    private TextView tev_detalle_stock;
    private Button btn_compra, btn_addcarrito, btnedit, btndelete;
    //private Button btn_map;
    //private GoogleMap mMap;
    String nombre, usuario;
    String precio;
    String imagen;
    String categoria;
    boolean entock;
    //Double latitud;
    //Double longitud;
    String id;
    FirebaseFirestore db;
    private Activity miActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_productouser_activiy);

        miActivity = this;
        SharedPreferences mispreferencias = miActivity.getSharedPreferences(Constant.PREFERENCE, Context.MODE_PRIVATE);
        usuario = mispreferencias.getString("usuario", "NO HAY USUARIO");
        tv_nombre = findViewById(R.id.tex_detalle_nombre);
        tv_precio = findViewById(R.id.text_detalle_precio);
        tv_category = findViewById(R.id.tev_detalle_categoria);
        imageView = findViewById(R.id.imv_detalle_imagen);
        btn_compra = findViewById(R.id.btn_compra);
        btn_addcarrito = findViewById(R.id.btn_addcarrito);
        //btnedit = findViewById(R.id.btn_edit);
        btndelete = findViewById(R.id.btn_DELETE);
        //btn_map = findViewById(R.id.btn_map);
        tev_detalle_stock = findViewById(R.id.tev_detalle_stock);
        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            //String producto = bundle.getString("PRODUCTOS");
            Log.e("PRODUCTO_RECIBIDO", bundle.getString("producto"));
            try {
                //JSONObject json = new JSONObject(producto);
                JSONObject producto = new JSONObject(bundle.getString("producto"));
                //id = producto.getString("codigo");
                nombre = producto.getString("nombre");
                precio = producto.getString("precio");
                imagen = producto.getString("imagen");
                entock = producto.getBoolean("entock");
                categoria = producto.getString("categoria");
                //latitud = producto.getDouble("latitud");
                //longitud = producto.getDouble("longitud");

/*
                if (latitud != 0.0 && longitud != 0.0) {
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    mapFragment.getMapAsync(this);
                }
*/
                tv_nombre.setText(nombre);
                tv_precio.setText("$" + precio);
                tv_category.setText(categoria);
                if (entock) {
                    tev_detalle_stock.setText("Producto disponible");
                    tev_detalle_stock.setTextColor(Color.GREEN);
                } else {
                    tev_detalle_stock.setText("Producto agotado");
                    tev_detalle_stock.setTextColor(Color.RED);
                }

                Glide.with(this)
                        .load(imagen)
                        .placeholder(new ColorDrawable(Color.BLACK))
                        .into(imageView);
            } catch (JSONException exception) {
                exception.printStackTrace();
            }

            btn_compra.setOnClickListener(this);
            btn_addcarrito.setOnClickListener(this);
        }
/*        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductEdit productEdit = new ProductEdit();

                FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                        .beginTransaction();
                 Bundle data = new Bundle();
                 //data.putString("codigo", id);
                 data.putString("nombre", nombre);
                 data.putString("precio", precio);
                 data.putString("imagen", imagen);
                 data.putBoolean("entock", entock);
                 data.putString("categoria", categoria);
                 //data.putDouble("latitud", latitud);
                 //data.putDouble("longitud", longitud);

                 productEdit.setArguments(data);
                 fragmentTransaction.replace(R.id.layoutdetalle, productEdit).commit();

            }
        });*/

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("producto")
                        .whereEqualTo("nombre", nombre)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()){
                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                    String documentID = documentSnapshot.getId();
                                    db.collection("favoritos"+usuario)
                                            .document(documentID)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(DetalleProductoActiviyuser.this, "Eliminado correctamente", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(DetalleProductoActiviyuser.this, "Error no se ha eliminado correctamente", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }else{
                                    Toast.makeText(DetalleProductoActiviyuser.this, "ERROR", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
/*        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetalleProductoActiviy.this, MapaActivity.class);
                startActivity(intent);
            }
        });*/


    }
    /*@Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng miubicacion = new LatLng(latitud, longitud);

        mMap.addMarker(new MarkerOptions().position(miubicacion).title("Mi ubicación"));

        // Move the camera instantly to Sydney with a zoom of 15.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miubicacion, 15));

// Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

// Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

// Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(miubicacion)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                //.bearing(90)                // Sets the orientation of the camera to east
                //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }*/

    @Override
    public void onClick(View v) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> producto = new HashMap<>();
        producto.put("nombre", nombre);
        producto.put("categoria", categoria);
        producto.put("precio", precio);
        //producto.put("codigo", codigo);
        producto.put("imagen", imagen);
        producto.put("entock", entock);
        producto.put("usuario", usuario);
        //producto.put("latitud", latitud);
        //producto.put("longitud", longitud);
        producto.put("cantidad", 1);
        switch (v.getId()){

            case R.id.btn_compra:

                // Add a new document with a generated ID
                db.collection("pedidos"+usuario)
                    .add(producto)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.e("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(miActivity, "Has comprado el producto", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("TAG", "Error adding document", e);
                            Toast.makeText(miActivity, "Error comprando el producto", Toast.LENGTH_SHORT).show();
                        }
                    });
                db.collection("pedidos")
                        .add(producto)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.e("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                                Toast.makeText(miActivity, "Has comprado el producto", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "Error adding document", e);
                                Toast.makeText(miActivity, "Error comprando el producto", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.btn_addcarrito:
                // Add a new document with a generated ID
                db.collection("carrito"+usuario)
                        .add(producto)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.e("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                                Toast.makeText(miActivity, "Has marcado como fab el producto", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "Error adding document", e);
                                Toast.makeText(miActivity, "Error al marcar como fab el producto", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
        }
    }
}