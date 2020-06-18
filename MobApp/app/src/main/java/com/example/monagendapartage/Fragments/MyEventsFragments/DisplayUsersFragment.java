package com.example.monagendapartage.Fragments.MyEventsFragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.monagendapartage.Activities.EventsActivities.UserProfilViewActivity;
import com.example.monagendapartage.Adapters.EventsAdapter.DisplayUsersAdapter;
import com.example.monagendapartage.Models.CommonModels.UserModel;
import com.example.monagendapartage.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DisplayUsersFragment extends Fragment {
    /** Déclaration variables */
    private Button aroundMe, filterButton;
    private NestedScrollView scrollView;
    private FrameLayout frameLayoutMap;
    private RecyclerView gridViewUsers;
    private DisplayUsersAdapter displayUsersAdapter;
    private List<UserModel> mUsers;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mProgressView;
    private LinearLayout startRelativeLayout;
    private SwipeRefreshLayout noResultsLayout;

    /** Constructeur de la classe DisplayUsersFragment */
    public DisplayUsersFragment() {
        // Required empty public constructor
    }

    /**
     * onCreateView | utilisée à la place de onCreate. Pourquoi ? Nous sommes sur un fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_display_users, container, false);

        /** Initialisation des variables */
        aroundMe = view.findViewById(R.id.aroundMe);
        filterButton = view.findViewById(R.id.filterButton);
        scrollView = view.findViewById(R.id.scrollView);
        frameLayoutMap = view.findViewById(R.id.frameLayoutMap);
        noResultsLayout = view.findViewById(R.id.noResultsLayout);

        gridViewUsers = view.findViewById(R.id.listView);
        gridViewUsers.setHasFixedSize(true);
        gridViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        gridViewUsers.setNestedScrollingEnabled(false);

        startRelativeLayout = view.findViewById(R.id.startRelativeLayout);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_items);
        mProgressView = view.findViewById(R.id.progressBar);

        mUsers = new ArrayList<>();

        /** Gestion du contrôle sur le bouton 'Liste'/'Carte' */
        aroundMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aroundMe.getText().toString().equals("Carte")) {
                    aroundMe.setText("Liste");
                    if (mUsers.size() > 0) {
                        noResultsLayout.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setVisibility(View.GONE);
                        frameLayoutMap.setVisibility(View.VISIBLE);
                    } else {
                        noResultsLayout.setVisibility(View.VISIBLE);
                        mSwipeRefreshLayout.setVisibility(View.GONE);
                        frameLayoutMap.setVisibility(View.GONE);
                    }
                } else if (aroundMe.getText().toString().equals("Liste")) {
                    aroundMe.setText("Carte");
                    if (mUsers.size() > 0) {
                        noResultsLayout.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        frameLayoutMap.setVisibility(View.GONE);
                    } else {
                        noResultsLayout.setVisibility(View.VISIBLE);
                        mSwipeRefreshLayout.setVisibility(View.GONE);
                        frameLayoutMap.setVisibility(View.GONE);
                    }

                }
            }
        });

        /** Contrôle sur le bouton 'Filtre' */
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Cette fonctionnalitée sera bientôt disponible ! Nos développeurs se concentre dessus", Toast.LENGTH_SHORT).show();

            }
        });

        noResultsLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _readUsers();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _readUsers();
            }
        });

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (scrollView.getScrollY() > 0) {
                    scrollView.smoothScrollTo(0, 0);
                }
            }
        };


        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        _readUsers();

        return view;
    }

    /**
     * Fonction permettant de récuperer tout les Utilisateurs
     * Lecture sur la table 'Users'
     */
    private void _readUsers() {
        _showProgress(true);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        /** Reference sur la table Users */
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    assert userModel != null;
                    assert firebaseUser != null;
                    if (!userModel.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(userModel);

                    }
                }

                gridViewUsers.setLayoutManager(new GridLayoutManager(getContext(), 2));
                displayUsersAdapter =  new DisplayUsersAdapter(getContext(), mUsers);
                gridViewUsers.setAdapter(displayUsersAdapter);
                /** On place tout les utilisateurs sur une map */
                _placeMarkersOnMap();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /**
     * Fonction permettant d'affiche les utilisateurs sur une carte Google Map
     */
    private void _placeMarkersOnMap() {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            if (mUsers.size() > 0) {
                SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                        mMap.clear(); //clear old markers

                        CameraPosition googlePlex = CameraPosition.builder()
                                .target(new LatLng(47.155404566149336,2.6491201250000813))
                                .zoom(2)
                                .bearing(0)
                                .tilt(45)
                                .build();

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);

                        for(UserModel usr: mUsers) {
                            String adress = usr.getNumeroVoie() + " " + usr.getTypeVoie() + " " + usr.getNomVoie() + ","  + usr.getCodePostal();
                            LatLng adressLatLng = getLocationFromAddress(getActivity(), adress);
                            if (adressLatLng != null) {
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(adressLatLng.latitude, adressLatLng.longitude))
                                        .title(usr.getId())
                                        .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_person_black_24dp)));
                            }
                        }

                        _showProgress(false);
                        mSwipeRefreshLayout.setRefreshing(false);
                        noResultsLayout.setRefreshing(false);
                        noResultsLayout.setVisibility(View.GONE);
                        aroundMe.setText("Carte");

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                Intent intent = new Intent(getActivity(), UserProfilViewActivity.class);
                                intent.putExtra("idUser", marker.getTitle());
                                startActivity(intent);
                                return true;
                            }
                        });
                    }
                });
            } else {
                Log.d("noResult", "ok ! ::::::");
                mProgressView.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                // startRelativeLayout.setVisibility(View.VISIBLE);
                noResultsLayout.setVisibility(View.VISIBLE);
                noResultsLayout.setRefreshing(false);
            }
        }
    }

    /**
     * Fonction permettant de récuperer la latitude et la longitude d'une adresse postale
     * @param context
     * @param strAddress
     * @return
     */
    public LatLng getLocationFromAddress(Context context,String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    /**
     * Fonction permettant de déssiner l'icône qui sera affichée sur Google Maps
     * @param context
     * @param vectorResId
     * @return
     */
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Fait apparaître le Spinner/Loader et cache différentes interfaces
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void _showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSwipeRefreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            mSwipeRefreshLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSwipeRefreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mSwipeRefreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
