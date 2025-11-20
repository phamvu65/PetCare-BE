package vn.vuxnye.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vuxnye.model.PetEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<PetEntity,Long> {

    @Override
    @Query(value = "SELECT p FROM PetEntity p JOIN FETCH p.owner",
            countQuery = "SELECT COUNT(p) FROM PetEntity p")
    Page<PetEntity> findAll(Pageable pageable);

    @Query(value = "SELECT p FROM PetEntity p JOIN FETCH p.owner where (lower(p.name) like :keyword ) " +
            "or (lower(p.breed) like :keyword ) " +
            "or (lower(p.color) like :keyword ) " +
            "or (lower(p.owner.username) like :keyword ) " +
            "or (lower(p.species) like :keyword ) " +
            "or (lower(p.sex) like :keyword ) ")
    Page<PetEntity> searchByKeyWord(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM PetEntity p JOIN FETCH p.owner WHERE p.id = :id")
    Optional<PetEntity> findById(@Param("id") Long id);

    @Query("SELECT p FROM PetEntity p JOIN FETCH p.owner WHERE p.owner.id = :ownerId")
    List<PetEntity> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT p FROM PetEntity p JOIN FETCH p.owner WHERE p.id = :id AND p.owner.id = :ownerId")
    Optional<PetEntity> findByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);

    @Query("SELECT p FROM PetEntity p JOIN FETCH p.owner WHERE p.owner.username = :username")
    List<PetEntity> findByOwnerUsername(@Param("username") String username);
}
