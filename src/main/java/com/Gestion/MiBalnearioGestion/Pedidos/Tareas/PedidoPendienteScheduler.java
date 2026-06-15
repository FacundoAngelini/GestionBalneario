package com.Gestion.MiBalnearioGestion.Pedidos.Tareas;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.iPedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoPendienteScheduler {

    private final iPedidoRepository pedidoRepository;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void cancelarPedidosVencidos() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(5);

        List<PedidoEntity> vencidos = pedidoRepository
                .findByEstadoPedidoAndFechaCreacionBefore(
                        EEstadoPedido.PENDIENTE_PAGO, limite);

        if (vencidos.isEmpty()) return;
        log.info("Cancelando {} pedido(s) vencidos.", vencidos.size());
        for (PedidoEntity p : vencidos) {
            p.setEstadoPedido(EEstadoPedido.CANCELADO);
            log.info(" Pedido {} cancelado por falta de pago.", p.getPublicId());
        }

        pedidoRepository.saveAll(vencidos);
    }
}