package com.surecn.familymovie.common;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Objects;

import jcifs.netbios.NbtAddress;

public class CIFSSearcher {
    private static final CIFSSearcher ourInstance = new CIFSSearcher();

    public static final short NET_MASK_MAX_LENGTH = 32;

    public static CIFSSearcher getInstance() {
        return ourInstance;
    }

    private CIFSSearcher() {
    }

//    public static int reversalIpv4Address(int Ipv4Address) {
//        int reversalIp = 0;
//        reversalIp += Ipv4Address << 24 & 0xFF000000;
//        reversalIp += Ipv4Address << 8 & 0xFF0000;
//        reversalIp += Ipv4Address >> 8 & 0xFF00;
//        reversalIp += Ipv4Address >> 24 & 0xFF;
//        return reversalIp;
//    }
//
//    public static short getNetMask(int host) throws UnknownHostException, SocketException {
//        InetAddress inetAddress = Inet4Address.getByAddress(BigInteger.valueOf(host).toByteArray());
//        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
//        for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
//            int length = interfaceAddress.getNetworkPrefixLength();
//            if (length > 0 && length <= NET_MASK_MAX_LENGTH) {
//                return interfaceAddress.getNetworkPrefixLength();
//            }
//        }
//        return 0;
//    }
//
//    public Flowable<CIFSDevice> searchDevices(int hostAddress) {
//        return Flowable.just(hostAddress)
//                .map(reversalIpv4Address)
//                .flatMap(host -> {
//                    short netMaskLength = NetUtils.getNetMask(host);
//                    if (netMaskLength == 0) {
//                        throw new Exception("net mask is 0");
//                    }
//                    int offset = 32 - netMaskLength;
//                    int startIp = (host >> offset << offset) + 1;
//                    int endIp = (startIp | ((1 << offset) - 1)) - 1;
//                    int count = endIp - startIp;
//                    return checkDevices(startIp, count);
//                });
//    }
//
//    private Flowable<CIFSDevice> checkDevices(int startIp, int count) {
//        return Flowable.range(startIp, count)
//                .map(hostIp -> NetUtils.formatIpv4Address(hostIp).getHostAddress())
//                .flatMap(hostIp -> Flowable.just(hostIp)
//                        .filter(this::canConnect)
//                        .map(ip -> {
//                            String hostName = ip;
//                            NbtAddress[] addresses = NbtAddress.getAllByAddress(ip);
//                            if (addresses != null && addresses.length > 0) {
//                                for (NbtAddress address : addresses) {
//                                    if (address.getNameType() != 0x00 && !address.getHostName().startsWith("IS~")) {
//                                        hostName = address.getHostName();
//                                        break;
//                                    }
//                                }
//                            }
//                            return new CIFSDevice(ip, hostName);
//                        })
//                        .subscribeOn(SchedulerProvider.getInstance().io()));
//    }
//
//    private boolean canConnect(String hostIp) {
//        Socket socket = null;
//        try {
//            socket = new Socket();
//            InetSocketAddress address = new InetSocketAddress(hostIp, 445);
//            socket.connect(address, 1000);
//            return true;
//        } catch (IOException e) {
//            return false;
//        } finally {
//            if (socket != null) {
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }

    public class CIFSDevice {
        private String mHostIp;
        private String mHostName;

        public CIFSDevice(String hostIp, String hostName) {
            this.mHostIp = hostIp;
            this.mHostName = hostName;
        }

        public String getHostIp() {
            return mHostIp;
        }

        public void setHostIp(String hostIp) {
            this.mHostIp = hostIp;
        }

        public String getHostName() {
            return mHostName;
        }

        public void setHostName(String hostName) {
            this.mHostName = hostName;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            CIFSDevice device = (CIFSDevice) obj;
            return device.getHostIp().equalsIgnoreCase(mHostIp) &&
                    device.getHostName().equalsIgnoreCase(mHostName);
        }

        @Override
        public String toString() {
            return "CIFSDevice{" +
                    "mHostIp='" + mHostIp + '\'' +
                    ", mHostName='" + mHostName + '\'' +
                    '}';
        }
    }
}