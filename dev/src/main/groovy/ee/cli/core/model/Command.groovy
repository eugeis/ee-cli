package ee.cli.core.model

class Command {
    String command
    String item
    String callable
    def params
    Command next


    @Override
    public String toString() {
        "Command{" +
                "item='" + item + '\'' +
                ", callable='" + callable + '\'' +
                ", params=" + params +
                '}'
    }
}
