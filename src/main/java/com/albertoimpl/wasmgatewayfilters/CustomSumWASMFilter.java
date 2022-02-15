package com.albertoimpl.wasmgatewayfilters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class CustomSumWASMFilter extends AbstractGatewayFilterFactory<CustomSumWASMFilter.Config> {

    private static final Logger LOG =
            LoggerFactory.getLogger(CustomSumWASMFilter.class);

    private SumWASMService sumWASMService;

    public CustomSumWASMFilter(SumWASMService sumWASMService) {
        super(Config.class);
        this.sumWASMService = sumWASMService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            LOG.info("Summing: {} + {}", config.x, config.y);
            int result = sum(config.x, config.y);
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                exchange.getResponse()
                        .getHeaders()
                        .add("X-CustomSum", String.valueOf(result));
            }));
        };
    }

    private int sum(int x, int y) {
        return sumWASMService.sum(x, y);
    }

    public static class Config {
        private int x;
        private int y;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("x", "y");
    }

    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.DEFAULT;
    }
}
