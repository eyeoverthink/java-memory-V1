package fraymus.swarm;

import java.util.*;
import java.util.concurrent.*;

/**
 * MessagingProtocol - Inter-Node Communication
 * Enables real-time message passing between swarm nodes.
 */
public class MessagingProtocol {
    private final List<Node> nodes;
    private final BlockingQueue<Message> messageQueue;
    private final List<Message> messageHistory;
    
    public MessagingProtocol(List<Node> nodes) {
        this.nodes = nodes;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.messageHistory = Collections.synchronizedList(new ArrayList<>());
    }
    
    public void sendMessage(Node sender, Node recipient, String content) {
        Message msg = new Message(sender, recipient, content);
        messageQueue.offer(msg);
        messageHistory.add(msg);
        
        // Process immediately for recipient
        if (recipient != null && recipient.isActive()) {
            recipient.processThought("[FROM:" + sender.getNodeId() + "] " + content);
        }
    }
    
    public void broadcast(Node sender, String content) {
        for (Node node : nodes) {
            if (node != sender && node.isActive()) {
                sendMessage(sender, node, content);
            }
        }
    }
    
    public String receiveMessage() {
        Message msg = messageQueue.poll();
        return msg != null ? msg.toString() : null;
    }
    
    public List<Message> getMessageHistory() {
        return new ArrayList<>(messageHistory);
    }
    
    public int getPendingCount() {
        return messageQueue.size();
    }
    
    public static class Message {
        public final long timestamp;
        public final String senderId;
        public final String recipientId;
        public final String content;
        
        public Message(Node sender, Node recipient, String content) {
            this.timestamp = System.currentTimeMillis();
            this.senderId = sender != null ? sender.getNodeId() : "SYSTEM";
            this.recipientId = recipient != null ? recipient.getNodeId() : "BROADCAST";
            this.content = content;
        }
        
        @Override
        public String toString() {
            return String.format("[%s → %s] %s", senderId, recipientId, content);
        }
    }
}
