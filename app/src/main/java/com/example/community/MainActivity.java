package com.example.community;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.text.format.DateFormat;
import android.widget.Toast;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private Button btnCl;
    private FirebaseAuth auth;
    private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if(o.getResultCode() == Activity.RESULT_OK)
            {
                Intent intent = o.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    assert account != null;
                    firebaseAuthWithGoogle(account.getIdToken());

                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(String.valueOf(com.firebase.ui.auth.R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));


        btnCl = findViewById(R.id.button_set);
        btnCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText textField = findViewById(R.id.messageField);
                String sTextField = textField.getText().toString();

                if (textField.getText().toString().trim().length() != 0) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .push()
                            .setValue(new Message(
                                    "admin",
                                    textField.getText().toString())

                            );
                }

                textField.setText("");
            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query messages = database.getReference();

        FirebaseListOptions<Message> options = new FirebaseListOptions.Builder<Message>().setQuery(messages, Message.class).setLayout(R.layout.item).build();

        ListView messageList = findViewById(R.id.list_view);
        FirebaseListAdapter<Message> adapter = new FirebaseListAdapter<Message>(options){
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView messageText = v.findViewById(R.id.message_Text);
                TextView messageUser = v.findViewById(R.id.message_User);
                TextView messageTime = v.findViewById(R.id.message_Time);

                messageText.setText(model.getMessage_text());
                messageUser.setText(model.getMessage_name());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessage_time()));
            }
        };

        messageList.setAdapter(adapter);
        adapter.startListening();

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                    Toast.makeText(MainActivity.this, "Successful login",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Filed login",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getImage()
    {
        Intent intentCh = new Intent();
        intentCh.setType("image/*");
        intentCh.setAction(Intent.ACTION_GET_CONTENT);
    }


}