package com.example.monagendapartage.Adapters.EventsAdapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monagendapartage.Models.CommonModels.UserModel;
import com.example.monagendapartage.Models.MyEventsModels.MyEventsModel;
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

public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.ViewHolder> {

    /** Déclaration de variables */
    private Context mContext;
    private List<MyEventsModel> mEvents;
    private List<String> mUsernames;
    // private StringBuilder builder;


    /** Constructeur */
    public MyEventsAdapter(Context mContext, List<MyEventsModel> mEvents) {
        this.mEvents = mEvents;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.events_item, parent, false);
        return new MyEventsAdapter.ViewHolder(view);
    }

    /**
     * Fonction permettant de contrôler chaque élément de nos vues xml
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final MyEventsAdapter.ViewHolder holder, int position) {
        final MyEventsModel eventsModel = mEvents.get(position);
        final StringBuilder builder;

        /*if (builder != null) {
            Log.d("builderClean","clearbUIlder");
            builder.setLength(0);
        }*/

        builder = new StringBuilder();

        Log.d("contentBuilder1", ""+builder.toString());

        holder.dateDebut.setText(eventsModel.getStartDate());
        holder.dateFin.setText(eventsModel.getEndDate());
        holder.startHour.setText(eventsModel.getStartHour());
        holder.endHour.setText(eventsModel.getEndHour());
        holder.description.setText(eventsModel.getEventDescription());

        holder.dateDebutTextView.setVisibility(View.VISIBLE);
        holder.dateDebutTextView.setText("Date de début :");
        holder.dateFinTextView.setVisibility(View.VISIBLE);
        holder.dateFinTextView.setText("Date de fin :");
        holder.startHourTextView.setVisibility(View.VISIBLE);
        holder.startHourTextView.setText("Heure de début :");
        holder.endHourTextView.setVisibility(View.VISIBLE);
        holder.endHourTextView.setText("Heure de fin :");
        holder.descriptionTextView.setVisibility(View.VISIBLE);
        holder.descriptionTextView.setText("Description de l'événement :");

        // Si l'évenement est partagé
        if (eventsModel.getSharedWith() != null) {
            builder.setLength(0);
            Log.d("contentBuilder3", ""+builder.toString());
            holder.sharedWithLinearLayout.setVisibility(View.VISIBLE);
            holder.iconIsShared.setVisibility(View.VISIBLE);
            holder.principalLinearLayout.setBackgroundColor(Color.parseColor("#9B59B6"));
            holder.secondaryLinearLayout.setBackgroundColor(Color.parseColor("#9B59B6"));
            holder.principalRelativeLayout.setBackgroundColor(Color.parseColor("#9B59B6"));

            for (Object value: eventsModel.getSharedWith().values()) {
                Log.d("contentValue", ""+value);
                _getUsername(holder, value.toString(), builder);

            }

            holder.sharedWithTextView.setText("Evenement partagé avec :");
            holder.sharedWithTextView.setVisibility(View.VISIBLE);

            holder.sharedWith.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Retourne le nombre d'item présent dans mEvents
     * @return
     */
    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    /**
     * Fonction permettant de récuperer les éléments XML de l'item 'events_item.xml'
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateDebut, dateFin;
        public TextView startHour, endHour;
        public TextView description;

        public ImageView iconIsShared;

        public TextView dateDebutTextView, dateFinTextView, startHourTextView, endHourTextView, descriptionTextView, sharedWith, sharedWithTextView;

        public LinearLayout principalLinearLayout, secondaryLinearLayout, sharedWithLinearLayout;

        public RelativeLayout principalRelativeLayout;

        public ViewHolder (View itemView) {
            super(itemView);

            dateDebut = itemView.findViewById(R.id.dateDebut);
            dateFin = itemView.findViewById(R.id.dateFin);
            startHour = itemView.findViewById(R.id.startHour);
            endHour = itemView.findViewById(R.id.endHour);
            description = itemView.findViewById(R.id.description);
            principalLinearLayout = itemView.findViewById(R.id.principalLinearLayout);
            secondaryLinearLayout = itemView.findViewById(R.id.secondaryLinearLayout);
            principalRelativeLayout = itemView.findViewById(R.id.principalRelativeLayout);
            sharedWithLinearLayout = itemView.findViewById(R.id.sharedWithLinearLayout);
            sharedWithTextView = itemView.findViewById(R.id.sharedWithTextView);
            sharedWith = itemView.findViewById(R.id.sharedWith);

            dateDebutTextView = itemView.findViewById(R.id.dateDebutTextView);
            dateFinTextView = itemView.findViewById(R.id.dateFinTextView);
            startHourTextView = itemView.findViewById(R.id.startHourTextView);
            endHourTextView = itemView.findViewById(R.id.endHourTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);

            iconIsShared = itemView.findViewById(R.id.event_item_logo_shared);

        }
    }

    private void _getUsername(final ViewHolder holder, final String valueReceived, final StringBuilder builderReceived) {
        Log.d("valueReceived", valueReceived);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("inDataChanged", "inDataChanged");
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    UserModel userInfo = snapshot.getValue(UserModel.class);
                    Log.d("contentUserInf", ""+userInfo.toString());
                    if (userInfo.getId().equals(valueReceived)){
                        Log.d("itstrue", "true");
                        Log.d("userName", userInfo.getUsername());
                        builderReceived.append(userInfo.getUsername());
                        builderReceived.append(" ");
                        Log.d("contentBuilder2",builderReceived.toString());
                        holder.sharedWith.setText(builderReceived.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
