package com.origin.controller;

import com.origin.Response;
import com.origin.dto.ScheduleDTO;
import com.origin.service.ScheduleService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ScheduleController {

    @Autowired
    ScheduleService scheduleService;

    @GetMapping("/schedule/{id}")
    public Response<ScheduleDTO> getScheduleById(@PathVariable Integer id){
        return Response.newSuccess(scheduleService.getScheduleById(id));
    }

    @DeleteMapping("/schedule")
    public Response<Void> deleteAllSchedulesById(@RequestBody List<Integer> ids){
        scheduleService.deleteAllSchedulesByIds(ids);
        return Response.newSuccess(null);
    }

    @PutMapping("/schedule/{id}")
    public Response<Void> updateSchedule(@PathVariable Integer id,@RequestBody ScheduleDTO scheduleDTO){
        scheduleService.updateScheduleById(id,scheduleDTO);
        return Response.newSuccess(null);
    }

    @PostMapping("/schedule")
    public Response<Void> addSchedule(@RequestBody ScheduleDTO scheduleDTO){
        scheduleService.addNewSchedule(scheduleDTO);
        return  Response.newSuccess(null);
    }

    @GetMapping("/schedule")
    public Response<List<ScheduleDTO>> getAllSchedules(){
        return Response.newSuccess(scheduleService.getAllSchedules());
    }
}
