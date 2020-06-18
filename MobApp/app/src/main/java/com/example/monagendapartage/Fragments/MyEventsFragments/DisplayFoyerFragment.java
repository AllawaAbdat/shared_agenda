package com.example.monagendapartage.Fragments.MyEventsFragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.monagendapartage.Adapters.EventsAdapter.DisplayFoyerAdapter;
import com.example.monagendapartage.Models.CommonModels.UserModel;
import com.example.monagendapartage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class DisplayFoyerFragment extends Fragment {
    /** Déclaration variables */
    private Button filterButton;
    private NestedScrollView scrollView;
    private FrameLayout frameLayoutMap;
    private RecyclerView gridViewUsers;
    private DisplayFoyerAdapter displayFoyerAdapter;
    private List<UserModel> mUsers;
    private List<String> mUsersId;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mProgressView;
    private LinearLayout startRelativeLayout;
    private SwipeRefreshLayout noResultsLayout;
    private String userIdToList;

    /** Constructeur de la classe DisplayUsersFragment */
    public DisplayFoyerFragment() {
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

        View view = inflater.inflate(R.layout.fragment_display_foyer, container, false);

        /** Initialisation des variables */
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

        userIdToList = "";

        mUsers = new ArrayList<>();
        mUsersId = new ArrayList<>();

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

    private void _readUsers() {
        mUsers.clear();
        mUsersId.clear();
        _showProgress(true);
        _getFromTable();
    }


    /**
     * Fonction permettant d'afficher les Utilisateurs
     */
    private void _placeUsersOnList() {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            if (mUsers.size() > 0) {
                _showProgress(false);
                mSwipeRefreshLayout.setRefreshing(false);
                noResultsLayout.setRefreshing(false);
                noResultsLayout.setVisibility(View.GONE);
            } else {
                mProgressView.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                noResultsLayout.setVisibility(View.VISIBLE);
                noResultsLayout.setRefreshing(false);
            }
        }
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

    private void _getFromTable() {
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
                        _setUsersOnListAndAdapter();
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void _setUsersOnListAndAdapter() {
        gridViewUsers.setLayoutManager(new GridLayoutManager(getContext(), 2));
        displayFoyerAdapter =  new DisplayFoyerAdapter(getContext(), mUsers);
        gridViewUsers.setAdapter(displayFoyerAdapter);
        /** On place tout les Utilisateurs sur une liste */
        _placeUsersOnList();
    }

}
