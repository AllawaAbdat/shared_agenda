package com.example.monagendapartage.Adapters.EventsMessagerieAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.monagendapartage.Activities.MessageActivities.MessageActivity;
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

public class EventsRequestReceivedAdapter extends RecyclerView.Adapter<EventsRequestReceivedAdapter.ViewHolder> {

    /** Déclaration de variables */
    private Context mContext;
    private List<UserModel> mUserModels;

    /** Constructeur */
    public EventsRequestReceivedAdapter(Context mContext, List<UserModel> mUserModels) {
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
    public EventsRequestReceivedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.events_request_received_item, parent, false);
        return new EventsRequestReceivedAdapter.ViewHolder(view);
    }

    /**
     * Fonction permettant de contrôler chaque élément de nos vues xml
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final EventsRequestReceivedAdapter.ViewHolder holder, int position) {
        final UserModel userModel = mUserModels.get(position);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference referenceTo = FirebaseDatabase.getInstance().getReference("Users").child(userModel.getId()).child("requests").child("to").child(firebaseUser.getUid());
        final DatabaseReference referenceFrom = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("requests").child("from").child(userModel.getId());

        holder.username.setText(userModel.getUsername());
        if (userModel.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(userModel.getImageURL()).into(holder.profile_image);
        }

        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        referenceFrom.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String requestType = dataSnapshot.child("requestType").getValue(String.class);
                    Log.d("dsValue", dataSnapshot.getValue().toString());
                    if (requestType.equals("received")) {
                        holder.acceptButton.setBackgroundResource(R.drawable.bg_button_accepted_ui);
                        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Si il accepte il faut passer le status dans from de received à accepted
                                // du coup
                                HashMap<String, String> hashMapFrom = new HashMap<>();
                                hashMapFrom.put("requestType", "accepted");
                                referenceFrom.setValue(hashMapFrom);
                                referenceTo.setValue(hashMapFrom);
                            }
                        });

                        holder.cancelButton.setBackgroundResource(R.drawable.bg_button_cancel_ui);
                        holder.cancelButton.setText("Refuser");
                        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HashMap<String, String> hashMapFrom = new HashMap<>();
                                hashMapFrom.put("requestType", "refused");
                                referenceFrom.setValue(hashMapFrom);
                            }
                        });
                    } else if (requestType.equals("accepted")) {
                        holder.acceptButton.setBackgroundResource(R.drawable.bg_button_generic_ui);
                        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(mContext, MessageActivity.class);
                                intent.putExtra("userid", userModel.getId());
                                mContext.startActivity(intent);
                            }
                        });
                        holder.acceptButton.setText("Envoyer un message");
                        holder.acceptButton.setEnabled(true);

                        holder.cancelButton.setBackgroundResource(R.drawable.bg_button_cancel_ui);
                        holder.cancelButton.setText("Supprimer");
                        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new AlertDialog.Builder(mContext)
                                        .setTitle("Etes vous sur ?")
                                        .setMessage("Etes vous sur de vouloir supprimer "+ userModel.getUsername()+ " de votre liste ?")
                                        .setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                referenceFrom.removeValue();
                                            }
                                        })
                                        .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show();
                            }
                        });
                    } else if (requestType.equals("refused")) {
                        holder.acceptButton.setBackgroundResource(R.drawable.bg_button_accepted_ui);
                        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new AlertDialog.Builder(mContext)
                                        .setTitle("Etes vous sur ?")
                                        .setMessage("\"Vous pouvez encore accepter la demande qui vous a été envoyé. Si vous souhaitez supprimer définitivement cette demande, appuyez sur \"Supprimer\". Si vous souhaitez accepter cette demande, appuyez sur \"Accepter\"")
                                        .setPositiveButton("Accepter", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                HashMap<String, String> hashMapFrom = new HashMap<>();
                                                hashMapFrom.put("requestType", "accepted");
                                                referenceFrom.setValue(hashMapFrom);
                                            }
                                        })
                                        .setNegativeButton("Supprimer", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                referenceFrom.removeValue();
                                            }
                                        })
                                        .show();
                            }
                        });
                        holder.acceptButton.setText("Accepter");
                        holder.acceptButton.setBackgroundResource(R.drawable.bg_button_accepted_ui);
                        holder.cancelButton.setText("Déjà refusée");
                        holder.cancelButton.setBackgroundResource(R.drawable.bg_button_cancel_ui);
                        holder.cancelButton.setEnabled(false);
                    }
                } else if (dataSnapshot.getValue() == null) {
                    holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * Retourne le nombre d'item présent dans mUserModels
     * @return
     */
    @Override
    public int getItemCount() {
        if (mUserModels != null && mUserModels.size() > 0) {
            return mUserModels.size();
        }
        return 0;
    }

    /**
     * Fonction permettant de récuperer les éléments XML de l'item 'events_request_received_item.xml'
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView profile_image;
        public Button cancelButton, acceptButton;

        public ViewHolder (View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
}
