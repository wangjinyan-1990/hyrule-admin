package com.king.test.usecaseManage.requireRepository.controller;

import com.baomidou.mybatisplus.annotation.TableField;
import com.king.common.Result;
import com.king.common.utils.CounterUtil;
import com.king.common.utils.SecurityUtils;
import com.king.framework.dataDictionary.service.IDataDictionaryService;
import com.king.sys.user.service.IUserService;
import com.king.test.baseManage.testDirectory.service.ITestDirectoryService;
import com.king.test.usecaseManage.requireRepository.entity.TfRequirepoint;
import com.king.test.usecaseManage.requireRepository.service.ITfRequirepointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 需求点Controller
 */
@RestController
@RequestMapping("/test/requirepoint")
public class TfRequirepointController {

    @Autowired
    @Qualifier("tfRequirepointServiceImpl")
    private ITfRequirepointService tfRequirepointService;
    
    @Autowired
    @Qualifier("testDirectoryServiceImpl")
    private ITestDirectoryService testDirectoryService;
    
    @Autowired
    private CounterUtil counterUtil;
    
    @Autowired
    @Qualifier("userServiceImpl")
    private IUserService userService;

    @Autowired
    @Qualifier("dataDictionaryServiceImpl")
    private IDataDictionaryService dataDictionaryService;
    
    @Autowired
    private SecurityUtils securityUtils;

    /**
     * 分页查询需求点列表
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param systemId 系统ID（可选）
     * @param directoryId 目录ID（可选）
     * @param requirePointType 需求点类型（可选）
     * @param reviewStatus 评审状态（可选）
     * @param requireStatus 需求状态（可选）
     * @param designer 设计人（可选）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getRequirePointList(
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "systemId", required = false) String systemId,
            @RequestParam(value = "directoryId", required = false) String directoryId,
            @RequestParam(value = "requirePointType", required = false) String requirePointType,
            @RequestParam(value = "reviewStatus", required = false) String reviewStatus,
            @RequestParam(value = "requireStatus", required = false) String requireStatus,
            @RequestParam(value = "designer", required = false) String designer) {

        try {
            Map<String, Object> data = tfRequirepointService.getRequirepointsWithPagination(
                pageNo, pageSize, systemId, directoryId, requirePointType, 
                reviewStatus, requireStatus, designer);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询需求点列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询需求点详情
     * @param requirePointId 需求点ID
     * @return 需求点详情
     */
    @GetMapping("/detail/{requirePointId}")
    public Result<TfRequirepoint> getRequirePointById(@PathVariable("requirePointId") String requirePointId) {
        if (!StringUtils.hasText(requirePointId)) {
            return Result.error("需求点ID不能为空");
        }

        try {
            TfRequirepoint requirepoint = tfRequirepointService.getRequirepointDetailById(requirePointId);
            if (requirepoint != null) {
                return Result.success(requirepoint);
            } else {
                return Result.error("需求点不存在");
            }
        } catch (Exception e) {
            return Result.error("查询需求点详情失败：" + e.getMessage());
        }
    }

    /**
     * 创建需求点
     * @param requirepoint 需求点信息
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result<?> createRequirePoint(@RequestBody TfRequirepoint requirepoint) {
        if (requirepoint == null) {
            return Result.error("需求点信息不能为空");
        }

        if (!StringUtils.hasText(requirepoint.getSystemId())) {
            return Result.error("系统ID不能为空");
        }

        if (!StringUtils.hasText(requirepoint.getRequirePointDesc())) {
            return Result.error("需求点概述不能为空");
        }

        try {
            boolean success = tfRequirepointService.createRequirepoint(requirepoint);
            if (success) {
                return Result.success("需求点创建成功");
            } else {
                return Result.error("需求点创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建需求点失败：" + e.getMessage());
        }
    }

    /**
     * 更新需求点
     * @param data 需求点信息
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result<?> updateRequirePoint(@RequestBody Map<String, Object> data) {
        if (data == null) {
            return Result.error("需求点信息不能为空");
        }

        String requirePointId = (String) data.get("requirePointId");
        if (!StringUtils.hasText(requirePointId)) {
            return Result.error("需求点ID不能为空");
        }

        try {
            // 将Map转换为TfRequirepoint对象
            TfRequirepoint requirepoint = new TfRequirepoint();
            requirepoint.setRequirePointId(requirePointId);
            requirepoint.setRequirePointDesc((String) data.get("requirePointDesc"));
            requirepoint.setSystemId((String) data.get("systemId"));
            requirepoint.setDirectoryId((String) data.get("directoryId"));
            requirepoint.setRequirePointType((String) data.get("requirePointType"));
            requirepoint.setReviewStatus((String) data.get("reviewStatus"));
            requirepoint.setAnalysisMethod((String) data.get("analysisMethod"));
            requirepoint.setRequireStatus((String) data.get("requireStatus"));
            requirepoint.setDesignerId((String) data.get("designerId"));
            // 自动设置修改人为当前用户
            String currentUserId = securityUtils.getUserId();
            if (StringUtils.hasText(currentUserId)) {
                requirepoint.setModifierId(currentUserId);
            }
            requirepoint.setRemark((String) data.get("remark"));
            requirepoint.setSendTestId((String) data.get("sendTestId"));
            
            Object workPackageIdObj = data.get("workPackageId");
            if (workPackageIdObj != null) {
                if (workPackageIdObj instanceof Integer) {
                    requirepoint.setWorkPackageId((Integer) workPackageIdObj);
                } else if (workPackageIdObj instanceof String) {
                    try {
                        requirepoint.setWorkPackageId(Integer.parseInt((String) workPackageIdObj));
                    } catch (NumberFormatException e) {
                        // 忽略转换错误
                    }
                }
            }

            boolean success = tfRequirepointService.updateRequirepoint(requirepoint);
            if (success) {
                return Result.success("需求点更新成功");
            } else {
                return Result.error("需求点更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新需求点失败：" + e.getMessage());
        }
    }

    /**
     * 删除需求点
     * @param data 包含需求点ID的数据
     * @return 删除结果
     */
    @PostMapping("/delete")
    public Result<?> deleteRequirePoint(@RequestBody Map<String, Object> data) {
        if (data == null) {
            return Result.error("请求数据不能为空");
        }

        String requirePointId = (String) data.get("requirePointId");
        if (!StringUtils.hasText(requirePointId)) {
            return Result.error("需求点ID不能为空");
        }

        try {
            boolean success = tfRequirepointService.deleteRequirepoint(requirePointId);
            if (success) {
                return Result.success("需求点删除成功");
            } else {
                return Result.error("需求点删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除需求点失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除需求点
     * @param data 包含需求点ID列表的数据
     * @return 删除结果
     */
    @PostMapping("/batchDelete")
    public Result<?> batchDeleteRequirePoints(@RequestBody Map<String, Object> data) {
        if (data == null) {
            return Result.error("请求数据不能为空");
        }

        @SuppressWarnings("unchecked")
        List<String> requirePointIds = (List<String>) data.get("requirePointIds");
        
        if (requirePointIds == null || requirePointIds.isEmpty()) {
            return Result.error("需求点ID列表不能为空");
        }

        try {
            boolean success = tfRequirepointService.batchDeleteRequirepoints(requirePointIds);
            if (success) {
                return Result.success("批量删除需求点成功");
            } else {
                return Result.error("批量删除需求点失败");
            }
        } catch (Exception e) {
            return Result.error("批量删除需求点失败：" + e.getMessage());
        }
    }

    /**
     * 批量评审需求点
     * @param data 批量评审数据
     * @return 评审结果
     */
    @PostMapping("/batchReview")
    public Result<?> batchReviewRequirePoints(@RequestBody Map<String, Object> data) {
        if (data == null) {
            return Result.error("请求数据不能为空");
        }

        @SuppressWarnings("unchecked")
        List<String> requirePointIds = (List<String>) data.get("requirePointIds");
        String reviewStatus = (String) data.get("reviewStatus");
        String reviewComment = (String) data.get("reviewComment");
        
        if (requirePointIds == null || requirePointIds.isEmpty()) {
            return Result.error("需求点ID列表不能为空");
        }
        
        if (!StringUtils.hasText(reviewStatus)) {
            return Result.error("评审状态不能为空");
        }

        try {
            boolean success = tfRequirepointService.batchReviewRequirepoints(requirePointIds, reviewStatus, reviewComment);
            if (success) {
                return Result.success("批量评审需求点成功");
            } else {
                return Result.error("批量评审需求点失败");
            }
        } catch (Exception e) {
            return Result.error("批量评审需求点失败：" + e.getMessage());
        }
    }

    /**
     * 导出需求点数据
     * @param data 导出参数
     * @param response HTTP响应对象
     */
    @PostMapping("/export")
    public void exportRequirePoints(@RequestBody Map<String, Object> data, HttpServletResponse response) {
        if (data == null) {
            try {
                response.getWriter().write("导出参数不能为空");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        String systemId = (String) data.get("systemId");
        String directoryId = (String) data.get("directoryId");
        String requirePointType = (String) data.get("requirePointType");
        String reviewStatus = (String) data.get("reviewStatus");
        String requireStatus = (String) data.get("requireStatus");
        String designerId = (String) data.get("designerId");

        try {
            // 获取需求点数据
            List<TfRequirepoint> requirepoints = tfRequirepointService.exportRequirepoints(
                systemId, directoryId, requirePointType, reviewStatus, requireStatus, designerId);
            
            // 创建Excel工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("需求点数据");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"需求点Id", "模块路径", "需求点概述", "评审状态", "分析方法", "设计者", "备注", "需求状态"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 填充数据
            int rowNum = 1;
            for (TfRequirepoint requirepoint : requirepoints) {
                Row row = sheet.createRow(rowNum++);
                
                // 需求点Id
                row.createCell(0).setCellValue(requirepoint.getRequirePointId() != null ? requirepoint.getRequirePointId() : "");
                
                // 模块路径 - 这里需要根据directoryId获取完整路径
                String fullPath = "";
                if (requirepoint.getDirectoryId() != null) {
                    // 这里可以调用目录服务获取完整路径
                    fullPath = getDirectoryFullPath(requirepoint.getDirectoryId());
                }
                row.createCell(1).setCellValue(fullPath);
                
                // 需求点概述
                row.createCell(2).setCellValue(requirepoint.getRequirePointDesc() != null ? requirepoint.getRequirePointDesc() : "");
                
                // 评审状态
                row.createCell(3).setCellValue(requirepoint.getReviewStatusName() != null ? requirepoint.getReviewStatusName() : "");
                
                // 分析方法
                row.createCell(4).setCellValue(requirepoint.getAnalysisMethodName() != null ? requirepoint.getAnalysisMethodName() : "");
                
                // 设计者
                row.createCell(5).setCellValue(requirepoint.getDesigner() != null ? requirepoint.getDesigner() : "");
                
                // 备注
                row.createCell(6).setCellValue(requirepoint.getRemark() != null ? requirepoint.getRemark() : "");
                
                // 需求状态
                row.createCell(7).setCellValue(requirepoint.getRequireStatusName() != null ? requirepoint.getRequireStatusName() : "");
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=requirepoint_data.xlsx");
            
            // 写入响应流
            workbook.write(response.getOutputStream());
            workbook.close();
            
        } catch (Exception e) {
            try {
                response.getWriter().write("导出需求点数据失败：" + e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * 获取目录完整路径
     * @param directoryId 目录ID
     * @return 完整路径
     */
    private String getDirectoryFullPath(String directoryId) {
        return testDirectoryService.getDirectoryFullPath(directoryId);
    }

    /**
     * 下载导入模板
     * @param response HTTP响应对象
     * @return 模板文件
     */
    @GetMapping("/template")
    public void downloadImportTemplate(HttpServletResponse response) {
        try {
            // 从classpath读取模板文件
            Resource resource = new ClassPathResource("templates/RequireTemplate.xlsx");
            
            if (!resource.exists()) {
                response.getWriter().write("模板文件不存在，请检查文件路径：templates/RequireTemplate.xlsx");
                return;
            }
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=RequireTemplate.xlsx");
            
            // 读取文件并写入响应流
            try (InputStream inputStream = resource.getInputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    response.getOutputStream().write(buffer, 0, bytesRead);
                }
                response.getOutputStream().flush();
            }
            
        } catch (IOException e) {
            try {
                response.getWriter().write("模板下载失败：" + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 导入需求点数据
     * @param file 上传的Excel文件
     * @param systemId 系统ID（可选，如果提供则覆盖Excel中的系统ID）
     * @return 导入结果
     */
    @PostMapping("/import")
    public Result<?> importRequirePoints(@RequestParam("file") MultipartFile file,
                                       @RequestParam(value = "systemId", required = false) String systemId) {
        if (file == null || file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            return Result.error("文件格式不正确，请上传Excel文件(.xlsx或.xls)");
        }

        try {
            // 解析Excel文件并收集错误信息
            Map<String, Object> parseResult = parseExcelFileWithErrors(file, systemId);
            List<TfRequirepoint> requirepoints = (List<TfRequirepoint>) parseResult.get("data");
            List<String> parseErrors = (List<String>) parseResult.get("errors");
            
            if (requirepoints.isEmpty() && parseErrors.isEmpty()) {
                return Result.error("Excel文件中没有有效的数据行");
            }

            // 批量保存需求点
            int successCount = 0;
            int failCount = 0;
            List<String> errorMessages = new ArrayList<>();
            
            // 添加解析错误
            errorMessages.addAll(parseErrors);

            for (TfRequirepoint requirepoint : requirepoints) {
                try {
                    boolean success = tfRequirepointService.createRequirepoint(requirepoint);
                    if (success) {
                        successCount++;
                    } else {
                        failCount++;
                        errorMessages.add("需求点 '" + requirepoint.getRequirePointDesc() + "' 保存失败");
                    }
                } catch (Exception e) {
                    failCount++;
                    errorMessages.add("需求点 '" + requirepoint.getRequirePointDesc() + "' 保存失败: " + e.getMessage());
                }
            }

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", requirepoints.size() + parseErrors.size());
            result.put("successCount", successCount);
            result.put("failCount", failCount + parseErrors.size());
            result.put("errorMessages", errorMessages);

            if (failCount == 0 && parseErrors.isEmpty()) {
                return Result.success(result, "导入成功！共导入 " + successCount + " 条需求点数据");
            } else {
                return Result.success(result, "导入完成！成功 " + successCount + " 条，失败 " + (failCount + parseErrors.size()) + " 条");
            }
            
        } catch (Exception e) {
            return Result.error("导入需求点数据失败：" + e.getMessage());
        }
    }

    /**
     * 解析Excel文件
     * @param file Excel文件
     * @param systemId 系统ID（可选，如果提供则覆盖Excel中的系统ID）
     * @return 需求点列表
     * @throws IOException IO异常
     */
    private List<TfRequirepoint> parseExcelFile(MultipartFile file, String systemId) throws IOException {
        List<TfRequirepoint> requirepoints = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            
            // 获取标题行（第一行）
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel文件缺少标题行");
            }
            
            // 跳过标题行，从第二行开始读取数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                // 检查是否为空行
                if (isRowEmpty(row)) continue;
                
                Map<String, Object> parseResult = parseRowToRequirepoint(row, systemId, headerRow, i + 1);
                if ((Boolean) parseResult.get("success")) {
                    TfRequirepoint requirepoint = (TfRequirepoint) parseResult.get("data");
                    requirepoints.add(requirepoint);
                }
                // 注意：这里暂时不处理错误信息，错误信息会在导入接口中统一处理
            }
        }
        
        return requirepoints;
    }

    /**
     * 解析行数据为需求点对象
     * @param row Excel行
     * @param systemId 系统ID（可选，如果提供则覆盖Excel中的系统ID）
     * @param headerRow 标题行，用于获取列名映射
     * @param rowNumber 行号（用于错误提示）
     * @return 解析结果，包含需求点对象和错误信息
     */
    private Map<String, Object> parseRowToRequirepoint(Row row, String systemId, Row headerRow, int rowNumber) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 根据列名获取数据：requirePointId
            String requirePointId = getCellValueByColumnName(row, headerRow, "需求点Id");
            if (requirePointId == null || requirePointId.isEmpty()) {
                requirePointId = getCellValueByColumnName(row, headerRow, "需求点id");
            }
            if (requirePointId == null || requirePointId.isEmpty()) {
                requirePointId = getCellValueByColumnName(row, headerRow, "需求点ID");
            }
            // 如果requirePointId不为空，则找到requirepoint，进行修改替换，若requirePointId为空，则新建
            TfRequirepoint requirepoint = null;
            if (StringUtils.hasText(requirePointId)) {
                requirepoint = tfRequirepointService.getById(requirePointId);
            }else{
                requirepoint = new TfRequirepoint();
                // 设置ID和创建时间
                requirepoint.setCreateTime(LocalDateTime.now());
                requirepoint.setSystemId(systemId);
                // 生成需求点ID
                requirepoint.setRequirePointId(generateRequirePointId(systemId));

                // 处理设计者信息
                String designer = getCellValueByColumnName(row, headerRow, "设计者*");
                if (designer == null || designer.isEmpty()) {
                    designer = getCellValueByColumnName(row, headerRow, "设计者");
                }
                if (StringUtils.hasText(designer)) {
                    String designerId = userService.getUserIdByUserName(designer);
                    // 设计者不存在
                    if (!StringUtils.hasText(designerId)) {
                        result.put("success", false);
                        result.put("error", "第" + rowNumber + "行，设计者不存在：" + designer);
                        return result;
                    }
                    requirepoint.setDesignerId(designerId);
                }

            }

            // 根据列名获取数据
            String fullPath = getCellValueByColumnName(row, headerRow, "模块路径*");
            // 根据fullPath完整路径 查找 directoryId目录ID
            String directoryId = testDirectoryService.getDirectoryIdByFullPath(fullPath);
            requirepoint.setDirectoryId(directoryId);


            requirepoint.setRequirePointDesc(getCellValueByColumnName(row, headerRow, "需求点概述*"));

            // 测试需求类型 - 通过数据字典验证和转换
            String requirePointType = getCellValueByColumnName(row, headerRow, "测试需求类型*");
            if (StringUtils.hasText(requirePointType)) {
                String requirePointTypeValue = dataDictionaryService.getDataValueByTypeAndName("requirePointType", requirePointType);
                if (requirePointTypeValue == null) {
                    result.put("success", false);
                    result.put("error", "第" + rowNumber + "行，测试需求类型不存在：" + requirePointType);
                    return result;
                }
                requirepoint.setRequirePointType(requirePointTypeValue);
            }

            // 分析方法 - 通过数据字典验证和转换
            String analysisMethod = getCellValueByColumnName(row, headerRow, "分析方法*");
            if (StringUtils.hasText(analysisMethod)) {
                String analysisMethodValue = dataDictionaryService.getDataValueByTypeAndName("analysisMethod", analysisMethod);
                if (analysisMethodValue == null) {
                    result.put("success", false);
                    result.put("error", "第" + rowNumber + "行，分析方法不存在：" + analysisMethod);
                    return result;
                }
                requirepoint.setAnalysisMethod(analysisMethodValue);
            }
            
            // 需求状态 - 通过数据字典验证和转换
            String requireStatus = getCellValueByColumnName(row, headerRow, "需求状态");
            if (StringUtils.hasText(requireStatus)) {
                String requireStatusValue = dataDictionaryService.getDataValueByTypeAndName("requireStatus", requireStatus);
                if (requireStatusValue == null) {
                    result.put("success", false);
                    result.put("error", "第" + rowNumber + "行，需求状态不存在：" + requireStatus);
                    return result;
                }
                requirepoint.setRequireStatus(requireStatusValue);
            }
            
            // 评审状态 - 通过数据字典验证和转换
            String reviewStatus = getCellValueByColumnName(row, headerRow, "评审状态");
            if (StringUtils.hasText(reviewStatus)) {
                String reviewStatusValue = dataDictionaryService.getDataValueByTypeAndName("reviewStatus", reviewStatus);
                if (reviewStatusValue == null) {
                    result.put("success", false);
                    result.put("error", "第" + rowNumber + "行，评审状态不存在：" + reviewStatus);
                    return result;
                }
                requirepoint.setReviewStatus(reviewStatusValue);
            } else {
                // 评审状态为空时，默认为待评审状态
                String defaultReviewStatus = dataDictionaryService.getDataValueByTypeAndName("reviewStatus", "待评审");
                if (defaultReviewStatus != null) {
                    requirepoint.setReviewStatus(defaultReviewStatus);
                } else {
                    requirepoint.setReviewStatus("0"); // 如果数据字典中没有默认值，使用硬编码
                }
            }

            requirepoint.setRemark(getCellValueByColumnName(row, headerRow, "备注"));

            // 验证必填字段
            if (!StringUtils.hasText(requirepoint.getDirectoryId())) {
                result.put("success", false);
                result.put("error", "第" + rowNumber + "行，模块路径不能为空");
                return result;
            }
            
            if (!StringUtils.hasText(requirepoint.getRequirePointDesc())) {
                result.put("success", false);
                result.put("error", "第" + rowNumber + "行，需求点概述不能为空");
                return result;
            }
            
            result.put("success", true);
            result.put("data", requirepoint);
            return result;
            
        } catch (Exception e) {
            // 解析失败
            result.put("success", false);
            result.put("error", "第" + rowNumber + "行，数据解析失败：" + e.getMessage());
            return result;
        }
    }

    /**
     * 获取单元格值作为字符串
     * @param cell 单元格
     * @return 字符串值
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 处理数字类型，避免科学计数法
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
                return null;
        }
    }

    /**
     * 检查行是否为空
     * @param row Excel行
     * @return 是否为空
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (StringUtils.hasText(value)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 根据列名获取单元格值
     * @param row 数据行
     * @param headerRow 标题行
     * @param columnName 列名
     * @return 单元格值
     */
    private String getCellValueByColumnName(Row row, Row headerRow, String columnName) {
        if (row == null || headerRow == null || !StringUtils.hasText(columnName)) {
            return null;
        }
        
        // 在标题行中查找列名对应的列索引
        int columnIndex = -1;
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell headerCell = headerRow.getCell(i);
            if (headerCell != null) {
                String headerValue = getCellValueAsString(headerCell);
                if (columnName.equals(headerValue)) {
                    columnIndex = i;
                    break;
                }
            }
        }
        
        // 如果找到列索引，获取对应单元格的值
        if (columnIndex >= 0) {
            Cell cell = row.getCell(columnIndex);
            return getCellValueAsString(cell);
        }
        
        return null;
    }

    /**
     * 解析Excel文件并收集错误信息
     * @param file Excel文件
     * @param systemId 系统ID（可选，如果提供则覆盖Excel中的系统ID）
     * @return 解析结果，包含需求点列表和错误信息
     * @throws IOException IO异常
     */
    private Map<String, Object> parseExcelFileWithErrors(MultipartFile file, String systemId) throws IOException {
        List<TfRequirepoint> requirepoints = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            
            // 获取标题行（第一行）
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel文件缺少标题行");
            }
            
            // 跳过标题行，从第二行开始读取数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                // 检查是否为空行
                if (isRowEmpty(row)) continue;
                
                Map<String, Object> parseResult = parseRowToRequirepoint(row, systemId, headerRow, i + 1);
                if ((Boolean) parseResult.get("success")) {
                    TfRequirepoint requirepoint = (TfRequirepoint) parseResult.get("data");
                    requirepoints.add(requirepoint);
                } else {
                    String error = (String) parseResult.get("error");
                    errors.add(error);
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", requirepoints);
        result.put("errors", errors);
        return result;
    }

    /**
     * 生成需求点ID
     * @param systemId 系统ID
     * @return 需求点ID
     */
    private String generateRequirePointId(String systemId) {
        // 使用计数器生成需求点ID
        String requirePointId = systemId + "-" + counterUtil.generateNextCode("requireCode");
        return requirePointId;
    }

}
