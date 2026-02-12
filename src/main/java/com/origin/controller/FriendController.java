package com.origin.controller;

import com.origin.dto.FriendDTO;
import com.origin.Response;
import com.origin.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FriendController {

    @Autowired
    private FriendService friendService;

    @GetMapping("/friend/{id}")
    public Response<FriendDTO> getFriendById(@PathVariable Integer id){
        return Response.newSuccess(friendService.getFriendById(id));
    }

    @PostMapping("/friend")
    public Response<Void> addFriend(@RequestBody FriendDTO friendDTO){
        friendService.addNewFriend(friendDTO);
        return Response.newSuccess(null);
    }

    @DeleteMapping("/friend")
    public Response<Void> deleteFriends(@RequestBody List<Integer> ids){
        friendService.deleteAllFriendsByIds(ids);
        return Response.newSuccess(null);
    }

    @PutMapping("/friend/{id}")
    public void updateFriendById(@PathVariable Integer id, @RequestBody FriendDTO friendDTO){
        friendService.updateFriendById(id,friendDTO);
    }

    @GetMapping("/friend")
    public Response<List<FriendDTO>> getFriendsByKeyword(@RequestParam(required = false) String keyword){
        List<FriendDTO> list = friendService.getFriendsByKeyword(keyword);
        return Response.newSuccess(list);
    }

}
