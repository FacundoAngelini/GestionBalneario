package com.Gestion.MiBalnearioGestion.Common.Configuracion;

public interface IMapper<T1,T2>{
    T1 convertToEntity(T2 t2, Class<T1> t1Class);
    T2 convertToDTO(T1 t);
}
