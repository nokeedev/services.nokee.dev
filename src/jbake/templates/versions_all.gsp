<%
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def resolveProperty = { String envVarKey, String systemPropKey ->
    Object propValue = System.getenv().get(envVarKey)

    if (propValue != null) {
        return propValue.toString()
    }

    propValue = System.getProperty(systemPropKey)
    if (propValue != null) {
        return propValue.toString()
    }

    return null
}

def allVersions = [] as Set
['distributions', 'distributions-snapshots'].each {
    def content = new URL("https://dl.bintray.com/nokeedev/${it}/dev/nokee/jni-library/dev.nokee.jni-library.gradle.plugin/").text
    allVersions.addAll(content.findAll(/\d+\.\d+\.\d+(-[a-z0-9]{7})?/))
}
def data = allVersions.collect { version ->
    def isNightly = version.contains('-')
    def repositoryName = isNightly ? 'distributions-snapshots' : 'distributions'
    def packageName = isNightly ? 'artifacts' : 'dev.nokee:nokee-gradle-plugins'

    def user = resolveProperty("BINTRAY_USER", "dev.nokee.bintray.user")
    def key = resolveProperty("BINTRAY_KEY", "dev.nokee.bintray.key")
    def curl = "curl -u${user}:${key} https://api.bintray.com/packages/nokeedev/${repositoryName}/${packageName}/versions/${version}".execute()
    assert curl.waitFor() == 0
    def versionData = new JsonSlurper().parseText(curl.in.text)
    return [
        version: version,
        nightly: (version.contains('-')),
        buildTime: versionData.created
    ]
}.findAll { it.buildTime != null }.sort { a, b -> b.buildTime <=> a.buildTime }

println JsonOutput.prettyPrint(JsonOutput.toJson(data))
%>