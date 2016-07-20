package ee.cli.core.integ.ext

import ee.cli.core.integ.AbstractExtension
import ee.cli.core.integ.api.Io

import javax.inject.Inject
import java.nio.file.Files
import java.nio.file.Path

class PathExt extends AbstractExtension {

    @Inject
    Io io

    protected void doExtend() {
        def meta = Path.metaClass

        meta.copyTo = { Path to ->
            io.copy(delegate, to)
        }

        meta.isRegularFile = {
            Files.isRegularFile(delegate)
        }

        meta.isDirectory = {
            Files.isDirectory(delegate)
        }

        meta.exists = {
            Files.exists(delegate)
        }

        meta.delete = {
            Files.delete(delegate)
        }

        meta.deleteIfExists = {
            Files.deleteIfExists(delegate)
        }

        meta.mkdirs = {
            io.mkdirs(delegate)
        }

        meta.ext = {
            Path path = delegate
            path.toString().fileExt()
        }
    }
}