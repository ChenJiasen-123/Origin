package com.origin.converter;

import com.origin.dto.FriendDTO;
import com.origin.entity.Friend;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendConverter {
    public static FriendDTO convertToFriendDTO(Friend friend) {

        if(friend == null) {
            System.out.println("No friend!");
            return null;
        }

        LocalDate today = LocalDate.now();
        LocalDate birthdayOfThisYear=null;

        if(friend.getBirthday() != null) {
            birthdayOfThisYear = LocalDate.of(today.getYear(),
                    friend.getBirthday().getMonth(),
                    friend.getBirthday().getDayOfMonth());
        }

        List<String> tagList = new ArrayList<>();
        if (friend.getTags() != null && !friend.getTags().isBlank()) {
            tagList = new ArrayList<>(Arrays.asList(friend.getTags().split(",")));
        }

        return FriendDTO.builder().
                id(friend.getId()).
                name(friend.getName()).
                number(friend.getNumber()).
                note(friend.getNote()).
                birthday(friend.getBirthday()).
                tags(tagList).
                age(friend.getAge()).
                howLongForBirthday(getHowLongForBirthday(today,birthdayOfThisYear)).
                lastModified(friend.getLastModified()).
                hometown(friend.getHometown()).
                build();
    }
    private static Integer getHowLongForBirthday(LocalDate today, LocalDate birthdayOfThisYear) {
        if(birthdayOfThisYear == null) {
            return null;
        }
        if(ChronoUnit.DAYS.between(today,birthdayOfThisYear)<0){
            LocalDate birthdayOfNextYear = birthdayOfThisYear.plusYears(1);
            return (int) ChronoUnit.DAYS.between(today,birthdayOfNextYear);
        }
        return (int) ChronoUnit.DAYS.between(today,birthdayOfThisYear);
    }
    private static Integer getAge(LocalDate birthday, LocalDate today) {
        if(birthday==null||birthday.isAfter(today)){
            return null;
        }
        return Period.between(birthday,today).getYears();
    }


    public static Friend convertToFriend(FriendDTO friendDTO) {
        if(friendDTO == null) {
            System.out.println("No friendDTO!");
            return null;
        }

        Integer age;

        Friend friend = new Friend();
        friend.setId(friendDTO.getId());
        friend.setName(friendDTO.getName());
        friend.setNumber(friendDTO.getNumber());
        friend.setNote(friendDTO.getNote());
        if (friendDTO.getTags() != null) {
            friend.setTags(String.join(",", friendDTO.getTags()));
        }
        LocalDate today = LocalDate.now();
        LocalDate birthdayOfThisYear;

        if(friendDTO.getBirthday() != null) {
            birthdayOfThisYear = LocalDate.of(today.getYear(),
                    friendDTO.getBirthday().getMonth(),
                    friendDTO.getBirthday().getDayOfMonth());
            age = getAge(friendDTO.getBirthday(),birthdayOfThisYear);
        }else{
            age = friendDTO.getAge();
        }
        friend.setBirthday(friendDTO.getBirthday());
        friend.setHometown(friendDTO.getHometown());
        friend.setLastModified(LocalDateTime.now());
        friend.setAge(age);
        return friend;
    }
}
