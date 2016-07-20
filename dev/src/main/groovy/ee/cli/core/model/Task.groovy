package ee.cli.core.model

import ee.cli.core.Handler

import javax.inject.Inject

abstract class Task extends Item implements Handler{
    @Inject
    String name

    @Inject
    boolean automatic = false
}
