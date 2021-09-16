package com.miw.database;

import com.miw.model.Client;

public interface ClientDao {

  Client save(Client client);
  Client findByEmail(String email);
}
