-- 流程引擎表
DROP TABLE IF EXISTS `flow_engine`;
CREATE TABLE `flow_engine` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `name` varchar(64) NOT NULL COMMENT '引擎名称，唯一',
                               `description` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
                               `content` longtext COMMENT '引擎配置内容，必须是xml形式',
                               `execution_mode` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'sync' COMMENT '执行方式：sync-同步执行；batch-批量执行；async-全异步执行',
                               `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'unavailable' COMMENT '状态',
                               `version` int NOT NULL DEFAULT '0' COMMENT '版本号',
                               `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='引擎配置表';