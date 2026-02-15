package com.origin.service;

import com.origin.dto.ScheduleDTO;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ScheduleService {

    ScheduleDTO getScheduleById(Integer id);

    void addNewSchedule(ScheduleDTO scheduleDTO);

    void deleteScheduleById(Integer id);

    void updateScheduleById(Integer id, ScheduleDTO scheduleDTO);

    List<ScheduleDTO> getAllSchedules();
}
