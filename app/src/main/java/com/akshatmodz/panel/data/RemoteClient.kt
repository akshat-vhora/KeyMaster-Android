package com.akshatmodz.panel.data

import com.akshatmodz.panel.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job

object RemoteClient {

    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    private val mediaType = "application/json".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val activeCalls = ConcurrentHashMap<String, Call>()

    private fun buildRequest(
        path: String,
        method: String = "GET",
        body: String? = null,
        token: String? = null
    ): Request {
        val url = "${BuildConfig.SUPABASE_URL}$path"
        val req = Request.Builder().url(url).method(method, body?.toRequestBody(mediaType))
        req.addHeader("Content-Type", "application/json")
        req.addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
        if (token != null) {
            req.addHeader("Authorization", "Bearer $token")
        }
        return req.build()
    }

    private suspend fun execute(req: Request, tag: String = ""): String = withContext(Dispatchers.IO) {
        val call = client.newCall(req)
        if (tag.isNotEmpty()) {
            activeCalls[tag]?.cancel()
            activeCalls[tag] = call
        }
        coroutineContext[Job]?.invokeOnCompletion { cause ->
            if (cause is CancellationException) call.cancel()
        }
        try {
            val resp = call.execute()
            val body = resp.body?.string() ?: ""
            if (!resp.isSuccessful) {
                val message = parseErrorMessage(body, resp.code)
                throw ApiException(resp.code, message)
            }
            body
        } finally {
            if (tag.isNotEmpty() && activeCalls[tag] === call) {
                activeCalls.remove(tag)
            }
        }
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val body = json.encodeToString(LoginRequest.serializer(), LoginRequest(email, password))
        val req = buildRequest("/auth/v1/token?grant_type=password", "POST", body)
        return json.decodeFromString(execute(req))
    }

    suspend fun signup(email: String, password: String, username: String, email_id: String = "", referral: String = ""): String {
        val body = json.encodeToString(UsersCreateRequest.serializer(), UsersCreateRequest(email, password, username, email_id, referral))
        val req = buildRequest("/functions/v1/users-create", "POST", body)
        return execute(req)
    }

    suspend fun getProfile(token: String): UserProfile {
        val req = buildRequest("/functions/v1/users-profile", "GET", token = token)
        return json.decodeFromString(execute(req))
    }

    suspend fun listKeys(token: String, search: String = "", game: String = "", page: Int = 1, limit: Int = 50): KeyListResponse {
        val params = mutableListOf("page=$page", "limit=$limit")
        if (search.isNotEmpty()) params.add("search=$search")
        if (game.isNotEmpty()) params.add("game=$game")
        val req = buildRequest("/functions/v1/keys-list?${params.joinToString("&")}", "GET", token = token)
        return json.decodeFromString(execute(req))
    }

    suspend fun generateKey(token: String, game: String, duration: Int, maxDevices: Int, customKey: String? = null): KeyGenerateResponse {
        val body = json.encodeToString(KeyGenerateRequest.serializer(), KeyGenerateRequest(game, duration, maxDevices, customKey))
        val req = buildRequest("/functions/v1/keys-generate", "POST", body, token)
        return json.decodeFromString(execute(req))
    }

    suspend fun editKey(token: String, idKeys: Int, game: String? = null, userKey: String? = null, duration: Int? = null, maxDevices: Int? = null, expiredDate: String? = null, devices: String? = null) {
        val body = json.encodeToString(KeyEditRequest.serializer(), KeyEditRequest(idKeys, game, userKey, duration, maxDevices, expiredDate, devices))
        val req = buildRequest("/functions/v1/keys-edit", "POST", body, token)
        execute(req)
    }

    suspend fun deleteKey(token: String, idKeys: Int) {
        val body = json.encodeToString(DeleteKeyRequest.serializer(), DeleteKeyRequest(idKeys))
        val req = buildRequest("/functions/v1/keys-delete", "POST", body, token)
        execute(req, "deleteKey")
    }

    suspend fun bulkKeys(token: String, action: String, idKeys: Int? = null): BulkResponse {
        val body = json.encodeToString(BulkRequest.serializer(), BulkRequest(action, idKeys))
        val req = buildRequest("/functions/v1/keys-bulk", "POST", body, token)
        return json.decodeFromString(execute(req))
    }

    suspend fun adminUsers(token: String, search: String = "", page: Int = 1, limit: Int = 50, levelFilter: String? = null): AdminUsersResponse {
        val params = mutableListOf("page=$page", "limit=$limit")
        if (search.isNotEmpty()) params.add("search=$search")
        if (levelFilter != null) params.add("level_filter=$levelFilter")
        val req = buildRequest("/functions/v1/admin-users?${params.joinToString("&")}", "GET", token = token)
        return json.decodeFromString(execute(req))
    }

    suspend fun adminEditUser(token: String, idUsers: Int, level: Int? = null, saldo: Int? = null, status: Int? = null) {
        val body = json.encodeToString(AdminUserEditRequest.serializer(), AdminUserEditRequest(idUsers, level = level, saldo = saldo, status = status))
        val req = buildRequest("/functions/v1/admin-users-edit", "POST", body, token)
        execute(req)
    }

    suspend fun createReferral(token: String, setSaldo: Int): ReferralItem {
        val body = json.encodeToString(ReferralCreateRequest.serializer(), ReferralCreateRequest(setSaldo))
        val req = buildRequest("/functions/v1/referral-create", "POST", body, token)
        return json.decodeFromString(execute(req))
    }

    suspend fun listReferrals(token: String, page: Int = 1, limit: Int = 50): ReferralListResponse {
        val req = buildRequest("/functions/v1/referral-list?page=$page&limit=$limit", "GET", token = token)
        return json.decodeFromString(execute(req))
    }

    suspend fun deleteUser(token: String, userId: Int) {
        val body = json.encodeToString(DeleteUserRequest.serializer(), DeleteUserRequest(userId))
        val req = buildRequest("/functions/v1/admin-users-delete", "POST", body, token)
        execute(req, "deleteUser")
    }

    suspend fun changePassword(token: String, currentPassword: String, newPassword: String) {
        val body = json.encodeToString(PasswordChangeRequest.serializer(), PasswordChangeRequest(currentPassword, newPassword))
        val req = buildRequest("/functions/v1/users-password-update", "POST", body, token)
        execute(req, "changePassword")
    }

    suspend fun deleteGame(token: String, code: String) {
        val body = json.encodeToString(GameDeleteRequest.serializer(), GameDeleteRequest(code))
        val req = buildRequest("/functions/v1/games-delete", "POST", body, token)
        execute(req)
    }

    suspend fun listGames(token: String, activeOnly: Boolean = false): List<GameItem> {
        val params = if (activeOnly) "?active_only=true" else ""
        val req = buildRequest("/functions/v1/games-list$params", "GET", token = token)
        return json.decodeFromString<GameListResponse>(execute(req)).data
    }

    suspend fun createGame(token: String, code: String, displayName: String, libName: String = "") {
        val body = json.encodeToString(GameCreateRequest.serializer(), GameCreateRequest(code, displayName, libName))
        val req = buildRequest("/functions/v1/games-create", "POST", body, token)
        execute(req)
    }

    suspend fun updateGame(token: String, code: String, displayName: String? = null, libName: String? = null, active: Boolean? = null) {
        val body = json.encodeToString(GameUpdateRequest.serializer(), GameUpdateRequest(code, displayName, libName, active))
        val req = buildRequest("/functions/v1/games-update", "POST", body, token)
        execute(req)
    }

    suspend fun checkLibExists(token: String, libName: String): Boolean {
        val clean = libName.removeSuffix(".so")
        val url = "${BuildConfig.SUPABASE_URL}/storage/v1/object/AMods-Libs/${clean}.so"
        val req = Request.Builder().url(url).head()
            .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer $token")
            .build()
        return withContext(Dispatchers.IO) {
            val resp = client.newCall(req).execute()
            resp.isSuccessful
        }
    }

    suspend fun uploadLib(token: String, libName: String, fileBytes: ByteArray, onProgress: (Float) -> Unit) {
        val clean = libName.removeSuffix(".so")
        val url = "${BuildConfig.SUPABASE_URL}/storage/v1/object/AMods-Libs/${clean}.so"
        val octetStream = "application/octet-stream".toMediaType()
        val total = fileBytes.size
        val progressBody = object : RequestBody() {
            override fun contentType() = octetStream
            override fun contentLength() = total.toLong()
            override fun writeTo(sink: BufferedSink) {
                var written = 0
                while (written < total) {
                    val chunk = minOf(4096, total - written)
                    sink.write(fileBytes, written, chunk)
                    written += chunk
                    onProgress(written.toFloat() / total.toFloat())
                }
            }
        }
        val req = Request.Builder().url(url)
            .method("POST", progressBody)
            .addHeader("Content-Type", "application/octet-stream")
            .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer $token")
            .build()
        val call = client.newCall(req)
        withContext(Dispatchers.IO) {
            val resp = call.execute()
            val body = resp.body?.string() ?: ""
            if (!resp.isSuccessful) {
                throw ApiException(resp.code, parseErrorMessage(body, resp.code))
            }
        }
    }

    suspend fun deleteStorageObject(token: String, libName: String) {
        val clean = libName.removeSuffix(".so")
        val url = "${BuildConfig.SUPABASE_URL}/storage/v1/object/AMods-Libs/${clean}.so"
        val req = Request.Builder().url(url)
            .method("DELETE", null)
            .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer $token")
            .build()
        withContext(Dispatchers.IO) {
            val resp = client.newCall(req).execute()
            if (!resp.isSuccessful && resp.code != 404) {
                val body = resp.body?.string() ?: ""
                throw ApiException(resp.code, parseErrorMessage(body, resp.code))
            }
        }
    }

    private fun parseErrorMessage(body: String, code: Int): String {
        if (body.isBlank()) {
            return when (code) {
                429 -> "Too many attempts. Please wait and try again."
                401 -> "Invalid credentials or session expired."
                403 -> "Access denied."
                404 -> "Resource not found."
                500 -> "Server error. Please try again later."
                else -> "Request failed ($code). Please try again."
            }
        }

        try {
            val err = json.decodeFromString<ErrorResponse>(body)
            val msg = err.errorDescription ?: err.error ?: err.reason
            if (msg != null) return msg
        } catch (_: Exception) {}

        try {
            val obj = json.parseToJsonElement(body).jsonObject
            val el = obj["error_description"] ?: obj["message"] ?: obj["msg"] ?: obj["error"] ?: obj["reason"]
            if (el != null) return el.jsonPrimitive.content
        } catch (_: Exception) {}

        return body
    }
}

class ApiException(val code: Int, message: String) : Exception(message)
