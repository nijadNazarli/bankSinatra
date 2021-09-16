package miw.database;

import java.util.List;

/*@author Laura Wagenaar
* Deze interface schrijft de CRUD functionaliteit voor t.b.v. de JDBC DAO klassen*/

public interface Dao<T> {

    List<T> list();

    void save (T t);

    void update (T t, int id);

/*  TODO: kan je een RowMapper methode via een interface implementeren?
     void RowMapper<T>;*/

}
