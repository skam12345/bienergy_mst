package bienergy_mst.bienergy_mst.config;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.boot.context.properties.ConfigurationProperties;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "netty")
public class NettyProperties {
    @NotNull
    @Size(min=7888, max=7888)
    private int tcpPort;

    @NotNull
    @Min(1)
    private int bossCount;

    @NotNull
    @Min(2)
    private int workerCount;
}
