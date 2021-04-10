package com.aitrades.blockchain.eth.gateway.integration.createorder;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.mongodb.inbound.MongoDbMessageSource;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.aitrades.blockchain.eth.gateway.domain.Order;

@Configuration
@ComponentScan("com.aitrades.blockchain.eth.gateway.integration")
@IntegrationComponentScan("com.aitrades.blockchain.eth.gateway.integration")
@EnableIntegration
public class OrderIntegrationConfig {
	
	private static final String CREATE_ORDER_TASK_EXECUTOR_THREAD = "createOrder_task_executor_thread";

	private static final String ORDER = "order";

	private static final String ORDER_CODE_$IN_83_84_READ_AVAL = "{'orderCode' : { $in: [83, 84] }, 'read': 'AVAL'}";

	@Resource(name="createOrderBinding")
	public Binding createOrderBinding;
	
	@Resource(name="createOrderDirectExchange")
	public DirectExchange createOrderDirectExchange;
	
	@Resource(name="createOrderQueue")
	public Queue createOrderQueue;
	
	@Resource(name="createOrderRabbitTemplate")
	public AmqpTemplate createOrderRabbitTemplate;
	
	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata poller() {
		return Pollers.fixedDelay(2, TimeUnit.SECONDS).get();
	}

	@Bean
	public Executor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(20);
		executor.setThreadNamePrefix(CREATE_ORDER_TASK_EXECUTOR_THREAD);
		executor.initialize();
		return executor;
	}
	
	@Bean
	@Autowired
	public IntegrationFlow mongoDbToRabbitQueue() throws Exception {
		return IntegrationFlows.from(mongoInboundSource(), c -> c.poller(poller()))
							   .handle("rabbitMqCreateOrderEndpoint", "addNewOrderToRabbitMq")
							   .channel(IntegrationContextUtils.NULL_CHANNEL_BEAN_NAME)
							   .get();
	}
	
	@Bean(name="rabbitMqCreateOrderEndpoint")
	public OrderGatewayEndpoint rabbitMqCreateOrderEndpoint() {
		return new OrderGatewayEndpoint();
	}

	@Bean(name="createOrderErrorHandler")
	public GlobalErrorHandler createOrderErrorHandler() {
		return new GlobalErrorHandler();
	}

	@Autowired
	@Qualifier("errorChannel")
	private PublishSubscribeChannel errorChannel;
	
	@Bean
	public IntegrationFlow errorHandlingFlow() {
		return IntegrationFlows.from(errorChannel)
				.handle("createOrderErrorHandler", "errorFlow")
				.get();
	}

	@Bean(name ="orderMongoDbFactory")
	@Primary
	@Autowired
	public SimpleMongoClientDatabaseFactory orderMongoDbFactory() throws Exception {
        return new SimpleMongoClientDatabaseFactory(com.mongodb.client.MongoClients.create(), ORDER);
    }
	
	@Bean
	@Autowired
	public MessageSource<Object> mongoInboundSource() throws Exception {// {'side' : 'buy'} // { qty: { $in: [ 5, 15 ] } } //  { $or: [ { 'status': 'A' } , { age: 50 } ] } {'orderCode' : { $in: [83, 84] }, 'read': 'AVAL'}  "{'orderCode' : { $in: [83, 84] }}"  
		MongoDbMessageSource messageSource = new MongoDbMessageSource(orderMongoDbFactory(), new LiteralExpression(ORDER_CODE_$IN_83_84_READ_AVAL));
		messageSource.setEntityClass(Order.class);
		messageSource.setCollectionNameExpression(new LiteralExpression(ORDER));
		return messageSource;
	}
}
