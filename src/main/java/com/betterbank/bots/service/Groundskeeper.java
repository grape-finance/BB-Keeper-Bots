package com.betterbank.bots.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3jService;

import java.util.List;

/**
 * this is a place where invocations are configured and whatever.
 * just methods with cron scheduled invocations
 */
@Service
public class Groundskeeper {

    private final Web3jService web3jService;

    private final String mintRedeemer;
    private final List<String>  treasuries;
    public Groundskeeper(Web3jService web3jService,
                         @Value("${groundskeeper.mintRedeemer}") String mintRedeemer,
                         @Value("${groundskeeper.treasuries}") List<String> treasuries) {
        this.web3jService = web3jService;
        this.mintRedeemer = mintRedeemer;
        this.treasuries = treasuries;
    }
}
