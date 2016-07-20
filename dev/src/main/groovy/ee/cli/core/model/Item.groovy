package ee.cli.core.model

abstract class Item extends Base implements Labeled {
    Label label = label()

    protected Label label() {
        buildLabel()
    }
}
