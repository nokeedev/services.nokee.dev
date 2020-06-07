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

    def user = resolveProperty("BINTRAY_USER", "dev.nokee.bintray.user")
    def key = resolveProperty("BINTRAY_KEY", "dev.nokee.bintray.key")
    def curl = "curl -u${user}:${key} https://api.bintray.com/packages/nokeedev/distributions/dev.nokee:nokee-gradle-plugins/versions/_latest".execute()
    assert curl.waitFor() == 0
    def versionData = new JsonSlurper().parseText(curl.in.text)
    def data = [
            version: versionData.name,
            nightly: false,
            buildTime: versionData.created
    ]
    println JsonOutput.prettyPrint(JsonOutput.toJson(data))
%>