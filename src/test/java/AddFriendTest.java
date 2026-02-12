
import com.origin.dto.FriendDTO;
import com.origin.service.FriendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest(classes = com.origin.Main.class)
class AddFriendsTest {

    @Autowired
    private FriendService friendService;

    @Test
    void addBulkFriends() {
        List<FriendDTO> list = List.of(
                FriendDTO.builder().name("袁一泓").number(123456789123L).tags(List.of("美丽","摄影","东北电力大学")).hometown("广东揭阳").build(),
                FriendDTO.builder().name("陈可胜").number(1234567890L).tags(List.of("书法","华南理工大学")).hometown("广东揭阳").build(),
                FriendDTO.builder().name("赖源泓").number(1234567890L).tags(List.of("书法","东莞理工学院")).hometown("广东揭阳").build(),
                FriendDTO.builder().name("罗英灏").number(1234567890L).tags(List.of("汕头大学")).hometown("广东揭阳").build(),
                FriendDTO.builder().name("王梓妍").number(1234567890L).tags(List.of("美丽","韩山师范大学")).hometown("广东揭阳").build()
        );
        list.forEach(friendService::addNewFriend);
        System.out.println("成功导入 " + list.size() + " 位好友数据！");
    }

    @Test
    void updateFriend() {
        friendService.updateFriendById(6,FriendDTO.builder().name("陈可胜").number(1234567890L).tags(List.of("书法","华南理工大学","帅气")).hometown("广东揭阳").build());
        friendService.updateFriendById(5,FriendDTO.builder().name("袁一泓").number(123456789123L).birthday(LocalDate.parse("2007-09-01")).tags(List.of("美丽","摄影","东北电力大学","书法")).hometown("广东揭阳").build());
        friendService.updateFriendById(8,FriendDTO.builder().name("罗英灏").number(1234567890L).tags(List.of("汕头大学","帅气")).birthday(LocalDate.parse("2007-11-03")).hometown("广东揭阳").build());
    }
}
