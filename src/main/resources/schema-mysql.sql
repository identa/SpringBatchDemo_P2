
CREATE TABLE IF NOT EXISTS `campaign` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  `statusID` INT NULL,
  `startDate` DATE NULL,
  `endDate` DATE NULL,
  `budget` DOUBLE NULL,
  `bid` DOUBLE NULL,
  PRIMARY KEY (`id`));
