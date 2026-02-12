import com.fasterxml.jackson.databind.ObjectMapper;
import com.origin.Main;
import com.origin.dto.FriendDTO;
import com.origin.service.FriendService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // 用于将对象转为 JSON 字符串

    @Autowired
    private FriendService friendService;

    @Test
    @Order(1)
    @Rollback(false)
    void testAddAndGetFriend() throws Exception {
        // 1. 构造一个 DTO
        FriendDTO dto = FriendDTO.builder()
                .name("张三")
                .number(13800138000L)
                .birthday(LocalDate.of(1995, 5, 20))
                .tags(List.of("运动", "旅游"))
                .hometown("北京")
                .build();

        // 2. 测试 POST 新增接口
        mockMvc.perform(post("/friend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // 3. 测试 GET 查询接口
        mockMvc.perform(get("/friend/张三"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("张三"))
                .andExpect(jsonPath("$.data[0].age").exists());
    }
}
