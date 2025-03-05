package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

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
        List<LocalDate> dateList = getDateList(begin, end);

        //构建营业额
        //查询出的日期若当天没有订单，则sql查询不会返回，应手动设置为0.0
        List<Map<String, Object>> turnoverList = orderMapper.getTurnoverData(dateList, Orders.COMPLETED);
        List<Double> amountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            boolean foundDate = false;
            for (Map<String, Object> map : turnoverList) {
                String orderDate = map.get("order_date").toString();
                Double amount = ((BigDecimal) map.get("amount")).doubleValue();
                if (date.toString().equals(orderDate)) {
                    amountList.add(amount);
                    foundDate = true;
                    break;
                }
            }
            if (!foundDate) {
                amountList.add(0.0);
            }
        }

        //封装返回
        String dateString = StringUtils.join(dateList, ",");
        String turnoverString = StringUtils.join(amountList, ",");
        return TurnoverReportVO.builder().dateList(dateString).turnoverList(turnoverString).build();
    }

    /**
     * 统计用户数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //构建连续日期的字符串
        List<LocalDate> dateList = getDateList(begin, end);

        //获取每天新增用户列表
        // select count(id) from user where create_time > '2025-02-01 00:00:00' and create_time < '2025-02-01 23:59:59'
        List<Integer> newUserList = new ArrayList<>();
        //获取截止每天23:59:59的总用户数
        // select count(id) from user where create_time < '2025-02-01 23:59:59'
        List<Integer> totalUserList = new ArrayList<>();

        //视频写法，在循环中查询数据库，实际上耗时
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            newUserList.add(userMapper.countByDate(beginTime, endTime));
            totalUserList.add(userMapper.countByDate(null, endTime));
        }

        //封装返回
        String dateString = StringUtils.join(dateList, ",");
        String newUserString = StringUtils.join(newUserList, ",");
        String totalUserString = StringUtils.join(totalUserList, ",");
        return UserReportVO.builder().dateList(dateString).newUserList(newUserString).totalUserList(totalUserString).build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);

        //获取订单总数和有效订单总数
        List<Integer> totalOrderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer totalOrders = orderMapper.getOrdersCountByDate(beginTime, endTime, null);
            Integer validOrders = orderMapper.getOrdersCountByDate(beginTime, endTime, Orders.COMPLETED);
            totalOrderCountList.add(totalOrders);
            validOrderCountList.add(validOrders);
        }
        Integer totalOrderCount = totalOrderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount.doubleValue();
        }

        String dateString = StringUtils.join(dateList, ",");
        String totalOrderCountString = StringUtils.join(totalOrderCountList, ",");
        String validOrderCountString = StringUtils.join(validOrderCountList, ",");

        return OrderReportVO.builder().dateList(dateString)
                .orderCountList(totalOrderCountString)
                .validOrderCountList(validOrderCountString)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 获取连续日期
     *
     * @param begin
     * @param end
     * @return
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        if (begin.isAfter(end)) {
            throw new RuntimeException("开始日期晚于结束日期，无法查询！");
        }
        LocalDate currentDate = begin;
        while (!currentDate.isAfter(end)) {
            dateList.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        return dateList;
    }
}
