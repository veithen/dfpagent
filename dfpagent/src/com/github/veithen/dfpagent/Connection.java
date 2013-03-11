package com.github.veithen.dfpagent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.veithen.dfpagent.resources.Messages;
import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;

public class Connection implements Runnable {
    private static final TraceComponent TC = Tr.register(Connection.class, Constants.TRACE_GROUP, Messages.class.getName());
    
    private static final Map<Integer,MessageType> messageTypeByCode = new HashMap<Integer,MessageType>();
    private static final Map<Integer,Type> typeByCode = new HashMap<Integer,Type>();
    
    static {
        for (MessageType messageType : MessageType.values()) {
            messageTypeByCode.put(messageType.getCode(), messageType);
        }
        for (Type type : Type.values()) {
            typeByCode.put(type.getCode(), type);
        }
    }
    
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Get the identifier for the DFP manager or SLB at the other end of the connection. This
     * identifier should be used in log messages.
     * 
     * @return the peer identifier
     */
    public String getPeerIdentifier() {
        return socket.getRemoteSocketAddress().toString();
    }
    
    public void run() {
        KeepAliveManager keepAliveManager = new KeepAliveManager(this);
        new Thread(keepAliveManager, "DFP Keep-Alive Thread: " + getPeerIdentifier()).start();
        try {
            ml: while (true) {
                int version = in.readUnsignedShort();
                if (version != DFPConstants.VERSION) {
                    Tr.error(TC, Messages._0005E, new Object[] { DFPConstants.VERSION, version });
                    break;
                }
                int messageTypeCode = in.readUnsignedShort();
                int messageLength = in.readInt();
                int remaining = messageLength - 8;
                List<TLV> tlvs = new ArrayList<TLV>();
                while (remaining != 0) {
                    if (remaining < 4) {
                        Tr.error(TC, Messages._0006E);
                        break ml;
                    }
                    int typeCode = in.readUnsignedShort();
                    int length = in.readUnsignedShort();
                    if (remaining < length) {
                        Tr.error(TC, Messages._0006E);
                        break ml;
                    }
                    byte[] value = new byte[length-4];
                    in.readFully(value);
                    Type type = typeByCode.get(typeCode);
                    if (type == null) {
                        Tr.warning(TC, Messages._0010W, typeCode);
                    } else {
                        tlvs.add(new TLV(type, value));
                    }
                    remaining -= length;
                }
                MessageType messageType = messageTypeByCode.get(messageTypeByCode);
                if (messageType == null) {
                    Tr.warning(TC, Messages._0009W, messageTypeCode);
                } else {
                    processMessage(new Message(messageType, tlvs));
                }
            }
            socket.close();
        } catch (IOException ex) {
            // TODO
        } finally {
            keepAliveManager.stop();
        }
    }
    
    private void processMessage(Message message) throws IOException {
        try {
            switch (message.getType()) {
                case DFP_PARAMETERS:
                    TLV keepAliveTLV = message.getRequiredTLV(Type.KEEP_ALIVE);
                    if (keepAliveTLV.getDataLength() != 4) {
                        Tr.error(TC, Messages._0011E, message.getType().getName());
                        return;
                    }
                    int keepAlive = keepAliveTLV.getValue().readInt();
                    if (TC.isDebugEnabled()) {
                        Tr.debug(TC, "Got new keep-alive value from peer {0}: {1}", new Object[] { getPeerIdentifier(), keepAlive });
                    }
                    break;
                default:
                    Tr.warning(TC, Messages._0007W, message.getType().getName());
            }
        } catch (MissingTLVException ex) {
            Tr.error(TC, Messages._0008E, new Object[] { ex.getMessageType().getName(), ex.getType().getName() });
        }
    }
    
    /**
     * Send a message to the peer.
     * 
     * @param message the message to send
     */
    private void sendMessage(Message message) throws IOException {
        int length = 8;
        for (TLV tlv : message) {
            length += tlv.getDataLength() + 4;
        }
        out.writeShort(DFPConstants.VERSION);
        out.writeShort(message.getType().getCode());
        out.writeInt(length);
        for (TLV tlv : message) {
            out.writeShort(tlv.getType().getCode());
            out.writeShort(tlv.getDataLength() + 4);
            tlv.writeValue(out);
        }
        out.flush();
    }
    
    /**
     * Send a Preference Information message as described in section 6.1 of the DFP specification.
     */
    public void sendPreferenceInformation() {
        TLV load = new TLV(Type.LOAD);
        new Message(MessageType.PREFERENCE_INFORMATION, Collections.singletonList(load));
    }
    
    public void stop() {
        // TODO
    }
}
