-- 创建数据库
CREATE DATABASE IF NOT EXISTS hyrule DEFAULT CHARACTER SET utf8mb4;

USE hyrule;


DROP TABLE IF EXISTS `t_sys_role`;

CREATE TABLE `t_sys_role` (
  `ROLE_ID` varchar(32) NOT NULL COMMENT '角色ID ',
  `ROLE_NAME` varchar(100) NOT NULL COMMENT '角色名称 ',
  `SORT_NO` decimal(4,0) NOT NULL COMMENT '排序号 ',
  `REMARK` varchar(400) DEFAULT NULL COMMENT '备注 ',
  PRIMARY KEY (`ROLE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `t_sys_role` */

insert  into `t_sys_role`(`ROLE_ID`,`ROLE_NAME`,`SORT_NO`,`REMARK`) values
('0001','开发人员',1,'开发人员使用'),
('0002','测试人员',2,'测试人员使用'),
('0007','管理角色',7,'系统管理'),
('0008','配置人员',3,'配置人员使用');

/*Table structure for table `t_sys_role_user` */

DROP TABLE IF EXISTS `t_sys_role_user`;

CREATE TABLE `t_sys_role_user` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'UUID ',
  `ROLE_ID` varchar(32) NOT NULL COMMENT '角色ID ',
  `USER_ID` varchar(32) NOT NULL COMMENT '用户ID ',
  PRIMARY KEY (`ID`),
  KEY `I_t_sys_role_user_ROLE_ID` (`ROLE_ID`),
  KEY `I_t_sys_role_user_USER_ID` (`USER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

/*Data for the table `t_sys_role_user` */

insert  into `t_sys_role_user`(`ID`,`ROLE_ID`,`USER_ID`) values
(1,'0001','2'),
(2,'0002','lili');

/*Table structure for table `t_sys_user` */

DROP TABLE IF EXISTS `t_sys_user`;

CREATE TABLE `t_sys_user` (
  `USER_ID` varchar(32) NOT NULL,
  `LOGIN_NAME` varchar(30) CHARACTER SET utf8 NOT NULL,
  `PASSWORD` varchar(100) CHARACTER SET utf8 NOT NULL,
  `USER_NAME` varchar(50) CHARACTER SET utf8 NOT NULL,
  `EMAIL` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `PHONE` varchar(20) CHARACTER SET utf8 DEFAULT NULL,
  `STATUS` int(11) NOT NULL COMMENT '用户状态',
  `SORT_NO` decimal(8,0) NOT NULL COMMENT '排序号',
  PRIMARY KEY (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `t_sys_user` */

insert  into `t_sys_user`(`USER_ID`,`LOGIN_NAME`,`PASSWORD`,`USER_NAME`,`EMAIL`,`PHONE`,`STATUS`,`SORT_NO`) values
('2','zhangsan','e10adc3949ba59abbe56e057f20f883e','zhangsan','zhangsan@gmail.com','13966667777',1,0),
('3','lisi','e10adc3949ba59abbe56e057f20f883e','lisi','lisi@gmail.com','13966667778',1,0),
('4','wangwu','e10adc3949ba59abbe56e057f20f883e','wangwu','wangwu@gmail.com','13966667772',1,0),
('5','zhaoer','e10adc3949ba59abbe56e057f20f883e','zhaoer','zhaoer@gmail.com','13966667776',0,0),
('6','songliu','123456','songliu','songliu@gmail.com','13966667771',1,0),
('admin','admin','e10adc3949ba59abbe56e057f20f883e','admin','super@aliyun.com','18677778888',1,0),
('lili','lili','e10adc3949ba59abbe56e057f20f883e','李莉','lili123@189.com','18765342789',1,20250906),
('yanke','yanke','e10adc3949ba59abbe56e057f20f883e','严科','yanke920618@163.com',NULL,1,0);

/*Table structure for table `x_menu` */

DROP TABLE IF EXISTS `x_menu`;

CREATE TABLE `x_menu` (
  `menu_id` int(11) NOT NULL AUTO_INCREMENT,
  `component` varchar(100) DEFAULT NULL,
  `path` varchar(100) DEFAULT NULL,
  `redirect` varchar(100) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `icon` varchar(100) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `is_leaf` varchar(1) DEFAULT NULL,
  `hidden` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;

/*Data for the table `x_menu` */

insert  into `x_menu`(`menu_id`,`component`,`path`,`redirect`,`name`,`title`,`icon`,`parent_id`,`is_leaf`,`hidden`) values
(1,'Layout','/user','/user/list','userManage','用户管理','userManage',0,'N',0),
(2,'user/user','list',NULL,'userList','用户列表','userList',1,'Y',0),
(3,'user/role','role',NULL,'roleList','角色列表','role',1,'Y',0),
(4,'user/permission','permission',NULL,'permissionList','权限列表','permission',1,'Y',0);

/*Table structure for table `x_role_menu` */

DROP TABLE IF EXISTS `x_role_menu`;

CREATE TABLE `x_role_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL,
  `menu_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

/*Data for the table `x_role_menu` */

insert  into `x_role_menu`(`id`,`role_id`,`menu_id`) values
(1,1,1),
(2,1,2),
(3,1,3),
(4,1,4);
