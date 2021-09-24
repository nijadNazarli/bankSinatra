package com.miw.database;

import com.miw.model.Client;

public interface ClientDao {

  Client save(Client client);
  Client findByEmail(String email);
  Client findByBsn(int bsn);
  Client findByAccountId(int accountId);
}
