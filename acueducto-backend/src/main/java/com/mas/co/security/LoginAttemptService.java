package com.mas.co.security;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;
    private static final int MAX_IP_ATTEMPTS = 20;

    private final ConcurrentHashMap<String, AttemptInfo> userAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AttemptInfo> ipAttempts = new ConcurrentHashMap<>();

    public void loginFailed(String username, String ip) {
        AttemptInfo userInfo = recordAttempt(userAttempts, username, MAX_ATTEMPTS);
        AttemptInfo ipInfo = recordAttempt(ipAttempts, ip, MAX_IP_ATTEMPTS);
        log.warn("Login fallido para '{}' desde IP '{}'. Intentos usuario: {}/{}, IP: {}/{}",
                username, ip, userInfo.getCount(), MAX_ATTEMPTS, ipInfo.getCount(), MAX_IP_ATTEMPTS);
    }

    public void loginFailed(String username) {
        loginFailed(username, "unknown");
    }

    public void loginSucceeded(String username) {
        userAttempts.remove(username);
    }

    public boolean isBlocked(String username) {
        return isKeyBlocked(userAttempts, username, MAX_ATTEMPTS);
    }

    public boolean isIpBlocked(String ip) {
        return isKeyBlocked(ipAttempts, ip, MAX_IP_ATTEMPTS);
    }

    public int getRemainingMinutes(String username) {
        return getMinutesLeft(userAttempts, username);
    }

    private AttemptInfo recordAttempt(ConcurrentHashMap<String, AttemptInfo> map, String key, int maxAttempts) {
        return map.compute(key, (k, existing) -> {
            if (existing == null || existing.isExpired()) {
                return new AttemptInfo(1, LocalDateTime.now().plusMinutes(LOCK_MINUTES));
            }
            existing.increment();
            return existing;
        });
    }

    private boolean isKeyBlocked(ConcurrentHashMap<String, AttemptInfo> map, String key, int maxAttempts) {
        AttemptInfo info = map.get(key);
        if (info == null) {
            return false;
        }
        if (info.isExpired()) {
            map.remove(key);
            return false;
        }
        return info.getCount() >= maxAttempts;
    }

    private int getMinutesLeft(ConcurrentHashMap<String, AttemptInfo> map, String key) {
        AttemptInfo info = map.get(key);
        if (info == null || info.isExpired()) {
            return 0;
        }
        return Math.max(1, (int) java.time.Duration.between(LocalDateTime.now(), info.getLockUntil()).toMinutes());
    }

    private static class AttemptInfo {
        private int count;
        private final LocalDateTime lockUntil;

        AttemptInfo(int count, LocalDateTime lockUntil) {
            this.count = count;
            this.lockUntil = lockUntil;
        }

        void increment() {
            this.count++;
        }

        int getCount() {
            return count;
        }

        LocalDateTime getLockUntil() {
            return lockUntil;
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(lockUntil);
        }
    }
}
