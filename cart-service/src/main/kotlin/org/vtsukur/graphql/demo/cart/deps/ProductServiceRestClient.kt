package org.vtsukur.graphql.demo.cart.deps

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.jackson.jacksonDeserializerOf
import org.vtsukur.graphql.demo.product.api.Product

class ProductServiceRestClient(private val baseUrl: String) {

    fun fetchProduct(productId: String): Product = "$baseUrl/products/$productId".httpGet()
            .responseObject(jacksonDeserializerOf<Product>())
            .third.get()

}
