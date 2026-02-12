
import com.origin.converter.FriendConverter;
import com.origin.dto.FriendDTO;
import com.origin.entity.Friend;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FriendConverterTest {

    @Test
    void testConvertToFriendDTO() {
        // 1. 准备数据：假设今天是 2026-02-10
        Friend friend = new Friend();
        friend.setName("测试君");
        friend.setBirthday(LocalDate.of(1980, 2, 25));
        friend.setTags("Java,Spring,Coding");

        // 2. 执行转换
        FriendDTO dto = FriendConverter.convertToFriendDTO(friend);

        // 3. 验证结果
        assertNotNull(dto);
        assertEquals(45, dto.getAge());
        assertEquals(15, dto.getHowLongForBirthday());
        assertEquals(3, dto.getTags().size());
        assertTrue(dto.getTags().contains("Java"));
    }
}
