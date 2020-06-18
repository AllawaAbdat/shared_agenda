package com.example.monagendapartage.Adapters.EventsMessagerieAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.monagendapartage.Models.MyEventsModels.ChatModel;
import com.example.monagendapartage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    /** Déclaration de variables */
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<ChatModel> mChatModel;
    private String imageurl;

    private FirebaseUser fuser;

    /** Constructeur */
    public MessageAdapter(Context mContext, List<ChatModel> mChatModel, String imageurl) {
        this.mChatModel = mChatModel;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }

    /**
     * Création du view holder. On récupère les item à injecter
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    /**
     * Fonction permettant de contrôler chaque élément de nos vues xml
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        ChatModel chatModel = mChatModel.get(position);

        holder.show_message.setText(chatModel.getMessage());

        if (imageurl.equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        if (position == mChatModel.size()-1) {
            if (chatModel.isIsseen()) {
                holder.txt_seen.setText("Lu");
            } else {
                holder.txt_seen.setText("Envoyé");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }
    }

    /**
     * Retourne le nombre d'item présent dans mChatModel
     * @return
     */
    @Override
    public int getItemCount() {
        return mChatModel.size();
    }


    /**
     * Classe ViewHolder permettant de faire le lien entre l'adaptateur et la classe onBindViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public ImageView profile_image;

        public TextView txt_seen;

        public ViewHolder (View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
        }
    }

    /**
     * Permet de récuperer le type de message à afficher (Utilisateur ou Destinataire)
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChatModel.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
