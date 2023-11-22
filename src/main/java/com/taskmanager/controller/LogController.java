package com.taskmanager.controller;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/logs")
public class LogController {
    private static final Logger logger = LoggerFactory.getLogger(LogController.class);
    private static final String LOGS_FOLDER = "logs";  // Ajusta la carpeta según tu configuración

    @GetMapping("/list")
    public List<String> getLogFilesList() {
        File logsFolder = new File(LOGS_FOLDER);
        return Arrays.stream(logsFolder.listFiles())
                .filter(file -> file.isFile() && file.getName().endsWith(".log"))
                .map(File::getName)
                .collect(Collectors.toList());
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewLogFile(@PathVariable String fileName) {
        File logFile = new File(LOGS_FOLDER, fileName);
        if (logFile.exists() && logFile.isFile()) {
            Resource resource = new FileSystemResource(logFile);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + logFile.getName())
                    .body(resource);
        } else {
            logger.error("El archivo de log {} no existe.", fileName);
            return ResponseEntity.notFound().build();
        }
    }
}
