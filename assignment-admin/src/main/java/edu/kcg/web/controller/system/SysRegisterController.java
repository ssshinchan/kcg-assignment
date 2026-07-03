package edu.kcg.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import edu.kcg.common.core.controller.BaseController;
import edu.kcg.common.core.domain.AjaxResult;
import edu.kcg.common.core.domain.model.RegisterBody;
import edu.kcg.common.utils.StringUtils;
import edu.kcg.framework.web.service.SysRegisterService;
import edu.kcg.system.service.ISysConfigService;

/**
 * 注册验证
 * 
 * @author assignment
 */
@RestController
public class SysRegisterController extends BaseController
{
    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private ISysConfigService configService;

    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody user)
    {
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            return error("現在、システムで新規登録機能は有効になっていません！");
        }
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }
}
