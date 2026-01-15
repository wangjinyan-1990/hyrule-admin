package com.king.environment.environmentList.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.environment.environmentList.mapper.EnvironmentListMapper;
import com.king.environment.environmentList.entity.TfEnvironment;
import com.king.environment.environmentList.entity.TfEnvironmentList;
import com.king.environment.environmentList.service.IEnvironmentListService;
import com.king.test.baseManage.testSystem.service.ITestSystemService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 环境清单Service实现类
 */
@Service("environmentListServiceImpl")
public class EnvironmentListServiceImpl extends ServiceImpl<EnvironmentListMapper, TfEnvironmentList> implements IEnvironmentListService {

    @Resource
    private EnvironmentListMapper environmentListMapper;

    @Resource
    @Qualifier("testSystemServiceImpl")
    private ITestSystemService testSystemService;

    private static final String TEMPLATE_PATH = "templates/EnvironmentListTemplate.xlsx";

    /**
     * 查询环境列表
     * @param testStage 测试阶段（可选，SIT、PAT）
     * @return 环境列表
     */
    @Override
    public List<TfEnvironment> getEnvironmentList(String testStage) {
        return environmentListMapper.selectEnvironmentList(testStage);
    }

    /**
     * 查询环境清单列表（带关联信息）
     * @param envId 环境Id（可选）
     * @param systemName 系统名称（可选，模糊查询）
     * @param serverName 服务名称（可选，模糊查询）
     * @param ipAddress 主机地址（可选，模糊查询）
     * @return 环境清单列表
     */
    @Override
    public List<TfEnvironmentList> getEnvironmentListList(Integer envId, String systemName, String serverName, String ipAddress) {
        return environmentListMapper.selectEnvironmentListWithJoin(envId, systemName, serverName, ipAddress);
    }

    /**
     * 根据ID查询环境清单详情（带关联信息）
     * @param envListId 环境清单Id
     * @return 环境清单详情
     */
    @Override
    public TfEnvironmentList getEnvironmentListDetail(Integer envListId) {
        Assert.notNull(envListId, "环境清单Id不能为空");
        return environmentListMapper.selectEnvironmentListDetailById(envListId);
    }

    /**
     * 创建环境清单
     * @param environmentList 环境清单信息
     * @return 是否创建成功
     */
    @Override
    public boolean createEnvironmentList(TfEnvironmentList environmentList) {
        Assert.notNull(environmentList, "环境清单信息不能为空");
        Assert.notNull(environmentList.getEnvId(), "环境Id不能为空");
        Assert.notNull(environmentList.getSystemId(), "系统ID不能为空");
        Assert.hasText(environmentList.getServerName(), "服务名称不能为空");
        Assert.hasText(environmentList.getIpAddress(), "主机地址不能为空");

        // 唯一性校验：系统ID、服务名称、主机地址的组合不能重复
        List<TfEnvironmentList> existingList = environmentListMapper.selectBySystemIdAndServerNameAndIpAddress(
                environmentList.getSystemId(),
                environmentList.getServerName(),
                environmentList.getIpAddress(),
                null  // 创建时不需要排除任何记录
        );
        Assert.isTrue(existingList == null || existingList.isEmpty(),
                "该环境清单已存在：系统ID=" + environmentList.getSystemId() +
                ", 服务名称=" + environmentList.getServerName() +
                ", 主机地址=" + environmentList.getIpAddress());

        return this.save(environmentList);
    }

    /**
     * 更新环境清单
     * @param environmentList 环境清单信息
     * @return 是否更新成功
     */
    @Override
    public boolean updateEnvironmentList(TfEnvironmentList environmentList) {
        Assert.notNull(environmentList, "环境清单信息不能为空");
        Assert.notNull(environmentList.getEnvListId(), "环境清单Id不能为空");
        Assert.notNull(environmentList.getEnvId(), "环境Id不能为空");
        Assert.notNull(environmentList.getSystemId(), "系统ID不能为空");
        Assert.hasText(environmentList.getServerName(), "服务名称不能为空");
        Assert.hasText(environmentList.getIpAddress(), "主机地址不能为空");

        // 唯一性校验：系统ID、服务名称、主机地址的组合不能重复（排除当前记录）
        List<TfEnvironmentList> existingList = environmentListMapper.selectBySystemIdAndServerNameAndIpAddress(
                environmentList.getSystemId(),
                environmentList.getServerName(),
                environmentList.getIpAddress(),
                environmentList.getEnvListId()  // 更新时需要排除当前记录
        );
        Assert.isTrue(existingList == null || existingList.isEmpty(),
                "该环境清单已存在：系统ID=" + environmentList.getSystemId() +
                ", 服务名称=" + environmentList.getServerName() +
                ", 主机地址=" + environmentList.getIpAddress());

        return this.updateById(environmentList);
    }

    /**
     * 删除环境清单
     * @param envListId 环境清单Id
     * @return 是否删除成功
     */
    @Override
    public boolean deleteEnvironmentList(Integer envListId) {
        Assert.notNull(envListId, "环境清单Id不能为空");
        return this.removeById(envListId);
    }

    /**
     * 导出环境清单数据到Excel
     * @param environmentLists 环境清单列表
     * @param response HTTP响应对象
     */
    @Override
    public void exportEnvironmentListToExcel(List<TfEnvironmentList> environmentLists, HttpServletResponse response) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("环境清单");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"系统名称", "环境名称", "服务名称", "主机地址", "端口信息", "链接地址", "备注"};
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

        // 填充数据
        int rowIndex = 1;
        for (TfEnvironmentList environmentList : environmentLists) {
            Row row = sheet.createRow(rowIndex++);
            int colIndex = 0;

            row.createCell(colIndex++).setCellValue(
                    environmentList.getSystemName() != null ? environmentList.getSystemName() : "");
            row.createCell(colIndex++).setCellValue(
                    environmentList.getEnvName() != null ? environmentList.getEnvName() : "");
            row.createCell(colIndex++).setCellValue(
                    environmentList.getServerName() != null ? environmentList.getServerName() : "");
            row.createCell(colIndex++).setCellValue(
                    environmentList.getIpAddress() != null ? environmentList.getIpAddress() : "");
            row.createCell(colIndex++).setCellValue(
                    environmentList.getPortInfo() != null ? environmentList.getPortInfo() : "");
            row.createCell(colIndex++).setCellValue(
                    environmentList.getLinkAddress() != null ? environmentList.getLinkAddress() : "");
            row.createCell(colIndex++).setCellValue(
                    environmentList.getRemark() != null ? environmentList.getRemark() : "");
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            // 设置最小列宽
            sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i), 3000));
        }

        try {
            // 生成文件名：环境清单_YYYYMMDDHHmmssSSS.xlsx
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String timestamp = sdf.format(new Date());
            String fileName = "环境清单_" + timestamp + ".xlsx";
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("导出环境清单数据失败: " + e.getMessage(), e);
        } finally {
            try {
                workbook.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 导入环境清单数据
     * @param file Excel文件
     * @return 导入结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importEnvironmentList(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            throw new IllegalArgumentException("文件格式不正确，请上传Excel文件(.xlsx或.xls)");
        }

        List<String> errors = new ArrayList<>();
        List<TfEnvironmentList> environmentLists = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new IllegalArgumentException("Excel中缺少工作表");
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel缺少表头");
            }

            // 验证表头
            String[] expectedHeaders = {"系统名称", "环境名称", "服务名称", "主机地址", "端口信息", "链接地址", "备注"};
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < expectedHeaders.length; i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    String headerValue = getCellValueAsString(cell);
                    headerMap.put(headerValue, i);
                }
            }

            // 检查必需的表头
            if (!headerMap.containsKey("系统名称") || !headerMap.containsKey("环境名称") || !headerMap.containsKey("服务名称")
                    || !headerMap.containsKey("主机地址")) {
                throw new IllegalArgumentException("Excel表头缺少必需字段：系统名称、环境名称、服务名称、主机地址");
            }

            // 解析数据行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }
                try {
                    TfEnvironmentList environmentList = parseRowToEnvironmentList(row, headerMap);
                    environmentLists.add(environmentList);
                } catch (Exception ex) {
                    errors.add(String.format("第%d行导入失败: %s", i + 1, ex.getMessage()));
                }
            }
        }

        // 批量保存或更新
        int successCount = 0;
        int updateCount = 0;
        int insertCount = 0;
        for (TfEnvironmentList environmentList : environmentLists) {
            try {
                // 根据系统ID和主机地址查询是否已存在
                List<TfEnvironmentList> existingList = environmentListMapper.selectBySystemIdAndIpAddress(
                        environmentList.getSystemId(),
                        environmentList.getIpAddress()
                );
                
                if (existingList != null && !existingList.isEmpty()) {
                    // 已存在，执行更新
                    TfEnvironmentList existing = existingList.get(0);
                    environmentList.setEnvListId(existing.getEnvListId());
                    // 保留原有的环境ID，如果新数据没有则使用原有的
                    if (environmentList.getEnvId() == null) {
                        environmentList.setEnvId(existing.getEnvId());
                    }
                    if (this.updateEnvironmentList(environmentList)) {
                        successCount++;
                        updateCount++;
                    }
                } else {
                    // 不存在，执行新增
                    if (this.createEnvironmentList(environmentList)) {
                        successCount++;
                        insertCount++;
                    }
                }
            } catch (Exception e) {
                errors.add(String.format("环境清单[系统=%s, 服务=%s, 地址=%s]保存失败: %s",
                        environmentList.getSystemName() != null ? environmentList.getSystemName() : "未知",
                        environmentList.getServerName() != null ? environmentList.getServerName() : "未知",
                        environmentList.getIpAddress() != null ? environmentList.getIpAddress() : "未知",
                        e.getMessage()));
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("insertCount", insertCount);
        result.put("updateCount", updateCount);
        result.put("failCount", errors.size());
        result.put("errors", errors);
        result.put("totalCount", environmentLists.size());
        return result;
    }

    /**
     * 下载导入模板
     * @param response HTTP响应对象
     */
    @Override
    public void downloadTemplate(HttpServletResponse response) {
        try (InputStream inputStream = Objects.requireNonNull(getClass().getClassLoader()).getResourceAsStream(TEMPLATE_PATH)) {
            if (inputStream == null) {
                throw new IllegalStateException("模板文件不存在: " + TEMPLATE_PATH);
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=EnvironmentListTemplate.xlsx");
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

    /**
     * 解析行数据为环境清单对象
     */
    private TfEnvironmentList parseRowToEnvironmentList(Row row, Map<String, Integer> headerMap) {
        TfEnvironmentList environmentList = new TfEnvironmentList();

        // 系统名称 -> 系统ID
        String systemName = getCellValueAsString(row.getCell(headerMap.get("系统名称")));
        if (!StringUtils.hasText(systemName)) {
            throw new IllegalArgumentException("系统名称不能为空");
        }
        String systemId = testSystemService.getSystemIdByName(systemName);
        if (systemId == null) {
            throw new IllegalArgumentException("系统名称[" + systemName + "]不存在");
        }
        environmentList.setSystemId(systemId);

        // 环境名称 -> 环境ID（必须从Excel数据中获取）
        String envName = getCellValueAsString(row.getCell(headerMap.get("环境名称")));
        if (!StringUtils.hasText(envName)) {
            throw new IllegalArgumentException("环境名称不能为空");
        }
        Integer envId = getEnvIdByName(envName);
        if (envId == null) {
            throw new IllegalArgumentException("环境名称[" + envName + "]不存在");
        }
        environmentList.setEnvId(envId);

        // 服务名称
        String serverName = getCellValueAsString(row.getCell(headerMap.get("服务名称")));
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("服务名称不能为空");
        }
        environmentList.setServerName(serverName);

        // 主机地址
        String ipAddress = getCellValueAsString(row.getCell(headerMap.get("主机地址")));
        if (!StringUtils.hasText(ipAddress)) {
            throw new IllegalArgumentException("主机地址不能为空");
        }
        environmentList.setIpAddress(ipAddress);

        // 端口信息（可选）
        if (headerMap.containsKey("端口信息")) {
            environmentList.setPortInfo(getCellValueAsString(row.getCell(headerMap.get("端口信息"))));
        }

        // 链接地址（可选）
        if (headerMap.containsKey("链接地址")) {
            environmentList.setLinkAddress(getCellValueAsString(row.getCell(headerMap.get("链接地址"))));
        }

        // 备注（可选）
        if (headerMap.containsKey("备注")) {
            environmentList.setRemark(getCellValueAsString(row.getCell(headerMap.get("备注"))));
        }

        return environmentList;
    }

    /**
     * 获取单元格值（字符串）
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 处理数字，避免科学计数法
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * 判断行是否为空
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && StringUtils.hasText(getCellValueAsString(cell))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据环境名称获取环境ID
     */
    private Integer getEnvIdByName(String envName) {
        List<TfEnvironment> environments = environmentListMapper.selectEnvironmentList(null);
        for (TfEnvironment env : environments) {
            if (envName.equals(env.getEnvName())) {
                return env.getEnvId();
            }
        }
        return null;
    }
}

