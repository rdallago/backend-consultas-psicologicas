package com.mycompany.consultas.psicologicas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mycompany.consultas.psicologicas.model.HistoriaClinica;
import com.mycompany.consultas.psicologicas.repository.HistoriaClinicaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class HistoriaClinicaService {

    @Autowired
    private HistoriaClinicaRepository repository;

    // Renombramos o creamos este para que el controller lo encuentre
    @Transactional
    public HistoriaClinica crear(HistoriaClinica hc) {
        Optional<HistoriaClinica> existente = repository.findByPacienteId(hc.getPaciente().getId());
        if (existente.isPresent()) {
            throw new RuntimeException("El paciente ya posee una historia clínica.");
        }

        hc.setFechaCreacion(LocalDate.now());
        if (hc.getVisitas() != null) {
            hc.getVisitas().forEach(v -> v.setHistoria(hc));
        }
        return repository.save(hc);
    }

    public List<HistoriaClinica> listar() {
        return repository.findAll();
    }

    public Optional<HistoriaClinica> buscarPorPaciente(Long pacienteId) {
        return repository.findByPacienteId(pacienteId);
    }


public HistoriaClinica actualizarFecha(Long id, String fechaStr) {

    HistoriaClinica hc = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Historia no encontrada"));

    LocalDate nuevaFecha = LocalDate.parse(fechaStr);

    // 🔥 VALIDACIÓN CLAVE
    if (hc.getVisitas() != null && !hc.getVisitas().isEmpty()) {

        LocalDate fechaMaxVisita = hc.getVisitas().stream()
            .map(v -> v.getFecha()) // 👈 asegurate que sea LocalDate
            .max(LocalDate::compareTo)
            .orElseThrow();

        if (nuevaFecha.isAfter(fechaMaxVisita)) {
            throw new RuntimeException(
                "La fecha de la historia clínica no puede ser posterior a las visitas registradas"
            );
        }
    }

    hc.setFechaCreacion(nuevaFecha);

    return repository.save(hc);
}

@Transactional
public void eliminar(Long id) {

    HistoriaClinica hc = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("No existe"));

    if (hc.getVisitas() != null && !hc.getVisitas().isEmpty()) {

        throw new RuntimeException(
            "No se puede eliminar: tiene visitas asociadas"
        );
    }

    repository.eliminarHistoria(id);

    System.out.println("🔥 HISTORIA ELIMINADA REALMENTE");
}

}