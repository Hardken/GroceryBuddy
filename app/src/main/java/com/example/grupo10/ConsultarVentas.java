package com.example.grupo10;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.grupo10.DetalleProductoActiviy;
import com.example.grupo10.R;
import com.example.grupo10.databinding.FragmentConsultarVentasBinding;
import com.example.grupo10.databinding.FragmentSlideshowBinding;
import com.example.grupo10.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConsultarVentas extends Fragment {

    private FragmentConsultarVentasBinding binding;
    private RecyclerView rev_fav;
    private RecyclerView.Adapter mAdapters;

    Button btnfil;
    EditText editText1;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_consultar_ventas,container, false);
        TextView precio=root.findViewById(R.id.venta);
        btnfil=root.findViewById(R.id.btnfind);
        editText1=root.findViewById(R.id.finduser);
        rev_fav = root.findViewById(R.id.rev_venta);
        rev_fav.setLayoutManager(new LinearLayoutManager(getActivity()));
        SharedPreferences mispreferencias = getActivity().getSharedPreferences(Constant.PREFERENCE, Context.MODE_PRIVATE);
        String usuario = mispreferencias.getString("usuario", "NO HAY USUARIO");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pedidos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            JSONArray productos = new JSONArray();
                            int pre=0;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.e("TAG", document.getId() + " => " + document.getData());

                                String nombre = document.getData().get("nombre").toString();
                                String usuarios = document.getData().get("usuario").toString();
                                String categoria = document.getData().get("categoria").toString();

                                Double precio = Double.parseDouble(document.getData().get("precio").toString());
                                Double precio2 = Double.parseDouble(document.getData().get("precio").toString());
                                pre += precio;
                                //boolean entock = Boolean.parseBoolean(document.getData().get("entock").toString());
                                String imagen = document.getData().get("imagen").toString();
                                //int cantidad = Integer.parseInt(document.getData().get("cantidad").toString());

                                Double latitud;
                                Double longitud;
                                try {
                                    latitud = Double.parseDouble(document.getData().get("latitud").toString());
                                    longitud = Double.parseDouble(document.getData().get("longitud").toString());
                                } catch (Exception e) {
                                    latitud = 0.0;
                                    longitud = 0.0;
                                }


                                JSONObject producto = new JSONObject();
                                try {
                                    producto.put("codigo", document.getId());
                                    producto.put("nombre", nombre);
                                    producto.put("usuario", usuarios);
                                    producto.put("categoria", categoria);
                                    producto.put("precio", precio);
                                    //producto.put("entock", entock);
                                    producto.put("imagen", imagen);
                                    producto.put("precioto",precio2);

                                    productos.put(producto);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                            mAdapters = new FavoritosAdapter(productos, getActivity());

                            rev_fav.setAdapter(mAdapters);

                            precio.setText("Venta total: $" + pre);

                        } else {
                            Log.e("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        btnfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user=editText1.getText().toString();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("pedidos"+user)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    JSONArray productos = new JSONArray();
                                    int pre=0;
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        Log.e("TAG", document.getId() + " => " + document.getData());

                                        String nombre = document.getData().get("nombre").toString();
                                        String usuarios = document.getData().get("usuario").toString();
                                        String categoria = document.getData().get("categoria").toString();

                                        int precio = Integer.parseInt(document.getData().get("precio").toString());
                                        int precio2 = Integer.parseInt(document.getData().get("precio").toString());
                                        pre += precio;
                                        //boolean entock = Boolean.parseBoolean(document.getData().get("entock").toString());
                                        String imagen = document.getData().get("imagen").toString();
                                        //int cantidad = Integer.parseInt(document.getData().get("cantidad").toString());

                                        Double latitud;
                                        Double longitud;
                                        try {
                                            latitud = Double.parseDouble(document.getData().get("latitud").toString());
                                            longitud = Double.parseDouble(document.getData().get("longitud").toString());
                                        } catch (Exception e) {
                                            latitud = 0.0;
                                            longitud = 0.0;
                                        }


                                        JSONObject producto = new JSONObject();
                                        try {
                                            producto.put("codigo", document.getId());
                                            producto.put("nombre", nombre);
                                            producto.put("usuario", usuarios);
                                            producto.put("categoria", categoria);
                                            producto.put("precio", precio);
                                            //producto.put("entock", entock);
                                            producto.put("imagen", imagen);
                                            producto.put("latitud", latitud);
                                            producto.put("longitud", longitud);
                                            producto.put("precioto",precio2);

                                            productos.put(producto);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }

                                    mAdapters = new FavoritosAdapter(productos, getActivity());

                                    rev_fav.setAdapter(mAdapters);

                                    precio.setText("Venta total: $" + pre);

                                } else {
                                    Log.e("TAG", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
        return root;
    }



    class FavoritosAdapter extends RecyclerView.Adapter<com.example.grupo10.ConsultarVentas.ViewHolder> {

        private final JSONArray productos;
        private final Activity miActividad;


        public FavoritosAdapter(JSONArray productos, Activity miActividad) {
            this.productos = productos;
            this.miActividad = miActividad;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View ve = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_ventas, parent, false);
            ViewHolder viewHolderfav = new ViewHolder(ve);
            return viewHolderfav;
        }


        @Override
        public void onBindViewHolder(com.example.grupo10.ConsultarVentas.ViewHolder holder, int position) {

            try {
                Log.e("POSICION", "POS: " + position);
                String nombre = productos.getJSONObject(position).getString("nombre");
                String usuario = productos.getJSONObject(position).getString("usuario");
                String categoria = productos.getJSONObject(position).getString("categoria");
                int precio = productos.getJSONObject(position).getInt("precio");


                //boolean entock = productos.getJSONObject(position).getBoolean("entock");
                String imagen = productos.getJSONObject(position).getString("imagen");
                Double latitud = productos.getJSONObject(position).getDouble("latitud");
                Double longitud = productos.getJSONObject(position).getDouble("longitud");
                //String codigo = productos.getJSONObject(position).getString("codigo");

                holder.tev_item_name.setText("Nombre: "+nombre);
                holder.tev_usuario.setText("Usuario: "+usuario);
                holder.tev_item_categoria.setText("Categoria: "+categoria);
                holder.tev_item_precio.setText("Precio: "+"$" + precio);


                if (imagen.equals("picasso")) {

                    holder.imv_item_producto.setImageResource(miActividad.getResources().getIdentifier(imagen, "drawable", miActividad.getPackageName()));

                    //holder.imv_item_producto.setImageDrawable(miActividad.getDrawable(R.drawable.picasso));
                } else {
                    Glide.with(miActividad)
                            .load(imagen)
                            .placeholder(new ColorDrawable(Color.BLACK))
                            .into(holder.imv_item_producto);
                }

                holder.imv_item_producto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Log.e("PRODUCTOS", productos.getJSONObject(position).toString());
                            /*Intent intent = new Intent(miActividad, DetalleProductoActiviy.class);

                            intent.putExtra("producto", productos.getJSONObject(position).toString());

                            miActividad.startActivity(intent);*/


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });



            } catch (JSONException e) {
                holder.tev_item_name.setText("error");
            }

        }

        @Override
        public int getItemCount() {
            Log.e("CANTIDAD_PRODUCTOS", "" + this.productos.length());
            return this.productos.length();
        }


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tev_item_name;
        private final TextView tev_item_categoria;
        private final TextView tev_usuario;
        private final TextView tev_item_precio;
        private final ImageView imv_item_producto;
        private Button btn_item_favorito;
        private Button btn_item_carrito;

        public ViewHolder(View v) {
            super(v);
            tev_item_name = v.findViewById(R.id.tev_item_name_pedidos);
            tev_usuario = v.findViewById(R.id.tev_item_usuario_pedidos);
            tev_item_categoria = v.findViewById(R.id.tev_item_categoria_pedidos);
            tev_item_precio = v.findViewById(R.id.tev_item_precio_pedidos);
            imv_item_producto = v.findViewById(R.id.imv_item_producto_pedidos);
            //btn_item_favorito = v.findViewById(R.id.btn_item_favorito_FAV);
            //btn_item_carrito = v.findViewById(R.id.btn_item_carrito_FAV);
        }
    }
}