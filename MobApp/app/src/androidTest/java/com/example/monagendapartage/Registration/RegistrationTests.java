package com.example.monagendapartage.Registration;

import android.widget.DatePicker;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.example.monagendapartage.Activities.MainActivitySharedAgenda.MainActivity;
import com.example.monagendapartage.Activities.RegistrationActivities.RegisterActivity;
import com.example.monagendapartage.R;
import com.example.monagendapartage.helper.ToastMatcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.monagendapartage.helper.CustomHelperFunctions.customSleep;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

public class RegistrationTests {

    private String username;
    private String email;
    private String lastName;
    private String firstName;
    private String birthDate;
    private String numeroVoie;
    private String typeVoie;
    private String nomVoie;
    private String codePostal;
    private String ville;
    private String telephone;
    private String imageURL;
    private String status;
    private String search;

    private String password;
    private String passwordConfirmation;

    // Invalid values
    private String emailInvalidPattern;
    private String loginInvalid;
    private String passswordInvalidLength;
    private String passswordInvalidPattern;
    private String passswordConfirmationInvalid;

    @Rule
    public IntentsTestRule<RegisterActivity> mActivityRule =
            new IntentsTestRule<>(RegisterActivity.class);

    @Before
    public void initStringValues() {
        // Specify valid values.
        username = "test_account";
        email = "test.account@test.fr";
        lastName = "Admin";
        firstName = "Admin";
        birthDate = "01/01/1980";
        numeroVoie = "60";
        typeVoie = "Rue";
        nomVoie = "Reaumur";
        codePostal = "75003";
        ville = "Paris";
        telephone = "0102030405";

        password = "Azerty@123";
        passwordConfirmation = "Azerty@123";

        // Specify invalid values.
        loginInvalid = "doe_j";
        emailInvalidPattern = "Bad-email.patter@";
        passswordInvalidLength = "Abc12";
        passswordInvalidPattern = "azerty123";
        passswordConfirmationInvalid = "Azerty456";
    }

    public void fillValidValues() {
        onView(withId(R.id.username))
                .perform(scrollTo(), typeText(username), closeSoftKeyboard());

        onView(withId(R.id.firstname))
                .perform(scrollTo(), typeText(firstName), closeSoftKeyboard());

        onView(withId(R.id.lastname))
                .perform(scrollTo(), typeText(lastName), closeSoftKeyboard());

        // Show the date picker
        onView(withId(R.id.birthDate)).perform(scrollTo(),click());
        // Sets a date on the date picker widget
        onView(isAssignableFrom(DatePicker.class)).perform(setDate(1980, 10, 30));
        // Confirm the selected date. This example uses a standard DatePickerDialog
        // which uses
        // android.R.id.button1 for the positive button id.
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.numeroVoieET))
                .perform(scrollTo(), typeText(numeroVoie), closeSoftKeyboard());

        onView(withId(R.id.adressTypeSpinner))
                .perform(click());
        onData(allOf(is(instanceOf(String.class)),is(typeVoie))).perform(click());

        onView(withId(R.id.adressNameET))
                .perform(scrollTo(), typeText(nomVoie), closeSoftKeyboard());

        onView(withId(R.id.codePostal))
                .perform(scrollTo(),typeText(codePostal), closeSoftKeyboard());


        onView(withId(R.id.townName))
                .perform(scrollTo(), typeText(ville), closeSoftKeyboard());

        onView(withId(R.id.numeroTelephoneET))
                .perform(scrollTo(), typeText(telephone), closeSoftKeyboard());

        onView(withId(R.id.email))
                .perform(scrollTo(), typeText(email), closeSoftKeyboard());

        onView(withId(R.id.passwordTxt))
                .perform(scrollTo(), typeText(password), closeSoftKeyboard());

        onView(withId(R.id.passwordTxtRetype))
                .perform(scrollTo(), typeText(passwordConfirmation), closeSoftKeyboard());
    }

    @Test
    // Test resgirterFalse with wrong values
    public void Register_nok_password_lenth() {
        // Type text
        fillValidValues();

        // Change invalid value
        onView(withId(R.id.passwordTxt))
                .perform(scrollTo(), clearText(), typeText(passswordInvalidLength), closeSoftKeyboard());
        onView(withId(R.id.passwordTxtRetype))
                .perform(scrollTo(), clearText(), typeText(passswordInvalidLength), closeSoftKeyboard());

        // press the button "Register"
        onView(withId(R.id.btn_register)).perform(scrollTo(), click());

        // Check if Toast show
        onView(withText(R.string.ACTIVITY_REGISTER_LENGTH_PASSWORD_TOO_SHORT_FR)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

    }

    @Test
    // Test resgirterFalse with wrong values
    public void Register_nok_password_pattern() {
        // Type text
        fillValidValues();

        // Change invalid value
        onView(withId(R.id.passwordTxt))
                .perform(scrollTo(), clearText(), typeText(passswordInvalidPattern), closeSoftKeyboard());
        onView(withId(R.id.passwordTxtRetype))
                .perform(scrollTo(), clearText(), typeText(passswordInvalidPattern), closeSoftKeyboard());

        // press the button "Register"
        onView(withId(R.id.btn_register)).perform(scrollTo(), click());

        // Check if Toast show
        onView(withText(R.string.ACTIVITY_REGISTER_LOWER_UPPER_DIGIT_FR)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

    }

    @Test
    // Test resgirterFalse with wrong values
    public void Register_nok_password_confirmation() {
        // Type text
        fillValidValues();

        // Change invalid value
        onView(withId(R.id.passwordTxtRetype))
                .perform(scrollTo(), clearText(), typeText(passswordConfirmationInvalid), closeSoftKeyboard());

        // press the button "Register"
        onView(withId(R.id.btn_register)).perform(scrollTo(), click());

        // Check if Toast show
        onView(withText(R.string.ACTIVITY_REGISTER_PASSWORD_IS_DIFFERENT_FR)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

    }

    @Test
    // Test resgirterFalse with wrong values
    public void Register_nok_email_pattern() {
        // Type text
        fillValidValues();

        // Change invalid value
        onView(withId(R.id.email))
                .perform(scrollTo(), clearText(), typeText(emailInvalidPattern), closeSoftKeyboard());

        // press the button "Register"
        onView(withId(R.id.btn_register)).perform(scrollTo(), click());

        // Check if Toast show
        onView(withText(R.string.ACTIVITY_REGISTER_EMAIL_FORMAT_ERROR_FR)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

    }

    @Test
    // Test resgirterFalse with wrong values
    public void Register_nok_login_duplicate() {
        // Type text
        fillValidValues();

        // Change invalid value
        onView(withId(R.id.username))
                .perform(scrollTo(), clearText(), typeText(loginInvalid), closeSoftKeyboard());

        // press the button "Register"
        onView(withId(R.id.btn_register)).perform(scrollTo(), click());

        // Check if Toast show
        onView(withText(R.string.ACTIVITY_REGISTER_DUPLICATE_USERNAME)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

    }

    @Test
    // Test loginFalse with right values
    public void Register_ok() {
        // Type text
        fillValidValues();

        // press the button "Register"
        onView(withId(R.id.btn_register)).perform(scrollTo(), click());

        // Check if Toast show
        onView(withText(R.string.ACTIVITY_REGISTER_SIGN_UP_SUCCESS_FR)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        // Check if MainActivity is launched
        intended(hasComponent(MainActivity.class.getName()));

    }
}
