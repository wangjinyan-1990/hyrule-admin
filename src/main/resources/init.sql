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
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `t_sys_counter` */

insert  into `t_sys_counter`(`COUNTER_ID`,`COUNTER_NAME`,`CURRENT_NUMBER`,`PREFIX`,`COUNTER_LENGTH`) values 
(1,'sysCode',1,'sys-',3),
(2,'requireCode',1,'req',5),
(3,'testcaseCode',1,'tc',5),
(4,'bugCode',1,'bug',6);

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
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `t_sys_menu` */

insert  into `t_sys_menu`(`MENU_ID`,`COMPONENT`,`PATH`,`REDIRECT`,`NAME`,`TITLE`,`ICON`,`PARENT_ID`,`IS_LEAF`,`HIDDEN`) values 
(1,'Layout','/sys','/sys','userManage','系统管理','sys',NULL,'0',0),
(2,'/sys/user','user',NULL,'user','用户管理','userManage',1,'1',0),
(3,'/sys/role','role',NULL,'role','角色管理','roleManage',1,'1',0),
(4,'Layout','/test','/test','test','测试模块','form',NULL,'0',0),
(5,'/test/testSystem','testSystem',NULL,'testSystem','测试系统维护','testSystemManage',4,'1',0),
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
(23,'/sys/menu','menu',NULL,'menu','菜单管理','menuManage',1,'1',0),
(26,'/sys/org','/org',NULL,'org','机构管理','orgManage',1,'1',0);

/*Table structure for table `t_sys_org` */

DROP TABLE IF EXISTS `t_sys_org`;

CREATE TABLE `t_sys_org` (
  `ORG_ID` varchar(32) NOT NULL COMMENT '机构ID ',
  `ORG_NAME` varchar(100) NOT NULL COMMENT '机构名称 ',
  `PARENT_ORG_ID` varchar(32) DEFAULT NULL COMMENT '上级机构ID ',
  `ORG_LEVEL` decimal(1,0) NOT NULL COMMENT '机构级次 ',
  `SORT_NO` decimal(4,0) NOT NULL COMMENT '排序号 ',
  `ORG_STATUS` char(1) DEFAULT NULL COMMENT '机构状态 ',
  `REMARK` varchar(400) DEFAULT NULL COMMENT '备注 ',
  PRIMARY KEY (`ORG_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `t_sys_org` */

insert  into `t_sys_org`(`ORG_ID`,`ORG_NAME`,`PARENT_ORG_ID`,`ORG_LEVEL`,`SORT_NO`,`ORG_STATUS`,`REMARK`) values 
('1000','汉东农信','',1,1,'A',''),
('100001','科技部','1000',2,1,'A',''),
('10000101','开发一科','100001',3,1,'A',''),
('10000102','开发二科','100001',3,2,'A',''),
('10000103','测试科','100001',3,3,'A',''),
('100002','信贷管理部','1000',2,2,'A',''),
('100003','会计结算部','1000',2,3,'A',''),
('100004','电子银行部','1000',2,4,'A',''),
('2000','京州银行','',1,2,'A',''),
('200001','京州借调人员','2000',2,2,'A',''),
('200002','京州科技','2000',2,2,'A',''),
('3000','外包公司','',1,3,'A',''),
('300001','神州数码','3000',2,1,'A',''),
('300002','至恒融兴','3000',2,2,'A',''),
('300003','赞同科技','3000',2,3,'A',''),
('300004','中油瑞飞','3000',2,4,'A',''),
('300005','中电金信','3000',2,5,'A',''),
('300006','东华软件','3000',2,6,'A',''),
('300007','安硕信息','3000',2,7,'A',''),
('300008','银信博荣','3000',2,8,'A',''),
('300009','法本信息','3000',2,9,'A',''),
('300010','融信易安','3000',2,10,'A',''),
('300011','润天远景','3000',2,11,'A',''),
('300012','元年科技','3000',2,12,'A',''),
('300013','易丰远景','3000',2,13,'A',''),
('300014','北京银丰','3000',2,14,'A',''),
('300015','银港科技','3000',2,15,'A',''),
('300016','数字天堂','3000',2,16,'A',''),
('300017','北京联银通','3000',2,17,'A',''),
('300018','宇信科技','3000',2,18,'A',''),
('300019','新希望金科','3000',2,19,'A',''),
('300020','北京得安','3000',2,20,'A',''),
('300021','普元信息','3000',2,21,'A',''),
('300022','上海屹通','3000',2,22,'A',''),
('300023','明宇未来','3000',2,23,'A',''),
('300024','信雅达','3000',2,24,'A',''),
('300025','高伟达','3000',2,25,'A',''),
('300026','恒生电子','3000',2,26,'A',''),
('300027','用友金融','3000',2,27,'A',''),
('300028','华软金科','3000',2,28,'A',''),
('300029','启明星辰','3000',2,29,'A',''),
('300030','梦网科技','3000',2,30,'A',''),
('300031','云从科技','3000',2,31,'A',''),
('300032','科蓝软件','3000',2,32,'A',''),
('300033','捷点科技','3000',2,33,'A',''),
('300034','先进数通','3000',2,34,'A',''),
('300035','紫宸公司','3000',2,35,'A',''),
('300036','明略数据','3000',2,36,'A',''),
('300037','广州天维','3000',2,37,'A',''),
('300038','远眺科技','3000',2,38,'A',''),
('300039','江苏国光','3000',2,39,'A',''),
('300040','雁联计算','3000',2,40,'A',''),
('300041','华毅软件','3000',2,41,'A',''),
('300042','杭州衡泰','3000',2,42,'A',''),
('300043','新华三','3000',2,43,'A',''),
('300044','京北方','3000',2,44,'A',''),
('300045','明朝万达','3000',2,45,'A',''),
('300046','银之杰','3000',2,46,'A',''),
('300047','北京明略','3000',2,47,'A',''),
('300048','融嘉合创','3000',2,48,'A',''),
('300049','卓越耐特','3000',2,49,'A',''),
('300050','骏敏科技','3000',2,50,'A',''),
('300051','壹肆零伍','3000',2,51,'A',''),
('300052','中税汇金','3000',2,52,'A',''),
('300053','西安南天','3000',2,53,'A',''),
('300054','中投恒升','3000',2,54,'A',''),
('300055','西安布瑞','3000',2,55,'A',''),
('300056','博思软件','3000',2,56,'A',''),
('300057','兆尹科技','3000',2,57,'A',''),
('300058','浙江邦盛','3000',2,58,'A',''),
('300059','博彦科技','3000',2,59,'A',''),
('300060','北京罗格','3000',2,60,'A',''),
('300061','上海天正','3000',2,61,'A',''),
('300062','西安远眺','3000',2,62,'A',''),
('300063','北京丁甲','3000',2,63,'A',''),
('300064','西安凤鸣','3000',2,64,'A',''),
('300065','昆仑数智','3000',2,65,'A',''),
('300066','北京必示','3000',2,66,'A',''),
('300067','长亮科技','3000',2,67,'A',''),
('300068','百度网讯','3000',2,68,'A',''),
('300069','金信润天','3000',2,69,'A',''),
('300070','金智维','3000',2,70,'A',''),
('300071','中科软件','3000',2,71,'A',''),
('300072','迪思杰','3000',2,72,'A',''),
('300073','丁甲数据','3000',2,73,'A',''),
('300074','泛鹏天地','3000',2,74,'A','');

/*Table structure for table `t_sys_role` */

DROP TABLE IF EXISTS `t_sys_role`;

CREATE TABLE `t_sys_role` (
  `ROLE_ID` varchar(32) NOT NULL COMMENT '角色ID ',
  `ROLE_NAME` varchar(100) NOT NULL COMMENT '角色名称 ',
  `SORT_NO` decimal(4,0) NOT NULL COMMENT '排序号 ',
  `REMARK` varchar(400) DEFAULT NULL COMMENT '备注 ',
  PRIMARY KEY (`ROLE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `t_sys_role` */

insert  into `t_sys_role`(`ROLE_ID`,`ROLE_NAME`,`SORT_NO`,`REMARK`) values 
('0001','开发人员',1,'开发人员使用'),
('0002','测试人员',2,'测试人员使用'),
('0007','管理角色',7,'系统管理'),
('0008','配置人员',3,'配置人员使用');

/*Table structure for table `t_sys_role_menu` */

DROP TABLE IF EXISTS `t_sys_role_menu`;

CREATE TABLE `t_sys_role_menu` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `ROLE_ID` varchar(32) DEFAULT NULL,
  `MENU_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
(22,'0001','22'),
(40,'0002','4'),
(41,'0002','5'),
(42,'0002','6'),
(43,'0002','7'),
(44,'0002','8'),
(45,'0002','9'),
(46,'0002','10'),
(47,'0002','13'),
(48,'0002','14'),
(49,'0002','15'),
(50,'0002','16'),
(51,'0002','17'),
(52,'0002','18'),
(53,'0002','19'),
(54,'0002','20');

/*Table structure for table `t_sys_role_user` */

DROP TABLE IF EXISTS `t_sys_role_user`;

CREATE TABLE `t_sys_role_user` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT 'UUID ',
  `ROLE_ID` varchar(32) NOT NULL COMMENT '角色ID ',
  `USER_ID` varchar(32) NOT NULL COMMENT '用户ID ',
  PRIMARY KEY (`ID`),
  KEY `I_t_sys_role_user_ROLE_ID` (`ROLE_ID`),
  KEY `I_t_sys_role_user_USER_ID` (`USER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `t_sys_role_user` */

insert  into `t_sys_role_user`(`ID`,`ROLE_ID`,`USER_ID`) values 
(1,'0001','2'),
(2,'0002','lili'),
(4,'0002','2'),
(6,'0001','fengyu'),
(7,'0002','anqi');

/*Table structure for table `t_sys_user` */

DROP TABLE IF EXISTS `t_sys_user`;

CREATE TABLE `t_sys_user` (
  `USER_ID` varchar(32) NOT NULL,
  `LOGIN_NAME` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `PASSWORD` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `USER_NAME` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `EMAIL` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `PHONE` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `STATUS` int NOT NULL COMMENT '用户状态',
  `ORG_ID` varchar(30) DEFAULT NULL COMMENT '所属机构',
  `SORT_NO` decimal(8,0) NOT NULL COMMENT '排序号',
  PRIMARY KEY (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `t_sys_user` */

insert  into `t_sys_user`(`USER_ID`,`LOGIN_NAME`,`PASSWORD`,`USER_NAME`,`EMAIL`,`PHONE`,`STATUS`,`ORG_ID`,`SORT_NO`) values 
('2','zhangsan','e10adc3949ba59abbe56e057f20f883e','zhangsan','zhangsan@gmail.com','13966667777',1,'300013',0),
('3','lisi','e10adc3949ba59abbe56e057f20f883e','lisi','lisi@gmail.com','13966667778',1,'300005',0),
('4','wangwu','e10adc3949ba59abbe56e057f20f883e','wangwu','wangwu@gmail.com','13966667772',1,'300005',0),
('5','zhaoer','e10adc3949ba59abbe56e057f20f883e','zhaoer','zhaoer@gmail.com','13966667776',0,'300005',0),
('6','songliu','123456','songliu','songliu@gmail.com','13966667771',1,'300005',0),
('admin','admin','e10adc3949ba59abbe56e057f20f883e','admin','super@aliyun.com','18677778888',1,'300002',0),
('anqi','anqi','e10adc3949ba59abbe56e057f20f883e','安琪','','15129872940',1,'300002',20250913),
('baiyang','baiyang','e10adc3949ba59abbe56e057f20f883e','白洋','','13609231828',1,'300002',20250913),
('caojie','caojie','e10adc3949ba59abbe56e057f20f883e','曹洁','','17829871871',1,'300002',20250913),
('chenchen','chenchen','e10adc3949ba59abbe56e057f20f883e','陈晨','','15129231991',1,'300002',20250913),
('fengyu','fengyu','e10adc3949ba59abbe56e057f20f883e','冯雨','','15852908997',1,'300002',20250914),
('jianghe','jianghe','e10adc3949ba59abbe56e057f20f883e','江荷','','13259798230',1,'300002',20250914),
('libo','libo','e10adc3949ba59abbe56e057f20f883e','李波','','13227789228',1,'300009',20250913),
('lili','lili','e10adc3949ba59abbe56e057f20f883e','李莉','lili123@189.com','18765342789',1,'300009',20250906),
('liqi','liqi','e10adc3949ba59abbe56e057f20f883e','李奇','','18700893077',1,'300009',20250913),
('yanke','yanke','e10adc3949ba59abbe56e057f20f883e','严科','yanke920618@163.com',NULL,1,'300009',0);
