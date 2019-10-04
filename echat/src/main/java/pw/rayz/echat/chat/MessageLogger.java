package pw.rayz.echat.chat;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.*;

public class MessageLogger {
    private final Map<Long, Instant> userLatestMessages = new HashMap<>();
    private final Deque<MessageInstance> recentMessages = new ArrayDeque<>(32);
    private final Deque<Long> deletedMessageIds = new ArrayDeque<>(32);

    public static final class MessageInstance {
        public final long userId;
        public final String msg;
        public final long msgId;
        public final TextChannel channel;
        public final Instant instant;

        MessageInstance(long userId, String msg, long msgId, TextChannel channel, Instant instant) {
            this.userId = userId;
            this.msg = msg;
            this.msgId = msgId;
            this.channel = channel;
            this.instant = instant;
        }
    }

    MessageLogger() {
    }

    /**
     * Return the {@link Instant} of the last message this nonull {@link Member} sent.
     *
     * @param member {@link Member} to lookup.
     * @return {@link Instant}.
     */
    public Instant lastMessage(@Nonnull Member member) {
        return userLatestMessages.get(member.getIdLong());
    }

    /**
     * Log that a message with id {@code msgId} was deleted.
     *
     * @param msgId {@code long} msgId.
     */
    public void logDeletedMessage(long msgId) {
        if (!deletedMessageIds.offerFirst(msgId)) {
            deletedMessageIds.pop();
            deletedMessageIds.push(msgId);
        }
    }

    /**
     * Log that a message was sent by a {@link Member}.
     *
     * @param member  The member who sent the message
     * @param message The message sent.
     */
    public void logSentMessage(@Nonnull Member member, @Nonnull Message message) {
        MessageInstance instance = new MessageInstance(
                member.getIdLong(), message.getContentRaw(),
                message.getIdLong(), message.getTextChannel(), Instant.now()
        );

        if (!recentMessages.offerFirst(instance)) {
            recentMessages.pop();
            recentMessages.push(instance);
        }

        userLatestMessages.put(member.getIdLong(), instance.instant);
    }

    public Set<MessageInstance> getDeletedMessages(int amount) {
        Deque<MessageInstance> stack = new ArrayDeque<>(recentMessages);
        Deque<Long> deletedStack = new ArrayDeque<>(deletedMessageIds);
        Set<MessageInstance> messages = new HashSet<>();

        while (messages.size() < amount && messages.size() < 8) {
            MessageInstance instance = stack.pollFirst();

            if (instance != null) {
                if (deletedStack.contains(instance.msgId))
                    messages.add(instance);
            } else break;
        }

        return messages;
    }

    public MessageInstance getMessageInstance(long msgId) {
        return recentMessages.stream().filter(i -> i.msgId == msgId).findFirst().orElse(null);
    }

}
