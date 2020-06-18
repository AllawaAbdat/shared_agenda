package com.example.monagendapartage.Adapters.EventsMessagerieAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.monagendapartage.Activities.MessageActivities.MessageActivity;
import com.example.monagendapartage.Models.MyEventsModels.ChatModel;
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



public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    /** Déclaration de variables */
    private Context mContext;
    private List<UserModel> mUserModels;
    private boolean ischat;
    private String theLastMessage;

    /** Constructeur */
    public UserAdapter(Context mContext, List<UserModel> mUserModels, boolean ischat) {
        this.mUserModels = mUserModels;
        this.mContext = mContext;
        this.ischat = ischat;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    /**
     * Fonction permettant de contrôler chaque élément de nos vues xml
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final UserModel userModel = mUserModels.get(position);
        holder.username.setText(userModel.getUsername());
        if (userModel.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(userModel.getImageURL()).into(holder.profile_image);
        }

        if (ischat) {
            lastMessage(userModel.getId(), holder.last_msg);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (ischat) {
            if (userModel.getStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", userModel.getId());
                mContext.startActivity(intent);
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;

        /**
         * Classe ViewHolder permettant de faire le lien entre l'adaptateur et la classe onBindViewHolder
         */
        public ViewHolder (View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }


    /**
     * Fonction permettant de récuperer et d'afficher le dernier message envoyé
     * @param userid
     * @param last_msg
     */
    private void lastMessage(final String userid, final TextView last_msg) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chatModel = snapshot.getValue(ChatModel.class);
                    if (chatModel.getReceiver().equals(firebaseUser.getUid()) && chatModel.getSender().equals(userid) ||
                        chatModel.getReceiver().equals(userid) && chatModel.getSender().equals(firebaseUser.getUid())){
                        theLastMessage = chatModel.getMessage();
                    }
                }
                switch (theLastMessage){
                    case "default":
                        last_msg.setText("Aucun Message");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
