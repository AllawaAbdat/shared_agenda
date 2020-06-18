package com.example.monagendapartage.Activities.EventsActivities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.monagendapartage.Activities.MainActivitySharedAgenda.MainActivity;
import com.example.monagendapartage.Models.CommonModels.UserModel;
import com.example.monagendapartage.Models.MyEventsModels.MyEventsModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.monagendapartage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    /** Déclaration de variables */
    private EditText startDate, endDate;
    private EditText startHour, endHour;
    private EditText eventDescription;
    private Button btnAddEvent, btnAddUsers;
    private boolean[] booleanTab;
    private List<UserModel> mUsers;
    private List<String> mUsersId;
    private HashMap<String, String> hashMap;
    private ArrayList selectedItems;
    private String date;
    private String idUserBundle;
    private String startDateString, endDateString, startHourString, endHourString;
    private int mYear;
    private int mMonth;
    private int mDay;
    private AlertDialog myAlertDialog;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        /** Animation Transition */
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        Toolbar toolbar = findViewById(R.id.toolbar);

        /** Mise en place de la toolbar */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        /** Initialisation des variables */
        selectedItems = new ArrayList();
        mUsers = new ArrayList();
        mUsersId = new ArrayList();
        hashMap = new HashMap<>();

        idUserBundle = (String) getIntent().getSerializableExtra("idUser");

        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        startHour = findViewById(R.id.startHour);
        endHour = findViewById(R.id.endHour);
        eventDescription = findViewById(R.id.eventDescription);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        btnAddUsers = findViewById(R.id.btnAddUsers);

        if (idUserBundle != null) {
            btnAddUsers.setVisibility(View.GONE);
        }

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                Locale.setDefault(Locale.FRANCE);

                mDay   = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mYear  = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        date = dayOfMonth + "/" + month + "/" + year;
                        startDateString = date;
                        startDate.setText(date);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                Locale.setDefault(Locale.FRANCE);

                mDay   = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mYear  = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        date = dayOfMonth + "/" + month + "/" + year;
                        endDateString = date;
                        endDate.setText(date);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        startHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                Locale.setDefault(Locale.FRANCE);

                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startHourString = selectedHour + ":" + selectedMinute;
                        startHour.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        endHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                Locale.setDefault(Locale.FRANCE);

                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endHourString = selectedHour + ":" + selectedMinute;
                        endHour.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        btnAddUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // _launchDialog();
                _getFromTable();
            }
        });

        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startDateString.equals("") || endDateString.equals("") || startHourString.equals("") || endHourString.equals("") || eventDescription.equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Veuillez remplir tout les champs", Toast.LENGTH_SHORT);
                } else {
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myEventsDb;
                    DatabaseReference myEventsCurrentUser;
                    DatabaseReference myEventsDestinataire;
                    String mGroupId;

                    if (idUserBundle == null) {
                        myEventsDb = database.getReference("Events").child(currentUser.getUid());
                        myEventsDestinataire = database.getReference("Events");
                        mGroupId = myEventsDb.push().getKey();

                        if (hashMap.size() > 0 && hashMap != null) {
                            String id = "";
                            hashMap.put("500", currentUser.getUid());
                            for (int i = 0; i < hashMap.size(); i++) {
                                if (i == (hashMap.size() - 1)) {
                                    id = hashMap.get("500");
                                } else {
                                    id = hashMap.get(Integer.toString(i));
                                }
                                myEventsDestinataire.child(id).child(mGroupId).setValue(new MyEventsModel(mGroupId, startDateString, endDateString, startHourString, endHourString, eventDescription.getText().toString()));
                                myEventsDestinataire.child(id).child(mGroupId).child("sharedWith").setValue(hashMap);

                            }
                            // myEventsDb.child(mGroupId).child("sharedWith").setValue(hashMap);
                        } else {
                            myEventsDb.child(mGroupId).setValue(new MyEventsModel(mGroupId, startDateString, endDateString, startHourString, endHourString, eventDescription.getText().toString()));
                        }

                    } else {
                        final HashMap<String, String> hashMapFinalDb = new HashMap<>();
                        final HashMap<String, String> hashMapFinalUser = new HashMap<>();

                        hashMapFinalDb.put("0", idUserBundle);
                        hashMapFinalUser.put("0", currentUser.getUid());

                        myEventsDb = database.getReference("Events").child(idUserBundle);
                        myEventsCurrentUser = database.getReference("Events").child(currentUser.getUid());

                        mGroupId = myEventsDb.push().getKey();

                        myEventsDb.child(mGroupId).setValue(new MyEventsModel(mGroupId, startDateString, endDateString, startHourString, endHourString, eventDescription.getText().toString()));
                        myEventsCurrentUser.child(mGroupId).setValue(new MyEventsModel(mGroupId, startDateString, endDateString, startHourString, endHourString, eventDescription.getText().toString()));

                        myEventsDb.child(mGroupId).child("sharedWith").setValue(hashMapFinalUser);
                        myEventsCurrentUser.child(mGroupId).child("sharedWith").setValue(hashMapFinalDb);

                    }

                    Intent intent = new Intent(AddEventActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast toast = Toast.makeText(getApplicationContext(), "Evenement ajouté !", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });
    }

    /**
     *
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Permet de récuperer l'ID des utilisateurs (String) 'ami' avec l'utilisateur
     * A partir du statut des DEMANDES ENVOYÉES PAR L'UTILISATEUR COURANT
     * Utilisé pour l'ouverture du Dialog permettant de sélectionner les utilisateurs avec qui partager l'évenement
     */
    private void _getFromTable() {
        mUsersId.clear();
        mUsers.clear();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference referenceFrom = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("requests").child("from");

        referenceFrom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String request = snapshot.child("requestType").getValue(String.class);
                    if (request.equals("accepted")) {
                        final String userId = snapshot.getKey();
                        mUsersId.add(userId);
                    }
                }
                _getToTable();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    /**
     * Permet de récuperer l'ID des utilisateurs (String) 'ami' avec l'utilisateur
     * A partir du statut des DEMANDES ENVOYÉES VERS L'UTILISATEUR COURANT
     * Utilisé pour l'ouverture du Dialog permettant de sélectionner les utilisateurs avec qui partager l'évenement
     */
    private void _getToTable() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference referenceTo = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("requests").child("to");

        referenceTo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String request = snapshot.child("requestType").getValue(String.class);
                    if (request.equals("accepted")) {
                        final String userId = snapshot.getKey();
                        mUsersId.add(userId);
                    }
                }
                _getFinalUsers();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Permet de récuperer les utilisateurs (UserModel) 'ami' avec l'utilisateur
     * A partir du statut des DEMANDES ENVOYÉES VERS L'UTILISATEUR COURANT
     * Utilisé pour l'ouverture du Dialog permettant de sélectionner les utilisateurs avec qui partager l'évenement
     */
    private void _getFinalUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        /** Reference sur la table Users */
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    assert userModel != null;
                    assert firebaseUser != null;
                    for (String usersId: mUsersId) {
                        if (userModel.getId().equals(usersId)) {
                            mUsers.add(userModel);
                        }
                    }
                    if (mUsers.size() == mUsersId.size()) {
                        _launchDialog();
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void _launchDialog() {
        hashMap.clear();
        AlertDialog.Builder addUsersBuilder = new AlertDialog.Builder(AddEventActivity.this);

        // On se base sur la liste des utilisateurs "amis"
        final CharSequence[] charSequencesId = new CharSequence[mUsers.size()];
        booleanTab = new boolean[mUsers.size()];

        // On initialise la valeur des utilisateur à ajouter (username)
        for (int i=0; i < mUsers.size(); i++) {
            charSequencesId[i] = mUsers.get(i).getUsername() ;
        }

        addUsersBuilder.setTitle("Ajouter un utilisateur").setMultiChoiceItems(charSequencesId, booleanTab, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                // Gestion des click des checkbox
                if (isChecked) {
                    selectedItems.add(charSequencesId[i]);
                    // booleanTab[i] = true;
                } else if (selectedItems.contains(i)) {
                    selectedItems.remove(Integer.valueOf(i));
                }
            }
        });
        addUsersBuilder.setPositiveButton("Ajouter à l'événement", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // On parcourt la liste des logins selectionnés (checkbox)
                for (int j=0; j < selectedItems.size(); j++) {
                    // On parcourt la liste des utilisteurs "amis"
                    for (int k=0; k< mUsers.size(); k++) {
                        // Sachant qu'il ne peut pas y avoir 2 logins identiques
                        // Si le login selectionné correspond au login de l'utilisateur "ami"
                        // on ajoute son id dans hashMap
                        if (selectedItems.get(j).toString().equals(mUsers.get(k).getUsername())) {
                            hashMap.put(Integer.toString(j), mUsers.get(k).getId());
                        }
                    }
                }
            }
        });

        myAlertDialog = addUsersBuilder.create();
        myAlertDialog.show();
    }

}
