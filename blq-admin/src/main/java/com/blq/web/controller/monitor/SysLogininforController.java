package com.blq.web.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.blq.common.annotation.Log;
import com.blq.common.core.controller.BaseController;
import com.blq.common.core.domain.R;
import com.blq.common.core.domain.PageQuery;
import com.blq.common.core.page.TableDataInfo;
import com.blq.common.enums.BusinessType;
import com.blq.common.utils.poi.ExcelUtil;
import com.blq.system.domain.SysLogininfor;
import com.blq.system.service.ISysLogininforService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 系统访问记录
 *
 * @author Blq
 */
@Validated
@Api(value = "系统访问记录", tags = {"系统访问记录管理"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends BaseController {

    private final ISysLogininforService logininforService;

    @ApiOperation("查询系统访问记录列表")
    @SaCheckPermission("monitor:logininfor:list")
    @GetMapping("/list")
    public TableDataInfo<SysLogininfor> list(SysLogininfor logininfor, PageQuery pageQuery) {
        return logininforService.selectPageLogininforList(logininfor, pageQuery);
    }

    @ApiOperation("导出系统访问记录列表")
    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @SaCheckPermission("monitor:logininfor:export")
    @PostMapping("/export")
    public void export(SysLogininfor logininfor, HttpServletResponse response) {
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        ExcelUtil.exportExcel(list, "登录日志", SysLogininfor.class, response);
    }

    @ApiOperation("删除系统访问记录")
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public R<Void> remove(@PathVariable Long[] infoIds) {
        return toAjax(logininforService.deleteLogininforByIds(infoIds));
    }

    @ApiOperation("清空系统访问记录")
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public R<Void> clean() {
        logininforService.cleanLogininfor();
        return R.ok();
    }
}
