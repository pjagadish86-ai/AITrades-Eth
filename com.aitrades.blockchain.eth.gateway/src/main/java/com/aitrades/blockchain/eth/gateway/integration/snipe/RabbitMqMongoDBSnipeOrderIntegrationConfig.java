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
public class RabbitMqMongoDBSnipeOrderIntegrationConfig {
	
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
		PollerMetadata poll = Pollers.fixedDelay(30, TimeUnit.SECONDS).get();
		poll.setTaskExecutor(snipeExecutor());
		// poll.setAdviceChain(transactionInterceptor());
		return poll;
	}

	@Bean
	public TaskExecutor snipeExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(4);
		executor.setThreadNamePrefix("snipeOrder_task_executor_thread");
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
	public RabbitMqSnipeOrderEndpoint rabbitMqsnipeOrderEndpoint() {
		return new RabbitMqSnipeOrderEndpoint();
	}

	//TODO: use reactive programming and mongodb driver implementation to kick of any inserts and update.
	@Bean
	@Autowired
	public MessageSource<Object> mongoSnipeInboundSource() throws Exception {// {'side' : 'buy'} // { qty: { $in: [ 5, 15 ] } } //  { $or: [ { 'status': 'A' } , { age: 50 } ] }
		MongoDbMessageSource messageSource = new MongoDbMessageSource(snipeOrderMongoDbFactory(), new LiteralExpression("{'snipeStatus': 'PENDING'}"));
		messageSource.setEntityClass(SnipeTransactionRequest.class);
		messageSource.setCollectionNameExpression(new LiteralExpression("snipeTransactionRequest"));
		return messageSource;
	}
}
