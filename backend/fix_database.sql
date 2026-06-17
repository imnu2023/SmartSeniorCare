USE smart_community;

SET NAMES utf8mb4;

-- 修复 device 表 - 英文转中文
UPDATE device SET device_name = '客厅灯光', location = '客厅' WHERE device_id = 'dev-001';
UPDATE device SET device_name = '卧室空调', location = '卧室' WHERE device_id = 'dev-002';
UPDATE device SET device_name = '窗帘控制器', location = '客厅' WHERE device_id = 'dev-003';
UPDATE device SET device_name = '主卧灯光', location = '主卧' WHERE device_id = 'dev-004';
UPDATE device SET device_name = '厨房空调', location = '厨房' WHERE device_id = 'dev-005';
UPDATE device SET device_name = '客厅灯光', location = '客厅' WHERE device_id = 'test-dev-001';
UPDATE device SET device_name = '客厅空调', location = '客厅' WHERE device_id = 'unique-dev-001';

-- 修复 emergency_call 表 - 英文转中文
UPDATE emergency_call SET call_type = '医疗', status = '已完成', description = '突发心脏病，需要紧急救治' WHERE id = 29;
UPDATE emergency_call SET call_type = '安全', status = '处理中', description = '家中漏水，需要帮助' WHERE id = 30;
UPDATE emergency_call SET call_type = '医疗', status = '已解决', description = '紧急呼叫测试' WHERE id = 31;
UPDATE emergency_call SET call_type = '医疗', status = '已解决', description = '紧急呼叫测试' WHERE id = 32;
UPDATE emergency_call SET call_type = '火灾', status = '已取消', description = '火灾报警测试' WHERE id = 33;

-- 修复 service_order 表 - 英文转中文
UPDATE service_order SET status = '已完成', address = '幸福社区1号楼101室' WHERE order_no = 'ORD20260606001';
UPDATE service_order SET status = '待服务', address = '幸福社区1号楼101室' WHERE order_no = 'ORD20260610002';

-- 修复 message 表乱码
UPDATE message SET content = '爸爸，今天天气不错，记得出门散步' WHERE id = 1;
UPDATE message SET content = '好的，我下午出去' WHERE id = 2;
UPDATE message SET content = '记得带伞，可能会下雨' WHERE id = 3;
UPDATE message SET content = '你好，下周周三下午两点有社区活动' WHERE id = 4;
UPDATE message SET content = '爸妈，今天晚上我回家吃饭' WHERE id = 5;
UPDATE message SET content = '好的，我们准备晚饭' WHERE id = 6;
UPDATE message SET content = '周末有空一起去公园吗' WHERE id = 7;
UPDATE message SET content = '可以啊，周六上午怎么样' WHERE id = 8;
UPDATE message SET content = '好的，周六见' WHERE id = 9;
UPDATE message SET content = '我们出门了' WHERE id = 11;
UPDATE message SET content = '我到家了' WHERE id = 12;
UPDATE message SET content = '明天有空来家里坐坐吗' WHERE id = 13;
UPDATE message SET content = '好的，明天下午三点' WHERE id = 14;
UPDATE message SET content = '记得带些水果' WHERE id = 15;
UPDATE message SET content = '好的，知道了' WHERE id = 16;
UPDATE message SET content = '周末去看电影吗' WHERE id = 17;
UPDATE message SET content = '好的，想看什么电影' WHERE id = 18;
UPDATE message SET content = '最近有部新上映的不错' WHERE id = 19;
UPDATE message SET content = '那我们周六去看' WHERE id = 20;

-- 检查并补充 device_control 表
INSERT INTO device_control (device_id, control_type, control_value, status, created_at)
SELECT 6, 'light', 'on', 'active', NOW() FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_control);
INSERT INTO device_control (device_id, control_type, control_value, status, created_at) VALUES (7, 'aircon', '26', 'active', NOW());
INSERT INTO device_control (device_id, control_type, control_value, status, created_at) VALUES (8, 'curtain', 'open', 'active', NOW());
INSERT INTO device_control (device_id, control_type, control_value, status, created_at) VALUES (11, 'light', 'on', 'active', NOW());
INSERT INTO device_control (device_id, control_type, control_value, status, created_at) VALUES (15, 'aircon', '24', 'active', NOW());

-- 检查并补充 wallet 表
INSERT INTO wallet (user_id, balance, created_at, updated_at) VALUES (2, 500.00, NOW(), NOW());
INSERT INTO wallet (user_id, balance, created_at, updated_at) VALUES (3, 800.00, NOW(), NOW());
INSERT INTO wallet (user_id, balance, created_at, updated_at) VALUES (14, 1200.00, NOW(), NOW());
INSERT INTO wallet (user_id, balance, created_at, updated_at) VALUES (15, 300.00, NOW(), NOW());

SELECT '数据库修复完成' AS result;