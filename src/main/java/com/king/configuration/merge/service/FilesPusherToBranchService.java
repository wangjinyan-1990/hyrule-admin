package com.king.configuration.merge.service;

import com.king.common.Result;
import org.eclipse.jgit.lib.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.util.io.DisabledOutputStream;

@Service
public class FilesPusherToBranchService {

    private static final Logger logger = LoggerFactory.getLogger(FilesPusherToBranchService.class);

    private static final String PARENT_DIR = "/home/hyrule/tmp";   // 固定父路径

    /**
     * 使用JGit推送文件到分支
     * 把目标分支拉下来 → 把源分支里指定文件覆盖进来 → 提交并直接推回目标分支
     *
     * @param gitlabUrl     项目 HTTP 地址（http://host/user/repo.git）
     * @param privateToken  GitLab Personal Access Token（需 write_repository）
     * @param sourceBranch  提供文件内容的分支
     * @param targetBranch  要被更新的分支
     * @param sendTestInfo  送测单信息（用于提交消息）
     * @param filePaths     想搬移的文件路径（支持通配符，如 src/main/java/*.java）
     * @return Result<String> 成功时返回"新增X个,删除Y个,修改Z个"，失败时返回失败原因
     */
    public static Result<String> pushFiles(String gitlabUrl,
                                           String privateToken,
                                           String sourceBranch,
                                           String targetBranch,
                                           String sendTestInfo,
                                           List<String> filePaths) {
        String tempDir = genTempCloneDir();
        try (Git git = Git.cloneRepository()
                .setURI(gitlabUrl.replace("http://", "http://oauth2:" + privateToken + "@"))
                .setDirectory(new File(tempDir))
                .setBranch(targetBranch)
                // 浅克隆 API 在 5.13 不存在，直接省略
                .call()) {

            Repository repo = git.getRepository();

            /* 1. 拿到源分支最新提交 */
            Ref srcRef = repo.findRef("refs/remotes/origin/" + sourceBranch);
            if (srcRef == null) {
                String errorMsg = "源分支不存在: " + sourceBranch;
                logger.error(errorMsg);
                return Result.error(errorMsg);
            }
            String srcCommit = srcRef.getObjectId().getName();

            Ref tgtRef = repo.findRef("refs/remotes/origin/" + targetBranch);
            if (tgtRef == null) {
                String errorMsg = "目标分支不存在: " + targetBranch;
                logger.error(errorMsg);
                return Result.error(errorMsg);
            }

            /* 2. 推送前统计差异（目标分支 vs 源分支） */
            int[] diffCount = countDiff(git, "origin/" + targetBranch, "origin/" + sourceBranch);
            int addedFiles = diffCount[0];
            int deletedFiles = diffCount[1];
            int modifiedFiles = diffCount[2];
            logger.info(">>> 待推送差异：新增={} 删除={} 修改={}", addedFiles, deletedFiles, modifiedFiles);

            /* 3. 检出目标分支（本地工作分支） */
            git.checkout()
                    .setName(targetBranch)
                    .setStartPoint("origin/" + targetBranch)
                    .call();

            /* 4. 把指定文件从源分支覆盖到工作区 */
            for (String path : filePaths) {
                git.checkout()
                        .setStartPoint(srcCommit)
                        .addPath(path)
                        .call();
            }

            /* 5. 提交 */
            git.add().addFilepattern(".").call();
            String commitMessage = sendTestInfo + ": 共涉及" + filePaths.size() + "个文件 from " + sourceBranch + " to " + targetBranch;
            git.commit()
                    .setMessage(commitMessage)
                    .setAllowEmpty(false)
                    .call();

            /* 6. 推送 */
            git.push()
                    .setRemote("origin")
                    .setRefSpecs(new RefSpec(targetBranch + ":" + targetBranch))
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider("oauth2", privateToken))
                    .call();

            // 构建成功结果信息：新增X个,删除Y个,修改Z个
            String successMsg = String.format("新增%d个,删除%d个,修改%d个", addedFiles, deletedFiles, modifiedFiles);

            logger.info("JGit推送文件到分支成功: 源分支={}, 目标分支={}, 文件数={}, 差异统计={}",
                    sourceBranch, targetBranch, filePaths.size(), successMsg);

            return Result.success(successMsg, "文件推送成功");

        } catch (Exception e) {
            String errorMsg = "JGit推送文件到分支失败: " + e.getMessage();
            logger.error(errorMsg, e);
            return Result.error(errorMsg);
        } finally {
            // 清理临时目录
            try {
                FileUtils.deleteDirectory(new File(tempDir));
            } catch (Exception e) {
                logger.warn("清理临时目录失败: {}", tempDir, e);
            }
        }
    }

    /* 生成线程唯一子目录：/home/hyrule/tmp/tmpBranch_20260117230345123_857 */
    private static String genTempCloneDir() {
        String ts   = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
                .format(LocalDateTime.now());
        int random  = ThreadLocalRandom.current().nextInt(100, 1000);
        String dir  = "tmpBranch_" + ts + "_" + random;   // 子目录名
        return Paths.get(PARENT_DIR, dir).toString();     // 拼成完整路径
    }

    /* 统计两个提交之间的新增/删除/修改文件数 */
    private static int[] countDiff(Git git, String oldCommit, String newCommit) throws Exception {
        int[] cnt = new int[3];        // 0-新增 1-删除 2-修改
        try (DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
            df.setRepository(git.getRepository());
            ObjectId oldId = oldCommit == null ? null : git.getRepository().resolve(oldCommit);
            ObjectId newId = newCommit == null ? null : git.getRepository().resolve(newCommit);

            List<DiffEntry> diffs = df.scan(oldId, newId);
            for (DiffEntry e : diffs) {
                switch (e.getChangeType()) {
                    case ADD:    cnt[0]++; break;
                    case DELETE: cnt[1]++; break;
                    case MODIFY: cnt[2]++; break;
                    default:                       // COPY / RENAME 忽略
                }
            }
        }
        return cnt;
    }


}
