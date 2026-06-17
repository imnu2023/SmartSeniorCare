import mysql.connector
from mysql.connector import Error

def fix_database():
    try:
        connection = mysql.connector.connect(
            host='localhost',
            database='smart_community',
            user='root',
            password='123456',
            charset='utf8mb4'
        )
        
        if connection.is_connected():
            cursor = connection.cursor()
            connection.set_charset_collation('utf8mb4', 'utf8mb4_unicode_ci')
            
            print("=== 修复 device 表 - 英文转中文 ===")
            update_device_sql = """
            UPDATE device SET device_name = %s, device_type = %s, location = %s WHERE device_id = %s
            """
            device_updates = [
                ('客厅灯光', 'light', '客厅', 'dev-001'),
                ('卧室空调', 'aircon', '卧室', 'dev-002'),
                ('窗帘控制器', 'curtain', '客厅', 'dev-003'),
                ('主卧灯光', 'light', '主卧', 'dev-004'),
                ('厨房空调', 'aircon', '厨房', 'dev-005'),
                ('客厅灯光', 'light', '客厅', 'test-dev-001'),
                ('客厅空调', 'aircon', '客厅', 'unique-dev-001')
            ]
            cursor.executemany(update_device_sql, device_updates)
            connection.commit()
            print(f"已更新 {cursor.rowcount} 条设备数据")
            
            print("\n=== 修复 emergency_call 表 - 英文转中文 ===")
            update_emergency_sql = """
            UPDATE emergency_call SET call_type = %s, status = %s, description = %s WHERE id = %s
            """
            emergency_updates = [
                ('医疗', '已完成', '突发心脏病，需要紧急救治', 29),
                ('安全', '处理中', '家中漏水，需要帮助', 30),
                ('医疗', '已解决', '紧急呼叫测试', 31),
                ('医疗', '已解决', '紧急呼叫测试', 32),
                ('火灾', '已取消', '火灾报警测试', 33)
            ]
            cursor.executemany(update_emergency_sql, emergency_updates)
            connection.commit()
            print(f"已更新 {cursor.rowcount} 条紧急呼叫记录")
            
            print("\n=== 修复 service_order 表 - 英文转中文 ===")
            update_order_sql = """
            UPDATE service_order SET status = %s, address = %s WHERE order_no = %s
            """
            order_updates = [
                ('已完成', '幸福社区1号楼101室', 'ORD20260606001'),
                ('待服务', '幸福社区1号楼101室', 'ORD20260610002')
            ]
            cursor.executemany(update_order_sql, order_updates)
            connection.commit()
            print(f"已更新 {cursor.rowcount} 条服务订单")
            
            print("\n=== 修复 message 表乱码 ===")
            update_message_sql = """
            UPDATE message SET content = %s WHERE id = %s
            """
            message_updates = [
                ('爸爸，今天天气不错，记得出门散步', 1),
                ('好的，我下午出去', 2),
                ('记得带伞，可能会下雨', 3),
                ('你好，下周周三下午两点有社区活动', 4),
                ('爸妈，今天晚上我回家吃饭', 5),
                ('好的，我们准备晚饭', 6),
                ('周末有空一起去公园吗', 7),
                ('可以啊，周六上午怎么样', 8),
                ('好的，周六见', 9),
                ('我们出门了', 11),
                ('我到家了', 12),
                ('明天有空来家里坐坐吗', 13),
                ('好的，明天下午三点', 14),
                ('记得带些水果', 15),
                ('好的，知道了', 16),
                ('周末去看电影吗', 17),
                ('好的，想看什么电影', 18),
                ('最近有部新上映的不错', 19),
                ('那我们周六去看', 20)
            ]
            cursor.executemany(update_message_sql, message_updates)
            connection.commit()
            print(f"已修复 {cursor.rowcount} 条消息记录")
            
            print("\n=== 检查并补充空表数据 ===")
            
            cursor.execute("SELECT COUNT(*) FROM device_control;")
            count = cursor.fetchone()[0]
            if count == 0:
                print("device_control 表为空，插入测试数据...")
                control_sql = """
                INSERT INTO device_control (device_id, control_type, control_value, status, created_at)
                VALUES (%s, %s, %s, %s, NOW())
                """
                controls = [
                    (6, 'light', 'on', 'active'),
                    (7, 'aircon', '26', 'active'),
                    (8, 'curtain', 'open', 'active'),
                    (11, 'light', 'on', 'active'),
                    (15, 'aircon', '24', 'active')
                ]
                cursor.executemany(control_sql, controls)
                connection.commit()
                print(f"已插入 {cursor.rowcount} 条设备控制记录")
            
            cursor.execute("SELECT COUNT(*) FROM wallet;")
            count = cursor.fetchone()[0]
            if count == 0:
                print("wallet 表为空，插入测试数据...")
                wallet_sql = """
                INSERT INTO wallet (user_id, balance, created_at, updated_at)
                VALUES (%s, %s, NOW(), NOW())
                """
                wallets = [
                    (2, 500.00),
                    (3, 800.00),
                    (14, 1200.00),
                    (15, 300.00)
                ]
                cursor.executemany(wallet_sql, wallets)
                connection.commit()
                print(f"已插入 {cursor.rowcount} 条钱包记录")
            
            print("\n=== 所有修复完成！===")
            
    except Error as e:
        print(f"数据库错误: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()
            print("数据库连接已关闭")

if __name__ == "__main__":
    fix_database()