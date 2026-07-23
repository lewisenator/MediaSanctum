plugins {
    java
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
    checkstyle
    pmd
    id("com.github.spotbugs") version "6.5.9"
}

group = "com.media-sanctum"
version = "0.0.1-SNAPSHOT"

// Override versions of Spring Boot BOM-managed dependencies
extra["tomcat.version"] = "11.0.22"

// Force patched versions of vulnerable transitive dependencies
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "commons-beanutils" && requested.name == "commons-beanutils") {
            useVersion("1.11.0")
        }
        if (requested.group == "org.codehaus.plexus" && requested.name == "plexus-utils") {
            useVersion("3.6.1")
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    implementation("org.xerial:sqlite-jdbc:3.53.2.0")
    implementation("org.hibernate.orm:hibernate-community-dialects")
    implementation("com.nimbusds:nimbus-jose-jwt:10.9")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.apache.commons:commons-lang3:3.20.0")
    implementation("io.github.resilience4j:resilience4j-spring-boot4:2.4.0")
    implementation("commons-codec:commons-codec:1.22.0")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    compileOnly("org.projectlombok:lombok")
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.10.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.skyscreamer:jsonassert:1.5.3")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Packages excluded from coverage measurement — configuration, data classes, and
// framework-glue that have no meaningful unit-test surface.
val coverageExclusions = listOf(
    "**/config/**",
    "**/entity/**",
    "**/model/**",
    "**/resource/**",
    "**/exception/**",
    "**/repository/**",
    "**/*Application*",
    "**/client/*/model/**",
)

// =============================================================================
// Tests
// =============================================================================

tasks.withType<Test> {
    val envFile = file("${projectDir}/.env")
    if (envFile.exists()) {
        envFile.forEachLine { line ->
            if (line.isNotBlank() && !line.startsWith("#")) {
                val idx = line.indexOf('=')
                if (idx > 0) {
                    environment(line.substring(0, idx).trim(), line.substring(idx + 1).trim())
                }
            }
        }
    }
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

// =============================================================================
// JaCoCo — code coverage
// =============================================================================

jacoco {
    toolVersion = "0.8.13"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) { exclude(coverageExclusions) }
        })
    )
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            limit {
                // Build fails if instruction coverage drops below 80%
                counter = "INSTRUCTION"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
    // Apply the same exclusions so excluded packages don't drag coverage down
    classDirectories.setFrom(tasks.jacocoTestReport.get().classDirectories)
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

// =============================================================================
// Checkstyle — Google Java Style
// =============================================================================

checkstyle {
    toolVersion = "10.21.4"
    configFile = file("${projectDir}/config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
    maxWarnings = 0
}

// =============================================================================
// PMD — source-level static analysis
// =============================================================================

pmd {
    toolVersion = "7.25.0"
    isConsoleOutput = true
    ruleSets = emptyList()
    ruleSetFiles = files("${projectDir}/config/pmd/ruleset.xml")
}

// PMD 7.x has a known StackOverflowError when resolving Spring's self-referential
// generic bounds (e.g. ResponseEntity.BodyBuilder). Clearing the aux classpath
// disables deep type resolution and works around the crash; source-level rules
// still run normally.
tasks.withType<Pmd> {
    classpath = files()
}

// =============================================================================
// SpotBugs — bytecode static analysis
// =============================================================================

spotbugs {
    toolVersion = "4.9.8"
    ignoreFailures = false
    showProgress = true
    excludeFilter = file("${projectDir}/config/spotbugs/exclude.xml")
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask> {
    reports.create("html") { required = true }
    reports.create("xml") { required = false }
}

// Test sources use underscore-separated method names (BDD style), text blocks with
// non-standard indentation, and other patterns that don't fit production style rules.
// Quality of tests is enforced by running them, not by linting them.
tasks.checkstyleTest { enabled = false }
tasks.pmdTest { enabled = false }
tasks.spotbugsTest { enabled = false }
