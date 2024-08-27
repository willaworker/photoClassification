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