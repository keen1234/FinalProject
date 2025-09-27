-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 27, 2025 at 11:06 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `librows`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`id`, `email`, `password`, `status`, `name`) VALUES
(1, 'adminkenth@gmail.com', 'admin12345', 'online', 'kenth');

-- --------------------------------------------------------

--
-- Table structure for table `book`
--

CREATE TABLE `book` (
  `id` bigint(20) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `author` varchar(255) DEFAULT NULL,
  `date_publish` date DEFAULT NULL,
  `genre` varchar(255) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'available',
  `country` varchar(255) DEFAULT NULL,
  `time` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `book`
--

INSERT INTO `book` (`id`, `title`, `author`, `date_publish`, `genre`, `language`, `description`, `status`, `country`, `time`) VALUES
(7, 'test', 'test', '1111-11-11', 'test', 'test', '', 'available', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `book_reservers`
--

CREATE TABLE `book_reservers` (
  `book_id` bigint(20) NOT NULL,
  `student_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `borrow_record`
--

CREATE TABLE `borrow_record` (
  `id` bigint(20) NOT NULL,
  `student_id` bigint(20) NOT NULL,
  `book_id` bigint(20) NOT NULL,
  `borrowed_date` datetime(6) DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `returned_date` datetime(6) DEFAULT NULL,
  `status` enum('BORROWED','RETURNED','OVERDUE') DEFAULT 'BORROWED',
  `notes` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `courses`
--

CREATE TABLE `courses` (
  `course_id` bigint(20) NOT NULL,
  `course_code` varchar(255) NOT NULL,
  `category` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `courses`
--

INSERT INTO `courses` (`course_id`, `course_code`, `category`) VALUES
(1, 'BSIT', 'Information Technology'),
(2, 'BSCS', 'Information Technology'),
(3, 'BSIS', 'Information Technology'),
(4, 'IT-2YR', 'Information Technology'),
(5, 'ACT-2YR', 'Information Technology'),
(6, 'BSBA', 'Business & Management'),
(7, 'BSA', 'Business & Management'),
(8, 'BSAIS', 'Business & Management'),
(9, 'BSMA', 'Business & Management'),
(10, 'BSRTCS', 'Business & Management'),
(11, 'ART-2YR', 'Business & Management'),
(12, 'BSHM', 'Hospitality Management'),
(13, 'BSCM', 'Hospitality Management'),
(14, 'HRA-3YR', 'Hospitality Management'),
(15, 'HRS-2YR', 'Hospitality Management'),
(16, 'BSTM', 'Tourism Management'),
(17, 'BSCpE', 'Engineering'),
(18, 'BAPSY', 'Arts & Sciences'),
(19, 'BMMA', 'Arts & Sciences'),
(20, 'BACOMM', 'Arts & Sciences'),
(21, 'BSMT', 'Maritime'),
(22, 'BSMarE', 'Maritime'),
(23, 'BSNAME', 'Maritime'),
(24, 'BSCrim', 'Criminal Justice Education');

-- --------------------------------------------------------

--
-- Table structure for table `librows`
--

CREATE TABLE `librows` (
  `C1` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `librows`
--

INSERT INTO `librows` (`C1`) VALUES
('-- phpMyAdmin SQL Dump'),
('-- version 5.2.1'),
('-- https://www.phpmyadmin.net/'),
('--'),
('-- Host: 127.0.0.1'),
('-- Generation Time: Sep 27, 2025 at 09:23 AM'),
('-- Server version: 10.4.32-MariaDB'),
('-- PHP Version: 8.2.12'),
(NULL),
('SET SQL_MODE = \"NO_AUTO_VALUE_ON_ZERO\";'),
('START TRANSACTION;'),
('SET time_zone = \"+00:00\";'),
(NULL),
(NULL),
('/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;'),
('/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;'),
('/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;'),
('/*!40101 SET NAMES utf8mb4 */;'),
(NULL),
('--'),
('-- Database: `librows`'),
('--'),
(NULL),
('-- --------------------------------------------------------'),
(NULL),
('--'),
('-- Table structure for table `admin`'),
('--'),
(NULL),
('DROP TABLE IF EXISTS `admin`;'),
(NULL),
('CREATE TABLE `admin` ('),
('  `id` bigint(20) NOT NULL,'),
('  `email` varchar(255) NOT NULL,'),
('  `password` varchar(255) DEFAULT NULL,'),
('  `status` varchar(255) DEFAULT NULL,'),
('  `name` varchar(255) NOT NULL'),
(') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;'),
(NULL),
('--'),
('-- Dumping data for table `admin`'),
('--'),
(NULL),
('INSERT INTO `admin` (`id`, `email`, `password`, `status`, `name`) VALUES'),
('(1, \'adminkenth@gmail.com\', \'admin12345\', \'online\', \'kenth\');'),
(NULL),
('-- --------------------------------------------------------'),
(NULL),
('--'),
('-- Table structure for table `book`'),
('--'),
('DROP TABLE IF EXISTS `book`;'),
('CREATE TABLE `book` ('),
('  `id` bigint(20) NOT NULL AUTO_INCREMENT,'),
('  `title` varchar(255) DEFAULT NULL,'),
('  `author` varchar(255) DEFAULT NULL,'),
('  `date_publish` date DEFAULT NULL,'),
('  `genre` varchar(255) DEFAULT NULL,'),
('  `language` varchar(255) DEFAULT NULL,'),
('  `description` varchar(255) DEFAULT NULL,'),
('  `status` varchar(20) DEFAULT \'available\','),
('  `country` varchar(255) DEFAULT NULL,'),
('  `time` varchar(255) DEFAULT NULL'),
(') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;'),
(NULL),
('-- --------------------------------------------------------'),
(NULL),
('--'),
('-- Table structure for table `book_reservers`'),
('--'),
('DROP TABLE IF EXISTS `book_reservers`;'),
('CREATE TABLE `book_reservers` ('),
('  `book_id` bigint(20) NOT NULL,'),
('  `student_id` bigint(20) NOT NULL,'),
('  PRIMARY KEY (`book_id`, `student_id`),'),
('  KEY `fk_book_id` (`book_id`),'),
('  KEY `fk_student_id` (`student_id`),'),
('  CONSTRAINT `fk_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),'),
('  CONSTRAINT `fk_student_id` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)'),
(') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;'),
(NULL),
('--'),
('-- Table structure for table `courses`'),
('--'),
('DROP TABLE IF EXISTS `courses`;'),
('CREATE TABLE `courses` ('),
('  `course_id` bigint(20) NOT NULL,'),
('  `course_code` varchar(255) NOT NULL,'),
('  `category` varchar(255) NOT NULL'),
(') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;'),
(NULL),
('--'),
('-- Dumping data for table `courses`'),
('--'),
(NULL),
('INSERT INTO `courses` (`course_id`, `course_code`, `category`) VALUES'),
('(1, \'BSIT\', \'Information Technology\'),'),
('(2, \'BSCS\', \'Information Technology\'),'),
('(3, \'BSIS\', \'Information Technology\'),'),
('(4, \'IT-2YR\', \'Information Technology\'),'),
('(5, \'ACT-2YR\', \'Information Technology\'),'),
('(6, \'BSBA\', \'Business & Management\'),'),
('(7, \'BSA\', \'Business & Management\'),'),
('(8, \'BSAIS\', \'Business & Management\'),'),
('(9, \'BSMA\', \'Business & Management\'),'),
('(10, \'BSRTCS\', \'Business & Management\'),'),
('(11, \'ART-2YR\', \'Business & Management\'),'),
('(12, \'BSHM\', \'Hospitality Management\'),'),
('(13, \'BSCM\', \'Hospitality Management\'),'),
('(14, \'HRA-3YR\', \'Hospitality Management\'),'),
('(15, \'HRS-2YR\', \'Hospitality Management\'),'),
('(16, \'BSTM\', \'Tourism Management\'),'),
('(17, \'BSCpE\', \'Engineering\'),'),
('(18, \'BAPSY\', \'Arts & Sciences\'),'),
('(19, \'BMMA\', \'Arts & Sciences\'),'),
('(20, \'BACOMM\', \'Arts & Sciences\'),'),
('(21, \'BSMT\', \'Maritime\'),'),
('(22, \'BSMarE\', \'Maritime\'),'),
('(23, \'BSNAME\', \'Maritime\'),'),
('(24, \'BSCrim\', \'Criminal Justice Education\');'),
(NULL),
('-- --------------------------------------------------------'),
(NULL),
('--'),
('-- Table structure for table `student`'),
('--'),
('DROP TABLE IF EXISTS `student`;'),
('CREATE TABLE `student` ('),
('  `id` bigint(20) NOT NULL,'),
('  `first_name` varchar(255) NOT NULL,'),
('  `last_name` varchar(255) NOT NULL,'),
('  `Course` varchar(255) DEFAULT NULL,'),
('  `address` varchar(255) DEFAULT NULL,'),
('  `number` varchar(255) DEFAULT NULL,'),
('  `email` varchar(255) NOT NULL,'),
('  `password` varchar(255) NOT NULL,'),
('  `course_id` bigint(20) NOT NULL,'),
('  `reserve` int(11) DEFAULT NULL,'),
('  `borrowedBooks` int(11) DEFAULT NULL'),
(') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;'),
(NULL),
('-- --------------------------------------------------------'),
(NULL),
('--'),
('-- Table structure for table `notification`'),
('DROP TABLE IF EXISTS `notification`;'),
('CREATE TABLE `notification` ('),
('  `id` bigint(20) NOT NULL AUTO_INCREMENT,'),
('  `created_at` datetime(6) DEFAULT NULL,'),
('  `is_read` bit(1) NOT NULL,'),
('  `message` varchar(255) DEFAULT NULL,'),
('  `student_id` bigint(20) DEFAULT NULL,'),
('  PRIMARY KEY (`id`),'),
('  KEY `fk_student_id` (`student_id`),'),
('  CONSTRAINT `fk_notification_student_id` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)'),
(') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;'),
(NULL),
('-- --------------------------------------------------------'),
(NULL),
('--'),
('-- Table structure for table `student_borrowed_books`'),
('--'),
('DROP TABLE IF EXISTS `student_borrowed_books`;'),
('CREATE TABLE `student_borrowed_books` ('),
('  `student_id` bigint(20) NOT NULL,'),
('  `book_id` bigint(20) NOT NULL,'),
('  PRIMARY KEY (`student_id`, `book_id`),'),
('  KEY `fk_student_borrowed_books_student_id` (`student_id`),'),
('  KEY `fk_student_borrowed_books_book_id` (`book_id`),'),
('  CONSTRAINT `fk_student_borrowed_books_student_id` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),'),
('  CONSTRAINT `fk_student_borrowed_books_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`)'),
(') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;'),
(NULL),
('--'),
('-- Indexes for dumped tables'),
('--'),
(NULL),
('--'),
('-- Indexes for table `admin`'),
('--'),
('ALTER TABLE `admin`'),
('  ADD PRIMARY KEY (`id`),'),
('  ADD UNIQUE KEY `user` (`email`);'),
(NULL),
('--'),
('-- Indexes for table `book`'),
('--'),
('ALTER TABLE `book`'),
('  ADD PRIMARY KEY (`id`);'),
(NULL),
('--'),
('-- Indexes for table `book_reservers`'),
('--'),
('ALTER TABLE `book_reservers`'),
('  ADD PRIMARY KEY (`book_id`,`student_id`),'),
('  ADD KEY `fk_book_id` (`book_id`),'),
('  ADD KEY `fk_student_id` (`student_id`);'),
(NULL),
('--'),
('-- Indexes for table `courses`'),
('--'),
('ALTER TABLE `courses`'),
('  ADD PRIMARY KEY (`course_id`);'),
(NULL),
('--'),
('-- Indexes for table `student`'),
('--'),
('ALTER TABLE `student`'),
('  ADD PRIMARY KEY (`id`),'),
('  ADD UNIQUE KEY `email` (`email`),'),
('  ADD KEY `FKlpf6c8wcwr3b29i4r315deuv5` (`course_id`);'),
(NULL),
('--'),
('-- Indexes for table `notification`'),
('--'),
('ALTER TABLE `notification`'),
('  ADD PRIMARY KEY (`id`),'),
('  ADD KEY `fk_student_id` (`student_id`);'),
(NULL),
('--'),
('-- Indexes for table `student_borrowed_books`'),
('--'),
('ALTER TABLE `student_borrowed_books`'),
('  ADD PRIMARY KEY (`student_id`,`book_id`),'),
('  ADD KEY `fk_student_borrowed_books_student_id` (`student_id`),'),
('  ADD KEY `fk_student_borrowed_books_book_id` (`book_id`);'),
(NULL),
('--'),
('-- AUTO_INCREMENT for dumped tables'),
('--'),
(NULL),
('--'),
('-- AUTO_INCREMENT for table `admin`'),
('--'),
('ALTER TABLE `admin`'),
('  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;'),
(NULL),
('--'),
('-- AUTO_INCREMENT for table `book`'),
('--'),
('ALTER TABLE `book`'),
('  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;'),
(NULL),
('--'),
('-- AUTO_INCREMENT for table `courses`'),
('--'),
('ALTER TABLE `courses`'),
('  MODIFY `course_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;'),
(NULL),
('--'),
('-- AUTO_INCREMENT for table `student`'),
('--'),
('ALTER TABLE `student`'),
('  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;'),
(NULL),
('--'),
('-- AUTO_INCREMENT for table `notification`'),
('--'),
('ALTER TABLE `notification`'),
('  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;'),
(NULL),
('--'),
('-- Constraints for dumped tables'),
('--'),
(NULL),
('--'),
('-- Constraints for table `student`'),
('--'),
('ALTER TABLE `student`'),
('  ADD CONSTRAINT `FKlpf6c8wcwr3b29i4r315deuv5` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`);'),
('ALTER TABLE `book_reservers`'),
('  ADD CONSTRAINT `fk_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),'),
('  ADD CONSTRAINT `fk_student_id` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`);'),
('ALTER TABLE `notification`'),
('  ADD CONSTRAINT `fk_notification_student_id` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`);'),
('ALTER TABLE `student_borrowed_books`'),
('  ADD CONSTRAINT `fk_student_borrowed_books_student_id` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),'),
('  ADD CONSTRAINT `fk_student_borrowed_books_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`);'),
('COMMIT;'),
(NULL),
('/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;'),
('/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;'),
('/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;');

-- --------------------------------------------------------

--
-- Table structure for table `notification`
--

CREATE TABLE `notification` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `is_read` bit(1) NOT NULL,
  `message` varchar(255) DEFAULT NULL,
  `student_id` bigint(20) DEFAULT NULL,
  `admin_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reservation`
--

CREATE TABLE `reservation` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `status` enum('accepted','pending','rejected') DEFAULT NULL,
  `book_id` bigint(20) DEFAULT NULL,
  `student_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `id` bigint(20) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `Course` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `number` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `course_id` bigint(20) NOT NULL,
  `reserve` int(11) DEFAULT NULL,
  `borrowedBooks` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`id`, `first_name`, `last_name`, `Course`, `address`, `number`, `email`, `password`, `course_id`, `reserve`, `borrowedBooks`) VALUES
(4, 'Alejo', 'Cabanero', NULL, 'Avida Sta Catalina Village', '09682185956', 'Kenthcabanero252@gmail.com', '$2a$10$Afdha5Fg82cSMl0XUmosnepTguqNIelfgZcWQCBxm6cmhMgNgqrSG', 1, NULL, NULL),
(8, 'test', 'test', NULL, 'test', 'test', 'test@gamil.com', '$2a$10$28lbkgHLg9aslZg3hQjxt.kJ/7bnOKCEoC7XDWibGVH2ppqcFJOKC', 1, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `student_borrowed_books`
--

CREATE TABLE `student_borrowed_books` (
  `student_id` bigint(20) NOT NULL,
  `book_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student_reserved_books`
--

CREATE TABLE `student_reserved_books` (
  `student_id` bigint(20) NOT NULL,
  `book_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `book`
--
ALTER TABLE `book`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `book_reservers`
--
ALTER TABLE `book_reservers`
  ADD PRIMARY KEY (`book_id`,`student_id`),
  ADD KEY `fk_book_id` (`book_id`),
  ADD KEY `fk_student_id` (`student_id`);

--
-- Indexes for table `borrow_record`
--
ALTER TABLE `borrow_record`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_borrow_record_student` (`student_id`),
  ADD KEY `fk_borrow_record_book` (`book_id`),
  ADD KEY `idx_borrow_record_status` (`status`),
  ADD KEY `idx_borrow_record_due_date` (`due_date`),
  ADD KEY `idx_borrow_record_student_status` (`student_id`,`status`),
  ADD KEY `idx_borrow_record_book_status` (`book_id`,`status`),
  ADD KEY `idx_borrow_record_borrowed_date` (`borrowed_date`);

--
-- Indexes for table `courses`
--
ALTER TABLE `courses`
  ADD PRIMARY KEY (`course_id`);

--
-- Indexes for table `notification`
--
ALTER TABLE `notification`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_student_id` (`student_id`);

--
-- Indexes for table `reservation`
--
ALTER TABLE `reservation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKiuft3416ayrn5538t6bd108fw` (`student_id`),
  ADD KEY `FKirxtcw4s6lhwi6l9ocrk6bjfy` (`book_id`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `FKlpf6c8wcwr3b29i4r315deuv5` (`course_id`);

--
-- Indexes for table `student_borrowed_books`
--
ALTER TABLE `student_borrowed_books`
  ADD PRIMARY KEY (`student_id`,`book_id`),
  ADD KEY `fk_student_borrowed_books_student_id` (`student_id`),
  ADD KEY `fk_student_borrowed_books_book_id` (`book_id`);

--
-- Indexes for table `student_reserved_books`
--
ALTER TABLE `student_reserved_books`
  ADD PRIMARY KEY (`student_id`,`book_id`),
  ADD KEY `fk_student_reserved_books_student_id` (`student_id`),
  ADD KEY `fk_student_reserved_books_book_id` (`book_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `book`
--
ALTER TABLE `book`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `borrow_record`
--
ALTER TABLE `borrow_record`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `courses`
--
ALTER TABLE `courses`
  MODIFY `course_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `notification`
--
ALTER TABLE `notification`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `reservation`
--
ALTER TABLE `reservation`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `student`
--
ALTER TABLE `student`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `book_reservers`
--
ALTER TABLE `book_reservers`
  ADD CONSTRAINT `fk_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
  ADD CONSTRAINT `fk_student_id` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`);

--
-- Constraints for table `borrow_record`
--
ALTER TABLE `borrow_record`
  ADD CONSTRAINT `fk_borrow_record_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_borrow_record_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `notification`
--
ALTER TABLE `notification`
  ADD CONSTRAINT `fk_notification_student_id` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`);

--
-- Constraints for table `reservation`
--
ALTER TABLE `reservation`
  ADD CONSTRAINT `FKirxtcw4s6lhwi6l9ocrk6bjfy` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
  ADD CONSTRAINT `FKiuft3416ayrn5538t6bd108fw` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`);

--
-- Constraints for table `student`
--
ALTER TABLE `student`
  ADD CONSTRAINT `FKlpf6c8wcwr3b29i4r315deuv5` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`);

--
-- Constraints for table `student_borrowed_books`
--
ALTER TABLE `student_borrowed_books`
  ADD CONSTRAINT `fk_student_borrowed_books_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
  ADD CONSTRAINT `fk_student_borrowed_books_student_id` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`);

--
-- Constraints for table `student_reserved_books`
--
ALTER TABLE `student_reserved_books`
  ADD CONSTRAINT `fk_student_reserved_books_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
  ADD CONSTRAINT `fk_student_reserved_books_student_id` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
