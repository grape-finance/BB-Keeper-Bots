package com.betterbank.bots.service;

import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3jService;

/**
 * this is a place where invocations are configured and whatever.
 * just methods with cron scheduled invocations
 */
@Service
public class Groundskeeper {

    private final Web3jService web3jService;

    public Groundskeeper(Web3jService web3jService) {
        this.web3jService = web3jService;
    }
}
