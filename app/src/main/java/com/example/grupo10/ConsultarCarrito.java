package com.example.grupo10;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.grupo10.ui.slideshow.SlideshowFragment;
import com.example.grupo10.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsultarCarrito#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultarCarrito extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConsultarCarrito() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsultarCarrito.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsultarCarrito newInstance(String param1, String param2) {
        ConsultarCarrito fragment = new ConsultarCarrito();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView rev_fav;
    private RecyclerView.Adapter mAdapters;

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
        View root = inflater.inflate(R.layout.fragment_consultar_carrito, container, false);

        rev_fav = root.findViewById(R.id.rev_car);
        rev_fav.setLayoutManager(new LinearLayoutManager(getActivity()));
        SharedPreferences mispreferencias = getActivity().getSharedPreferences(Constant.PREFERENCE, Context.MODE_PRIVATE);
        String usuario = mispreferencias.getString("usuario", "NO HAY USUARIO");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("carrito"+usuario)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            JSONArray productos = new JSONArray();

                            for (QueryDocumentSnapshot document : task.getResult()){
                                String nombre = document.getData().get("nombre").toString();
                                String categoria = document.getData().get("categoria").toString();
                                int precio = Integer.parseInt(document.getData().get("precio").toString());
                                boolean entock = Boolean.parseBoolean(document.getData().get("entock").toString());
                                String imagen = document.getData().get("imagen").toString();

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
                                    producto.put("categoria", categoria);
                                    producto.put("precio", precio);
                                    producto.put("entock", entock);
                                    producto.put("imagen", imagen);
                                    producto.put("latitud", latitud);
                                    producto.put("longitud", longitud);

                                    productos.put(producto);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            mAdapters = new CarAdapter(productos, getActivity());

                            rev_fav.setAdapter(mAdapters);
                        }else {
                            Log.e("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return root;
    }

    class CarAdapter extends RecyclerView.Adapter<com.example.grupo10.ConsultarCarrito.ViewHolder> {

        private final JSONArray productos;
        private final Activity miActividad;
        SharedPreferences mispreferencias = getActivity().getSharedPreferences(Constant.PREFERENCE, Context.MODE_PRIVATE);
        String usuario = mispreferencias.getString("usuario", "NO HAY USUARIO");
        FirebaseFirestore db;
        public CarAdapter(JSONArray productos, Activity miActividad) {
            this.productos = productos;
            this.miActividad = miActividad;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View ve = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_carrito, parent, false);
            ConsultarCarrito.ViewHolder viewHolderfav = new ViewHolder(ve);
            db =  FirebaseFirestore.getInstance();
            return viewHolderfav;
        }


        @Override
        public void onBindViewHolder(com.example.grupo10.ConsultarCarrito.ViewHolder holder, int position) {

            try {
                Log.e("POSICION", "POS: " + position);
                String nombre = productos.getJSONObject(position).getString("nombre");
                String categoria = productos.getJSONObject(position).getString("categoria");
                int precio = productos.getJSONObject(position).getInt("precio");
                boolean entock = productos.getJSONObject(position).getBoolean("entock");
                String imagen = productos.getJSONObject(position).getString("imagen");
                Double latitud = productos.getJSONObject(position).getDouble("latitud");
                Double longitud = productos.getJSONObject(position).getDouble("longitud");
                //String codigo = productos.getJSONObject(position).getString("codigo");

                holder.tev_item_name.setText(nombre);
                holder.tev_item_categoria.setText(categoria);
                holder.tev_item_precio.setText("$" + precio);


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
                            //Log.e("PRODUCTOS", productos.getJSONObject(position).toString());
                            Intent intent = new Intent(miActividad, DetalleProductoActiviy.class);

                            intent.putExtra("producto", productos.getJSONObject(position).toString());

                            miActividad.startActivity(intent);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                holder.btn_item_del_car.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.collection("carrito" + usuario)
                                .whereEqualTo("nombre", nombre)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful() && !task.getResult().isEmpty()){
                                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                            String documentID = documentSnapshot.getId();
                                            db.collection("carrito" + usuario)
                                                    .document(documentID)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getActivity(), "Eliminado correctamente", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getActivity(), "Error no se ha eliminado correctamente", Toast.LENGTH_SHORT).show();

                                                        }
                                                    });
                                        }else{
                                            Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
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
        private final TextView tev_item_precio;
        private final ImageView imv_item_producto;
        private Button btn_item_del_car;

        public ViewHolder(View v) {
            super(v);
            tev_item_name = v.findViewById(R.id.tev_item_name_car);
            tev_item_categoria = v.findViewById(R.id.tev_item_categoria_car);
            tev_item_precio = v.findViewById(R.id.tev_item_precio_car);
            imv_item_producto = v.findViewById(R.id.imv_item_producto_car);
            btn_item_del_car = v.findViewById(R.id.btn_item_delete_car);
        }
    }
}