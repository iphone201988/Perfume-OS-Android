package com.tech.perfumos

import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.rules.ActivityScenarioRule


@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testEmptyUsernameShowsError() {
        // Click login button without entering username
        onView(withId(R.id.loginButton)).perform(click())

        // Check if error message is shown for username
        // If you use showErrorToast, Espresso can't check Toasts directly.
        // If you set error on EditText, use:
        onView(withId(R.id.username)).check(matches(hasErrorText("Please enter username")))
    }

    @Test
    fun testEmptyPasswordShowsError() {
        // Enter username
        onView(withId(R.id.username)).perform(typeText("testuser"), closeSoftKeyboard())

        // Click login button without entering password
        onView(withId(R.id.loginButton)).perform(click())

        // Check if error message is shown for password
        // If you use showToast, Espresso can't check Toasts directly.
        // If you set error on EditText, use:
        onView(withId(R.id.password)).check(matches(hasErrorText("Please enter password")))
    }

    @Test
    fun testValidLogin() {
        // Enter username
        onView(withId(R.id.username)).perform(typeText("qwe"), closeSoftKeyboard())

        // Enter password
        onView(withId(R.id.password)).perform(typeText("123456"), closeSoftKeyboard())

        // Click login button
        onView(withId(R.id.loginButton)).perform(click())

        // Here you can check if the next activity is launched or a success UI is shown.
        // This may require an IdlingResource or mocking the ViewModel/network layer.
    }
}