package edu.kcg.web.controller.tool;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.kcg.common.core.controller.BaseController;
import edu.kcg.common.core.domain.R;
import edu.kcg.common.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * swagger 用户测试方法
 *
 * @author assignment
 */
@Tag(name = "ユーザー情報管理")
@RestController
@RequestMapping("/test/user")
public class TestController extends BaseController
{
    private final static Map<Integer, UserEntity> users = new LinkedHashMap<Integer, UserEntity>();
    {
        users.put(1, new UserEntity(1, "admin", "admin123", "15888888888"));
        users.put(2, new UserEntity(2, "ry", "admin123", "15666666666"));
    }
    
    @Operation(summary = "ユーザー一覧の取得")
    @GetMapping("/list")
    public R<List<UserEntity>> userList()
    {
        List<UserEntity> userList = new ArrayList<UserEntity>(users.values());
        return R.ok(userList);
    }
    
    @Operation(summary = "ユーザー詳細の取得")
    @GetMapping("/{userId}")
    public R<UserEntity> getUser(@PathVariable(name = "userId")
    Integer userId)
    {
        if (!users.isEmpty() && users.containsKey(userId))
        {
            return R.ok(users.get(userId));
        }
        else
        {
            return R.fail("ユーザーが存在しません");
        }
    }
    
    @Operation(summary = "ユーザーの追加")
    @PostMapping("/save")
    public R<String> save(UserEntity user)
    {
        if (StringUtils.isNull(user) || StringUtils.isNull(user.getUserId()))
        {
            return R.fail("ユーザーIDは必須入力です");
        }
        users.put(user.getUserId(), user);
        return R.ok();
    }
    
    @Operation(summary = "ユーザーの更新")
    @PutMapping("/update")
    public R<String> update(@RequestBody
    UserEntity user)
    {
        if (StringUtils.isNull(user) || StringUtils.isNull(user.getUserId()))
        {
            return R.fail("ユーザーIDは必須入力です");
        }
        if (users.isEmpty() || !users.containsKey(user.getUserId()))
        {
            return R.fail("ユーザーが存在しません");
        }
        users.remove(user.getUserId());
        users.put(user.getUserId(), user);
        return R.ok();
    }
    
    @Operation(summary = "ユーザー情報の削除")
    @DeleteMapping("/{userId}")
    public R<String> delete(@PathVariable(name = "userId")
    Integer userId)
    {
        if (!users.isEmpty() && users.containsKey(userId))
        {
            users.remove(userId);
            return R.ok();
        }
        else
        {
            return R.fail("ユーザーが存在しません");
        }
    }
}

@Schema(description = "ユーザーエンティティ")
class UserEntity
{
    @Schema(title = "ユーザーID")
    private Integer userId;
    
    @Schema(title = "ユーザー名")
    private String username;
    
    @Schema(title = "ユーザーパスワード")
    private String password;
    
    @Schema(title = "ユーザー携帯電話番号")
    private String mobile;
    
    public UserEntity()
    {
        
    }
    
    public UserEntity(Integer userId, String username, String password, String mobile)
    {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.mobile = mobile;
    }
    
    public Integer getUserId()
    {
        return userId;
    }
    
    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getMobile()
    {
        return mobile;
    }
    
    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }
}
