package learning.basics.service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

@Service
public class TimerService {

    private final ScheduledExecutorService scheduler = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
    private final UserService userService;

    public TimerService(@Lazy UserService userService) {
        this.userService = userService;
    }

    public void scheduleAccountDeletion(String username, long delayInDays) {
        scheduler.schedule(() -> userService.deleteUserByUsername(username),
                delayInDays, TimeUnit.DAYS);
    }

    @PreDestroy
    void shutdown() {
        scheduler.shutdownNow();
    }
}
