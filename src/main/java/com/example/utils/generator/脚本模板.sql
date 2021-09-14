
-- 通用字段sql片段
-- Java String类型（varchar)
`name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
-- Java Long类型
`id` bigint(20) NULL DEFAULT NULL COMMENT 'ID',
-- Java Double类型
`price` double(20, 2) NULL COMMENT '价格',
-- Java String类型（text）
`description` text NULL COMMENT '介绍',
-- Java String类型（longtext）
`article` longtext NULL COMMENT '文章'

--------------------------------------------------------------------------------------
-- 预约审核模型
DROP TABLE IF EXISTS `verify`;
CREATE TABLE `verify`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
-- 填写通用字段的sql片段
`name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
`time` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '时间',
-------
  `publish_file` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发布文件',
  `publish_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布人',
  `publish_id` bigint(20) NULL DEFAULT NULL COMMENT '发布人id',
  `reserve_file` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '预约文件',
  `reserve_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预约人',
  `reserve_id` bigint(20) NULL DEFAULT NULL COMMENT '预约人id',
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `reason` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '理由',
  `verify_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核人',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-------------------------------------------------------------------------------------
-- 提交审核模型
DROP TABLE IF EXISTS `submit`;
CREATE TABLE `submit`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
-- 填写通用字段的sql片段
  `submit_file` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发布文件',
  `submit_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布人',
  `submit_id` bigint(20) NULL DEFAULT NULL COMMENT '发布人id',
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `reason` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '理由',
  `verify_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;