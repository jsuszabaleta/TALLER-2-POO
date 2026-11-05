package com.clinica;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class UtilidadesClinica {

    public static <T extends Comparable<? super T>> void ordenar(List<T> lista) {
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (lista.get(j).compareTo(lista.get(j + 1)) > 0) {
                    T temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
    }

    public static <T> List<T> filtrar(List<T> lista, Predicate<T> condicion) {
        List<T> resultado = new ArrayList<>();
        for (T item : lista) {
            if (condicion.test(item)) {
                resultado.add(item);
            }
        }
        return resultado;
    }


    public static <T, R> List<R> transformar(List<T> lista, Function<T, R> funcion) {
        List<R> resultado = new ArrayList<>();
        for (T item : lista) {
            resultado.add(funcion.apply(item));
        }
        return resultado;
    }


    public static <T> Optional<T> buscarPrimero(List<T> lista, Predicate<T> condicion) {
        for (T item : lista) {
            if (condicion.test(item)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public static int contarElementos(List<?> lista) {

        return lista == null ? 0 : lista.size();
    }

    public static List<String> obtenerPerfilesPersonas(List<? extends Persona> personas) {
        List<String> perfiles = new ArrayList<>();
        for (Persona p : personas) {
            perfiles.add(p.mostrarPerfil());
        }
        return perfiles;
    }

    public static void agregarPacientes(List<Paciente> origen, List<? super Paciente> destino) {
        for (Paciente p : origen) {
            destino.add(p);
        }
    }
}
