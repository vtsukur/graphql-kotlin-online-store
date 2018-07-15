package org.vtsukur.graphql.demo.cart.web.graphql.spqr

import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.analysis.MaxQueryComplexityInstrumentation
import graphql.analysis.MaxQueryDepthInstrumentation
import graphql.execution.batched.BatchedExecutionStrategy
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.execution.instrumentation.Instrumentation
import io.leangen.graphql.GraphQLSchemaGenerator
import io.leangen.graphql.metadata.strategy.value.jackson.JacksonValueMapperFactory
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class GraphQLController(cartGraph: CartGraph, productQuery: ProductGraph) {

    private val graphQL: GraphQL

    init {
        val schema = GraphQLSchemaGenerator()
                .withOperationsFromSingleton(cartGraph)
                .withOperationsFromSingleton(productQuery)
                .withValueMapperFactory(JacksonValueMapperFactory())
                .generate()
        graphQL = GraphQL.newGraphQL(schema)
                .queryExecutionStrategy(BatchedExecutionStrategy())
                .instrumentation(ChainedInstrumentation(Arrays.asList<Instrumentation>(
                        MaxQueryComplexityInstrumentation(200),
                        MaxQueryDepthInstrumentation(20)
                )))
                .build()
    }

    @PostMapping(value = ["/graphql"], consumes = [APPLICATION_JSON_UTF8_VALUE], produces = [APPLICATION_JSON_UTF8_VALUE])
    @ResponseBody
    fun execute(@RequestBody request: Map<String, Any>): ExecutionResult =
            graphQL.execute(ExecutionInput.newExecutionInput()
                    .query(request["query"] as String)
                    .operationName((request["operationName"] ?: "") as String)
                    .build())

}
