package bienergy_mst.bienergy_mst.config;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import bienergy_mst.bienergy_mst.mysql.MysqlConnection;
import bienergy_mst.bienergy_mst.mysql.QueryExcuteClass;
import bienergy_mst.bienergy_mst.netty.handler.SimpleChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(NettyProperties.class)
public class NettyConfiguration {
    @Value("${server.host}")
    private String host;
    @Value("${server.port}")
    private int port;
    @Value("${server.netty.boss-count}")
    private int bossCount;
     @Value("${server.netty.worker-count}")
    private int workerCount;
    @Bean(name = "serverBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup())
            .channel(NioServerSocketChannel.class)
            .childHandler(new SimpleChannelInitializer());

        return b;
    }

    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(bossCount);
    }

    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(workerCount);
    }

    @Bean
    public InetSocketAddress inetSocketAddress() {
        return new InetSocketAddress(host, port);
    }
}
