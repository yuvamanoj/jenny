import org.yaml.snakeyaml.Yaml

Map.metaClass.addNested { Map rhs ->
    def lhs = delegate
    rhs.each { k, v ->
        if (lhs.containsKey(k)) {
            lhs[k].addNested(v)
        } else {
            lhs[k] = v
        }
    }   
    lhs
}

List.metaClass.addNested { List rhs ->
    def lhs = delegate
    rhs.each {
        lhs.add it
    }

    lhs
}

// -------------------------------------------------------------------
// Read the configuration from the home folder.
// -------------------------------------------------------------------
Yaml parser = new Yaml()
jennyConfig = [:]

loadConfigFile = { fileName ->
    if ((fileName as File).exists()) {
        println "> loading config file: ${fileName}"
        def loadedConfig = parser.load((fileName as File).text)
        jennyConfig.addNested(loadedConfig)
    }
}

loadCommandLineOptions = { options -> 
    def commandLineOptions = ["execute":[:]]

    if (options.libs) {
        commandLineOptions["libs"] = options.libs
    }

    if (options.params) {
        def parameters = [:]

        for (def i = 0; i < options.params.size(); i += 2) {
            parameters[options.params[i]] = parameters[i + 1]
        }

        commandLineOptions["params"] = parameters
    }

    if (options.onlys) {
        commandLineOptions.execute["only"] = options.onlys
    }

    if (options.skips) {
        commandLineOptions.execute["skip"] = options.skips
    }

    if (options.resumeFrom) {
        commandLineOptions.execute["resumeFrom"] = options.resumeFrom
    }

    if (options.skipAfter) {
        commandLineOptions.execute["skipAfter"] = options.skipAfter
    }

    jennyConfig.addNested(commandLineOptions)
}