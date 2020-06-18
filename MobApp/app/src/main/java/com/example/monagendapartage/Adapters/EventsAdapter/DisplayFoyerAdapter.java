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
import com.example.monagendapartage.Activities.EventsActivities.AddEventActivity;
import com.example.monagendapartage.Activities.EventsActivities.DisplayUserFoyerAgendaActivity;
import com.example.monagendapartage.Models.CommonModels.UserModel;
import com.example.monagendapartage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayFoyerAdapter extends RecyclerView.Adapter<DisplayFoyerAdapter.ViewHolder> {
    /** Déclaration de variables */
    private Context mContext;
    private List<UserModel> mUserModels;

    /** Constructeur */
    public DisplayFoyerAdapter(Context mContext, List<UserModel> mUserModels) {
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
    public DisplayFoyerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_of_foyer_item, parent, false);
        return new DisplayFoyerAdapter.ViewHolder(view);
    }

    /**
     * Fonction permettant de contrôler chaque élément de nos vues xml
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull DisplayFoyerAdapter.ViewHolder holder, int position) {
        final UserModel userModel = mUserModels.get(position);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference referenceTo = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("requests").child("to").child(userModel.getId());
        final DatabaseReference referenceFrom = FirebaseDatabase.getInstance().getReference("Users").child(userModel.getId()).child("requests").child("from").child(firebaseUser.getUid());

        final PopupMenu popup = new PopupMenu(mContext, holder.imageButton);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.display_foyer_profil_menu, popup.getMenu());

        holder.firstAndLastname.setText(userModel.getPrenom() + " " + userModel.getNom());
        holder.whichCountry.setText(userModel.getVille() + " " + userModel.getCodePostal());

        if (userModel.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(userModel.getImageURL()).into(holder.profile_image);
        }

        popup.getMenu().getItem(0).setTitle("Consulter l'agenda");
        popup.getMenu().getItem(0).setEnabled(true);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.seeAgenda:
                        Intent intentFoyerAgenda = new Intent(mContext, DisplayUserFoyerAgendaActivity.class);
                        intentFoyerAgenda.putExtra("idUser", userModel.getId());
                        mContext.startActivity(intentFoyerAgenda);
                        return true;
                    case R.id.addEvent:
                        Intent addEvent = new Intent(mContext, AddEventActivity.class);
                        addEvent.putExtra("idUser", userModel.getId());
                        mContext.startActivity(addEvent);
                        return true;
                }
                return true;
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
     * Fonction permettant de récuperer les éléments XML de l'item 'user_of_foyer_item.xml'
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
