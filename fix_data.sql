USE smart_community;

-- 更新订单表中的英文地址
UPDATE service_order SET address = '幸福社区1号楼101室' WHERE address LIKE '%Community 1 Building 101%';
UPDATE service_order SET address = '幸福社区1号楼101室' WHERE address LIKE '%????1??101?%';
UPDATE service_order SET address = '幸福社区' WHERE address = '1';

-- 更新设备表中的中文乱码
UPDATE device SET deviceName = '智能灯光', location = '客厅' WHERE deviceName = '???';
UPDATE device SET deviceName = '智能空调', location = '卧室' WHERE deviceName = '????';
UPDATE device SET deviceName = '智能窗帘', location = '阳台' WHERE deviceName = '?????';

SELECT '数据修复完成' AS result;