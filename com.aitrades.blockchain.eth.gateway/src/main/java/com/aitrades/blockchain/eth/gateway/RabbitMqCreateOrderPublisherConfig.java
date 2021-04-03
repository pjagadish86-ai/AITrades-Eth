package com.aitrades.blockchain.eth.gateway;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqCreateOrderPublisherConfig {

	@Value("${aitrades.order.process.rabbitmq.queue}")
	String createOrderQueueName;

	@Value("${aitrades.order.process.rabbitmq.exchange}")
	String createOrderExchangeName;

	@Value("${aitrades.order.process.rabbitmq.routingkey}")
	private String createOrderRoutingkey;
	
	@Autowired
	private MessageConverter jsonMessageConverter;
	
	@Bean(name="createOrderQueue")
	public Queue createOrderQueue() {
		return new Queue(createOrderQueueName, false);
	}

	@Bean(name="createOrderDirectExchange")
	public DirectExchange createOrderDirectExchange() {
		return new DirectExchange(createOrderExchangeName);
	}

	@Bean(name="createOrderBinding")
	public Binding createOrderBinding() {
		return BindingBuilder.bind(createOrderQueue())
							 .to(createOrderDirectExchange())
							 .with(createOrderRoutingkey);
	}

	@Bean(name = "createOrderRabbitTemplate")
	public AmqpTemplate createOrderRabbitTemplate(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter);
		rabbitTemplate.setExchange(createOrderExchangeName);
		rabbitTemplate.setDefaultReceiveQueue(createOrderQueueName);
		rabbitTemplate.setRoutingKey(createOrderRoutingkey);
		return rabbitTemplate;
	}

}