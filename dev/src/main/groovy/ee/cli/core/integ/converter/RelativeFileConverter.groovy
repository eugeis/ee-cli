package ee.cli.core.integ.converter

import ee.cli.core.integ.ExpressionResolver
import ee.cli.core.integ.Relative

import javax.inject.Inject
import javax.persistence.AttributeConverter
import java.lang.reflect.Field

class RelativeFileConverter implements AttributeConverter<File, String> {
    @Inject
    ExpressionResolver expressionResolver

    @Inject
    Object item

    @Inject
    Field target

    @Override
    String convertToDatabaseColumn(File file) {
        String ret = file.canonicalPath
        def relative = target.getAnnotation(Relative)
        if (relative) {
            File base = expressionResolver.resolve(relative.value(), item)
            String baseCanonicalPath = base.canonicalPath
            if (ret.indexOf(baseCanonicalPath) == 0) {
                ret = ret.substring(baseCanonicalPath.length())
            }
        }
        ret
    }

    @Override
    File convertToEntityAttribute(String filePath) {
        File ret
        def relative = target.getAnnotation(Relative)
        if (relative) {
            File base = expressionResolver.resolve(relative.value(), item)
            ret = new File(base, filePath)
        } else {
            ret = new File(filePath)
        }
        ret
    }
}
