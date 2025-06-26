package com.betterbank.bots.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.JsonRpc2_0Admin;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.utils.Async;

import java.net.ConnectException;

@Configuration
public class Web3jConfiguration {
    Logger logger = LoggerFactory.getLogger(Web3jConfiguration.class);

    @Value("${web3j.node.url}")
    private String nodeUrl;
    @Value("${web3j.node.pollingInterval}")
    private long pollingInterval;

    @Value("${web3j.node.readerKey}")
    private String readerKey;

    @Bean
    public Web3j web3j(WebSocketService webSocketService) {
        logger.info("connecting to websocket: {}", nodeUrl);
        return Web3j.build(webSocketService, pollingInterval, Async.defaultExecutorService());
    }


    /**
     * this bean is providing administrative API, giving access to TX pool and such stuff
     * @param service
     * @return
     */
    @Bean
    public JsonRpc2_0Admin web3jAdmin(WebSocketService service) {
        return new JsonRpc2_0Admin(service);
    }

    /**
     * basioc functionality acess - like  contract method invocations
     * @return
     */
    @Bean
    public WebSocketService getWebSocketService() {
        WebSocketService webSocketService = new WebSocketService(nodeUrl, false);
        try {
            webSocketService.connect(s -> {
                    },
                    t -> {
                        logger.error("websocket error", t);
                    },
                    () -> {

                        logger.info("websocket closed.  reconnecting.");
                        // this shall be done in a new thread !!!!!!
                        new Thread(() -> {
                            try {
                                webSocketService.connect();
                            } catch (ConnectException e) {
                                logger.info("websocket reconnect failed: {}", e.getMessage());
                                throw new RuntimeException(e);
                            }
                        });
                    });
        } catch (ConnectException e) {
            logger.error("error while connecting", e);
            throw new RuntimeException(e);
        }

        return webSocketService;
    }


    /**
     * credentials to read from the contracts. needs to be a valid key,
     * not secure not valuable
     *
     * @return
     */
    @Bean
    public Credentials readCredentials() {
        return Credentials.create(readerKey);
    }


}
