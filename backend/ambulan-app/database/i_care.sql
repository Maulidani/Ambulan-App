-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Nov 26, 2022 at 06:53 PM
-- Server version: 10.4.24-MariaDB
-- PHP Version: 8.1.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `i_care`
--

-- --------------------------------------------------------

--
-- Table structure for table `articles`
--

CREATE TABLE `articles` (
  `id` bigint(20) NOT NULL,
  `title` varchar(150) NOT NULL,
  `description` text NOT NULL,
  `image` varchar(100) NOT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `articles`
--

INSERT INTO `articles` (`id`, `title`, `description`, `image`, `updated_at`, `created_at`) VALUES
(4, 'cara membuat kue', 'sex\nf\nc\nc\nc\nc\nccfs\nwyd\nfufufjf\nf\nf\nfjfjfjfjfjfjfjf\n\nf\nf\n\ng\ng\ng\nffs\nd\nggggh\nf\nf\nf\nf\nf\nf\ng\ng\ng\ng\ng\ng\ng\ng\ng', '/image/article/1657923915.IMG_20220716_062506454.jpg', '2022-07-15 14:25:15', '2022-07-12 08:23:20');

-- --------------------------------------------------------

--
-- Table structure for table `chats`
--

CREATE TABLE `chats` (
  `id` bigint(20) NOT NULL,
  `from_user_type` enum('admin','customer','driver') NOT NULL,
  `to_user_type` enum('admin','customer','driver') NOT NULL,
  `from_user_id` bigint(20) NOT NULL,
  `to_user_id` int(11) NOT NULL,
  `message` text NOT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `chats`
--

INSERT INTO `chats` (`id`, `from_user_type`, `to_user_type`, `from_user_id`, `to_user_id`, `message`, `updated_at`, `created_at`) VALUES
(1, 'driver', 'customer', 1, 3, 'lokasi anda dimana?', '2022-07-11 12:22:44', '2022-07-11 12:22:44'),
(2, 'driver', 'customer', 1, 3, 'cepat beritahu', '2022-07-11 12:23:29', '2022-07-11 12:23:29'),
(3, 'customer', 'driver', 3, 1, 'saya di rumah pak', '2022-07-11 12:23:56', '2022-07-11 12:23:56'),
(4, 'customer', 'driver', 3, 1, 'cepat kesini', '2022-07-11 12:24:03', '2022-07-11 12:24:03'),
(6, 'admin', 'driver', 1, 1, 'oi apa yang kamu lakukan ?', '2022-07-11 12:30:46', '2022-07-11 12:30:46'),
(7, 'driver', 'admin', 1, 1, 'aku sedang mengemudi', '2022-07-11 12:31:20', '2022-07-11 12:31:20'),
(9, 'customer', 'driver', 3, 1, 'hi', '2022-07-11 12:33:30', '2022-07-11 12:33:30'),
(11, 'driver', 'customer', 1, 2, 'hi customer', '2022-07-11 12:35:13', '2022-07-11 12:35:13'),
(12, 'driver', 'customer', 1, 2, 'hi', '2022-07-12 07:36:08', '2022-07-12 07:36:08'),
(13, 'driver', 'customer', 1, 2, 'hi', '2022-07-12 08:29:01', '2022-07-12 08:29:01'),
(14, 'driver', 'customer', 1, 2, 'dmnko', '2022-07-16 02:00:50', '2022-07-16 02:00:50'),
(15, 'customer', 'driver', 2, 1, 'kau iya dmn boss', '2022-07-16 02:04:24', '2022-07-16 02:04:24'),
(17, 'customer', 'driver', 2, 1, 'tolong cepat pak', '2022-07-16 02:05:07', '2022-07-16 02:05:07'),
(18, 'driver', 'customer', 1, 2, 'disinija woi', '2022-07-16 02:05:42', '2022-07-16 02:05:42'),
(19, 'customer', 'driver', 3, 1, 'dmnko', '2022-07-16 02:09:38', '2022-07-16 02:09:38'),
(31, 'driver', 'customer', 1, 3, 'iya sabar pak', '2022-07-16 02:11:11', '2022-07-16 02:11:11'),
(32, 'driver', 'customer', 1, 3, 'saya lgi dijalan nih', '2022-07-16 02:11:16', '2022-07-16 02:11:16'),
(33, 'driver', 'customer', 1, 3, 'macet banget tua dijalan', '2022-07-16 02:11:28', '2022-07-16 02:11:28'),
(34, 'customer', 'driver', 3, 1, 'teja', '2022-07-16 02:11:38', '2022-07-16 02:11:38'),
(35, 'driver', 'customer', 1, 3, 'woiiii', '2022-07-16 02:17:39', '2022-07-16 02:17:39'),
(36, 'admin', 'driver', 1, 7, 'hi nama kamu siapa', '2022-07-16 02:51:32', '2022-07-16 02:51:32'),
(37, 'driver', 'admin', 1, 1, 'kepo banger deh eloo', '2022-07-16 03:18:16', '2022-07-16 03:18:16'),
(38, 'admin', 'driver', 1, 1, 'gua admin bangsat', '2022-07-16 03:18:53', '2022-07-16 03:18:53'),
(39, 'driver', 'admin', 3, 1, 'oi', '2022-07-16 03:31:47', '2022-07-16 03:31:47'),
(40, 'admin', 'driver', 1, 3, 'apa anjing', '2022-07-16 03:36:12', '2022-07-16 03:36:12'),
(41, 'customer', 'admin', 2, 1, 'hi admin saya customer', '2022-07-25 08:45:57', '2022-07-25 08:45:57'),
(42, 'admin', 'customer', 1, 2, 'saya admin', '2022-07-25 08:46:21', '2022-07-25 08:46:21'),
(43, 'customer', 'admin', 3, 1, 'hi admin', '2022-07-25 09:03:32', '2022-07-25 09:03:32'),
(44, 'customer', 'admin', 3, 1, 'saya adalah customer', '2022-07-25 09:04:00', '2022-07-25 09:04:00'),
(45, 'admin', 'customer', 1, 3, 'saya admin kenapai rewako??', '2022-07-25 09:32:17', '2022-07-25 09:32:17'),
(46, 'customer', 'admin', 2, 1, 'ok', '2022-07-25 09:33:19', '2022-07-25 09:33:19');

-- --------------------------------------------------------

--
-- Table structure for table `hospitals`
--

CREATE TABLE `hospitals` (
  `id` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `address` text NOT NULL,
  `image` varchar(100) NOT NULL,
  `latitude` varchar(50) NOT NULL,
  `longitude` varchar(50) NOT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `hospitals`
--

INSERT INTO `hospitals` (`id`, `name`, `address`, `image`, `latitude`, `longitude`, `updated_at`, `created_at`) VALUES
(1, 'universitas rumah sakit jiwa', 'daya', '/image/hospital/1657558098.fotoku pake headset.jpeg', 'opop', 'long', '2022-07-12 08:21:57', '2022-07-11 08:44:20'),
(4, 'tajuddin', 'jl. goa ria', '/image/hospital/1657558332_LOGO FORECASt.png', 'oi', 'oioi', '2022-07-11 08:52:12', '2022-07-11 08:52:12'),
(12, 'bddbdnd', 'bdhdjdj.bdjdjfjf\nnxncnc\nkckckf.\ndndjf', '/image/hospital/1657923955_IMG_20220716_062550605.jpg', '99494', '93939303', '2022-07-15 14:25:55', '2022-07-15 14:25:55');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `id` bigint(20) NOT NULL,
  `user_customer_id` bigint(20) NOT NULL,
  `user_driver_id` bigint(20) NOT NULL,
  `hospital_id` bigint(20) NOT NULL,
  `pick_up_latitude` varchar(50) NOT NULL,
  `pick_up_longitude` varchar(50) NOT NULL,
  `status` enum('to_pick_up','to_drop_off','finish','cancel') NOT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`id`, `user_customer_id`, `user_driver_id`, `hospital_id`, `pick_up_latitude`, `pick_up_longitude`, `status`, `updated_at`, `created_at`) VALUES
(10, 2, 1, 4, '9090909090', '121212121', 'finish', '2022-07-14 12:51:31', '2022-07-11 10:05:12'),
(11, 3, 1, 4, '9090909090', '121212121', 'finish', '2022-07-11 10:05:44', '2022-07-11 10:05:44'),
(13, 2, 1, 1, '9090909090', '121212121', 'finish', '2022-07-12 07:26:35', '2022-07-12 07:26:35'),
(14, 2, 1, 1, '-5.0988053', '119.5342223', 'cancel', '2022-07-14 13:24:21', '2022-07-12 08:26:22'),
(15, 2, 1, 1, '-5.106662505954962', '119.53532762825488', 'finish', '2022-07-15 13:11:02', '2022-07-15 13:11:02'),
(17, 2, 1, 1, '-5.0988328', '119.534223', 'cancel', '2022-07-15 13:23:19', '2022-07-15 13:23:19'),
(18, 2, 1, 4, '-5.111529740551793', '119.53242514282466', 'finish', '2022-07-15 13:39:53', '2022-07-15 13:38:00'),
(20, 2, 1, 1, '-5.098811', '119.5342346', 'finish', '2022-07-16 02:08:32', '2022-07-15 13:56:37'),
(22, 3, 1, 4, '-5.0988107', '119.5342258', 'finish', '2022-07-16 03:28:39', '2022-07-16 02:08:57'),
(23, 2, 3, 12, '-5.11741211571131', '119.52736649662256', 'finish', '2022-07-16 03:32:01', '2022-07-16 03:30:41');

-- --------------------------------------------------------

--
-- Table structure for table `user_admins`
--

CREATE TABLE `user_admins` (
  `id` bigint(20) NOT NULL,
  `name` varchar(20) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user_admins`
--

INSERT INTO `user_admins` (`id`, `name`, `username`, `password`, `updated_at`, `created_at`) VALUES
(1, 'Admin', 'admin', 'adminadmin', '2022-07-10 14:39:57', '2022-07-10 14:39:57');

-- --------------------------------------------------------

--
-- Table structure for table `user_customers`
--

CREATE TABLE `user_customers` (
  `id` bigint(20) NOT NULL,
  `name` varchar(25) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  `image` varchar(100) NOT NULL,
  `latitude` varchar(100) DEFAULT NULL,
  `longitude` varchar(100) DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user_customers`
--

INSERT INTO `user_customers` (`id`, `name`, `phone`, `password`, `image`, `latitude`, `longitude`, `updated_at`, `created_at`) VALUES
(2, 'adly alqadrawy', '123456789', 'qwerty', '/image/user/1657923438.IMG_20220716_061711678.jpg', NULL, NULL, '2022-07-15 14:17:18', '2022-07-10 06:31:40'),
(3, 'iyam ini', '0', '0', '/image/user/1657464212.—Pngtree—whatsapp icon whatsapp logo_3584844.png', '00000', '09898989', '2022-07-10 06:50:42', '2022-07-10 06:34:43'),
(9, 'gs', '6', 'd', '/image/user/1657653643_IMG_20220713_032037310.jpg', NULL, NULL, '2022-07-12 11:20:43', '2022-07-12 11:20:43'),
(10, 'v', '1234567891', 'qwertyuiop', '/image/user/1657654109_IMG_20220713_032823561.jpg', NULL, NULL, '2022-07-12 11:28:29', '2022-07-12 11:28:29');

-- --------------------------------------------------------

--
-- Table structure for table `user_drivers`
--

CREATE TABLE `user_drivers` (
  `id` bigint(20) NOT NULL,
  `name` varchar(25) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  `image` varchar(100) NOT NULL,
  `latitude` varchar(50) DEFAULT NULL,
  `longitude` varchar(50) DEFAULT NULL,
  `status` enum('0','1') DEFAULT '0',
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user_drivers`
--

INSERT INTO `user_drivers` (`id`, `name`, `phone`, `password`, `image`, `latitude`, `longitude`, `status`, `updated_at`, `created_at`) VALUES
(1, 'iyam ini kaue', '000', 'qqq', '/image/user/1657638344.pngwing.com(1).png', '-5.0989121', '119.53428', '0', '2022-07-25 09:33:45', '2022-07-10 06:39:02'),
(3, 'ozan', '123456', 'qwerty', '/image/user/1657638318_pngwing.com(2).png', '-5.0988067', '119.5342223', '1', '2022-07-16 03:35:33', '2022-07-12 07:05:18'),
(5, 'ozan', '086', 'qwertyuiop', '/image/user/1657652565_pngwing.com(1).png', '0', '0', '0', '2022-07-12 11:02:45', '2022-07-12 11:02:45'),
(7, 'fhcjcivvd', '0088808888', 'xhxjckvk', '/image/user/1657924050.IMG_20220716_062723701.jpg', NULL, NULL, '0', '2022-07-15 14:27:36', '2022-07-13 06:06:56'),
(9, 'kembar', '089494949494994', 'vdbdbdbdbdbf', '/image/user/1657722078.IMG_20220713_222112170.jpg', NULL, NULL, '0', '2022-07-13 06:21:18', '2022-07-13 06:17:34'),
(10, 'checjvrjvr', '950505058585', 'chcwjvsvjvskvd', '/image/user/1657924021_IMG_20220716_062658013.jpg', NULL, NULL, '0', '2022-07-15 14:27:01', '2022-07-15 14:27:01');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `articles`
--
ALTER TABLE `articles`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `chats`
--
ALTER TABLE `chats`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `hospitals`
--
ALTER TABLE `hospitals`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD KEY `hospital_id` (`hospital_id`),
  ADD KEY `user_customer_id` (`user_customer_id`),
  ADD KEY `user_driver_id` (`user_driver_id`);

--
-- Indexes for table `user_admins`
--
ALTER TABLE `user_admins`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user_customers`
--
ALTER TABLE `user_customers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user_drivers`
--
ALTER TABLE `user_drivers`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `articles`
--
ALTER TABLE `articles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `chats`
--
ALTER TABLE `chats`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=47;

--
-- AUTO_INCREMENT for table `hospitals`
--
ALTER TABLE `hospitals`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `user_admins`
--
ALTER TABLE `user_admins`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `user_customers`
--
ALTER TABLE `user_customers`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `user_drivers`
--
ALTER TABLE `user_drivers`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`hospital_id`) REFERENCES `hospitals` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`user_customer_id`) REFERENCES `user_customers` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`user_driver_id`) REFERENCES `user_drivers` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
