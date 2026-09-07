package learning.basics.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import learning.basics.model.User;

@ExtendWith(MockitoExtension.class)
public class TimerServiceTest {
    @Mock
    UserService userService;

    @Mock
    ScheduledExecutorService scheduler;

    User testUser = new User();
    
    @BeforeEach
    void setUp() {
        testUser.setUsername("max");
        testUser.setEmail("max@example.com");
    }

    @Test
    @DisplayName("Sollte den User nach Ablauf der konfigurierten Tage löschen")
    void testScheduleAccountDeletion() {
        TimerService timerService = new TimerService(scheduler, userService);
        Map<String, User> users = new HashMap<>();
        users.put(testUser.getUsername(), testUser);
        doAnswer(invocation -> {
            users.remove(invocation.getArgument(0));
            return null;
        }).when(userService).deleteUserByUsername(testUser.getUsername());

        ArgumentCaptor<Runnable> deletionTask = ArgumentCaptor.forClass(Runnable.class);
        when(scheduler.schedule(deletionTask.capture(), eq(365L * 2), eq(TimeUnit.DAYS)))
                .thenReturn(null);

        timerService.scheduleAccountDeletion(testUser.getUsername(), 365 * 2);

        verify(scheduler).schedule(any(Runnable.class), eq(365L * 2), eq(TimeUnit.DAYS));
        assertTrue(users.containsKey(testUser.getUsername()));

        deletionTask.getValue().run();

        assertFalse(users.containsKey(testUser.getUsername()));
        verify(userService).deleteUserByUsername(testUser.getUsername());
    }
}
