package com.campus.campusbookingsystem.service;

import com.campus.campusbookingsystem.entity.AuditLog;
import com.campus.campusbookingsystem.repository.AuditLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void record(String action, String entityType, Long entityId,
                       String description, String performedBy) {

        String actor = (performedBy == null || performedBy.isBlank())
                ? "System" : performedBy;

        auditLogRepository.save(
                new AuditLog(action, entityType, entityId, description, actor));
    }

    public List<AuditLog> getAll() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<AuditLog> getRecent(int limit) {
        return auditLogRepository.findByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }
}
