
package com.mycompany.consultas.psicologicas.service;

import com.mycompany.consultas.psicologicas.exception.RecursoNoEncontradoException;
import com.mycompany.consultas.psicologicas.model.EstadoTurno;
import com.mycompany.consultas.psicologicas.model.Paciente;
import com.mycompany.consultas.psicologicas.repository.HistoriaClinicaRepository;
import com.mycompany.consultas.psicologicas.repository.PacienteRepository;
import com.mycompany.consultas.psicologicas.repository.TurnoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private HistoriaClinicaRepository historiaRepository;

    // =========================
    // LISTAR PACIENTES
    // =========================

    public List<Paciente> listar() {
        return pacienteRepository.findAll();
    }

    // =========================
    // GUARDAR / ACTUALIZAR
    // =========================

    public Paciente guardar(Paciente paciente) {

        // Validar DNI si fue informado
        if (paciente.getDni() != null &&
            !paciente.getDni().trim().isEmpty()) {

            if (!paciente.getDni().matches("\\d+")) {
                throw new RuntimeException(
                    "El DNI debe contener solamente números"
                );
            }
        }

        // Validar teléfono si fue informado
        if (paciente.getTelefono() != null &&
            !paciente.getTelefono().trim().isEmpty()) {

            if (!paciente.getTelefono().matches("\\d+")) {
                throw new RuntimeException(
                    "El teléfono debe contener solamente números"
                );
            }
        }

        // La edad ya no se guarda.
        // Se calcula mediante Paciente.getEdad().

        // fechaInicioTratamiento se persiste
        // automáticamente como atributo JPA.

        return pacienteRepository.save(paciente);
    }

    // =========================
    // OBTENER POR ID
    // =========================

    public Paciente obtenerPorId(Long id) {

        return pacienteRepository.findById(id)
            .orElseThrow(() ->
                new RecursoNoEncontradoException(
                    "Paciente no encontrado"
                )
            );
    }

    // =========================
    // ELIMINAR PACIENTE
    // =========================

    public void eliminar(Long id) {

        if (historiaRepository
                .contarHistoriasPorPaciente(id) > 0) {

            throw new RuntimeException(
                "No se puede eliminar: el paciente tiene historia clínica"
            );
        }

        if (turnoRepository
                .countByPacienteIdAndEstado(
                    id, EstadoTurno.ASISTIO) > 0) {

            throw new RuntimeException(
                "No se puede eliminar: el paciente tiene turnos asistidos"
            );
        }

        pacienteRepository.deleteById(id);
    }
}
