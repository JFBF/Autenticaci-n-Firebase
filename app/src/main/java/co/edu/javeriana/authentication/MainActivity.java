package co.edu.javeriana.authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    //firebase	authentication
    private FirebaseAuth mAuth;
    private	FirebaseAuth.AuthStateListener mAuthListener;
    //
    private TextView user, mpassword;
    private Button login, signup;
    private boolean autentication = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth =	FirebaseAuth.getInstance();
        mAuthListener =	new	FirebaseAuth.AuthStateListener()	{
            @Override
            public	void	onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)	{
                FirebaseUser user	=	firebaseAuth.getCurrentUser();
                if	(user	!=	null)	{
                    //	User	is	signed	in
                    Toast.makeText(MainActivity.this, "onAuthStateChanged:signed_in:", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,	InicioActivity.class));
                }	else	{
                    //	User	is	signed	out
                    //Toast.makeText(MainActivity.this, "onAuthStateChanged:signed_out", Toast.LENGTH_SHORT).show();
                }
            }
        };

        user = (TextView) findViewById(R.id.editText);
        mpassword = (TextView) findViewById(R.id.editTextPassword);
        login = (Button) findViewById(R.id.button2);
        signup = (Button) findViewById(R.id.button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                    if(signin()){
                        Intent intent = new Intent(getApplicationContext(),InicioActivity.class);
                        startActivity(intent);
                    }
                }else
                    Toast.makeText(MainActivity.this, "Incorrecto", Toast.LENGTH_SHORT).show();

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegistroActivity.class);
                startActivity(intent);
            }
        });

    }

    private	boolean validateForm()	{
        boolean valid	=	true;
        String	email	=	user.getText().toString();
        if	(TextUtils.isEmpty(email))	{
            user.setError("Required.");
            valid	=	false;
        }else{
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                valid	=	false;
                user.setError("Format required.");
            }else
                user.setError(null);
        }
        String	password	=	mpassword.getText().toString();
        if	(TextUtils.isEmpty(password))	 {
            mpassword.setError("Required.");
            valid	=	false;
        }	else	{
            if(mpassword.length()<8){
                mpassword.setError("Min 8 caracteres");
            }else
                mpassword.setError(null);
        }
        return	valid;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private boolean signin(){
        String	email	=	user.getText().toString();
        final String	password	=	mpassword.getText().toString();
        autentication = false;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, "Fallo en la autenticación "+task.getException(), Toast.LENGTH_SHORT).show();
                            user.setText("");
                            mpassword.setText("");
                        }else autentication = true;
                    }
                });
        return autentication;
    }
}
