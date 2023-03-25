/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50740
 Source Host           : 192.168.3.16:3306
 Source Schema         : study

 Target Server Type    : MySQL
 Target Server Version : 50740
 File Encoding         : 65001

 Date: 25/03/2023 23:25:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `createTime` datetime NULL DEFAULT NULL,
  `modifyTime` datetime NULL DEFAULT NULL,
  `type` int(2) NULL DEFAULT NULL,
  `dictId` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book
-- ----------------------------
INSERT INTO `book` VALUES ('1560526548464287746', 'java', 'lar', '2023-03-04 22:58:42', '2023-03-04 22:58:42', 1, '163203282');
INSERT INTO `book` VALUES ('1632063417354850306', 'java4', 'lar', '2023-03-05 01:00:16', '2023-03-05 01:00:16', 1, '163203283');
INSERT INTO `book` VALUES ('1632246590256762881', 'java', 'lar', '2023-03-05 13:08:07', '2023-03-05 13:08:07', 1, NULL);
INSERT INTO `book` VALUES ('1632246647399960578', 'java3', 'lar', '2023-03-05 13:08:21', '2023-03-05 13:08:21', 1, NULL);
INSERT INTO `book` VALUES ('5235235235235', 'java2', 'hh', '2023-03-05 13:07:49', '2023-03-05 13:07:49', NULL, NULL);



-- ----------------------------
-- Table structure for dict
-- ----------------------------
DROP TABLE IF EXISTS `dict`;
CREATE TABLE `dict`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dict
-- ----------------------------
INSERT INTO `dict` VALUES ('163203282', '书籍1');
INSERT INTO `dict` VALUES ('163203283', '书籍2');


SET FOREIGN_KEY_CHECKS = 1;
