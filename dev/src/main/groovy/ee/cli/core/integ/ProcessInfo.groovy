package ee.cli.core.integ

class ProcessInfo {
    String id
    String name
    String info

    boolean like(String partOfNameOrProcessInfo) {
        name.contains(partOfNameOrProcessInfo) || info.contains(partOfNameOrProcessInfo)
    }
}
