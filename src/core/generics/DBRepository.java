package core.generics;
import java.util.*;


public class DBRepository<T> {

    private final Map<String, T> storage = new HashMap<>();

    // Créer / ajouter un objet
    public void save(String id, T obj) {
        storage.put(id, obj);
    }

    // Lire un objet par ID
    public Optional<T> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    // Lire tous les objets
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    // Supprimer un objet
    public void delete(String id) {
        storage.remove(id);
    }

    // Vérifier existence
    public boolean exists(String id) {
        return storage.containsKey(id);
    }

}
