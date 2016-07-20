package ee.cli.core.integ.api

import ee.cli.core.integ.ProcessInfo
import ee.cli.core.model.Base

import java.nio.charset.Charset

class Os extends Base {
    String name
    String family

    boolean isLinux
    boolean isWindows

    Os() {
        // allow to overwrite the OS, e.g. to switch between cygwin and windows
        name = System.getProperty('os') ?: System.getProperty('os.name')
        family = ['Windows', 'Linux'].find { name =~ "^$it" }
        if (name != System.getProperty('os.name')) {
            log.info 'real OS {}, effective OS {}', System.getProperty('os.name'), name
        }

        isLinux = family == 'Linux'
        isWindows = family == 'Windows'
    }


    boolean isWindows() {
        isWindows
    }

    boolean isLinux() {
        isLinux
    }

    List<ProcessInfo> javaProcesses() {
        List<String> ret = new ArrayList<String>()
        BufferedReader output = null
        try {
            Process jps = Runtime.runtime.exec('jps -m')
            output = new BufferedReader(new InputStreamReader(jps.inputStream, Charset.forName("UTF-8")))
            String line = output.readLine()
            while (line != null) {
                String[] info = line.split(' ')
                ret.add(new ProcessInfo(id: info[0], name: info[1], info: line))
                line = output.readLine()
            }
        } catch (Exception e) {
            log.error('Exception at getting java process status list.: {}', e)
        } finally {
            if (output != null) {
                try {
                    output.close()
                } catch (IOException e) {
                    log.error('Exception at getting java process status list.: {}', e)
                }
            }
        }
        ret
    }

    ProcessInfo findJavaProcessLike(String partOfNameOrParams) {
        def processes = javaProcesses()
        processes.find { it.like(partOfNameOrParams) }
    }
}