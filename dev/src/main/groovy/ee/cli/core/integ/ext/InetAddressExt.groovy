package ee.cli.core.integ.ext

import ee.cli.core.integ.AbstractExtension

class InetAddressExt extends AbstractExtension {

    protected void doExtend() {
        def meta = InetAddress.metaClass

        meta.isLocalAddress = { InetAddress address = delegate
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces()
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement()
                Enumeration<InetAddress> addresses = iface.getInetAddresses()

                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement()
                    if (addr.equals(address)) {
                        return true
                    }
                }
            }
            false
        }
    }

}