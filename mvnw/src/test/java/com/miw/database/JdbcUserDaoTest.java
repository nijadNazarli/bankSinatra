package com.miw.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class JdbcUserDaoTest {

    @Autowired
    private JdbcUserDao userDao;

    @Test
    void getUserByEmail() {
        String emailAdmin = "admin@admin.com";
        String emailClient = "test3@test.com";
        System.out.println("Admin from database: " + userDao.getUserByEmail(emailAdmin));
        System.out.println("Client from database: " + userDao.getUserByEmail(emailClient));
    }

    @Test
    void getRoleByEmail() {
    }

    @Test
    void getRoleByID() {
    }

    @Test
    void getIDByEmail() {
    }
}