package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.AjustePreciosDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.ResultadoAjustePreciosDTO;

public interface IPrecioLoteServicio {
    ResultadoAjustePreciosDTO ajustar(AjustePreciosDTO dto);
}