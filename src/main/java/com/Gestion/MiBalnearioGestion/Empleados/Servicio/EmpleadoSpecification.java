package com.Gestion.MiBalnearioGestion.Empleados.Servicio;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class EmpleadoSpecification {

    public static PredicateSpecification<EmpleadoEntity> dniIgual(Integer dniIgual){
        return(root,cb)-> dniIgual == null
                ? cb.conjunction()
                : cb.equal(root.get("dni"), dniIgual);
    }

    public static PredicateSpecification<EmpleadoEntity> dniContiene(Integer dniContiene){
        return(root,cb)-> dniContiene == null
                ? cb.conjunction()
                : cb.like(root.get("dni"),"%"+ dniContiene + "%");
    }



    public static PredicateSpecification<EmpleadoEntity> nombreIgual(String nombreIgual){
        return(root,cb)-> nombreIgual == null || nombreIgual.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("nombre"), nombreIgual);
    }

    public static PredicateSpecification<EmpleadoEntity> nombreContiene(String nombreContiene){
        return(root,cb)-> nombreContiene == null || nombreContiene.isBlank()
                ? cb.conjunction()
                : cb.like(root.get("nombre"),"%"+ nombreContiene + "%");
    }


    public static PredicateSpecification<EmpleadoEntity> apellidoIgual(String apellidoIgual){
        return(root,cb)-> apellidoIgual == null || apellidoIgual.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("apellido"), apellidoIgual);
    }

    public static PredicateSpecification<EmpleadoEntity> apellidoContiene(String apellidoContiene){
        return(root,cb)-> apellidoContiene == null || apellidoContiene.isBlank()
                ? cb.conjunction()
                : cb.like(root.get("apellido"),"%"+ apellidoContiene + "%");
    }

    public static PredicateSpecification<EmpleadoEntity> telefonoIgual(String telefonoIgual){
        return(root,cb)-> telefonoIgual == null
                ? cb.conjunction()
                : cb.equal(root.get("telefono"), telefonoIgual);
    }

    public static PredicateSpecification<EmpleadoEntity> telefonoContiene(String telefonoContiene){
        return(root,cb)-> telefonoContiene == null
                ? cb.conjunction()
                : cb.like(root.get("telefono"),"%"+ telefonoContiene + "%");
    }

    public static PredicateSpecification<EmpleadoEntity> cuitIgual(String cuitIgual){
        return(root,cb)-> cuitIgual == null || cuitIgual.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("cuit"), cuitIgual);
    }

    public static PredicateSpecification<EmpleadoEntity> cuitContiene(String cuitContiene){
        return(root,cb)-> cuitContiene == null || cuitContiene.isBlank()
                ? cb.conjunction()
                : cb.like(root.get("cuit"),"%"+ cuitContiene + "%");
    }

    public static PredicateSpecification<EmpleadoEntity> sueldoIgual(Double sueldoIgual){
        return(root,cb)-> sueldoIgual == null
                ? cb.conjunction()
                : cb.equal(root.get("sueldo"), sueldoIgual);
    }

    public static PredicateSpecification<EmpleadoEntity> sueldoMenor(Double sueldoIgual){
        return(root,cb)-> sueldoIgual == null
                ? cb.conjunction()
                : cb.lessThan(root.get("sueldo"), sueldoIgual);
    }

    public static PredicateSpecification<EmpleadoEntity> sueldoMayor(Double sueldoMayor){
        return(root,cb)-> sueldoMayor == null
                ? cb.conjunction()
                : cb.greaterThan(root.get("sueldo"), sueldoMayor);
    }


    public static PredicateSpecification<EmpleadoEntity> sectorIgual(String sectorIgual){
        return(root,cb)-> sectorIgual == null || sectorIgual.isBlank()
                ? cb.conjunction()
                : cb.equal(root.join("sector").get("sector"), sectorIgual);
    }

    public static PredicateSpecification<EmpleadoEntity> sectorContiene(String sectorContiene){
        return(root,cb)-> sectorContiene == null || sectorContiene.isBlank()
                ? cb.conjunction()
                : cb.like(root.join("sector").get("sector"),"%"+ sectorContiene + "%");
    }

    public static PredicateSpecification<EmpleadoEntity> rolIgual(String rolIgual){
        return(root,cb)-> rolIgual == null || rolIgual.isBlank()
                ? cb.conjunction()
                : cb.equal(root.join("rol").get("rol"), rolIgual);
    }

    public static PredicateSpecification<EmpleadoEntity> rolContiene(String rolContiene){
        return(root,cb)-> rolContiene == null || rolContiene.isBlank()
                ? cb.conjunction()
                : cb.like(root.get("rol"),"%"+ rolContiene + "%");
    }

    public static PredicateSpecification<EmpleadoEntity> calleIgual(String calleIgual){
        return(root,cb)-> calleIgual == null || calleIgual.isBlank()
                ? cb.conjunction()
                : cb.equal(root.join("direccion").get("calle"), calleIgual);
    }

    public static PredicateSpecification<EmpleadoEntity> calleContiene(String calleContiene){
        return(root,cb)-> calleContiene == null || calleContiene.isBlank()
                ? cb.conjunction()
                : cb.like(root.join("direccion").get("calle"),"%"+ calleContiene + "%");
    }

    public static PredicateSpecification<EmpleadoEntity> numeroIgual(Integer numeroIgual){
        return(root,cb)-> numeroIgual == null
                ? cb.conjunction()
                : cb.equal(root.join("direccion").get("numero"), numeroIgual);
    }

    public static PredicateSpecification<EmpleadoEntity> numeroContiene(Integer numeroContiene){
        return(root,cb)-> numeroContiene == null
                ? cb.conjunction()
                : cb.like(root.join("direccion").get("numero"),"%"+ numeroContiene + "%");
    }

    public static PredicateSpecification<EmpleadoEntity> ciudadIgual(String ciudadIgual){
        return(root,cb)-> ciudadIgual == null || ciudadIgual.isBlank()
                ? cb.conjunction()
                : cb.equal(root.join("direccion").get("ciudad"), ciudadIgual);
    }

    public static PredicateSpecification<EmpleadoEntity> ciudadContiene(String ciudadContiene){
        return(root,cb)-> ciudadContiene == null || ciudadContiene.isBlank()
                ? cb.conjunction()
                : cb.like(root.join("direccion").get("ciudad"),"%"+ ciudadContiene + "%");
    }

    public static PredicateSpecification<EmpleadoEntity> provinciaIgual(String provinciaIgual){
        return(root,cb)-> provinciaIgual == null || provinciaIgual.isBlank()
                ? cb.conjunction()
                : cb.equal(root.join("direccion").get("provincia"), provinciaIgual);
    }

    public static PredicateSpecification<EmpleadoEntity> provinciaContiene(String provinciaContiene){
        return(root,cb)-> provinciaContiene == null || provinciaContiene.isBlank()
                ? cb.conjunction()
                : cb.like(root.join("direccion").get("provincia"),"%"+ provinciaContiene + "%");
    }

    public static PredicateSpecification<EmpleadoEntity> estadoIgual(EEstadoEmpleado estadoEmpleado){
        return (root,cb)-> estadoEmpleado == null
                ? cb.conjunction()
                :cb.equal(root.get("estado"), estadoEmpleado);
    }

    public static PredicateSpecification<EmpleadoEntity> estadoActivo(EEstadoEmpleado estadoContiene){
        return (root,cb)->cb.equal(root.get("ACTIVO"),estadoContiene);
    }

    public static PredicateSpecification<EmpleadoEntity> estadoInactivo(EEstadoEmpleado estadoContiene){
        return (root,cb)->cb.equal(root.get("INACTIVO"),estadoContiene);
    }












}