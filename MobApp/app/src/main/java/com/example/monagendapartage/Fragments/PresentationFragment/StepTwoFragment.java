package com.example.monagendapartage.Fragments.PresentationFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.monagendapartage.Activities.AuthentificationActivities.LoginActivity;
import com.example.monagendapartage.R;

public class StepTwoFragment extends Fragment {
    Button buttonLetsGo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_two, container, false);

        buttonLetsGo = view.findViewById(R.id.buttonLetsGo);

        buttonLetsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
