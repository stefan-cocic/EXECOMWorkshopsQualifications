package eu.execom.todolistgrouptwo.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// By Stefan Ćoćić
public class User {

    @DatabaseField(canBeNull = false, unique = true, columnName = "Email")
    private String email;

    @DatabaseField(canBeNull = false, columnName = "Password")
    private String password;

    @DatabaseField(canBeNull = false, columnName = "ConfirmPassword")
    private String confirmPassword;

    public User() {
    }

    public User(String email, String password, String confirmPassword) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", confirmPassword='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
    }
}
