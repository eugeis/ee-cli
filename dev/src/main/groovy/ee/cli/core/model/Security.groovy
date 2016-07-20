package ee.cli.core.model

import ee.cli.core.integ.Relative

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.validation.constraints.NotNull

class Security extends Item {
    @Inject
    @NotNull
    Workspace workspace

    @Inject
    @Relative('workspace.home')
    File home

    @Inject
    @Relative('home')
    File authFile

    @PostConstruct
    void init() {
        if(!home) {
            home = workspace.ee
        }

        if(!authFile) {
            authFile = new File(home, 'vault.dat')
        }
    }
}