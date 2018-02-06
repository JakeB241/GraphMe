package umich.jakebock.trackme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import umich.jakebock.trackme.R;

public class LoginActivity extends AppCompatActivity
{
    private FirebaseAuth firebaseAuth;

    public static String currentUserId;

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Call the Super
        super.onCreate(savedInstanceState);

        // Create the View
        setContentView(R.layout.activity_login);

        // Initialize the Google Sign In Button
        initializeSignInButton();

        // Get the FireBase Auth Instance
        firebaseAuth = FirebaseAuth.getInstance();

        // Set the Offline Data to True
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
    }

    @Override
    public void onStart()
    {
        // Call the Super
        super.onStart();

        // Fetch the User ID Token and Launch Main Activity
        startMainActivity(firebaseAuth.getCurrentUser());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try
            {
                firebaseAuthWithGoogle(task.getResult(ApiException.class));
            }

            catch (ApiException e)
            {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    // Sign In Success
                    startMainActivity(firebaseAuth.getCurrentUser());
                }

                else
                {
                    // If sign in fails, display a message to the user.
                    //Snackbar.make(findViewById(R.id.lo), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                    //Toast.makeText(this, "Authentican Failed", Toast.LENGTH_LONG).show();
                    //updateUI(null);
                }
            }
        });
    }

    private void initializeSignInButton()
    {
        // Fetch the Sign In Button
        SignInButton signInButton = findViewById(R.id.google_sign_in_button);

        // Set the Size of the Sign in Button
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        // Set the Listener for the Sign In Button
        signInButton.setOnClickListener(googleSignInClickListener);
    }

    private void startMainActivity(FirebaseUser currentUser)
    {
        if (currentUser != null)
        {
            // Fetch Current User ID
            currentUserId = currentUser.getUid();

            // Start the Main Activity
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    // Create the Listener for the Google Sign In Button
    private View.OnClickListener googleSignInClickListener = new View.OnClickListener()
    {
        public void onClick(View view)
        {
            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

            // Build a GoogleSignInClient with the options specified by gso.
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

            // Sign In
            startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
        }
    };
}
