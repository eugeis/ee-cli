package ee.cli.core

import ee.cli.core.model.Context
import ee.cli.core.model.Result

interface Handler {
    Result execute(Context context, def source)
}