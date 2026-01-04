package com.king.test.usecaseManage.usecaseRepository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.utils.CounterUtil;
import com.king.common.utils.SecurityUtils;
import com.king.framework.dataDictionary.service.IDataDictionaryService;
import com.king.sys.user.service.IUserService;
import com.king.test.baseManage.testDirectory.entity.TTestDirectory;
import com.king.test.baseManage.testDirectory.service.ITestDirectoryService;
import com.king.test.usecaseManage.requireRepository.entity.TfRequirepoint;
import com.king.test.usecaseManage.usecaseRepository.entity.TfUsecase;
import com.king.test.usecaseManage.usecaseRepository.entity.TfUsecaseHistory;
import com.king.test.usecaseManage.usecaseRepository.mapper.TfUsecaseHistoryMapper;
import com.king.test.usecaseManage.usecaseRepository.mapper.UsecaseRepositoryMapper;
import com.king.test.usecaseManage.usecaseRepository.service.ITfUsecaseService;
import com.king.test.usecaseManage.usecaseRequireLink.entity.TfUsecaseRequire;
import com.king.test.usecaseManage.usecaseRequireLink.mapper.TfUsecaseRequireMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service("tfUsecaseServiceImpl")
public class TfUsecaseServiceImpl extends ServiceImpl<UsecaseRepositoryMapper, TfUsecase> implements ITfUsecaseService {

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private CounterUtil counterUtil;

    @Autowired
    @Qualifier("testDirectoryServiceImpl")
    private ITestDirectoryService testDirectoryService;

    @Autowired
    @Qualifier("dataDictionaryServiceImpl")
    private IDataDictionaryService dataDictionaryService;

    @Autowired
    @Qualifier("userServiceImpl")
    private IUserService userService;

    @Autowired
    private TfUsecaseHistoryMapper usecaseHistoryMapper;

    @Autowired
    private TfUsecaseRequireMapper usecaseRequireMapper;

    private static final String TEMPLATE_PATH = "templates/UsecaseTemplate.xlsx";

    @Override
    public Map<String, Object> getUsecasePage(int pageNo,
                                              int pageSize,
                                              String systemId,
                                              String directoryId,
                                              String usecaseName,
                                              String usecaseType,
                                              String usecaseNature,
                                              String prority,
                                              String isSmokeTest,
                                              String creatorId) {
        Page<TfUsecase> page = new Page<>(pageNo, pageSize);
        List<String> directoryIds = fetchDirectoryHierarchy(directoryId);
        Page<TfUsecase> result = this.baseMapper.selectPageUsecases(page, systemId, directoryIds, usecaseName, usecaseType, usecaseNature, prority, isSmokeTest, creatorId);

        Map<String, Object> data = new HashMap<>(8);
        data.put("rows", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pageNo", result.getCurrent());
        data.put("pageSize", result.getSize());
        data.put("totalPages", result.getPages());
        return data;
    }

    @Override
    public TfUsecase getUsecaseDetail(String usecaseId) {
        Assert.hasText(usecaseId, "用例ID不能为空");
        TfUsecase usecase = this.baseMapper.selectUsecaseDetailById(usecaseId);
        if (usecase == null) {
            throw new IllegalArgumentException("用例不存在");
        }
        return usecase;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUsecase(TfUsecase usecase) {
        Assert.notNull(usecase, "用例信息不能为空");
        Assert.hasText(usecase.getSystemId(), "系统ID不能为空");
        Assert.hasText(usecase.getUsecaseName(), "用例名称不能为空");

        if (!StringUtils.hasText(usecase.getUsecaseId())) {
            usecase.setUsecaseId(generateUsecaseId(usecase.getSystemId()));
        }

        if (!StringUtils.hasText(usecase.getCreatorId())) {
            String currentUser = securityUtils.getUserId();
            if (StringUtils.hasText(currentUser)) {
                usecase.setCreatorId(currentUser);
            }
        }

        usecase.setCreateTime(Optional.ofNullable(usecase.getCreateTime()).orElse(LocalDateTime.now()));
        usecase.setModifyTime(usecase.getCreateTime());
        if (!StringUtils.hasText(usecase.getModifierId())) {
            usecase.setModifierId(usecase.getCreatorId());
        }

        boolean saved = this.save(usecase);
        if (saved) {
            recordUsecaseHistory(usecase.getUsecaseId(), "CREATE", null, "创建用例", usecase.getCreatorId());
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUsecase(TfUsecase usecase) {
        Assert.notNull(usecase, "用例信息不能为空");
        Assert.hasText(usecase.getUsecaseId(), "用例ID不能为空");
        Assert.hasText(usecase.getUsecaseName(), "用例名称不能为空");

        TfUsecase existing = this.getById(usecase.getUsecaseId());
        if (existing == null) {
            throw new IllegalArgumentException("用例不存在");
        }

        String currentUser = securityUtils.getUserId();
        if (StringUtils.hasText(currentUser)) {
            usecase.setModifierId(currentUser);
        }
        usecase.setModifyTime(LocalDateTime.now());

        boolean updated = this.updateById(usecase);
        if (updated) {
            String description = buildChangeDescription(existing, usecase);
            recordUsecaseHistory(usecase.getUsecaseId(), "UPDATE", existing.getLatestExeStatus(), description, usecase.getModifierId());
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUsecase(String usecaseId) {
        Assert.hasText(usecaseId, "用例ID不能为空");
        TfUsecase usecase = this.getById(usecaseId);
        if (usecase == null) {
            throw new IllegalArgumentException("用例不存在");
        }

        usecaseRequireMapper.delete(new LambdaQueryWrapper<TfUsecaseRequire>().eq(TfUsecaseRequire::getUsecaseId, usecaseId));
        usecaseHistoryMapper.delete(new LambdaQueryWrapper<TfUsecaseHistory>().eq(TfUsecaseHistory::getUsecaseId, usecaseId));
        return this.removeById(usecaseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteUsecases(List<String> usecaseIds) {
        Assert.notEmpty(usecaseIds, "用例ID列表不能为空");
        for (String usecaseId : usecaseIds) {
            deleteUsecase(usecaseId);
        }
        return true;
    }

    @Override
    public List<TfUsecase> listUsecasesForExport(String systemId,
                                                 String directoryId,
                                                 String usecaseName,
                                                 String usecaseType,
                                                 String usecaseNature,
                                                 String prority,
                                                 String isSmokeTest,
                                                 String creatorId) {
        List<String> directoryIds = fetchDirectoryHierarchy(directoryId);
        return this.baseMapper.selectUsecasesForExport(systemId, directoryIds, usecaseName, usecaseType, usecaseNature, prority, isSmokeTest, creatorId);
    }

    @Override
    public void exportUsecasesToExcel(List<TfUsecase> usecases, HttpServletResponse response) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Usecases");
        String[] headers = {"用例ID", "用例名称", "系统ID", "目录ID", "用例类型", "测试要点", "用例性质", "优先级",
                "是否冒烟", "创建人", "创建时间", "修改人", "修改时间"};

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowIndex = 1;
        DataFormat format = workbook.createDataFormat();
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(format.getFormat("yyyy-MM-dd HH:mm:ss"));

        for (TfUsecase usecase : usecases) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(Optional.ofNullable(usecase.getUsecaseId()).orElse(""));
            row.createCell(1).setCellValue(Optional.ofNullable(usecase.getUsecaseName()).orElse(""));
            row.createCell(2).setCellValue(Optional.ofNullable(usecase.getSystemId()).orElse(""));
            row.createCell(3).setCellValue(Optional.ofNullable(usecase.getDirectoryId()).orElse(""));
            row.createCell(4).setCellValue(Optional.ofNullable(usecase.getUsecaseTypeName()).orElse(Optional.ofNullable(usecase.getUsecaseType()).orElse("")));
            row.createCell(5).setCellValue(Optional.ofNullable(usecase.getTestPointName()).orElse(Optional.ofNullable(usecase.getTestPoint()).orElse("")));
            row.createCell(6).setCellValue(Optional.ofNullable(usecase.getUsecaseNatureName()).orElse(Optional.ofNullable(usecase.getUsecaseNature()).orElse("")));
            row.createCell(7).setCellValue(Optional.ofNullable(usecase.getProrityName()).orElse(Optional.ofNullable(usecase.getPrority()).orElse("")));
            row.createCell(8).setCellValue(Optional.ofNullable(usecase.getIsSmokeTest()).orElse(""));
            row.createCell(9).setCellValue(Optional.ofNullable(usecase.getCreator()).orElse(Optional.ofNullable(usecase.getCreatorId()).orElse("")));
            setDateCell(row, 10, usecase.getCreateTime(), dateStyle);
            row.createCell(11).setCellValue(Optional.ofNullable(usecase.getModifier()).orElse(Optional.ofNullable(usecase.getModifierId()).orElse("")));
            setDateCell(row, 12, usecase.getModifyTime(), dateStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("usecase_data.xlsx", "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("导出用例数据失败: " + e.getMessage(), e);
        } finally {
            try {
                workbook.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) {
        try (InputStream inputStream = Objects.requireNonNull(getClass().getClassLoader()).getResourceAsStream(TEMPLATE_PATH)) {
            if (inputStream == null) {
                throw new IllegalStateException("模板文件不存在: " + TEMPLATE_PATH);
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=UsecaseTemplate.xlsx");
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, len);
            }
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException("下载模板失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importUsecases(MultipartFile file, String systemId, String directoryId) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        List<String> errors = new ArrayList<>();
        List<TfUsecase> usecases = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new IllegalArgumentException("Excel中缺少工作表");
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel缺少表头");
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }
                try {
                    TfUsecase usecase = parseRowToUsecase(row, headerRow, systemId, directoryId);
                    usecases.add(usecase);
                } catch (Exception ex) {
                    errors.add(String.format("第%d行导入失败: %s", i + 1, ex.getMessage()));
                }
            }
        }

        int successCount = 0;
        for (TfUsecase usecase : usecases) {
            try {
                if (this.save(usecase)) {
                    successCount++;
                    recordUsecaseHistory(usecase.getUsecaseId(), "IMPORT", null, "导入用例", usecase.getCreatorId());
                }
            } catch (Exception e) {
                errors.add(String.format("用例[%s]保存失败: %s", Optional.ofNullable(usecase.getUsecaseName()).orElse("未知"), e.getMessage()));
            }
        }

        Map<String, Object> result = new HashMap<>(8);
        result.put("successCount", successCount);
        result.put("failCount", errors.size());
        result.put("errors", errors);
        return result;
    }

    @Override
    public Map<String, Object> getUsecaseStatistics(String systemId, String directoryId) {
        List<String> directoryIds = fetchDirectoryHierarchy(directoryId);
        List<Map<String, Object>> rows = this.baseMapper.selectUsecaseStatistics(systemId, directoryIds);
        long total = rows.stream().mapToLong(row -> ((Number) row.getOrDefault("count", 0)).longValue()).sum();

        Map<String, Object> result = new HashMap<>(8);
        result.put("total", total);
        result.put("items", rows);
        return result;
    }

    @Override
    public List<Map<String, Object>> getUsecaseTypeStatistics(String systemId, String directoryId) {
        List<String> directoryIds = fetchDirectoryHierarchy(directoryId);
        return this.baseMapper.selectUsecaseTypeStatistics(systemId, directoryIds);
    }

    @Override
    public List<Map<String, Object>> getUsecaseStatusStatistics(String systemId, String directoryId) {
        List<String> directoryIds = fetchDirectoryHierarchy(directoryId);
        return this.baseMapper.selectUsecaseStatusStatistics(systemId, directoryIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TfUsecase copyUsecase(String usecaseId, Map<String, Object> options) {
        TfUsecase source = this.getById(usecaseId);
        if (source == null) {
            throw new IllegalArgumentException("待复制的用例不存在");
        }

        TfUsecase target = new TfUsecase();
        BeanUtils.copyProperties(source, target);
        target.setUsecaseId(generateUsecaseId(source.getSystemId()));
        target.setUsecaseName((String) options.getOrDefault("usecaseName", source.getUsecaseName() + "-复制"));
        String targetDirectoryId = (String) options.get("directoryId");
        if (StringUtils.hasText(targetDirectoryId)) {
            target.setDirectoryId(targetDirectoryId);
        }

        String currentUser = securityUtils.getUserId();
        target.setCreatorId(currentUser);
        target.setModifierId(currentUser);
        target.setCreateTime(LocalDateTime.now());
        target.setModifyTime(LocalDateTime.now());

        this.save(target);
        recordUsecaseHistory(target.getUsecaseId(), "COPY", null, "复制自用例:" + usecaseId, currentUser);

        boolean copyLinks = Boolean.TRUE.equals(options.get("copyRequirePoints"));
        if (copyLinks) {
            List<String> requirePointIds = usecaseRequireMapper.selectRequirePointIdsByUsecaseId(usecaseId);
            linkRequirePoints(target.getUsecaseId(), requirePointIds);
        }

        return target;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean moveUsecases(List<String> usecaseIds, String targetDirectoryId) {
        Assert.notEmpty(usecaseIds, "用例ID列表不能为空");
        Assert.hasText(targetDirectoryId, "目标目录不能为空");

        for (String usecaseId : usecaseIds) {
            TfUsecase usecase = this.getById(usecaseId);
            if (usecase != null) {
                usecase.setDirectoryId(targetDirectoryId);
                usecase.setModifyTime(LocalDateTime.now());
                usecase.setModifierId(securityUtils.getUserId());
                this.updateById(usecase);
            }
        }
        return true;
    }

    @Override
    public Map<String, Object> getUsecaseHistory(String usecaseId, int pageNo, int pageSize) {
        Page<TfUsecaseHistory> page = new Page<>(pageNo, pageSize);
        Page<TfUsecaseHistory> result = usecaseHistoryMapper.selectHistoryPage(page, usecaseId);
        Map<String, Object> data = new HashMap<>(8);
        data.put("rows", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pageNo", result.getCurrent());
        data.put("pageSize", result.getSize());
        data.put("totalPages", result.getPages());
        return data;
    }

    @Override
    public List<TfUsecaseHistory> listUsecaseHistory(String usecaseId) {
        return usecaseHistoryMapper.selectHistoryList(usecaseId);
    }

    @Override
    public List<TfRequirepoint> getLinkedRequirePoints(String usecaseId) {
        return usecaseRequireMapper.selectRequirePointsByUsecaseId(usecaseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean linkRequirePoints(String usecaseId, List<String> requirePointIds) {
        if (!StringUtils.hasText(usecaseId) || CollectionUtils.isEmpty(requirePointIds)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        List<String> existing = usecaseRequireMapper.selectRequirePointIdsByUsecaseId(usecaseId);
        List<String> toInsert = requirePointIds.stream()
                .filter(id -> !existing.contains(id))
                .collect(Collectors.toList());
        if (toInsert.isEmpty()) {
            return true;
        }
        for (String requirePointId : toInsert) {
            TfUsecaseRequire link = new TfUsecaseRequire();
            link.setUsecaseId(usecaseId);
            link.setRequirePointId(requirePointId);
            usecaseRequireMapper.insert(link);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlinkRequirePoints(String usecaseId, List<String> requirePointIds) {
        if (!StringUtils.hasText(usecaseId) || CollectionUtils.isEmpty(requirePointIds)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        usecaseRequireMapper.deleteByUsecaseIdAndRequirePointIds(usecaseId, requirePointIds);
        return true;
    }

    private List<String> fetchDirectoryHierarchy(String directoryId) {
        if (!StringUtils.hasText(directoryId)) {
            return null;
        }
        List<String> ids = new ArrayList<>();
        ids.add(directoryId);
        collectChildrenDirectoryIds(directoryId, ids);
        return ids;
    }

    private void collectChildrenDirectoryIds(String parentId, List<String> collector) {
        List<TTestDirectory> children = testDirectoryService.list(new LambdaQueryWrapper<TTestDirectory>()
                .eq(TTestDirectory::getDirectoryParentId, parentId));
        for (TTestDirectory child : children) {
            collector.add(child.getDirectoryId());
            collectChildrenDirectoryIds(child.getDirectoryId(), collector);
        }
    }

    private String generateUsecaseId(String systemId) {
        String base = counterUtil.generateNextCode("usecaseCode");
        if (StringUtils.hasText(systemId)) {
            return systemId + "-" + base;
        }
        return base;
    }

    private void recordUsecaseHistory(String usecaseId, String action, String oldState, String content, String operatorId) {
        TfUsecaseHistory history = new TfUsecaseHistory();
        history.setUsecaseHistoryId(UUID.randomUUID().toString().replaceAll("-", ""));
        history.setUsecaseId(usecaseId);
        history.setOperatingTime(LocalDateTime.now());
        history.setOperatorId(operatorId);
        history.setModifiedContent(String.format("[%s] %s", action, content));
        usecaseHistoryMapper.insert(history);
    }

    private void setDateCell(Row row, int index, LocalDateTime dateTime, CellStyle style) {
        if (dateTime == null) {
            row.createCell(index).setCellValue("");
            return;
        }
        Cell cell = row.createCell(index);
        cell.setCellStyle(style);
        cell.setCellValue(java.sql.Timestamp.valueOf(dateTime));
    }

    private boolean isRowEmpty(Row row) {
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValue(cell);
                if (StringUtils.hasText(value)) {
                    return false;
                }
            }
        }
        return true;
    }

    private TfUsecase parseRowToUsecase(Row row, Row headerRow, String systemId, String directoryId) {
        TfUsecase usecase = new TfUsecase();
        usecase.setUsecaseId(generateUsecaseId(systemId));
        usecase.setSystemId(systemId);
        usecase.setDirectoryId(directoryId);
        usecase.setUsecaseName(getCellValueByHeader(row, headerRow, "用例名称"));
        if (!StringUtils.hasText(usecase.getUsecaseName())) {
            throw new IllegalArgumentException("用例名称不能为空");
        }
        usecase.setUsecaseType(resolveDictionaryValue("usecaseType", getCellValueByHeader(row, headerRow, "用例类型")));
        usecase.setTestPoint(resolveDictionaryValue("testPoint", getCellValueByHeader(row, headerRow, "测试要点")));
        usecase.setUsecaseNature(resolveDictionaryValue("usecaseNature", getCellValueByHeader(row, headerRow, "用例性质")));
        usecase.setPrority(resolveDictionaryValue("prority", getCellValueByHeader(row, headerRow, "优先级")));
        usecase.setIsSmokeTest(getCellValueByHeader(row, headerRow, "是否冒烟"));
        usecase.setPrecondition(getCellValueByHeader(row, headerRow, "前置条件"));
        usecase.setTestData(getCellValueByHeader(row, headerRow, "测试数据"));
        usecase.setTestStep(getCellValueByHeader(row, headerRow, "测试步骤"));
        usecase.setExpectedResult(getCellValueByHeader(row, headerRow, "预期结果"));

        String creatorName = getCellValueByHeader(row, headerRow, "创建人");
        if (StringUtils.hasText(creatorName)) {
            String creatorId = userService.getUserIdByUserName(creatorName);
            usecase.setCreatorId(creatorId);
        } else {
            usecase.setCreatorId(securityUtils.getUserId());
        }
        usecase.setCreateTime(LocalDateTime.now());
        usecase.setModifyTime(LocalDateTime.now());
        usecase.setModifierId(usecase.getCreatorId());

        return usecase;
    }

    private String resolveDictionaryValue(String type, String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        String value = dataDictionaryService.getDataValueByTypeAndName(type, name);
        return value != null ? value : name;
    }

    private String getCellValueByHeader(Row row, Row headerRow, String headerName) {
        if (row == null || headerRow == null) {
            return null;
        }
        for (int i = headerRow.getFirstCellNum(); i < headerRow.getLastCellNum(); i++) {
            Cell headerCell = headerRow.getCell(i);
            if (headerCell != null && headerName.equalsIgnoreCase(headerCell.getStringCellValue().trim())) {
                Cell cell = row.getCell(i);
                return getCellValue(cell);
            }
        }
        return null;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                return BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private String buildChangeDescription(TfUsecase original, TfUsecase updated) {
        List<String> changes = new ArrayList<>();
        if (!Objects.equals(original.getUsecaseName(), updated.getUsecaseName())) {
            changes.add(String.format("用例名称: %s -> %s", original.getUsecaseName(), updated.getUsecaseName()));
        }
        if (!Objects.equals(original.getDirectoryId(), updated.getDirectoryId())) {
            changes.add(String.format("目录: %s -> %s", original.getDirectoryId(), updated.getDirectoryId()));
        }
        if (!Objects.equals(original.getUsecaseType(), updated.getUsecaseType())) {
            changes.add(String.format("用例类型: %s -> %s", original.getUsecaseType(), updated.getUsecaseType()));
        }
        if (!Objects.equals(original.getPrority(), updated.getPrority())) {
            changes.add(String.format("优先级: %s -> %s", original.getPrority(), updated.getPrority()));
        }
        if (!Objects.equals(original.getLatestExeStatus(), updated.getLatestExeStatus())) {
            changes.add(String.format("执行状态: %s -> %s", original.getLatestExeStatus(), updated.getLatestExeStatus()));
        }
        if (changes.isEmpty()) {
            return "修改用例";
        }
        return String.join("; ", changes);
    }
}
