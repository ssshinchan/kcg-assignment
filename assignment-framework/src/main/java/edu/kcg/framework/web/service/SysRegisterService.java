package edu.kcg.framework.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import edu.kcg.common.constant.CacheConstants;
import edu.kcg.common.constant.Constants;
import edu.kcg.common.constant.UserConstants;
import edu.kcg.common.core.domain.entity.SysUser;
import edu.kcg.common.core.domain.model.RegisterBody;
import edu.kcg.common.core.redis.RedisCache;
import edu.kcg.common.exception.user.CaptchaException;
import edu.kcg.common.exception.user.CaptchaExpireException;
import edu.kcg.common.utils.DateUtils;
import edu.kcg.common.utils.MessageUtils;
import edu.kcg.common.utils.SecurityUtils;
import edu.kcg.common.utils.StringUtils;
import edu.kcg.framework.manager.AsyncManager;
import edu.kcg.framework.manager.factory.AsyncFactory;
import edu.kcg.system.service.ISysConfigService;
import edu.kcg.system.service.ISysUserService;

/**
 * 注册校验方法
 * 
 * @author assignment
 */
@Component
public class SysRegisterService
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 注册
     */
    public String register(RegisterBody registerBody)
    {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);

        // 验证码开关
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username))
        {
            msg = "ユーザー名は必須入力です";
        }
        else if (StringUtils.isEmpty(password))
        {
            msg = "ユーザーパスワードは必須入力です";
        }
        else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            msg = "アカウントの長さは2文字から20文字の間である必要があります";
        }
        else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            msg = "パスワードの長さは5文字から20文字の間である必要があります";
        }
        else if (!userService.checkUserNameUnique(sysUser))
        {
            msg = "ユーザー '" + username + "' の保存に失敗しました。登録アカウントが既に存在します";
        }
        else
        {
            sysUser.setNickName(username);
            sysUser.setPwdUpdateDate(DateUtils.getNowDate());
            sysUser.setPassword(SecurityUtils.encryptPassword(password));
            boolean regFlag = userService.registerUser(sysUser);
            if (!regFlag)
            {
                msg = "登録に失敗しました。システム管理者にお問い合わせください";
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success")));
            }
        }
        return msg;
    }

    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }
}
