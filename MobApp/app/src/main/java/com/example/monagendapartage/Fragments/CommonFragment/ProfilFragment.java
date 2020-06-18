package com.example.monagendapartage.Fragments.CommonFragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.monagendapartage.Activities.AuthentificationActivities.LoginActivity;
import com.example.monagendapartage.Activities.EventsActivities.SendEmailActivity;
import com.example.monagendapartage.Activities.MainActivitySharedAgenda.MainActivity;
import com.example.monagendapartage.Models.CommonModels.UserModel;
import com.example.monagendapartage.Dialogs.EditProfilDialog;
import com.example.monagendapartage.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;


public class ProfilFragment extends Fragment {

    /** Déclaration de variables */
    private ImageView editPhoneNumberProfilImageView, editAdressProfilImageView, editCountryProfilImageView, editZipCodeProfilImageView;
    private TextView username, emailUser, telephone, adresse, ville, codePostal, pseudo;
    private Button deconnection, sendMail, deleteAccount;
    private DatabaseReference reference;
    private FirebaseUser fuser;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private UserModel currentUserModel;
    private CircleImageView image_profile;

    /** Constructeur */
    public ProfilFragment (UserModel currentUserModel) {
        this.currentUserModel = currentUserModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        /** Initialisation des variables */
        image_profile = view.findViewById(R.id.imageViewUser);
        username = view.findViewById(R.id.usernameTextView);
        pseudo = view.findViewById(R.id.pseudoTextView);
        emailUser = view.findViewById(R.id.emailUserTextView);
        deconnection = view.findViewById(R.id.deconnectionButton);
        telephone = view.findViewById(R.id.telephoneTextView);
        adresse = view.findViewById(R.id.adresseTextView);
        ville = view.findViewById(R.id.villeTextView);
        codePostal = view.findViewById(R.id.codePostalTextView);
        sendMail = view.findViewById(R.id.sendMail);

        deleteAccount = view.findViewById(R.id.deleteAccountButton);

        editPhoneNumberProfilImageView = view.findViewById(R.id.editPhoneNumberProfilImageView);
        editAdressProfilImageView = view.findViewById(R.id.editAdressProfilImageView);
        editCountryProfilImageView = view.findViewById(R.id.editCountryProfilImageView);
        editZipCodeProfilImageView = view.findViewById(R.id.editZipCodeProfilImageView);


        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        /** On récupère les informations de l'utilisateur */
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isAdded()) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    username.setText(userModel.getPrenom()+" "+ userModel.getNom());
                    pseudo.setText("("+ userModel.getUsername()+")");
                    emailUser.setText(userModel.getEmail());
                    telephone.setText(userModel.getTelephone());
                    adresse.setText(userModel.getNumeroVoie()+" "+ userModel.getTypeVoie()+" "+ userModel.getNomVoie());
                    ville.setText(userModel.getVille());
                    codePostal.setText(userModel.getCodePostal());
                    if (userModel.getImageURL().equals("default")) {
                        image_profile.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(getContext()).load(userModel.getImageURL()).into(image_profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });


        deconnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SendEmailActivity.class);
                startActivity(intent);
            }
        });

        editPhoneNumberProfilImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("editPhoneNumber", "editPhoneNumber");
                EditProfilDialog editProfilDialog = new EditProfilDialog();
                editProfilDialog.setArguments(bundle);
                editProfilDialog.show(getFragmentManager(), "EditProfilDialog");
            }
        });

        editAdressProfilImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("editAddress", "editAddress");
                EditProfilDialog editProfilDialog = new EditProfilDialog();
                editProfilDialog.setArguments(bundle);
                editProfilDialog.show(getFragmentManager(), "EditProfilDialog");
            }
        });

        editCountryProfilImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("editCountry", "editCountry");
                EditProfilDialog editProfilDialog = new EditProfilDialog();
                editProfilDialog.setArguments(bundle);
                editProfilDialog.show(getFragmentManager(), "EditProfilDialog");
            }
        });

        editZipCodeProfilImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("editZip", "editZip");
                EditProfilDialog editProfilDialog = new EditProfilDialog();
                editProfilDialog.setArguments(bundle);
                editProfilDialog.show(getFragmentManager(), "EditProfilDialog");
            }
        });

        // **** SUPRESSION DU COMPTE DES UTILISATEURS ****
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.GENERIC_DELETE_ACCOUNT_FR))
                        .setMessage(getString(R.string.GENERIC_YOU_WANT_TO_DELETE_YOUR_ACCOUNT_ARE_YOU_SURE_FR))
                        .setPositiveButton(getString(R.string.GENERIC_YES_FR), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                new AlertDialog.Builder(getContext())
                                        .setTitle(getString(R.string.GENERIC_DELETE_ACCOUNT_FR))
                                        .setMessage(getString(R.string.GENERIC_YOU_WANT_TO_DELETE_YOUR_ACCOUNT_ARE_YOU_SURE_CONFIRM_FR))
                                        .setPositiveButton(getString(R.string.GENERIC_YES_DELETE_ACCOUNT_FR), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (fuser != null) {
                                                    fuser.delete() // On supprime le compte de l'utilisateur
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        // On suprimme les datas de l'utilisateur dans la RealTime Database
                                                                        FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).removeValue();
                                                                        // On affiche un toast de confirmation de suppression du compte
                                                                        Toast.makeText(getContext(), getString(R.string.GENERIC_DELETE_ACCOUNT_TOAST_REDIRECTION_MSG_FR), Toast.LENGTH_SHORT).show();
                                                                        // Redirection vers la page de login
                                                                        startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        })
                                        .setNegativeButton(getString(R.string.GENERIC_CANCEL_FR), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();


                            }
                        })
                        .setNegativeButton(getString(R.string.GENERIC_CANCEL_FR), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        return view;
    }

    /** Permet d'ouvrir la gallerie de l'utilisateur */
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    /**
     * Permet de récupérer l'extension du fichier
     * @param uri
     */
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    /** Fonction permettant d'uploader la nouvelle image de l'utilisateur sur Firebase Storage */
    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw  task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        pd.dismiss();

                    } else {
                        Toast.makeText(getContext(), "Upload echoué !", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "Aucune image n'a été séléctionné", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload en cours", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }



}