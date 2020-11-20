# kvision-project-wizard
KVision plugin for project creation.
Supported project types:
 * Fullstack KTOR
 * Fullstack Spring-Boot
 * Frontend-only
 
 ## Contribution
If you want to contribute, You can add support for other backend frameworks like javalin, jooby, vertx and others. To do so You must:
* inherit by `TreeGenerator`
* add case to KVisionModuleBuilder.createGenerator()
* add enum to KVisionModuleBuilder.supportedBackendLibraries
