package ee.cli.core.model

import javax.inject.Inject

abstract class Alias extends Item {
    @Inject
    String name

    @Inject
    boolean automatic = false

    abstract void apply(Command command, Context context)
}
