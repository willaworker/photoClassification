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

--DROP VIEW IF EXISTS ImagesByCategory;
--CREATE VIEW ImagesByCategory AS
--WITH category_expanded AS (
--    SELECT
--        id,
--        name,
--        -- 1. 将 category 字符串中的部分符号替换掉并拆分成数组
--        --    REPLACE(REPLACE(category, '","', '","'), '"', '')：
--        --    第一次 REPLACE 替换掉逗号和双引号之间的内容（例如 `","` 替换成 `,`），第二次 REPLACE 去掉所有双引号。
--        --    结果是一个没有引号的类别字符串，例如从 `"类别1","类别2"` 变为 `类别1,类别2`。
--
--        -- 2. STRING_TO_ARRAY(REPLACE(...), ',')：
--        --    将处理后的字符串按逗号分隔，转换为数组。例如，`类别1,类别2` 变为 `{类别1,类别2}`。
--
--        -- 3. UNNEST(STRING_TO_ARRAY(...))：
--        --    将数组中的每个元素转换为独立的行。例如 `{类别1,类别2}` 变为两行，分别包含 `类别1` 和 `类别2`。
--
--        -- 4. TRIM(BOTH '"][' FROM UNNEST(...))：
--        --    去掉任何可能存在的残留的方括号 `[` 或 `]`，确保最终得到干净的类别名称。
--        TRIM(BOTH '"][' FROM UNNEST(STRING_TO_ARRAY(REPLACE(REPLACE(category, '","', '","'), '"', ''), ','))) AS single_category
--    FROM
--        Images
--    WHERE
--        -- 确保 category 字段不为空并且不是一个空的数组
--        category IS NOT NULL AND category != '[]'
--)
--SELECT
--    single_category AS category,
--    COUNT(id) AS total_images,
--    ARRAY_AGG(name) AS image_names
--FROM
--    category_expanded
--GROUP BY
--    single_category
--ORDER BY
--    category;

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
  category    TEXT NULL                          -- 类别（可为空）
);

