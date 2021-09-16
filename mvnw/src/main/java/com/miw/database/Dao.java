package com.miw.database;

import java.util.List;
import java.util.Optional;

/*@author Laura Wagenaar
* Deze interface schrijft de CRUD functionaliteit voor t.b.v. de JDBC DAO klassen*/

public interface Dao<T> {

    List<T> list();

    void create (T t);

    // an optional allows for an empty <T> to be returned when is doesn't exist instead of an exception
    Optional<T> get(int id);

    void update (T t, int id);

    void delete (int id);

}
