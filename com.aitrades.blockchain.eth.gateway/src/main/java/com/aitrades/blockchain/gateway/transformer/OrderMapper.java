package com.aitrades.blockchain.gateway.transformer;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.web3j.protocol.core.methods.request.Transaction;

import com.aitrades.blockchain.gateway.domain.Order;

@Mapper
public interface OrderMapper {
	
    @Mappings({
        @Mapping(target="from", source="order.from"),
        @Mapping(target="to", source="order.to"),
        @Mapping(target="gas", source="order.gasEth"),
        @Mapping(target="gasPrice", source="order.gasPriceEth"),
        @Mapping(target="value", source="order.quatity"),
        @Mapping(target="data", source="order.data"),
        @Mapping(target="nonce", source="order.nonce"),
        @Mapping(target="gasPremium", source="order.gasPremiumEth"),
        @Mapping(target="feeCap", source="order.feeCapEth")
      })
    Transaction orderToTransaction(Order order);
    
    List<Transaction> ordersToTransactions(List<Order> orders); 
}
