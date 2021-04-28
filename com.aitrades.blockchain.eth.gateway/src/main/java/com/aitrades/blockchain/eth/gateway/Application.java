package com.aitrades.blockchain.eth.gateway;

import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.tx.response.NoOpProcessor;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@SpringBootApplication(scanBasePackages = { "com.aitrades.blockchain.eth.gateway" })
@EnableAsync
@EnableMongoRepositories(basePackages = { "com.aitrades.blockchain.eth.gateway.repository" })
@Lazy
public class Application {

	private static final int _40 = 40;

	private static final String ETH_ENDPOINT_WSS = "wss://cool-sparkling-dawn.quiknode.pro/d5ffadd5dde5cbfe5e2c1d919316cf4ab383858d/";
	private static final String BSC_ENDPOINT_WSS ="wss://holy-twilight-violet.bsc.quiknode.pro/9ccdc8c6748f92a972bc9c9c1b8b56de961c0fc6/";
	
	
	private static final long WEBCLIENT_TIMEOUT= 20l;
	private static final String ETH_GAS_PRICE_ORACLE ="https://www.etherchain.org/api";
	
	@SuppressWarnings("unused")
	private static final String ETH_GAS_STATION ="https://data-api.defipulse.com/api/v1/egs/api/ethgasAPI.json?api-key=2d249b5b77ce8b5d20fdd6a6c09a5ac3a954981252730a2e26dcfbc4a41a";
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	@Bean(name = "web3jClient")
	public Web3j web3J() throws Exception {
		return Web3j.build(webSocketService());
		//return Web3j.build(httpBSCService());
	}

	@Bean(name = "web3bscjClient")
	public Web3j web3bscjClient() throws Exception {
		return Web3j.build(webBSCSocketService());
		//return Web3j.build(httpETHService());
	}
	
//	//@Bean(name ="httpBSCService")
//	public HttpService httpBSCService() {
//		Dispatcher dispatcher = new Dispatcher(Executors.newFixedThreadPool(40));
//		 dispatcher.setMaxRequests(60);
//		 dispatcher.setMaxRequestsPerHost(5);
//		 
//		OkHttpClient client = new OkHttpClient.Builder()
//			      .readTimeout(6000, TimeUnit.SECONDS)
//			      .dispatcher(dispatcher)
//			      .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
//			      .connectTimeout(6000, TimeUnit.SECONDS)
//			        .writeTimeout(6000, TimeUnit.SECONDS)
//			      .addInterceptor(new DefaultContentTypeInterceptor())
//			      .retryOnConnectionFailure(true)
//			      .build();
//		return new HttpService(BSC_ENDPOINT);
//	}
//	
//	
//	//@Bean(name ="httpETHService")
//	public HttpService httpETHService() {
//		Dispatcher dispatcher = new Dispatcher(Executors.newFixedThreadPool(40));
//		 dispatcher.setMaxRequests(60);
//		 dispatcher.setMaxRequestsPerHost(5);
//		 
//		OkHttpClient client = new OkHttpClient.Builder()
//			      .readTimeout(6000, TimeUnit.SECONDS)
//			      .dispatcher(dispatcher)
//			      .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
//			      .connectTimeout(6000, TimeUnit.SECONDS)
//			        .writeTimeout(6000, TimeUnit.SECONDS)
//			      .addInterceptor(new DefaultContentTypeInterceptor())
//			      .retryOnConnectionFailure(true)
//			      .build();
//		return new HttpService(ETH_ENDPOINT);
//	}
//	
	
	
	
	@Bean(name = "webBSCSocketService")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public WebSocketService webBSCSocketService() throws Exception {
		WebSocketService webSocketService = new WebSocketService(new CustomWebSocketClient(parseURI(BSC_ENDPOINT_WSS)), false);
		try {
			webSocketService.connect();
		} catch (Exception e) {
			System.out.println("sleeping -->>");
			Thread.sleep(6000l);
			try {
				webSocketService.connect();
				System.out.println("reconnected to bsc!!!");
			} catch (ConnectException e1) {
				System.err.println("BSC node WSS failed, restart app -> eth gateway");
			}
		}
		return webSocketService;
	}

	@Bean(name = "webSocketService")
	public WebSocketService webSocketService() throws Exception {
		WebSocketService webSocketService = new WebSocketService(new CustomWebSocketClient(parseURI(ETH_ENDPOINT_WSS)), false);
		try {
			webSocketService.connect();
		} catch (Exception e) {
			System.out.println("sleeping -->>");
			Thread.sleep(6000l);
			try {
				webSocketService.connect();
				System.out.println("reconnected to ETH!!!");
			} catch (ConnectException e1) {
				System.err.println("ETH node WSS failed, restart app -> eth gateway");
			}
		}
		return webSocketService;
	}
	
	 @Bean
	 ReactiveMongoTransactionManager transactionManager(ReactiveMongoDatabaseFactory dbFactory) {
	     return new ReactiveMongoTransactionManager(dbFactory);
	 }
    
	private static URI parseURI(String serverUrl) {
        try {
            return new URI(serverUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Failed to parse URL: '%s'", serverUrl), e);
        }
    }
    
	@Bean(name= "pollingTransactionReceiptProcessor")
	public PollingTransactionReceiptProcessor pollingTransactionReceiptProcessor() throws Exception {
		return new PollingTransactionReceiptProcessor(web3J(), 4000, _40);
	}
	
	
	@Bean(name= "noOpProcessor")
	public NoOpProcessor noOpProcessor() throws Exception {
		return new NoOpProcessor(web3J());
	}
	
	
	@Bean(name = "web3jServiceClient")
	@Primary
	public Web3jServiceClient web3jServiceClient(@Qualifier("web3jClient") final Web3j web3j,
												 final ObjectMapper objectMapper) {
		return new Web3jServiceClient(web3j, restTemplate(), objectMapper);
	}

	
	@Bean(name = "web3jBscServiceClient")
	public Web3jServiceClient web3jBscServiceClient(@Qualifier("web3bscjClient") final Web3j web3j,
												 final ObjectMapper objectMapper) {
		return new Web3jServiceClient(web3j, restTemplate(), objectMapper);
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean(name = "gasWebClient")
	public WebClient getWebClient(@Qualifier("externalHttpClientCalls") HttpClient externalHttpClientCalls) {
		return WebClient.builder()
					    .baseUrl(ETH_GAS_PRICE_ORACLE)
					    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					    .clientConnector(new ReactorClientHttpConnector(externalHttpClientCalls))
					    .build();
	}

	@Bean(name = "externalHttpClientCalls")
	public HttpClient getHttpClient() {
		return HttpClient.create()
			    		 .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 50000)
			    		 .responseTimeout(Duration.ofMillis(50000))
			    		 .doOnConnected(conn -> 
	    		  						conn.addHandlerLast(new ReadTimeoutHandler(WEBCLIENT_TIMEOUT, TimeUnit.SECONDS))
	    		  							.addHandlerLast(new WriteTimeoutHandler(WEBCLIENT_TIMEOUT, TimeUnit.SECONDS)));
	}

	@Bean
	public MongoClient mongoClient() {
		return MongoClients.create();
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	
}
