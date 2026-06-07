package com.mycompany.consultas.psicologicas.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class BackupService {

    // AJUSTAR ESTO A TU MYSQL
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "";
    private final String DB_NAME = "gestion_psicologia";

    private final String MYSQL_DUMP_PATH = "C:\\xampp\\mysql\\bin\\mysqldump.exe"; // o ruta completa si no está en PATH
    private final String MYSQL_PATH =
        "C:\\xampp\\mysql\\bin\\mysql.exe";

    // =========================
    // GENERAR BACKUP
    // =========================
    public File generarBackupFile() throws Exception {

    System.out.println("=== INICIO BACKUP ===");

    String timestamp =
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    File backupFile = new File("backup_" + timestamp + ".sql");

    System.out.println("ARCHIVO BACKUP:");
    System.out.println(backupFile.getAbsolutePath());

    System.out.println("MYSQLDUMP:");
    System.out.println(MYSQL_DUMP_PATH);

    ProcessBuilder pb = new ProcessBuilder(
            MYSQL_DUMP_PATH,
            "-u" + DB_USER,
            DB_NAME
    );

    pb.redirectOutput(backupFile);

    System.out.println("ANTES DE pb.start()");

    Process process = pb.start();

    System.out.println("DESPUÉS DE pb.start()");

    int exitCode = process.waitFor();

    System.out.println("EXIT CODE = " + exitCode);

    if (exitCode != 0) {
        throw new RuntimeException("Error generando backup MySQL");
    }

    System.out.println("=== FIN BACKUP ===");

    return backupFile;
}
    // =========================
    // RESTAURAR BACKUP
    // =========================
    public void restaurarBackup(MultipartFile file) throws Exception {

    System.out.println("=== INICIO RESTAURACION ===");

    File tempFile = File.createTempFile("restore_", ".sql");

    file.transferTo(tempFile);

    System.out.println("Archivo temporal:");
    System.out.println(tempFile.getAbsolutePath());

    ProcessBuilder pb;

    if (DB_PASSWORD == null || DB_PASSWORD.isBlank()) {

        pb = new ProcessBuilder(
                MYSQL_PATH,
                "-u",
                DB_USER,
                DB_NAME
        );

    } else {

        pb = new ProcessBuilder(
                MYSQL_PATH,
                "-u",
                DB_USER,
                "-p" + DB_PASSWORD,
                DB_NAME
        );
    }

    pb.redirectInput(tempFile);

    Process process = pb.start();

    BufferedReader errorReader =
            new BufferedReader(
                    new InputStreamReader(
                            process.getErrorStream()));

    String line;

    while ((line = errorReader.readLine()) != null) {
        System.out.println("MYSQL ERROR: " + line);
    }

    int exitCode = process.waitFor();

    System.out.println("EXIT CODE = " + exitCode);

    if (exitCode != 0) {
        throw new RuntimeException(
                "Error restaurando backup MySQL");
    }

    System.out.println("=== FIN RESTAURACION ===");
}

}