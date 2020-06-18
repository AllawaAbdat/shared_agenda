package com.example.monagendapartage.Authentification;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.example.monagendapartage.Activities.AuthentificationActivities.LoginActivity;
import com.example.monagendapartage.Activities.MainActivitySharedAgenda.MainActivity;
import com.example.monagendapartage.R;
import com.example.monagendapartage.helper.ToastMatcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.monagendapartage.helper.CustomHelperFunctions.customSleep;

public class AuthentificationTests {

    private String loginFalse;
    private String passswordFalse;

    private String loginTrue;
    private String passswordTrue;

    @Rule
    public IntentsTestRule<LoginActivity> mActivityRule =
            new IntentsTestRule<>(LoginActivity.class);

    @Before
    public void initStringValues() {
        // Specify an ivalid string.
        loginFalse = "email@mail.fr";
        passswordFalse = "Password123";

        // Specify a valid string.
        loginTrue = "chinam_l@laposte.com";
        passswordTrue = "Azerty123";
    }

    @Test
    // Test loginFalse with wrong values
    public void Login_nok_loginActivity() {
        // Type text and then press the button.
        onView(withId(R.id.email))
                .perform(typeText(loginFalse), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText(passswordFalse), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());

        // Check that the text was changed.
        onView(withId(R.id.email))
                .check(matches(withText(loginFalse)));

        // Check if Toast show
        onView(withText(R.string.ACTIVITY_LOGIN_WRONG_CREDENTIALS_FR)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

    }

    @Test
    // Test loginFalse with right values
    public void Login_ok_loginActivity() {
        // Type text and then press the button.
        onView(withId(R.id.email))
                .perform(typeText(loginTrue), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText(passswordTrue), closeSoftKeyboard());
        // Check that the text was changed.
        onView(withId(R.id.email))
                .check(matches(withText(loginTrue)));
        onView(withId(R.id.password))
                .check(matches(withText(passswordTrue)));

        onView(withId(R.id.btn_login)).perform(click());

        customSleep();

        // Check if MainActivity is launched
        intended(hasComponent(MainActivity.class.getName()));

    }
}
