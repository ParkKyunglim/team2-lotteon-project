package com.lotteon.entity.member;

import com.lotteon.dto.responseDto.GetIncomeDto;
import com.lotteon.dto.responseDto.GetShopsDto;
import com.lotteon.entity.product.OrderItem;
import com.lotteon.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seller")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 번호

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "mem_id")
    @ToString.Exclude
    private Member member;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "seller")
    @ToString.Exclude
    private List<Product> product;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL , mappedBy = "seller")
    @ToString.Exclude
    private List<OrderItem> orderItems;

    @Column(name = "sell_grade")
    private int sellGrade; // 판매자 등급

    @Column(name = "sell_company")
    private String sellCompany; // 상호명

    @Column(name = "sell_representative")
    private String sellRepresentative; // 대표명

    @Column(name = "sell_business_code")
    private String sellBusinessCode; // 사업자번호

    @Column(name = "sell_order_code")
    private String sellOrderCode; // 통신판매업번호

    @Column(name = "sell_hp")
    private String sellHp; // 판매자 전화번호

    @Column(name = "sell_fax")
    private String sellFax; // 팩스번호

    @Column(name = "sell_email")
    private String sellEmail;

    @Column(name = "sell_addr")
    private String sellAddr; // 판매자 주소

    // Entity -> DTO 변환

    public GetShopsDto toGetShopsDto() {
        String state;
        if(member.getMemState().equals("basic")){
            state = "운영준비";
        } else if(member.getMemState().equals("leave")){
            state = "운영중지";
        } else {
            state = "운영중";
        }

        return GetShopsDto.builder()
                .businessCode(formatBusinessCode(sellBusinessCode))
                .orderCode(sellOrderCode)
                .hp(formatPhoneNumber(sellHp))
                .company(sellCompany)
                .state(state)
                .id(id)
                .name(sellRepresentative)
                .build();
    }

    public static String formatBusinessCode(String code) {
        if (code == null || code.length() != 10) {
            throw new IllegalArgumentException("Invalid business code. It should be exactly 10 digits.");
        }
        return code.substring(0, 3) + "-" + code.substring(3, 5) + "-" + code.substring(5);
    }

    public static String formatPhoneNumber(String number) {
        if (number == null || number.length() != 11) {
            throw new IllegalArgumentException("Invalid phone number. It should be exactly 11 digits.");
        }
        return number.substring(0, 3) + "-" + number.substring(3, 7) + "-" + number.substring(7);
    }

    public GetIncomeDto toGetIncomeDto(){
        int deliCnt=0;
        int deliCompleteCnt=0;
        int confirmCnt=0;
        Double totalPrice = 0.0;
        Double totalRealPrice = 0.0;
        int variableOrderCnt=0;
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

            for(OrderItem orderItem : orderItems){
                LocalDateTime orderDate = orderItem.getOrder().getOrderRdate().toLocalDateTime();
                if(orderDate.isAfter(todayStart)&&orderDate.isBefore(todayEnd)){
                    if(orderItem.getState2()==1){ //배송중
                        deliCnt++;
                    }
                    if(orderItem.getState2()==4){ // 배송완료
                        deliCompleteCnt++;
                    }
                    if(orderItem.getState1()==1){ // 구매확정
                        confirmCnt++;
                        totalRealPrice += orderItem.getTotal();
                    }
                    totalPrice += orderItem.getTotal();
                    variableOrderCnt++;
                }
            }

            return GetIncomeDto.builder()
                    .busiCode(formatBusinessCode(sellBusinessCode))
                    .company(sellCompany)
                    .orderCnt(variableOrderCnt)
                    .completeCnt(variableOrderCnt)
                    .deliCnt(deliCnt)
                    .deliCompleteCnt(deliCompleteCnt)
                    .confirmCnt(confirmCnt)
                    .totalRealPrice(totalRealPrice)
                    .totalPrice(totalPrice)
                    .id(id)
                    .build();
    }

    public GetIncomeDto toGetIncomeDto2(){
        int deliCnt=0;
        int deliCompleteCnt=0;
        int confirmCnt=0;
        int variableOrderCnt=0;
        Double totalPrice = 0.0;
        Double totalRealPrice = 0.0;
        LocalDateTime oneWeekAgoStart = LocalDateTime.now().minus(1, ChronoUnit.WEEKS).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        for(OrderItem orderItem : orderItems){
            LocalDateTime orderDate = orderItem.getOrder().getOrderRdate().toLocalDateTime();
            if(orderDate.isAfter(oneWeekAgoStart)&&orderDate.isBefore(todayEnd)){
                if(orderItem.getState2()==1){ //배송중
                    deliCnt++;
                }
                if(orderItem.getState2()==4){ // 배송완료
                    deliCompleteCnt++;
                }
                if(orderItem.getState1()==1){ // 구매확정
                    confirmCnt++;
                    totalRealPrice += orderItem.getTotal();
                }
                totalPrice += orderItem.getTotal();
                variableOrderCnt ++;
            }
        }

        return GetIncomeDto.builder()
                .busiCode(formatBusinessCode(sellBusinessCode))
                .company(sellCompany)
                .orderCnt(variableOrderCnt)
                .completeCnt(variableOrderCnt)
                .deliCnt(deliCnt)
                .deliCompleteCnt(deliCompleteCnt)
                .confirmCnt(confirmCnt)
                .totalRealPrice(totalRealPrice)
                .totalPrice(totalPrice)
                .id(id)
                .build();

    }

    public GetIncomeDto toGetIncomeDto3(){
        int deliCnt=0;
        int deliCompleteCnt=0;
        int confirmCnt=0;
        Double totalPrice = 0.0;
        Double totalRealPrice = 0.0;
        int variableOrderCnt=0;
        LocalDateTime oneMonthAgoStart = LocalDateTime.now().minus(1, ChronoUnit.MONTHS).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        for(OrderItem orderItem : orderItems){
            LocalDateTime orderDate = orderItem.getOrder().getOrderRdate().toLocalDateTime();
            if(orderDate.isAfter(oneMonthAgoStart)&&orderDate.isBefore(todayEnd)){
                if(orderItem.getState2()==1){ //배송중
                    deliCnt++;
                }
                if(orderItem.getState2()==4){ // 배송완료
                    deliCompleteCnt++;
                }
                if(orderItem.getState1()==1){ // 구매확정
                    confirmCnt++;
                    totalRealPrice += orderItem.getTotal();
                }
                totalPrice += orderItem.getTotal();
                variableOrderCnt++;
            }
        }

        return GetIncomeDto.builder()
                .busiCode(formatBusinessCode(sellBusinessCode))
                .company(sellCompany)
                .orderCnt(variableOrderCnt)
                .completeCnt(variableOrderCnt)
                .deliCnt(deliCnt)
                .deliCompleteCnt(deliCompleteCnt)
                .confirmCnt(confirmCnt)
                .totalRealPrice(totalRealPrice)
                .totalPrice(totalPrice)
                .id(id)
                .build();

    }
}
