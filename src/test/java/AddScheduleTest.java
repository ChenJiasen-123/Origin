import com.origin.dto.ScheduleDTO;
import com.origin.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(classes = com.origin.Main.class)
public class AddScheduleTest {

    @Autowired
    private ScheduleService scheduleService;

    @Test
    public void addSchedule() {
        List<ScheduleDTO> lists = List.of(
                ScheduleDTO.builder().name("Java").location("三教502").start_time(LocalDateTime.parse("2026-02-14T16:00")).end_time(LocalDateTime.parse("2026-02-14T17:30")).color("#555").build()
        );
        lists.forEach(scheduleService::addNewSchedule);
    }
}
