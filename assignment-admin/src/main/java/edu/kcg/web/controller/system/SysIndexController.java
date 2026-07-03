package edu.kcg.web.controller.system;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.kcg.common.config.AssignmentConfig;
import edu.kcg.common.core.domain.AjaxResult;
import edu.kcg.common.core.domain.entity.SysUser;
import edu.kcg.common.utils.SecurityUtils;
import edu.kcg.common.utils.StringUtils;
import edu.kcg.system.service.ISysUserService;

/**
 * 首页
 *
 * @author assignment
 */
@RestController
public class SysIndexController
{
    /** 系统基础配置 */
    @Autowired
    private AssignmentConfig assignmentConfig;

    @Autowired
    private ISysUserService userService;

    /**
     * 访问首页，提示语
     */
    @RequestMapping("/")
    public String index()
    {
        return StringUtils.format("{} 管理画面システムをご利用いただきありがとうございます。現在のバージョンは v{} です。フロントエンド of のURLからアクセスしてください。", assignmentConfig.getName(), assignmentConfig.getVersion());
    }

    /**
     * 解锁屏幕
     */
    @PostMapping("/unlockscreen")
    public AjaxResult unlockScreen(@RequestBody Map<String, String> body)
    {
        String password = body.get("password");
        if (StringUtils.isEmpty(password))
        {
            return AjaxResult.error("パスワードは必須入力です");
        }
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username);
        if (user == null)
        {
            return AjaxResult.error("サーバーセッションがタイムアウトしました。再度ログインしてください");
        }
        if (!SecurityUtils.matchesPassword(password, user.getPassword()))
        {
            return AjaxResult.error("パスワードが間違っています。再入力してください");
        }

        return AjaxResult.success("アンロック成功");
    }
}
