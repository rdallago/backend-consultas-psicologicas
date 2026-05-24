package com.mycompany.consultas.psicologicas.service;

import java.util.Map;
import java.util.HashMap;

import com.mycompany.consultas.psicologicas.exception.RecursoNoEncontradoException;
import com.mycompany.consultas.psicologicas.model.EstadoTurno;
import com.mycompany.consultas.psicologicas.model.Turno;
import com.mycompany.consultas.psicologicas.repository.TurnoRepository;
import com.mycompany.consultas.psicologicas.repository.VisitaRepository;
import com.mycompany.consultas.psicologicas.repository.HistoriaClinicaRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private VisitaRepository visitaRepository;

    // ===============================
    // LISTAR TODOS
    // ===============================
    public List<Turno> listar() {

        return turnoRepository.findAll();
    }

    // ===============================
    // BUSCAR POR ID
    // ===============================
    public Turno obtenerPorId(Long id) {

        return turnoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Turno no encontrado"
                        ));
    }

    // ===============================
    // CREAR TURNO
    // ===============================
    public Turno guardar(Turno turno) {

       

            turno.setEstado(EstadoTurno.AGENDADO);
        

        validarSeparacion40Minutos(
                turno.getFechaHora(),
                null,
                turno.getEstado()
        );

        validarMotivoSegunEstado(turno);

        return turnoRepository.save(turno);
    }

    // ===============================
    // MODIFICAR TURNO
    // ===============================
    // ===============================
// MODIFICAR TURNO
// ===============================
        public Turno actualizar(Long id,Turno turnoActualizado) {

    Turno turnoExistente = obtenerPorId(id);

    // =========================================
    // SI YA ESTÁ ASISTIDO NO SE PUEDE EDITAR
    // =========================================

    if (turnoExistente.getEstado() == EstadoTurno.ASISTIO) {

        throw new RuntimeException(
                "Los turnos ASISTIDOS solo pueden modificarse desde Visitas"
        );
    }

    // =========================================
    // NO PERMITIR ASIGNAR ASISTIO DESDE TURNOS
    // =========================================

    if (turnoActualizado.getEstado() == EstadoTurno.ASISTIO) {

        throw new RuntimeException(
                "El estado ASISTIO solo puede asignarse desde Visitas"
        );
    }

    // =========================================
    // VALIDACIONES
    // =========================================

    validarSeparacion40Minutos(
            turnoActualizado.getFechaHora(),
            id,
            turnoActualizado.getEstado()
    );

    // =========================================
    // ACTUALIZAR DATOS
    // =========================================

    turnoExistente.setPaciente(
            turnoActualizado.getPaciente()
    );

    turnoExistente.setFechaHora(
            turnoActualizado.getFechaHora()
    );

    turnoExistente.setEstado(
            turnoActualizado.getEstado()
    );

    turnoExistente.setMotivo(
            turnoActualizado.getMotivo()
    );

    validarMotivoSegunEstado(
            turnoExistente
    );

    return turnoRepository.save(
            turnoExistente
    );
}

    // ===============================
    // CAMBIAR ESTADO
    // ===============================
    public Turno cambiarEstado(
            Long id,
            EstadoTurno estado
    ) {

        Turno turno =
                obtenerPorId(id);

        turno.setEstado(estado);

        validarMotivoSegunEstado(
                turno
        );

        return turnoRepository.save(
                turno
        );
    }

    // ===============================
    // BUSCAR ENTRE FECHAS
    // ===============================
    public List<Turno> buscarEntreFechas(
            LocalDateTime desde,
            LocalDateTime hasta
    ) {

        return turnoRepository
                .findByFechaHoraBetween(
                        desde,
                        hasta
                );
    }

    // ===============================
    // ELIMINAR
    // ===============================
    public void eliminar(Long id) {
         Turno turno = turnoRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("El turno no existe.")
            );

             
        if (turno.getEstado() == EstadoTurno.ASISTIO) {

    throw new RuntimeException(
            "No se puede eliminar un turno ASISTIDO"
    );
}

        if (visitaRepository.existsByTurnoId(id)) {

            throw new RuntimeException(
                    "No se puede eliminar el turno porque posee una visita asociada."
            );
        }

        turnoRepository.deleteById(id);
    }

    // ===============================
    // VALIDAR 40 MINUTOS
    // ===============================
    private void validarSeparacion40Minutos(
            LocalDateTime nuevaFecha,
            Long idActual,
            EstadoTurno estadoNuevo
    ) {

        // ====================================
        // SI EL NUEVO TURNO ES CANCELADO
        // O AUSENTE -> NO VALIDAR
        // ====================================
        if (
                estadoNuevo == EstadoTurno.CANCELADO
                || estadoNuevo == EstadoTurno.AUSENTE
        ) {

            return;
        }

        List<Turno> turnos =
                turnoRepository.findAll();

        for (Turno t : turnos) {

            // ====================================
            // IGNORAR MISMO TURNO EN EDICIÓN
            // ====================================
            if (
                    idActual != null
                    && t.getId().equals(idActual)
            ) {

                continue;
            }

            // ====================================
            // IGNORAR CANCELADOS Y AUSENTES
            // ====================================
            if (
                    t.getEstado() == EstadoTurno.CANCELADO
                    || t.getEstado() == EstadoTurno.AUSENTE
            ) {

                continue;
            }

            long diferencia = Math.abs(

                    Duration.between(
                            t.getFechaHora(),
                            nuevaFecha
                    ).toMinutes()
            );

            if (diferencia < 40) {

                throw new RuntimeException(
                        "Debe existir al menos 40 minutos entre turnos"
                );
            }
        }
    }

    // ===============================
    // VALIDAR MOTIVO
    // ===============================
    private void validarMotivoSegunEstado(
            Turno turno
    ) {

        EstadoTurno estado =
                turno.getEstado();

        // ====================================
        // AUSENTE O CANCELADO
        // REQUIEREN MOTIVO
        // ====================================
        if (
                estado == EstadoTurno.AUSENTE
                || estado == EstadoTurno.CANCELADO
        ) {

            if (
                    turno.getMotivo() == null
                    || turno.getMotivo().isBlank()
            ) {

                throw new RuntimeException(
                        "Debe ingresar un motivo para AUSENTE o CANCELADO"
                );
            }

        } else {

            // ====================================
            // AGENDADO Y ASISTIO
            // NO GUARDAN MOTIVO
            // ====================================
            turno.setMotivo(null);
        }
    }

    // ===============================
    // ESTADÍSTICAS AUSENCIAS
    // ===============================
    public List<Map<String, Object>>
    obtenerAusenciasPorPaciente() {

        List<Object[]> resultados =
                turnoRepository
                        .contarAusenciasPorPaciente();

        return resultados.stream().map(r -> {

            Map<String, Object> map =
                    new HashMap<>();

            map.put(
                    "nombre",
                    r[1] + " " + r[2]
            );

            map.put(
                    "ausencias",
                    r[3]
            );

            return map;

        }).toList();
    }
    
    @Autowired
private HistoriaClinicaRepository historiaRepo;

public boolean existeHistoriaClinicaPorPacienteId(Long pacienteId) {
    return historiaRepo.existsByPacienteId(pacienteId);
}
}