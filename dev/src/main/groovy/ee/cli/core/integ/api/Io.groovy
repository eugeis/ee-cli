package ee.cli.core.integ.api

import ee.cli.core.Nop
import ee.cli.core.integ.Injector
import ee.cli.core.integ.NopInterceptor
import ee.cli.core.model.ExecConfig
import ee.cli.core.model.Item

import javax.inject.Inject
import javax.interceptor.Interceptors
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

@Interceptors([NopInterceptor])
class Io extends Item {

    @Inject
    Ant ant

    @Inject
    Runner runner

    @Inject
    Injector injector

    @Nop
    Path copy(Path from, Path to) {
        Path ret
        try {
            if (mkdirs(to)) {
                if (Files.isDirectory(from)) {
                    ant.copy(todir: to) {
                        fileset(dir: from)
                    }
                } else {
                    ret = Files.copy(from, to.resolve(from.fileName))
                }
            }
        } catch (e) {
            log.warn('Copy of {} to {} not possible because of {}', from, to, e)
        }
        ret
    }

    @Nop
    boolean mkdirs(Path to) {
        boolean ret = false
        try {
            if (!Files.exists(to)) {
                Files.createDirectories(to)
                ret = true
            } else if (!Files.isDirectory(to)) {
                log.warn('Create directories for {} not possible because it is a existing file.')
                ret = false
            }
        } catch (e) {
            log.warn('Create createDirectories for {} not possible because of {}', to, e)
        }
        ret
    }


    boolean createSymLink(Path alias, Path target) {
        deleteFileOrLink(alias)
        ExecConfig execConfig = injector.wire(
                new ExecConfig(home: alias.parent.toFile(), wait: true, cmd: ['mklink', alias.toString(), target.toString()]))
        runner.run execConfig
    }

    @Nop
    protected void deleteFileOrLink(Path alias) {
        if (alias.exists()) {
            if (alias.isRegularFile() || alias.isSymbolicLink()) {
                alias.delete()
            } else {
                log.error("Can not delete, the file {} exists and is a directory.", alias)
            }
        }
    }
}
