package com.miw.service.authentication;

import org.springframework.stereotype.Service;

@Service
public class PepperService {

    private static final String PEPPER = "d24145c413bac64082d2a9681e20890a"; // dit is dus eigenlijk top secret

    public String getPepper() {
        return PEPPER;
    }
}
