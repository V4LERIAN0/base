package com.proyecto.galeria.service;

import com.proyecto.galeria.model.album;
import com.proyecto.galeria.model.reporte;
import com.proyecto.galeria.model.usuario;
import com.proyecto.galeria.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class ReporteService {

    @Autowired private ReporteRepository repo;
    @Autowired private albumService albumService;
    @Autowired private IUsuarioService usuarioService;

    public reporte saveDraft(Integer albumId,
                             Integer userId,
                             String contenido) {

        album a  = albumService.get(albumId).orElseThrow();
        usuario u = usuarioService.findById(userId).orElseThrow();

        reporte r = new reporte();
        r.setAlbum(a);
        r.setAutor(u);
        r.setContenido(contenido);
        r.setFechaHora(LocalDateTime.now());
        r.setEstado(reporte.Estado.DRAFT);

        return repo.save(r);
    }

    public reporte send(Integer albumId,
                        Integer userId,
                        String contenido) {

        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end   = start.plusDays(1);

        album a  = albumService.get(albumId).orElseThrow();
        usuario u = usuarioService.findById(userId).orElseThrow();

        // one final report / album / day
        if (repo.existsByAlbumAndAutorIdAndEstadoAndFechaHoraBetween(
                a, userId, reporte.Estado.SENT, start, end)) {
            throw new IllegalStateException("Ya se envió un reporte hoy.");
        }

        reporte r = new reporte();
        r.setAlbum(a);
        r.setAutor(u);
        r.setContenido(contenido);
        r.setFechaHora(LocalDateTime.now());
        r.setEstado(reporte.Estado.SENT);

        return repo.save(r);
    }

    public List<reporte> logs(Integer albumId) {
        return repo.findByAlbumOrderByFechaHoraDesc(
                albumService.get(albumId).orElseThrow());
    }

    public reporte update(Integer reportId, String contenido) {
        reporte r = repo.findById(reportId).orElseThrow();
        r.setContenido(contenido);
        return repo.save(r);
    }
}
