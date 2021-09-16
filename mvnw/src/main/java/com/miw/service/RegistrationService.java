
package com.miw.service;

import com.miw.model.Client;
import com.miw.database.RootRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

  private RootRepository rootRepository;

  private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

  @Autowired
  public RegistrationService(RootRepository rootRepository) {
    super();
    this.rootRepository = rootRepository;
    logger.info("New RegistrationService");
  }

  public Client register(Client client) {
    rootRepository.saveUser(client);
    return client;
  }

  public RootRepository getRootRepository() {
    return rootRepository;
  }
}
