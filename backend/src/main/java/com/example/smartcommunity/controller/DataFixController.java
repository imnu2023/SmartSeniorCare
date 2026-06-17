package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.entity.Device;
import com.example.smartcommunity.entity.ServiceOrder;
import com.example.smartcommunity.mapper.DeviceMapper;
import com.example.smartcommunity.mapper.ServiceOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/fix")
@RequiredArgsConstructor
public class DataFixController {
    
    private final ServiceOrderMapper orderMapper;
    private final DeviceMapper deviceMapper;
    
    @PostMapping("/orders")
    public ApiResponse<String> fixOrders() {
        List<ServiceOrder> orders = orderMapper.selectList(null);
        int count = 0;
        
        for (ServiceOrder order : orders) {
            String address = order.getAddress();
            if (address != null && (address.contains("Community") || address.contains("?") || address.equals("1"))) {
                if (address.contains("Building 101")) {
                    order.setAddress("幸福社区1号楼101室");
                } else if (address.contains("Building 101")) {
                    order.setAddress("幸福社区1号楼101室");
                } else if (address.contains("?") || address.equals("1")) {
                    order.setAddress("幸福社区");
                }
                order.setUpdatedAt(LocalDateTime.now());
                orderMapper.updateById(order);
                count++;
            }
        }
        
        return ApiResponse.success("修复了 " + count + " 条订单数据");
    }
    
    @PostMapping("/devices")
    public ApiResponse<String> fixDevices() {
        List<Device> devices = deviceMapper.selectList(null);
        int count = 0;
        
        for (Device device : devices) {
            boolean updated = false;
            
            String deviceName = device.getDeviceName();
            if (deviceName != null && deviceName.contains("?")) {
                if (deviceName.equals("???") || device.getDeviceType().equals("light")) {
                    device.setDeviceName("智能灯光");
                } else if (deviceName.equals("????") || device.getDeviceType().equals("aircon")) {
                    device.setDeviceName("智能空调");
                } else if (deviceName.equals("?????") || device.getDeviceType().equals("curtain")) {
                    device.setDeviceName("智能窗帘");
                }
                updated = true;
            }
            
            String location = device.getLocation();
            if (location != null && location.contains("?")) {
                if (device.getDeviceType().equals("light")) {
                    device.setLocation("客厅");
                } else if (device.getDeviceType().equals("aircon")) {
                    device.setLocation("卧室");
                } else if (device.getDeviceType().equals("curtain")) {
                    device.setLocation("阳台");
                }
                updated = true;
            }
            
            if (updated) {
                device.setUpdatedAt(LocalDateTime.now());
                deviceMapper.updateById(device);
                count++;
            }
        }
        
        return ApiResponse.success("修复了 " + count + " 条设备数据");
    }
    
    @PostMapping("/all")
    public ApiResponse<String> fixAll() {
        fixOrders();
        fixDevices();
        return ApiResponse.success("所有数据修复完成");
    }
}