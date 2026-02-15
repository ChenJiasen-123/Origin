package com.origin.controller;

import com.origin.service.ColorForSchedule;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ColorController {

    @GetMapping("/api/colorList")
    public List<String> getColors(){
        return Arrays.stream(ColorForSchedule.values()).map(ColorForSchedule::getColor).collect(Collectors.toList());
    }
}
