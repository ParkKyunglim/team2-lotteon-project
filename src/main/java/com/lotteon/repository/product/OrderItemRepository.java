package com.lotteon.repository.product;

import com.lotteon.entity.member.Seller;
import com.lotteon.entity.product.Order;
import com.lotteon.entity.product.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    Page<OrderItem> findAllBySeller(Seller seller, Pageable pageable);

    Optional<OrderItem> findByOrder_Id(Long orderId);

    List<OrderItem> findAllByOrder_Id(Long orderId);

    List<OrderItem> findAllBySellerAndOrder_Id(Seller seller, Long orderId);

    Page<OrderItem> findAllByOrder_OrderRdateBetween(Timestamp startTimestamp, Timestamp endTimestamp, Pageable pageable);

    List<OrderItem> findAllByDeliSdateBefore(LocalDate threeDaysAgo);

    List<OrderItem> findAllByDeliSdateAfter(LocalDate threeDaysAgo);
}
