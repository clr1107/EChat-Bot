package pw.rayz.echat.chat;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import pw.rayz.echat.utils.collections.CircularStack;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageLogger {
    private final Map<Long, Instant> userLatestMessages = new HashMap<>();
    private final CircularStack<MessageInstance> recentMessages = new CircularStack<>(32);
    private final CircularStack<Long> deletedMessageIds = new CircularStack<>(32);

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
        deletedMessageIds.push(msgId);
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

        recentMessages.push(instance);
        userLatestMessages.put(member.getIdLong(), instance.instant);
    }

    public List<MessageInstance> getDeletedMessages(int amount) {
        CircularStack<Long> deletedStack = new CircularStack<>(deletedMessageIds);
        List<MessageInstance> messages = new ArrayList<>();

        amount = amount > 8 ? 8 : Math.max(amount, 1);

        while (messages.size() < amount) {
            Long id = deletedStack.pop();

            if (id == null)
                break;

            MessageInstance instance = getMessageInstance(id);

            if (instance != null) {
                messages.add(instance);
            }
        }

        return messages;
    }

    public MessageInstance getMessageInstance(long msgId) {
        for (MessageInstance instance : recentMessages) {
            if (instance.msgId == msgId)
                return instance;
        }

        return null;
    }

}
