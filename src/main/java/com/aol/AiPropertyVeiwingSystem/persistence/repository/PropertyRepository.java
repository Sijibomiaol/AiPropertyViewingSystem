package com.aol.AiPropertyVeiwingSystem.persistence.repository;

import com.aol.AiPropertyVeiwingSystem.enums.PropertyStatus;
import com.aol.AiPropertyVeiwingSystem.persistence.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PropertyRepository extends JpaRepository<com.aol.AiPropertyVeiwingSystem.persistence.entity.Property, Long>, JpaSpecificationExecutor<Property> {
    @Query("SELECT p FROM Property p WHERE p.status = :status AND p.landlord.id = :landlordId")
    Page<com.aol.AiPropertyVeiwingSystem.persistence.entity.Property> findByStatusAndLandlordId(PropertyStatus status, Long landlordId, Pageable pageable);

    @Query("SELECT p FROM Property p LEFT JOIN FETCH p.appointments WHERE p.id = :id")
    Optional<com.aol.AiPropertyVeiwingSystem.persistence.entity.Property> findByIdWithAppointments(Long id);

    @Query("""
        SELECT p FROM Property p 
        WHERE p.status = 'AVAILABLE' 
        AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) 
        OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        """)
    Page<com.aol.AiPropertyVeiwingSystem.persistence.entity.Property> searchAvailableProperties(String searchTerm, Pageable pageable);
} 