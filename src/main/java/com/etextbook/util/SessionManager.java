// util/SessionManager.java
package com.etextbook.util;
import java.util.Optional;
import com.etextbook.model.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class SessionManager {
    private static final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000; // 30 minutes

    public static String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, new SessionInfo(user));
        return sessionId;
    }

    public static Optional<User> getUser(String sessionId) {
        SessionInfo session = sessions.get(sessionId);
        if (session != null && !session.isExpired()) {
            session.updateLastAccessed();
            return Optional.of(session.getUser());
        }
        sessions.remove(sessionId);
        return Optional.empty();
    }

    public static void invalidateSession(String sessionId) {
        sessions.remove(sessionId);
    }

    private static class SessionInfo {
        private final User user;
        private long lastAccessed;

        public SessionInfo(User user) {
            this.user = user;
            this.lastAccessed = System.currentTimeMillis();
        }

        public User getUser() {
            return user;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - lastAccessed > SESSION_TIMEOUT;
        }

        public void updateLastAccessed() {
            this.lastAccessed = System.currentTimeMillis();
        }
    }
}