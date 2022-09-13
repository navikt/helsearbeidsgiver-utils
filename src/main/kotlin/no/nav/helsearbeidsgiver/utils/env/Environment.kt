package no.nav.helsearbeidsgiver.utils.env

open class Environment {
    init {
        injectIt()
    }

    private fun injectIt() {
        for (f in this.javaClass.declaredFields) {
            f.isAccessible = true
            if (f.isAnnotationPresent(EnvironmentValue::class.java)) {
                val name: String = f.getAnnotation(EnvironmentValue::class.java).name
                val value = System.getenv(name)
                if (f.type == String::class.java) {
                    try {
                        f[this] = value
                    } catch (e: IllegalAccessException) {
                        throw RuntimeException(e)
                    }
                }
            }
        }
    }
}
