package com.multicore.crm.service;

import com.multicore.crm.entity.Notification;
import com.multicore.crm.entity.User;
import com.multicore.crm.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void notifyUser(User user, String title, String message) {
        if (user == null) {
            return;
        }
        Notification n = new Notification();
        n.setUser(user);
        n.setTitle(title);
        n.setMessage(message);
        notificationRepository.save(n);
        log.debug("Notification queued for {}: {}", user.getEmail(), title);
    }
}

