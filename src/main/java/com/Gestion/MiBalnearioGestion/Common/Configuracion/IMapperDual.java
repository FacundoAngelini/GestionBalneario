package com.Gestion.MiBalnearioGestion.Common.Configuracion;

public interface IMapperDual<T1, T2, T3> extends IMapper<T1, T2> {

    T3 convertToResponseDTO(T1 entity);

}