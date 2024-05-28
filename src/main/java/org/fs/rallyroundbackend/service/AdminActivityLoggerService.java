package org.fs.rallyroundbackend.service;

import java.util.UUID;

public interface AdminActivityLoggerService {
    UUID saveLog(String actionType, String actionDescription, String afectedResourceId, String afectedResourceIdType,
                 String adminEmail);
}
