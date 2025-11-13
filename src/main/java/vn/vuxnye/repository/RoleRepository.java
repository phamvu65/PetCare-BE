package vn.vuxnye.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vuxnye.model.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

}
