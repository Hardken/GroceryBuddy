package com.example.grupo10;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.grupo10.ui.gallery.GalleryFragment;
import com.example.grupo10.ui.home.HomeFragment;
import com.example.grupo10.util.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo10.databinding.ActivityMenuBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenuBinding binding;
    private ActivityMenuBinding binding2;
    private SharedPreferences mispreferencias;
    private FirebaseAuth mAuth;
    private Activity miactividad;
    ImageView navImage;
    Uri data1;
    String urlImage;
    final  int OPEN_GALLERY = 1;
    String usuario;
    String userne;
    int usuario2;
    MediaPlayer player;

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mispreferencias = getSharedPreferences(Constant.PREFERENCE, MODE_PRIVATE);

        usuario = mispreferencias.getString("usuario", "NO HAY USUARIO");
        //String contrasena = mispreferencias.getString("contrasena", "NO HAY CONTRASEÑA");
        String nombre = mispreferencias.getString("nombre", "NO HAY User");
        String imagen = mispreferencias.getString("imagen","");


        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMenu.toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        //Intent intent = new Intent(MenuActivity.this, MenuActivity.class);
        Bundle bundle2 = getIntent().getExtras();
        if (bundle2 != null) {
            usuario2 = bundle2.getInt("usertipe");
            userne = bundle2.getString("usuario");
            // ...
        }

        Bundle args = new Bundle();
        args.putInt("usertipe", usuario2);


        miactividad = this;
        mAuth = FirebaseAuth.getInstance();

        binding.appBarMenu.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();

                //Navigation.findNavController(miactividad, R.id.nav_host_fragment).navigate(R.id.nav_producto);


            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_producto)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View headview = navigationView.getHeaderView(0);
        TextView name = headview.findViewById(R.id.tev_header_nombre);
        TextView correo = headview.findViewById(R.id.tev_header_correo);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()

/*
                if (id==R.id.nav_gallery){
                    GalleryFragment galleryFragment = new GalleryFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                            .beginTransaction();
                    Bundle bundle2 = getIntent().getExtras();
                    Bundle data = new Bundle();
                    data.putString("myData",bundle2.getString(usuario));
                    galleryFragment.setArguments(data);
                    fragmentTransaction.replace(R.id.drawer_layout,galleryFragment).commit();

                }*/

                if (id==R.id.nav_salir){

                    SharedPreferences.Editor editor = mispreferencias.edit();

                    editor.putString("usuarioa", "");
                    editor.putString("contrasena", "");
                    editor.putString("nombre", "");
                    editor.putString("imagen", "");
                    editor.commit();

                    //FirebaseAuth.getInstance().signOut();
                    //editor.clear();// si no se tiene informacion en el preference o sea no es aplicacion offline


                    Intent intent = new Intent(miactividad, LoginActivity.class);
                    startActivity(intent);
                    player = MediaPlayer.create(MenuActivity.this,R.raw.mepicanloscocos);
                    player.start();

                    finish();
                    return true;

                }
                //This is for maintaining the behavior of the Navigation view
                NavigationUI.onNavDestinationSelected(menuItem,navController);
                //This is for closing the drawer after acting on it
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Log.e("BUN_USUARIO", bundle.getString("usuario"));
            Log.e("BUN_CONTRASENA", bundle.getString("contrasena"));
        }


        View headerView = navigationView.getHeaderView(0);
        TextView navNombre = headerView.findViewById(R.id.tev_header_nombre);
        TextView navCorreo = headerView.findViewById(R.id.tev_header_correo);
        navImage = headerView.findViewById(R.id.imv_header_imagen);
        navImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), OPEN_GALLERY);

            }
        });
            Glide.with(miactividad)
                    .load(imagen)
                    .placeholder(R.drawable.avatar)
                    .into(navImage);


        navNombre.setText(nombre);
        navCorreo.setText(usuario);

        //if (usuario.equals("admin@admin.com") || usuario.equals("carlos@arango.com")) {
        //    fab.setVisibility(View.VISIBLE);
        //}
        // set Fragmentclass Arguments
        if (usuario.contains("@grupo10.com")) {
            fab.setVisibility(View.VISIBLE);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {

                data1 = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data1);
                    navImage.setImageBitmap(bitmap);

                    subirImagen();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(this, "ERROR CON LA IMAGEN", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void subirImagen() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        //if there is a file to upload
        if (data1 != null) {
            //displaying a progress dialog while upload is going on

            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String strDate = sdf.format(c.getTime());
            String nombreImagen = strDate + ".jpg";

            mispreferencias = getSharedPreferences(Constant.PREFERENCE, MODE_PRIVATE);

            String usuario = mispreferencias.getString("usuario", "NO HAY USUARIO");


            StorageReference riversRef = storageReference.child(usuario + "/" + nombreImagen);
            riversRef.putFile(data1)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog

                            //and displaying a success toast

                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urlImage = uri.toString();
                                    Log.e("URL_IMAGE", urlImage);

                                    SharedPreferences.Editor editor = mispreferencias.edit();
                                    editor.putString("imagen", urlImage);
                                    editor.commit();

                                    Map<String, Object> data = new HashMap<>();
                                    data.put("imagen", urlImage);

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("usuarios").document(usuario)
                                            .set(data, SetOptions.merge());


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


                            //and displaying error message
                            Toast.makeText(miactividad, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }

}