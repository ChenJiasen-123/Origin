package com.origin.service;

import com.origin.converter.ScheduleConverter;
import com.origin.dto.ScheduleDTO;
import com.origin.entity.Schedule;
import com.origin.entity.ScheduleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl  implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private HttpSession session;

    @Override
    public ScheduleDTO getScheduleById(Long id) {
        Long userId = (Long) session.getAttribute("USER_ID");
        Schedule schedule = scheduleRepository.findById(id).orElse(null);
        if (schedule != null && !userId.equals(schedule.getUserId())) {
            return null;
        }
        if(schedule == null) return null;
        return ScheduleConverter.convertToScheduleDTO(schedule);
    }

    @Override
    public void addNewSchedule(ScheduleDTO scheduleDTO) {
        if(scheduleDTO!=null){
            Long userId = (Long) session.getAttribute("USER_ID");
            Schedule s = ScheduleConverter.convertToSchedule(scheduleDTO);
            s.setUserId(userId);
            scheduleRepository.save(s);
        }
    }

    @Override
    public void deleteScheduleById(Long id) {
        if(id==null){
            return;
        }
        Long userId = (Long) session.getAttribute("USER_ID");
        Schedule s = scheduleRepository.findById(id).orElseThrow(()->new IllegalStateException("id: "+id+" does not exist"));
        if (!userId.equals(s.getUserId())) {
            throw new IllegalStateException("无权限");
        }
        scheduleRepository.deleteById(id);
    }

    @Override
    public void updateScheduleById(Long id, ScheduleDTO scheduleDTO) {
        Long userId = (Long) session.getAttribute("USER_ID");
        Schedule scheduleInDB = scheduleRepository.findById(id).orElseThrow(()->new IllegalStateException("id: "+id+" does not exist"));
        if (!userId.equals(scheduleInDB.getUserId())) {
            throw new IllegalStateException("无权限");
        }
        if(scheduleInDB!=null&&scheduleDTO!=null){
            scheduleInDB = ScheduleConverter.convertToSchedule(scheduleDTO);
            scheduleInDB.setId(scheduleDTO.getId());
            scheduleInDB.setUserId(userId);
            scheduleRepository.save(scheduleInDB);
        }
    }

    @Override
    public List<ScheduleDTO> getAllSchedules() {
        Long userId = (Long) session.getAttribute("USER_ID");
        return scheduleRepository.findAllByUserId(userId).stream().map(ScheduleConverter::convertToScheduleDTO).toList();
    }
}
