package com.origin.service;

import com.origin.converter.FriendConverter;
import com.origin.dto.FriendDTO;
import com.origin.entity.Friend;
import com.origin.entity.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendServiceImpl implements FriendService{

    @Autowired
    private FriendRepository friendRepository;

    @Override
    public FriendDTO getFriendById(Integer id){
        Friend friend = friendRepository.findById(id).orElse(null);
        if(friend==null){
            System.out.println("can't find any friend");
            return null;
        }
        return FriendConverter.convertToFriendDTO(friend);
    }

    @Override
    public void addNewFriend(FriendDTO friendDTO) {
        if (friendDTO != null) {
            friendRepository.save(FriendConverter.convertToFriend(friendDTO));
        }else{
            System.out.println("FriendDTO is null");
        }
    }

    @Override
    public void deleteAllFriendsByIds(List<Integer> ids) {
        if(ids==null||ids.isEmpty()){
            System.out.println("ids are null");
            return;
        }
        for (Integer id : ids) {
            friendRepository.findById(id).orElseThrow(()->new IllegalStateException("id: "+id+" does not exist"));
            friendRepository.deleteById(id);
        }
    }

    @Override
    public void updateFriendById(Integer id, FriendDTO friendDTO) {
        Friend friendInDB = friendRepository.findById(id).orElseThrow(()->new IllegalStateException("id: "+id+" does not exist"));
        if (friendInDB != null&&friendDTO != null) {
            friendInDB = FriendConverter.convertToFriend(friendDTO);
            friendInDB.setId(id);
            friendRepository.save(friendInDB);
        } else {
            System.out.println("Please make sure friendDTO or name is nonnull");
        }
    }

    @Override
    public List<FriendDTO> getFriendsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return friendRepository.findAll().stream().map(FriendConverter::convertToFriendDTO).collect(Collectors.toList());
        }
        return friendRepository.searchFriends(keyword).stream().map(FriendConverter::convertToFriendDTO).collect(Collectors.toList());
    }
}
