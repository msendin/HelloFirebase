package demos.eps.udl.hellofirebase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class MainActivity extends Activity implements View.OnClickListener {

    FirebaseDatabase database;
    DatabaseReference myRef, myRef2,myr, myUser, myToken;

    FirebaseUser userID;

    TextView useruid;

    String uID;

    EditText edtlog, edtpass;
    private FirebaseAuth mAuth;

    TextView tv;

    private static final String TAG = "MainActivity";

    private final int RC_SIGN_IN = 1;

    private GoogleSignInClient mGoogleSignInClient;

    private MyFirebaseMessagingService mFMS;

/***
    private class setEventValueListener extends AsyncTask<DatabaseReference, Void, String> {

        protected String doInBackground(DatabaseReference... myRef) {
            myRef[0].addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);
                    tv.setText(value);
                    Log.d("TAG", "Value is: " + value);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("TAG", "Failed to read value.", error.toException());
                }
            });
            return "";
        }

        protected void onPostExecute(String st) {
            st = "";
        }
    }
**************/
/*
private final ActivityResultLauncher requestPermissionLauncher = registerForActivityResult(
        new ActivityResultContracts.RequestPermission()
    {
        isGranted:
        Boolean -> {
            if (isGranted) {
                Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(this, "FCM can't post notifications without POST_NOTIFICATIONS permission",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtlog = (EditText) findViewById(R.id.edtlogin);
        edtpass = (EditText) findViewById(R.id.edtpassw);

        useruid = findViewById(R.id.useruid);

        useruid.setText("");

        tv = findViewById(R.id.textgreet);

        Button b = findViewById(R.id.boto);
        b.setOnClickListener(this);

        Button breg = findViewById(R.id.botoReg);
        breg.setOnClickListener(this);

        Button blog = findViewById(R.id.botoLogin);
        blog.setOnClickListener(this);

        Button blogout = findViewById(R.id.botoLogout);
        blogout.setOnClickListener(this);

        //Button bafegir = (Button) findViewById(R.id.botoafguser);
        //bafegir.setOnClickListener(this);

        // OJO, no provat, caldria comprovar primer fent el check !!


        findViewById(R.id.sign_in_button).setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("message/greetings");

        //myr = database.getReference();

        //new setEventValueListener().execute();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                tv.setText(value);
                Log.d("TAG", "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });


        mFMS = new MyFirebaseMessagingService();

        // Read from the database

        //new AsyncAddValueEventL().execute();


        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);





        Button subscribeButton = (Button) findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // [START subscribe_topics]
                FirebaseMessaging.getInstance().subscribeToTopic("news")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = getString(R.string.msg_subscribed);
                                if (!task.isSuccessful()) {
                                    msg = getString(R.string.msg_subscribe_failed);
                                }
                                Log.d(TAG, msg);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });



        Button logTokenButton = (Button) findViewById(R.id.logTokenButton);
        logTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get token
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult();

                                // Log and toast
                                String msg = getString(R.string.msg_token_fmt, token);
                                Log.d(TAG, msg);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                mFMS.sendRegistrationToServer(token);
                            }
                        });
                // [END retrieve_current_token]
            }
        });

        //Toast.makeText(this, "See README for setup instructions", Toast.LENGTH_SHORT).show()
        //askNotificationPermission()


/*
                Intent it = new Intent(Intent.ACTION_SEND);

                it.setData(Uri.parse("mailto:"));
                it.setType("text/plain");

                it.putExtra(Intent.EXTRA_EMAIL, "msendinv@gmail.com");
                it.putExtra(Intent.EXTRA_TEXT, msg);
                it.putExtra(Intent.EXTRA_SUBJECT, "Reg Token Mòbil");

                //Intent chooser = Intent.createChooser(it,"Tria programa");
                startActivity(it);  */
       //     }
       // });

        /*FirebaseUser user = mAuth.getCurrentUser(); // mAuth is your current firebase auth instance
        user.getToken(true).addOnCompleteListener(this, new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    Log.d("TAG", "token=" + task.getResult().getToken());
                } else {
                    Log.e("TAG", "exception=" +task.getException().toString());
                }
            }
        });*/


        /*DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.e("TAG", "connected");
                } else {
                    Log.e("TAG", "not connected");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        }); */

  }

  /*
    private void askNotificationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

   */

/*
    private static class Device {
        private String token;
        private String type;

        public Device (String tk, String tp)  {
            token = tk;
            type = tp;
        }
    }
    */




    /*******************

    public void createDevice (String tk, DatabaseReference tRef) {

        //Map<String, Device> devices = new HashMap<String, Device>();
        //devices.put("Emulator", new Device(tk, "Emulator"));

        //Device device = new Device(tk, "Emulator");

        //tRef.child("Emulator1").setValue(device);

        //tRef.push().setValue(tk);
        tRef.setValue(tk);
    }

      ***********************/


    @SuppressLint("SetTextI18n")
    public void onClick (View v) {
        // Write a message to the database
        if (v.getId() == R.id.boto)
            myRef.setValue(getString(R.string.device_mssg));


        else if (v.getId() == R.id.botoReg){
            mAuth.createUserWithEmailAndPassword(edtlog.getText().toString(), edtpass.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("AUTH", "createUserWithEmail:onComplete:" + task.isSuccessful());
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            if (!task.isSuccessful()) {
                                Log.d("FIREBASE AUTH", "onComplete: Failed=" + Objects.requireNonNull(task.getException()).getMessage());
                                Toast.makeText(getApplicationContext(),"REGISTER FAILED",Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(getApplicationContext(),"REGISTER SUCCESFUL",Toast.LENGTH_SHORT).show();
                          }
                    });

/*
            mAuth.createUserWithEmailAndPassword(edtlog.getText().toString(), edtpass.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // ...
                        }
                    });  */
        }
        else if (v.getId() == R.id.botoLogin) {
            mAuth.signInWithEmailAndPassword(edtlog.getText().toString(), edtpass.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("FIREBASE AUTH", "signInWithEmail:onComplete:" + task.isSuccessful());

                                //myUser = database.getReference("users");
                               // myUser.child(String.valueOf(mAuth.getCurrentUser()));

                                //myUser.setValue(mAuth.getCurrentUser());

                                Toast.makeText(getApplicationContext(),"SIGN IN SUCCESFUL",Toast.LENGTH_SHORT).show();
                                updateUI(mAuth.getCurrentUser());
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                            } else {
                                Log.w("FIREBASE AUTH", "signInWithEmail:failed", task.getException());
                                Toast.makeText(getApplicationContext(), "SIGN IN FAILED",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Name, email address, and profile photo Url
                String name = user.getDisplayName();
                String email = user.getEmail();
                //Uri photoUrl = user.getPhotoUrl();

                // Check if user's email is verified
                boolean emailVerified = user.isEmailVerified();

                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getIdToken() instead.

                String uid = user.getUid();
                useruid.setText(email + '\n'+ uid + '\n');
            }
        }
        else if (v.getId() == R.id.botoLogout) {
            mAuth.signOut();
            Toast.makeText(this, "SIGN OUT SUCCESFUL", Toast.LENGTH_SHORT).show();
            updateUI(null);
        } //else Toast.makeText(this, "SIGN OUT FAILED", Toast.LENGTH_SHORT).show();


        else if (v.getId() == R.id.sign_in_button) {
            signIn();
        }


        /**********************  BOTO AFEGIR USUARI   ********************
        else if (v.getId() == R.id.botoafguser) {


            myRef2 = database.getReference("users/montse/username");
            myRef2.setValue("msendin");

            database.getReference().child("users").child("montse").child("username").setValue("msendin2");
        }

         **********************************************/
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
            // Signed in successfully, show authenticated UI.
            //updateUIGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInRoesult:failed code=" + e.getStatusCode());
            //updateUIGoogle(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(getApplicationContext(), "SIGN IN SUCCESFUL", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }



    @SuppressLint("SetTextI18n")
    void updateUI(FirebaseUser account) {
        if (account != null) {
            //Només x comptes de Google ??????????
            useruid.setText(account.getEmail() + '\n' + '\n' + account.getIdToken(true) + '\n');
            useruid.append(account.getUid());
        }
        else
            useruid.setText("");
    }


    //////////   NO USAT   ////////////////
    /*
    void updateUIGoogle(GoogleSignInAccount  account) {
        if (account != null)
            //Només x comptes de Google ??????????
            useruid.setText(account.getEmail() + '\n'+ account.getId() + '\n'+ account.getDisplayName() + '\n'+ account.getIdToken());
    }
    */

   /* private class AsyncAddValueEventL extends AsyncTask<Void, Void, Void> {
        public Void doInBackground (Void...params) {

            return null;
        }
    }*/


    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
/***********************   NO AQUEST
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUIGoogle(account);
 *****************/


/***********************   AQUEST ES EL BO, PERO EL TREC PQ NO SURTI A L'INICI JA EL TEXT, SINO Q S'ESPERI A FER EL LOGIN
 *
 */
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

    }


    @Override
    protected void onPause() {
        super.onPause();
        mAuth.signOut();
    }




}
