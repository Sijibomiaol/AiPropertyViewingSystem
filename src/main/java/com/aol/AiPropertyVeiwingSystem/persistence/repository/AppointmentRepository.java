package com.aol.AiPropertyVeiwingSystem.persistence.repository;

import com.aol.AiPropertyVeiwingSystem.enums.AppointmentStatus;
import com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface AppointmentRepository extends JpaRepository<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment, Long> {
    Page<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findByPropertyId(Long propertyId, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.property.id = :propertyId AND a.appointmentTime BETWEEN :startDate AND :endDate")
    Page<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findByPropertyAndDateRange(
            @Param("propertyId") Long propertyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    Page<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findByTenantId(Long tenantId, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.tenant.id = :tenantId AND a.appointmentTime BETWEEN :startDate AND :endDate")
    Page<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findByTenantAndDateRange(
            @Param("tenantId") Long tenantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    List<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findByTenantIdAndStatus(Long tenantId, AppointmentStatus status);

    Page<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findByPropertyLandlordId(Long landlordId, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.property.landlord.id = :landlordId AND a.appointmentTime BETWEEN :startDate AND :endDate")
    Page<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findByLandlordAndDateRange(
            @Param("landlordId") Long landlordId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    List<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findByLandlordIdAndStatus(Long landlordId, AppointmentStatus status);

    Page<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findByStatus(AppointmentStatus status, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.status IN :statuses")
    Page<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findByStatusIn(@Param("statuses") List<AppointmentStatus> statuses, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentTime BETWEEN :startDate AND :endDate")
    Page<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("""
        SELECT a FROM Appointment a 
        WHERE a.appointmentTime BETWEEN :startTime AND :endTime 
        AND a.status = :status
        """)
    List<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findUpcomingAppointments(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("status") AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.property.id = :propertyId " +
            "AND a.appointmentTime BETWEEN :startTime AND :endTime " +
            "AND a.status != 'CANCELLED'")
    List<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findOverlappingAppointments(
            @Param("propertyId") Long propertyId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("""
        SELECT COUNT(a) > 0 FROM Appointment a 
        WHERE a.property.id = :propertyId 
        AND a.appointmentTime BETWEEN :startTime AND :endTime
        AND a.status NOT IN (:cancelledStatus, :completedStatus)
        """)
    boolean existsOverlappingAppointment(
            @Param("propertyId") Long propertyId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus,
            @Param("completedStatus") AppointmentStatus completedStatus
    );

    @Query("""
        SELECT a FROM Appointment a 
        WHERE a.property.id = :propertyId 
        AND a.status = :status
        AND a.appointmentTime BETWEEN :startDate AND :endDate
        """)
    Page<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findByPropertyStatusAndDateRange(
            @Param("propertyId") Long propertyId,
            @Param("status") AppointmentStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("""
        SELECT a FROM Appointment a 
        WHERE a.tenant.id = :tenantId 
        AND a.status = :status
        AND a.appointmentTime BETWEEN :startDate AND :endDate
        """)
    Page<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findByTenantStatusAndDateRange(
            @Param("tenantId") Long tenantId,
            @Param("status") AppointmentStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.aiConversationId = :conversationId")
    com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment findByAiConversationId(@Param("conversationId") String conversationId);

    @Query("SELECT a FROM Appointment a WHERE a.aiDataValidated = false AND a.aiExtractedDetails IS NOT NULL")
    List<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findUnvalidatedAiAppointments();

    @Query("""
        SELECT a FROM Appointment a 
        WHERE a.appointmentTime BETWEEN :startTime AND :endTime 
        AND a.status = :status
        AND (a.tenantReminderSent = false OR a.landlordReminderSent = false)
        """)
    List<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findAppointmentsNeedingReminders(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("status") AppointmentStatus status);

    @Query("""
        SELECT a FROM Appointment a 
        WHERE a.appointmentTime BETWEEN :startTime AND :endTime 
        AND a.status = :status
        AND a.tenantReminderSent = false
        """)
    List<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findAppointmentsNeedingTenantReminders(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("status") AppointmentStatus status);

    @Query("""
        SELECT a FROM Appointment a 
        WHERE a.appointmentTime BETWEEN :startTime AND :endTime 
        AND a.status = :status
        AND a.landlordReminderSent = false
        """)
    List<com.aol.AiPropertyVeiwingSystem.persistence.entity.Appointment> findAppointmentsNeedingLandlordReminders(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("status") AppointmentStatus status);
} 