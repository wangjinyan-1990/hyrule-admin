package com.king.framework.attachment.controller;

import com.king.common.Result;
import com.king.framework.attachment.entity.TfAttachment;
import com.king.framework.attachment.service.IAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 附件Controller
 */
@RestController
@RequestMapping("/framework/attachment")
public class AttachmentController {
    
    @Autowired
    @Qualifier("attachmentServiceImpl")
    private IAttachmentService attachmentService;
    
    /**
     * 上传附件
     * @param file 上传的文件
     * @param module 模块名称
     * @param relateId 关联ID
     * @return 上传结果
     */
    @PostMapping("/upload")
    public Result<TfAttachment> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "module", required = false) String module,
            @RequestParam(value = "relateId", required = false) String relateId) {
        try {
            TfAttachment attachment = attachmentService.uploadAttachment(file, module, relateId);
            return Result.success(attachment, "文件上传成功");
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量上传附件
     * @param files 上传的文件列表
     * @param module 模块名称
     * @param relateId 关联ID
     * @return 上传结果
     */
    @PostMapping("/upload/batch")
    public Result<List<TfAttachment>> uploadAttachments(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "module", required = false) String module,
            @RequestParam(value = "relateId", required = false) String relateId) {
        try {
            List<TfAttachment> attachments = new java.util.ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    TfAttachment attachment = attachmentService.uploadAttachment(file, module, relateId);
                    attachments.add(attachment);
                }
            }
            return Result.success(attachments, "批量上传成功");
        } catch (Exception e) {
            return Result.error("批量上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据模块和关联ID查询附件列表（组合查询）
     * @param module 模块名称（可选）
     * @param relateId 关联ID（可选）
     * @return 附件列表
     */
    @GetMapping("/list")
    public Result<List<TfAttachment>> getAttachments(
            @RequestParam(value = "module", required = false) String module,
            @RequestParam(value = "relateId", required = false) String relateId) {
        try {
            List<TfAttachment> attachments = attachmentService.getAttachments(module, relateId);
            return Result.success(attachments, "获取附件列表成功");
        } catch (Exception e) {
            return Result.error("查询附件失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据关联ID查询附件列表
     * @param relateId 关联ID
     * @return 附件列表
     */
    @GetMapping("/list/relate/{relateId}")
    public Result<List<TfAttachment>> getAttachmentsByRelateId(@PathVariable("relateId") String relateId) {
        try {
            List<TfAttachment> attachments = attachmentService.getAttachmentsByRelateId(relateId);
            return Result.success(attachments);
        } catch (Exception e) {
            return Result.error("查询附件失败: " + e.getMessage());
        }
    }

    /**
     * 根据附件ID查询附件信息
     * @param attachmentId 附件ID
     * @return 附件信息
     */
    @GetMapping("/{attachmentId}")
    public Result<TfAttachment> getAttachmentById(@PathVariable("attachmentId") String attachmentId) {
        try {
            TfAttachment attachment = attachmentService.getById(attachmentId);
            if (attachment != null) {
                return Result.success(attachment);
            } else {
                return Result.error("附件不存在");
            }
        } catch (Exception e) {
            return Result.error("查询附件失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除附件
     * @param attachmentId 附件ID
     * @return 删除结果
     */
    @DeleteMapping("/{attachmentId}")
    public Result<Boolean> deleteAttachment(@PathVariable("attachmentId") String attachmentId) {
        try {
            boolean success = attachmentService.deleteAttachment(attachmentId);
            if (success) {
                return Result.success(true, "删除附件成功");
            } else {
                return Result.error("删除失败，附件不存在");
            }
        } catch (Exception e) {
            return Result.error("删除附件失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量删除附件
     * @param request 包含附件ID列表的请求体
     * @return 删除结果
     */
    @DeleteMapping("/batch/delete")
    public Result<Boolean> batchDeleteAttachments(@RequestBody Map<String, List<String>> request) {
        try {
            List<String> attachmentIds = request.get("attachmentIds");
            if (attachmentIds == null || attachmentIds.isEmpty()) {
                return Result.error("附件ID列表不能为空");
            }
            
            boolean success = attachmentService.batchDeleteAttachments(attachmentIds);
            if (success) {
                return Result.success(true, "成功删除 " + attachmentIds.size() + " 个附件");
            } else {
                return Result.error("批量删除失败");
            }
        } catch (Exception e) {
            return Result.error("批量删除附件失败: " + e.getMessage());
        }
    }
    
    /**
     * 下载附件
     * @param attachmentId 附件ID
     * @return 文件流
     */
    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable("attachmentId") String attachmentId) {
        try {
            TfAttachment attachment = attachmentService.getById(attachmentId);
            if (attachment == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 获取文件
            File file = new File(attachment.getUploadPath());
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            
            // 对文件名进行URL编码，支持中文
            String encodedFileName = URLEncoder.encode(attachment.getOriginalFileName(), StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            
            // 设置响应头
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 查看文本文件内容
     * @param attachmentId 附件ID
     * @return 文件内容（JSON格式）
     */
    @GetMapping("/viewTextFile/{attachmentId}")
    public Result<Map<String, Object>> viewTextFile(@PathVariable("attachmentId") String attachmentId) {
        try {
            TfAttachment attachment = attachmentService.getById(attachmentId);
            if (attachment == null) {
                return Result.error("附件不存在");
            }
            
            // 获取文件
            File file = new File(attachment.getUploadPath());
            if (!file.exists()) {
                return Result.error("文件不存在");
            }
            
            // 获取文件扩展名
            String fileName = attachment.getOriginalFileName();
            String fileExtension = "";
            if (fileName != null && fileName.contains(".")) {
                fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            }
            
            // 检查是否是支持的文本文件类型
            String[] supportedTypes = {"txt", "md", "json", "xml", "html", "css", "js", 
                                      "java", "py", "sql", "yaml", "yml", "properties", 
                                      "log", "csv", "sh", "bat"};
            boolean isSupported = false;
            for (String type : supportedTypes) {
                if (type.equals(fileExtension)) {
                    isSupported = true;
                    break;
                }
            }
            
            if (!isSupported) {
                return Result.error("不支持的文件类型: " + fileExtension);
            }
            
            // 检测文件编码
            java.nio.charset.Charset detectedCharset = detectFileCharset(file);
            
            // 读取文件内容（Java 8 兼容方式）
            String content;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), detectedCharset))) {
                content = reader.lines().collect(Collectors.joining("\n"));
            }
            
            // 构建返回结果
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("attachmentId", attachment.getAttachmentId());
            result.put("fileName", attachment.getOriginalFileName());
            result.put("fileType", fileExtension);
            result.put("fileSize", attachment.getAttachmentSize());
            result.put("content", content);
            result.put("encoding", detectedCharset.name());
            result.put("lines", content.split("\n").length);
            result.put("uploadDate", attachment.getUploadDate());
            
            return Result.success(result, "获取文件内容成功");
            
        } catch (Exception e) {
            return Result.error("查看文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 检测文件编码
     * @param file 文件
     * @return 字符集
     */
    private java.nio.charset.Charset detectFileCharset(File file) {
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead = is.read(buffer);
            
            if (bytesRead > 0) {
                // 检查BOM (Byte Order Mark)
                if (bytesRead >= 3 && buffer[0] == (byte) 0xEF && 
                    buffer[1] == (byte) 0xBB && buffer[2] == (byte) 0xBF) {
                    return StandardCharsets.UTF_8;
                }
                if (bytesRead >= 2 && buffer[0] == (byte) 0xFF && buffer[1] == (byte) 0xFE) {
                    return StandardCharsets.UTF_16LE;
                }
                if (bytesRead >= 2 && buffer[0] == (byte) 0xFE && buffer[1] == (byte) 0xFF) {
                    return StandardCharsets.UTF_16BE;
                }
                
                // 检测是否是UTF-8（无BOM）
                boolean possibleUTF8 = true;
                for (int i = 0; i < bytesRead; i++) {
                    int b = buffer[i] & 0xFF;
                    
                    // 检查多字节UTF-8序列
                    if (b >= 0x80) {
                        int followBytes = 0;
                        if ((b & 0xE0) == 0xC0) followBytes = 1;      // 110xxxxx
                        else if ((b & 0xF0) == 0xE0) followBytes = 2; // 1110xxxx
                        else if ((b & 0xF8) == 0xF0) followBytes = 3; // 11110xxx
                        else {
                            possibleUTF8 = false;
                            break;
                        }
                        
                        // 验证后续字节
                        for (int j = 0; j < followBytes; j++) {
                            i++;
                            if (i >= bytesRead || (buffer[i] & 0xC0) != 0x80) {
                                possibleUTF8 = false;
                                break;
                            }
                        }
                        if (!possibleUTF8) break;
                    }
                }
                
                if (possibleUTF8) {
                    return StandardCharsets.UTF_8;
                }
                
                // 如果不是UTF-8，尝试GBK（中文常用编码）
                try {
                    java.nio.charset.Charset gbk = java.nio.charset.Charset.forName("GBK");
                    return gbk;
                } catch (Exception e) {
                    // GBK不可用，使用UTF-8作为后备
                }
            }
            
        } catch (IOException e) {
            // 出错时使用UTF-8作为默认值
        }
        
        return StandardCharsets.UTF_8;
    }
}
