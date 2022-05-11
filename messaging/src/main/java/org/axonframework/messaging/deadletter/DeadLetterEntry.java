/*
 * Copyright (c) 2010-2022. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.messaging.deadletter;

import org.axonframework.messaging.Message;

import java.time.Instant;

/**
 * Entry describing a dead-lettered {@link Message}.
 * <p>
 * The time of storing the {@link #message()} is kept through {@link #deadLettered()}. This letter can be regarded for
 * evaluation once the {@link #expiresAt()} time is reached. Upon successful evaluation the entry can be cleared through
 * {@link #acknowledge()}. The {@link #requeue()} method should be used to signal evaluation failed, reentering the
 * letter into its queue.
 *
 * @param <T> The type of {@link Message} represented by this entry.
 * @author Steven van Beelen
 * @since 4.6.0
 */
public interface DeadLetterEntry<T extends Message<?>> {

    /**
     * The {@link QueueIdentifier} this dead-letter belongs to.
     *
     * @return The {@link QueueIdentifier} this dead-letter belongs to
     */
    QueueIdentifier queueIdentifier();

    /**
     * The {@link Message} of type {@code T} contained in this entry.
     *
     * @return The {@link Message} of type {@code T} contained in this entry.
     */
    T message();

    /**
     * The cause for the {@link #message()} to be dead-lettered. May be {@code null} in case this letter is enqueued as
     * a consequence of earlier letters with the same {@link QueueIdentifier}.
     *
     * @return The cause for the {@link #message()} to be dead-lettered
     */
    Throwable cause();

    /**
     * The moment in time when the {@link #message()} was dead-lettered.
     *
     * @return The moment in time when the {@link #message()} was dead-lettered.
     */
    Instant deadLettered();

    /**
     * The moment in time when this letter will expire. Should be used to deduce whether the letter is ready to be
     * evaluated. Will equal the {@link #deadLettered()} value if this entry is enqueued as part of a sequence to ensure
     * it is available right after a previous letter within the same sequence.
     *
     * @return The moment in time when this letter will expire.
     */
    Instant expiresAt();

    /**
     * The number of retries this {@link DeadLetterEntry dead-letter} has gone through.
     *
     * @return The number of retries this {@link DeadLetterEntry dead-letter} has gone through.
     */
    int numberOfRetries();

    /**
     * Acknowledges this {@link DeadLetterEntry dead-letter} as successfully evaluated. This operation will remove the
     * entry from its queue.
     */
    void acknowledge();

    /**
     * Reenters this {@link DeadLetterEntry dead-letter} in the queue it originates from. This method should be used to
     * signal the evaluation failed.
     * <p>
     * The operation will adjust the {@link #expiresAt()} to the current time and increment the
     * {@link #numberOfRetries()}. This operation might remove the entry from the {@link DeadLetterQueue queue} it
     * originated from.
     */
    void requeue();
}
