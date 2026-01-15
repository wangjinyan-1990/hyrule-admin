-- 环境清单菜单
insert into `t_sys_menu` (`MENU_ID`, `COMPONENT`, `PATH`, `REDIRECT`, `NAME`, `TITLE`, `ICON`, `PARENT_ID`, `IS_LEAF`, `HIDDEN`) values('28','Layout','/environment',NULL,'environment','环境管理','environment',NULL,'0','0');
insert into `t_sys_menu` (`MENU_ID`, `COMPONENT`, `PATH`, `REDIRECT`, `NAME`, `TITLE`, `ICON`, `PARENT_ID`, `IS_LEAF`, `HIDDEN`) values('29','/environment/environmentList/index','environmentList',NULL,'environmentList','环境清单','environmentList','28','1','0');


-- 环境表
create table `tf_environment` (
	`ENV_ID` int (11),
	`ENV_NAME` varchar (90),
	`TEST_STAGE` varchar (15),
	`REMARK` blob 
); 
insert into `tf_environment` (`ENV_ID`, `ENV_NAME`, `TEST_STAGE`, `REMARK`) values('1','CT2','SIT',NULL);
insert into `tf_environment` (`ENV_ID`, `ENV_NAME`, `TEST_STAGE`, `REMARK`) values('2','CT12','PAT',NULL);

-- 环境清单表
create table `tf_environment_list` (
	`ENV_LIST_ID` int (11),
	`ENV_ID` int (11),
	`SYSTEM_ID` varchar (96),
	`SERVER_NAME` varchar (360),
	`IP_ADDRESS` varchar (60),
	`PORT_INFO` varchar (96),
	`LINK_ADDRESS` varchar (240),
	`REMARK` blob 
); 
insert into `tf_environment_list` (`ENV_LIST_ID`, `ENV_ID`, `SYSTEM_ID`, `SERVER_NAME`, `IP_ADDRESS`, `PORT_INFO`, `LINK_ADDRESS`, `REMARK`) values('1','1','sys-004','应用','9.6.239.104','','','');
