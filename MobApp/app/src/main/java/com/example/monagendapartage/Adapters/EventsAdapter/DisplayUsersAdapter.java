package com.example.monagendapartage.Adapters.EventsAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.monagendapartage.Activities.MessageActivities.MessageActivity;
import com.example.monagendapartage.Activities.EventsActivities.UserProfilViewActivity;
import com.example.monagendapartage.Models.CommonModels.UserModel;
import com.example.monagendapartage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayUsersAdapter extends RecyclerView.Adapter<DisplayUsersAdapter.ViewHolder>  {

    /** Déclaration de variables */
    private Context mContext;
    private List<UserModel> mUserModels;

    /** Constructeur */
    public DisplayUsersAdapter(Context mContext, List<UserModel> mUserModels) {
        this.mUserModels = mUserModels;
        this.mContext = mContext;
    }

    /**
     * Création du view holder. On récupère les item à injecter
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_to_find_item, parent, false);
        return new DisplayUsersAdapter.ViewHolder(view);
    }

    /**
     * Fonction permettant de contrôler chaque élément de nos vues xml
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull DisplayUsersAdapter.ViewHolder holder, int position) {
        final UserModel userModel = mUserModels.get(position);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Demande d'ami envoyé par utilisateur courant ==> utilisateur selectionné
        final DatabaseReference referenceTo = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("requests").child("to").child(userModel.getId());
        // Demande d'ami reçu par utilisateur selectionné <== utilisateur courant
        final DatabaseReference referenceFrom = FirebaseDatabase.getInstance().getReference("Users").child(userModel.getId()).child("requests").child("from").child(firebaseUser.getUid());
        // Demande d'ami envoyé par l'utilisateur selectionné ==> utilisateur courant
        final DatabaseReference referenceToUser = FirebaseDatabase.getInstance().getReference("Users").child(userModel.getId()).child("requests").child("to").child(firebaseUser.getUid());

        final PopupMenu popup = new PopupMenu(mContext, holder.imageButton);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.display_user_profil_menu, popup.getMenu());

        holder.firstAndLastname.setText(userModel.getPrenom() + " " + userModel.getNom());
        holder.whichCountry.setText(userModel.getVille() + " " + userModel.getCodePostal());

        if (userModel.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(userModel.getImageURL()).into(holder.profile_image);
        }
        // Firebase : Demande d'ami envoyé par utilisateur courant ==> utilisateur selectionné
        referenceTo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String requestType = dataSnapshot.child("requestType").getValue(String.class);
                    if (requestType.equals("send")) {
                        popup.getMenu().getItem(1).setTitle("Demande envoyée !");
                        popup.getMenu().getItem(1).setEnabled(false);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.seeProfil:
                                        Intent intentProfil = new Intent(mContext, UserProfilViewActivity.class);
                                        intentProfil.putExtra("idUser", userModel.getId());
                                        mContext.startActivity(intentProfil);
                                        return true;
                                }
                                return true;
                            }
                        });
                    } else if (requestType.equals("accepted")) {
                        popup.getMenu().getItem(1).setTitle("Demande acceptée");
                        popup.getMenu().getItem(1).setEnabled(true);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.seeProfil:
                                        Intent intentProfil = new Intent(mContext, UserProfilViewActivity.class);
                                        intentProfil.putExtra("idUser", userModel.getId());
                                        mContext.startActivity(intentProfil);
                                        return true;
                                    case R.id.sendRequest:
                                        Intent intentMessage = new Intent(mContext, MessageActivity.class);
                                        intentMessage.putExtra("userid", userModel.getId());
                                        mContext.startActivity(intentMessage);
                                        return true;
                                }
                                return true;
                            }
                        });
                    } else if (requestType.equals("refused")) {
                        popup.getMenu().getItem(1).setTitle("Votre demande a été refusée");
                        popup.getMenu().getItem(1).setEnabled(false);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.seeProfil:
                                        Intent intentProfil = new Intent(mContext, UserProfilViewActivity.class);
                                        intentProfil.putExtra("idUser", userModel.getId());
                                        mContext.startActivity(intentProfil);
                                        return true;
                                }
                                return true;
                            }
                        });
                    }
                } else if (dataSnapshot.getValue() == null) {
                    popup.getMenu().getItem(1).setTitle("Envoyer une demande");
                    popup.getMenu().getItem(1).setEnabled(true);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.seeProfil:
                                    Intent intentProfil = new Intent(mContext, UserProfilViewActivity.class);
                                    intentProfil.putExtra("idUser", userModel.getId());
                                    mContext.startActivity(intentProfil);
                                    return true;
                                case R.id.sendRequest:
                                    HashMap<String, String> hashMapTo = new HashMap<>();
                                    hashMapTo.put("requestType", "send");
                                    HashMap<String, String> hashMapFrom = new HashMap<>();
                                    hashMapFrom.put("requestType", "received");
                                    referenceTo.setValue(hashMapTo);
                                    referenceFrom.setValue(hashMapFrom);
                                    return true;
                            }
                            return true;
                        }
                    });
                }
                // Firebase : Demande d'ami envoyé par l'utilisateur selectionné ==> utilisateur courant
                referenceToUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            String requestType = dataSnapshot.child("requestType").getValue(String.class);
                            if (requestType.equals("received")) {
                                popup.getMenu().getItem(1).setTitle("Demande envoyée !");
                                popup.getMenu().getItem(1).setEnabled(false);
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.seeProfil:
                                                Intent intentProfil = new Intent(mContext, UserProfilViewActivity.class);
                                                intentProfil.putExtra("idUser", userModel.getId());
                                                mContext.startActivity(intentProfil);
                                                return true;
                                        }
                                        return true;
                                    }
                                });
                            } else if (requestType.equals("accepted")) {
                                popup.getMenu().getItem(1).setTitle("Demande acceptée");
                                popup.getMenu().getItem(1).setEnabled(true);
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.seeProfil:
                                                Intent intentProfil = new Intent(mContext, UserProfilViewActivity.class);
                                                intentProfil.putExtra("idUser", userModel.getId());
                                                mContext.startActivity(intentProfil);
                                                return true;
                                            case R.id.sendRequest:
                                                Intent intentMessage = new Intent(mContext, MessageActivity.class);
                                                intentMessage.putExtra("userid", userModel.getId());
                                                mContext.startActivity(intentMessage);
                                                return true;
                                        }
                                        return true;
                                    }
                                });
                            } else if (requestType.equals("refused")) {
                                popup.getMenu().getItem(1).setTitle("Demande refusée");
                                popup.getMenu().getItem(1).setEnabled(false);
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.seeProfil:
                                                Intent intentProfil = new Intent(mContext, UserProfilViewActivity.class);
                                                intentProfil.putExtra("idUser", userModel.getId());
                                                mContext.startActivity(intentProfil);
                                                return true;
                                        }
                                        return true;
                                    }
                                });
                            }
                        } else if (dataSnapshot.getValue() == null) {
                            popup.getMenu().getItem(1).setTitle("Envoyer une demande");
                            popup.getMenu().getItem(1).setEnabled(true);
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.seeProfil:
                                            Intent intentProfil = new Intent(mContext, UserProfilViewActivity.class);
                                            intentProfil.putExtra("idUser", userModel.getId());
                                            mContext.startActivity(intentProfil);
                                            return true;
                                        case R.id.sendRequest:
                                            HashMap<String, String> hashMapTo = new HashMap<>();
                                            hashMapTo.put("requestType", "send");
                                            HashMap<String, String> hashMapFrom = new HashMap<>();
                                            hashMapFrom.put("requestType", "received");
                                            referenceTo.setValue(hashMapTo);
                                            referenceFrom.setValue(hashMapFrom);
                                            return true;
                                    }
                                    return true;
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.show();
            }
        });



    }

    /**
     * Retourne le nombre d'item présent dans mUserModels
     * @return
     */
    @Override
    public int getItemCount() {
        return mUserModels.size();
    }

    /**
     * Fonction permettant de récuperer les éléments XML de l'item 'user_to_find_item.xml'
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView firstAndLastname, whichCountry;
        public CircleImageView profile_image;
        public ImageButton imageButton;

        public ViewHolder (View itemView) {
            super(itemView);

            firstAndLastname = itemView.findViewById(R.id.firstAndLastname);
            profile_image = itemView.findViewById(R.id.profile_image);
            imageButton = itemView.findViewById(R.id.imageButton);
            whichCountry = itemView.findViewById(R.id.whichCountry);
        }
    }
}
