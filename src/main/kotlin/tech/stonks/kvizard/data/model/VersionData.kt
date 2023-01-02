package tech.stonks.kvizard.data.model

import com.google.gson.annotations.SerializedName

data class VersionData(
    @SerializedName("kvision")
    val kVision: String,
    @SerializedName("kotlin")
    val kotlin: String,
    @SerializedName("coroutines")
    val coroutines: String,
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
    val templateVertx: TemplateVertx,
    @SerializedName("modules")
    val modules: List<Module>
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
    val springBoot: String
)

data class TemplateVertx(
    @SerializedName("vertx-plugin")
    val vertxPlugin: String
)

data class Module(
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("excludes")
    val excludes: List<String>?,
    @SerializedName("initializers")
    val initializers: List<String>?
)
