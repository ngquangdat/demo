CREATE TABLE `account`
(
    `id`           varchar(36) PRIMARY KEY,
    `username`     varchar(1000),
    `password`     varchar(1000),
    `name`         varchar(1000),
    `phone`        varchar(100),
    `point`        bigint,
    `image`        varchar(3000),
    `created_time` datetime,
    `updated_time` datetime
);

CREATE TABLE `refresh_token`
(
    `id`           bigint,
    `token_id`        varchar(1000),
    `account_id`   varchar(36),
    `expired_time` datetime
);