package jr.brian.issarecipeapp.model.remote

import com.google.gson.JsonParser
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

class OpenAIImageClient(private val apiKey: String) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()

    data class ImageGenerationRequest(
        val model: String,
        val prompt: String,
        val size: String,
        val quality: String,
        val n: Int
    )

    private fun extractImageUrl(jsonResponse: String): String {
        val rootObject = JsonParser.parseString(jsonResponse).asJsonObject
        if (rootObject.has("data")
            && rootObject.getAsJsonArray("data").size() > 0
        ) {
            val dataArray = rootObject.getAsJsonArray("data")
            val firstDataObject = dataArray[0].asJsonObject
            if (firstDataObject.has("url")) {
                return firstDataObject.getAsJsonPrimitive("url").asString
            }
        }
        return "Empty Image Url"
    }

    fun generateImageUrl(request: ImageGenerationRequest): String {
        val json = """{
            "model": "${request.model}",
            "prompt": "${request.prompt}",
            "size": "${request.size}",
            "quality": "${request.quality}",
            "n": ${request.n}
        }"""

        val body: RequestBody = json.toRequestBody(mediaType)
        val httpRequest: Request = Request.Builder()
            .url("https://api.openai.com/v1/images/generations")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        val response = client.newCall(httpRequest).execute()
        val jsonResponse = response.body!!.string()
        val rootObject = JsonParser.parseString(jsonResponse).asJsonObject

        if (response.isSuccessful) {
            return extractImageUrl(rootObject.toString())
        } else {
            throw IllegalArgumentException(rootObject["error"].asJsonObject["message"].asString)
        }
    }
}