package org.example;

import java.util.List;
import java.util.Optional;

public interface Repositorio<T, ID> {

    void guardar(ID id, T entidad);

    Optional<T> buscarPorId(ID id);

    List<T> obtenerTodos();
}
