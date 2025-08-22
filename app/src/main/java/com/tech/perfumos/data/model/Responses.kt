package com.tech.perfumos.data.model


/**************************  login & signup  api response*********************/
data class LoginResponseAPI(
    val data: LoginResponseAPIData?, val message: String?, val status: Int?
)

data class LoginResponseAPIData(
    val city_id: Int?,
    val city_name: String?,
    val country_id: Int?,
    val country_name: String?,
    val created_at: String?,
    val device_token: String?,
    val device_type: Int?,
    val email: String?,
    val id: Int?,
    val is_active: Int?,
    val is_block: Any?,
    val is_profile_complete: Int?,
    val is_verified: Any?,
    val lattitude: Double,
    val location: String?,
    val login_type: Any?,
    val longitude: Double,
    val mobile: Any?,
    val name: String?,
    val otp: Any?,
    val otp_expired_at: Any?,
    val profile: String?,
    val role: Int?,
    val social_id: Any?,
    val state_id: Int?,
    val state_name: String?,
    val token: String?,
    val updated_at: String?


)


/**************************    country, city & state api response*********************/
data class GetCountry(
    val data: List<GetCountryData>?, val message: String?, val status: Int
)

data class GetCountryData(
    val code: String?,
    val created_at: String?,
    val id: Int?,
    val name: String?,
    val phone: Int?,
    val updated_at: String?,
    val countries_id: Int?,
    val states_id: Int?,
)


/**************************    drop down api response*********************/

data class DropDown(
    val data: DropDownData?, val message: String, val status: Int
)

data class DropDownData(
    val Description: List<Description>?,
    val Emirate: List<Emirate>?,
    val Employment_status: List<EmploymentStatu>?,
    val Language: List<Language>?,
    val Marital_status: List<MaritalStatu>?,
    val Nationality: List<Nationality>?,
    val Visa_type: List<VisaType>?,
    val experience: List<ExperienceApi>?,
    val skill: List<Skill>?,
    val title: List<Title>?
)

data class Description(
    val created_at: String?,
    val description: String?,
    val id: Int?,
    val is_active: Int?,
    val title_id: Int?,
    val updated_at: String?,
    var selected: Boolean = false
)

data class Emirate(
    val created_at: Any?,
    val emirates: String?,
    val id: Int?,
    val is_active: Int?,
    val updated_at: Any?
)

data class EmploymentStatu(
    val created_at: String?,
    val employment_status: String?,
    val id: Int?,
    val is_active: Int?,
    val updated_at: Any?
)

data class Language(
    val created_at: Any?,
    val id: Int?,
    val is_active: Int?,
    val language: String?,
    val updated_at: Any?
)

data class MaritalStatu(
    val created_at: Any?,
    val id: Int?,
    val is_active: Int?,
    val marital_status: String?,
    val updated_at: Any?
)

data class Nationality(
    val created_at: Any?,
    val id: Int?,
    val is_active: Int?,
    val nationality: String?,
    val updated_at: Any?
)

data class VisaType(
    val created_at: Any?,
    val id: Int?,
    val is_active: Int?,
    val updated_at: Any?,
    val visa_type: String?
)

data class ExperienceApi(
    val created_at: String?,
    val experience: String?,
    val id: Int?,
    val is_active: Int?,
    val total_experience: String?,
    val updated_at: Any?
)

data class Skill(
    val created_at: String?,
    val id: Int?,
    val is_active: Int?,
    val skill: String?,
    val updated_at: Any?
)

data class Title(
    val created_at: String?,
    val id: Int?,
    val is_active: Int?,
    val title: String?,
    val updated_at: Any?
)




