
package com.miw.database;

import com.miw.model.Account;
import com.miw.model.Client;
import com.miw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RootRepository {

  private final Logger logger = LoggerFactory.getLogger(RootRepository.class);

  private ClientDao clientDAO;
  private JdbcAccountDao temp;

  @Autowired
  public RootRepository(ClientDao clientDAO, JdbcAccountDao jdbcAccountDao) { // TODO: interface aanroepen ipv jdbcAccountDAO zelf
    super();
    this.clientDAO = clientDAO;
    this.temp = jdbcAccountDao;
    logger.info("New RootRepository");
  }

  public Client saveUser(Client client) {
    client = clientDAO.save(client);
    Account updatedAccount = temp.save(client.getAccount(), client.getUserId());
    client.setAccount(updatedAccount);
    return client;
  }

  public User findByEmail(String email) {
      return clientDAO.findByEmail(email);
    }
}
