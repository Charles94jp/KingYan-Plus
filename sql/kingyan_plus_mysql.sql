CREATE DATABASE IF NOT EXISTS kingyan_plus;

USE kingyan_plus;

DROP TABLE IF EXISTS `user`;

# 角色和权限去关系表查
CREATE TABLE IF NOT EXISTS `user`
(
    `id`           bigint(20)   NOT NULL    COMMENT '用户ID'  AUTO_INCREMENT,
    `name`         varchar(30)  NOT NULL    COMMENT '用户登录名，唯一',
    `password`     varchar(100) NOT NULL    COMMENT '登录密码，密文',
    `nickname`     varchar(30)              COMMENT '用户昵称',
    `sex`          char(1)                  COMMENT '用户性别，0未知，1男，2女',
    `phone`        varchar(11)              COMMENT '用户手机号',
    `email`        varchar(50)              COMMENT '用户邮箱地址',
    `created_date` date                     COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

# 分段执行，否则执行太快，表还未创建好就插入会报错
INSERT INTO `user`(`id`, `name`, `password`)
    # 密码：123@Admin，和配置文件中的SM2密钥对无关，这个是盐值+SM3 Hash
VALUES (1, 'admin', 'lCNGtzgohvibIsSTd9H+763MneUXDluOpS4Mv4Nq/CaWZfmIqvYv+V+y4cjzMmrpjVXIFmf1NGanqZFeTa9Phg=='),
       # 密码：123@Test
       (2, 'test', '2kWIaYsYxO2Kjq+qtDB6B8jOvck6vbyuxHuFwbqSNuzWd3QDKePPkFmY4sXQ/gC94cEvRqvKprwHSZvKpULa7A=='),
       # 密码：123@Test
       (3, 'test1', '2kWIaYsYxO2Kjq+qtDB6B8jOvck6vbyuxHuFwbqSNuzWd3QDKePPkFmY4sXQ/gC94cEvRqvKprwHSZvKpULa7A==');

DROP TABLE IF EXISTS `role`;

CREATE TABLE IF NOT EXISTS `role`
(
    `id`   int(20)     NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name` varchar(30) NOT NULL COMMENT '角色名',
    PRIMARY KEY (`id`),
    UNIQUE (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO `role` (id, name)
VALUES (1, 'admin'),
       (2, 'test');

DROP TABLE IF EXISTS `user_role`;

# 用户，角色多对多，关系表
CREATE TABLE IF NOT EXISTS `user_role`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '对应关系的id',
    `user_id` bigint(20) NOT NULL,
    `role_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO `user_role` (id, user_id, role_id)
VALUES (1, 1, 1),
       (2, 2, 2);

DROP TABLE IF EXISTS `permission`;

CREATE TABLE IF NOT EXISTS `permission`
(
    `id`   int(20)     NOT NULL COMMENT '权限id' AUTO_INCREMENT,
    `name` varchar(30) NOT NULL COMMENT '权限名',
    PRIMARY KEY (`id`),
    UNIQUE (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO `permission` (id, name)
VALUES (1, 'hello-admin'),
       (2, 'hello-test');

DROP TABLE IF EXISTS `user_permission`;

# 用户，权限多对多，关系表
CREATE TABLE IF NOT EXISTS `user_permission`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '对应关系的id',
    `user_id`       bigint(20) NOT NULL,
    `permission_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO `user_permission` (id, user_id, permission_id)
VALUES (1, 1, 1),
       (2, 2, 2);

DROP TABLE IF EXISTS `role_permission`;

# 角色，权限多对多，关系表
CREATE TABLE IF NOT EXISTS `role_permission`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '对应关系的id',
    `role_id`       bigint(20) NOT NULL,
    `permission_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO `role_permission` (id, role_id, permission_id)
VALUES (1, 1, 1),
       (2, 2, 2);