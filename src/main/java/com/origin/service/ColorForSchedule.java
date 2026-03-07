package com.origin.service;

import lombok.Getter;

@Getter
public enum ColorForSchedule {

    // 红色/粉色系
    MAROON("#8B0000", "栗色"),
    DARK_RED("#B22222", "猩红"),
    DEEP_PINK("#C71585", "暗紫红"),
    
   
    // 橙色/黄色系
    DARK_ORANGE("#FF8C00", "深橙"),
    CHOCOLATE("#D2691E", "巧克力色"),
    DARK_GOLDENROD("#B8860B", "暗金色"),


    // 绿色系
    FOREST_GREEN("#228B22", "森林绿"),
    SEA_GREEN("#01510dff", "深海绿"), 
    OLIVE_DRAB("#6B8E23", "橄榄褐"),
    

    // 蓝色/青色系
    MIDNIGHT_BLUE("#191970", "午夜蓝"),
    STEEL_BLUE("#4682b4ff", "钢青蓝"),
    ROYAL_BLUE("#4169E1", "皇家蓝"),
    SEA_BLUE("#006994", "海蓝"),
   

    // 紫色/创意系
    INDIGO("#4B0082", "靛青"),
    DARK_VIOLET("#9932CC", "暗紫"),
    VIOLET("#2600feff", "紫罗兰");
  

    public final String color;
    public final String description;
    ColorForSchedule(String color, String description) {
        this.color = color;
        this.description = description;
    }
}
//bicidaoduian-first-fix 
//bian-second-fix-2026.3.7 