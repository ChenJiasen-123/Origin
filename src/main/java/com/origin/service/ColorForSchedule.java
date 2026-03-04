package com.origin.service;

import lombok.Getter;

@Getter
public enum ColorForSchedule {

    // 红色/粉色系
    PURE_RED("#FF0000", "纯红"),
    CRIMSON("#DC143C", "猩红"),
    DEEP_PINK("#FF1493", "深粉"),
    
   
    // 橙色/黄色系
    DARK_ORANGE("#FF8C00", "深橙"),
    ORANGE("#FFA500", "橙色"),
    GOLD("#FFD700", "金黄"),


    // 绿色系
    LIME("#00FF00", "荧光绿"),
    LIME_GREEN("#32CD32", "酸橙绿"), 
    GREEN("#008000", "纯绿"),
    

    // 蓝色/青色系
    PURE_BLUE("#0000FF", "纯蓝"),
    DODGER_BLUE("#1E90FF", "道奇蓝"),
    ROYAL_BLUE("#4169E1", "皇家蓝"),
   

    // 紫色/创意系
    BLUE_VIOLET("#8A2BE2", "蓝紫"),
    DARK_VIOLET("#9400D3", "暗紫"),
    MAGENTA("#FF00FF", "洋红");
  

    public final String color;
    public final String description;
    ColorForSchedule(String color, String description) {
        this.color = color;
        this.description = description;
    }
}
//bicidaoduian-first-fix 