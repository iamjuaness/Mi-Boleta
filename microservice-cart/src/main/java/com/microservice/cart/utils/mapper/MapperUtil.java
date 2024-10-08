package com.microservice.cart.utils.mapper;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapperUtil {

    /**
     * Método genérico para mapear una lista de un tipo a otro.
     *
     * @param sourceList La lista de objetos de tipo T.
     * @param mapper La función de mapeo que convierte de T a R.
     * @param <T> El tipo de los elementos de entrada.
     * @param <R> El tipo de los elementos de salida.
     * @return Una lista de objetos de tipo R.
     */
    public static <T, R> List<R> mapList(List<T> sourceList, Function<T, R> mapper) {
        return sourceList.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }
}
