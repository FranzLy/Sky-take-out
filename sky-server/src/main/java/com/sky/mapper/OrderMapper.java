package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersAmountDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     * @param orders
     */
    void insertOrder(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 批量修改订单
     * @param ordersList
     */
    void updateBatchly(List<Orders> ordersList);

    /**
     * 查询超时订单
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getTimeOutOrders(int status, LocalDateTime time);

    /**
     * 分页条件查询并按下单时间排序
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    /**
     * 根据状态统计订单数量
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);


    /**
     * 统计指定日期内的菜品订单金额
     * @param dates
     * @param status
     * @return
     */
    List<OrdersAmountDTO> getTurnoverData(@Param("dates") List<LocalDate> dates, Integer status);

    /**
     * 根据日期统计订单数量
     * @param beginTime
     * @param endTime
     */
    Integer getOrdersCountByDate(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    /**
     * 查询销量排名top10
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    //SELECT od.name, SUM(od.number) as total_number
    //FROM order_detail od, orders o
    //WHERE od.order_id = o.id
    //  and o.status = 5
    //  and o.order_time > '2025-03-03 00:00:00' AND o.order_time <= '2025-03-03 23:59:59'
    //GROUP BY name
    //ORDER BY total_number DESC
    //LIMIT 0,10;
    List<GoodsSalesDTO> getGoodSalesTop10(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    /**
     * 根据动态条件统计营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 根据动态条件统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
