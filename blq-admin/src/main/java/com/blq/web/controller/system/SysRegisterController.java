package com.blq.web.controller.system;

import com.blq.common.annotation.Anonymous;
import com.blq.common.core.controller.BaseController;
import com.blq.common.core.domain.R;
import com.blq.common.core.domain.model.RegisterBody;
import com.blq.system.service.ISysConfigService;
import com.blq.system.service.SysRegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册验证
 *
 * @author Lion Li
 */
@Validated
@Api(value = "注册验证控制器", tags = {"注册验证管理"})
@RequiredArgsConstructor
@RestController
public class SysRegisterController extends BaseController {

    private final SysRegisterService registerService;
    private final ISysConfigService configService;

    @Anonymous
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public R<Void> register(@Validated @RequestBody RegisterBody user) {
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) {
            return R.fail("当前系统没有开启注册功能！");
        }
        registerService.register(user);
        return R.ok();
    }
}
