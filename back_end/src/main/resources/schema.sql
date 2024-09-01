--  CREATE TABLE IF NOT EXISTS Images
--  (
--      id          SERIAL PRIMARY KEY, -- 唯一标识符
--      url         TEXT NOT NULL,                     -- 路径
--      name        TEXT NOT NULL,                     -- 名称
--      photo_time  TIMESTAMP NULL,                     -- 拍摄时间（可为空）
--      upload_time TIMESTAMP NOT NULL,                 -- 上传时间
--      size        TEXT NOT NULL,                     -- 大小
--      formatType  TEXT NOT NULL,                     -- 照片格式
--      place       TEXT NULL,                         -- 拍摄地点（可为空）
--      device      TEXT NULL,                         -- 设备信息（可为空）
--      category    TEXT NULL                          -- 类别（可为空）
--      uploadTimeVue  TEXT NULL                        -- 上传时间（时间戳）
--  );
--
--  CREATE TABLE IF NOT EXISTS classification
--  (
--      id          SERIAL PRIMARY KEY,
--      type        VARCHAR(255),  -- 分类类型，如 'date' 或 'place'
--      value       VARCHAR(255)  -- 分类值，如日期 '2024-08-25' 或地点 'New York'
--  );
--
--  CREATE TABLE IF NOT EXISTS classification_image
--  (
--      classification_id INT,
--      image_id INT,
--      FOREIGN KEY (classification_id) REFERENCES classification(id),
--      FOREIGN KEY (image_id) REFERENCES Images(id),
--      PRIMARY KEY (classification_id, image_id)
--  );
--
-- DROP VIEW IF EXISTS ImagesByDate;
--
-- CREATE VIEW ImagesByDate AS
-- SELECT
--     EXTRACT(YEAR FROM photo_time) AS year,
--     EXTRACT(MONTH FROM photo_time) AS month,
--     EXTRACT(DAY FROM photo_time) AS day,
--     COUNT(name) AS total_images,
--     ARRAY_AGG(name) AS image_names -- 将所有照片的 name 聚合成一个数组
-- FROM
--     Images
-- GROUP BY
--     EXTRACT(YEAR FROM photo_time),
--     EXTRACT(MONTH FROM photo_time),
--     EXTRACT(DAY FROM photo_time)
-- ORDER BY
--     year, month, day;

CREATE TABLE IF NOT EXISTS Images
(
  id          INTEGER PRIMARY KEY AUTOINCREMENT, -- 唯一标识符
  url         TEXT NOT NULL,                     -- 路径
  name        TEXT NOT NULL,                     -- 名称
  photo_time  DATETIME NULL,                     -- 拍摄时间（可为空）
  upload_time DATETIME NOT NULL,                 -- 上传时间
  size        TEXT NOT NULL,                     -- 大小
  formatType  TEXT NOT NULL,                     -- 照片格式
  place       TEXT NULL,                         -- 拍摄地点（可为空）
  device      TEXT NULL,                         -- 设备信息（可为空）
  category    TEXT NULL,                         -- 类别（可为空）
  uploadTimeVue  TEXT NULL                        -- 上传时间（时间戳）
);