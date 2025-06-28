package com.betterbank.bots.service;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.generated.contracts.IMintRedeemer;
import org.web3j.generated.contracts.ITreasury;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DynamicGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * this is a place where invocations are configured and whatever.
 * just methods with cron scheduled invocations
 */
@Service
public class Groundskeeper {

    private final TransactionManager txManager;
    private final Web3j web3j;
    private final Credentials credentials;
    private final long chainId;
    private final String mintRedeemer;
    private final List<String> treasuries;
    private final BigInteger gasLimit;
    private final BigInteger bribe;
    Logger logger = LoggerFactory.getLogger(Groundskeeper.class);


    private final DynamicGasProvider dynamicGasProvider;

    private final List<ITreasury> iTreasuries;
    private final IMintRedeemer iMintRedeemer;

    public Groundskeeper(Web3j web3j,
                         @Value("${groundskeeper.mintRedeemer}") String mintRedeemer,
                         @Value("${groundskeeper.treasuries}") List<String> treasuries,
                         @Value("${groundskeeper.executor}") String executor,
                         @Value("${web3j.node.chainId}") long chainId,
                         @Value("${groundskeeper.gasLimit}") int gasLimit,
                         @Value("${groundskeeper.bribe}") int bribe) {

        this.credentials = Credentials.create(executor);
        this.dynamicGasProvider = new DynamicGasProvider(web3j);
        this.chainId = chainId;
        this.txManager = new RawTransactionManager(web3j, credentials, this.chainId);

        this.mintRedeemer = mintRedeemer;
        iMintRedeemer = IMintRedeemer.load(this.mintRedeemer, web3j, credentials, dynamicGasProvider);
        this.treasuries = treasuries;
        iTreasuries = this.treasuries.stream().map(t -> ITreasury.load(t, web3j, credentials, dynamicGasProvider)).toList();
        this.web3j = web3j;
        this.gasLimit = BigInteger.valueOf(gasLimit);
        this.bribe = BigInteger.valueOf(bribe);
    }


    @Scheduled(cron = "${groundskeeper.updateEsteemRate.cron}")
    public void updateEsteemRate() {
        logger.info("updating esteem rate");

        try {
            BigInteger gasPrice = getGasPrice();

            String encodedFunctionCall = iMintRedeemer.updateEsteemRate().encodeFunctionCall();


            EthSendTransaction sent = txManager.sendEIP1559Transaction(
                    chainId,
                    gasPrice,
                    gasPrice,
                    gasLimit,
                    mintRedeemer,
                    encodedFunctionCall,
                    null
            );

            logger.info("update esteem rate tx: {}", sent.getTransactionHash());
        } catch (Exception e) {
            logger.error("error while updating esteem rate", e);
        }
    }

    @Scheduled(cron = "${groundskeeper.allocateSeignorage.cron}")
    public void allocateSeignorage() throws IOException {

        BigInteger gasPrice = getGasPrice();

        iTreasuries.forEach(t -> {
            try {

                logger.info("allocating seignorage to {}", t.getContractAddress());

                String encodedFunctionCall = t.allocateSeigniorage().encodeFunctionCall();
                EthSendTransaction sent = txManager.sendEIP1559Transaction(
                        chainId,
                        gasPrice,
                        gasPrice,
                        gasLimit,
                        t.getContractAddress(),
                        encodedFunctionCall,
                        null
                );
                logger.info("tx sent {}", sent.getTransactionHash());
            } catch (Exception e) {
                logger.error("error while allocating seignorage", e);
            }
        });
    }

    @NotNull
    private BigInteger getGasPrice() throws IOException {
        EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock();
        BigInteger gasPrice = block.getBaseFeePerGas().multiply(BigInteger.valueOf(100).add(bribe)).divide(BigInteger.valueOf(100));
        return gasPrice;
    }
}
