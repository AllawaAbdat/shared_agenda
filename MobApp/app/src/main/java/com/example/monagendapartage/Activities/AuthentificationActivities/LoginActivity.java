package com.example.monagendapartage.Activities.AuthentificationActivities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monagendapartage.Activities.MainActivitySharedAgenda.MainActivity;
import com.example.monagendapartage.R;
import com.example.monagendapartage.Activities.RegistrationActivities.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    /** Déclaration de variables */
    private String userId;
    private EditText email, password;
    private Button btn_login;
    private ImageButton btRegister;
    private ImageView tvLogin;
    private CardView cv;
    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth auth;
    private TextView forgot_password;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /** Transition */
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        /** Initialisation des variables */
        btRegister  = findViewById(R.id.btRegister);
        btRegister.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        tvLogin = findViewById(R.id.tvLogin);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        forgot_password = findViewById(R.id.forgot_password);
        cv = findViewById(R.id.cv);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mProgressView = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.sv);

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    public void attemptLogin() {
        /** Décalaration de pattern. Contient une liste des lettres minuscules et majuscules ainsi que les chiffre d'un pavé numérique */
        /** Utilisée pour vérifier le mot de passe */
        final Pattern lowerCase = Pattern.compile("[a-z]");
        final Pattern digitCase = Pattern.compile("[0-9]");
        final Pattern upperCase = Pattern.compile("[A-Z]");
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        /** On stock l'eamil et le mot de passe de l'utilisateur à chaque tentative de connexion */
        final String emailAttemptLogin = email.getText().toString();
        final String passwordAttemptLogin = password.getText().toString();

        if (TextUtils.isEmpty(emailAttemptLogin) || TextUtils.isEmpty(passwordAttemptLogin)) {
            Toast.makeText(LoginActivity.this, "Tout les champs sont requis !", Toast.LENGTH_SHORT).show();
        } else {
            /** Permet de fermer le clavier du téléphone */
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            /** showProgress, fonction permettant d'afficher ou non le Spinner (loader) */
            showProgress(true);

            /** Connexion + Verification sur les informations saisies (Champs requis, véracité du mot de passe etc etc ... )*/
            auth.signInWithEmailAndPassword(emailAttemptLogin, passwordAttemptLogin)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                showProgress(false);
                                if (passwordAttemptLogin.length() < 6 || !lowerCase.matcher(passwordAttemptLogin).find() || !digitCase.matcher(passwordAttemptLogin).find() || !upperCase.matcher(passwordAttemptLogin).find()) {
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.ACTIVITY_LOGIN_PASSWORD_LENTH_FR), Toast.LENGTH_SHORT).show();
                                } else if (!lowerCase.matcher(passwordAttemptLogin).find() || !digitCase.matcher(passwordAttemptLogin).find() || !upperCase.matcher(passwordAttemptLogin).find() ) {
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.ACTIVITY_LOGIN_PASSWORD_PATTERN_FR), Toast.LENGTH_SHORT).show();
                                } else if (!emailAttemptLogin.trim().matches(emailPattern)) {
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.ACTIVITY_LOGIN_EMAIL_PATTERN_FR), Toast.LENGTH_SHORT).show();
                                }  else {
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.ACTIVITY_LOGIN_WRONG_CREDENTIALS_FR), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                _login();
                            }
                        }
                    });
        }
    }

    private void _login() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }


    /**
     * Cette annotation (@RequiresApi) indique que l'élément (la fonction onClick) ne doit être appelé qu'au niveau de l'API donné (Version LOLLIPOP) ou au dessus
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        if (v==btRegister){
            Intent intent   = new Intent(LoginActivity.this, RegisterActivity.class);
            Pair[] pairs    = new Pair[1];
            pairs[0] = new Pair<View,String>(tvLogin,"tvLogin");
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,pairs);
            startActivity(intent,activityOptions.toBundle());
        }
    }

    /**
     * Fait apparaître le Spinner/Loader et cache l'interface de saisie des identifiants
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            cv.setVisibility(show ? View.GONE : View.VISIBLE);
            cv.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    cv.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });


            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            cv.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
