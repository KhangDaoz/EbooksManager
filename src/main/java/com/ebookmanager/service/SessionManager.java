package com.ebookmanager.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
public class SessionManager {
    private final Map<String,Integer> activeSessions = new ConcurrentHashMap<>();
    public String createSession(int userId) {
        String token = UUID.randomUUID().toString();
        activeSessions.put(token, userId);
        return token;
    }
    public Integer getUserIdFromToken(String token) {
        return activeSessions.get(token);
    }

    public void endSession(String token) {
        activeSessions.remove(token);
    }
    public boolean isValidSession(String token) {
        return activeSessions.containsKey(token);
    }

    public void removeUserSessions(int userId) {
        activeSessions.entrySet().removeIf(entry -> entry.getValue().equals(userId));
    }

}
