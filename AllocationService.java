import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Observer Pattern - Rathi
public class AllocationService {
    private static AllocationService instance;
    private final List<MatchObserver> observers = new ArrayList<>();
    private final List<String> notificationsLog = new ArrayList<>();

    private AllocationService() {}

    public static synchronized AllocationService getInstance() {
        if (instance == null) {
            instance = new AllocationService();
        }
        return instance;
    }

    public void addObserver(MatchObserver observer) {
        observers.add(observer);
    }

    public void clearObservers() {
        observers.clear();
    }

    public void removeObserver(MatchObserver observer) {
        observers.remove(observer);
    }

    public void confirmMatch(MatchResult match) {
        if (match == null || !match.hasMatch()) {
            return;
        }
        for (MatchObserver observer : observers) {
            observer.onMatchConfirmed(match);
        }
    }

    public void logNotification(String message) {
        notificationsLog.add(message);
    }

    public void logNotification(String observerType, MatchResult match, String message) {
        notificationsLog.add(message);
        NationalTransplantRegistry.getInstance().recordNotification(match, observerType, message);
    }

    public List<String> getNotificationsLog() {
        return Collections.unmodifiableList(notificationsLog);
    }

    public void replaceNotificationsLog(List<String> messages) {
        notificationsLog.clear();
        notificationsLog.addAll(messages);
    }

    public void clearNotifications() {
        notificationsLog.clear();
    }
}
