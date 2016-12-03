package eu.execom.todolistgrouptwo.preference;

import org.androidannotations.annotations.sharedpreferences.SharedPref;


@SharedPref(SharedPref.Scope.UNIQUE)
public interface UserPreferences {

    String accessToken();

}
