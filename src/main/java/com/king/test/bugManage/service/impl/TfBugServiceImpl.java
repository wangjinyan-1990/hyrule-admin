package com.king.test.bugManage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.utils.CounterUtil;
import com.king.common.utils.SecurityUtils;
import com.king.framework.dataDictionary.service.IDataDictionaryService;
import com.king.test.baseManage.testDirectory.service.ITestDirectoryService;
import com.king.test.bugManage.entity.TfBug;
import com.king.test.bugManage.entity.TfBugHistory;
import com.king.test.bugManage.entity.TfBugState;
import com.king.test.bugManage.mapper.TfBugHistoryMapper;
import com.king.test.bugManage.mapper.TfBugMapper;
import com.king.test.bugManage.mapper.TfBugStateMapper;
import com.king.test.bugManage.service.ITfBugService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缺陷Service实现类
 */
@Service("tfBugServiceImpl")
public class TfBugServiceImpl extends ServiceImpl<TfBugMapper, TfBug> implements ITfBugService {

    private static final Logger logger = LoggerFactory.getLogger(TfBugServiceImpl.class);

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    @Qualifier("testDirectoryServiceImpl")
    private ITestDirectoryService testDirectoryService;

    @Autowired
    @Qualifier("dataDictionaryServiceImpl")
    private IDataDictionaryService dataDictionaryService;

    @Autowired
    private TfBugHistoryMapper bugHistoryMapper;

    @Autowired
    private TfBugStateMapper bugStateMapper;

    @Autowired
    private CounterUtil counterUtil;

    @Override
    public Map<String, Object> getBugPage(int pageNo, int pageSize,
                                          String systemId, String directoryId,
                                          String bugId, String bugName,
                                          String bugState, String bugType,
                                          Integer bugSeverityLevel, String bugSource,
                                          String submitterId, String checkerId, String developerId,
                                          String commitTimeStart, String commitTimeEnd,
                                          String closeTimeStart, String closeTimeEnd) {
        Page<TfBug> page = new Page<>(pageNo, pageSize);
        // 获取目录及其所有子目录的ID列表（包含当前目录）
        List<String> directoryIds = fetchDirectoryHierarchy(directoryId, systemId);
        Page<TfBug> result = this.baseMapper.selectPageBugList(page, systemId, directoryIds,
                bugId, bugName, bugState, bugType, bugSeverityLevel, bugSource,
                submitterId, checkerId, developerId,
                commitTimeStart, commitTimeEnd, closeTimeStart, closeTimeEnd);

        // 填充字典名称和测试集路径（如果 SQL 关联失败，手动查询数据字典）
        for (TfBug bug : result.getRecords()) {
            fillDictionaryNames(bug);
            fillFullPath(bug);
        }

        Map<String, Object> data = new HashMap<>(8);
        data.put("rows", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pageNo", result.getCurrent());
        data.put("pageSize", result.getSize());
        data.put("totalPages", result.getPages());
        return data;
    }

    @Override
    public TfBug getBugDetail(String bugId) {
        Assert.hasText(bugId, "缺陷ID不能为空");
        TfBug bug = this.baseMapper.selectBugDetailById(bugId);
        if (bug == null) {
            throw new IllegalArgumentException("缺陷不存在");
        }
        fillDictionaryNames(bug);
        fillFullPath(bug);
        return bug;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createBug(TfBug bug) {
        Assert.notNull(bug, "缺陷信息不能为空");
        Assert.hasText(bug.getBugName(), "缺陷名称不能为空");
        Assert.hasText(bug.getSystemId(), "系统ID不能为空");

        // 数据转换：将前端传入的中文名称转换为代码值
        convertBugDataFromFrontend(bug);

        // 生成缺陷ID（如果未提供）
        if (!StringUtils.hasText(bug.getBugId())) {
            String bugIdStr = generateBugId(bug.getSystemId());
            bug.setBugId(bugIdStr);
            logger.debug("生成缺陷ID: {}", bugIdStr);
        }

        // 设置提交时间和提交人
        LocalDateTime now = LocalDateTime.now();
        bug.setCommitTime(now);
        String currentUser = securityUtils.getUserId();
        if (StringUtils.hasText(currentUser)) {
            bug.setSubmitterId(currentUser);
        }

        // 初始化解决次数和提交次数
        if (bug.getSolveVolume() == null) {
            bug.setSolveVolume(0);
        }
        if (bug.getSubmittedVolume() == null) {
            bug.setSubmittedVolume(1);
        }

        boolean saved = this.save(bug);
        if (saved) {
            // 记录历史，如果有备注则使用备注，否则使用默认描述
            // 旧状态设置为'无'，因为这是创建操作
            String comment = StringUtils.hasText(bug.getRemark()) ? bug.getRemark() : "创建缺陷";
            recordBugHistory(bug.getBugId(), "无", bug.getBugState(), comment, currentUser, bug.getSystemId());
            logger.info("创建缺陷成功: bugId={}, bugName={}", bug.getBugId(), bug.getBugName());
        } else {
            logger.error("创建缺陷失败: bugName={}", bug.getBugName());
        }
        return saved;
    }

    /**
     * 将前端传入的数据转换为数据库存储格式
     * 1. 将中文状态名转换为代码值
     * 2. 处理空字符串，转换为null
     * 3. 处理其他字段转换
     */
    private void convertBugDataFromFrontend(TfBug bug) {
        // 转换缺陷状态：如果传入的是中文名称，转换为代码值
        if (StringUtils.hasText(bug.getBugState())) {
            // 先尝试通过数据字典将名称转换为代码值
            String bugStateCode = dataDictionaryService.getDataValueByTypeAndName("bugState", bug.getBugState());
            if (bugStateCode != null) {
                logger.debug("缺陷状态转换: {} -> {}", bug.getBugState(), bugStateCode);
                bug.setBugState(bugStateCode);
            } else {
                // 如果转换失败，说明传入的已经是代码值，保持不变
                logger.debug("缺陷状态未转换，使用原值: {}", bug.getBugState());
            }
        }

        // 处理空字符串：将空字符串转换为null
        if (bug.getBugSource() != null && bug.getBugSource().trim().isEmpty()) {
            bug.setBugSource(null);
        }
        if (bug.getPrority() != null && bug.getPrority().trim().isEmpty()) {
            bug.setPrority(null);
        }
        if (bug.getBugType() != null && bug.getBugType().trim().isEmpty()) {
            bug.setBugType(null);
        }
        if (bug.getCloseReason() != null && bug.getCloseReason().trim().isEmpty()) {
            bug.setCloseReason(null);
        }
        if (bug.getRemark() != null && bug.getRemark().trim().isEmpty()) {
            bug.setRemark(null);
        }

        // 转换优先级：如果传入的是中文名称，转换为代码值
        if (StringUtils.hasText(bug.getProrityName()) && !StringUtils.hasText(bug.getPrority())) {
            String prorityCode = dataDictionaryService.getDataValueByTypeAndName("prority", bug.getProrityName());
            if (prorityCode != null) {
                logger.debug("优先级转换: {} -> {}", bug.getProrityName(), prorityCode);
                bug.setPrority(prorityCode);
            }
        }

        // 转换缺陷来源：如果传入的是中文名称，转换为代码值
        if (StringUtils.hasText(bug.getBugSourceName()) && !StringUtils.hasText(bug.getBugSource())) {
            String bugSourceCode = dataDictionaryService.getDataValueByTypeAndName("bugSource", bug.getBugSourceName());
            if (bugSourceCode != null) {
                logger.debug("缺陷来源转换: {} -> {}", bug.getBugSourceName(), bugSourceCode);
                bug.setBugSource(bugSourceCode);
            }
        }

        // 转换缺陷类型：如果传入的是中文名称，转换为代码值
        if (StringUtils.hasText(bug.getBugTypeName()) && !StringUtils.hasText(bug.getBugType())) {
            String bugTypeCode = dataDictionaryService.getDataValueByTypeAndName("bugType", bug.getBugTypeName());
            if (bugTypeCode != null) {
                logger.debug("缺陷类型转换: {} -> {}", bug.getBugTypeName(), bugTypeCode);
                bug.setBugType(bugTypeCode);
            }
        }

        // 转换缺陷严重级别：如果传入的是中文名称，转换为代码值
        if (bug.getBugSeverityLevel() == null && StringUtils.hasText(bug.getBugSeverityLevelName())) {
            String severityLevelCode = dataDictionaryService.getDataValueByTypeAndName("bugSeverityLevel", bug.getBugSeverityLevelName());
            if (severityLevelCode != null) {
                try {
                    bug.setBugSeverityLevel(Integer.valueOf(severityLevelCode));
                    logger.debug("缺陷严重级别转换: {} -> {}", bug.getBugSeverityLevelName(), severityLevelCode);
                } catch (NumberFormatException e) {
                    logger.warn("缺陷严重级别转换失败: {}", severityLevelCode, e);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBug(TfBug bug) {
        Assert.notNull(bug, "缺陷信息不能为空");
        Assert.hasText(bug.getBugId(), "缺陷ID不能为空");
        Assert.hasText(bug.getBugName(), "缺陷名称不能为空");

        // 查询原缺陷信息
        TfBug existing = this.getById(bug.getBugId());
        if (existing == null) {
            throw new IllegalArgumentException("缺陷不存在");
        }

        // 记录状态变更
        String oldState = existing.getBugState();
        String newState = bug.getBugState();
        String currentUser = securityUtils.getUserId();

        boolean updated = this.updateById(bug);
        if (updated) {
            // 如果状态发生变化，记录历史
            if (!StringUtils.hasText(oldState) || !oldState.equals(newState)) {
                // 如果有备注则使用备注，否则使用默认描述
                String comment = StringUtils.hasText(bug.getRemark()) ? bug.getRemark() : "更新缺陷";
                recordBugHistory(bug.getBugId(), oldState, newState, comment, currentUser, bug.getSystemId());
            } else if (StringUtils.hasText(bug.getRemark())) {
                // 即使状态未变化，如果有备注也记录历史
                recordBugHistory(bug.getBugId(), oldState, newState, bug.getRemark(), currentUser, bug.getSystemId());
            }
            logger.info("更新缺陷成功: bugId={}, bugName={}", bug.getBugId(), bug.getBugName());
        } else {
            logger.error("更新缺陷失败: bugId={}", bug.getBugId());
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBug(String bugId) {
        Assert.hasText(bugId, "缺陷ID不能为空");
        TfBug bug = this.getById(bugId);
        if (bug == null) {
            throw new IllegalArgumentException("缺陷不存在");
        }

        // 删除关联的历史记录
        LambdaQueryWrapper<TfBugHistory> historyWrapper = new LambdaQueryWrapper<>();
        historyWrapper.eq(TfBugHistory::getBugId, bugId);
        bugHistoryMapper.delete(historyWrapper);

        boolean deleted = this.removeById(bugId);
        if (deleted) {
            logger.info("删除缺陷成功: bugId={}", bugId);
        } else {
            logger.error("删除缺陷失败: bugId={}", bugId);
        }
        return deleted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteBugs(List<String> bugIds) {
        Assert.notEmpty(bugIds, "缺陷ID列表不能为空");
        for (String bugId : bugIds) {
            deleteBug(bugId);
        }
        return true;
    }

    @Override
    public List<TfBug> listBugsForExport(String systemId, String directoryId,
                                        String bugId, String bugName,
                                        String bugState, String bugType,
                                        Integer bugSeverityLevel, String bugSource,
                                        String submitterId, String checkerId, String developerId,
                                        String commitTimeStart, String commitTimeEnd,
                                        String closeTimeStart, String closeTimeEnd) {
        // 获取目录及其所有子目录的ID列表（包含当前目录）
        List<String> directoryIds = fetchDirectoryHierarchy(directoryId, systemId);
        List<TfBug> bugs = this.baseMapper.selectBugsForExport(systemId, directoryIds,
                bugId, bugName, bugState, bugType, bugSeverityLevel, bugSource,
                submitterId, checkerId, developerId,
                commitTimeStart, commitTimeEnd, closeTimeStart, closeTimeEnd);

        // 填充字典名称和测试集路径
        for (TfBug bug : bugs) {
            fillDictionaryNames(bug);
            fillFullPath(bug);
        }
        return bugs;
    }

    @Override
    public void exportBugsToExcel(List<TfBug> bugs, HttpServletResponse response) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("缺陷列表");
        String[] headers = {"编号", "标题", "状态", "所属系统", "提交人", "开发组长", "责任人", "验证人",
                "用例模块", "测试集目录", "严重级别", "缺陷来源", "提交时间", "解决时间"};

        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIndex = 1;
        DataFormat format = workbook.createDataFormat();
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(format.getFormat("yyyy-MM-dd HH:mm:ss"));

        for (TfBug bug : bugs) {
            Row row = sheet.createRow(rowIndex++);
            // 编号
            row.createCell(0).setCellValue(bug.getBugId() != null ? bug.getBugId().toString() : "");
            // 标题
            row.createCell(1).setCellValue(safeToString(bug.getBugName()));
            // 状态
            row.createCell(2).setCellValue(safeToString(bug.getBugStateName()));
            // 所属系统
            row.createCell(3).setCellValue(safeToString(bug.getSystemId()));
            // 提交人
            row.createCell(4).setCellValue(safeToString(bug.getSubmitterName()));
            // 开发组长
            row.createCell(5).setCellValue(safeToString(bug.getDevLeaderName()));
            // 责任人（开发人员）
            row.createCell(6).setCellValue(safeToString(bug.getDeveloperName()));
            // 验证人
            row.createCell(7).setCellValue(safeToString(bug.getCheckerName()));
            // 用例模块（目录路径）
            String modulePath = "";
            if (StringUtils.hasText(bug.getDirectoryId())) {
                try {
                    modulePath = testDirectoryService.getDirectoryFullPath(bug.getDirectoryId());
                } catch (Exception e) {
                    logger.warn("获取目录路径失败: directoryId={}", bug.getDirectoryId(), e);
                }
            }
            row.createCell(8).setCellValue(modulePath);
            // 测试集目录（同用例模块）
            row.createCell(9).setCellValue(modulePath);
            // 严重级别
            row.createCell(10).setCellValue(safeToString(bug.getBugSeverityLevelName()));
            // 缺陷来源
            row.createCell(11).setCellValue(safeToString(bug.getBugSourceName()));
            // 提交时间
            Cell commitTimeCell = row.createCell(12);
            if (bug.getCommitTime() != null) {
                commitTimeCell.setCellValue(bug.getCommitTime());
                commitTimeCell.setCellStyle(dateStyle);
            } else {
                commitTimeCell.setCellValue("");
            }
            // 解决时间
            Cell resolvedTimeCell = row.createCell(13);
            if (bug.getResolvedTime() != null) {
                resolvedTimeCell.setCellValue(bug.getResolvedTime());
                resolvedTimeCell.setCellStyle(dateStyle);
            } else {
                resolvedTimeCell.setCellValue("");
            }
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("bug_data.xlsx", "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("导出缺陷数据失败: " + e.getMessage(), e);
        } finally {
            try {
                workbook.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public Map<String, Object> getBugHistory(String bugId, int pageNo, int pageSize) {
        Assert.hasText(bugId, "缺陷ID不能为空");
        Page<TfBugHistory> page = new Page<>(pageNo, pageSize);
        Page<TfBugHistory> result = bugHistoryMapper.selectPageBugHistoryList(page, bugId);

        // 填充字典名称
        for (TfBugHistory history : result.getRecords()) {
            if (StringUtils.hasText(history.getNewState()) && !StringUtils.hasText(history.getNewStateName())) {
                String newStateName = dataDictionaryService.getDataNameByTypeAndValue("bugState", history.getNewState());
                history.setNewStateName(newStateName);
            }
            if (StringUtils.hasText(history.getOldState()) && !StringUtils.hasText(history.getOldStateName())) {
                String oldStateName = dataDictionaryService.getDataNameByTypeAndValue("bugState", history.getOldState());
                history.setOldStateName(oldStateName);
            }
        }

        Map<String, Object> data = new HashMap<>(8);
        data.put("rows", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pageNo", result.getCurrent());
        data.put("pageSize", result.getSize());
        data.put("totalPages", result.getPages());
        return data;
    }

    /**
     * 填充测试集路径
     */
    private void fillFullPath(TfBug bug) {
        if (StringUtils.hasText(bug.getDirectoryId())) {
            try {
                String fullPath = testDirectoryService.getDirectoryFullPath(bug.getDirectoryId());
                bug.setFullPath(fullPath);
            } catch (Exception e) {
                logger.warn("获取测试集路径失败: directoryId={}", bug.getDirectoryId(), e);
                bug.setFullPath("");
            }
        }
    }

    /**
     * 填充字典名称
     */
    private void fillDictionaryNames(TfBug bug) {
        if (StringUtils.hasText(bug.getBugState()) && !StringUtils.hasText(bug.getBugStateName())) {
            String bugStateName = dataDictionaryService.getDataNameByTypeAndValue("bugState", bug.getBugState());
            bug.setBugStateName(bugStateName);
        }
        if (StringUtils.hasText(bug.getBugSource()) && !StringUtils.hasText(bug.getBugSourceName())) {
            String bugSourceName = dataDictionaryService.getDataNameByTypeAndValue("bugSource", bug.getBugSource());
            bug.setBugSourceName(bugSourceName);
        }
        if (StringUtils.hasText(bug.getPrority()) && !StringUtils.hasText(bug.getProrityName())) {
            String prorityName = dataDictionaryService.getDataNameByTypeAndValue("prority", bug.getPrority());
            bug.setProrityName(prorityName);
        }
        if (bug.getBugSeverityLevel() != null && !StringUtils.hasText(bug.getBugSeverityLevelName())) {
            String bugSeverityLevelName = dataDictionaryService.getDataNameByTypeAndValue("bugSeverityLevel", bug.getBugSeverityLevel().toString());
            bug.setBugSeverityLevelName(bugSeverityLevelName);
        }
        if (StringUtils.hasText(bug.getBugType()) && !StringUtils.hasText(bug.getBugTypeName())) {
            String bugTypeName = dataDictionaryService.getDataNameByTypeAndValue("bugType", bug.getBugType());
            bug.setBugTypeName(bugTypeName);
        }
    }

    /**
     * 记录缺陷历史
     */
    private void recordBugHistory(String bugId, String oldState, String newState, String comment, String operatorId, String systemId) {
        TfBugHistory history = new TfBugHistory();
        history.setBugId(bugId);
        history.setOldState(oldState);
        history.setNewState(newState);
        history.setComment(comment);
        history.setOperatorId(operatorId);
        history.setSystemId(systemId);
        history.setOperatingTime(LocalDateTime.now());
        history.setLastOperationTime(LocalDateTime.now());
        bugHistoryMapper.insert(history);
    }

    /**
     * 获取目录及其所有子目录的ID列表
     */
    private List<String> fetchDirectoryHierarchy(String directoryId, String systemId) {
        if (!StringUtils.hasText(directoryId)) {
            return null;
        }
        // 调用目录服务获取当前目录及其所有子目录的ID列表
        List<String> ids = testDirectoryService.getAllChildrenDirectoryIds(directoryId, systemId);
        if (ids == null || ids.isEmpty()) {
            List<String> result = new ArrayList<>();
            result.add(directoryId);
            return result;
        }
        logger.debug("查询目录层级: directoryId={}, systemId={}, 包含的目录数量={}", directoryId, systemId, ids.size());
        return ids;
    }

    @Override
    public Map<String, Object> getRelatedBugs(int pageNo, int pageSize, String usecaseId, String directoryId) {
        Page<TfBug> page = new Page<>(pageNo, pageSize);
        
        // 直接使用String类型的directoryId进行查询
        // 数据库表中DIRECTORY_ID是VARCHAR类型，支持UUID格式
        Page<TfBug> result = this.baseMapper.selectPageRelatedBugs(page, usecaseId, directoryId);

        // 填充字典名称和测试集路径（如果 SQL 关联失败，手动查询数据字典）
        for (TfBug bug : result.getRecords()) {
            fillDictionaryNames(bug);
            fillFullPath(bug);
        }

        Map<String, Object> data = new HashMap<>(8);
        data.put("rows", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pageNo", result.getCurrent());
        data.put("pageSize", result.getSize());
        data.put("totalPages", result.getPages());
        return data;
    }

    /**
     * 安全转换为字符串
     */
    private String safeToString(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    /**
     * 生成缺陷ID
     * @param systemId 系统ID
     * @return 缺陷ID，格式：systemId-baseCode 或 baseCode
     */
    private String generateBugId(String systemId) {
        String base = counterUtil.generateNextCode("bugCode");
        if (StringUtils.hasText(systemId)) {
            return systemId + "-" + base;
        }
        return base;
    }

    @Override
    public List<TfBugState> getAllBugStates() {
        try {
            List<TfBugState> states = bugStateMapper.selectAllBugStates();
            logger.info("查询所有缺陷状态，共 {} 条", states != null ? states.size() : 0);
            return states;
        } catch (Exception e) {
            logger.error("查询所有缺陷状态失败", e);
            throw new RuntimeException("查询所有缺陷状态失败: " + e.getMessage(), e);
        }
    }
}
