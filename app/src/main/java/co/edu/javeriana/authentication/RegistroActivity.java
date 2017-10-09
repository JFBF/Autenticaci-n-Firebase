package co.edu.javeriana.authentication;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class RegistroActivity extends AppCompatActivity {

    private Button registrarme;
    private EditText nombre,apellido,contraseña,correo;
    private FirebaseAuth mAuth ;

    final int MY_PERMISSIONS_REQUEST_CAMARA = 1;
    final int IMAGE_PICKER_REQUEST = 2;
    Button Galeria,Foto;
    ImageView Igaleria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        registrarme = (Button) findViewById(R.id.buttonRegistrarme);
        nombre = (EditText)findViewById(R.id.editTextRNombre);
        apellido = (EditText)findViewById(R.id.editTextRApellio);
        contraseña = (EditText)findViewById(R.id.editTextRPassword);
        correo = (EditText)findViewById(R.id.editTextRCorreo);
        registrarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()){
                    registrar();
                }
            }
        });

        Igaleria = (ImageView) findViewById(R.id.imageView);
        Galeria = (Button) findViewById(R.id.Bgaleria);
        Foto = (Button) findViewById(R.id.Bcamara);
        Galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE);
                if(permissionCheck == 0){
                    galeriaSelection();
                }else
                    solicitudPermisoGaleria();
            }
        });

        Foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.CAMERA);
                if(permissionCheck == 0){
                    takePicture();
                }else
                    solicitudPermisoCamara();
            }
        });

    }

    private	boolean validateForm()	{
        boolean valid	=	true;
        String	email	=	correo.getText().toString();
        if	(TextUtils.isEmpty(email))	{
            correo.setError("Required.");
            valid	=	false;
        }else{
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                valid	=	false;
                correo.setError("Format required.");
            }else
                correo.setError(null);
        }
        String	password	=	contraseña.getText().toString();
        if	(TextUtils.isEmpty(password))	 {
            contraseña.setError("Required.");
            valid	=	false;
        }	else	{
            if(contraseña.length()<8){
                contraseña.setError("Min 8 caracteres");
            }else
                contraseña.setError(null);
        }
        String	name	=	nombre.getText().toString();
        if	(TextUtils.isEmpty(name))	 {
            nombre.setError("Required.");
            valid	=	false;
        }	else	{
            nombre.setError(null);
        }
        String	nick	=	apellido.getText().toString();
        if	(TextUtils.isEmpty(nick))	 {
            apellido.setError("Required.");
            valid	=	false;
        }	else	{
            apellido.setError(null);
        }
        return	valid;
    }

    private void registrar(){
        String c = correo.getText().toString();
        String p = contraseña.getText().toString();
        mAuth.createUserWithEmailAndPassword(c,p)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)	{
                        if(task.isSuccessful()){
                            // Log.d(TAG,	"createUserWithEmail:onComplete:"	+	task.isSuccessful());
                            FirebaseUser user	=	mAuth.getCurrentUser();
                            if(user!=null){	//Update	user	Info
                                UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                                upcrb.setDisplayName(nombre.getText().toString()+" "+apellido.getText().toString());
                                // upcrb.setPhotoUri(Uri.parse("path/to/pic"));//fake	 uri,	real	one	coming	soon
                                user.updateProfile(upcrb.build());
                                startActivity(new Intent(RegistroActivity.this,	InicioActivity.class));	//o		en	el	listener
                            }
                        }
                        if	(!task.isSuccessful())	 {
                            Toast.makeText(RegistroActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMARA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(getApplicationContext(),"Permiso denegado camara", Toast.LENGTH_LONG).show();
                }
                return;
            }case IMAGE_PICKER_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galeriaSelection();
                } else {
                    Toast.makeText(getApplicationContext(),"Permiso denegado galeria", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void solicitudPermisoCamara (){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Se necesita el permiso para poder mostrar la imagen!", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMARA);


        }
    }
    private void solicitudPermisoGaleria (){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Se necesita el permiso para poder mostrar la imagen!", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    IMAGE_PICKER_REQUEST);


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case IMAGE_PICKER_REQUEST:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        System.out.println("uri es "+imageUri);
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        Igaleria.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case MY_PERMISSIONS_REQUEST_CAMARA:
                if(resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    Igaleria.setImageBitmap(imageBitmap);
                    // como obtener URI
                }
                break;
        }
    }

    private void galeriaSelection(){
        Intent pickImage = new Intent(Intent.ACTION_PICK);
        pickImage.setType("image/*");
        startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
    }

    private void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, MY_PERMISSIONS_REQUEST_CAMARA);
        }
    }
}
