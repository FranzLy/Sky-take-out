package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdersAmountDTO implements Serializable {
    //订单日期
    private String orderDate;

    //订单金额
    private Double orderAmount;
}
