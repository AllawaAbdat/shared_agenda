package com.example.monagendapartage.Fragments.MyEventsFragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.monagendapartage.Activities.EventsActivities.AddEventActivity;
import com.example.monagendapartage.Adapters.EventsAdapter.MyEventsAdapter;
import com.example.monagendapartage.Models.MyEventsModels.MyEventsModel;
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

public class EventsFragment extends Fragment {
    /** Déclaration variables */
    private Button filterButton, addEventButton;
    private NestedScrollView scrollView;
    private RecyclerView gridEvents;
    private MyEventsAdapter myEventsAdapter;
    private List<MyEventsModel> mEvents;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mProgressView;
    private LinearLayout startRelativeLayout;
    private SwipeRefreshLayout noResultsLayout;

    /** Constructeur de la classe EventsFragment */
    public EventsFragment() {
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

        View view = inflater.inflate(R.layout.fragment_events, container, false);

        /** Initialisation des variables */
        filterButton = view.findViewById(R.id.filterButton);
        addEventButton = view.findViewById(R.id.addEventButton);
        scrollView = view.findViewById(R.id.scrollView);
        noResultsLayout = view.findViewById(R.id.noResultsLayout);

        gridEvents = view.findViewById(R.id.listView);
        gridEvents.setHasFixedSize(true);
        gridEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        gridEvents.setNestedScrollingEnabled(false);

        startRelativeLayout = view.findViewById(R.id.startRelativeLayout);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_items);
        mProgressView = view.findViewById(R.id.progressBar);

        mEvents = new ArrayList<>();

        /** Contrôle sur le bouton 'Filtre' */
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Cette fonctionnalitée sera bientôt disponible ! Nos développeurs se concentre dessus", Toast.LENGTH_SHORT).show();

            }
        });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddEventActivity.class);
                startActivity(intent);
            }
        });

        noResultsLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _readEvents();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _readEvents();
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

        _readEvents();

        return view;
    }

    /**
     * Fonction permettant de récuperer tout les Evenements de l'utilisateur
     */
    private void _readEvents() {
        _showProgress(true);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        /** Reference sur la table Events */
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Events").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mEvents.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MyEventsModel eventModel = snapshot.getValue(MyEventsModel.class);
                    assert eventModel != null;
                    mEvents.add(eventModel);
                }

                myEventsAdapter =  new MyEventsAdapter(getContext(), mEvents);
                gridEvents.setAdapter(myEventsAdapter);
                /** On liste tout les evenements */
                _placeEventsOnList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /**
     * Fonction permettant d'afficher les evenements
     */
    private void _placeEventsOnList() {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            if (mEvents.size() > 0) {
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
}
