package com.example.kotlingateway.filter

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.core.Ordered
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class LoggingFilter : AbstractGatewayFilterFactory<LoggingFilter.Config>(Config::class.java) {

    private val log = LoggerFactory.getLogger(LoggingFilter::class.java)

    override fun apply(config: Config): GatewayFilter {
        var filter: GatewayFilter = OrderedGatewayFilter({ exchange, chain ->
            val request: ServerHttpRequest = exchange.request
            val response: ServerHttpResponse = exchange.response
            log.info("Logging Filter baseMessage: {}", config.baseMessage)

            if (config.preLogger) {
                log.info("Logging PRE Filter: request id -> {}", request.id)
            }

            chain.filter(exchange).then(Mono.fromRunnable {
                if (config.postLogger) {
                    log.info("Logging POST Filter: response code -> {}", response.statusCode)
                }
            })
        }, Ordered.LOWEST_PRECEDENCE)  // 우선 순위가 가장 높아서 Global 보다 먼저 실행됨
        return filter
    }

    data class Config(
        var baseMessage: String? = null,
        var preLogger: Boolean = false,
        var postLogger: Boolean = false
    )

}