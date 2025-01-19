package com.example.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class IgniteConfig {
    private static final Logger logger = LoggerFactory.getLogger(IgniteConfig.class);

    @Value("${ignite.discovery.tcp.remote-addresses}")
    private String remoteAddresses;

    @Value("${ignite.local-address}")
    private String localAddress;

    @Value("${ignite.default.cache.name:demoCache}")
    private String cacheName;

    @Bean
    public Ignite igniteInstance() {
        IgniteConfiguration cfg = new IgniteConfiguration();

        // Set Slf4j-based logging
        cfg.setGridLogger(new Slf4jLogger());

        cfg.setIgniteInstanceName("spring-app-ignite-instance");
        cfg.setClientMode(true); // Explicitly set client mode

        // Configure TCP Discovery
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
        discoverySpi.setLocalPort(47500);
        discoverySpi.setLocalPortRange(100);

        // Configure IP Finder
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setMulticastGroup("228.10.10.157");
        String[] addresses = remoteAddresses.split(",");
        logger.info("Configuring Ignite with remote addresses: {}", Arrays.toString(addresses));
        ipFinder.setAddresses(Arrays.asList(addresses));

        discoverySpi.setIpFinder(ipFinder);

        if (localAddress != null && !localAddress.isEmpty()) {
            logger.info("Setting local address for Ignite: {}", localAddress);
            discoverySpi.setLocalAddress(localAddress);
        }

        cfg.setDiscoverySpi(discoverySpi);

        // Memory configuration
        DataStorageConfiguration storageCfg = new DataStorageConfiguration();
        storageCfg.setDefaultDataRegionConfiguration(
                new DataRegionConfiguration()
                        .setName("Default_Region")
                        .setInitialSize(100L * 1024 * 1024)
                        .setMaxSize(500L * 1024 * 1024));

        cfg.setDataStorageConfiguration(storageCfg);

        // Configure default cache with logging
        CacheConfiguration<String, Object> cacheCfg = new CacheConfiguration<>(cacheName);
        cacheCfg.setBackups(1);
        cacheCfg.setStatisticsEnabled(true); // Enable statistics for monitoring
        cfg.setCacheConfiguration(cacheCfg);

        logger.info("Starting Ignite instance with configuration: {}", cfg);

        try {
            return Ignition.start(cfg);
        } catch (Exception e) {
            logger.error("Failed to start Ignite instance", e);
            throw e;
        }
    }

    @Bean
    public IgniteCache<String, Object> igniteCache(Ignite ignite) {
        IgniteCache<String, Object> cache = ignite.getOrCreateCache(cacheName);
        logger.info("Created/Retrieved cache '{}' successfully", cacheName);
        return cache;
    }
}