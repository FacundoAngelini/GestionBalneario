package com.Gestion.MiBalnearioGestion.Empleados.Servicio;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;
public class EmpleadoSpecification {

    public static PredicateSpecification<EmpleadoEntity> dniIgual(Integer dni){
        return (root, cb) -> dni == null
                ? cb.conjunction()
                : cb.equal(root.get("dni"), dni);
    }


    public static PredicateSpecification<EmpleadoEntity> dniContiene(Integer dni){
        return (root, cb) -> dni == null
                ? cb.conjunction()
                : cb.like(
                root.get("dni").as(String.class),
                "%" + dni + "%"
        );
    }



    public static PredicateSpecification<EmpleadoEntity> nombreIgual(String nombre){
        return (root, cb) -> nombre == null || nombre.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("nombre"), nombre);
    }


    public static PredicateSpecification<EmpleadoEntity> nombreContiene(String nombre){
        return (root, cb) -> nombre == null || nombre.isBlank()
                ? cb.conjunction()
                : cb.like(root.get("nombre"), "%" + nombre + "%");
    }



    public static PredicateSpecification<EmpleadoEntity> apellidoIgual(String apellido){
        return (root, cb) -> apellido == null || apellido.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("apellido"), apellido);
    }


    public static PredicateSpecification<EmpleadoEntity> apellidoContiene(String apellido){
        return (root, cb) -> apellido == null || apellido.isBlank()
                ? cb.conjunction()
                : cb.like(root.get("apellido"), "%" + apellido + "%");
    }



    public static PredicateSpecification<EmpleadoEntity> telefonoIgual(String telefono){
        return (root, cb) -> telefono == null
                ? cb.conjunction()
                : cb.equal(root.get("telefono"), telefono);
    }


    public static PredicateSpecification<EmpleadoEntity> telefonoContiene(String telefono){
        return (root, cb) -> telefono == null
                ? cb.conjunction()
                : cb.like(root.get("telefono"), "%" + telefono + "%");
    }



    public static PredicateSpecification<EmpleadoEntity> cuitIgual(String cuit){
        return (root, cb) -> cuit == null || cuit.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("cuit"), cuit);
    }


    public static PredicateSpecification<EmpleadoEntity> cuitContiene(String cuit){
        return (root, cb) -> cuit == null || cuit.isBlank()
                ? cb.conjunction()
                : cb.like(root.get("cuit"), "%" + cuit + "%");
    }



    public static PredicateSpecification<EmpleadoEntity> sueldoIgual(Double sueldo){
        return (root, cb) -> sueldo == null
                ? cb.conjunction()
                : cb.equal(root.get("sueldo"), sueldo);
    }


    public static PredicateSpecification<EmpleadoEntity> sueldoMenor(Double sueldo){
        return (root, cb) -> sueldo == null
                ? cb.conjunction()
                : cb.lessThan(root.get("sueldo"), sueldo);
    }


    public static PredicateSpecification<EmpleadoEntity> sueldoMayor(Double sueldo){
        return (root, cb) -> sueldo == null
                ? cb.conjunction()
                : cb.greaterThan(root.get("sueldo"), sueldo);
    }




    // SECTOR

    public static PredicateSpecification<EmpleadoEntity> sectorIgual(String sector){
        return (root, cb) -> sector == null || sector.isBlank()
                ? cb.conjunction()
                : cb.equal(
                root.join("sector").get("nombre"),
                sector
        );
    }


    public static PredicateSpecification<EmpleadoEntity> sectorContiene(String sector){
        return (root, cb) -> sector == null || sector.isBlank()
                ? cb.conjunction()
                : cb.like(
                root.join("sector").get("nombre"),
                "%" + sector + "%"
        );
    }




    // ROL

    public static PredicateSpecification<EmpleadoEntity> rolIgual(String rol){
        return (root, cb) -> rol == null || rol.isBlank()
                ? cb.conjunction()
                : cb.equal(
                root.join("rol").get("tipoRol"),
                rol
        );
    }


    public static PredicateSpecification<EmpleadoEntity> rolContiene(String rol){
        return (root, cb) -> rol == null || rol.isBlank()
                ? cb.conjunction()
                : cb.like(
                root.join("rol")
                        .get("tipoRol")
                        .as(String.class),
                "%" + rol + "%"
        );
    }




    // DIRECCION

    public static PredicateSpecification<EmpleadoEntity> calleIgual(String calle){
        return (root, cb) -> calle == null || calle.isBlank()
                ? cb.conjunction()
                : cb.equal(
                root.join("direccion").get("calle"),
                calle
        );
    }


    public static PredicateSpecification<EmpleadoEntity> calleContiene(String calle){
        return (root, cb) -> calle == null || calle.isBlank()
                ? cb.conjunction()
                : cb.like(
                root.join("direccion").get("calle"),
                "%" + calle + "%"
        );
    }


    public static PredicateSpecification<EmpleadoEntity> numeroIgual(Integer numero){
        return (root, cb) -> numero == null
                ? cb.conjunction()
                : cb.equal(
                root.join("direccion").get("numero"),
                numero
        );
    }



    public static PredicateSpecification<EmpleadoEntity> ciudadIgual(String ciudad){
        return (root, cb) -> ciudad == null || ciudad.isBlank()
                ? cb.conjunction()
                : cb.equal(
                root.join("direccion").get("ciudad"),
                ciudad
        );
    }


    public static PredicateSpecification<EmpleadoEntity> ciudadContiene(String ciudad){
        return (root, cb) -> ciudad == null || ciudad.isBlank()
                ? cb.conjunction()
                : cb.like(
                root.join("direccion").get("ciudad"),
                "%" + ciudad + "%"
        );
    }



    public static PredicateSpecification<EmpleadoEntity> provinciaIgual(String provincia){
        return (root, cb) -> provincia == null || provincia.isBlank()
                ? cb.conjunction()
                : cb.equal(
                root.join("direccion").get("provincia"),
                provincia
        );
    }



    public static PredicateSpecification<EmpleadoEntity> provinciaContiene(String provincia){
        return (root, cb) -> provincia == null || provincia.isBlank()
                ? cb.conjunction()
                : cb.like(
                root.join("direccion").get("provincia"),
                "%" + provincia + "%"
        );
    }



    // ESTADO

    public static PredicateSpecification<EmpleadoEntity> estadoIgual(EEstadoEmpleado estado){
        return (root, cb) -> estado == null
                ? cb.conjunction()
                : cb.equal(
                root.get("estadoEmpleado"),
                estado
        );
    }

}