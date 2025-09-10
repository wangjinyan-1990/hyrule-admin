package com.king;

import com.king.sys.login.dto.LoginResult;
import com.king.sys.login.service.ILoginervice;
import com.king.sys.user.entity.TSysUser;
import com.king.sys.user.mapper.UserMapper;
import com.king.sys.user.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class HyruleAdminApplicationTests {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private ILoginervice loginService;

    @Test
    void contextLoads() {
        // 测试Spring上下文是否能正常加载
    }

    @Test
    void testGetRoleNameByUserId() {
        // 测试根据用户ID获取角色名称
        String userId = "1"; // 假设测试用户ID为1
        List<String> roleNames = userMapper.getRoleNameByUserId(userId);
        
        System.out.println("用户ID: " + userId + " 的角色列表: " + roleNames);
        
        // 断言结果不为null
        assert roleNames != null : "角色列表不应为null";
        
        // 打印每个角色名称
        if (!roleNames.isEmpty()) {
            System.out.println("找到的角色:");
            roleNames.forEach(roleName -> System.out.println("- " + roleName));
        } else {
            System.out.println("该用户没有分配任何角色");
        }
    }

    @Test
    void testSelectUserList() {
        List<TSysUser> users = userMapper.selectUserList();
        // 断言结果不为null
        assert users != null : "用户列表不应为null";

        // 打印每个角色名称
        if (!users.isEmpty()) {
            System.out.println("找到的用户:");
            users.forEach(user -> System.out.println("- " + user));
        } else {
            System.out.println("没有用户");
        }
    }

    @Test
    void testUpdateUser() {
        // 测试用户更新功能
        System.out.println("开始测试用户更新功能...");
        
        // 首先获取一个现有用户进行测试
        List<TSysUser> users = userMapper.selectUserList();
        if (users.isEmpty()) {
            System.out.println("没有现有用户，跳过更新测试");
            return;
        }
        
        TSysUser testUser = users.get(0);
        System.out.println("测试用户: " + testUser.getUserId() + " - " + testUser.getUserName());
        
        // 保存原始信息
        String originalUserName = testUser.getUserName();
        String originalPhone = testUser.getPhone();
        String originalEmail = testUser.getEmail();
        String originalLoginName = testUser.getLoginName();
        
        // 准备更新数据
        TSysUser updateUser = new TSysUser();
        updateUser.setUserId(testUser.getUserId());
        updateUser.setUserName(originalUserName + "_updated");
        updateUser.setLoginName(originalLoginName + "_updated");
        updateUser.setPhone(originalPhone);
        updateUser.setEmail(originalEmail != null ? originalEmail + "_updated" : "test@example.com");
        updateUser.setStatus(testUser.getStatus());
        
        try {
            // 执行更新
            userService.updateUser(updateUser);
            System.out.println("用户更新成功！");
            
            // 验证更新结果
            TSysUser updatedUser = userMapper.selectById(testUser.getUserId());
            assert updatedUser != null : "更新后的用户不应为null";
            assert updatedUser.getUserName().equals(updateUser.getUserName()) : "用户名更新失败";
            assert updatedUser.getLoginName().equals(updateUser.getLoginName()) : "登录名更新失败";
            assert updatedUser.getEmail().equals(updateUser.getEmail()) : "邮箱更新失败";
            
            System.out.println("更新验证成功！");
            System.out.println("新用户名: " + updatedUser.getUserName());
            System.out.println("新登录名: " + updatedUser.getLoginName());
            System.out.println("新邮箱: " + updatedUser.getEmail());
            
            // 恢复原始数据
            TSysUser restoreUser = new TSysUser();
            restoreUser.setUserId(testUser.getUserId());
            restoreUser.setUserName(originalUserName);
            restoreUser.setLoginName(originalLoginName);
            restoreUser.setPhone(originalPhone);
            restoreUser.setEmail(originalEmail);
            restoreUser.setStatus(testUser.getStatus());
            
            userService.updateUser(restoreUser);
            System.out.println("数据已恢复到原始状态");
            
        } catch (Exception e) {
            System.out.println("用户更新测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void testLoginWithSpecificErrors() {
        // 测试登录的具体错误信息
        System.out.println("开始测试登录错误信息...");
        
        // 测试1：登录名为空
        TSysUser user1 = new TSysUser();
        user1.setPassword("123456");
        LoginResult result1 = loginService.login(user1);
        System.out.println("登录名为空测试: " + result1.getErrorMessage());
        assert !result1.isSuccess() : "登录名为空应该失败";
        
        // 测试2：密码为空
        TSysUser user2 = new TSysUser();
        user2.setLoginName("testuser");
        LoginResult result2 = loginService.login(user2);
        System.out.println("密码为空测试: " + result2.getErrorMessage());
        assert !result2.isSuccess() : "密码为空应该失败";
        
        // 测试3：用户不存在
        TSysUser user3 = new TSysUser();
        user3.setLoginName("nonexistentuser");
        user3.setPassword("123456");
        LoginResult result3 = loginService.login(user3);
        System.out.println("用户不存在测试: " + result3.getErrorMessage());
        assert !result3.isSuccess() : "用户不存在应该失败";
        
        // 测试4：获取一个真实用户进行测试
        List<TSysUser> users = userMapper.selectUserList();
        if (!users.isEmpty()) {
            TSysUser realUser = users.get(0);
            System.out.println("使用真实用户测试: " + realUser.getLoginName());
            
            // 测试5：密码错误
            TSysUser user4 = new TSysUser();
            user4.setLoginName(realUser.getLoginName());
            user4.setPassword("wrongpassword");
            LoginResult result4 = loginService.login(user4);
            System.out.println("密码错误测试: " + result4.getErrorMessage());
            assert !result4.isSuccess() : "密码错误应该失败";
            
            // 测试6：用户被禁用（如果状态不为0）
            if (realUser.getStatus() != 0) {
                TSysUser user5 = new TSysUser();
                user5.setLoginName(realUser.getLoginName());
                user5.setPassword("123456"); // 假设这是正确密码
                LoginResult result5 = loginService.login(user5);
                System.out.println("用户被禁用测试: " + result5.getErrorMessage());
                assert !result5.isSuccess() : "用户被禁用应该失败";
            }
        }
        
        System.out.println("登录错误信息测试完成！");
    }

}