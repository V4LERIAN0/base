package com.proyecto.galeria.controller;

import com.proyecto.galeria.model.usuario;
import com.proyecto.galeria.service.ReporteService;
import com.proyecto.galeria.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/albumes/{albumId}/reports")
public class ReporteController {

    @Autowired private ReporteService servicio;
    @Autowired private IUsuarioService usuarioService;

    private usuario usuarioSesion(HttpSession s) {
        Integer id = Integer.parseInt(s.getAttribute("idusuario").toString());
        return usuarioService.findById(id).orElseThrow();
    }

    /* ----------- Supervisor – redacción ---------------- */

    @PostMapping("/draft")
    @ResponseBody
    public String draft(@PathVariable Integer albumId,
                        @RequestParam String contenido,
                        HttpSession sesion) {

        usuario u = usuarioSesion(sesion);
        if (!"SUPERVISOR".equals(u.getTipo_usuario()))
            return "FORBIDDEN";

        servicio.saveDraft(albumId, u.getId(), contenido);
        return "OK";
    }

    @PostMapping("/send")
    @ResponseBody
    public String send(@PathVariable Integer albumId,
                       @RequestParam String contenido,
                       HttpSession sesion) {

        usuario u = usuarioSesion(sesion);
        if (!"SUPERVISOR".equals(u.getTipo_usuario()))
            return "FORBIDDEN";

        try {
            servicio.send(albumId, u.getId(), contenido);
            return "OK";
        } catch (IllegalStateException ex) {
            return "ALREADY_SENT";
        }
    }

    /* ------------- Logs (both roles) ------------------- */

    @GetMapping("")
    public String logs(@PathVariable Integer albumId,
                       Model model,
                       HttpSession sesion) {

        usuario u = usuarioSesion(sesion);
        boolean editable = "ADMIN".equals(u.getTipo_usuario());

        model.addAttribute("editable", editable);
        model.addAttribute("reportes", servicio.logs(albumId));
        return "albumes/report_logs :: body";
    }

    /* ------------- Admin edit -------------------------- */

    @PostMapping("/{id}/edit")
    @ResponseBody
    public String edit(@PathVariable Integer albumId,
                       @PathVariable Integer id,
                       @RequestParam String contenido,
                       HttpSession sesion) {

        usuario u = usuarioSesion(sesion);
        if (!"ADMIN".equals(u.getTipo_usuario()))
            return "FORBIDDEN";

        servicio.update(id, contenido);
        return "OK";
    }
}
