package ee.cli.core.model

enum PackageState {
    UNKNOWN(0), RESOLVED(1), PREPARED(2), INSTALLED(3), UNINSTALLED(-1)

    private Integer order

    private PackageState(int order) {
        this.order = order
    }

    boolean isUnknown() {
        UNKNOWN == this
    }

    boolean isResolved() {
        RESOLVED == this
    }

    boolean isPrepared() {
        PREPARED == this
    }

    boolean isInstalled() {
        INSTALLED == this
    }

    boolean isUninstalled() {
        UNINSTALLED == this
    }

    int getOrder() {
        order
    }

    static PackageState findByName(def name) {
        PackageState ret
        if (name) {
            ret = values().find { it.name().equalsIgnoreCase(name) }
        }
        ret ?: UNKNOWN
    }
}
