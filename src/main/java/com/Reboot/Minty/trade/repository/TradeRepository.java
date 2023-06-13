package com.Reboot.Minty.trade.repository;


import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    Trade findByBoardId(Long boardId);

    Page<Trade> findAllByBuyerIdOrSellerId(User buyer, User seller, Pageable pageable);

    int countByBoardIdAndBuyerIdAndSellerId(TradeBoard tradeBoard, User buyer,  User seller);

    @Query("SELECT t FROM Trade t WHERE t.sellerId.id = :userId OR t.buyerId.id = :userId")
    List<Trade> getTradeList(@Param("userId") Long userId);
}