package com.proyecto.galeria.repository;

import com.proyecto.galeria.model.album;
import com.proyecto.galeria.model.reporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReporteRepository extends JpaRepository<reporte, Integer> {

    List<reporte> findByAlbumOrderByFechaHoraDesc(album album);

    boolean existsByAlbumAndAutorIdAndEstadoAndFechaHoraBetween(
            album album, Integer autorId,
            reporte.Estado estado,
            LocalDateTime start, LocalDateTime end);
}
