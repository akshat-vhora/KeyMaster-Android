package com.akshatmodz.panel.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class UsersCreateRequest(val email: String, val password: String, val username: String, val email_id: String = "", val referral: String = "")

@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long,
    val user: UserData
)

@Serializable
data class UserData(
    val id: String,
    val email: String,
    @SerialName("app_metadata") val appMetadata: AppMetadata = AppMetadata(),
    @SerialName("user_metadata") val userMetadata: UserMetadata = UserMetadata()
)

@Serializable
data class AppMetadata(
    val userlevel: String = "",
    val username: String = ""
)

@Serializable
data class UserMetadata(
    val fullname: String = "",
    val username: String = "",
    val level: Int = 2,
    val saldo: Int = 0
)

@Serializable
data class UserProfile(
    @SerialName("id_users") val idUsers: Int = 0,
    val username: String = "",
    @SerialName("email_id") val email: String = "",
    val level: Int = 2,
    val saldo: Int = 0,
    val status: Int = 1,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class KeyItem(
    @SerialName("id_keys") val idKeys: Int = 0,
    val game: String = "",
    @SerialName("user_key") val userKey: String = "",
    val duration: Int = 0,
    @SerialName("expired_date") val expiredDate: String? = null,
    @SerialName("max_devices") val maxDevices: Int = 1,
    val devices: String? = null,
    val registrator: String = "",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class KeyListResponse(
    val data: List<KeyItem>,
    val count: Int,
    val page: Int,
    val limit: Int
)

@Serializable
data class KeyGenerateRequest(
    val game: String,
    val duration: Int,
    @SerialName("max_devices") val maxDevices: Int,
    @SerialName("custom_key") val customKey: String? = null
)

@Serializable
data class KeyGenerateResponse(
    val key: String,
    val game: String = "",
    val duration: Int = 0,
    @SerialName("max_devices") val maxDevices: Int = 0,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class KeyEditRequest(
    @SerialName("id_keys") val idKeys: Int,
    val game: String? = null,
    @SerialName("user_key") val userKey: String? = null,
    val duration: Int? = null,
    @SerialName("max_devices") val maxDevices: Int? = null,
    @SerialName("expired_date") val expiredDate: String? = null,
    val devices: String? = null
)

@Serializable
data class BulkRequest(
    val action: String,
    @SerialName("id_keys") val idKeys: Int? = null
)

@Serializable
data class BulkResponse(val count: Int)

@Serializable
data class AdminUserItem(
    @SerialName("id_users") val idUsers: Int = 0,
    val username: String = "",
    @SerialName("email_id") val email: String = "",
    val level: Int = 2,
    val saldo: Int = 0,
    val status: Int = 1,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class AdminUsersResponse(
    val data: List<AdminUserItem>,
    val page: Int,
    val limit: Int,
    val total: Int,
    @SerialName("total_pages") val totalPages: Int
)

@Serializable
data class AdminUserEditRequest(
    @SerialName("id_users") val idUsers: Int,
    val username: String? = null,
    @SerialName("email_id") val email: String? = null,
    val level: Int? = null,
    val saldo: Int? = null,
    val status: Int? = null
)

@Serializable
data class ReferralCreateRequest(@SerialName("set_saldo") val setSaldo: Int)

@Serializable
data class ReferralItem(
    @SerialName("id_reff") val idReff: Int,
    val code: String,
    @SerialName("set_saldo") val setSaldo: Int,
    @SerialName("created_by") val createdBy: String,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class ReferralListResponse(
    val data: List<ReferralItem>,
    val page: Int,
    val limit: Int,
    val total: Int
)

@Serializable
data class PasswordChangeRequest(val currentPassword: String, val password: String)

@Serializable
data class DeleteKeyRequest(@SerialName("id_keys") val idKeys: Int)

@Serializable
data class DeleteUserRequest(@SerialName("id_users") val idUsers: Int)

@Serializable
data class ErrorResponse(
    val error: String? = null,
    @SerialName("error_description") val errorDescription: String? = null,
    val status: Boolean? = null,
    val reason: String? = null
)

@Serializable
data class GameItem(
    val code: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("lib_name") val libName: String = "",
    val active: Boolean = true
)

@Serializable
data class GameListResponse(val data: List<GameItem>)

@Serializable
data class GameCreateRequest(
    val code: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("lib_name") val libName: String = ""
)

@Serializable
data class GameUpdateRequest(
    val code: String,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("lib_name") val libName: String? = null,
    val active: Boolean? = null
)

@Serializable
data class GameDeleteRequest(val code: String)


