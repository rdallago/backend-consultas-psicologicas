package com.mycompany.consultas.psicologicas.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mycompany.consultas.psicologicas.service.BackupService;

import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/api/backups")
@CrossOrigin(
    origins = "http://localhost:4200",
    exposedHeaders = "Content-Disposition"
)
public class BackupController {

    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    // =========================
    // GENERAR BACKUP (DESCARGA SQL)
    // =========================
    @GetMapping("/generar")
    public ResponseEntity<InputStreamResource> generarBackup() throws Exception {

        File backupFile = backupService.generarBackupFile();

        InputStreamResource resource =
                new InputStreamResource(new FileInputStream(backupFile));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + backupFile.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(backupFile.length())
                .body(resource);
    }

    // =========================
    // RESTAURAR BACKUP
    // =========================
    @PostMapping("/restaurar")
public ResponseEntity<String> restaurarBackup(
        @RequestParam("file") MultipartFile file
) throws Exception {

    System.out.println("******** ENTRO AL CONTROLLER RESTAURAR ********");

    backupService.restaurarBackup(file);

    return ResponseEntity.ok(
            "Backup restaurado correctamente");
}
}