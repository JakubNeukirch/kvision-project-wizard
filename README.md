# KVision Project Wizard
KVision IntellJ IDEA plugin for new project creation.
Supported project types:
* Frontend project
* Ktor fullstack project
* Spring Boot fullstack project
* Javalin fullstack project
* Jooby fullstack project
* Micronaut fullstack project
* Vert.x fullstack project
 
 ## Contribution
You can contribute new project types:
* create new `TreeGenerator` subclass
* add new case to `KVisionModuleBuilder.createGenerator()`
* add new value to `KVisionModuleBuilder.supportedProjectTypes`
