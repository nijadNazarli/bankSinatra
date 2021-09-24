package com.miw.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.validation.constraints.*;
import java.util.Objects;

public abstract class User {

    private final Logger logger = LoggerFactory.getLogger(User.class);

    protected int userId;

    @NotEmpty
    @Email
    protected String email;

    @NotEmpty
    @Size(min = 8, max = 64)
    //@Pattern(regexp = "^(.+?)(?:\\1)+$") - maar dan dus het tegenovergestelde van dit aah
    protected String password;

    protected String salt;

    @NotEmpty
    protected String firstName;

    protected String prefix;

    @NotEmpty
    protected String lastName;

    protected boolean isBlocked;

    public User(String email, String password, String salt, String firstName, String prefix, String lastName, boolean isBlocked) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.prefix = prefix;
        this.lastName = lastName;
        this.salt = salt;
        this.isBlocked = isBlocked;
        logger.info("new User-object created");
    }

    public User(String email, String password, String salt, String firstName, String prefix, String lastName) {
        this(email, password, salt, firstName, prefix, lastName, false);
    }

    public User() {
        this(null, null, null, null, null, null, false);
    }

    public User(String email, String password) {
        super();
        this.email = email;
        this.password = password;
        logger.info("new User-object created");
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email);
    }
}


