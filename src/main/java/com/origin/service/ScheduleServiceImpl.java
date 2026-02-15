package com.origin.service;

import com.origin.converter.ScheduleConverter;
import com.origin.dto.ScheduleDTO;
import com.origin.entity.Schedule;
import com.origin.entity.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl  implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public ScheduleDTO getScheduleById(Integer id) {
        Schedule schedule = scheduleRepository.findById(id).orElse(null);
        if(schedule == null) return null;
        return ScheduleConverter.convertToScheduleDTO(schedule);
    }

    @Override
    public void addNewSchedule(ScheduleDTO scheduleDTO) {
        if(scheduleDTO!=null){
            scheduleRepository.save(ScheduleConverter.convertToSchedule(scheduleDTO));
        }
    }

    @Override
    public void deleteScheduleById(Integer id) {
        if(id==null){
            return;
        }
        scheduleRepository.findById(id).orElseThrow(()->new IllegalStateException("id: "+id+" does not exist"));
        scheduleRepository.deleteById(id);
    }

    @Override
    public void updateScheduleById(Integer id, ScheduleDTO scheduleDTO) {
        Schedule scheduleInDB = scheduleRepository.findById(id).orElseThrow(()->new IllegalStateException("id: "+id+" does not exist"));
        if(scheduleInDB!=null&&scheduleDTO!=null){
            scheduleInDB = ScheduleConverter.convertToSchedule(scheduleDTO);
            scheduleInDB.setId(scheduleDTO.getId());
            scheduleRepository.save(scheduleInDB);
        }
    }

    @Override
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream().map(ScheduleConverter::convertToScheduleDTO).toList();
    }
}
