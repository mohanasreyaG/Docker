package com.example.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
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

    @Value("${ignite.default.cache.name:demoCache}")
    private String cacheName;

    @Bean
    public Ignite igniteInstance() {
        IgniteConfiguration cfg = new IgniteConfiguration();

        // Set instance name
        cfg.setIgniteInstanceName("ignite-instance");

        // Configure TCP Discovery with better timeout settings
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
        discoverySpi.setLocalPort(47500);
        discoverySpi.setLocalPortRange(100);
        discoverySpi.setJoinTimeout(30000);
        discoverySpi.setNetworkTimeout(15000);
        discoverySpi.setSocketTimeout(5000);
        discoverySpi.setAckTimeout(5000);

        // Use Multicast IP Finder for better discovery in Docker
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setMulticastGroup("228.10.10.157");
        ipFinder.setAddresses(Arrays.asList(remoteAddresses.split(",")));
        discoverySpi.setIpFinder(ipFinder);

        // Set local address if specified
        if (localAddress != null && !localAddress.isEmpty()) {
            discoverySpi.setLocalAddress(localAddress);
        }

        cfg.setDiscoverySpi(discoverySpi);

        // Memory configuration
        DataStorageConfiguration storageCfg = new DataStorageConfiguration();
        storageCfg.setDefaultDataRegionConfiguration(
                new DataRegionConfiguration()
                        .setName("Default_Region")
                        .setInitialSize(100L * 1024 * 1024)
                        .setMaxSize(500L * 1024 * 1024)
                        .setPersistenceEnabled(false));

        cfg.setDataStorageConfiguration(storageCfg);

        // Increase system thread pool for better stability
        cfg.setSystemThreadPoolSize(16);
        cfg.setPublicThreadPoolSize(16);

        // Configure default cache
        CacheConfiguration<String, Object> cacheCfg = new CacheConfiguration<>(cacheName);
        cacheCfg.setBackups(1);
        cfg.setCacheConfiguration(cacheCfg);

        return Ignition.start(cfg);
    }

    @Bean
    public IgniteCache<String, Object> igniteCache(Ignite ignite) {
        return ignite.getOrCreateCache(cacheName);
    }
}














//package com.example.config;
//
//import org.apache.ignite.Ignite;
//import org.apache.ignite.IgniteCache;
//import org.apache.ignite.Ignition;
//import org.apache.ignite.configuration.CacheConfiguration;
//import org.apache.ignite.configuration.DataRegionConfiguration;
//import org.apache.ignite.configuration.DataStorageConfiguration;
//import org.apache.ignite.configuration.IgniteConfiguration;
//import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
//import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Arrays;
//
//@Configuration
//public class IgniteConfig {
//
//    @Value("${ignite.discovery.tcp.remote-addresses}")
//    private String remoteAddresses;
//
//    @Value("${ignite.local-address}")
//    private String localAddress;
//
//    @Value("${ignite.default.cache.name:demoCache}")
//    private String cacheName;
//
//    @Bean
//    public Ignite igniteInstance() {
//        IgniteConfiguration cfg = new IgniteConfiguration();
//
//        // Set instance name
//        cfg.setIgniteInstanceName("ignite-instance");
//
//        // Configure TCP Discovery SPI
//        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
//
//        // Configure VM IP finder
//        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
//        ipFinder.setAddresses(Arrays.asList(remoteAddresses.split(",")));
//        discoverySpi.setIpFinder(ipFinder);
//
//        // Set network configuration
//        discoverySpi.setLocalAddress(localAddress);
//        discoverySpi.setLocalPort(47500);
//        discoverySpi.setLocalPortRange(20); // Wider range for flexibility
//        cfg.setDiscoverySpi(discoverySpi);
//
//        // Memory and persistence configuration
//        DataStorageConfiguration storageCfg = new DataStorageConfiguration();
//
//        storageCfg.setDefaultDataRegionConfiguration(
//                new DataRegionConfiguration()
//                        .setName("Default_Region")
//                        .setInitialSize(100L * 1024 * 1024) // 100MB
//                        .setMaxSize(500L * 1024 * 1024)     // 500MB
//                        .setPersistenceEnabled(false));    // Set to true for persistence
//
//        cfg.setDataStorageConfiguration(storageCfg);
//
//        // Thread pool configuration
//        cfg.setSystemThreadPoolSize(8);
//        cfg.setPublicThreadPoolSize(8);
//
//        // Enable metrics for monitoring
//        cfg.setMetricsLogFrequency(60000); // Log metrics every 60 seconds
//
//        // Configure default cache
//        CacheConfiguration<String, Object> cacheCfg = new CacheConfiguration<>(cacheName);
//        cacheCfg.setBackups(1); // Set number of backups for fault tolerance
//        cfg.setCacheConfiguration(cacheCfg);
//
//        // Start Ignite instance
//        return Ignition.start(cfg);
//    }
//
//    @Bean
//    public IgniteCache<String, Object> igniteCache(Ignite ignite) {
//        return ignite.getOrCreateCache(cacheName);
//    }
//}



//package com.example.config;
//
//import org.apache.ignite.Ignite;
//import org.apache.ignite.IgniteCache;
//import org.apache.ignite.Ignition;
//import org.apache.ignite.configuration.DataRegionConfiguration;
//import org.apache.ignite.configuration.DataStorageConfiguration;
//import org.apache.ignite.configuration.IgniteConfiguration;
//import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
//import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Arrays;
//
//@Configuration
//public class IgniteConfig {
//
//    @Value("${ignite.discovery.tcp.remote-addresses}")
//    private String remoteAddresses;
//
//    @Value("${ignite.local-address}")
//    private String localAddress;
//
//    @Value("${ignite.client-mode:true}")
//    private boolean clientMode;
//
//    @Bean
//    public Ignite igniteInstance() {
//        IgniteConfiguration cfg = new IgniteConfiguration();
//
//        // Set instance name
//        cfg.setIgniteInstanceName("demoCache");
//
//        // Configure TCP Discovery SPI
//        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
//
//        // Configure VM IP finder
//        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
//        ipFinder.setAddresses(Arrays.asList(remoteAddresses.split(",")));
//        discoverySpi.setIpFinder(ipFinder);
//
//        // Set network configuration
//        discoverySpi.setLocalAddress(localAddress);
//        discoverySpi.setLocalPort(47500);
//        discoverySpi.setLocalPortRange(10);
//
//        // Set discovery SPI
//        cfg.setDiscoverySpi(discoverySpi);
//
//        // Set client mode
//        cfg.setClientMode(clientMode);
//
//        // Memory configuration
//        cfg.setSystemThreadPoolSize(4);
//        cfg.setPublicThreadPoolSize(4);
//
//        // Reduce default memory allocation
//        cfg.setDataStorageConfiguration(new DataStorageConfiguration()
//                .setDefaultDataRegionConfiguration(new DataRegionConfiguration()
//                        .setInitialSize(100L * 1024 * 1024)     // 100MB initial
//                        .setMaxSize(500L * 1024 * 1024)));      // 500MB max
//
//        return Ignition.start(cfg);
//    }
//
//    @Bean
//    public IgniteCache<String, Object> demoCache(Ignite ignite) {
//        return ignite.getOrCreateCache("demoCache");
//    }
//}