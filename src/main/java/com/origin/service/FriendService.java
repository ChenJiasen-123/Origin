package com.origin.service;

import com.origin.dto.FriendDTO;

import java.util.List;

/**
 * <strong>规定了对外提供哪些功能:</strong>
 * <li>getFriendsByName</li>
 * <li>getFriendByNameAndId</li>
 * <li>addNewFriend</li>
 * <li>deleteFriendById</li>
 * <li>updateFriendByName</li>
 */
public interface FriendService {
    FriendDTO getFriendById(Integer id);

    void addNewFriend(FriendDTO friendDTO);

    void deleteAllFriendsByIds(List<Integer> ids);

    void updateFriendById(Integer id, FriendDTO friendDTO);

    List<FriendDTO> getFriendsByKeyword(String keyword);
}
