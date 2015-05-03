CREATE TABLE `user` (
  `id` varchar(32) NOT NULL,
  `name` varchar(50) NOT NULL,
  `username` varchar(256) NOT NULL,
  `password` varchar(60) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

CREATE TABLE `user_group` (
  `group_name` enum('user','developer','admin') NOT NULL,
  `user_id` varchar(32) NOT NULL,
  PRIMARY KEY (`group_name`,`user_id`),
  CONSTRAINT `FK_group_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

CREATE TABLE `platform` (
  `id` varchar(32) NOT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_platform_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

CREATE TABLE `device` (
  `id` varchar(32) NOT NULL,
  `name` varchar(50) NOT NULL,
  `platform_id` varchar(32) NOT NULL,
  `owner_id` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_device_owner_name` (`owner_id`, `name`),
  CONSTRAINT `FK_device_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_device_platform` FOREIGN KEY (`platform_id`) REFERENCES `platform` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

CREATE TABLE `app` (
  `id` varchar(32) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `platform_id` varchar(32) NOT NULL,
  `author_id` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_app_name` (`name`),
  CONSTRAINT `FK_app_author` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_app_platform` FOREIGN KEY (`platform_id`) REFERENCES `platform` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

CREATE TABLE `app_property` (
  `app_id` varchar(32) NOT NULL,
  `property_name` varchar(20) NOT NULL,
  `property_value` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`app_id`,`property_name`),
  CONSTRAINT `FK_app_property_app` FOREIGN KEY (`app_id`) REFERENCES `app` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

CREATE TABLE `category` (
  `id` varchar(32) NOT NULL,
  `name` varchar(50) NOT NULL,
  `parent_id` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_category_parent_name` (`parent_id`, `name`),
  CONSTRAINT `FK_category_parent` FOREIGN KEY (`parent_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

CREATE TABLE `app_category` (
  `app_id` varchar(32) NOT NULL,
  `category_id` varchar(32) NOT NULL,
  PRIMARY KEY (`app_id`,`category_id`),
  CONSTRAINT `FK_app_category_app` FOREIGN KEY (`app_id`) REFERENCES `app` (`id`),
  CONSTRAINT `FK_app_category_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

CREATE TABLE `installation` (
  `id` varchar(32) NOT NULL,
  `app_id` varchar(32) NOT NULL,
  `device_id` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_installation_app_device` (`app_id`,`device_id`),
  CONSTRAINT `FK_installation_app` FOREIGN KEY (`app_id`) REFERENCES `app` (`id`),
  CONSTRAINT `FK_installation_device` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

CREATE TABLE `installation_property` (
  `installation_id` varchar(32) NOT NULL,
  `property_name` varchar(20) NOT NULL,
  `property_value` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`installation_id`,`property_name`),
  CONSTRAINT `FK_installation_property_installation_id` FOREIGN KEY (`installation_id`) REFERENCES `installation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

CREATE TABLE `installation_method` (
  `id` varchar(32) NOT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_installation_method_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

CREATE TABLE `installation_method_property` (
  `installation_method_id` varchar(32) NOT NULL,
  `property_name` varchar(20) NOT NULL,
  PRIMARY KEY (`installation_method_id`,`property_name`),
  CONSTRAINT `FK_installation_method_property_installation_method_` FOREIGN KEY (`installation_method_id`) REFERENCES `installation_method` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

CREATE TABLE `manager_type` (
  `id` varchar(32) NOT NULL,
  `name` varchar(50) NOT NULL,
  `platform_id` varchar(32) NOT NULL,
  `installation_method_id` varchar(32) NOT NULL,
  `cardinality` enum('ONE','MANY','ALL') NOT NULL,
  `installation_instructions` varchar(150) DEFAULT NULL,
  `installation_url` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_manager_type_name` (`name`),
  UNIQUE KEY `UC_manager_type_installation_method_platform` (`installation_method_id`, `platform_id`),
  CONSTRAINT `FK_manager_type_installation_method` FOREIGN KEY (`installation_method_id`) REFERENCES `installation_method` (`id`),
  CONSTRAINT `FK_manager_type_platform` FOREIGN KEY (`platform_id`) REFERENCES `platform` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

CREATE TABLE `manager` (
  `id` varchar(32) NOT NULL,
  `name` varchar(50) NOT NULL,
  `owner_id` varchar(32) NOT NULL,
  `manager_type_id` varchar(32) NOT NULL,
  `key` varchar(32) DEFAULT NULL,
  `installed` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_manager_owner_name` (`owner_id`, `name`),
  CONSTRAINT `FK_manager_manager_type` FOREIGN KEY (`manager_type_id`) REFERENCES `manager_type` (`id`),
  CONSTRAINT `FK_manager_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

CREATE TABLE `managed_device` (
  `manager_id` varchar(32) NOT NULL,
  `device_id` varchar(32) NOT NULL,
  PRIMARY KEY (`manager_id`,`device_id`),
  CONSTRAINT `FK_managed_device_device` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`),
  CONSTRAINT `FK_managed_device_manager` FOREIGN KEY (`manager_id`) REFERENCES `manager` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;
