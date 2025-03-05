package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 统计营业额数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverData(LocalDate begin, LocalDate end) {
        //构建连续日期的字符串
        List<LocalDate> dateList = new ArrayList<>();
        if(begin.isAfter(end)) {
            throw new RuntimeException("开始日期晚于结束日期，无法查询！");
        }
        LocalDate currentDate = begin;
        while(!currentDate.isAfter(end)) {
            dateList.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        //构建营业额
        //查询出的日期若当天没有订单，则sql查询不会返回，应手动设置为0.0
        List<Map<String, Object>> turnoverList = orderMapper.getTurnoverData(dateList, Orders.COMPLETED);
        List<Double> amountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            boolean foundDate = false;
            for (Map<String, Object> map : turnoverList) {
                String orderDate = map.get("order_date").toString();
                Double amount = ((BigDecimal)map.get("amount")).doubleValue();
                if(date.toString().equals(orderDate)) {
                    amountList.add(amount);
                    foundDate = true;
                    break;
                }
            }
            if(!foundDate) {
                amountList.add(0.0);
            }
        }

        //封装返回
        String dateString = StringUtils.join(dateList, ",");
        String turnoverString = StringUtils.join(amountList, ",");
        return TurnoverReportVO.builder().dateList(dateString).turnoverList(turnoverString).build();
    }
}
