package com.king.tools.networkPolicyList.service.impl;

import com.king.tools.networkPolicyList.dto.NetworkListDto;
import com.king.tools.networkPolicyList.service.INetworkPolicyListService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 网络策略清单Service实现类
 */
@Service("networkPolicyListServiceImpl")
public class NetworkPolicyListServiceImpl implements INetworkPolicyListService {

    private static final Logger logger = LoggerFactory.getLogger(NetworkPolicyListServiceImpl.class);

    /**
     * 模板文件路径
     */
    private static final String TEMPLATE_PATH = "templates/网络策略开通清单模板.xlsx";

    /**
     * 下载导入模板
     * @param response HTTP响应对象
     */
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
            String fileName = "网络策略开通清单模板.xlsx";
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

    /**
     * 加工网络策略清单
     * 读取上传的Excel文件，处理后生成新的Excel文件返回
     * @param file 上传的Excel文件
     * @param response HTTP响应对象
     */
    @Override
    public void processNetworkPolicyList(MultipartFile file, HttpServletResponse response) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            throw new IllegalArgumentException("文件格式不正确，请上传Excel文件(.xlsx或.xls)");
        }

        Workbook inputWorkbook = null;
        Workbook outputWorkbook = null;

        try {
            // 1. 读取上传的Excel文件
            logger.info("开始处理网络策略清单文件: {}", originalFilename);
            InputStream inputStream = file.getInputStream();
            inputWorkbook = new XSSFWorkbook(inputStream);

            // 查找名为"工单模板"的sheet页
            Sheet inputSheet = inputWorkbook.getSheet("工单模板");
            if (inputSheet == null) {
                // 如果找不到"工单模板"，尝试查找其他可能的名称
                logger.warn("未找到名为'工单模板'的工作表，尝试查找其他工作表");
                int sheetCount = inputWorkbook.getNumberOfSheets();
                for (int i = 0; i < sheetCount; i++) {
                    String sheetName = inputWorkbook.getSheetName(i);
                    logger.info("工作表 {}: {}", i, sheetName);
                }
                throw new IllegalArgumentException("Excel中缺少名为'工单模板'的工作表，请确保存在该工作表");
            }
            logger.info("找到工作表: 工单模板");

            Row headerRow = inputSheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel缺少表头");
            }

            // 2. 读取表头，建立列名映射（保持列的顺序）
            Map<String, Integer> headerMap = new LinkedHashMap<>();
            List<String> headerOrder = new ArrayList<>(); // 保存列名的顺序
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    String headerValue = getCellValueAsString(cell);
                    if (StringUtils.hasText(headerValue)) {
                        headerMap.put(headerValue, i);
                        headerOrder.add(headerValue); // 按顺序保存列名
                    }
                }
            }

            logger.info("读取到表头: {}", headerOrder);

            // 3. 读取数据行
            List<NetworkListDto> dataList = new ArrayList<>();
            for (int i = 1; i <= inputSheet.getLastRowNum(); i++) {
                Row row = inputSheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                NetworkListDto rowData = new NetworkListDto();
                for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
                    String columnName = entry.getKey();
                    int columnIndex = entry.getValue();
                    Cell cell = row.getCell(columnIndex);
                    String cellValue = getCellValueAsString(cell);
                    setDtoFieldValue(rowData, columnName, cellValue);
                }
                dataList.add(rowData);
            }

            logger.info("读取到 {} 行数据", dataList.size());

            // 4. 处理数据（这里可以根据具体需求添加处理逻辑）
            List<NetworkListDto> processedData = processData(dataList);

            // 5. 生成新的Excel文件（保留原始workbook的其他sheet页）
            outputWorkbook = new XSSFWorkbook();
            
            // 5.1 先复制原始workbook中除了"工单模板"之外的所有sheet页
            int sheetCount = inputWorkbook.getNumberOfSheets();
            for (int i = 0; i < sheetCount; i++) {
                Sheet originalSheet = inputWorkbook.getSheetAt(i);
                String sheetName = inputWorkbook.getSheetName(i);
                
                // 跳过"工单模板"sheet，稍后会创建新的
                if ("工单模板".equals(sheetName)) {
                    continue;
                }
                
                // 复制其他sheet页
                Sheet newSheet = outputWorkbook.createSheet(sheetName);
                copySheet(originalSheet, newSheet, outputWorkbook);
                logger.info("已复制sheet页: {}", sheetName);
            }
            
            // 5.2 创建处理后的"工单模板"sheet页（使用原始名称）
            Sheet outputSheet = outputWorkbook.createSheet("工单模板");

            // 创建表头样式
            CellStyle headerStyle = outputWorkbook.createCellStyle();
            Font headerFont = outputWorkbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 创建表头（按照原始列的顺序）
            Row outputHeaderRow = outputSheet.createRow(0);
            // 使用headerOrder保持列的顺序
            for (int i = 0; i < headerOrder.size(); i++) {
                String columnName = headerOrder.get(i);
                Cell cell = outputHeaderRow.createCell(i);
                cell.setCellValue(columnName);
                cell.setCellStyle(headerStyle);
            }

            // 填充处理后的数据（按照原始列的顺序）
            int rowIndex = 1;
            for (NetworkListDto rowData : processedData) {
                Row outputRow = outputSheet.createRow(rowIndex++);
                for (int i = 0; i < headerOrder.size(); i++) {
                    String columnName = headerOrder.get(i);
                    String cellValue = getDtoFieldValue(rowData, columnName);
                    outputRow.createCell(i).setCellValue(cellValue);
                }
            }

            // 自动调整列宽
            for (int i = 0; i < headerOrder.size(); i++) {
                outputSheet.autoSizeColumn(i);
                // 设置最小列宽
                outputSheet.setColumnWidth(i, Math.max(outputSheet.getColumnWidth(i), 3000));
            }

            // 6. 生成文件名并返回
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String timestamp = sdf.format(new Date());
            String fileName = "网络策略清单_" + timestamp + ".xlsx";

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

            outputWorkbook.write(response.getOutputStream());
            response.getOutputStream().flush();

            logger.info("网络策略清单加工完成，生成文件: {}", fileName);

        } catch (IOException e) {
            logger.error("加工网络策略清单失败: {}", e.getMessage(), e);
            throw new RuntimeException("加工网络策略清单失败: " + e.getMessage(), e);
        } finally {
            // 关闭工作簿
            if (inputWorkbook != null) {
                try {
                    inputWorkbook.close();
                } catch (IOException ignored) {
                }
            }
            if (outputWorkbook != null) {
                try {
                    outputWorkbook.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 处理数据（根据具体需求实现处理逻辑）
     * 第一次加工：如果A端IP地址或B端IP地址中包含多个IP，则进行拆分
     * 第二次加工：根据A端IP地址、B端IP地址和B端端口号三者组合去重，并重新排序序号
     * @param dataList 原始数据列表
     * @return 处理后的数据列表
     */
    private List<NetworkListDto> processData(List<NetworkListDto> dataList) {
        // 第一次加工：拆分数据
        List<NetworkListDto> processedData = new ArrayList<>();
        
        for (NetworkListDto rowData : dataList) {
            // 获取A端和B端IP地址
            String aSideIpAddress = rowData.getASideIpAddress() != null ? rowData.getASideIpAddress() : "";
            String bSideIpAddress = rowData.getBSideIpAddress() != null ? rowData.getBSideIpAddress() : "";
            
            // 解析IP地址列表（可能用换行符、逗号、分号等分隔）
            List<String> aSideIpList = parseIpAddresses(aSideIpAddress);
            List<String> bSideIpList = parseIpAddresses(bSideIpAddress);
            
            // 如果A端或B端IP数量大于1，需要进行拆分
            if (aSideIpList.size() > 1 || bSideIpList.size() > 1) {
                // 如果A端IP列表为空，至少保留一个空字符串
                if (aSideIpList.isEmpty()) {
                    aSideIpList.add("");
                }
                // 如果B端IP列表为空，至少保留一个空字符串
                if (bSideIpList.isEmpty()) {
                    bSideIpList.add("");
                }
                
                // 双层循环拆分：A端IP和B端IP各取一个，组合成新行
                for (String aIp : aSideIpList) {
                    for (String bIp : bSideIpList) {
                        // 创建新行数据（复制原行所有数据）
                        NetworkListDto newRow = copyDto(rowData);
                        // 更新A端IP地址和B端IP地址
                        newRow.setASideIpAddress(aIp);
                        newRow.setBSideIpAddress(bIp);
                        processedData.add(newRow);
                    }
                }
                
                logger.debug("拆分行数据：A端IP数量={}, B端IP数量={}, 拆分后行数={}", 
                        aSideIpList.size(), bSideIpList.size(), 
                        aSideIpList.size() * bSideIpList.size());
            } else {
                // A端和B端都只有1个IP（或为空），无需拆分，直接添加
                processedData.add(rowData);
            }
        }
        
        logger.info("第一次加工完成：原始行数={}, 拆分后行数={}", dataList.size(), processedData.size());
        
        // 第二次加工：去重和重新排序
        List<NetworkListDto> deduplicatedData = deduplicateAndReorder(processedData);
        
        logger.info("第二次加工完成：去重前行数={}, 去重后行数={}", processedData.size(), deduplicatedData.size());
        
        return deduplicatedData;
    }

    /**
     * 第二次加工：根据A端IP地址、B端IP地址和B端端口号三者组合去重，并重新排序序号
     * @param dataList 数据列表
     * @return 去重并重新排序后的数据列表
     */
    private List<NetworkListDto> deduplicateAndReorder(List<NetworkListDto> dataList) {
        // 使用LinkedHashSet保持插入顺序，同时用于去重
        Set<String> uniqueKeys = new LinkedHashSet<>();
        List<NetworkListDto> deduplicatedData = new ArrayList<>();
        
        for (NetworkListDto rowData : dataList) {
            // 构建唯一键：A端IP地址 + B端IP地址 + B端端口号
            String aSideIp = rowData.getASideIpAddress() != null ? rowData.getASideIpAddress() : "";
            String bSideIp = rowData.getBSideIpAddress() != null ? rowData.getBSideIpAddress() : "";
            String bSidePort = rowData.getBSidePortNumber() != null ? rowData.getBSidePortNumber() : "";
            
            // 使用分隔符组合成唯一键（使用特殊分隔符避免冲突）
            String uniqueKey = aSideIp + "|||" + bSideIp + "|||" + bSidePort;
            
            // 如果该组合不存在，则添加
            if (!uniqueKeys.contains(uniqueKey)) {
                uniqueKeys.add(uniqueKey);
                deduplicatedData.add(rowData);
            } else {
                logger.debug("发现重复数据，已去重：A端IP={}, B端IP={}, B端端口={}", aSideIp, bSideIp, bSidePort);
            }
        }
        
        // 重新排序序号
        for (int i = 0; i < deduplicatedData.size(); i++) {
            deduplicatedData.get(i).setSerialNumber(i + 1);
        }
        
        logger.info("去重完成：去重前={}行, 去重后={}行, 重新排序序号完成", dataList.size(), deduplicatedData.size());
        
        return deduplicatedData;
    }

    /**
     * 复制DTO对象
     * @param source 源DTO对象
     * @return 新的DTO对象
     */
    private NetworkListDto copyDto(NetworkListDto source) {
        NetworkListDto target = new NetworkListDto();
        target.setSerialNumber(source.getSerialNumber());
        target.setASideSystemName(source.getASideSystemName());
        target.setASideInRowAttribute(source.getASideInRowAttribute());
        target.setASideIpAddress(source.getASideIpAddress());
        target.setASidePortNumber(source.getASidePortNumber());
        target.setAccessDirection(source.getAccessDirection());
        target.setBSideSystemName(source.getBSideSystemName());
        target.setBSideInRowAttribute(source.getBSideInRowAttribute());
        target.setBSideIpAddress(source.getBSideIpAddress());
        target.setBSidePortNumber(source.getBSidePortNumber());
        target.setPortType(source.getPortType());
        target.setIsLongConnection(source.getIsLongConnection());
        target.setBusinessDescription(source.getBusinessDescription());
        return target;
    }

    /**
     * 根据列名设置DTO字段值
     * @param dto DTO对象
     * @param columnName 列名
     * @param value 值
     */
    private void setDtoFieldValue(NetworkListDto dto, String columnName, String value) {
        if (dto == null || columnName == null) {
            return;
        }
        
        switch (columnName) {
            case "序号":
                try {
                    dto.setSerialNumber(StringUtils.hasText(value) ? Integer.parseInt(value) : null);
                } catch (NumberFormatException e) {
                    dto.setSerialNumber(null);
                }
                break;
            case "A端系统名称":
                dto.setASideSystemName(value);
                break;
            case "A端行内属性":
                dto.setASideInRowAttribute(value);
                break;
            case "A端IP地址":
                dto.setASideIpAddress(value);
                break;
            case "A端端口号":
                dto.setASidePortNumber(value);
                break;
            case "访问方向":
                dto.setAccessDirection(value);
                break;
            case "B端系统名称":
                dto.setBSideSystemName(value);
                break;
            case "B端行内属性":
                dto.setBSideInRowAttribute(value);
                break;
            case "B端IP地址":
                dto.setBSideIpAddress(value);
                break;
            case "B端端口号":
                dto.setBSidePortNumber(value);
                break;
            case "端口类型":
                dto.setPortType(value);
                break;
            case "是否为长连接":
                dto.setIsLongConnection(value);
                break;
            case "业务描述":
                dto.setBusinessDescription(value);
                break;
            default:
                logger.warn("未知的列名: {}", columnName);
                break;
        }
    }

    /**
     * 根据列名获取DTO字段值
     * @param dto DTO对象
     * @param columnName 列名
     * @return 字段值
     */
    private String getDtoFieldValue(NetworkListDto dto, String columnName) {
        if (dto == null || columnName == null) {
            return "";
        }
        
        switch (columnName) {
            case "序号":
                return dto.getSerialNumber() != null ? String.valueOf(dto.getSerialNumber()) : "";
            case "A端系统名称":
                return dto.getASideSystemName() != null ? dto.getASideSystemName() : "";
            case "A端行内属性":
                return dto.getASideInRowAttribute() != null ? dto.getASideInRowAttribute() : "";
            case "A端IP地址":
                return dto.getASideIpAddress() != null ? dto.getASideIpAddress() : "";
            case "A端端口号":
                return dto.getASidePortNumber() != null ? dto.getASidePortNumber() : "";
            case "访问方向":
                return dto.getAccessDirection() != null ? dto.getAccessDirection() : "";
            case "B端系统名称":
                return dto.getBSideSystemName() != null ? dto.getBSideSystemName() : "";
            case "B端行内属性":
                return dto.getBSideInRowAttribute() != null ? dto.getBSideInRowAttribute() : "";
            case "B端IP地址":
                return dto.getBSideIpAddress() != null ? dto.getBSideIpAddress() : "";
            case "B端端口号":
                return dto.getBSidePortNumber() != null ? dto.getBSidePortNumber() : "";
            case "端口类型":
                return dto.getPortType() != null ? dto.getPortType() : "";
            case "是否为长连接":
                return dto.getIsLongConnection() != null ? dto.getIsLongConnection() : "";
            case "业务描述":
                return dto.getBusinessDescription() != null ? dto.getBusinessDescription() : "";
            default:
                logger.warn("未知的列名: {}", columnName);
                return "";
        }
    }

    /**
     * 解析IP地址字符串，提取所有IP地址
     * 支持换行符、逗号、分号等分隔符
     * @param ipAddressString IP地址字符串（可能包含多个IP，用各种分隔符分隔）
     * @return IP地址列表
     */
    private List<String> parseIpAddresses(String ipAddressString) {
        List<String> ipList = new ArrayList<>();
        
        if (!StringUtils.hasText(ipAddressString)) {
            return ipList;
        }
        
        // 先按换行符分割
        String[] lines = ipAddressString.split("[\r\n]+");
        
        for (String line : lines) {
            line = line.trim();
            if (!StringUtils.hasText(line)) {
                continue;
            }
            
            // 如果一行中包含逗号或分号，继续分割
            String[] parts = line.split("[,;，；]+");
            for (String part : parts) {
                part = part.trim();
                if (StringUtils.hasText(part)) {
                    ipList.add(part);
                }
            }
        }
        
        return ipList;
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
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
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
     * 复制sheet页（包括所有行和单元格）
     * @param sourceSheet 源sheet页
     * @param targetSheet 目标sheet页
     * @param targetWorkbook 目标workbook（用于创建样式）
     */
    private void copySheet(Sheet sourceSheet, Sheet targetSheet, Workbook targetWorkbook) {
        // 复制所有行
        for (int i = 0; i <= sourceSheet.getLastRowNum(); i++) {
            Row sourceRow = sourceSheet.getRow(i);
            if (sourceRow == null) {
                continue;
            }
            
            Row targetRow = targetSheet.createRow(i);
            targetRow.setHeight(sourceRow.getHeight());
            
            // 复制所有单元格
            for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
                Cell sourceCell = sourceRow.getCell(j);
                if (sourceCell == null) {
                    continue;
                }
                
                Cell targetCell = targetRow.createCell(j);
                
                // 复制单元格值
                switch (sourceCell.getCellType()) {
                    case STRING:
                        targetCell.setCellValue(sourceCell.getStringCellValue());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(sourceCell)) {
                            targetCell.setCellValue(sourceCell.getDateCellValue());
                        } else {
                            targetCell.setCellValue(sourceCell.getNumericCellValue());
                        }
                        break;
                    case BOOLEAN:
                        targetCell.setCellValue(sourceCell.getBooleanCellValue());
                        break;
                    case FORMULA:
                        targetCell.setCellFormula(sourceCell.getCellFormula());
                        break;
                    case BLANK:
                        targetCell.setBlank();
                        break;
                    default:
                        break;
                }
                
                // 复制单元格样式（简化处理，只复制基本样式）
                CellStyle sourceStyle = sourceCell.getCellStyle();
                CellStyle targetStyle = targetWorkbook.createCellStyle();
                targetStyle.cloneStyleFrom(sourceStyle);
                targetCell.setCellStyle(targetStyle);
            }
        }
        
        // 复制列宽
        if (sourceSheet.getRow(0) != null) {
            for (int i = 0; i < sourceSheet.getRow(0).getLastCellNum(); i++) {
                int columnWidth = sourceSheet.getColumnWidth(i);
                targetSheet.setColumnWidth(i, columnWidth);
            }
        }
    }
}
