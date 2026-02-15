package com.origin.service;

import lombok.Getter;

@Getter
public enum ColorForSchedule {

    // 红色/粉色系
    PURE_RED("#FF0000", "纯红"),
    ORANGE_RED("#FF4500", "橙红"),
    CRIMSON("#DC143C", "猩红"),
    DEEP_PINK("#FF1493", "深粉"),
    HOT_PINK("#FF69B4", "亮粉"),
    MEDIUM_VIOLET_RED("#C71585", "中紫红"),

    // 橙色/黄色系
    DARK_ORANGE("#FF8C00", "深橙"),
    ORANGE("#FFA500", "橙色"),
    GOLD("#FFD700", "金黄"),
    YELLOW("#FFFF00", "鲜黄"),
    SANDY_BROWN("#F4A460", "沙褐"),
    PEACH("#FFDAB9", "桃色"),

    // 绿色系
    LIME("#00FF00", "荧光绿"),
    LIME_GREEN("#32CD32", "酸橙绿"),
    MEDIUM_SPRING_GREEN("#00FA9A", "中春绿"),
    GREEN_YELLOW("#ADFF2F", "绿黄"),
    GREEN("#008000", "纯绿"),
    LIGHT_SEA_GREEN("#20B2AA", "浅海蓝"),

    // 蓝色/青色系
    PURE_BLUE("#0000FF", "纯蓝"),
    DODGER_BLUE("#1E90FF", "道奇蓝"),
    DEEP_SKY_BLUE("#00BFFF", "深天蓝"),
    AQUA("#00FFFF", "青色"),
    ROYAL_BLUE("#4169E1", "皇家蓝"),
    DARK_TURQUOISE("#00CED1", "深松石"),

    // 紫色/创意系
    BLUE_VIOLET("#8A2BE2", "蓝紫"),
    DARK_VIOLET("#9400D3", "暗紫"),
    DARK_ORCHID("#9932CC", "深兰花"),
    MAGENTA("#FF00FF", "洋红"),
    MEDIUM_SLATE_BLUE("#7B68EE", "中板岩蓝"),
    VIOLET("#EE82EE", "紫罗兰");

    public final String color;
    public final String description;
    ColorForSchedule(String color, String description) {
        this.color = color;
        this.description = description;
    }
}
