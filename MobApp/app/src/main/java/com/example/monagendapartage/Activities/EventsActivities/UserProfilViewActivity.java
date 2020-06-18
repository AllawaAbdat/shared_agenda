package com.example.monagendapartage.Activities.EventsActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.monagendapartage.Activities.MessageActivities.MessageActivity;
import com.example.monagendapartage.Models.CommonModels.UserModel;
import com.example.monagendapartage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserProfilViewActivity extends AppCompatActivity {

    /** Déclaration de variables */
    private DatabaseReference reference;
    private String requestType;
    private String idUser;
    private ImageView image_profile;
    private TextView username, pseudo;
    private Button closeButton, sendRequestButton, disponibilityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profil_view);
        /** Transition Animation */
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        /** Récupere l'id de l'utilisateur à afficher */
        idUser = (String) getIntent().getSerializableExtra("idUser");

        /** References firebase */
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference referenceTo = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("requests").child("to").child(idUser);
        final DatabaseReference referenceFrom = FirebaseDatabase.getInstance().getReference("Users").child(idUser).child("requests").child("from").child(firebaseUser.getUid());

        /** Initialisation des variables */
        image_profile = findViewById(R.id.imageViewUser);
        username = findViewById(R.id.usernameTextView);
        pseudo = findViewById(R.id.pseudoTextView);
        closeButton = findViewById(R.id.closeButton);
        sendRequestButton = findViewById(R.id.sendRequest);
        disponibilityButton = findViewById(R.id.disponibility);
        reference = FirebaseDatabase.getInstance().getReference("Users").child(idUser);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                username.setText(userModel.getPrenom() + " " + userModel.getNom());
                pseudo.setText("(" + userModel.getUsername() + ")");
                if (userModel.getImageURL().equals("default")) {
                    image_profile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(userModel.getImageURL()).into(image_profile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        referenceTo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    requestType = dataSnapshot.child("requestType").getValue(String.class);
                    if (requestType != null) {
                        if (requestType.equals("send")) {
                            sendRequestButton.setText("Demande envoyée !");
                            sendRequestButton.setEnabled(false);
                        } else if (requestType.equals("accepted")) {
                            sendRequestButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                                    intent.putExtra("userid", idUser);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                } else {
                    sendRequestButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("noValue", "noValue");
                            HashMap<String, String> hashMapTo = new HashMap<>();
                            hashMapTo.put("requestType", "send");
                            HashMap<String, String> hashMapFrom = new HashMap<>();
                            hashMapFrom.put("requestType", "received");

                            referenceTo.setValue(hashMapTo);
                            referenceFrom.setValue(hashMapFrom);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        disponibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UserProfilViewActivity.this, "Vous ne pouvez pas consulter agenda. Ajouter d'abord l'utilisateur à votre foyer", Toast.LENGTH_SHORT).show();

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

}
