package com.origin.service;

import com.origin.converter.FriendConverter;
import com.origin.dto.FriendDTO;
import com.origin.entity.Friend;
import com.origin.entity.FriendRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendServiceImpl implements FriendService{

    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private HttpSession session;

    @Override
    public FriendDTO getFriendById(Long id){
        Long userId = (Long) session.getAttribute("USER_ID");
        Friend friend = friendRepository.findByIdAndUserId(id, userId).orElse(null);
        if(friend==null){
            System.out.println("can't find any friend");
            return null;
        }
        return FriendConverter.convertToFriendDTO(friend);
    }

    @Override
    public void addNewFriend(FriendDTO friendDTO) {
        if (friendDTO != null) {
            Long userId = (Long) session.getAttribute("USER_ID");
            Friend f = FriendConverter.convertToFriend(friendDTO);
            f.setUserId(userId);
            friendRepository.save(f);
        }else{
            System.out.println("FriendDTO is null");
        }
    }

    @Override
    public void deleteAllFriendsByIds(List<Long> ids) {
        if(ids==null||ids.isEmpty()){
            System.out.println("ids are null");
            return;
        }
        Long userId = (Long) session.getAttribute("USER_ID");
        for (Long id : ids) {
            friendRepository.findByIdAndUserId(id, userId).orElseThrow(()->new IllegalStateException("id: "+id+" does not exist"));
            friendRepository.deleteById(id);
        }
    }

    @Override
    public void updateFriendById(Long id, FriendDTO friendDTO) {
        Long userId = (Long) session.getAttribute("USER_ID");
        Friend friendInDB = friendRepository.findByIdAndUserId(id, userId).orElseThrow(()->new IllegalStateException("id: "+id+" does not exist"));
        if (friendInDB != null&&friendDTO != null) {
            friendInDB = FriendConverter.convertToFriend(friendDTO);
            friendInDB.setId(id);
            friendInDB.setUserId(userId);
            friendRepository.save(friendInDB);
        } else {
            System.out.println("Please make sure friendDTO or name is nonnull");
        }
    }

    @Override
    public List<FriendDTO> getFriendsByKeyword(String keyword) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (keyword == null || keyword.trim().isEmpty()) {
            return friendRepository.findAllByUserId(userId).stream().map(FriendConverter::convertToFriendDTO).collect(Collectors.toList());
        }
        return friendRepository.searchFriends(userId, keyword).stream().map(FriendConverter::convertToFriendDTO).collect(Collectors.toList());
    }
}
