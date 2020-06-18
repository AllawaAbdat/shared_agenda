package com.example.monagendapartage.Activities.EventsActivities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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

public class DisplayUserFoyerAgendaActivity extends AppCompatActivity {

    /** Déclaration variables */
    private Button addEventButton;
    private NestedScrollView scrollView;
    private RecyclerView gridEvents;
    private MyEventsAdapter myEventsAdapter;
    private List<MyEventsModel> mEvents;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mProgressView;
    private LinearLayout startRelativeLayout;
    private SwipeRefreshLayout noResultsLayout;
    private String idUserBundle;

    /** Constructeur de la classe DisplayUserFoyerAgendaActivity */
    public DisplayUserFoyerAgendaActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_foyer_agenda);

        idUserBundle = (String) getIntent().getSerializableExtra("idUser");

        /*overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/


        /** Initialisation des variables */
        addEventButton = findViewById(R.id.addEventButton);
        scrollView = findViewById(R.id.scrollView);
        noResultsLayout = findViewById(R.id.noResultsLayout);

        gridEvents = findViewById(R.id.listView);
        gridEvents.setHasFixedSize(true);
        gridEvents.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        gridEvents.setNestedScrollingEnabled(false);

        startRelativeLayout = findViewById(R.id.startRelativeLayout);
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh_items);
        mProgressView = findViewById(R.id.progressBar);

        mEvents = new ArrayList<>();


        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisplayUserFoyerAgendaActivity.this, AddEventActivity.class);
                intent.putExtra("idUser", idUserBundle);
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

        /* if (bundleArg == null) {
            _readUsers();
        } else {
            ArrayList<UserModel> myNewList = new ArrayList<>();
            myNewList.addAll(bundleArg.getParcelableArrayList("listUsers"));
            applyFilter(myNewList);
        }*/

        _readEvents();
    }

    /**
     * Fonction permettant de récuperer tout les Evenements de l'utilisateur
     */
    private void _readEvents() {
        _showProgress(true);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        /** Reference sur la table Events */
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Events").child(idUserBundle);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mEvents.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MyEventsModel eventModel = snapshot.getValue(MyEventsModel.class);
                    assert eventModel != null;
                    assert firebaseUser != null;
                    mEvents.add(eventModel);
                }

                myEventsAdapter =  new MyEventsAdapter(getApplicationContext(), mEvents);
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
        Activity activity = DisplayUserFoyerAgendaActivity.this;
        if (activity != null) {
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
