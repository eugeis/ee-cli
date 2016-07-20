package ee.main

import ee.cli.Ee
import ee.cli.core.model.Result

class EeMain {

    public static void main(String[] args) {
        Result ret = new Ee().execute(args)
    }
}