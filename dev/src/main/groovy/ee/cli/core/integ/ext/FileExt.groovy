package ee.cli.core.integ.ext

import ee.cli.core.integ.AbstractExtension

class FileExt extends AbstractExtension {

    protected void doExtend() {
        def meta = File.metaClass

        meta.path = {
            File file = delegate
            file.canonicalPath.path()
        }

        meta.ext = {
            File file = delegate
            file.name.fileExt()
        }

        meta.waitForDeleted = { File file ->
            def ant = ant()
            ant.waitfor(maxwait: '10', maxwaitunit: 'second', timeoutproperty: 'ret') {
                not { available(file: "$file") }
            }
            !ant.ret("Timeout for waitForFileDeleted($file)")
        }
        meta.forceDeleteAny = {
            File file = delegate
            boolean ret = false
            log.info "Delete '$file'"
            try {
                if (file.isDirectory()) {
                    ret = file.deleteDir()
                } else {
                    ret = file.delete()
                }
            } catch (e) {
                log.error "Exception '$e' at deleting of '$file'"
            }
            ret
        }
        meta.forceDelete = {
            File file = delegate
            if (file.exists()) {
                if (!file.forceDeleteAny()) {
                    info "Normal delete was not possible, so try unlock and delete the '$file'"
                    def command = "unlocker.exe $file.path /S /D"
                    run {
                        cmd = command
                        dir = file.parentFile
                    }
                }
            } else {
                log.info "File '$file' does not exists, nothing to delete."
            }
        }
    }
}