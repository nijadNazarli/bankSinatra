package com.miw.model;
/**
 * @Author: Nijad Nazarli
 * @Description: This class is used to conveniently pass on client details
 *              in the body of the request as JSON object while logging in
 */
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Objects;

public class Credentials {
    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    @Size(min = 8, max = 64)
    private String password;

    public Credentials(String email, String password) {
        super();
        this.email = email;
        this.password = password;
    }

    public Credentials() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credentials that = (Credentials) o;
        return Objects.equals(email, that.email) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String username) {
        this.email = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
