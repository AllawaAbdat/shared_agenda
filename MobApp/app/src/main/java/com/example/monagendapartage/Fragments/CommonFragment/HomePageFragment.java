package com.example.monagendapartage.Fragments.CommonFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.monagendapartage.Activities.MainActivitySharedAgenda.MainActivity;
import com.example.monagendapartage.Fragments.MyEventsFragments.DisplayEventsFragment;
import com.example.monagendapartage.Fragments.MyEventsFragments.EventsMessagerieFragment;
import com.example.monagendapartage.Models.CommonModels.UserModel;
import com.example.monagendapartage.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomePageFragment extends Fragment {

    /** Déclaration de variables */
    private FirebaseUser fuser;
    private CardView handleCalendar,
            profilCardId, messagerieCardViewId, aboutCardViewId, parrainageCardViewId;
    private BottomNavigationView mBottomNav;
    private UserModel currentUserModel;
    CircleImageView image_profile;
    TextView username;
    DatabaseReference reference;
    StorageReference storageReference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        /** Initialisation des variables */
        image_profile = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        handleCalendar = view.findViewById(R.id.handleCalendar);
        profilCardId = view.findViewById(R.id.profilCardId);
        messagerieCardViewId = view.findViewById(R.id.messagerieCardViewId);
        aboutCardViewId = view.findViewById(R.id.aboutCardViewId);
        parrainageCardViewId = view.findViewById(R.id.parrainageCardViewId);


        handleCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomNav = ((MainActivity)getActivity()).findViewById(R.id.bottomNavigation);
                MenuItem homeItem = mBottomNav.getMenu().getItem(1);
                mBottomNav.setSelectedItemId(homeItem.getItemId());
                DisplayEventsFragment eventsFragment = new DisplayEventsFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, eventsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        profilCardId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomNav = ((MainActivity)getActivity()).findViewById(R.id.bottomNavigation);
                MenuItem homeItem = mBottomNav.getMenu().getItem(4);
                mBottomNav.setSelectedItemId(homeItem.getItemId());
                ProfilFragment profilFragment = new ProfilFragment(currentUserModel);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, profilFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        messagerieCardViewId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomNav = ((MainActivity)getActivity()).findViewById(R.id.bottomNavigation);
                MenuItem homeItem = mBottomNav.getMenu().getItem(3);
                mBottomNav.setSelectedItemId(homeItem.getItemId());
                EventsMessagerieFragment messagerieFragment = new EventsMessagerieFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, messagerieFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                // Toast.makeText(getContext(), "Cette section est en cours de développement. Nous l'afficherons très bientôt !", Toast.LENGTH_SHORT).show();
            }
        });
        aboutCardViewId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Cette section est en cours de développement. Nous l'afficherons très bientôt !", Toast.LENGTH_SHORT).show();
            }
        });
        parrainageCardViewId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Cette section est en cours de développement. Nous l'afficherons très bientôt !", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


}