package com.king.test.usecaseManage.usecaseRepository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.test.usecaseManage.requireRepository.entity.TfRequirepoint;
import com.king.test.usecaseManage.usecaseRepository.entity.TfUsecase;
import com.king.test.usecaseManage.usecaseRepository.entity.TfUsecaseHistory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ITfUsecaseService extends IService<TfUsecase> {

    Map<String, Object> getUsecasePage(int pageNo,
                                       int pageSize,
                                       String systemId,
                                       String directoryId,
                                       String usecaseName,
                                       String usecaseType,
                                       String usecaseNature,
                                       String prority,
                                       String isSmokeTest,
                                       String creatorId);

    TfUsecase getUsecaseDetail(String usecaseId);

    boolean createUsecase(TfUsecase usecase);

    boolean updateUsecase(TfUsecase usecase);

    boolean deleteUsecase(String usecaseId);

    boolean batchDeleteUsecases(List<String> usecaseIds);

    List<TfUsecase> listUsecasesForExport(String systemId,
                                          String directoryId,
                                          String usecaseName,
                                          String usecaseType,
                                          String usecaseNature,
                                          String prority,
                                          String isSmokeTest,
                                          String creatorId);

    void exportUsecasesToExcel(List<TfUsecase> usecases, HttpServletResponse response);

    void downloadTemplate(HttpServletResponse response);

    Map<String, Object> importUsecases(MultipartFile file, String systemId, String directoryId) throws Exception;

    Map<String, Object> getUsecaseStatistics(String systemId, String directoryId);

    List<Map<String, Object>> getUsecaseTypeStatistics(String systemId, String directoryId);

    List<Map<String, Object>> getUsecaseStatusStatistics(String systemId, String directoryId);

    TfUsecase copyUsecase(String usecaseId, Map<String, Object> options);

    boolean moveUsecases(List<String> usecaseIds, String targetDirectoryId);

    Map<String, Object> getUsecaseHistory(String usecaseId, int pageNo, int pageSize);

    List<TfUsecaseHistory> listUsecaseHistory(String usecaseId);

    List<TfRequirepoint> getLinkedRequirePoints(String usecaseId);

    boolean linkRequirePoints(String usecaseId, List<String> requirePointIds);

    boolean unlinkRequirePoints(String usecaseId, List<String> requirePointIds);
}
