package learning.basics.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

@Service
public class TimerService {

    private final ScheduledExecutorService scheduler;
    private final UserService userService;

    @Autowired
    public TimerService(@Lazy UserService userService) {
        this(Executors.newSingleThreadScheduledExecutor(), userService);
    }

    TimerService(ScheduledExecutorService scheduler, UserService userService) {
        this.scheduler = scheduler;
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
