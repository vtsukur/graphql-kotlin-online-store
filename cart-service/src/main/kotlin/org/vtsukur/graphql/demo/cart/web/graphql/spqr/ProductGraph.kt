package org.vtsukur.graphql.demo.cart.web.graphql.spqr

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.jackson.jacksonDeserializerOf
import graphql.execution.batched.Batched
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLEnvironment
import io.leangen.graphql.annotations.GraphQLQuery
import org.springframework.stereotype.Component
import org.vtsukur.graphql.demo.cart.domain.Item
import org.vtsukur.graphql.demo.product.api.Product
import org.vtsukur.graphql.demo.product.api.Products

@Component
class ProductGraph {

    @GraphQLQuery(name = "product")
    @Batched
    fun products(@GraphQLContext items: List<Item>,
                 @GraphQLEnvironment fields: Set<String>): List<Product> =
            "http://localhost.charlesproxy.com:9090/products".httpGet(listOf(
                    "ids" to items.joinToString(",") { it.productId },
                    "include" to fields.joinToString(","))
            ).responseObject(jacksonDeserializerOf<Products>())
                    .third.get()
                    .products

    @GraphQLQuery(name = "images")
    fun images(@GraphQLContext product: Product,
               @GraphQLArgument(name = "limit", defaultValue = "0") limit: Int): List<String> =
            product.images.subList(0,
                    if (limit > 0) Math.min(limit, product.images.size)
                    else product.images.size)

}
