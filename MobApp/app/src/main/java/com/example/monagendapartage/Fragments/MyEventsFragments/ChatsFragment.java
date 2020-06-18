package com.example.monagendapartage.Fragments.MyEventsFragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monagendapartage.Adapters.EventsMessagerieAdapter.UserAdapter;
import com.example.monagendapartage.Models.CommonModels.UserModel;
import com.example.monagendapartage.Models.MyEventsModels.ChatListModel;
import com.example.monagendapartage.Notifications.Token;
import com.example.monagendapartage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

    /** Déclaration de variables */
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserModel> mUserModels;
    private EditText search_users;
    private String searchUserParced;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    private List<ChatListModel> usersList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mUserModels = savedInstanceState.getParcelableArrayList("mUserModels");
            Log.d("mUserModels", mUserModels.toString());
            Log.d("savedInstanceNull","savedInstanceNull");
            searchUserParced = savedInstanceState.getString("searchUserParced");
        } else {
            mUserModels = new ArrayList<>();
            Log.d("mUserModels", mUserModels.toString());
            Log.d("savedInstanceOk","savedInstanceOk");
            searchUserParced = "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        search_users = view.findViewById(R.id.search_users);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();


        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                _searchUsers(charSequence.toString().toLowerCase());
                searchUserParced = (charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("editable", editable.toString());
                _searchUsers(searchUserParced);
            }
        });

        _getUserChatList();

        _updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    /**
     * Permet de rafraichir le token de l'utilisateur
     * @param token
     */
    private void _updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    /**
     *
     */
    private void _chatList() {
        mUserModels = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserModels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    for (ChatListModel chatListModel : usersList) {
                        if (userModel.getId().equals(chatListModel.getId())) {
                            mUserModels.add(userModel);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUserModels, true);
                recyclerView.setAdapter(userAdapter);
                _updateToken(FirebaseInstanceId.getInstance().getToken());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Permet de chercher un utilisateur
     * @param s
     */
    private void _searchUsers(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserModels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    for (ChatListModel chatListModel : usersList) {
                        if (userModel.getId().equals(chatListModel.getId())) {
                            mUserModels.add(userModel);
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(), mUserModels, false);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     *
     */
    private void _getUserChatList() {
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatListModel chatListModel = snapshot.getValue(ChatListModel.class);
                    usersList.add(chatListModel);
                }

                _chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("searchUserParced", searchUserParced);
        outState.putParcelableArrayList("mUserModels", (ArrayList<? extends Parcelable>) mUserModels);
    }



}
