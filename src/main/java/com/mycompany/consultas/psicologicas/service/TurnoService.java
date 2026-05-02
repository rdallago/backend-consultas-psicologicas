package com.mycompany.consultas.psicologicas.service;

import com.mycompany.consultas.psicologicas.exception.RecursoNoEncontradoException;
import com.mycompany.consultas.psicologicas.model.EstadoTurno;
import com.mycompany.consultas.psicologicas.model.Turno;
import com.mycompany.consultas.psicologicas.repository.TurnoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

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
                .orElseThrow(() -> new RecursoNoEncontradoException("Turno no encontrado"));
    }

    // ===============================
    // CREAR TURNO
    // ===============================
    public Turno guardar(Turno turno) {

        validarSeparacion40Minutos(turno.getFechaHora(), null);

        if (turno.getEstado() == null) {
            turno.setEstado(EstadoTurno.AGENDADO);
        }

        return turnoRepository.save(turno);
    }

    // ===============================
    // MODIFICAR TURNO
    // ===============================
    public Turno actualizar(Long id, Turno turnoActualizado) {

        Turno turnoExistente = obtenerPorId(id);

        validarSeparacion40Minutos(turnoActualizado.getFechaHora(), id);

        turnoExistente.setPaciente(turnoActualizado.getPaciente());
        turnoExistente.setFechaHora(turnoActualizado.getFechaHora());
        turnoExistente.setEstado(turnoActualizado.getEstado());

        return turnoRepository.save(turnoExistente);
    }

    // ===============================
    // CAMBIAR ESTADO
    // ===============================
    public Turno cambiarEstado(Long id, EstadoTurno estado) {

        Turno turno = obtenerPorId(id);

        turno.setEstado(estado);

        return turnoRepository.save(turno);
    }

    // ===============================
    // BUSCAR ENTRE FECHAS
    // ===============================
    public List<Turno> buscarEntreFechas(LocalDateTime desde,
                                         LocalDateTime hasta) {

        return turnoRepository.findByFechaHoraBetween(desde, hasta);
    }

    // ===============================
    // ELIMINAR
    // ===============================
    public void eliminar(Long id) {
        turnoRepository.deleteById(id);
    }

    // ===============================
    // VALIDAR 40 MINUTOS ENTRE TURNOS
    // ===============================
    private void validarSeparacion40Minutos(LocalDateTime nuevaFecha,
                                            Long idActual) {

        List<Turno> turnos = turnoRepository.findAll();

        for (Turno t : turnos) {

            if (idActual != null && t.getId().equals(idActual)) {
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
}