package com.example.monagendapartage.Fragments.MyEventsFragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.monagendapartage.Adapters.EventsMessagerieAdapter.EventsRequestReceivedAdapter;
import com.example.monagendapartage.Models.CommonModels.UserModel;
import com.example.monagendapartage.Notifications.Token;
import com.example.monagendapartage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class EventsRequestReceivedFragment extends Fragment {

    /** Déclaration de variables */
    private RecyclerView gridViewEventsReceived;
    private EventsRequestReceivedAdapter requestReceivedAdapter;
    private List<UserModel> mUserModels;
    private FirebaseUser fuser;
    private DatabaseReference referenceRequests;
    private List<String> keysList;
    private NestedScrollView scrollView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /** Constructeur vide */
    public EventsRequestReceivedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_request_received, container, false);

        /** Initialisation de variables */
        gridViewEventsReceived = view.findViewById(R.id.listView);
        gridViewEventsReceived.setHasFixedSize(true);
        gridViewEventsReceived.setLayoutManager(new LinearLayoutManager(getContext()));
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_items);
        scrollView = view.findViewById(R.id.scrollView);
        keysList = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        referenceRequests = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("requests").child("from");


        referenceRequests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keysList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String requestType = snapshot.child("requestType").getValue(String.class);
                    String key = snapshot.getKey();
                    keysList.add(snapshot.getKey());
                }
                _readReceivedRequests();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _readReceivedRequests();
            }
        });

        /** Contrôle lorsque le bouton back du téléphone est appuyé */
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (scrollView.getScrollY() > 0) {
                    scrollView.smoothScrollTo(0, 0);
                }
            }
        };

        _updateToken(FirebaseInstanceId.getInstance().getToken());


        return view;
    }

    /**
     * Rafraichissement du token
     * @param token
     */
    private void _updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    /**
     * Permet de récuperer les demandes reçu
     */
    private void _readReceivedRequests() {
        mUserModels = new ArrayList<>();
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users");

        referenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserModels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    for(String key: keysList) {
                        if (userModel.getId().equals(key)) {
                            Log.d("userId", userModel.getId());
                            mUserModels.add(userModel);
                        }
                    }
                }
                requestReceivedAdapter = new EventsRequestReceivedAdapter(getContext(), mUserModels);
                gridViewEventsReceived.setAdapter(requestReceivedAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
