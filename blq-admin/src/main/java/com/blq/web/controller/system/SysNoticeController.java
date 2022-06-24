package com.blq.web.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.blq.common.annotation.Log;
import com.blq.common.core.controller.BaseController;
import com.blq.common.core.domain.R;
import com.blq.common.core.domain.PageQuery;
import com.blq.common.core.page.TableDataInfo;
import com.blq.common.enums.BusinessType;
import com.blq.system.domain.SysNotice;
import com.blq.system.service.ISysNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 公告 信息操作处理
 *
 * @author Lion Li
 */
@Validated
@Api(value = "公告信息控制器", tags = {"公告信息管理"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController extends BaseController {

    private final ISysNoticeService noticeService;

    /**
     * 获取通知公告列表
     */
    @ApiOperation("获取通知公告列表")
    @SaCheckPermission("system:notice:list")
    @GetMapping("/list")
    public TableDataInfo<SysNotice> list(SysNotice notice, PageQuery pageQuery) {
        return noticeService.selectPageNoticeList(notice, pageQuery);
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @ApiOperation("根据通知公告编号获取详细信息")
    @SaCheckPermission("system:notice:query")
    @GetMapping(value = "/{noticeId}")
    public R<SysNotice> getInfo(@ApiParam("公告ID") @PathVariable Long noticeId) {
        return R.ok(noticeService.selectNoticeById(noticeId));
    }

    /**
     * 新增通知公告
     */
    @ApiOperation("新增通知公告")
    @SaCheckPermission("system:notice:add")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysNotice notice) {
        return toAjax(noticeService.insertNotice(notice));
    }

    /**
     * 修改通知公告
     */
    @ApiOperation("修改通知公告")
    @SaCheckPermission("system:notice:edit")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysNotice notice) {
        return toAjax(noticeService.updateNotice(notice));
    }

    /**
     * 删除通知公告
     */
    @ApiOperation("删除通知公告")
    @SaCheckPermission("system:notice:remove")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    public R<Void> remove(@ApiParam("公告ID串") @PathVariable Long[] noticeIds) {
        return toAjax(noticeService.deleteNoticeByIds(noticeIds));
    }
}