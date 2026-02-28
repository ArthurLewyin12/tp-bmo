package core.generics;

import java.util.List;
import java.util.Optional;

public interface IRepository<T> {
    void save(String id, T obj);
    Optional<T> findById(String id);
    List<T> findAll();
    void delete(String id);
    boolean exists(String id);
}
