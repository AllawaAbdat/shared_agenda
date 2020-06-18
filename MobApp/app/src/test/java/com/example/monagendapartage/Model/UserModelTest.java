package com.example.monagendapartage.Model;


import com.example.monagendapartage.Model.Helper.HelperCustomClass;
import com.example.monagendapartage.Models.CommonModels.UserModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class UserModelTest {

    private UserModel userModel;

    private static final String DEFAULT_USER_ID = HelperCustomClass.generateString();
    private static final String DEFAULT_USER_USERNAME = "USER123";
    private static final String DEFAULT_USER_IMG_URL = "default";
    private static final String DEFAULT_USER_STATUS = "online";
    private static final String DEFAULT_USER_SEARCH = "user123";
    private static final String DEFAULT_USER_IS_PETSITTER = "yes";
    private static final String DEFAULT_USER_NUMEROVOIE = "12";
    private static final String DEFAULT_USER_NOMVOIE = "Des fleurs";
    private static final String DEFAULT_USER_TYPEVOIE = "rue";
    private static final String DEFAULT_USER_CODEPOSTAL = "75011";
    private static final String DEFAULT_USER_NOM = "Doe";
    private static final String DEFAULT_USER_PRENOM = "John";
    private static final String DEFAULT_USER_VILLE = "Paris";
    private static final String DEFAULT_USER_TELEPHONE = "0102030405";
    private static final String DEFAULT_USER_EMAIL = "mail@mail.fr";

    @Before
    public void setUp() throws Exception {
        userModel = new UserModel();

        // Add Things here !!
        // Mock for services, Factories or DAO for example...

    }

    @Test
    public void createPetSitter() {
        // Here we test getters

        userModel.setId(DEFAULT_USER_ID);
        assertEquals(DEFAULT_USER_ID,userModel.getId());

        userModel.setUsername(DEFAULT_USER_USERNAME);
        assertEquals(DEFAULT_USER_USERNAME,userModel.getUsername());

        userModel.setImageURL(DEFAULT_USER_IMG_URL);
        assertEquals(DEFAULT_USER_IMG_URL,userModel.getImageURL());

        userModel.setStatus(DEFAULT_USER_STATUS);
        assertEquals(DEFAULT_USER_STATUS,userModel.getStatus());

        userModel.setSearch(DEFAULT_USER_SEARCH);
        assertEquals(DEFAULT_USER_SEARCH,userModel.getSearch());
        assertEquals(DEFAULT_USER_USERNAME.toLowerCase(),userModel.getSearch());

        userModel.setNumeroVoie(DEFAULT_USER_NUMEROVOIE);
        assertEquals(DEFAULT_USER_NUMEROVOIE,userModel.getNumeroVoie());

        userModel.setNomVoie(DEFAULT_USER_NOMVOIE);
        assertEquals(DEFAULT_USER_NOMVOIE,userModel.getNomVoie());

        userModel.setTypeVoie(DEFAULT_USER_TYPEVOIE);
        assertEquals(DEFAULT_USER_TYPEVOIE,userModel.getTypeVoie());

        userModel.setCodePostal(DEFAULT_USER_CODEPOSTAL);
        assertEquals(DEFAULT_USER_CODEPOSTAL,userModel.getCodePostal());

        userModel.setNom(DEFAULT_USER_NOM);
        assertEquals(DEFAULT_USER_NOM,userModel.getNom());

        userModel.setPrenom(DEFAULT_USER_PRENOM);
        assertEquals(DEFAULT_USER_PRENOM,userModel.getPrenom());

        userModel.setVille(DEFAULT_USER_VILLE);
        assertEquals(DEFAULT_USER_VILLE,userModel.getVille());

        userModel.setTelephone(DEFAULT_USER_TELEPHONE);
        assertEquals(DEFAULT_USER_TELEPHONE,userModel.getTelephone());

        userModel.setEmail(DEFAULT_USER_EMAIL);
        assertEquals(DEFAULT_USER_EMAIL,userModel.getEmail());

        userModel.setCodePostal(DEFAULT_USER_CODEPOSTAL);
        assertEquals(DEFAULT_USER_CODEPOSTAL,userModel.getCodePostal());

    }


    @Test
    public void UpdatePetSitter() {

        String newuserModelID = HelperCustomClass.generateString();
        userModel.setId(newuserModelID); // Should not be modified...
        String newuserModeluserModelname = "userModel456";
        userModel.setUsername(newuserModeluserModelname);
        userModel.setImageURL("https://firebasestorage.googleapis.com/");
        userModel.setStatus("offline");
        userModel.setSearch("usermodel456");

        assertEquals(newuserModelID, userModel.getId());
        assertEquals(newuserModeluserModelname, userModel.getUsername());
        assertEquals("https://firebasestorage.googleapis.com/", userModel.getImageURL());
        assertEquals("offline", userModel.getStatus());
        assertEquals(newuserModeluserModelname.toLowerCase(), userModel.getSearch());

    }

    @Test
    public void testEquality() throws Exception {


        UserModel userModel2 = new UserModel();
        userModel2 = userModel;
        assertEquals(userModel2,userModel);

        UserModel userModel3;
        userModel3 = new UserModel(userModel.getId(),
                userModel.getUsername(),
                userModel.getImageURL(),
                userModel.getStatus(),
                userModel.getSearch(),
                userModel.getNumeroVoie(),
                userModel.getNomVoie(),
                userModel.getTypeVoie(),
                userModel.getCodePostal(),
                userModel.getNom(),
                userModel.getPrenom(),
                userModel.getVille(),
                userModel.getTelephone(),
                userModel.getEmail());

        assertNotEquals(userModel,userModel3);

    }

    @After
    public void free() {
        userModel = null;
    }
}
