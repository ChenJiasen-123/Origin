package com.origin.service;

import com.origin.dto.ScheduleDTO;

import java.util.List;

public interface ScheduleService {

    ScheduleDTO getScheduleById(Long id);

    void addNewSchedule(ScheduleDTO scheduleDTO);

    void deleteScheduleById(Long id);

    void updateScheduleById(Long id, ScheduleDTO scheduleDTO);

    List<ScheduleDTO> getAllSchedules();
}
