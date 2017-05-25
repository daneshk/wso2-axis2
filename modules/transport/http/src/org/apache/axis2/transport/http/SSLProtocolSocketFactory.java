/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.axis2.transport.http;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This is a wrapper class to SecureProtocolSocketFactory class to enable https protocol defined as System Property.
 * This class will add protocols to the socket returns from SecureProtocolSocketFactory.
 */
public class SSLProtocolSocketFactory implements SecureProtocolSocketFactory {

    private static final String HTTPS_PROTOCOLS_KEY = "https.protocols";
    private static final String PROTOCOLS_SEPARATOR = ",";
    private final SecureProtocolSocketFactory base;

    public SSLProtocolSocketFactory(ProtocolSocketFactory base)
    {
        if(base == null || !(base instanceof SecureProtocolSocketFactory)) throw new IllegalArgumentException();
        this.base = (SecureProtocolSocketFactory) base;
    }

    private Socket enableSocketProtocols(Socket socket) {
        if(!(socket instanceof SSLSocket)) {
            return socket;
        }
        // check the system property(https.protocols). if not set returns the default socket
        String httpsProtocols = System.getProperty(HTTPS_PROTOCOLS_KEY);
        if (httpsProtocols == null) {
            return socket;
        }

        // set https protocols to the socket.
        SSLSocket sslSocket = (SSLSocket) socket;
        sslSocket.setEnabledProtocols(httpsProtocols.split(PROTOCOLS_SEPARATOR));
        return sslSocket;
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return enableSocketProtocols(base.createSocket(socket, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException {
        return enableSocketProtocols(base.createSocket(host, port, localAddress, localPort));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort, HttpConnectionParams params) throws IOException {
        return enableSocketProtocols(base.createSocket(host, port, localAddress, localPort, params));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return enableSocketProtocols(base.createSocket(host, port));
    }
}
