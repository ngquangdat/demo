CREATE TABLE `account` (
  `id` varchar(36) PRIMARY KEY,
  `username` varchar(1000),
  `password` varchar(1000),
  `name` varchar(1000),
  `phone` varchar(100),
  `point` bigint,
  `image` varchar(3000),
  `created_time` datetime,
  `updated_time` datetime
);
