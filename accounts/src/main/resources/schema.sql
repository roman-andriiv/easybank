create TABLE IF NOT EXISTS `customer`
(
    `customer_id`   int AUTO_INCREMENT PRIMARY KEY,
    `name`          varchar(100) NOT NULL,
    `email`         varchar(100) NOT NULL,
    `mobile_number` varchar(20)  NOT NULL,
    `created_at`    date         NOT NULL,
    `created_by`    varchar(20)  NOT NULL,
    `updated_at`    date        DEFAULT NULL,
    `updated_by`    varchar(20) DEFAULT NULL
);

create TABLE IF NOT EXISTS `account`
(
    `customer_id`    int          NOT NULL,
    `account_number` int AUTO_INCREMENT PRIMARY KEY,
    `account_type`   varchar(100) NOT NULL,
    `branch_address` varchar(200) NOT NULL,
    `created_at`     date         NOT NULL,
    `created_by`     varchar(20)  NOT NULL,
    `updated_at`     date        DEFAULT NULL,
    `updated_by`     varchar(20) DEFAULT NULL
);

INSERT INTO `customer` (`name`, `email`, `mobile_number`, `created_at`, `created_by`)
VALUES ('Alice Johnson', 'alice.johnson@example.com', '500100200', CURRENT_DATE, 'test-data'),
       ('Bob Smith', 'bob.smith@example.com', '500200300', CURRENT_DATE, 'test-data'),
       ('Carol Williams', 'carol.williams@example.com', '500300400', CURRENT_DATE, 'test-data');

INSERT INTO `account`
(`customer_id`, `account_number`, `account_type`, `branch_address`, `created_at`, `created_by`)
VALUES (1, 1000000001, 'Savings', '10 Market Street, Warsaw', CURRENT_DATE, 'test-data'),
       (2, 1000000002, 'Savings', '25 River Road, Krakow', CURRENT_DATE, 'test-data'),
       (3, 1000000003, 'Savings', '8 Central Avenue, Gdansk', CURRENT_DATE, 'test-data');
