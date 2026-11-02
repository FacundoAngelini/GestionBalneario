package com.Gestion.MiBalnearioGestion.Pedidos.Tareas;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.IPedidoRepository;
import com.Gestion.MiBalnearioGestion.Pedidos.Servicios.PedidoService;
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

    private final IPedidoRepository pedidoRepository;
    private final PedidoService pedidoService;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void cancelarPedidosVencidos() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(5);

        List<PedidoEntity> vencidos = pedidoRepository
                .findByEstadoPedidoAndFechaCreacionBefore(EEstadoPedido.PENDIENTE_PAGO, limite);

        if (vencidos.isEmpty()) return;

        log.info("Cancelando de forma lógica {} pedido(s) vencidos.", vencidos.size());

        for (PedidoEntity pedido : vencidos) {
            try {
                pedidoService.cancelarPedido(pedido.getPublicId());
                log.info("  Pedido {} cancelado e invalidado en MP exitosamente.", pedido.getPublicId());
            } catch (Exception e) {
                log.error("Error al cancelar automáticamente el pedido {}: {}", pedido.getPublicId(), e.getMessage());
            }
        }
    }
}