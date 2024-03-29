package com.aitrades.blockchain.eth.gateway.integration.snipe;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.mongodb.inbound.MongoDbMessageSource;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;

@Configuration
@ComponentScan("com.aitrades.blockchain.eth.gateway.integration")
@IntegrationComponentScan("com.aitrades.blockchain.eth.gateway.integration")
@EnableIntegration
public class SnipeOrderIntegrationConfig {
	
	private static final String SNIPE_ORDER_TASK_EXECUTOR_THREAD = "snipeOrder_task_executor_thread";

	private static final String SNIPE_TRANSACTION_REQUEST = "snipeTransactionRequest";

	private static final String $AND_SNIPE_STATUS_WORKING_READ_AVAL = "{ $and: [ {'snipeStatus':'WORKING'}, { 'read':'AVAL'} ] }";

	@Resource(name="snipeOrderBinding")
	public Binding snipeOrderBinding;
	
	@Resource(name="snipeOrderDirectExchange")
	public DirectExchange snipeOrderDirectExchange;
	
	@Resource(name="snipeOrderQueue")
	public Queue snipeOrderQueue;
	
	@Resource(name="snipeOrderRabbitTemplate")
	public AmqpTemplate snipeOrderRabbitTemplate;
	
	
	@Bean(name = "snipePoller")
	public PollerMetadata snipePoller() {
		return Pollers.fixedDelay(1, TimeUnit.SECONDS).get();
	}

	@Bean
	public TaskExecutor snipeExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(4);
		executor.setThreadNamePrefix(SNIPE_ORDER_TASK_EXECUTOR_THREAD);
		executor.initialize();
		return executor;
	}

	@Bean
	@Autowired
	public IntegrationFlow mongoDbToRabbitSnipeQueue() throws Exception {
		return IntegrationFlows.from(mongoSnipeInboundSource(), c -> c.poller(snipePoller()))
							   .handle("rabbitMqsnipeOrderEndpoint", "addSnipeOrderToRabbitMq")
							   .channel(IntegrationContextUtils.NULL_CHANNEL_BEAN_NAME)
							   .get();
	}
	

	@Bean(name ="snipeOrderMongoDbFactory")
	@Autowired
	public SimpleMongoClientDatabaseFactory snipeOrderMongoDbFactory() throws Exception {
        return new SimpleMongoClientDatabaseFactory(com.mongodb.client.MongoClients.create(), "snipeOrder");
    }
	
	@Bean(name="rabbitMqsnipeOrderEndpoint")
	public SnipeOrderGatewayEndpoint rabbitMqsnipeOrderEndpoint() {
		return new SnipeOrderGatewayEndpoint();
	}
	
	@Bean
	@Autowired
	public MessageSource<Object> mongoSnipeInboundSource() throws Exception {
		MongoDbMessageSource messageSource = new MongoDbMessageSource(snipeOrderMongoDbFactory(), new LiteralExpression($AND_SNIPE_STATUS_WORKING_READ_AVAL));
		messageSource.setEntityClass(SnipeTransactionRequest.class);
		messageSource.setCollectionNameExpression(new LiteralExpression(SNIPE_TRANSACTION_REQUEST));
		return messageSource;
	}
}
