package com.king.configuration.deploy.service;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FilesPusherToBranchService {

    private static final Logger logger = LoggerFactory.getLogger(FilesPusherToBranchService.class);

    /**
     *
     * @param gitlabUrl     项目 HTTP 地址（http://host/user/repo.git）
     * @param privateToken  GitLab Personal Access Token（需 write_repository）
     * @param sourceBranch  提供文件内容的分支
     * @param targetBranch  要被更新的分支
     * @param filePaths     想搬移的文件路径（支持通配符，如 src/main/java/*.java）
     */
    public static void pushFiles(String gitlabUrl,
                                 String privateToken,
                                 String sourceBranch,
                                 String targetBranch,
                                 String sendTestInfo,
                                 List<String> filePaths) throws Exception {

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
                throw new RuntimeException("源分支不存在: " + sourceBranch);
            }
            String srcCommit = srcRef.getObjectId().getName();

            /* 2. 检出目标分支（本地工作分支） */
            git.checkout()
                    .setName(targetBranch)
                    .setStartPoint("origin/" + targetBranch)
                    .call();

            /* 3. 把指定文件从源分支覆盖到工作区 */
            for (String path : filePaths) {
                git.checkout()
                        .setStartPoint(srcCommit)
                        .addPath(path)
                        .call();
            }

            /* 4. 提交 */
            git.add().addFilepattern(".").call();
            git.commit()
                    .setMessage(sendTestInfo + ": " + filePaths + " from " + sourceBranch)
                    .setAllowEmpty(false)
                    .call();

            /* 5. 推送 */
            git.push()
                    .setRemote("origin")
                    .setRefSpecs(new RefSpec(targetBranch + ":" + targetBranch))
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider("oauth2", privateToken))
                    .call();

        } finally {
            // 6. 清理临时目录
            FileUtils.deleteDirectory(new File(tempDir));
        }
    }

    /* 生成线程唯一目录名：/tmp/versionsync_20260117230345123_t-42_857 */
    private static String genTempCloneDir() {
        String ts = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
        long threadId = Thread.currentThread().getId();
        int random = ThreadLocalRandom.current().nextInt(100, 1000);
        return String.format("/tmp/versionsync_%s_t-%d_%d", ts, threadId, random);
    }

}
