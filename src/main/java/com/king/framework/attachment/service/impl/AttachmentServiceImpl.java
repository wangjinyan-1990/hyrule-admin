package com.king.framework.attachment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.utils.JwtUtil;
import com.king.framework.attachment.entity.TfAttachment;
import com.king.framework.attachment.mapper.AttachmentMapper;
import com.king.framework.attachment.service.IAttachmentService;
import com.king.framework.param.service.ISysParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 附件Service实现类
 */
@Service("attachmentServiceImpl")
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, TfAttachment> implements IAttachmentService {

    @Autowired
    @Qualifier("sysParamServiceImpl")
    private ISysParamService sysParamService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public TfAttachment uploadAttachment(MultipartFile file, String module, String relateId) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }
        
        try {
            // 获取当前用户ID
            String uploadUserId = getCurrentUserId();
            if (!StringUtils.hasText(uploadUserId)) {
                throw new RuntimeException("无法获取当前用户信息，请重新登录");
            }
            
            // 生成唯一文件名
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            
            // 生成服务器文件名：module_yyyyMMddhhmmssSSS.fileExt
            String moduleName = StringUtils.hasText(module) ? module : "default";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String timestamp = sdf.format(new Date());
            String serverFileName = moduleName + "_" + timestamp + fileExtension;
            
            // 创建上传目录
            String baseUploadPath = sysParamService.getParamValueById("fileUploadPath");
            if (!StringUtils.hasText(baseUploadPath)) {
                baseUploadPath = "/uploads"; // 默认上传路径
            }
            
            // 按年月和module分目录存放
            SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy/MM");
            String yearMonth = yearMonthFormat.format(new Date());
            
            // 构建完整路径：baseUploadPath/yyyy/MM/module/
            Path uploadDir = Paths.get(baseUploadPath, yearMonth, moduleName);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            // 保存文件 - 使用 try-with-resources 确保输入流被正确关闭
            Path filePath = uploadDir.resolve(serverFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            
            // 创建附件记录
            TfAttachment attachment = new TfAttachment();
            attachment.setAttachmentId(UUID.randomUUID().toString().replace("-", ""));
            attachment.setOriginalFileName(originalFileName);
            attachment.setServerFileName(serverFileName);
            attachment.setAttachmentSize((int) file.getSize());
            attachment.setUploadDate(new Date());
            attachment.setUploadUserId(uploadUserId);
            attachment.setUploadPath(filePath.toString());
            attachment.setModule(module);
            attachment.setRelateId(relateId);
            
            // 保存到数据库
            boolean saveResult = this.save(attachment);
            if (!saveResult) {
                throw new RuntimeException("保存附件记录到数据库失败");
            }
            
            return attachment;
            
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户ID
     * @return 当前用户ID
     */
    private String getCurrentUserId() {
        try {
            // 获取当前请求
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            
            HttpServletRequest request = attributes.getRequest();
            
            // 从请求头获取token
            String token = request.getHeader("Authorization");
            if (token == null) {
                token = request.getHeader("X-Token");
            }
            
            if (StringUtils.hasText(token)) {
                return jwtUtil.getUserIdFromToken(token);
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public List<TfAttachment> getAttachments(String module, String relateId) {
        LambdaQueryWrapper<TfAttachment> wrapper = new LambdaQueryWrapper<>();
        
        // 根据传入的参数动态组合查询条件
        if (StringUtils.hasText(module)) {
            wrapper.eq(TfAttachment::getModule, module);
        }
        
        if (StringUtils.hasText(relateId)) {
            wrapper.eq(TfAttachment::getRelateId, relateId);
        }
        
        // 按上传时间倒序排列
        wrapper.orderByDesc(TfAttachment::getUploadDate);
        
        return this.list(wrapper);
    }
    
    @Override
    public List<TfAttachment> getAttachmentsByRelateId(String relateId) {
        if (!StringUtils.hasText(relateId)) {
            return null;
        }
        
        LambdaQueryWrapper<TfAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfAttachment::getRelateId, relateId);
        wrapper.orderByDesc(TfAttachment::getUploadDate);
        
        return this.list(wrapper);
    }

    @Override
    public boolean deleteAttachment(String attachmentId) {
        if (!StringUtils.hasText(attachmentId)) {
            return false;
        }
        
        // 查询附件信息
        TfAttachment attachment = this.getById(attachmentId);
        if (attachment == null) {
            return false;
        }
        
        try {
            // 删除物理文件
            Path filePath = Paths.get(attachment.getUploadPath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            
            // 删除数据库记录
            return this.removeById(attachmentId);
            
        } catch (IOException e) {
            throw new RuntimeException("删除附件失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean batchDeleteAttachments(List<String> attachmentIds) {
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return false;
        }
        
        try {
            int successCount = 0;
            int failCount = 0;
            
            for (String attachmentId : attachmentIds) {
                try {
                    boolean deleted = deleteAttachment(attachmentId);
                    if (deleted) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                    // 记录错误但继续处理其他文件
                    System.err.println("删除附件失败 [" + attachmentId + "]: " + e.getMessage());
                }
            }
            
            // 如果至少有一个删除成功，则认为批量删除成功
            return successCount > 0;
            
        } catch (Exception e) {
            throw new RuntimeException("批量删除附件失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> viewTextFile(String attachmentId) {
        if (!StringUtils.hasText(attachmentId)) {
            return null;
        }
        
        // 查询附件信息
        TfAttachment attachment = this.getById(attachmentId);
        if (attachment == null) {
            return null;
        }
        
        // 获取文件扩展名
        String fileName = attachment.getOriginalFileName();
        String fileExtension = "";
        if (fileName != null && fileName.contains(".")) {
            fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        
        // 支持的文本文件类型
        Set<String> supportedTextTypes = new HashSet<>(Arrays.asList(
            "txt", "md", "json", "xml", "html", "css", "js", "java", "py", "sql", 
            "yaml", "yml", "properties", "log", "csv", "sh", "bat"
        ));
        
        // 检查是否是支持的文本文件类型
        if (!supportedTextTypes.contains(fileExtension)) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "不支持的文件类型");
            errorResult.put("fileType", fileExtension);
            errorResult.put("supportedTypes", supportedTextTypes);
            return errorResult;
        }
        
        // 读取文件内容
        File file = new File(attachment.getUploadPath());
        if (!file.exists()) {
            return null;
        }
        
        try {
            // 尝试检测文件编码
            Charset charset = detectCharset(file);
            
            // 读取文件内容
            String content;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), charset))) {
                content = reader.lines().collect(Collectors.joining("\n"));
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("attachmentId", attachment.getAttachmentId());
            result.put("fileName", attachment.getOriginalFileName());
            result.put("fileType", fileExtension);
            result.put("fileSize", attachment.getAttachmentSize());
            result.put("content", content);
            result.put("encoding", charset.name());
            result.put("lines", content.split("\n").length);
            result.put("uploadDate", attachment.getUploadDate());
            
            return result;
            
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 检测文件编码
     * @param file 文件
     * @return 字符集
     */
    private Charset detectCharset(File file) {
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead = is.read(buffer);
            
            if (bytesRead > 0) {
                // 检查BOM
                if (bytesRead >= 3 && buffer[0] == (byte) 0xEF && buffer[1] == (byte) 0xBB && buffer[2] == (byte) 0xBF) {
                    return StandardCharsets.UTF_8;
                }
                if (bytesRead >= 2 && buffer[0] == (byte) 0xFF && buffer[1] == (byte) 0xFE) {
                    return StandardCharsets.UTF_16LE;
                }
                if (bytesRead >= 2 && buffer[0] == (byte) 0xFE && buffer[1] == (byte) 0xFF) {
                    return StandardCharsets.UTF_16BE;
                }
                
                // 尝试UTF-8解码
                String testString = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                if (testString.indexOf('\uFFFD') == -1) { // 没有替换字符，可能是UTF-8
                    return StandardCharsets.UTF_8;
                }
            }
            
            // 默认使用UTF-8
            return StandardCharsets.UTF_8;
            
        } catch (IOException e) {
            return StandardCharsets.UTF_8;
        }
    }
}
