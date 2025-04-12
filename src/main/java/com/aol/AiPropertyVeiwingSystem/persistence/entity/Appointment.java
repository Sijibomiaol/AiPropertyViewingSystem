package com.aol.AiPropertyVeiwingSystem.persistence.entity;

import com.aol.AiPropertyVeiwingSystem.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private User landlord;

    @Column(nullable = false)
    private LocalDateTime appointmentTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private boolean reminderSent;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private Boolean isConfirmed;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column
    private Integer rating;

    @Column
    private String cancellationReason;

    @Column
    private LocalDateTime cancellationTime;

    @Column
    private String rescheduleReason;

    @Column
    private LocalDateTime rescheduleTime;

    @Column(nullable = false)
    private Integer numberOfReschedules;

    @Column(nullable = false)
    private Boolean requiresConfirmation;

    @Column
    private String confirmationCode;

    @Column
    private LocalDateTime confirmationTime;

    @Column
    private String meetingLink;

    @Column(columnDefinition = "TEXT")
    private String locationDetails;

    @Column(columnDefinition = "TEXT")
    private String specialInstructions;

    @Column
    private String contactPhone;

    @Column
    private String contactEmail;

    @Column
    private String emergencyContact;

    @Column
    private String emergencyPhone;

    @Column(columnDefinition = "TEXT")
    private String aiConversationId;

    @Column(columnDefinition = "TEXT")
    private String aiPromptContext;

    @Column(columnDefinition = "TEXT")
    private String aiExtractedDetails;

    @Column
    private Boolean aiDataValidated;

    @Column(columnDefinition = "TEXT")
    private String aiValidationErrors;

    @Column
    private LocalDateTime lastReminderSent;

    @Column
    private Integer reminderAttempts;

    @Column
    private Boolean tenantReminderSent;

    @Column
    private Boolean landlordReminderSent;

    @Column
    private LocalDateTime tenantReminderTime;

    @Column
    private LocalDateTime landlordReminderTime;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        if (numberOfReschedules == null) {
            numberOfReschedules = 0;
        }
        if (requiresConfirmation == null) {
            requiresConfirmation = false;
        }
        if (isConfirmed == null) {
            isConfirmed = false;
        }
        if (duration == null) {
            duration = 60;
        }
        if (reminderAttempts == null) {
            reminderAttempts = 0;
        }
        if (tenantReminderSent == null) {
            tenantReminderSent = false;
        }
        if (landlordReminderSent == null) {
            landlordReminderSent = false;
        }
        if (aiDataValidated == null) {
            aiDataValidated = false;
        }
    }

    public void setTenant(User tenant) {
        this.tenant = tenant;
        if (tenant != null) {
            tenant.getTenantAppointments().add(this);
        }
    }

    public void setLandlord(User landlord) {
        this.landlord = landlord;
        if (landlord != null) {
            landlord.getLandlordAppointments().add(this);
        }
    }

    public void setProperty(Property property) {
        this.property = property;
        if (property != null) {
            property.getAppointments().add(this);
        }
    }

    public boolean isUpcoming() {
        return appointmentTime.isAfter(LocalDateTime.now()) &&
                status != AppointmentStatus.CANCELLED &&
                status != AppointmentStatus.COMPLETED;
    }

    public boolean isPast() {
        return appointmentTime.isBefore(LocalDateTime.now()) ||
                status == AppointmentStatus.COMPLETED;
    }

    public boolean isCancellable() {
        return isUpcoming() &&
                status != AppointmentStatus.CANCELLED;
    }

    public boolean isReschedulable() {
        return isUpcoming() &&
                status != AppointmentStatus.CANCELLED &&
                numberOfReschedules < 3; // Maximum 3 reschedules allowed
    }

    public boolean needsAiValidation() {
        return !aiDataValidated && aiExtractedDetails != null;
    }

    public boolean isReadyForConfirmation() {
        return aiDataValidated && !isConfirmed && status == AppointmentStatus.SCHEDULED;
    }

    public boolean needsReminder() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourBefore = appointmentTime.minusHours(1);
        return isUpcoming() &&
                now.isAfter(oneHourBefore) &&
                !tenantReminderSent &&
                !landlordReminderSent;
    }

    public void markReminderSent(boolean isTenant) {
        if (isTenant) {
            tenantReminderSent = true;
            tenantReminderTime = LocalDateTime.now();
        } else {
            landlordReminderSent = true;
            landlordReminderTime = LocalDateTime.now();
        }
        reminderAttempts++;
        lastReminderSent = LocalDateTime.now();
    }
} 