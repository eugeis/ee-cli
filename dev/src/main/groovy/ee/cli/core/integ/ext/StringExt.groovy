package ee.cli.core.integ.ext

import ee.cli.core.integ.AbstractExtension

import java.nio.file.Paths

class StringExt extends AbstractExtension {

    protected void doExtend() {
        def meta = String.metaClass

        meta.literalAsJavaName = { String string ->
            string.split('_').collect { String part -> part.toLowerCase().capitalize() }.join('')
        }

        meta.path = {
            delegate.replaceAll('\\\\', '/')
        }

        meta.toPath = {
            Paths.get(delegate)
        }

        meta.dotsAsPath = {
            delegate.replaceAll('\\.', '/')
        }

        meta.uncapitalize = {
            String ret = delegate
            if (ret) {
                if (ret.length() >= 2) {
                    ret = ret[0].toLowerCase() + ret.substring(1)
                } else {
                    ret = ret.toLowerCase()
                }
            }
            ret
        }

        meta.key = {
            delegate.replaceAll('-', '_')
        }

        meta.underscoreToCamelCase = {
            String ret = delegate
            if (!ret || ret.isAllWhitespace()) {
                return ''
            }
            ret.replaceAll(/_\w/) { it[1].toUpperCase() }
        }

        meta.extension = {
            String ret = delegate
            int lastDot = ret.lastIndexOf('.')
            lastDot ? ret.substring(lastDot + 1) : ret
        }

        meta.removeSuffix = { String suffix ->
            String ret = delegate
            int pos = ret.indexOf(suffix)
            if (pos > 0) {
                ret = ret.substring(0, pos)
            } else if (!pos) {
                ret = ''
            }
            ret
        }

        meta.asClass = { String namespace ->
            String className = delegate
            String fullClassName = namespace ? "$namespace.$className" : className
            StringExt.classLoader.loadClass(fullClassName, true)
        }

        meta.asClassInstance = { String namespace ->
            delegate.asClass(namespace).newInstance()
        }

        meta.fileExt = {
            String fileName = delegate
            String ret = fileName.lastIndexOf('.').with { it != -1 ? fileName.substring(it + 1) : '' }
            ret.toLowerCase()
        }
    }
}