package com.example.grupo10.ui.home;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.grupo10.R;
import com.example.grupo10.databinding.FragmentHomeBinding;
import com.example.grupo10.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.protobuf.StringValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    String item;
    String nombre;
    Double val, cuenta;
    int cant, usertipe, pre;;
    String nomcant;
    String nompro;
    String cate;
    List<String> carnes;
    List<String> Lacteos;
    List<String> Verduleria;
    List<String> cantidades;

    Spinner spinnercat, spinnerpro, cantidad;
    EditText valor;

    TextView cuentatxt;
    Button genbtn, pedirbtn;

    private FragmentHomeBinding binding;
    MediaPlayer player, pl2;
    private Activity miActivity;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences mispreferencias = getActivity().getSharedPreferences(Constant.PREFERENCE, Context.MODE_PRIVATE);
        nombre = mispreferencias.getString("usuario", "NO HAY USUARIO");
        usertipe = mispreferencias.getInt("usertipe",2);
        cuenta = Double.parseDouble(mispreferencias.getString("cuenta", "0.0"));
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        spinnercat = root.findViewById(R.id.sppinercat);
        spinnerpro = root.findViewById(R.id.sppinerpro);
        cantidad = root.findViewById(R.id.cantxt);
        valor = root.findViewById(R.id.txtvalor);
        cuentatxt = root.findViewById(R.id.cuentatxt);
        valor.setInputType(InputType.TYPE_CLASS_NUMBER);
        genbtn = root.findViewById(R.id.generarbtn);
        pedirbtn = root.findViewById(R.id.pedirbtn);
        getcuenta(nombre);
        String[] categorias = new String[]{"Carnes", "Quesos", "Verduleria", "Frutas"};
        String[] frutas = new String[]{"Manzana", "Naranja", "Banano", "Uva", "Mango", "Papaya", "Piña", "Aguacate", "Mandarina", "Fresa"};
        carnes = new ArrayList<>();
        carnes.add("Pechuga de pollo");
        carnes.add("Muslos de pollo");
        carnes.add("Solomito de res");
        carnes.add("Pechuga de cerdo");
        carnes.add("Lomo de cerdo");
        carnes.add("Sobrebarriga");
        Lacteos = new ArrayList<>();
        Lacteos.add("Queso tajado");
        Lacteos.add("Queso costeño");
        Lacteos.add("Queso campesino");
        Lacteos.add("Queso parmesano");
        Verduleria = new ArrayList<>();
        Verduleria.add("Tomate");
        Verduleria.add("Cebolla");
        Verduleria.add("Papa");
        Verduleria.add("Yuca");
        Verduleria.add("Zanahoria");
        Verduleria.add("Cebolla larga");
        Verduleria.add("Ajo");
        Verduleria.add("Pepino");
        Verduleria.add("Mazorca");
        Verduleria.add("Brocoli");
        Verduleria.add("Coliflor");
        Verduleria.add("Lechuga");
        Verduleria.add("Pimiento");
        Verduleria.add("Remolacha");
        Verduleria.add("Champiñon");
        cantidades = new ArrayList<String>();
        cantidades.add("1/2 libra");
        cantidades.add("1 libra");
        cantidades.add("1 libra y media");
        cantidades.add("2 libras");
        cantidades.add("3 Libras");
        cantidades.add("4 libras");
        cantidades.add("5 libras");
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, categorias);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnercat.setAdapter(adaptador);

        spinnercat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                item = (String) spinnercat.getItemAtPosition(i);

                switch (item) {
                    case "Carnes":
                        spinnerpro.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, carnes));
                        cantidad.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, cantidades));

                        break;
                    case "Quesos":
                        spinnerpro.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, Lacteos));                        cantidad.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, cantidades));
                        cantidad.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, cantidades));

                        break;
                    case "Verduleria":
                        spinnerpro.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, Verduleria));                        cantidad.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, cantidades));
                        cantidad.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, cantidades));

                        break;
                    case "Frutas":
                        ArrayAdapter<String> fru = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, frutas);
                        fru.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerpro.setAdapter(fru);
                        cantidad.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, cantidades));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        genbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genfuntion();
            }
        });
        pedirbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genfuntion();
                Toast.makeText(getContext(), "valor: "+ String.valueOf(val), Toast.LENGTH_SHORT).show();
                //SharedPreferences mispreferencias = miActivity.getSharedPreferences(Constant.PREFERENCE, Context.MODE_PRIVATE);
                //String usuario = mispreferencias.getString("usuario", "NO HAY USUARIO");
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> producto = new HashMap<>();
                producto.put("nombre", nomcant+" "+nompro);
                producto.put("categoria", cate);
                producto.put("precio", val);
                producto.put("imagen", "https://www.clara.es/recetas/recetas-pechuga-pollo_16195");
                producto.put("usuario", nombre);
                producto.put("fecha", fecha());
                producto.put("cantidad", 1);
                db.collection("pedidos_"+nombre)
                        .add(producto)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                //Log.e("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                                actcuenta(nombre);
                                Toast.makeText(getContext(), "Has comprado el producto", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "Error adding document", e);
                                Toast.makeText(getContext(), "Error comprando el producto", Toast.LENGTH_SHORT).show();
                            }
                        });
                db.collection("pedidos")
                        .add(producto)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.e("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                                Toast.makeText(getContext(), "Has comprado el producto", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "Error adding document", e);
                                Toast.makeText(getContext(), "Error comprando el producto", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return root;
    }


    public void interfuncion(int lib){
        nompro = String.valueOf(spinnerpro.getSelectedItem());
        Toast.makeText(getContext(),"cartegoria: "+cate+" "+"Producto :"+nompro,Toast.LENGTH_SHORT).show();
        if (valor.getText().toString().isEmpty()==false){
            val= (Double.parseDouble(valor.getText().toString()));
            nomcant = String.valueOf(val);
        }else{
            nomcant  =String.valueOf(cantidad.getSelectedItem());
            cant = Integer.parseInt(String.valueOf(cantidad.getSelectedItemId()));
            switch (cant){
                case 0:
                    val = Double.parseDouble(String.valueOf((lib*1)/2));
                    valor.setText(val.toString());
                    break;
                case 1:
                    val = Double.parseDouble(String.valueOf((lib*1)/1));
                    valor.setText(val.toString());
                    break;
                case 2:
                    val = Double.parseDouble(String.valueOf((1.5*lib)/1));
                    valor.setText(val.toString());
                    break;
                case 3:
                    val = Double.parseDouble(String.valueOf((2*lib)/1));
                    valor.setText(val.toString());
                    break;
                case 4:
                    val = Double.parseDouble(String.valueOf((3*lib)/1));
                    valor.setText(val.toString());
                    break;
                case 5:
                    val = Double.parseDouble(String.valueOf((4*lib)/1));
                    valor.setText(val.toString());
                    break;
                case 6:
                    val = Double.parseDouble(String.valueOf((5*lib)/1));
                    valor.setText(val.toString());
                    break;
            }
        }
    }

    public void genfuntion(){
        cate = (String.valueOf(spinnercat.getSelectedItem()));
        int lib;
        switch (cate){
            case "Carnes":
                switch (Integer.parseInt(String.valueOf(spinnerpro.getSelectedItemId()))){
                    case 0:
                        lib = 14000;
                        interfuncion(lib);
                        break;
                    case 1:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 2:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 3:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 4:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 5:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    default:
                        break;
                }
                break;
            case "Quesos":
                switch (Integer.parseInt(String.valueOf(spinnerpro.getSelectedItemId()))){
                    case 0:
                        lib = 14000;
                        interfuncion(lib);
                        break;
                    case 1:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 2:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 3:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 4:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 5:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    default:
                        break;
                }
                break;
            case "Verduleria":
                switch (Integer.parseInt(String.valueOf(spinnerpro.getSelectedItemId()))){
                    case 0:
                        lib = 14000;
                        interfuncion(lib);
                        break;
                    case 1:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 2:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 3:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 4:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 5:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    default:
                        break;
                }
                break;
            case "Frutas":
                switch (Integer.parseInt(String.valueOf(spinnerpro.getSelectedItemId()))){
                    case 0:
                        lib = 14000;
                        interfuncion(lib);
                        break;
                    case 1:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 2:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 3:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 4:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    case 5:
                        lib = 16000;
                        interfuncion(lib);
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public void getcuenta(String nombre){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pedidos_"+nombre)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            JSONArray productos = new JSONArray();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.e("TAG", document.getId() + " => " + document.getData());


                                Double precio = Double.parseDouble(document.getData().get("precio").toString());
                                Double precio2 = Double.parseDouble(document.getData().get("precio").toString());
                                cuenta += precio;
                                actcuenta(nombre);

                                JSONObject producto = new JSONObject();
                                try {
                                    producto.put("precio", precio);
                                    producto.put("precioto",precio2);

                                    productos.put(producto);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                            cuentatxt.setText("Cuenta total: $" + cuenta);

                        } else {
                            Log.e("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void actcuenta(String nombre){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> usuario = new HashMap<>();
        //usuario.put("devtoken", "");
        usuario.put("cuenta", cuenta);
        db.collection("usuarios")
                .whereEqualTo("correo", nombre)
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
    public void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        // Log and toast
                        System.out.println(token);
                        Toast.makeText(getContext(), "tokes is: "+token, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public String fecha(){
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}