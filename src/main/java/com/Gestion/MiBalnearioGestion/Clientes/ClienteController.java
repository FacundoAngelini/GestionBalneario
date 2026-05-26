package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
@AllArgsConstructor
public class ClienteController {

    private final IClienteService clienteService;

    @GetMapping //Response entity
    public ResponseEntity<List<ClienteDTO>> listarTodos(){return new ResponseEntity<>(clienteService.listarTodos(),HttpStatus.OK);}

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarPorIdpublico(@PathVariable UUID IDpublico)
    {
        return new ResponseEntity<ClienteDTO>(clienteService.buscarPorIDpublico(IDpublico), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente (@Valid @RequestBody ClienteDTO clienteNuevo)
    {
        return new ResponseEntity<ClienteDTO>(clienteService.crearCliente(clienteNuevo),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizarCliente (@PathVariable UUID IDpublico, @Valid @RequestBody ClienteDTO clienteNuevo)
    {
        return new ResponseEntity<ClienteDTO>(clienteService.actualizarCliente(IDpublico,clienteNuevo),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable UUID IDpublico)
    {
        clienteService.borrarCliente(IDpublico);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


}
