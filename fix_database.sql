USE smart_community;

-- 修改表字符集
ALTER TABLE service_order CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE device CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 更新订单地址
UPDATE service_order SET address = '幸福社区1号楼101室' WHERE address LIKE '%Community%';
UPDATE service_order SET address = '幸福社区1号楼101室' WHERE address LIKE '%Building%';
UPDATE service_order SET address = '幸福社区' WHERE address = '1';
UPDATE service_order SET address = '幸福社区' WHERE address REGEXP '^\\?+$';

-- 更新设备名称和位置
UPDATE device SET deviceName = '智能灯光', location = '客厅' WHERE id = 6;
UPDATE device SET deviceName = '智能空调', location = '卧室' WHERE id = 7;
UPDATE device SET deviceName = '智能窗帘', location = '阳台' WHERE id = 8;
UPDATE device SET deviceName = '智能灯光', location = '书房' WHERE id = 9;
UPDATE device SET deviceName = '智能空调', location = '客厅' WHERE id = 10;
UPDATE device SET deviceName = '智能灯光', location = '厨房' WHERE id = 16;
UPDATE device SET deviceName = '智能插座', location = '客厅' WHERE id = 18;