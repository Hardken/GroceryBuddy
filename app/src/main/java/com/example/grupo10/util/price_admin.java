package com.example.grupo10.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import com.example.grupo10.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link price_admin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class price_admin extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String nombre, pro, cat, item;
    double precio;
    List<String> carnes;
    List<String> Lacteos;
    List<String> Verduleria;
    Spinner spinnercat, spinnerpro;
    EditText valor;
    TextView cuentatxt;
    Button genbtn, pedirbtn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public price_admin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment price_admin.
     */
    // TODO: Rename and change types and number of parameters
    public static price_admin newInstance(String param1, String param2) {
        price_admin fragment = new price_admin();
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
        SharedPreferences mispreferencias = getActivity().getSharedPreferences(Constant.PREFERENCE, Context.MODE_PRIVATE);
        nombre = mispreferencias.getString("usuario", "NO HAY USUARIO");
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_price_admin, container, false);
        spinnercat = root.findViewById(R.id.pricesppinercat);
        spinnerpro = root.findViewById(R.id.pricesppinerpro);
        valor = root.findViewById(R.id.pricetxtvalor);
        cuentatxt = root.findViewById(R.id.pricetextadmin);
        valor.setInputType(InputType.TYPE_CLASS_NUMBER);
        genbtn = root.findViewById(R.id.pricegenerarbtn);
        pedirbtn = root.findViewById(R.id.pricepedirbtn);
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


                        break;
                    case "Quesos":
                        spinnerpro.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, Lacteos));


                        break;
                    case "Verduleria":
                        spinnerpro.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, Verduleria));


                        break;
                    case "Frutas":
                        ArrayAdapter<String> fru = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, frutas);
                        fru.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerpro.setAdapter(fru);

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
                pro = spinnerpro.getSelectedItem().toString();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("precios")
                        .whereEqualTo("nombre", pro)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            JSONArray productos = new JSONArray();
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()){
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        precio = Double.parseDouble(document.getData().get("precio").toString());
                                    }
                                    valor.setText(String.valueOf(precio));
                                }else {
                                    Log.e("TAG", "Error getting documents: ", task.getException());
                                }

                            }
                        });
            }
        });
        pedirbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    precio = Double.parseDouble(valor.getText().toString());
                    pro = spinnerpro.getSelectedItem().toString();
                    cat = spinnercat.getSelectedItem().toString();
                actualizarprecio(precio);
            }
        });
        return root;
    }

    public void actualizarprecio(double precio){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("precios");
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // La colección existe, hacer algo si es necesario
                        Map<String, Object> price = new HashMap<>();
                        price.put("nombre", pro);
                        price.put("categorias", cat);
                        price.put("precio", precio);
                        collectionRef.whereEqualTo("nombre", pro)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if(querySnapshot != null && !querySnapshot.isEmpty()) {
                                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                            String documentID = documentSnapshot.getId();
                                            db.collection("precios")
                                                    .document(documentID)
                                                    .update(price)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            valor.setText("");
                                                            Toast.makeText(getActivity(), "Actualizacion Correcta", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getActivity(), "Actualizacion incorrecta", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }else{
                                            Map<String, Object> price = new HashMap<>();
                                            price.put("nombre", pro);
                                            price.put("categorias", cat);
                                            price.put("precio", precio);
                                            db.collection("precios")
                                                    .add(price)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(getActivity(), "Operacion exitosa",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                            Toast.makeText(getContext(), "La colección no existe.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                        Toast.makeText(getContext(), "La colección existe.", Toast.LENGTH_SHORT).show();

                    } else {
                        // La colección no existe
                        Map<String, Object> price = new HashMap<>();
                        price.put("nombre", pro);
                        price.put("categorias", cat);
                        price.put("precio", precio);
                        db.collection("precios")
                                .add(price)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(getActivity(), "Operacion exitosa",Toast.LENGTH_SHORT).show();
                                    }
                                });
                        Toast.makeText(getContext(), "La colección no existe.", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    // Error al obtener la referencia de la colección

                }
            }
        });




    }
}

