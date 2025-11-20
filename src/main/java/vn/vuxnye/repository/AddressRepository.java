package vn.vuxnye.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vuxnye.model.AddressEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity,Long> {

    List<AddressEntity> findByUserId(Long userId);

    Optional<AddressEntity> findByUserIdAndIsDefaultTrue(Long userId);

    Optional<AddressEntity> findByIdAndUserId(Long id, Long userId);

    List<AddressEntity> findByUserIdAndIdNot(Long userId, Long addressId);

    /**
     * (Dùng cho logic 'isDefault')
     * Hủy (set false) tất cả địa chỉ mặc định cũ của một User
     */
    @Modifying
    @Query("UPDATE AddressEntity a SET a.isDefault = false WHERE a.user.id = :userId AND a.isDefault = true")
    void clearOldDefaults(Long userId);

}
