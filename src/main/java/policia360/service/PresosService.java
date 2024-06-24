package policia360.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import policia360.domain.Presos;
import policia360.repository.PresosRepository;

@Service
@Transactional
public class PresosService {

    private PresosRepository presosRepository;
    private final Logger log = LoggerFactory.getLogger(PresosService.class);

    public PresosService(PresosRepository presosRepository) {
        this.presosRepository = presosRepository;
    }

    public List<Object[]> getPrisoners() {
        log.debug("Request to get presos per celda");
        return presosRepository.getPrisoners();
    }
}
