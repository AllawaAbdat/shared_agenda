package com.example.monagendapartage.Activities.RegistrationActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.monagendapartage.Activities.MainActivitySharedAgenda.MainActivity;
import com.example.monagendapartage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /** Déclaration de variables */
    private EditText passwordTxt, passwordTxtRetype, email, username, firstname,
            lastname, dateDeNaissance, nbVoie, nomVoie, codePostal, townName, telephone;

    private Button btn_register;
    private ProgressBar spinner;
    private Spinner adressTypeSpinnerView;
    private ScrollView scroll_register_view;
    private LinearLayout Loader_Layout;

    private FirebaseAuth auth;
    private DatabaseReference reference;

    private String date;
    private int mYear;
    private int mMonth;
    private int mDay;

    RelativeLayout RegisterActivity_Layout;
    /** Lottie => Librairie offrant des animations en json */
    LottieAnimationView animation_view, animation_view_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        /** overridePendingTransition : fonction utilisée pour ajouter une animation de transition */
        // overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        /** Configuration de la toolbar présente au sein du fichier xml de cette activitée */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /** On récupère une instance Firebase */
        auth = FirebaseAuth.getInstance();

        /** Biding entre les éléments du fichier xml et le fichier java */
        passwordTxt = findViewById(R.id.passwordTxt);
        btn_register = findViewById(R.id.btn_register);
        passwordTxtRetype = findViewById(R.id.passwordTxtRetype);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        lastname = findViewById(R.id.lastname);
        firstname = findViewById(R.id.firstname);
        dateDeNaissance = findViewById(R.id.birthDate);
        adressTypeSpinnerView = findViewById(R.id.adressTypeSpinner);
        nbVoie = findViewById(R.id.numeroVoieET);
        nomVoie = findViewById(R.id.adressNameET);
        codePostal = findViewById(R.id.codePostal);
        townName = findViewById(R.id.townName);
        telephone = findViewById(R.id.numeroTelephoneET);
        spinner = findViewById(R.id.progressBar1);
        animation_view = findViewById(R.id.animation_view);
        animation_view_2 = findViewById(R.id.animation_view_2);
        scroll_register_view = findViewById(R.id.scroll_register_view);
        RegisterActivity_Layout = findViewById(R.id.RegisterActivity_Layout);
        Loader_Layout = findViewById(R.id.loader_layout);


        /** On change le bouton d'inscription de couleur et on désactive le clique dessus */
        // btn_register.setClickable(false);
        // btn_register.setBackgroundColor(Color.parseColor("#808080"));

        /** On change le bouton d'inscription de couleur et on active le clique dessus */
        // btn_register.setClickable(true);
        // btn_register.setBackgroundColor(Color.parseColor("#226E6E"));


        /** Logique afin d'afficher une modale Calendrier permettant à l'utilisateur de séléctionner sa date de naissance */
        dateDeNaissance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                Locale.setDefault(Locale.FRANCE);

                mDay   = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mYear  = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        date = dayOfMonth + ":" + month + ":" + year;
                        dateDeNaissance.setText(date);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        /** Spinner (ou liste à choix multiple) permettant à l'utilisateur de choisir sa rue
         * On ajoute une liste à cette liste provenant du fichier strings.xml
         **/
        Spinner adressTypeSpinner = findViewById(R.id.adressTypeSpinner);
        ArrayAdapter<CharSequence> adresseTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.adressTypeSpinner, android.R.layout.simple_spinner_item);
        adresseTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adressTypeSpinner.setAdapter(adresseTypeAdapter);
        adressTypeSpinner.setOnItemSelectedListener(this);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Pattern upperCase = Pattern.compile("[A-Z]");
                final Pattern lowerCase = Pattern.compile("[a-z]");
                final Pattern digitCase = Pattern.compile("[0-9]");
                final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                final String txt_username = username.getText().toString();
                final String txt_email = email.getText().toString();
                final String txt_lastname = lastname.getText().toString();
                final String txt_firstname = firstname.getText().toString();
                final String txt_birthdate = dateDeNaissance.getText().toString();
                final String txt_nbVoie = nbVoie.getText().toString();
                final String txt_typeVoie = adressTypeSpinnerView.getSelectedItem().toString();
                final String txt_nomVoie = nomVoie.getText().toString();
                final String txt_codePostal = codePostal.getText().toString();
                final String txt_townName = townName.getText().toString();
                final String txt_telephone = telephone.getText().toString();

                final String txt_password = passwordTxt.getText().toString();
                final String txt_passwordRetype = passwordTxtRetype.getText().toString();

                // On vérifie qu'il n'y a pas de duplication du login
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                Query query = reference.orderByChild("username".toLowerCase()).equalTo(txt_username.toLowerCase());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Un utilisateur a déjà un username identique
                            Toast.makeText(RegisterActivity.this, getString(R.string.ACTIVITY_REGISTER_DUPLICATE_USERNAME), Toast.LENGTH_SHORT).show();
                            // On vide le champs username du formulaire
                            username.setText("");
                        } else {
                            // On peut vérifier les champs saisis
                            if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty((txt_password)) ||
                                    TextUtils.isEmpty((txt_passwordRetype)) ||TextUtils.isEmpty((txt_lastname)) || TextUtils.isEmpty((txt_firstname)) || TextUtils.isEmpty((txt_birthdate)) ||
                                    TextUtils.isEmpty((txt_nbVoie)) || TextUtils.isEmpty((txt_typeVoie)) || TextUtils.isEmpty((txt_nomVoie)) ||
                                    TextUtils.isEmpty((txt_codePostal)) || TextUtils.isEmpty((txt_townName)) || TextUtils.isEmpty((txt_telephone))) {
                                Toast.makeText(RegisterActivity.this, getString(R.string.ACTIVITY_REGISTER_ALL_FIELDS_REQUIRED_FR) , Toast.LENGTH_SHORT).show();
                            } else if (txt_password.length() < 6) {
                                Toast.makeText(RegisterActivity.this, getString(R.string.ACTIVITY_REGISTER_LENGTH_PASSWORD_TOO_SHORT_FR), Toast.LENGTH_SHORT).show();
                            } else if (!lowerCase.matcher(txt_password).find() || !digitCase.matcher(txt_password).find() || !upperCase.matcher(txt_password).find() ) {
                                Toast.makeText(RegisterActivity.this, getString(R.string.ACTIVITY_REGISTER_LOWER_UPPER_DIGIT_FR), Toast.LENGTH_SHORT).show();
                            } else if (!txt_password.equals(txt_passwordRetype)) {
                                Toast.makeText(RegisterActivity.this, getString(R.string.ACTIVITY_REGISTER_PASSWORD_IS_DIFFERENT_FR), Toast.LENGTH_SHORT).show();
                            } else if (!txt_email.trim().matches(emailPattern)) {
                                Toast.makeText(RegisterActivity.this, getString(R.string.ACTIVITY_REGISTER_EMAIL_FORMAT_ERROR_FR), Toast.LENGTH_SHORT).show();
                            } else if (txt_password.equals(txt_passwordRetype)) {
                                _registrationProcess(txt_username, txt_email, txt_password, txt_lastname, txt_firstname, txt_birthdate, txt_nbVoie, txt_typeVoie, txt_nomVoie, txt_codePostal, txt_townName, txt_telephone);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    /**
     * 12 Paramètres
     * @param username
     * @param email
     * @param password
     * @param txt_lastname
     * @param txt_firstname
     * @param txt_birthdate
     * @param txt_nbVoie
     * @param txt_typeVoie
     * @param txt_nomVoie
     * @param txt_codePostal
     * @param ville
     * @param telephone
     * Fonction utilisée afin d'entamer une procédure d'inscription avec Firebase (createUserWithEmailAndPassword)
     */
    private void _registrationProcess (final String username, final String email, String password, final String txt_lastname, final String txt_firstname,
                                       final String txt_birthdate, final String txt_nbVoie, final String txt_typeVoie, final String txt_nomVoie, final String txt_codePostal,
                                       final String ville, final String telephone) {

        Loader_Layout.setVisibility(View.VISIBLE);
        scroll_register_view.setVisibility(View.GONE);
        Loader_Layout.animate().alpha(1.0f).setDuration(1000);
        scroll_register_view.animate().alpha(0.0f).setDuration(250);

        /** Permet de fermer le clavier du téléphone */
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("nom", txt_lastname);
                            hashMap.put("prenom", txt_firstname);
                            hashMap.put("username", username);
                            hashMap.put("email", email);
                            hashMap.put("dateDeNaissance", txt_birthdate);
                            hashMap.put("numeroVoie", txt_nbVoie);
                            hashMap.put("typeVoie", txt_typeVoie);
                            hashMap.put("nomVoie", txt_nomVoie);
                            hashMap.put("codePostal", txt_codePostal);
                            hashMap.put("ville", ville);
                            hashMap.put("telephone", telephone);
                            hashMap.put("availableDates", "");
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline");
                            hashMap.put("search", username.toLowerCase());

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        spinner.setVisibility(View.GONE);
                                        Toast.makeText(RegisterActivity.this, getString(R.string.ACTIVITY_REGISTER_SIGN_UP_SUCCESS_FR), Toast.LENGTH_SHORT ).show();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, getString(R.string.ACTIVITY_REGISTER_CANNOT_SIGN_UP), Toast.LENGTH_SHORT).show();
                            scroll_register_view.setVisibility(View.VISIBLE);
                            Loader_Layout.setVisibility(View.GONE);
                            Loader_Layout.animate().alpha(0.0f).setDuration(250);
                            scroll_register_view.animate().alpha(1.0f).setDuration(1000);
                        }
                    }
                });
    }

    /** Déclaration de méthodes abstraites nécessaire au bon fonctionnement des combobox */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
