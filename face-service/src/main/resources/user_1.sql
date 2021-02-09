/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50712
Source Host           : localhost:3306
Source Database       : boot1

Target Server Type    : MYSQL
Target Server Version : 50712
File Encoding         : 65001

Date: 2020-06-17 22:48:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `user_1`
-- ----------------------------
DROP TABLE IF EXISTS `user_1`;
CREATE TABLE `user_1` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(60) DEFAULT NULL COMMENT '姓名',
  `age` int(20) DEFAULT NULL COMMENT '年龄',
  `birthday` varchar(20) DEFAULT NULL COMMENT '生日',
  `address` varchar(20) DEFAULT NULL COMMENT '地址',
  `sex` int(10) DEFAULT NULL COMMENT '身份证号码',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=480144140672172033 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_1
-- ----------------------------
