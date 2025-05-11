package blackorbs.dev.blackshop.util

import android.content.Context
import android.view.View
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import blackorbs.dev.blackshop.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.startsWith
import org.hamcrest.TypeSafeMatcher

class Robot(
    private val context: Context = ApplicationProvider.getApplicationContext()
) {

    fun assertTextDisplayed(@StringRes textResId: Int) {
        onView(withText(context.getString(textResId))).check(matches(isDisplayed()))
    }

    fun assertTextDisplayed(text: String) {
        onView(withText(text)).check(matches(isDisplayed()))
    }

    fun assertTextNotDisplayed(@StringRes textResId: Int) {
        onView(withText(context.getString(textResId))).check(doesNotExist())
    }

    fun assertTextInListItem(
        position: Int,
        textPrefix: String,
        @IdRes recyclerViewId: Int = R.id.products_list
    ){
        onView(withId(recyclerViewId)).check(matches(
            hasItemAtPosition(
                position, hasDescendant(withText(startsWith(textPrefix)))
            )
        ))
    }

    fun enterText(@StringRes hintResId: Int, input: String) {
        onView(withHint(context.getString(hintResId)))
            .perform(clearText(), typeText(input), closeSoftKeyboard())
    }

    fun clickButton(@IdRes buttonId: Int) {
        onView(withId(buttonId)).perform(click())
    }

    fun clickButtonWithContentDescription(
        @StringRes textResId: Int
    ){
        onView(withContentDescription(
            context.getString(textResId)
        )).perform(click())
    }

    fun clickListItemAtPosition(
        position: Int,
        @IdRes recyclerViewId: Int = R.id.products_list,
    ) {
        onView(withId(recyclerViewId))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    position, click()
                )
            )
    }

    fun clickAddToCartInItemAtPosition(
        position: Int,
        @IdRes addToCartButtonId: Int = R.id.add_btn,
        @IdRes recyclerViewId: Int = R.id.products_list
    ) {
        onView(withId(recyclerViewId)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                position,
                clickChildViewWithId(addToCartButtonId)
            )
        )
    }

    private fun clickChildViewWithId(@IdRes id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isAssignableFrom(View::class.java)

            override fun getDescription(): String = "Click on a child view with specified id: $id"

            override fun perform(uiController: UiController, view: View) {
                val v = view.findViewById<View>(id)
                v?.performClick()
            }
        }
    }


    private fun hasItemAtPosition(position: Int, matcher: Matcher<View>) : Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

            override fun describeTo(description: Description?) {
                description?.appendText("has item at position $position : ")
                matcher.describeTo(description)
            }

            override fun matchesSafely(item: RecyclerView?): Boolean {
                val viewHolder = item?.findViewHolderForAdapterPosition(position)
                return matcher.matches(viewHolder?.itemView)
            }
        }
    }

    private fun withHint(expectedHint: String): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("EditText with hint: $expectedHint")
            }

            override fun matchesSafely(view: View): Boolean {
                if (view is EditText) {
                    val hint = view.hint?.toString()
                    return hint == expectedHint
                }
                return false
            }
        }
    }
}
