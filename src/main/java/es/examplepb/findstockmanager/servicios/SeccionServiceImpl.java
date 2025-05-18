package es.examplepb.findstockmanager.servicios;

import es.examplepb.findstockmanager.entidades.SeccionEntity;
import es.examplepb.findstockmanager.repositorios.SeccionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeccionServiceImpl implements SeccionService {

    private final SeccionRepository seccionRepository;

    @Override
    public List<SeccionEntity> findAll() {
        log.info("Buscando todas las secciones");
        return seccionRepository.findAll();
    }

}

