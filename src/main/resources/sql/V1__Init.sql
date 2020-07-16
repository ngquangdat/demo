CREATE TABLE `item` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(3000),
  `description` varchar(5000),
  `unsigned_name` varchar(3000),
  `unsigned_description` varchar(5000),
  `image` varchar(3000),
  `longitude` double,
  `latitude` double,
  `status` int,
  `ward_id` int,
  `type` int,
  `user_id` bigint,
  `contact` varchar(1000),
  `picked_time` datetime,
  `lost_time` datetime,
  `created_time` datetime,
  `updated_time` datetime
);

CREATE TABLE `item_status` (
  `id` int PRIMARY KEY,
  `name` varchar(1000)
);

CREATE TABLE `item_type` (
  `id` int PRIMARY KEY,
  `name` varchar(1000),
  `item_type_parent` bigint
);

CREATE TABLE `user` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `username` varchar(1000),
  `password` varchar(1000),
  `name` varchar(1000),
  `phone` varchar(100),
  `point` bigint,
  `image` varchar(3000),
  `created_time` datetime,
  `updated_time` datetime
);

CREATE TABLE `ward` (
  `id` int PRIMARY KEY,
  `name` varchar(1000),
  `district_id` int
);

CREATE TABLE `district` (
  `id` int PRIMARY KEY,
  `name` varchar(1000),
  `city_id` int
);

CREATE TABLE `city` (
  `id` int PRIMARY KEY,
  `name` varchar(1000)
);

ALTER TABLE `item` ADD FOREIGN KEY (`status`) REFERENCES `item_status` (`id`);

ALTER TABLE `item` ADD FOREIGN KEY (`type`) REFERENCES `item_type` (`id`);

ALTER TABLE `item` ADD FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `district` ADD FOREIGN KEY (`city_id`) REFERENCES `city` (`id`);

ALTER TABLE `ward` ADD FOREIGN KEY (`district_id`) REFERENCES `district` (`id`);

ALTER TABLE `item` ADD FOREIGN KEY (`ward_id`) REFERENCES `ward` (`id`);
