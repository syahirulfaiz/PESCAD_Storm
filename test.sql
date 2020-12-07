-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 07, 2020 at 02:54 PM
-- Server version: 10.4.13-MariaDB
-- PHP Version: 7.4.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `test`
--

-- --------------------------------------------------------

--
-- Table structure for table `anomaly`
--

CREATE TABLE `anomaly` (
  `id_status` varchar(30) DEFAULT NULL,
  `elapsed_time` varchar(5) DEFAULT NULL,
  `from_class_name` varchar(25) DEFAULT NULL,
  `from_machine_name` varchar(15) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `collected_tweets`
--

CREATE TABLE `collected_tweets` (
  `id_status` varchar(30) DEFAULT NULL,
  `screen_name` varchar(15) DEFAULT NULL,
  `status` mediumtext DEFAULT NULL,
  `topic` mediumtext DEFAULT NULL,
  `place` varchar(30) DEFAULT NULL,
  `latitude` varchar(10) DEFAULT NULL,
  `longitude` varchar(10) DEFAULT NULL,
  `created_at` varchar(20) DEFAULT NULL,
  `is_event` varchar(1) DEFAULT NULL,
  `elapsed_time` varchar(5) DEFAULT NULL,
  `from_class_name` varchar(25) DEFAULT NULL,
  `from_machine_name` varchar(15) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `keyword_cluster`
--

CREATE TABLE `keyword_cluster` (
  `id_status` varchar(30) DEFAULT NULL,
  `keyword` varchar(20) DEFAULT NULL,
  `point_keyword` varchar(20) DEFAULT NULL,
  `id_cluster` varchar(40) DEFAULT NULL,
  `point_cluster` varchar(20) DEFAULT NULL,
  `elapsed_time` varchar(5) DEFAULT NULL,
  `from_class_name` varchar(25) DEFAULT NULL,
  `from_machine_name` varchar(15) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

--
-- Triggers `keyword_cluster`
--
DELIMITER $$
CREATE TRIGGER `keyword_cluster_set_id_status_1` BEFORE INSERT ON `keyword_cluster` FOR EACH ROW SET NEW.id_status = 
	(SELECT id_status FROM collected_tweets 
	WHERE topic LIKE CONCAT('%', NEW.keyword, '%') 
	ORDER BY created_at DESC LIMIT 1)
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `keyword_cluster_set_id_status_2` BEFORE UPDATE ON `keyword_cluster` FOR EACH ROW SET NEW.id_status = 
	(SELECT id_status FROM collected_tweets 
	WHERE topic LIKE CONCAT('%', NEW.keyword, '%') 
	ORDER BY created_at DESC LIMIT 1)
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `keyword_count`
--

CREATE TABLE `keyword_count` (
  `id_status` varchar(30) DEFAULT NULL,
  `keyword` varchar(20) DEFAULT NULL,
  `word_count` varchar(5) DEFAULT NULL,
  `tf_idf` varchar(10) DEFAULT NULL,
  `elapsed_time` varchar(5) DEFAULT NULL,
  `from_class_name` varchar(25) DEFAULT NULL,
  `from_machine_name` varchar(15) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `keyword_cluster`
--
ALTER TABLE `keyword_cluster`
  ADD UNIQUE KEY `keyword` (`keyword`);

--
-- Indexes for table `keyword_count`
--
ALTER TABLE `keyword_count`
  ADD UNIQUE KEY `keyword` (`keyword`);
COMMIT;


ALTER TABLE collected_tweets CONVERT TO CHARACTER SET utf8mb4;
ALTER TABLE keyword_cluster CONVERT TO CHARACTER SET utf8mb4;
ALTER TABLE keyword_count CONVERT TO CHARACTER SET utf8mb4;
ALTER TABLE anomaly CONVERT TO CHARACTER SET utf8mb4;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'password' WITH GRANT OPTION;
FLUSH PRIVILEGES; 
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '';


/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
