package com.clinica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositorioMemoria<T, ID> implements Repositorio<T, ID> {

    private final Map<ID, T> datos = new HashMap<>();

    @Override
    public void guardar(ID id, T entidad) {
        datos.put(id, entidad);
    }

    @Override
    public Optional<T> buscarPorId(ID id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public List<T> obtenerTodos() {
        return new ArrayList<>(datos.values());
    }
}
