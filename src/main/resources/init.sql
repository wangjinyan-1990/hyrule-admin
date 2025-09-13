-- 创建数据库
CREATE DATABASE IF NOT EXISTS hyrule DEFAULT CHARACTER SET utf8mb4;

USE hyrule;


/*Table structure for table `t_sys_counter` */

DROP TABLE IF EXISTS `t_sys_counter`;

CREATE TABLE `t_sys_counter` (
  `COUNTER_ID` int NOT NULL AUTO_INCREMENT,
  `COUNTER_NAME` varchar(384) DEFAULT NULL,
  `CURRENT_NUMBER` int DEFAULT NULL,
  `PREFIX` varchar(30) DEFAULT NULL,
  `COUNTER_LENGTH` tinyint DEFAULT NULL,
  PRIMARY KEY (`COUNTER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4;

/*Data for the table `t_sys_counter` */

insert  into `t_sys_counter`(`COUNTER_ID`,`COUNTER_NAME`,`CURRENT_NUMBER`,`PREFIX`,`COUNTER_LENGTH`) values 
(1,'sysCode',1,'sys-',3),
(2,'requireCode',1,'req',5),
(3,'testcaseCode',1,'tc',5),
(4,'bugCode',1,'bug',6);


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

/*Table structure for table `t_sys_menu` */

DROP TABLE IF EXISTS `t_sys_menu`;

CREATE TABLE `t_sys_menu` (
  `MENU_ID` int NOT NULL AUTO_INCREMENT,
  `COMPONENT` varchar(100) DEFAULT NULL,
  `PATH` varchar(100) DEFAULT NULL,
  `REDIRECT` varchar(100) DEFAULT NULL,
  `NAME` varchar(100) DEFAULT NULL,
  `TITLE` varchar(100) DEFAULT NULL,
  `ICON` varchar(100) DEFAULT NULL,
  `PARENT_ID` int DEFAULT NULL,
  `IS_LEAF` varchar(1) DEFAULT NULL,
  `HIDDEN` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`MENU_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4;

/*Data for the table `t_sys_menu` */

insert  into `t_sys_menu`(`MENU_ID`,`COMPONENT`,`PATH`,`REDIRECT`,`NAME`,`TITLE`,`ICON`,`PARENT_ID`,`IS_LEAF`,`HIDDEN`) values 
(1,'Layout','/sys','/sys/user','userManage','系统管理','sys',NULL,'0',0),
(2,'/sys/user','user',NULL,'user','用户管理','userManage',1,'1',0),
(3,'/sys/role','role',NULL,'role','角色管理','roleManage',1,'1',0),
(4,'Layout','/test','/test/test1','test','测试模块','form',NULL,'0',0),
(5,'/test/test1','test1',NULL,'test1','功能1','el-icon-s-help',4,'1',0),
(6,'/test/test2','test2',NULL,'test2','功能2','el-icon-s-help',4,'1',0),
(7,'/test/test3','test3',NULL,'test3','功能3','el-icon-s-help',4,'1',0),
(8,'Layout','/example','/example/table','Example','Example','el-icon-s-help',NULL,'0',0),
(9,'/example/table','table',NULL,'Table','Table','table',8,'1',0),
(10,'/example/tree','tree',NULL,'Tree','Tree','tree',8,'1',0),
(11,'Layout','/form',NULL,NULL,'Form','form',NULL,'0',0),
(12,'/form/index','index',NULL,'Form','Form','form',11,'1',0),
(13,'Layout','/nested','/nested/menu1','Nested','Nested','nested',NULL,'0',0),
(14,'/nested/menu1/index','menu1',NULL,'Menu1','Menu1',NULL,13,'0',0),
(15,'/nested/menu1/menu1-1','menu1-1',NULL,'Menu1-1','Menu1-1',NULL,14,'1',0),
(16,'/nested/menu1/menu1-2','menu1-2',NULL,'Menu1-2','Menu1-2',NULL,14,'0',0),
(17,'/nested/menu1/menu1-2/menu1-2-1','menu1-2-1',NULL,'Menu1-2-1','Menu1-2-1',NULL,16,'1',0),
(18,'/nested/menu1/menu1-2/menu1-2-2','menu1-2-2',NULL,'Menu1-2-2','Menu1-2-2',NULL,16,'1',0),
(19,'/nested/menu1/menu1-3','menu1-3',NULL,'Menu1-3','Menu1-3',NULL,14,'1',0),
(20,'/nested/menu2/index','menu2',NULL,'Menu2','menu2',NULL,13,'1',0),
(21,'Layout','external-link',NULL,NULL,'External Link','link',NULL,'0',0),
(22,NULL,'https://panjiachen.github.io/vue-element-admin-site/#/',NULL,NULL,'External Link','link',21,'1',0),
(23,'/sys/menu','menu',NULL,'menu','菜单管理','menuManage',1,'1',0);

/*Table structure for table `t_sys_role_menu` */

DROP TABLE IF EXISTS `t_sys_role_menu`;

CREATE TABLE `t_sys_role_menu` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `ROLE_ID` varchar(32) DEFAULT NULL,
  `MENU_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4;

/*Data for the table `t_sys_role_menu` */

insert  into `t_sys_role_menu`(`ID`,`ROLE_ID`,`MENU_ID`) values 
(1,'0001','1'),
(2,'0001','2'),
(3,'0001','3'),
(4,'0001','4'),
(5,'0001','5'),
(6,'0001','6'),
(7,'0001','7'),
(8,'0001','8'),
(9,'0001','9'),
(10,'0001','10'),
(11,'0001','11'),
(12,'0001','12'),
(13,'0001','13'),
(14,'0001','14'),
(15,'0001','15'),
(16,'0001','16'),
(17,'0001','17'),
(18,'0001','18'),
(19,'0001','19'),
(20,'0001','20'),
(21,'0001','21'),
(22,'0001','22');