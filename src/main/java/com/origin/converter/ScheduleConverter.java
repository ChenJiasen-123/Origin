package com.origin.converter;

import com.origin.dto.ScheduleDTO;
import com.origin.entity.Schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScheduleConverter {

    public  static ScheduleDTO convertTOScheduleDTO(Schedule schedule) {

        List<String> whos = new ArrayList<>();
        if(schedule.getWho() != null||!schedule.getWho().isEmpty()) {
            whos = new ArrayList<>(Arrays.asList(schedule.getWho().split(",")));
        }

        return ScheduleDTO.builder().
                name(schedule.getName()).
                location(schedule.getLocation()).
                who(whos).
                start_time(schedule.getStart_time()).
                end_time(schedule.getEnd_time()).
                reminder_time(schedule.getReminder_time()).
                is_completed(schedule.getIs_completed()).
                note(schedule.getNote()).
                color(schedule.getColor()).
                build();
    }

    public static Schedule convertFromScheduleDTO(ScheduleDTO scheduleDTO) {

        String who=null;
        if(scheduleDTO.getWho() != null||!scheduleDTO.getWho().isEmpty()) {
            who = String.join(",", scheduleDTO.getWho());
        }

        Schedule schedule = new Schedule();
        schedule.setName(scheduleDTO.getName());
        schedule.setLocation(scheduleDTO.getLocation());
        schedule.setColor(scheduleDTO.getColor());
        schedule.setStart_time(scheduleDTO.getStart_time());
        schedule.setEnd_time(scheduleDTO.getEnd_time());
        schedule.setReminder_time(scheduleDTO.getReminder_time());
        schedule.setIs_completed(scheduleDTO.getIs_completed());
        schedule.setNote(scheduleDTO.getNote());
        schedule.setWho(who);

        return schedule;
    }
}
