package com.example.fetchbuddy

import io.github.cdimascio.dotenv.dotenv
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.DriverManager

// a data class to hold the parts & products data from the query to output as an array of valid json
data class Part(
    val punctuatedPartNumber: String,
    val partDescr: String,
    val productId: Int,
    val originalRetailPrice: Float,
    val brandName: String,
    val productName: String,
    val categoryName: String,
    val pngUrl: String
    )

@RestController
@RequestMapping("api/v1/parts")
class GetController {
    @GetMapping
    fun returnPartsData(): Any
    {
        // get db credentials from env variables
        val dotenv = dotenv()
        val envUrl = dotenv.get("DB_URL") ?: "missing url"
        val envUid = dotenv.get("DB_USERNAME") ?: "missing username"
        val envPwd = dotenv.get("DB_PASSWORD") ?: "missing password"
        val connection = DriverManager.getConnection(envUrl, envUid, envPwd)
        if (connection.isValid(0) ) {
            val query = connection.prepareStatement("SELECT x.\"punctuatedPartNumber\", x.\"partDescr\", x.\"productId\", x.\"originalRetailPrice\", x.\"brandName\",y.\"productName\", y.\"categoryName\", x.\"pngUrl\" FROM public.parts x left join public.products y on x.\"productId\" = y.\"productId\" ")
            val result = query.executeQuery()
            val parts = mutableListOf<Part>()
            while (result.next()) {
                val punctuatedPartNumber = result.getString("punctuatedPartNumber")
                val partDescr = result.getString("partDescr")
                val productId = result.getInt("productId")
                val originalRetailPrice = result.getFloat("originalRetailPrice")
                val brandName = result.getString("brandName")
                val productName = result.getString("productName")
                val categoryName = result.getString("categoryName")
                val pngUrl = result.getString("pngUrl")
                parts.add(
                    Part(
                        punctuatedPartNumber,
                        partDescr,
                        productId,
                        originalRetailPrice,
                        brandName,
                        productName,
                        categoryName,
                        pngUrl
                    )
                )
            }
            println("Successfully pulled data as array of json objects")
            return parts // return array of json objects from parts & products data
        }
        println("Unable to connect, ensure database url is correct and all dependencies are installed")
        return 0 // handle error etc
    }
    }


