package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final IClienteService clienteService;

    @GetMapping
    public List<ClienteDTO> listarTodos(){return clienteService.listarTodos();}

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarPorIdpublico(@PathVariable UUID IDpublico)
    {
        return new ResponseEntity<ClienteDTO>(clienteService.buscarPorIDpublico(IDpublico), HttpStatus.OK);
    }

    public ResponseEntity<ClienteDTO> crearCliente (@RequestBody ClienteDTO clienteNuevo)
    {
        return new ResponseEntity<ClienteDTO>(clienteService.crearCliente(clienteNuevo),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizarCliente (@PathVariable UUID IDpublico,@RequestBody ClienteDTO clienteNuevo)
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
