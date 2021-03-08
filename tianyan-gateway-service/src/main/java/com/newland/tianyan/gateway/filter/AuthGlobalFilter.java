package com.newland.tianyan.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nimbusds.jose.JWSObject;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;


/**
 * @Author: huangJunJie  2021-03-04 16:25
 */
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null || token.isEmpty()) {
            return chain.filter(exchange);
        }
        try {
            String realToken = token.replace("Bearer ", "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            String info = jwsObject.getPayload().toString();
            System.out.println(info);
            JSONObject jsonObject = JSON.parseObject(info);
            String grant_type = jsonObject.getString("grant_type");
            if (grant_type.equals("client_credentials")) {
                ServerHttpRequest request = exchange.getRequest().mutate().header("app_id", jsonObject.getString("appId"))
                        .header("account", jsonObject.getString("account"))
                        .build();
                exchange = exchange.mutate().request(request).build();
            } else if (grant_type.equals("password")) {
                ServerHttpRequest request = exchange.getRequest().mutate().header("account", jsonObject.getString("user_name")).build();
                exchange = exchange.mutate().request(request).build();

//                ServerHttpRequest request = exchange.getRequest();
//                Flux<DataBuffer> body = request.getBody();
//                System.out.println("aa");
//                StringBuilder sb = new StringBuilder();
//                body.subscribe(buffer -> {
//                    byte[] bytes = new byte[buffer.readableByteCount()];
//                    buffer.read(bytes);
//                    DataBufferUtils.release(buffer);
//                    String bodyString = new String(bytes, StandardCharsets.UTF_8);
//                    sb.append(bodyString);
//                });
//                System.out.println(sb);

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -2000;
    }
}
