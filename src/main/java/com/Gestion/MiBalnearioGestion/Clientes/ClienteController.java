package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteRequest;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final IClienteService clienteService;

    @GetMapping //Response entity
    public ResponseEntity<List<ClienteResponse>> listarTodos(){return new ResponseEntity<>(clienteService.listarTodos(),HttpStatus.OK);}

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorIdpublico(@PathVariable UUID IDpublico)
    {
        return new ResponseEntity<ClienteResponse>(clienteService.buscarPorIDpublico(IDpublico), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @PostMapping
    public ResponseEntity<ClienteResponse> crearCliente (@Valid @RequestBody ClienteRequest clienteNuevo)
    {
        return new ResponseEntity<ClienteResponse>(clienteService.crearCliente(clienteNuevo),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizarCliente (@PathVariable UUID IDpublico, @Valid @RequestBody ClienteRequest clienteNuevo)
    {
        return new ResponseEntity<ClienteResponse>(clienteService.actualizarCliente(IDpublico,clienteNuevo),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable UUID IDpublico)
    {
        clienteService.borrarCliente(IDpublico);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


}
