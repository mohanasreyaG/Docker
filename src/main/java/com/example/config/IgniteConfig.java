package com.example.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class IgniteConfig {

    @Value("${ignite.discovery.tcp.remote-addresses}")
    private String remoteAddresses;

    @Value("${ignite.local-address}")
    private String localAddress;

    @Value("${ignite.client-mode:true}")
    private boolean clientMode;

    @Bean
    public Ignite igniteInstance() {
        IgniteConfiguration cfg = new IgniteConfiguration();

        // Set instance name
        cfg.setIgniteInstanceName("demoCache");

        // Configure TCP Discovery SPI
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();

        // Configure VM IP finder
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Arrays.asList(remoteAddresses.split(",")));
        discoverySpi.setIpFinder(ipFinder);

        // Set network configuration
        discoverySpi.setLocalAddress(localAddress);
        discoverySpi.setLocalPort(47500);
        discoverySpi.setLocalPortRange(10);

        // Set discovery SPI
        cfg.setDiscoverySpi(discoverySpi);

        // Set client mode
        cfg.setClientMode(clientMode);

        // Memory configuration
        cfg.setSystemThreadPoolSize(4);
        cfg.setPublicThreadPoolSize(4);

        // Reduce default memory allocation
        cfg.setDataStorageConfiguration(new DataStorageConfiguration()
                .setDefaultDataRegionConfiguration(new DataRegionConfiguration()
                        .setInitialSize(100L * 1024 * 1024)     // 100MB initial
                        .setMaxSize(500L * 1024 * 1024)));      // 500MB max

        return Ignition.start(cfg);
    }

    @Bean
    public IgniteCache<String, Object> demoCache(Ignite ignite) {
        return ignite.getOrCreateCache("demoCache");
    }
}