package com.miw.service;

import com.miw.database.RootRepository;
import com.miw.service.authentication.RegistrationService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegistrationServiceTest {

    private RootRepository mockRepo;
    private RegistrationService registrationService;

    @BeforeEach
    public void setup(){
        mockRepo = Mockito.mock(RootRepository.class);
        registrationService = new RegistrationService(mockRepo);
    }

    @AfterAll
    public void tearDown(){
        mockRepo = null;
        registrationService = null;
    }

    @Test
    void register() {
        //TODO: test aanpassen als we iets interessanters kunnen testen

        //User testUser = new User("testje", "welkom123");
        //Mockito.when(mockRepo.saveUser(testUser)).thenReturn(testUser);

        String username = "test";
        String password = "ookTest";
//        User testUser = registrationService.register(username, password);
//        assertThat(testUser.getEmailaddress()).isEqualTo("test");

    }

    @Test
    void generatePassword() {
    }
}