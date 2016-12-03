package eu.execom.todolistgrouptwo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.web.client.RestClientException;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.api.RestApi;
import eu.execom.todolistgrouptwo.model.User;
import eu.execom.todolistgrouptwo.util.Params;

@EActivity(R.layout.activity_register)
public class RegisterActivity extends AppCompatActivity {

    @ViewById
    EditText email;

    @ViewById
    EditText password;

    @ViewById
    EditText confirmPassword;

    @RestService
    RestApi restApi;

    @EditorAction(R.id.confirmPassword)
    @Click
    void register() {
        final String email = this.email.getText().toString();
        final String password = this.password.getText().toString();
        final String confirmPassword = this.confirmPassword.getText().toString();
        final User user = new User(email, password, confirmPassword);

        registerUser(user);
    }

    @Background
    void registerUser(User user) {
        /* By Stefan Ćoćić */
        try {
            restApi.register(user);
            login(user);
        } catch (RestClientException e) {
            showRegisterError();
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    @UiThread
    void login(User user) {
        final Intent intent = new Intent();

        /* By Stefan Ćoćić */
        intent.putExtra(Params.EMAIL, user.getEmail());

        /* By Stefan Ćoćić */
        intent.putExtra(Params.PASSWORD, user.getPassword());

        setResult(RESULT_OK, intent);
        finish();
    }

    @UiThread
    void showRegisterError() {
        email.setError("Email already exists.");

        /* By Stefan Ćoćić */
        email.requestFocus();
    }
}
