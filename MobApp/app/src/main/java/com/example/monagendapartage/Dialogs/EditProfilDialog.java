package com.example.monagendapartage.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.monagendapartage.Models.CommonModels.UserModel;
import com.example.monagendapartage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class EditProfilDialog extends AppCompatDialogFragment {

    /** Déclaration de variables */
    private DatabaseReference mDatabaseUser;
    private EditText editTextChange, editTextAddressName;
    private TextView textViewChange, textViewSentence, typeVoie, textViewAddressName;
    private String childToChange, addressType, addressName;
    private Spinner addressTypeSpinner;
    private List<UserModel> mUsersList;
    private String titleUserModal, titleModal;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final Bundle mArgs = getArguments();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.edit_profil_dialog, null);

        textViewChange = view.findViewById(R.id.textViewChange);
        textViewSentence = view.findViewById(R.id.textViewSentence);
        typeVoie = view.findViewById(R.id.textViewTypeDeVoie);
        textViewAddressName = view.findViewById(R.id.textViewAddressName);
        editTextAddressName = view.findViewById(R.id.editTextAddressName);
        editTextChange = view.findViewById(R.id.editTextChange);

        addressTypeSpinner = view.findViewById(R.id.adressTypeSpinner);

        titleModal = "";
        titleUserModal = "Modifier votre profil";

        mDatabaseUser = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser);
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                titleModal = titleUserModal;
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                _setViewLogicUser(userModel, mArgs, view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        /** On crée notre vue */
        builder.setView(view)
                .setTitle(titleModal)
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (childToChange.equals("numeroVoie")) {
                            mDatabaseUser.child(childToChange).setValue(editTextChange.getText().toString());
                            mDatabaseUser.child(addressType).setValue(addressTypeSpinner.getSelectedItem().toString());
                            mDatabaseUser.child(addressName).setValue(editTextAddressName.getText().toString());
                            mArgs.clear();

                        } else {
                            mDatabaseUser.child(childToChange).setValue(editTextChange.getText().toString());
                            mArgs.clear();
                        }
                    }
                });


        return builder.create();
    }

    private void _setViewLogicUser(UserModel userModel, Bundle mArgs, View view) {
        if (mArgs == null) {
            Toast.makeText(getContext(), "Une erreur est survenue lors de l'ouverture de l'edition de profil", Toast.LENGTH_SHORT).show();
        } else if (mArgs.getString("editPhoneNumber") != null) {
            _editPhoneNumber(userModel);

        } else if (mArgs.getString("editAddress") != null) {
            _editAddress(userModel, view);

        } else if (mArgs.getString("editCountry") != null ) {
            _editCountry(userModel);

        } else if (mArgs.getString("editZip") != null) {
            _editZip(userModel);

        }
    }

    private void _editPhoneNumber(UserModel userModel) {
        childToChange = "telephone";
        textViewSentence.setText("- Saisir un nouveau numéro de téléphone -");
        textViewChange.setText("Numéro de Téléphone");
        editTextChange.setText(userModel.getTelephone());
        editTextChange.setInputType(InputType.TYPE_CLASS_PHONE);
    }

    private void _editAddress(UserModel userModel, View view) {
        childToChange = "numeroVoie";
        addressType = "typeVoie";
        addressName = "nomVoie";

        Spinner adressTypeSpinner = view.findViewById(R.id.adressTypeSpinner);
        ArrayAdapter<CharSequence> adresseTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.adressTypeSpinner, android.R.layout.simple_spinner_item);
        adresseTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adressTypeSpinner.setAdapter(adresseTypeAdapter);

        typeVoie.setVisibility(View.VISIBLE);
        addressTypeSpinner.setVisibility(View.VISIBLE);
        textViewAddressName.setVisibility(View.VISIBLE);
        editTextAddressName.setVisibility(View.VISIBLE);
        adressTypeSpinner.setSelection(1);

        textViewSentence.setText("- Saisir une nouvelle adresse -");
        textViewChange.setText("Numéro de voie");

        editTextChange.setText(userModel.getNumeroVoie());
        editTextAddressName.setText(userModel.getNomVoie());
    }

    private void _editCountry(UserModel userModel) {
        childToChange = "ville";
        textViewSentence.setText("- Saisir une nouvelle ville -");
        textViewChange.setText("Ville");
        editTextChange.setText(userModel.getVille());
        editTextChange.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    private void _editZip(UserModel userModel) {
        childToChange = "codePostal";
        textViewSentence.setText("- Saisir un nouveau code postale -");
        textViewChange.setText("Code Postale");
        editTextChange.setText(userModel.getCodePostal());
        editTextChange.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

}
