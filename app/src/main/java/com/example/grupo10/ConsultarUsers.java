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
import androidx.fragment.app.FragmentTransaction;
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
 * Use the {@link ConsultarUsers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultarUsers extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConsultarUsers() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsultarUsers.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsultarUsers newInstance(String param1, String param2) {
        ConsultarUsers fragment = new ConsultarUsers();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView rev_fav;
    private RecyclerView.Adapter mAdapters;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_consultar_users,container, false);
        rev_fav = root.findViewById(R.id.rev_users);
        rev_fav.setLayoutManager(new LinearLayoutManager(getActivity()));
        SharedPreferences mispreferencias = getActivity().getSharedPreferences(Constant.PREFERENCE, Context.MODE_PRIVATE);
        String usuario = mispreferencias.getString("usuario", "NO HAY USUARIO");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            JSONArray usuarios = new JSONArray();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("TAG", document.getId() + " => " + document.getData());

                                String nombre = document.getData().get("nombre").toString();
                                String apellido = document.getData().get("apellido").toString();
                                String correo = document.getData().get("correo").toString();
                                String contreña = document.getData().get("contrasena").toString();
                                String imagen = document.getData().get("imagen").toString();

                                JSONObject usuario = new JSONObject();
                                try {
                                    usuario.put("codigo", document.getId());
                                    usuario.put("nombre", nombre);
                                    usuario.put("apellido", apellido);
                                    usuario.put("correo", correo);
                                    usuario.put("contrasena", contreña);
                                    usuario.put("imagen", imagen);
                                    usuarios.put(usuario);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                            mAdapters = new FavoritosAdapter(usuarios, getActivity());

                            rev_fav.setAdapter(mAdapters);

                            //rev_fav.setAdapter(mAdapters);


                        } else {
                            Log.e("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

        return root;
    }

    class FavoritosAdapter extends RecyclerView.Adapter<com.example.grupo10.ConsultarUsers.ViewHolder> {

        private final JSONArray usuarios;
        private final Activity miActividad;
        FirebaseFirestore db;

        public FavoritosAdapter(JSONArray productos, Activity miActividad) {
            this.usuarios = productos;
            this.miActividad = miActividad;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View ve = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_users, parent, false);
            ConsultarUsers.ViewHolder viewHolderfav = new ViewHolder(ve);
            db = FirebaseFirestore.getInstance();
            return viewHolderfav;
        }


        @Override
        public void onBindViewHolder(com.example.grupo10.ConsultarUsers.ViewHolder holder, int position) {

            try {
                Log.e("POSICION", "POS: " + position);
                String nombre = usuarios.getJSONObject(position).getString("nombre");
                String apellido = usuarios.getJSONObject(position).getString("apellido");
                String correo = usuarios.getJSONObject(position).getString("correo");
                String imagen = usuarios.getJSONObject(position).getString("imagen");
                String contraseña = usuarios.getJSONObject(position).getString("contrasena");


                holder.tev_item_name.setText("Nombre: "+nombre);
                holder.tev_usuario.setText("Apellido: "+apellido);
                holder.tev_item_categoria.setText("Correo: "+correo);


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
                            //Log.e("PRODUCTOS", usuarios.getJSONObject(position).toString());

                            Intent intent = new Intent(miActividad, PerfilActivity.class);

                            intent.putExtra("usuario", usuarios.getJSONObject(position).toString());

                            miActividad.startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                holder.btn_user_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            //Log.e("PRODUCTOS", usuarios.getJSONObject(position).toString());

                            Intent intent = new Intent(miActividad, PerfilActivity.class);

                            intent.putExtra("usuario", usuarios.getJSONObject(position).toString());

                            miActividad.startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                holder.btn_user_delete.setOnClickListener(new View.OnClickListener() {
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
            Log.e("CANTIDAD_PRODUCTOS", "" + this.usuarios.length());
            return this.usuarios.length();
        }


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tev_item_name;
        private final TextView tev_item_categoria;
        private final TextView tev_usuario;
        private final ImageView imv_item_producto;
        private final Button btn_user_edit;
        private final Button btn_user_delete;

        public ViewHolder(View v) {
            super(v);
            tev_item_name = v.findViewById(R.id.tev_item_name_users);
            tev_usuario = v.findViewById(R.id.tev_item_apellido_users);
            tev_item_categoria = v.findViewById(R.id.tev_item_Correo_users);
            imv_item_producto =  v.findViewById(R.id.imv_item_users);
            btn_user_edit =  v.findViewById(R.id.btn_user_edit);
            btn_user_delete =  v.findViewById(R.id.btn_user_delete);

        }
    }
}