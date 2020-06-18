package com.example.monagendapartage.Adapters.EventsMessagerieAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

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

import java.util.List;

public class EventsRequestSendedAdapter extends RecyclerView.Adapter<EventsRequestSendedAdapter.ViewHolder> {
    /** Déclaration de variables */
    private Context mContext;
    private List<UserModel> mUserModels;

    /** Constructeur */
    public EventsRequestSendedAdapter(Context mContext, List<UserModel> mUserModels) {
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
    public EventsRequestSendedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.events_request_sended_item, parent, false);
        return new EventsRequestSendedAdapter.ViewHolder(view);
    }

    /**
     * Fonction permettant de contrôler chaque élément de nos vues xml
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final EventsRequestSendedAdapter.ViewHolder holder, int position) {
        final UserModel userModel = mUserModels.get(position);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference referenceTo = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("requests").child("to").child(userModel.getId());
        final DatabaseReference referenceFrom = FirebaseDatabase.getInstance().getReference("Users").child(userModel.getId()).child("requests").child("from").child(firebaseUser.getUid());

        holder.username.setText(userModel.getUsername());
        if (userModel.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(userModel.getImageURL()).into(holder.profile_image);
        }

        referenceTo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String requestType = dataSnapshot.child("requestType").getValue(String.class);
                    if (requestType.equals("accepted")) {
                        holder.message.setVisibility(View.GONE);
                        holder.requestAcceptedTextView.setVisibility(View.VISIBLE);
                        holder.cancelButton.setText("Envoyer un message");
                        holder.cancelButton.setBackgroundResource(R.drawable.bg_button_generic_ui);
                        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(mContext, MessageActivity.class);
                                intent.putExtra("userid", userModel.getId());
                                mContext.startActivity(intent);
                            }

                        });
                        holder.deleteButton.setVisibility(View.VISIBLE);
                        holder.deleteButton.setBackgroundResource(R.drawable.bg_button_cancel_ui);
                        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new AlertDialog.Builder(mContext)
                                        .setTitle("Etes vous sur ?")
                                        .setMessage("Souhaitez vous supprimer "+ userModel.getUsername()+" de votre liste ?")
                                        .setPositiveButton("Annuler la demande", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                referenceTo.removeValue();
                                                referenceFrom.removeValue();
                                            }
                                        })
                                        .setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        });
                    } else if (requestType.equals("send")) {
                        holder.requestAcceptedTextView.setVisibility(View.GONE);
                        holder.message.setVisibility(View.VISIBLE);
                        holder.cancelButton.setBackgroundResource(R.drawable.bg_button_cancel_ui);
                        holder.deleteButton.setVisibility(View.GONE);
                        holder.cancelButton.setVisibility(View.VISIBLE);
                        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new AlertDialog.Builder(mContext)
                                        .setTitle("Etes vous sur ?")
                                        .setMessage("Vous avez envoyé une demande à "+ userModel.getUsername()+ ". Souhaitez vous annuler votre demande ?")
                                        .setPositiveButton("Annuler la demande", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                referenceTo.removeValue();
                                                referenceFrom.removeValue();
                                            }
                                        })
                                        .setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        });
                    }
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
     * Fonction permettant de récuperer les éléments XML de l'item 'events_request_sended_item.xml'
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username, requestAcceptedTextView, message;
        public ImageView profile_image;
        public Button cancelButton, deleteButton;

        public ViewHolder (View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            requestAcceptedTextView = itemView.findViewById(R.id.requestAcceptedTextView);
            message = itemView.findViewById(R.id.message);
            profile_image = itemView.findViewById(R.id.profile_image);
            cancelButton = itemView.findViewById(R.id.cancelButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
