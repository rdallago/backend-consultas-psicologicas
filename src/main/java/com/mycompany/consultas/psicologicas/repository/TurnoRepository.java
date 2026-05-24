package com.mycompany.consultas.psicologicas.repository;
import java.util.Optional;
import com.mycompany.consultas.psicologicas.model.EstadoTurno;
import com.mycompany.consultas.psicologicas.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    boolean existsByFechaHora(LocalDateTime fechaHora);

    List<Turno> findByFechaHoraBetween(LocalDateTime desde, LocalDateTime hasta);

    long countByPacienteIdAndEstado(Long pacienteId, EstadoTurno estado);
    @Query("""
SELECT t.paciente.id, t.paciente.nombre, t.paciente.apellido, COUNT(t)
FROM Turno t
WHERE t.estado = 'AUSENTE'
GROUP BY t.paciente.id, t.paciente.nombre, t.paciente.apellido
ORDER BY COUNT(t) DESC
""")
List<Object[]> contarAusenciasPorPaciente();

@Query(value = "SELECT p.nombre as nombre, p.apellido as apellido, COUNT(t.id) as total " +
               "FROM pacientes p JOIN turnos t ON p.id = t.paciente_id " +
               "WHERE t.estado = 'AUSENTE' " +
               "GROUP BY p.id ORDER BY total DESC LIMIT 5", nativeQuery = true)
List<Map<String, Object>> obtenerTop5Ausencias();
long countByEstado(EstadoTurno estado);
Optional<Turno> findById(Long id);
}