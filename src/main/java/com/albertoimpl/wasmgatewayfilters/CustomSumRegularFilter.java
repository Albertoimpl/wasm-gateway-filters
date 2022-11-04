package com.albertoimpl.wasmgatewayfilters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomSumRegularFilter extends AbstractGatewayFilterFactory<Object> {

    private static final Logger LOG =
            LoggerFactory.getLogger(CustomSumRegularFilter.class);

    private final SumRegularService sumRegularService;

    public CustomSumRegularFilter(SumRegularService sumRegularService) {
        this.sumRegularService = sumRegularService;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String x = exchange.getRequest().getHeaders().get("X-CustomSum-x").get(0);
            String y = exchange.getRequest().getHeaders().get("X-CustomSum-y").get(0);
            LOG.info("Summing: {} + {}", x, y);
            int result = sum(Integer.parseInt(x), Integer.parseInt(y));
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                exchange.getResponse()
                        .getHeaders()
                        .add("X-CustomSum", String.valueOf(result));
            }));
        };
    }

    private int sum(int x, int y) {
        return sumRegularService.customSum(x, y);
    }

}
