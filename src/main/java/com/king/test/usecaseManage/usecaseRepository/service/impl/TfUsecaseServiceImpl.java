package com.king.test.usecaseManage.usecaseRepository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.utils.CounterUtil;
import com.king.common.utils.SecurityUtils;
import com.king.framework.dataDictionary.service.IDataDictionaryService;
import com.king.sys.user.service.IUserService;
import com.king.test.baseManage.testDirectory.service.ITestDirectoryService;
import com.king.test.usecaseManage.requireRepository.entity.TfRequirepoint;
import com.king.test.usecaseManage.requireRepository.service.ITfRequirepointService;
import com.king.test.usecaseManage.usecaseRepository.entity.TfUsecase;
import com.king.test.usecaseManage.usecaseRepository.entity.TfUsecaseHistory;
import com.king.test.usecaseManage.usecaseRepository.mapper.TfUsecaseHistoryMapper;
import com.king.test.usecaseManage.usecaseRepository.mapper.UsecaseRepositoryMapper;
import com.king.test.usecaseManage.usecaseRepository.service.ITfUsecaseService;
import com.king.test.usecaseManage.usecaseRequireLink.entity.TfUsecaseRequire;
import com.king.test.usecaseManage.usecaseRequireLink.mapper.TfUsecaseRequireMapper;
import com.king.test.usecaseManage.usecaseRequireLink.service.IUsecaseRequireLinkService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service("tfUsecaseServiceImpl")
public class TfUsecaseServiceImpl extends ServiceImpl<UsecaseRepositoryMapper, TfUsecase> implements ITfUsecaseService {

    private static final Logger logger = LoggerFactory.getLogger(TfUsecaseServiceImpl.class);

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
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private TfUsecaseHistoryMapper usecaseHistoryMapper;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private TfUsecaseRequireMapper usecaseRequireMapper;

    @Autowired
    @Qualifier("usecaseRequireLinkServiceImpl")
    private IUsecaseRequireLinkService usecaseRequireLinkService;

    @Autowired
    @Qualifier("tfRequirepointServiceImpl")
    private ITfRequirepointService requirePointService;

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
        // 获取目录及其所有子目录的ID列表（包含当前目录）
        List<String> directoryIds = fetchDirectoryHierarchy(directoryId, systemId);
        Page<TfUsecase> result = this.baseMapper.selectPageUsecases(page, systemId, directoryIds, usecaseName, usecaseType, usecaseNature, prority, isSmokeTest, creatorId);

        // 填充 prorityName（如果 SQL 关联失败，手动查询数据字典）
        for (TfUsecase usecase : result.getRecords()) {
            if (StringUtils.hasText(usecase.getPrority()) && !StringUtils.hasText(usecase.getProrityName())) {
                String prorityName = dataDictionaryService.getDataNameByTypeAndValue("prority", usecase.getPrority());
                usecase.setProrityName(prorityName);
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

    @Override
    public TfUsecase getUsecaseDetail(String usecaseId) {
        Assert.hasText(usecaseId, "用例ID不能为空");
        TfUsecase usecase = this.baseMapper.selectUsecaseDetailById(usecaseId);
        if (usecase == null) {
            throw new IllegalArgumentException("用例不存在");
        }

        // 填充 prorityName（如果 SQL 关联失败，手动查询数据字典）
        if (StringUtils.hasText(usecase.getPrority()) && !StringUtils.hasText(usecase.getProrityName())) {
            String prorityName = dataDictionaryService.getDataNameByTypeAndValue("prority", usecase.getPrority());
            usecase.setProrityName(prorityName);
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
        List<String> directoryIds = fetchDirectoryHierarchy(directoryId, systemId);
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
        try {
            // 从classpath读取模板文件
            Resource resource = new ClassPathResource(TEMPLATE_PATH);

            if (!resource.exists()) {
                throw new IllegalStateException("模板文件不存在: " + TEMPLATE_PATH);
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            // 对文件名进行URL编码，支持中文
            String fileName = "UsecaseTemplate.xlsx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

            // 读取文件并写入响应流
            try (InputStream inputStream = resource.getInputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    response.getOutputStream().write(buffer, 0, bytesRead);
                }
                response.getOutputStream().flush();
            }

            logger.info("用例模板下载成功: {}", TEMPLATE_PATH);

        } catch (IOException e) {
            logger.error("下载用例模板失败: {}", e.getMessage(), e);
            throw new RuntimeException("下载模板失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importUsecases(MultipartFile file, String systemId, String directoryId) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        if (!StringUtils.hasText(systemId)) {
            throw new IllegalArgumentException("系统ID不能为空");
        }

        List<String> errors = new ArrayList<>();
        List<TfUsecase> usecases = new ArrayList<>();

        logger.info("开始导入用例: systemId={}, directoryId={}, fileName={}", systemId, directoryId, file.getOriginalFilename());

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
                    // 解析用例信息，同时获取关联测试需求编号
                    String requirePointId = getCellValueByHeader(row, headerRow, "关联测试需求编号");
                    // 一个用例只对应一个需求点，如果存在多个需求点编号（用逗号或分号分隔），只取第一个
                    String firstRequirePointId = null;
                    if (StringUtils.hasText(requirePointId)) {
                        String trimmedId = requirePointId.trim();
                        String[] requirePointIds = trimmedId.split("[,;，；]");
                        firstRequirePointId = requirePointIds[0].trim();

                        // 如果存在多个需求点编号，记录警告
                        if (requirePointIds.length > 1) {
                            String warnMsg = String.format("第%d行: 一个用例只能关联一个需求点，已自动取第一个需求点编号: %s，忽略其他编号",
                                    i + 1, firstRequirePointId);
                            logger.warn(warnMsg);
                            errors.add(warnMsg);
                        }
                    }

                    // 解析用例信息，并在parseRowToUsecase中检查需求点是否存在
                    TfUsecase usecase = parseRowToUsecase(row, headerRow, systemId, directoryId, firstRequirePointId, i + 1, errors);
                    usecases.add(usecase);
                } catch (Exception ex) {
                    errors.add(String.format("第%d行导入失败: %s", i + 1, ex.getMessage()));
                }
            }
        }

        // 验证所有用例，只要有一条验证不通过，则均不保存
        for (int i = 0; i < usecases.size(); i++) {
            TfUsecase usecase = usecases.get(i);
            // 验证必需字段
            if (!StringUtils.hasText(usecase.getUsecaseId())) {
                String errorMsg = String.format("第%d行: 用例ID不能为空", i + 2); // i+2 因为第一行是表头，从第二行开始
                errors.add(errorMsg);
                logger.error("用例验证失败: {}", errorMsg);
            }
            if (!StringUtils.hasText(usecase.getSystemId())) {
                String errorMsg = String.format("第%d行: 系统ID不能为空", i + 2);
                errors.add(errorMsg);
                logger.error("用例验证失败: {}", errorMsg);
            }
            if (!StringUtils.hasText(usecase.getUsecaseName())) {
                String errorMsg = String.format("第%d行: 用例名称不能为空", i + 2);
                errors.add(errorMsg);
                logger.error("用例验证失败: {}", errorMsg);
            }
        }

        // 如果有任何验证错误，抛出异常，不保存任何数据
        if (!errors.isEmpty()) {
            String errorSummary = String.format("验证失败，共%d条错误，所有数据均不保存", errors.size());
            logger.error("用例导入验证失败: {}", errorSummary);
            throw new IllegalArgumentException(errorSummary + "。错误详情: " + String.join("; ", errors));
        }

        // 所有验证通过，批量保存所有用例
        int successCount = 0;
        try {
            // 使用批量保存
            boolean batchSaved = this.saveBatch(usecases);
            if (batchSaved) {
                successCount = usecases.size();
                logger.info("批量保存用例成功: 数量={}", successCount);

                // 批量记录历史
                for (TfUsecase usecase : usecases) {
                    try {
                        recordUsecaseHistory(usecase.getUsecaseId(), "IMPORT", null, "导入用例", usecase.getCreatorId());
                    } catch (Exception e) {
                        logger.warn("记录用例历史失败: usecaseId={}, error={}", usecase.getUsecaseId(), e.getMessage());
                        // 历史记录失败不影响主流程
                    }
                }

                // 关联用例和需求点（一个用例只对应一个需求点）
                for (TfUsecase usecase : usecases) {
                    String requirePointId = usecase.getRequirePointId();
                    if (requirePointId != null && !requirePointId.isEmpty()) {
                        try {
                            logger.info("开始关联用例和需求点: usecaseId={}, requirePointId={}", usecase.getUsecaseId(), requirePointId);
                            boolean linked = usecaseRequireLinkService.linkUsecaseToRequirePoint(usecase.getUsecaseId(), requirePointId);
                            if (linked) {
                                logger.info("关联用例和需求点成功: usecaseId={}, requirePointId={}", usecase.getUsecaseId(), requirePointId);
                            } else {
                                String errorMsg = String.format("关联用例和需求点失败: usecaseId=%s, requirePointId=%s，请检查需求点是否存在", usecase.getUsecaseId(), requirePointId);
                                logger.error(errorMsg);
                                errors.add(errorMsg);
                                // 关联失败时记录错误，但不影响主流程（用例已保存）
                            }
                        } catch (Exception e) {
                            String errorMsg = String.format("关联用例和需求点异常: usecaseId=%s, requirePointId=%s, error=%s",
                                    usecase.getUsecaseId(), requirePointId, e.getMessage());
                            logger.error(errorMsg, e);
                            errors.add(errorMsg);
                            // 关联异常时记录错误，但不影响主流程（用例已保存）
                        }
                    } else {
                        logger.debug("用例没有关联的需求点: usecaseId={}", usecase.getUsecaseId());
                    }
                }
            } else {
                String errorMsg = "批量保存用例失败: saveBatch方法返回false";
                errors.add(errorMsg);
                logger.error("批量保存用例失败: 数量={}", usecases.size());
                throw new RuntimeException(errorMsg);
            }
        } catch (Exception e) {
            logger.error("批量保存用例异常: 数量={}, error={}", usecases.size(), e.getMessage(), e);
            // 重新抛出异常以触发事务回滚
            throw new RuntimeException("批量保存用例失败: " + e.getMessage(), e);
        }

        Map<String, Object> result = new HashMap<>(8);
        result.put("successCount", successCount);
        result.put("failCount", errors.size());
        result.put("errors", errors);
        logger.info("用例导入完成: 成功={}, 失败={}, 总行数={}", successCount, errors.size(), usecases.size());
        return result;
    }

    @Override
    public Map<String, Object> getUsecaseStatistics(String systemId, String directoryId) {
        List<String> directoryIds = fetchDirectoryHierarchy(directoryId, systemId);
        List<Map<String, Object>> rows = this.baseMapper.selectUsecaseStatistics(systemId, directoryIds);
        long total = rows.stream().mapToLong(row -> ((Number) row.getOrDefault("count", 0)).longValue()).sum();

        Map<String, Object> result = new HashMap<>(8);
        result.put("total", total);
        result.put("items", rows);
        return result;
    }

    @Override
    public List<Map<String, Object>> getUsecaseTypeStatistics(String systemId, String directoryId) {
        List<String> directoryIds = fetchDirectoryHierarchy(directoryId, systemId);
        return this.baseMapper.selectUsecaseTypeStatistics(systemId, directoryIds);
    }

    @Override
    public List<Map<String, Object>> getUsecaseStatusStatistics(String systemId, String directoryId) {
        List<String> directoryIds = fetchDirectoryHierarchy(directoryId, systemId);
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

    /**
     * 获取目录及其所有子目录的ID列表
     * 当传递了directoryId和systemId时，会递归查询该目录下的所有子目录，
     * 返回包含当前目录及其所有子目录的ID列表，用于查询该目录及其子目录下的所有用例
     * @param directoryId 目录ID
     * @param systemId 系统ID
     * @return 目录ID列表（包含当前目录及其所有子目录），如果directoryId为空则返回null（不限制目录）
     */
    private List<String>  fetchDirectoryHierarchy(String directoryId, String systemId) {
        if (!StringUtils.hasText(directoryId)) {
            return null;
        }
        // 调用目录服务获取当前目录及其所有子目录的ID列表
        List<String> ids = testDirectoryService.getAllChildrenDirectoryIds(directoryId, systemId);
        logger.debug("查询目录层级: directoryId={}, systemId={}, 包含的目录数量={}, 目录IDs={}", directoryId, systemId, ids != null ? ids.size() : 0, ids);
        return ids;
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

    private TfUsecase parseRowToUsecase(Row row, Row headerRow, String systemId, String directoryId, String requirePointId, int rowNumber, List<String> errors) {
        TfUsecase usecase = new TfUsecase();
        usecase.setUsecaseId(generateUsecaseId(systemId));
        usecase.setSystemId(systemId);
        usecase.setDirectoryId(directoryId);

        // 检查需求点是否存在
        if (StringUtils.hasText(requirePointId)) {
            TfRequirepoint requirePoint = requirePointService.getById(requirePointId);
            if (requirePoint == null) {
                String errorMsg = String.format("第%d行: 关联测试需求编号 '%s' 对应的需求点不存在", rowNumber, requirePointId);
                logger.error(errorMsg);
                errors.add(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }else{
                usecase.setRequirePointId(requirePointId);
            }
        }

        // 用例名称* (必填)
        String usecaseName = getCellValueByHeaderWithStar(row, headerRow, "用例名称");
        if (!StringUtils.hasText(usecaseName)) {
            throw new IllegalArgumentException("用例名称不能为空");
        }
        usecase.setUsecaseName(usecaseName);

        // 用例类型* (必填，需要从数据字典获取码值)
        String usecaseTypeName = getCellValueByHeaderWithStar(row, headerRow, "用例类型");
        usecase.setUsecaseType(resolveDictionaryValue("usecaseType", usecaseTypeName));

        // 测试要点 (可选)
        usecase.setTestPoint(resolveDictionaryValue("testPoint", getCellValueByHeader(row, headerRow, "测试要点")));

        // 用例性质* (必填，需要从数据字典获取码值)
        String usecaseNatureName = getCellValueByHeaderWithStar(row, headerRow, "用例性质");
        usecase.setUsecaseNature(resolveDictionaryValue("usecaseNature", usecaseNatureName));

        // 优先级 (可选)
        usecase.setPrority(resolveDictionaryValue("prority", getCellValueByHeader(row, headerRow, "优先级")));

        // 是否冒烟 (可选)
        usecase.setIsSmokeTest(getCellValueByHeader(row, headerRow, "是否冒烟"));

        // 前置条件 (可选)
        usecase.setPrecondition(getCellValueByHeader(row, headerRow, "前置条件"));

        // 测试数据 (可选)
        usecase.setTestData(getCellValueByHeader(row, headerRow, "测试数据"));

        // 测试步骤* (必填)
        String testStep = getCellValueByHeaderWithStar(row, headerRow, "测试步骤");
        usecase.setTestStep(testStep);

        // 预期结果* (必填)
        String expectedResult = getCellValueByHeaderWithStar(row, headerRow, "预期结果");
        usecase.setExpectedResult(expectedResult);

        // 创建人* (必填)
        String creatorName = getCellValueByHeaderWithStar(row, headerRow, "创建人");
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

    /**
     * 获取单元格值，先尝试带*号的列名，如果取不到值，再尝试不带*号的列名
     * @param row 数据行
     * @param headerRow 表头行
     * @param headerName 列名（不带*号）
     * @return 单元格值，如果都取不到则返回null
     */
    private String getCellValueByHeaderWithStar(Row row, Row headerRow, String headerName) {
        // 先尝试带*号的列名
        String value = getCellValueByHeader(row, headerRow, headerName + "*");
        // 如果第一次就取到值了，直接返回
        if (StringUtils.hasText(value)) {
            return value;
        }
        // 如果第一次没取到值，再尝试不带*号的列名
        return getCellValueByHeader(row, headerRow, headerName);
    }

    /**
     * 从数据字典中解析码值
     * 根据dataType和dataName查询dataValue，如果找不到则抛出异常
     * @param type 数据类型（dataType）
     * @param name 数据名称（dataName）
     * @return 数据值（dataValue）
     * @throws IllegalArgumentException 如果找不到对应的码值
     */
    private String resolveDictionaryValue(String type, String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        // 去掉可能的*号
        String normalizedName = name.replace("*", "").trim();
        String value = dataDictionaryService.getDataValueByTypeAndName(type, normalizedName);
        if (value == null) {
            throw new IllegalArgumentException(String.format("数据字典中未找到对应的码值: dataType=%s, dataName=%s", type, normalizedName));
        }
        return value;
    }

    private String getCellValueByHeader(Row row, Row headerRow, String headerName) {
        if (row == null || headerRow == null) {
            return null;
        }
        for (int i = headerRow.getFirstCellNum(); i < headerRow.getLastCellNum(); i++) {
            Cell headerCell = headerRow.getCell(i);
            if (headerCell != null) {
                String cellValue = headerCell.getStringCellValue().trim();
                // 去掉*号后进行比较，支持"用例名称"和"用例名称*"两种格式
                String normalizedCellValue = cellValue.replace("*", "").trim();
                String normalizedHeaderName = headerName.replace("*", "").trim();
                // 使用或的关系：匹配"用例名称"或"用例名称*"
                if (normalizedHeaderName.equalsIgnoreCase(normalizedCellValue)
                    || headerName.equalsIgnoreCase(cellValue)
                    || (headerName + "*").equalsIgnoreCase(cellValue)) {
                    Cell cell = row.getCell(i);
                    return getCellValue(cell);
                }
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
