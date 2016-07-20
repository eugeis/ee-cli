package ee.cli.core.model

interface Settings {
    String getKey()

    def get(String name)
    def get(String name, defaultValue)
    void set(String name, value)
    void clear(String name)
    void flags(String... flags)
    void params(Map<String, Object> params)

    boolean assertProp(String name)
    boolean assertProps(String... names)
}
