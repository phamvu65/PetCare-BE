package vn.vuxnye.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vn.vuxnye.model.PetEntity;

@Repository
public interface PetRepository {
    Page<PetEntity> searchByKeyWord(String keyword, Pageable pageable);
}
