package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@Api(tags = "数据统计相关接口")
@RequestMapping("/admin/report")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;


    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("营业额统计")
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("查询{}到{}的营业额数据", begin, end);

        //查询日期对应的营业额数据
        //将日期列表和营业额列表封装到VO中
        TurnoverReportVO  reportVO = reportService.getTurnoverData(begin, end);
        return Result.success(reportVO);
    }

    @ApiOperation("用户统计")
    @GetMapping("/userStatistics")
    public Result<UserReportVO> getUserStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("查询{}到{}的用户数据", begin, end);

        //查询日期对应的用户数据
        //将日期列表和用户数据封装到VO中
        UserReportVO  reportVO = reportService.getUserStatistics(begin, end);
        return Result.success(reportVO);
    }

    @ApiOperation("订单统计")
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> getOrdersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("查询{}到{}的订单数据", begin, end);

        //查询日期对应的订单数据
        //将日期列表和订单数据封装到VO中
        OrderReportVO  reportVO = reportService.getOrderStatistics(begin, end);
        return Result.success(reportVO);
    }

    @ApiOperation("销量排名Top10")
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> getSalesTop10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("查询{}到{}的Top10数据", begin, end);

        //查询日期对应的订单数据
        //将日期列表和订单数据封装到VO中
        SalesTop10ReportVO  reportVO = reportService.getSalesTop10(begin, end);
        return Result.success(reportVO);
    }

    @ApiOperation("运营数据统计")
    @GetMapping("/export")
    public void export(HttpServletResponse reponse) {
        reportService.export(reponse);
    }
}
