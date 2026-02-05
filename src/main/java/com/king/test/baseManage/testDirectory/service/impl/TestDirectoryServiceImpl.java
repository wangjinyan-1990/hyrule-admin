package com.king.test.baseManage.testDirectory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.test.baseManage.testDirectory.entity.TTestDirectory;
import com.king.test.baseManage.testDirectory.mapper.TestDirectoryMapper;
import com.king.test.baseManage.testDirectory.service.ITestDirectoryService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 测试目录Service实现类
 */
@Service("testDirectoryServiceImpl")
public class TestDirectoryServiceImpl extends ServiceImpl<TestDirectoryMapper, TTestDirectory> implements ITestDirectoryService {

    private static final Logger logger = LoggerFactory.getLogger(TestDirectoryServiceImpl.class);

    /**
     * 模板文件路径
     */
    private static final String TEMPLATE_PATH = "templates/DirectoryTempalte.xlsx";

    /**
     * 查询用户参与的测试系统,目录树展示系统根目录
     */
    @Override
    public Map<String, Object> getRootDirectoryByUserId(String userId) {
        Assert.hasText(userId, "用户ID不能为空");
        List<TTestDirectory> rootDirectories;
        // 使用自定义查询方法
        if("admin".equals(userId)){
            rootDirectories = baseMapper.getRootDirectory();
        }else{
            rootDirectories = baseMapper.getRootDirectoryByUserId(userId);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("rows", rootDirectories);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDirectory(TTestDirectory directory) {
        Assert.notNull(directory, "目录信息不能为空");
        Assert.hasText(directory.getDirectoryName(), "目录名称不能为空");
        Assert.hasText(directory.getSystemId(), "系统ID不能为空");

        // 检查目录名称是否重复
        validateDirectoryNameUnique(directory.getDirectoryName(), directory.getDirectoryParentId(),
                                  directory.getSystemId(), null);

        // 生成目录ID
        directory.setDirectoryId(UUID.randomUUID().toString().replace("-", ""));

        // 设置创建时间
        directory.setCreateTime(LocalDateTime.now());

        // 构建完整路径
        buildFullPath(directory);

        // 设置路径层级字段
        setPathLevels(directory);

        // 设置默认值
        if (directory.getIsUseTestcase() == null) {
            directory.setIsUseTestcase("1");
        }
        if (directory.getIsUseTestset() == null) {
            directory.setIsUseTestset("1");
        }

        // 设置新目录为叶子目录（默认值为1）
        directory.setIsLeafDirectory("1");

        // 保存到数据库
        boolean result = baseMapper.insert(directory) > 0;

        // 如果有父目录，将父目录设置为非叶子目录
        if (result && directory.getDirectoryParentId() != null && !directory.getDirectoryParentId().trim().isEmpty()) {
            updateParentDirectoryLeafStatus(directory.getDirectoryParentId(), "0");
        }

        return result;
    }

    /**
     * 构建完整路径
     */
    private void buildFullPath(TTestDirectory directory) {
        StringBuilder fullPath = new StringBuilder();

        // 如果有父目录，先获取父目录的完整路径
        if (directory.getDirectoryParentId() != null && !directory.getDirectoryParentId().trim().isEmpty()) {
            TTestDirectory parent = baseMapper.selectById(directory.getDirectoryParentId());
            if (parent != null && parent.getFullPath() != null) {
                fullPath.append(parent.getFullPath());
            }
        }

        // 添加当前目录名称
        if (fullPath.length() > 0) {
            fullPath.append("/");
        }
        fullPath.append(directory.getDirectoryName());

        directory.setFullPath(fullPath.toString());
    }

    /**
     * 设置路径层级字段
     */
    private void setPathLevels(TTestDirectory directory) {
        String fullPath = directory.getFullPath();
        if (fullPath != null && !fullPath.isEmpty()) {
            String[] pathParts = fullPath.split("/");

            // 设置层级
            directory.setLevel(pathParts.length);

            // 设置各级路径
            if (pathParts.length >= 1) {
                directory.setFirstPath(pathParts[0]);
            }
            if (pathParts.length >= 2) {
                directory.setSecondPath(pathParts[1]);
            }
            if (pathParts.length >= 3) {
                directory.setThirdPath(pathParts[2]);
            }
        }
    }

    @Override
    public Map<String, Object> getChildrenByParentId(String directoryParentId, String systemId, String module) {
        Assert.hasText(systemId, "系统ID不能为空");

        List<TTestDirectory> children = baseMapper.getChildrenByParentId(directoryParentId, systemId, module);

        Map<String, Object> result = new HashMap<>();
        result.put("rows", children);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDirectory(TTestDirectory directory) {
        Assert.notNull(directory, "目录信息不能为空");
        Assert.hasText(directory.getDirectoryId(), "目录ID不能为空");
        Assert.hasText(directory.getDirectoryName(), "目录名称不能为空");
        Assert.hasText(directory.getSystemId(), "系统ID不能为空");

        // 检查目录是否存在
        TTestDirectory existingDirectory = baseMapper.selectById(directory.getDirectoryId());
        if (existingDirectory == null) {
            throw new IllegalArgumentException("目录不存在");
        }

        // 如果目录名称发生变化，检查是否重复
        if (!directory.getDirectoryName().equals(existingDirectory.getDirectoryName())) {
            validateDirectoryNameUnique(directory.getDirectoryName(), directory.getDirectoryParentId(),
                                      directory.getSystemId(), directory.getDirectoryId());
            buildFullPath(directory);
            setPathLevels(directory);
        }

        // 设置默认值
        if (directory.getIsUseTestcase() == null) {
            directory.setIsUseTestcase("1");
        }
        if (directory.getIsUseTestset() == null) {
            directory.setIsUseTestset("1");
        }

        // 更新到数据库
        return baseMapper.updateById(directory) > 0;
    }

    /**
     * 验证目录名称唯一性
     * @param directoryName 目录名称
     * @param directoryParentId 父目录ID
     * @param systemId 系统ID
     * @param excludeDirectoryId 排除的目录ID（更新时使用）
     */
    private void validateDirectoryNameUnique(String directoryName, String directoryParentId,
                                           String systemId, String excludeDirectoryId) {
        List<TTestDirectory> existingDirectories = baseMapper.checkDirectoryNameExists(
            directoryName, directoryParentId, systemId, excludeDirectoryId);

        if (!existingDirectories.isEmpty()) {
            throw new IllegalArgumentException("目录名称已存在");
        }
    }

    /**
     * 更新父目录的叶子状态
     * @param directoryParentId 父目录ID
     * @param isLeafDirectory 是否为叶子目录："0"-不是；"1"-是
     */
    private void updateParentDirectoryLeafStatus(String directoryParentId, String isLeafDirectory) {
        TTestDirectory parentDirectory = baseMapper.selectById(directoryParentId);
        if (parentDirectory != null) {
            parentDirectory.setIsLeafDirectory(isLeafDirectory);
            baseMapper.updateById(parentDirectory);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDirectory(String directoryId) {
        Assert.hasText(directoryId, "目录ID不能为空");

        // 检查目录是否存在
        TTestDirectory directory = baseMapper.selectById(directoryId);
        if (directory == null) {
            throw new IllegalArgumentException("目录不存在");
        }

        // 检查是否有子目录
        List<TTestDirectory> children = baseMapper.getChildrenByParentId(directoryId, directory.getSystemId(), null);
        if (!children.isEmpty()) {
            throw new IllegalArgumentException("目录下存在子目录，无法删除");
        }

        // 保存父目录ID，用于后续更新父目录的叶子状态
        String parentId = directory.getDirectoryParentId();

        // 删除目录
        boolean result = baseMapper.deleteById(directoryId) > 0;

        // 如果有父目录，检查父目录是否还有其他子目录
        if (result && parentId != null && !parentId.trim().isEmpty()) {
            checkAndUpdateParentLeafStatus(parentId);
        }

        return result;
    }

    /**
     * 检查并更新父目录的叶子状态
     * @param parentId 父目录ID
     */
    private void checkAndUpdateParentLeafStatus(String parentId) {
        TTestDirectory parentDirectory = baseMapper.selectById(parentId);
        if (parentDirectory != null) {
            // 查询父目录下是否还有其他子目录
            List<TTestDirectory> siblings = baseMapper.getChildrenByParentId(parentId, parentDirectory.getSystemId(), null);

            // 如果没有子目录了，设置为叶子目录
            if (siblings.isEmpty()) {
                updateParentDirectoryLeafStatus(parentId, "1");
            }
        }
    }

    @Override
    public String getDirectoryIdByFullPath(String fullPath) {
        if (!StringUtils.hasText(fullPath)) {
            return null;
        }

        TTestDirectory directory = baseMapper.getDirectoryByFullPath(fullPath);
        return directory != null ? directory.getDirectoryId() : null;
    }

    @Override
    public String getDirectoryFullPath(String directoryId) {
        if (!StringUtils.hasText(directoryId)) {
            return "";
        }

        try {
            TTestDirectory directory = this.getById(directoryId);
            if (directory != null && directory.getFullPath() != null) {
                return directory.getFullPath();
            }

            return null; // 如果获取不到完整路径，返回null
        } catch (Exception e) {
            return null; // 异常时返回null
        }
    }

    @Override
    public List<String> getAllChildrenDirectoryIds(String directoryId, String systemId) {
        if (!StringUtils.hasText(directoryId)) {
            return new ArrayList<>();
        }
        
        // 验证目录是否存在且属于指定系统
        TTestDirectory directory = this.getById(directoryId);
        if (directory == null) {
            return new ArrayList<>();
        }
        
        // 如果提供了 systemId，验证目录是否属于该系统
        if (StringUtils.hasText(systemId) && !systemId.equals(directory.getSystemId())) {
            return new ArrayList<>();
        }
        
        // 使用目录的实际 systemId（如果未提供 systemId 参数）
        String actualSystemId = StringUtils.hasText(systemId) ? systemId : directory.getSystemId();
        
        // 获取父目录的完整路径
        String parentFullPath = directory.getFullPath();
        if (!StringUtils.hasText(parentFullPath)) {
            // 如果 fullPath 为空，回退到递归查询方式
            List<String> allDirectoryIds = new ArrayList<>();
            allDirectoryIds.add(directoryId);
            getAllChildrenDirectoryIdsRecursive(directoryId, actualSystemId, allDirectoryIds);
            return allDirectoryIds;
        }
        
        // 使用 fullPath 的 LIKE 查询获取所有子目录（包含当前目录）
        // 例如：父目录 fullPath 为 "ABC/DEF"，则查询：
        //   - FULLPATH = 'ABC/DEF' （当前目录）
        //   - FULLPATH LIKE 'ABC/DEF/%' （所有子目录，包括子目录的子目录）
        // 这样可以一次性获取所有子目录，避免递归查询，提高性能
        LambdaQueryWrapper<TTestDirectory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TTestDirectory::getSystemId, actualSystemId)
               .and(w -> w.eq(TTestDirectory::getFullPath, parentFullPath)
                         .or()
                         .apply("FULLPATH LIKE {0}", parentFullPath + "/%"));
        
        List<TTestDirectory> directories = this.list(wrapper);
        
        // 提取目录ID列表
        List<String> allDirectoryIds = new ArrayList<>();
        for (TTestDirectory dir : directories) {
            if (dir != null && StringUtils.hasText(dir.getDirectoryId())) {
                allDirectoryIds.add(dir.getDirectoryId());
            }
        }
        
        return allDirectoryIds;
    }

    /**
     * 递归获取所有子目录ID（备用方法，当 fullPath 为空时使用）
     * @param parentDirectoryId 父目录ID
     * @param systemId 系统ID（用于过滤，确保只查询同一系统下的子目录）
     * @param allDirectoryIds 所有目录ID列表
     */
    private void getAllChildrenDirectoryIdsRecursive(String parentDirectoryId, String systemId, List<String> allDirectoryIds) {
        if (!StringUtils.hasText(parentDirectoryId) || !StringUtils.hasText(systemId)) {
            return;
        }
        
        try {
            // 查询直接子目录，同时过滤系统ID
            LambdaQueryWrapper<TTestDirectory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TTestDirectory::getDirectoryParentId, parentDirectoryId)
                   .eq(TTestDirectory::getSystemId, systemId);
            List<TTestDirectory> children = this.list(wrapper);
            
            for (TTestDirectory child : children) {
                if (child != null && StringUtils.hasText(child.getDirectoryId())) {
                    String childId = child.getDirectoryId();
                    // 避免重复添加
                    if (!allDirectoryIds.contains(childId)) {
                        allDirectoryIds.add(childId);
                        // 递归查询子目录的子目录
                        getAllChildrenDirectoryIdsRecursive(childId, systemId, allDirectoryIds);
                    }
                }
            }
        } catch (Exception e) {
            // 查询失败时记录日志，但不影响主流程
            System.err.println("查询子目录失败: parentDirectoryId=" + parentDirectoryId + ", systemId=" + systemId + ", error=" + e.getMessage());
        }
    }

    @Override
    public TTestDirectory getDirectoryById(String directoryId) {
        Assert.hasText(directoryId, "目录ID不能为空");
        
        TTestDirectory directory = this.getById(directoryId);
        if (directory == null) {
            throw new IllegalArgumentException("目录不存在");
        }
        
        // 确保fullPath字段被填充
        // 如果数据库中的fullPath为空，尝试构建完整路径
        if (!StringUtils.hasText(directory.getFullPath())) {
            // 尝试构建完整路径
            buildFullPath(directory);
        }
        
        return directory;
    }

    @Override
    public void exportDirectory(Map<String, Object> params, HttpServletResponse response) {
        try {
            String systemId = params != null ? (String) params.get("systemId") : null;
            
            // 查询目录数据
            List<TTestDirectory> directories;
            if (StringUtils.hasText(systemId)) {
                LambdaQueryWrapper<TTestDirectory> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(TTestDirectory::getSystemId, systemId)
                       .orderByAsc(TTestDirectory::getFullPath);
                directories = this.list(wrapper);
            } else {
                directories = this.list();
            }

            // 创建Excel工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("目录数据");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"完整路径*", "用例库是否使用*", "执行库是否使用*"};
            
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
            for (TTestDirectory directory : directories) {
                Row row = sheet.createRow(rowIndex++);
                
                // 完整路径
                row.createCell(0).setCellValue(directory.getFullPath() != null ? directory.getFullPath() : "");
                // 用例库是否使用
                row.createCell(1).setCellValue(directory.getIsUseTestcase() != null ? directory.getIsUseTestcase() : "1");
                // 执行库是否使用
                row.createCell(2).setCellValue(directory.getIsUseTestset() != null ? directory.getIsUseTestset() : "1");
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 生成文件名
            String fileName = "目录数据_" + System.currentTimeMillis() + ".xlsx";
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
            
            // 写入响应流
            workbook.write(response.getOutputStream());
            workbook.close();
            
            logger.info("导出目录数据成功: 共 {} 条记录", directories.size());
            
        } catch (IOException e) {
            logger.error("导出目录数据失败", e);
            throw new RuntimeException("导出目录数据失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) {
        try {
            // 从classpath读取模板文件
            Resource resource = new ClassPathResource(TEMPLATE_PATH);
            
            if (!resource.exists()) {
                throw new IllegalStateException("模板文件不存在: " + TEMPLATE_PATH);
            }
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            
            // 对文件名进行URL编码，支持中文
            String fileName = "DirectoryTempalte.xlsx";
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
            
            logger.info("模板下载成功: {}", TEMPLATE_PATH);
            
        } catch (IOException e) {
            logger.error("下载模板失败: {}", e.getMessage(), e);
            throw new RuntimeException("下载模板失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importDirectory(MultipartFile file, String systemId) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        if (file == null || file.isEmpty()) {
            result.put("success", false);
            result.put("message", "上传文件不能为空");
            return result;
        }

        if (!StringUtils.hasText(systemId)) {
            result.put("success", false);
            result.put("message", "系统ID不能为空");
            return result;
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            result.put("success", false);
            result.put("message", "文件格式不正确，请上传Excel文件(.xlsx或.xls)");
            return result;
        }

        try (InputStream inputStream = file.getInputStream(); 
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                result.put("success", false);
                result.put("message", "Excel中缺少工作表");
                return result;
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                result.put("success", false);
                result.put("message", "Excel缺少表头");
                return result;
            }

            // 解析数据行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }
                
                try {
                    TTestDirectory directory = parseRowToDirectory(row, headerRow, i + 1, systemId);
                    if (directory != null) {
                        // 根据完整路径和系统ID查找是否已存在
                        TTestDirectory existing = null;
                        if (StringUtils.hasText(directory.getFullPath())) {
                            // 先查找是否有相同完整路径的目录
                            LambdaQueryWrapper<TTestDirectory> wrapper = new LambdaQueryWrapper<>();
                            wrapper.eq(TTestDirectory::getFullPath, directory.getFullPath())
                                   .eq(TTestDirectory::getSystemId, systemId);
                            List<TTestDirectory> existingList = this.list(wrapper);
                            if (!existingList.isEmpty()) {
                                existing = existingList.get(0);
                            }
                        }

                        if (existing != null) {
                            // 更新现有目录
                            directory.setDirectoryId(existing.getDirectoryId());
                            // 保留原有的父目录ID和层级信息
                            directory.setDirectoryParentId(existing.getDirectoryParentId());
                            directory.setLevel(existing.getLevel());
                            directory.setFirstPath(existing.getFirstPath());
                            directory.setSecondPath(existing.getSecondPath());
                            directory.setThirdPath(existing.getThirdPath());
                            
                            boolean updated = this.updateById(directory);
                            if (updated) {
                                successCount++;
                            } else {
                                failCount++;
                                errors.add(String.format("第%d行更新失败: 完整路径=%s", i + 1, directory.getFullPath()));
                            }
                        } else {
                            // 创建新目录
                            directory.setDirectoryId(UUID.randomUUID().toString().replace("-", ""));
                            directory.setSystemId(systemId);
                            directory.setCreateTime(LocalDateTime.now());
                            
                            // 构建完整路径和层级信息（如果还没有）
                            if (!StringUtils.hasText(directory.getFullPath())) {
                                buildFullPath(directory);
                            }
                            setPathLevels(directory);
                            
                            // 设置默认值
                            if (directory.getIsLeafDirectory() == null) {
                                directory.setIsLeafDirectory("1");
                            }

                            boolean saved = this.save(directory);
                            if (saved) {
                                successCount++;
                                // 如果有父目录，更新父目录的叶子状态
                                if (StringUtils.hasText(directory.getDirectoryParentId())) {
                                    updateParentDirectoryLeafStatus(directory.getDirectoryParentId(), "0");
                                }
                            } else {
                                failCount++;
                                errors.add(String.format("第%d行保存失败: 完整路径=%s", i + 1, directory.getFullPath()));
                            }
                        }
                    }
                } catch (Exception ex) {
                    failCount++;
                    errors.add(String.format("第%d行导入失败: %s", i + 1, ex.getMessage()));
                }
            }

        } catch (IOException e) {
            logger.error("导入目录数据失败", e);
            result.put("success", false);
            result.put("message", "导入失败: " + e.getMessage());
            return result;
        }

        result.put("success", true);
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("errors", errors);
        result.put("message", String.format("导入完成: 成功 %d 条，失败 %d 条", successCount, failCount));

        logger.info("导入目录数据完成: 成功={}, 失败={}", successCount, failCount);

        return result;
    }

    /**
     * 解析行数据为目录对象
     */
    private TTestDirectory parseRowToDirectory(Row row, Row headerRow, int rowNumber, String systemId) {
        TTestDirectory directory = new TTestDirectory();
        
        // 创建列名映射
        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String headerValue = getCellValueAsString(cell);
                if (headerValue != null) {
                    // 去除表头中的*号
                    headerValue = headerValue.replace("*", "").trim();
                    headerMap.put(headerValue, i);
                }
            }
        }

        // 解析各字段
        try {
            // 完整路径（必填）
            String fullPath = null;
            if (headerMap.containsKey("完整路径")) {
                fullPath = getCellValueAsString(row.getCell(headerMap.get("完整路径")));
            }
            if (!StringUtils.hasText(fullPath)) {
                throw new IllegalArgumentException("完整路径不能为空");
            }
            fullPath = fullPath.trim();
            directory.setFullPath(fullPath);

            // 从完整路径中提取目录名称和父目录信息
            String[] pathParts = fullPath.split("/");
            if (pathParts.length == 0) {
                throw new IllegalArgumentException("完整路径格式错误");
            }
            
            // 目录名称是路径的最后一部分
            String directoryName = pathParts[pathParts.length - 1];
            directory.setDirectoryName(directoryName);
            
            // 设置层级
            directory.setLevel(pathParts.length);
            
            // 设置各级路径
            if (pathParts.length >= 1) {
                directory.setFirstPath(pathParts[0]);
            }
            if (pathParts.length >= 2) {
                directory.setSecondPath(pathParts[1]);
            }
            if (pathParts.length >= 3) {
                directory.setThirdPath(pathParts[2]);
            }

            // 构建父目录路径（如果有父目录）
            if (pathParts.length > 1) {
                StringBuilder parentPath = new StringBuilder();
                for (int i = 0; i < pathParts.length - 1; i++) {
                    if (i > 0) {
                        parentPath.append("/");
                    }
                    parentPath.append(pathParts[i]);
                }
                // 根据父目录路径和系统ID查找父目录ID
                LambdaQueryWrapper<TTestDirectory> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(TTestDirectory::getFullPath, parentPath.toString())
                       .eq(TTestDirectory::getSystemId, systemId);
                List<TTestDirectory> parentList = this.list(wrapper);
                if (!parentList.isEmpty()) {
                    directory.setDirectoryParentId(parentList.get(0).getDirectoryId());
                } else {
                    throw new IllegalArgumentException("父目录不存在: " + parentPath.toString() + " (系统ID: " + systemId + ")");
                }
            } else if (pathParts.length == 1) {
                // level为1的目录，父目录ID设置为systemId
                directory.setDirectoryParentId(systemId);
            }

            // 用例库是否使用（必填）
            String isUseTestcase = null;
            if (headerMap.containsKey("用例库是否使用")) {
                isUseTestcase = getCellValueAsString(row.getCell(headerMap.get("用例库是否使用")));
            }
            if (!StringUtils.hasText(isUseTestcase)) {
                isUseTestcase = "1"; // 默认值
            }
            directory.setIsUseTestcase(isUseTestcase.trim());

            // 执行库是否使用（必填）
            String isUseTestset = null;
            if (headerMap.containsKey("执行库是否使用")) {
                isUseTestset = getCellValueAsString(row.getCell(headerMap.get("执行库是否使用")));
            }
            if (!StringUtils.hasText(isUseTestset)) {
                isUseTestset = "1"; // 默认值
            }
            directory.setIsUseTestset(isUseTestset.trim());

            // 设置系统ID
            directory.setSystemId(systemId);

            // 设置默认值
            directory.setIsLeafDirectory("1"); // 新创建的目录默认为叶子目录

        } catch (Exception e) {
            throw new IllegalArgumentException("解析数据失败: " + e.getMessage(), e);
        }

        return directory;
    }

    /**
     * 获取单元格值作为字符串
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
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
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

}
