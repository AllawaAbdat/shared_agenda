package com.example.monagendapartage.Activities.MainActivitySharedAgenda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.monagendapartage.Activities.AuthentificationActivities.LoginActivity;
import com.example.monagendapartage.Fragments.CommonFragment.HomePageFragment;
import com.example.monagendapartage.Fragments.CommonFragment.NotificationsFragment;
import com.example.monagendapartage.Fragments.CommonFragment.ProfilUtilisateurFragment;
import com.example.monagendapartage.Fragments.MyEventsFragments.DisplayEventsFragment;
import com.example.monagendapartage.Fragments.MyEventsFragments.EventsMessagerieFragment;
import com.example.monagendapartage.Models.CommonModels.UserModel;
import com.example.monagendapartage.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    /** Déclaration de variables */
    private CircleImageView profile_image;
    private TextView username;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private BottomNavigationView navigation;
    private UserModel currenUserModel;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /** Fonction utilisée pour apporter une transition entre les activitées */
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        /** On set la toolbar */
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        navigation = findViewById(R.id.bottomNavigation);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currenUserModel = dataSnapshot.getValue(UserModel.class);
                username.setText(currenUserModel.getUsername());
                if (currenUserModel.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(currenUserModel.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.home:
                        /** HomePageFragment | Même fragment utilisée pour la page d'accueil*/
                        fragment = new HomePageFragment();
                        break;
                    case R.id.calendriers:
                        /** DisplayEventsFragment | Utilisée pour afficher les évènements et calendrier de l'utilisateur*/
                        fragment = new DisplayEventsFragment();
                        break;
                    case R.id.notifications:
                        /** Même et unique Fragment utilisée pour recevoir des notifications en tout genre*/
                        fragment = new NotificationsFragment();
                        break;
                    case R.id.contacts:
                        /** Messagerie / Contacts*/
                        fragment = new EventsMessagerieFragment();
                        break;
                    case R.id.profil:
                        /** Même et unique Fragment pour la gestion du profil de l'utilisateur */
                        fragment = new ProfilUtilisateurFragment(currenUserModel);
                        break;
                }

                final FragmentTransaction transaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, fragment).commit();
                return true;
            }
        });

        /** Start Activity with HomePageFragment */
        Fragment startFragment = new HomePageFragment();
        _loadFragment(startFragment);
    }

    /**
     * 1 paramètre
     * Fonction permettant de charger le Fragment en fonction du choix effectué par l'utilisateur sur la bar du menu
     * @param fragment
     * @return
     */
    private boolean _loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    /**
     * A tester | Afin de savoir si encore utilisée ou pas
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * 1 paramètre
     * Fonction permettant de rediriger l'utilisateur sur l'inferface souhaitait | Permet également la déconnexion
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.GENERIC_LOG_OUT_FR))
                        .setMessage(getString(R.string.GENERIC_YOU_WANT_TO_LOG_OUT_ARE_YOU_SURE_FR))
                        .setPositiveButton(getString(R.string.GENERIC_YES_FR), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }
                        })
                        .setNegativeButton(getString(R.string.GENERIC_NO_FR), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
        }
        return false;
    }
}
