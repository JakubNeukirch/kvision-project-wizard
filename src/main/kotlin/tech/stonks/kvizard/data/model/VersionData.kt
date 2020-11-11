package tech.stonks.kvizard.data.model

import com.google.gson.annotations.SerializedName

data class VersionData(
    @SerializedName("kvision")
    val kVision: String,
    @SerializedName("kotlin")
    val kotlin: String,
    @SerializedName("serialization")
    val serialization: String,
    @SerializedName("template-jooby")
    val templateJooby: TemplateJooby,
    @SerializedName("template-ktor")
    val templateKtor: TemplateKtor,
    @SerializedName("template-micronaut")
    val templateMicronaut: TemplateMicronaut,
    @SerializedName("template-spring-boot")
    val templateSpring: TemplateSpring,
    @SerializedName("template-vertx")
    val templateVertx: TemplateVertx
)

data class TemplateJooby(
    @SerializedName("jooby")
    val jooby: String
)

data class TemplateKtor(
    @SerializedName("ktor")
    val ktor: String
)

data class TemplateMicronaut(
    @SerializedName("micronaut")
    val micronaut: String
)

data class TemplateSpring(
    @SerializedName("spring-boot")
    val springBoot: String,
    @SerializedName("spring-data-r2dbc")
    val springDataR2dbc: String,
    @SerializedName("r2dbc-postgresql")
    val r2dbcPostgres: String,
    @SerializedName("r2dbc-h2")
    val r2dbcH2: String
)

data class TemplateVertx(
    @SerializedName("vertx-plugin")
    val vertxPlugin: String
)
