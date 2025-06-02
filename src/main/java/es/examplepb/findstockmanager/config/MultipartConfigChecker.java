package es.examplepb.findstockmanager.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component // ¡Importante para que Spring la detecte como un componente!
@Slf4j // Para usar log.info, etc.
public class MultipartConfigChecker {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    @PostConstruct
    public void init() {
        log.info("--- CONFIGURACIÓN DE SUBIDA DE ARCHIVOS ---");
        log.info("spring.servlet.multipart.max-file-size: {}", maxFileSize);
        log.info("spring.servlet.multipart.max-request-size: {}", maxRequestSize);
        log.info("------------------------------------------");
    }
}