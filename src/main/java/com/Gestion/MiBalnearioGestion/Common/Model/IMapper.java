package com.Gestion.MiBalnearioGestion.Common.Model;

public interface IMapper <T,U>{
    T toEntity(U u);
    U toDTO(T t);
}